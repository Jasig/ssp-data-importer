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
package org.jasig.ssp.util.importer.job.twodottwo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.jasig.ssp.util.importer.job.report.ErrorEntry;
import org.jasig.ssp.util.importer.job.report.ReportEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/twodottwo-test-parse-rawitem-header-only/launch-context-test.xml")
public class HeaderOnlyTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();


    @SuppressWarnings("unchecked")
    @Test
    public void testJob() throws Exception {


        JobExecution jobExecution = jobLauncherTestUtils.launchJob();


        Map<String, ReportEntry> report = (Map<String, ReportEntry>)jobExecution.getExecutionContext().get("report");
        Assert.assertNotNull(report);
        Set<Entry<String, ReportEntry>> entrySet = report.entrySet();
        Assert.assertEquals(1, entrySet.size());
        for (Entry<String, ReportEntry> entry : entrySet) {
            Assert.assertEquals(new Integer(0), entry.getValue().getNumberParsed());
            Assert.assertEquals(new Integer(0), entry.getValue().getNumberSkippedOnParse());
            Assert.assertEquals(new Integer(0), entry.getValue().getNumberSkippedOnDatabaseWrite());
            Assert.assertEquals(new Integer(0), entry.getValue().getNumberInsertedUpdated());
        }
    }
}