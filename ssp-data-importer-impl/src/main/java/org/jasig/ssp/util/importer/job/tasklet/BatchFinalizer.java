package org.jasig.ssp.util.importer.job.tasklet;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.jasig.ssp.util.importer.job.util.ZipDirectory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;

public class BatchFinalizer implements JobExecutionListener {

    private Resource archiveDirectory;
    private Resource inputDirectory;
    private Resource processDirectory;
    private Resource upsertDirectory;
    private String batchTitle = "csv_import";
    private ArchiveType archiveFiles = ArchiveType.UNIQUE;
    private Boolean retainInputFiles = false;


    private FilenameFilter csvFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            String lowercaseName = name.toLowerCase();
            if (lowercaseName.endsWith(".csv")) {
                return true;
            } else {
                return false;
            }
        }
    };

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

    public void setBatchTitle(String batchTitle) {
        this.batchTitle = batchTitle;
    }

    //For testing purposes only
    public void setArchiveFiles(ArchiveType archiveFiles) {
        this.archiveFiles = archiveFiles;
    }

  //For testing purposes only
    public void setRetainInputFiles(Boolean retainInputFiles) {
        this.retainInputFiles = retainInputFiles;
    }


    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        try{
            if(!archiveFiles.equals(ArchiveType.NONE))
                archive() ;

            FileSystemUtils.deleteRecursively(processDirectory.getFile());
            FileSystemUtils.deleteRecursively(upsertDirectory.getFile());
            if(!retainInputFiles)
                deleteFiles(inputDirectory.getFile());
        }catch(Exception e){

        }
    }

    private void archive() throws IOException{
        if(archiveFiles.equals(ArchiveType.UNIQUE)){
            if(!retainInputFiles){
                removeDuplicates(processDirectory.getFile(),inputDirectory.getFile());
                removeDuplicates(inputDirectory.getFile(), upsertDirectory.getFile());
            }
            removeDuplicates(processDirectory.getFile(), upsertDirectory.getFile());
        }

        ZipDirectory zippy = new ZipDirectory(generateFileArchive());
        zippy.zipIt(Arrays.asList(inputDirectory.getFile(), processDirectory.getFile(), upsertDirectory.getFile()), true);
    }

    private void removeDuplicates(File srcDirectory, File destDirectory){
        if(!destDirectory.exists() || !srcDirectory.exists())
            return;

        File[] destFiles = destDirectory.listFiles(csvFilter);
        File[] srcFiles = srcDirectory.listFiles(csvFilter);
        for(File srcFile:srcFiles){
            for(File destFile:destFiles){
                if(srcFile.getName().equals(destFile.getName()) && srcFile.length() == destFile.length()){
                    srcFile.delete();
                    break;
                }
            }
        }
    }

    private File generateFileArchive() throws IOException{
        Date processDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE_MMM_dd_hh_mm_ss_a_yyyy_");
        return new File(archiveDirectory.getFile(), dateFormat.format(processDate) + "_importJOB_" + batchTitle + ".zip");
    }

    private void deleteFiles(File directory) {
         for(File file: directory.listFiles(csvFilter)){
             file.delete();
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
