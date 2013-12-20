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

import junit.framework.Assert;

import org.jasig.ssp.util.importer.job.twodottwo.StageFailTest;
import org.jasig.ssp.util.importer.job.twodottwo.TestBase;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/launch-context-test.xml")
public class PrototypeJobRunnerTest extends TestBase {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    private static final Logger logger = LoggerFactory.getLogger(PrototypeJobRunnerTest.class);

    @Test
    public void testJob() throws Exception {

        logger.info(System.getProperty("batch.tables.upsert.files"));

        BatchStatus jobExecution = jobLauncherTestUtils.launchJob().getStatus();

        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution);
    }

    @After
    public void cleanup() throws Exception{
        super.cleanup();
    }
}