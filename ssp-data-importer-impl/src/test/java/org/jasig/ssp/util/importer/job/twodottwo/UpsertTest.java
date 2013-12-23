package org.jasig.ssp.util.importer.job.twodottwo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang3.time.DateUtils;
import org.jasig.ssp.util.importer.job.PrototypeJobRunnerTest;
import org.jasig.ssp.util.importer.job.tasklet.BatchInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/launch-context-test.xml")
public class UpsertTest extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(PrototypeJobRunnerTest.class);


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtilUpsert = new JobLauncherTestUtils();


    public UpsertTest() {
    }

     @Test
     public void testUpsert() throws Exception {

            String sql = "insert into external_person (school_id,username,first_name,middle_name,last_name,birth_date,primary_email_address,address_line_1,address_line_2,city,state,zip_code,home_phone,work_phone,office_location,office_hours,department_name,actual_start_term," +
                    "actual_start_year,marital_status,ethnicity,gender,is_local,balance_owed,coach_school_id,cell_phone,photo_url) VALUES('mjackson15t','mjackson15t','Maria'," +
                    "'Mumford','Jackson','1983-08-20','6@unicon.net','5 house on the corner','','Mesa','AZ','85201','480-775-2345','480-775-7894','','','','FA12',2013,'single','x','x','t',0.00,'','','http://www.unicon.com/jstanley.com');\n" +
                    "insert into external_person (school_id,username,first_name,middle_name,last_name,birth_date,primary_email_address,address_line_1,address_line_2,city,state,zip_code,home_phone,work_phone,office_location,office_hours,department_name," +
                    "actual_start_term,actual_start_year,marital_status,ethnicity,gender,is_local,balance_owed,coach_school_id,cell_phone,photo_url) VALUES(" +
                    "'pjones12t','pjones12t','Patricia','Mumford','Jones','1983-08-20','3@unicon.net','2 house on the corner','','Mesa','AZ','85201','480-775-2345','480-775-7894','','','','FA12',2013,'single','x','x','t',0.00,'','','http://www.unicon.com/jstanley.com');\n" +

                    "insert into external_person (school_id,username,first_name,middle_name,last_name,birth_date,primary_email_address,address_line_1,address_line_2,city,state,zip_code,home_phone,work_phone,office_location,office_hours,department_name,actual_start_term," +
                    "actual_start_year,marital_status,ethnicity,gender,is_local,balance_owed,coach_school_id,cell_phone,photo_url) VALUES(" +
                    "'mjones13t','mjones13t','Maria','Mumford','Jones','1983-08-20','4@unicon.net','3 house on the corner','','Mesa','AZ','85201','480-775-2345','480-775-7894','','','','FA12',2013,'single','x','x','t',0.00,'','','http://www.unicon.com/jstanley.com');";

            runSQL(sql);
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("middle_name","Mumford");
            map.put("birth_date",DateUtils.parseDateStrictly("1983-08-20", "yyyy-MM-dd"));
            map.put("city","Mesa");
            map.put("actual_start_year",new Integer(2013));
            map.put("balance_owed",new BigDecimal("0.00"));
            Assert.assertTrue(validateMap("Select middle_name,birth_date,city,actual_start_year,balance_owed from external_person", map, 3));


            logger.info("Start upsert job test.");
            BatchInitializer initializer = (BatchInitializer)applicationContext.getBean("batchInitializer");
            ClassPathResource[] resources = new ClassPathResource[1];
            resources[0] = new ClassPathResource("/twodottwo-upsert-test/external_person.csv");
            initializer.setResources(resources);
            BatchStatus jobExecution = jobLauncherTestUtilUpsert.launchJob().getStatus();
            Assert.assertEquals(BatchStatus.COMPLETED, jobExecution);
            logger.info("End upsert job test.");
            map = new HashMap<String,Object>();

            map = new HashMap<String,Object>();
            map.put("middle_name","Balle");
            map.put("birth_date",DateUtils.parseDate("2000-08-20", "yyyy-MM-dd"));
            map.put("city","Tempe");
            map.put("actual_start_year",new Integer(2015));
            map.put("balance_owed",new BigDecimal("1000.00"));
            Assert.assertTrue(validateMap("Select middle_name,birth_date,city,actual_start_year,balance_owed from external_person", map, 24));


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
