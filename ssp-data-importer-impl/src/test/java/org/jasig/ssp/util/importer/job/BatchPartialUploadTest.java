package org.jasig.ssp.util.importer.job;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/batch-initialization/launch-context-test-partial-upload.xml")
public class BatchPartialUploadTest {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    final private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();


    final private String inputDirectoryPath = "/tmp/batch-initialization/input/";

    public BatchPartialUploadTest() {

    }


    @SuppressWarnings("unchecked")
    @Test
    public void testPartialUploadGuard() throws Exception {


        deleteDirectory(inputDirectoryPath);
        createFiles(inputDirectoryPath);


        Assert.assertTrue(directoryContainsFiles(inputDirectoryPath, 3, csvFilter));


        BatchStatus exitStatus = jobLauncherTestUtils.launchJob().getStatus();


        Assert.assertEquals(BatchStatus.FAILED, exitStatus);

        Assert.assertTrue(directoryExists(inputDirectoryPath));
        Assert.assertTrue(directoryContainsFiles(inputDirectoryPath, 3, csvFilter));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDirectoryNoFiles() throws Exception {


        deleteDirectory(inputDirectoryPath);
        createDirectory(inputDirectoryPath);
        Assert.assertTrue(directoryExists(inputDirectoryPath));

        BatchStatus exitStatus = jobLauncherTestUtils.launchJob().getStatus();

        Assert.assertEquals(BatchStatus.FAILED, exitStatus);

        Assert.assertTrue(directoryExists(inputDirectoryPath));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNoDirectory() throws Exception {


        deleteDirectory(inputDirectoryPath);

        Assert.assertTrue(!directoryExists(inputDirectoryPath));

        BatchStatus exitStatus = jobLauncherTestUtils.launchJob().getStatus();

        Assert.assertEquals(BatchStatus.FAILED, exitStatus);

        Assert.assertTrue(!directoryExists(inputDirectoryPath));

    }



    @After
    public void cleanup(){
        try {
            deleteDirectory(inputDirectoryPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void deleteDirectory(String directoryPath) throws IOException{
        File directory = new File(directoryPath);
        if(directory.exists())
            FileUtils.deleteDirectory(directory);
    }

    private void createDirectory(String directoryPath) throws IOException{
        File directory = new File(directoryPath);
        if(!directory.exists())
             FileUtils.forceMkdir(directory);
    }

    private Boolean directoryExists(String directoryPath){
        File file = new File(directoryPath);

        if(file.exists() && file.isDirectory())
            return true;
        return false;
    }

    private  void createFiles(String directoryPath) throws IOException{
         File directory = new File(directoryPath);
        FileUtils.forceMkdir(directory);
        File file = new File(directory, "test.csv");
        FileUtils.writeStringToFile(file, "header1,header2,header,3");
        file = new File(directory, "test1.csv");
        FileUtils.writeStringToFile(file, "header1,header2,header,3");
        file = new File(directory, "test3.csv");
        FileUtils.writeStringToFile(file, "header1,header2,header,3");
    }

    private Boolean directoryContainsFiles(String directoryPath, int count, FilenameFilter filter){
        File file = new File(directoryPath);

        if(!file.exists() || !file.isDirectory())
            return false;

        if(file.list(filter).length == count)
            return true;
        return false;
    }

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

    private FilenameFilter zipFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(".zip")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

    public String getInputDirectoryPath() {
        return inputDirectoryPath;
    }

    public JobLauncherTestUtils getJobLauncherTestUtils() {
        return jobLauncherTestUtils;
    }


}
