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
@ContextConfiguration("/batch-initialization/launch-context-test.xml")
public class BatchInitializerTest {

    @Autowired
    final private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    @Autowired
    private ApplicationContext applicationContext;

    private final String tempDir = System.getProperty("java.io.tmpdir");
    final private String processDirectoryPath = tempDir + "/batch-initialization/process/";
    final private String upsertDirectoryPath = tempDir + "/batch-initialization/upsert/";
    final private String inputDirectoryPath = tempDir + "/batch-initialization/input/";

    public BatchInitializerTest() {

    }


    @Test
    public void testCreateDirectories() throws Exception {

        deleteDirectory(processDirectoryPath);
        deleteDirectory(upsertDirectoryPath);
        createFiles(inputDirectoryPath);

        Assert.assertTrue(!directoryExists(processDirectoryPath));
        Assert.assertTrue(!directoryExists(upsertDirectoryPath));
        Assert.assertTrue(directoryExists(inputDirectoryPath));
        Assert.assertTrue(directoryContainsFiles(inputDirectoryPath, 2));


        BatchStatus status = jobLauncherTestUtils.launchJob().getStatus();

        Assert.assertTrue(directoryExists(processDirectoryPath));
        Assert.assertTrue(directoryExists(upsertDirectoryPath));
        Assert.assertTrue(directoryExists(inputDirectoryPath));
        Assert.assertTrue(directoryContainsFiles(processDirectoryPath, 2));
        Assert.assertTrue(directoryContainsFiles(upsertDirectoryPath, 0));
        Assert.assertTrue(directoryContainsFiles(inputDirectoryPath, 0));
        Assert.assertEquals(BatchStatus.COMPLETED, status);
    }

    @Test
    public void testRemoveStaleFiles() throws Exception {

        deleteDirectory(processDirectoryPath);
        deleteDirectory(upsertDirectoryPath);
        createFiles(processDirectoryPath);
        createFiles(upsertDirectoryPath);
        createFiles(inputDirectoryPath);

        Assert.assertTrue(directoryExists(processDirectoryPath));
        Assert.assertTrue(directoryExists(upsertDirectoryPath));
        Assert.assertTrue(directoryExists(inputDirectoryPath));
        Assert.assertTrue(directoryContainsFiles(inputDirectoryPath, 2));
        Assert.assertTrue(directoryContainsFiles(upsertDirectoryPath, 2));
        Assert.assertTrue(directoryContainsFiles(processDirectoryPath, 2));


        BatchStatus status = jobLauncherTestUtils.launchJob().getStatus();

        Assert.assertTrue(directoryExists(processDirectoryPath));
        Assert.assertTrue(directoryExists(upsertDirectoryPath));
        Assert.assertTrue(directoryExists(inputDirectoryPath));
        Assert.assertTrue(directoryContainsFiles(processDirectoryPath, 2));
        Assert.assertTrue(directoryContainsFiles(upsertDirectoryPath, 0));
        Assert.assertTrue(directoryContainsFiles(inputDirectoryPath, 0));

        Assert.assertEquals(BatchStatus.COMPLETED, status);

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
