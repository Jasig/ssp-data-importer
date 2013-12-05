package org.jasig.ssp.util.importer.job.twodottwo;

import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/twodottwo-test-stage-success-with-skip/launch-context-test.xml")
public class StageSuccessWithSkipTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
    

    @Test
    public void testStageSuccessWithSkip() throws Exception {


        //Test file should have 1 step which should write successfully but skip 1 line
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
        for (StepExecution stepExecution : stepExecutions) {
            Assert.assertEquals(stepExecution.getWriteCount(),1);
            Assert.assertEquals(stepExecution.getSkipCount(),1);
        }
        BatchStatus exitStatus = jobExecution.getStatus();

        Assert.assertEquals(BatchStatus.COMPLETED, exitStatus);

        
    }
}