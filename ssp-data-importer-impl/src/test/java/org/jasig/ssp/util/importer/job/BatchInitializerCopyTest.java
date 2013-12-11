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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/batch-initialization/launch-context-test-copy.xml")
public class BatchInitializerCopyTest {

    @Autowired
    final private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();


    final private String processDirectoryPath = "/tmp/batch-initialization/process/";
    final private String upsertDirectoryPath = "/tmp/batch-initialization/upsert/";
    final private String inputDirectoryPath = "/tmp/batch-initialization/input/";


    public BatchInitializerCopyTest() {
        super();
    }



    @SuppressWarnings("unchecked")
    @Test
    public void testCopyToProcessDirectory() throws Exception {

        System.setProperty("ssp.importer.configdir", "batch-initialization");

        deleteDirectory(this.getProcessDirectoryPath());
        deleteDirectory(getUpsertDirectoryPath());
        createFiles(getInputDirectoryPath());

        Assert.assertTrue(!directoryExists(getProcessDirectoryPath()));
        Assert.assertTrue(!directoryExists(getUpsertDirectoryPath()));
        Assert.assertTrue(directoryExists(getInputDirectoryPath()));
        Assert.assertTrue(directoryContainsFiles(getInputDirectoryPath(), 2));

        BatchStatus exitStatus = jobLauncherTestUtils.launchJob().getStatus();

        Assert.assertTrue(directoryExists(getProcessDirectoryPath()));
        Assert.assertTrue(directoryExists(getUpsertDirectoryPath()));
        Assert.assertTrue(directoryExists(getInputDirectoryPath()));
        Assert.assertTrue(directoryContainsFiles(getProcessDirectoryPath(), 2));
        Assert.assertTrue(directoryContainsFiles(getUpsertDirectoryPath(), 0));
        Assert.assertTrue(directoryContainsFiles(getInputDirectoryPath(), 2));
        Assert.assertEquals(BatchStatus.COMPLETED, exitStatus);
    }

    @After
    public void cleanup(){
        try {
            deleteDirectory(processDirectoryPath);
            deleteDirectory(upsertDirectoryPath);
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
    }

    private Boolean directoryContainsFiles(String directoryPath, int count){
        File file = new File(directoryPath);

        if(!file.exists() || !file.isDirectory())
            return false;

        if(file.list(csvFilter).length == count)
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

    public String getProcessDirectoryPath() {
        return processDirectoryPath;
    }


    public String getUpsertDirectoryPath() {
        return upsertDirectoryPath;
    }


    public String getInputDirectoryPath() {
        return inputDirectoryPath;
    }

    public JobLauncherTestUtils getJobLauncherTestUtils() {
        return jobLauncherTestUtils;
    }


}
