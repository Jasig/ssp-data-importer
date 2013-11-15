package org.jasig.ssp.util.importer.job;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/launch-context-test.xml")
public class PrototypeJobRunnerTest {

	@Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    @Test
    public void testJob() throws Exception {

        BatchStatus jobExecution = jobLauncherTestUtils.launchJob().getStatus();


        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution);
    }
}