package org.jasig.ssp.util.importer.job.twodottwo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang3.time.DateUtils;
import org.jasig.ssp.util.importer.job.PrototypeJobRunnerTest;
import org.junit.After;
import org.junit.Before;
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
public class InsertTest extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(PrototypeJobRunnerTest.class);

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    public InsertTest() {
    }

    @Test
    public void testInsert() throws Exception {

           logger.info(System.getProperty("batch.tables.upsert.files"));

           BatchStatus jobExecution = jobLauncherTestUtils.launchJob().getStatus();
           Map<String,Object> map = new HashMap<String,Object>();
           map.put("middle_name","Mumford");
           map.put("birth_date",DateUtils.parseDateStrictly("1983-08-20", "yyyy-MM-dd"));
           map.put("city","Mesa");
           map.put("actual_start_year",new Integer(2013));
           map.put("balance_owed",new BigDecimal("0.00"));
           Assert.assertTrue(validateMap("Select middle_name,birth_date,city,actual_start_year,balance_owed from external_person", map, 24));
           Assert.assertEquals(BatchStatus.COMPLETED, jobExecution);
    }

    @Before
    public void setup() throws Exception
    {
        super.cleanup();
    }

    @After
    public void cleanup() throws Exception
    {
        super.cleanup();
    }

}
