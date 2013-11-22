package org.jasig.ssp.util.importer.job.tasklet;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

public class BatchInitializer implements Tasklet {

    private Resource[] resources;
    private Resource processDirectoryResource;
    private Boolean dulicateResources = false;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        File processDirectory = createDirectory();
        if(dulicateResources){
            copyFiles(processDirectory);
        }else{
            copyDeleteFiles(processDirectory);
        }
        return RepeatStatus.FINISHED;
    }

    public void setResources(Resource[] resources){
        Assert.notNull(resources, "The resources must not be null");
        this.resources = resources;
    }

    public void setProcessDirectoryResource(Resource processDirectoryResource){
        this.processDirectoryResource = processDirectoryResource;
    }

    public Boolean getDulicateResources() {
        return dulicateResources;
    }

    public void setDulicateResources(Boolean dulicateResources) {
        this.dulicateResources = dulicateResources;
    }

    private File createDirectory() throws Exception{
        File dir = processDirectoryResource.getFile();
        if(!dir.exists())
            if(!dir.mkdirs())
                throw new Exception("process directory not createsd");
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
