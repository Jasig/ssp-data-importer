/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.util.importer.job.tasklet;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class PartialUploadGuard implements Tasklet,StepExecutionListener {

    private static Logger logger = LoggerFactory.getLogger(PartialUploadGuard.class);
    Resource[] resources;
    Resource directory;
    JobOperator jobOperator;
    StepExecution stepExecution;
    Integer lagTimeBeforeStartInMinutes = 0;// programmatic default set to 0 for testing, time needs to be set by expected load times
    final String FILE_SOAK_TIME = "File does not meet minimum lag time since modification. File: ";

    public PartialUploadGuard(){
        super();
    }
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Date lastAllowedModificationTime = getLastAllowedModifiedDate();

        if(resources == null || resources.length == 0){
            logger.error("Job not started. No resources found");
            stopJob();
            return RepeatStatus.FINISHED;
        }
        for(Resource resource:resources){
            Date modified = new Date(resource.lastModified());
            if(modified.after(lastAllowedModificationTime)){
                logger.info(FILE_SOAK_TIME + resource.getFilename());
                stopJob();
                return RepeatStatus.FINISHED;
            }
        }
        return RepeatStatus.FINISHED;
    }

    public void setResources(Resource[] resources) throws PartialUploadGuardException{
        Assert.notNull(resources, "Input directory has not been created.");
        this.resources = resources;
    }

    public void setLagTimeBeforeStartInMinutes(Integer lagTimeBeforeStartInMinutes){
        this.lagTimeBeforeStartInMinutes = lagTimeBeforeStartInMinutes;
    }

    private Date getLastAllowedModifiedDate(){
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.MINUTE, -lagTimeBeforeStartInMinutes);
        return calendar.getTime();
    }

    public void setJobOperator(JobOperator jobOperator){
        this.jobOperator = jobOperator;
    }

    public void setDirectory(Resource directory) throws PartialUploadGuardException, IOException{
        this.directory = directory;
        if(!directory.getFile().exists())
            throw new PartialUploadGuardException("Input directory does not exist. Location:" + directory.getFile().getPath());
    }

    private Long getJobExecutionId(){
        return stepExecution.getJobExecutionId();
    }

    private void stopJob() throws NoSuchJobExecutionException, JobExecutionNotRunningException{
        if(jobOperator != null)
            jobOperator.stop(getJobExecutionId());
    }
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;

    }
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
