package org.jasig.ssp.util.importer.job.tasklet;

import java.io.File;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;

public class BatchFinalizer implements JobExecutionListener {

    private Resource archiveDirectory;
    private Resource inputDirectory;
    private Resource processDirectory;
    private Resource upsertDirectory;

    public void setArchiveDirectory(Resource archiveDirectory) throws Exception {
        this.archiveDirectory = archiveDirectory;
        createDirectory(this.archiveDirectory);
    }

    public void setInputDirectory(Resource inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public void setProcessDirectory(Resource processDirectory) {
        this.processDirectory = processDirectory;
    }

    public void setUpsertDirectory(Resource upsertDirectory) {
        this.upsertDirectory = upsertDirectory;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        try{
            FileSystemUtils.copyRecursively(inputDirectory.getFile(), archiveDirectory.getFile());
            FileSystemUtils.copyRecursively(processDirectory.getFile(), archiveDirectory.getFile());
            FileSystemUtils.copyRecursively(upsertDirectory.getFile(), archiveDirectory.getFile());

            FileSystemUtils.deleteRecursively(processDirectory.getFile());
            FileSystemUtils.deleteRecursively(upsertDirectory.getFile());
        }catch(Exception e){

        }
    }

    private File createDirectory(Resource directory) throws Exception{
        File dir = directory.getFile();
        if(!dir.exists())
            if(!dir.mkdirs())
                throw new Exception("process directory not createsd");
        return dir;
    }

}
