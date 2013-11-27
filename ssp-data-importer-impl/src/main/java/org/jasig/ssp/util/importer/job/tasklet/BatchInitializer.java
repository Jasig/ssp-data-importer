package org.jasig.ssp.util.importer.job.tasklet;

import java.io.File;
import java.io.IOException;

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
    private Boolean dulicateResources = false;

    Logger logger = LoggerFactory.getLogger(BatchInitializer.class);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        createDirectory(upsertDirectory);
        File directory = createDirectory(processDirectory);
        if(dulicateResources){
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

    public Boolean getDulicateResources() {
        return dulicateResources;
    }

    public void setDulicateResources(Boolean dulicateResources) {
        this.dulicateResources = dulicateResources;
    }

    private File createDirectory(Resource directory) throws Exception{
         File dir = directory.getFile();
         if(!dir.exists())
             if(!dir.mkdirs()){
                 logger.error("Process directory was not created at " + dir.getPath());
                 throw new Exception("Process directory was not created at " + dir.getPath());
             }
         return dir;
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
