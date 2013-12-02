package org.jasig.ssp.util.importer.job.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.CachingTableColumnMetadataRepository;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;

public class StagingTableTruncator implements StepExecutionListener {

    private DataSource dataSource;

    private MetadataConfigurations metadataRepository;
    
    private static List<String> stagingTables = new ArrayList<String>();
    
    static {
        stagingTables.add("stg_external_course");
        stagingTables.add("stg_external_course_program");
        stagingTables.add("stg_external_course_requisite");
        stagingTables.add("stg_external_course_tag");
        stagingTables.add("stg_external_course_term");
        stagingTables.add("stg_external_department");
        stagingTables.add("stg_external_faculty_course");
        stagingTables.add("stg_external_faculty_course_roster");
        stagingTables.add("stg_external_person");
        stagingTables.add("stg_external_person_note");
        stagingTables.add("stg_external_person_planning_status");
        stagingTables.add("stg_external_program");
        stagingTables.add("stg_external_student_academic_program");
        stagingTables.add("stg_external_division");
        stagingTables.add("stg_external_registration_status_by_term");
        stagingTables.add("stg_external_student_financial_aid");
        stagingTables.add("stg_external_student_test");
        stagingTables.add("stg_external_student_transcript");
        stagingTables.add("stg_external_student_transcript_course");
        stagingTables.add("stg_external_student_transcript_term");
        stagingTables.add("stg_external_term");
        
        }

    @Override
    public ExitStatus afterStep(StepExecution arg0) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            dataSource.getConnection().setAutoCommit(true);
            for (String table : stagingTables) {
                String sql = "truncate table "+table+";";
                jdbcTemplate.execute(sql);
                System.out.println(sql);
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        System.out.println("DONE TRUNCATE");
        return ExitStatus.COMPLETED;
    }

    @Override
    public void beforeStep(StepExecution arg0) {


    }

    public MetadataConfigurations getMetadataRepository() {
        return metadataRepository;
    }

    public void setMetadataRepository(MetadataConfigurations metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }




}
