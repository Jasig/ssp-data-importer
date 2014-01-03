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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.jasig.ssp.util.importer.job.report.ErrorEntry;
import org.jasig.ssp.util.importer.job.report.ReportEntry;
import org.jasig.ssp.util.importer.job.tasklet.PartialUploadGuardException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/batch-initialization-header-fail/launch-context-test.xml")
public class BatchInitializerHeaderFailTest {

    @Autowired
    final private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    @Autowired
    private ApplicationContext applicationContext;

    final private String processDirectoryPath = "/tmp/batch-initialization/process/";
    final private String upsertDirectoryPath = "/tmp/batch-initialization/upsert/";

    public BatchInitializerHeaderFailTest() {

    }


    @Test
    public void testHeaderFailTest() throws Exception {

        deleteDirectory(processDirectoryPath);
        deleteDirectory(upsertDirectoryPath);

        Assert.assertTrue(!directoryExists(processDirectoryPath));
        Assert.assertTrue(!directoryExists(upsertDirectoryPath));


        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        Assert.assertEquals(BatchStatus.FAILED, jobExecution.getStatus());
        
        @SuppressWarnings("unchecked")
		Map<String, ReportEntry> report = (Map<String, ReportEntry>)jobExecution.getExecutionContext().get("report");
        Assert.assertNull(report);

        @SuppressWarnings("unchecked")
		List<ErrorEntry> errors =(List<ErrorEntry>) jobExecution.getExecutionContext().get("errors");
        Assert.assertNull(errors);
        
        List<Throwable> failureExceptions = jobExecution.getAllFailureExceptions();
        Assert.assertEquals(new Integer(1), new Integer(failureExceptions.size()));
        
        Assert.assertEquals(PartialUploadGuardException.class, failureExceptions.get(0).getClass());
        
    }




    @After
    public void cleanup(){
        try {
            deleteDirectory(processDirectoryPath);
            deleteDirectory(upsertDirectoryPath);
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


    public JobLauncherTestUtils getJobLauncherTestUtils() {
        return jobLauncherTestUtils;
    }



}
