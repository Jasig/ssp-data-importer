package org.jasig.ssp.util.importer.job.twodottwo;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.jasig.ssp.util.importer.job.report.ReportEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/twodottwo-test-report-accuracy/launch-context-test.xml")
public class ReportAccuracyTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
    

    @SuppressWarnings("unchecked")
    @Test
    public void testJob() throws Exception {


        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        

        Map<String, ReportEntry> report = (Map<String, ReportEntry>)jobExecution.getExecutionContext().get("report");
        Assert.assertNotNull(report);
        Set<Entry<String, ReportEntry>> entrySet = report.entrySet();
        Assert.assertEquals(3, entrySet.size());
        for (Entry<String, ReportEntry> entry : entrySet) {
            Assert.assertEquals(new Integer(2), entry.getValue().getNumberInsertedUpdated());
        }
        
    }
}