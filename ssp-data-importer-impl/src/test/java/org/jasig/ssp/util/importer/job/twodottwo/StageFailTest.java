package org.jasig.ssp.util.importer.job.twodottwo;

import java.io.File;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.jasig.ssp.util.importer.job.listener.StagingAndUpsertSkipListener;
import org.junit.BeforeClass;
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
@ContextConfiguration("/twodottwo-test-input-stage-fail/launch-context-test.xml")
public class StageFailTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
    
    private static final Logger logger = LoggerFactory.getLogger(StageFailTest.class);
    

    @Test
    public void testStageFail() throws Exception {


        //Test file has duplicate values and should fail on stage, where skip limit is set to 1
        BatchStatus exitStatus = jobLauncherTestUtils.launchJob().getStatus();


        Assert.assertEquals(BatchStatus.FAILED, exitStatus);
        
    }
}