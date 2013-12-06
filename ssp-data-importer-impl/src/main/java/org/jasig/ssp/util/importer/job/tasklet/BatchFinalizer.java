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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jasig.ssp.util.importer.job.util.ZipDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BatchFinalizer implements JobExecutionListener {

    private Resource archiveDirectory;
    private Resource inputDirectory;
    private Resource processDirectory;
    private Resource upsertDirectory;
    private String batchTitle = "csv_import";
    private ArchiveType archiveFiles = ArchiveType.UNIQUE;
    private Boolean retainInputFiles = false;
    Logger logger = LoggerFactory.getLogger(BatchFinalizer.class);


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
         logger.info("Files deleted and archived");
         Long diff = TimeUnit.MILLISECONDS.toMinutes(jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime());

         logger.info("Job Duration in minutes: " + diff.toString());

        try{
            if(!archiveFiles.equals(ArchiveType.NONE)) {
                try {
                    archive();
                } catch ( Exception e ) {
                    logger.error("Error Archiving. Proceeding with file cleanup anyway.", e);
                }
            }
            // Always want to at least attempt clean up. Else behavior of next upload probably isn't what you expect.
            cleanDirectoryQuietly(processDirectory.getFile());
            cleanDirectoryQuietly(upsertDirectory.getFile());
            if(!retainInputFiles) {
                cleanCsvFilesQuietly(inputDirectory.getFile());
            }
        }catch(Exception e){
            logger.error("Error Delete Process, Upsert And Input Directory", e);
        }
    }

    private void archive() throws IOException{
        try{
            if(archiveFiles.equals(ArchiveType.UNIQUE)){
                logger.info("Deleting duplicate files prior to archiving");
                if(!retainInputFiles){
                    removeDuplicates(processDirectory.getFile(),inputDirectory.getFile());
                    removeDuplicates(inputDirectory.getFile(), upsertDirectory.getFile());
                }
                removeDuplicates(processDirectory.getFile(), upsertDirectory.getFile());
            }

            final List<File> filesToZip = Arrays.asList(inputDirectory.getFile(), processDirectory.getFile(),
                    upsertDirectory.getFile());
            // zipIt() will freak out if filesToZip is empty or consists entirely of empty directories.
            if ( hasAnyNonDirectoryFiles(filesToZip) ) {
                logger.info("Archiving [{}]", filesToZip);
                ZipDirectory zippy = new ZipDirectory(generateFileArchive());
                zippy.zipIt(filesToZip, true);
            } else {
                logger.info("No files to archive after removing duplicates from [{}], [{}], and [{}]",
                        new Object[] { inputDirectory, processDirectory, upsertDirectory });
            }
        }catch(IOException e){
            throw e;
        }
    }

    private boolean hasAnyNonDirectoryFiles(List<File> files) {
        if ( files == null || files.isEmpty() ) {
            return false;
        }
        for ( File file: files ) {
            if ( !(file.exists()) ) {
                continue;
            }
            if ( file.isFile() ) {
                return true;
            }
            if ( file.isDirectory() ) {
                final Collection<File> regularFiles = FileUtils.listFiles(file, FileFileFilter.FILE,
                        TrueFileFilter.INSTANCE);
                if ( regularFiles != null && !(regularFiles.isEmpty()) ) {
                    return true;
                }
            }
        }
        return false;
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

    private void cleanCsvFilesQuietly(File directory) {
        final File[] files;
        try {
            files = directory.listFiles(csvFilter);
        } catch ( Exception e ) {
            logger.error("Unable to list CSV files in [{}]", directory, e);
            return;
        }

        for(File file: files){
             if (!(file.delete())) {
                logger.error("Failed to delete file [{}]", file);
             }
         }
    }

    private void cleanDirectoryQuietly(File directory) {
        try {
            logger.info("Emptying directory [{}]", directory);
            FileUtils.cleanDirectory(directory);
        } catch ( Exception e ) {
            logger.error("Failed to empty directory [{}]", directory, e);
        }
    }

    private File createDirectory(Resource directory) throws Exception{
        File dir = directory.getFile();
        if(!dir.exists())
            if(!dir.mkdirs())
                throw new Exception("Archive directory not created");
        return dir;
    }

}
