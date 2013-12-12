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
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/batch-initialization/launch-context-test-finalizer-no-archive.xml")
public class BatchFinalizerNoArchiveTest {

    @Autowired
    final private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();


    final private String processDirectoryPath = "/tmp/batch-initialization/process/";
    final private String upsertDirectoryPath = "/tmp/batch-initialization/upsert/";
    final private String inputDirectoryPath = "/tmp/batch-initialization/input/";
    final private String archiveDirectoryPath = "/tmp/batch-initialization/archive/";

    public BatchFinalizerNoArchiveTest() {

    }


    @SuppressWarnings("unchecked")
    @Test
    public void testFinalizeNoArchive() throws Exception {

        deleteDirectory(processDirectoryPath);
        deleteDirectory(upsertDirectoryPath);
        deleteDirectory(inputDirectoryPath);
        deleteDirectory(archiveDirectoryPath);
        createFiles(inputDirectoryPath);
        createFiles(upsertDirectoryPath);
        createFiles(processDirectoryPath);


        Assert.assertTrue(directoryContainsFiles(inputDirectoryPath, 3, csvFilter));
        Assert.assertTrue(directoryContainsFiles(upsertDirectoryPath, 3, csvFilter));
        Assert.assertTrue(directoryContainsFiles(processDirectoryPath, 3, csvFilter));

        BatchStatus exitStatus = jobLauncherTestUtils.launchJob().getStatus();


        Assert.assertTrue(directoryExists(processDirectoryPath));
        Assert.assertTrue(directoryExists(upsertDirectoryPath));
        Assert.assertTrue(directoryExists(inputDirectoryPath));
        Assert.assertTrue(!directoryExists(archiveDirectoryPath));
        Assert.assertTrue(directoryContainsFiles(processDirectoryPath, 0, csvFilter));
        Assert.assertTrue(directoryContainsFiles(upsertDirectoryPath, 0, csvFilter));
        Assert.assertTrue(directoryContainsFiles(inputDirectoryPath, 0, csvFilter));
        Assert.assertEquals(BatchStatus.COMPLETED, exitStatus);
    }


    @After
    public void cleanup(){
        try {
            deleteDirectory(processDirectoryPath);
            deleteDirectory(upsertDirectoryPath);
            deleteDirectory(inputDirectoryPath);
            deleteDirectory(archiveDirectoryPath);
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
