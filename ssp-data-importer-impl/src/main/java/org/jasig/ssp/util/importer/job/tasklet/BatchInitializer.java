package org.jasig.ssp.util.importer.job.tasklet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

public class BatchInitializer implements Tasklet {

    private Resource[] resources;
    private Resource processDirectory;
    private Resource upsertDirectory;
    private Boolean dulicateResources = false;

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
        if(dir.exists()){
            FileSystemUtils.deleteRecursively(dir);
        }
        Files.createDirectory(dir.toPath());
        return dir;
    }

    private void copyFiles(File processDirectory) throws IOException{
        for(Resource resource:resources){
            File source = resource.getFile();
            File dest =  new File(processDirectory, source.getName());
            int count = FileCopyUtils.copy(source, dest);
            if(count <= 0 ){
                throw new IOException("");
            }
        }
    }

    private void copyDeleteFiles(File processDirectory) throws IOException{
        for(Resource resource:resources){
            File file = resource.getFile();
            file.renameTo(new File(processDirectory, file.getName()));
        }
    }
}
