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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

public class BatchInitializer implements Tasklet {

    private Resource[] resources;
    private Resource processDirectory;
    private Resource upsertDirectory;
    private Boolean duplicateResources = false;

    private Logger logger = LoggerFactory.getLogger(BatchInitializer.class);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        createDirectory(upsertDirectory);
        File directory = createDirectory(processDirectory);
        if(duplicateResources){
            copyFiles(directory);
        }else{
            copyDeleteFiles(directory);
        }
        return RepeatStatus.FINISHED;
    }

    public void setResources(Resource[] resources){
        if(resources == null || resources.length == 0)
            logger.error("Batch not initialized. No resources found");

        Assert.notNull(resources, "The resources must not be null");

        this.resources = resources;
    }

    public void setProcessDirectory(Resource processDirectory){
        this.processDirectory = processDirectory;
    }

    public void setUpsertDirectory(Resource upsertDirectory){
        this.upsertDirectory = upsertDirectory;
    }

    public Boolean getDuplicateResources() {
        return duplicateResources;
    }

    public void setDuplicateResources(Boolean duplicateResources) {
        this.duplicateResources = duplicateResources;
    }

    private File createDirectory(Resource directory) throws Exception{
         File dir = directory.getFile();
         if(!dir.exists()){
             if(!dir.mkdirs()){
                 logger.error("Process directory was not created at " + dir.getPath());
                 throw new Exception("Process directory was not created at " + dir.getPath());
             }
         }else
             cleanDirectoryQuietly(dir);
         return dir;
    }

    private void cleanDirectoryQuietly(File directory) {
        try {
            logger.info("Emptying directory [{}]", directory);
            FileUtils.cleanDirectory(directory);
        } catch ( Exception e ) {
            logger.error("Failed to empty directory [{}]", directory, e);
        }
    }

    private void copyFiles(File processDirectory) throws IOException{
        for(Resource resource:resources){

            File source = resource.getFile();
            File dest =  new File(processDirectory, source.getName());
            int count = FileCopyUtils.copy(source, dest);
            if(count <= 0 && source.length() > 0){
                throw new IOException("File: " +
                        source.getName() +
                        "of size " +
                        Long.valueOf(source.length()).toString() +
                        "could not be copied to " + dest.getPath());
            }
            logger.info("Copy file from " + source.getPath() + " to " + dest.getPath());
        }
    }

    private void copyDeleteFiles(File processDirectory) throws IOException{
        for(Resource resource:resources){
            File file = resource.getFile();
            logger.info("Move file from " + file.getPath() + " to " + processDirectory.getPath());
            file.renameTo(new File(processDirectory, file.getName()));

        }
    }
}
