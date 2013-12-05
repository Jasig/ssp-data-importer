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


import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class PartialUploadGuard implements Tasklet {

    Resource[] resources;
    Integer lagTimeBeforeStartInMinutes = 0;// programmatic default set to 0 for testing, time needs to be set by expected load times
    Logger logger = LoggerFactory.getLogger(PartialUploadGuard.class);
    final String FILE_SOAK_TIME = "File does not meet minimum time since modification batch processing will not start: ";
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Date lastAllowedModificationTime = getLastAllowedModifiedDate();
        for(Resource resource:resources){
            Date modified = new Date(resource.lastModified());
            if(modified.after(lastAllowedModificationTime)){
                logger.info(FILE_SOAK_TIME + resource.getFilename());
                throw new Exception(FILE_SOAK_TIME + resource.getFilename());
            }
        }
        return RepeatStatus.FINISHED;
    }

    public void setResources(Resource[] resources){
        if(resources == null || resources.length == 0)
            logger.error("Batch not initialized. No resources found");
        Assert.notNull(resources, "The resources must not be null");
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
}
