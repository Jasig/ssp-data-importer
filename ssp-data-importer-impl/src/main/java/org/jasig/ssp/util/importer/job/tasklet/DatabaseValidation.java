package org.jasig.ssp.util.importer.job.tasklet;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.report.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class DatabaseValidation implements Tasklet, StepExecutionListener  {
	
	
	private StepExecution stepExecution;
	
    @Autowired
    private DataSource dataSource;
    
    private Boolean validateDatabase = true;
    
    String EOL = System.getProperty("line.separator");
    
    Logger logger = LoggerFactory.getLogger(DatabaseValidation.class);

	public DatabaseValidation() {
		
	}

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		if(!validateDatabase)
			return RepeatStatus.FINISHED;
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		StringBuilder validations = new StringBuilder();
		for(Pair<String,String> sql:buildSQLValidation()){
			String invalidRow = new String(sql.getLeft() + EOL);
			Boolean invalidRowFound = false;
			try{
				SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql.getRight());
				while(sqlRowSet.next()){
					invalidRowFound = true;
					for(String columnName:sqlRowSet.getMetaData().getColumnNames())
					{
						Object obj = sqlRowSet.getObject(columnName);
						invalidRow = invalidRow + columnName + ":" + obj.toString() + ",";
					}
					invalidRow = invalidRow + EOL;
				}
			}catch(Exception e){
				validations.append("Error thrown on: " + sql.getLeft());
				logger.error("Error thrown on: " + sql.getLeft(), e);
			}finally{
			}
			if(invalidRowFound)
				validations.append(invalidRow + EOL);
		}
		this.stepExecution.getJobExecution().getExecutionContext().put("databaseValidations", validations.toString());
		
		return RepeatStatus.FINISHED;
	}
	
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    
    private  List<Pair<String,String>> buildSQLValidation(){
    	List<Pair<String,String>> statements = new ArrayList<Pair<String,String>>();
    	
    	/*** external_person  coach_school_id ****/
    	statements.add(new ImmutablePair<String,String>("Coach School Id does not Exist: ",
    			"select distinct coach_school_id from external_person where coach_school_id IS NOT NULL AND coach_school_id NOT IN (select distinct school_id from external_person)"));
    	statements.add(new ImmutablePair<String,String>("Marital Status does not Exist: ",
    			"select school_id,marital_status from external_person where marital_status IS NOT NULL AND marital_status NOT IN (select name from marital_status)"));
    	statements.add(new ImmutablePair<String,String>("Ethnicity Value does not Exist: ","select school_id,ethnicity from external_person where ethnicity IS NOT NULL AND ethnicity NOT IN (select name from ethnicity)"));
    	statements.add(new ImmutablePair<String,String>("Gender Value does not Exist: ",
    			"select school_id,gender from external_person where gender IS NOT NULL AND gender NOT IN ('M','F','Male','Female')"));
    	statements.add(new ImmutablePair<String,String>("Is Local Value does not Exist: ",
    			"select school_id,is_local from external_person where is_local IS NOT NULL AND is_local NOT IN ('N','Y','n','y')"));

    	statements.add(new ImmutablePair<String,String>("Student Type Code Value does not Exist: ",
    			"select school_id,student_type_code from external_person where student_type_code IS NOT NULL AND student_type_code NOT IN (select code from student_type)"));

    	statements.add(new ImmutablePair<String,String>("Non local address Value does not Exist: ",
    			"select school_id,non_local_address from external_person where non_local_address IS NOT NULL AND non_local_address NOT IN ('Y','N','y', 'n')"));
    	
    	statements.add(new ImmutablePair<String,String>("Race Code Value does not Exist: ",
    			"select school_id,race_code from external_person where race_code IS NOT NULL AND race_code NOT IN (select code from race)"));
    	
    	/*** external_student_transcript_term  ****/
    	statements.add(new ImmutablePair<String,String>("external_student_transcript_term has term_code that does not exist: ",
    			"select distinct term_code from external_student_transcript_term where term_code IS NOT NULL AND term_code NOT IN (select distinct code from external_term)"));
    	statements.add(new ImmutablePair<String,String>("external_student_transcript_term has school_id that does not exist: ",
    			"select distinct school_id from external_student_transcript_term where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));
    	
    	/*** external_course_program  ****/
    	statements.add(new ImmutablePair<String,String>("external_course_program has course_code that does not exist: ",
    			"select distinct course_code from external_course_program  where course_code IS NOT NULL AND course_code NOT IN (select distinct code from external_course)"));
    	
    	/*** external_course_term  ****/
    	statements.add(new ImmutablePair<String,String>("external_course_term has course_code that does not exist: ",
    			"select distinct course_code from external_course_term  where course_code IS NOT NULL AND course_code NOT IN (select distinct code from external_course)"));
    	statements.add(new ImmutablePair<String,String>("external_course_term has term_code that does not exist: ",
    			"select distinct term_code from external_course_term where term_code IS NOT NULL AND term_code NOT IN (select distinct code from external_term)"));

    	/*** external_faculty_course  ****/
    	statements.add(new ImmutablePair<String,String>("external_faculty_course has faculty_school_id that does not exist: ",
    			"select distinct faculty_school_id from external_faculty_course where faculty_school_id IS NOT NULL AND faculty_school_id NOT IN (select distinct school_id from external_person)"));
    	statements.add(new ImmutablePair<String,String>("external_faculty_course has term_code that does not exist: ",
    			"select distinct term_code from external_faculty_course where term_code IS NOT NULL AND term_code NOT IN (select distinct code from external_term)"));
    	statements.add(new ImmutablePair<String,String>("external_faculty_course has formatted_course that does not exist: ",
    			"select distinct formatted_course from external_faculty_course where formatted_course IS NOT NULL AND formatted_course NOT IN (select distinct formatted_course from external_course)"));
    	
    	/*** external_course_requisite  ****/
    	statements.add(new ImmutablePair<String,String>("external_course_requisite has requisite_code that does not exist: ",
    			"select * from external_course_requisite where requisite_code NOT IN ('CO','PRE','PRE_CO')"));
    	statements.add(new ImmutablePair<String,String>("external_course_requisite has requiring_course_code that does not exist: ",
    			"select distinct requiring_course_code from external_course_requisite where requiring_course_code IS NOT NULL AND requiring_course_code NOT IN (select distinct code from external_course)"));
    	statements.add(new ImmutablePair<String,String>("external_course_requisite has required_course_code that does not exist: ",
    			"select distinct required_course_code from external_course_requisite where required_course_code IS NOT NULL AND required_course_code NOT IN (select distinct code from external_course)"));

    	/*** external_course_tag  ****/
    	statements.add(new ImmutablePair<String,String>("external_course_tag has course_code that does not exist: ",
    			"select distinct course_code from external_course_tag  where course_code IS NOT NULL AND course_code NOT IN (select distinct code from external_course)"));
  
    
    	/*** external_faculty_course_roster  ****/
    	statements.add(new ImmutablePair<String,String>("external_faculty_course_roster has faculty_school_id that does not exist: ",
    			"select distinct faculty_school_id from external_faculty_course_roster where faculty_school_id IS NOT NULL AND faculty_school_id NOT IN (select distinct school_id from external_person)"));
    	statements.add(new ImmutablePair<String,String>("external_faculty_course_roster has term_code that does not exist: ",
    			"select distinct term_code from external_faculty_course_roster where term_code IS NOT NULL AND term_code NOT IN (select distinct code from external_term)"));
    	statements.add(new ImmutablePair<String,String>("external_faculty_course_roster has formatted_course that does not exist: ",
    			"select distinct formatted_course from external_faculty_course_roster where formatted_course IS NOT NULL AND formatted_course NOT IN (select distinct formatted_course from external_course)"));
    	statements.add(new ImmutablePair<String,String>("external_faculty_course_roster has school_id that does not exist: ",
    			"select distinct school_id from external_faculty_course_roster where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));


    	/*** external_person_planning_status  ****/
    	statements.add(new ImmutablePair<String,String>("external_person_planning_status has status that does not exist: ",
    			"select school_id,status from external_person_planning_status where status NOT IN ('ON','OFF')"));
    	statements.add(new ImmutablePair<String,String>("external_person_planning_status has school_id that does not exist: ",
    			"select distinct school_id from external_person_planning_status where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));

    	/*** external_person_note  ****/
    	statements.add(new ImmutablePair<String,String>("external_person_note has school_id that does not exist: ",
    			"select distinct school_id from external_person_note where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));
    	
    	/*** external_registration_status_by_term  ****/
    	statements.add(new ImmutablePair<String,String>("external_registration_status_by_term has term_code that does not exist: ",
    			"select distinct term_code from external_registration_status_by_term where term_code IS NOT NULL AND term_code NOT IN (select distinct code from external_term)"));
    	statements.add(new ImmutablePair<String,String>("external_registration_status_by_term has school_id that does not exist: ",
    			"select distinct school_id from external_registration_status_by_term where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));


    	/*** external_student_academic_program  ****/
    	statements.add(new ImmutablePair<String,String>("external_student_transcript_term has school_id that does not exist: ",
    			"select distinct school_id from external_student_transcript_term where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));
 
    	/*** external_student_financial_aid  ****/
    	statements.add(new ImmutablePair<String,String>("external_student_financial_aid has school_id that does not exist: ",
    			"select distinct school_id from external_student_financial_aid where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));
    	statements.add(new ImmutablePair<String,String>("external_student_financial_aid has sap_status_code that does not exist: ",
    			"select distinct school_id,sap_status_code from external_student_financial_aid where sap_status_code IS NOT NULL AND sap_status_code NOT IN (select distinct code from sap_status)"));
    	statements.add(new ImmutablePair<String,String>("external_student_financial_aid has school_id that does not exist: ",
    			"select distinct school_id,financial_aid_file_status from external_student_financial_aid where financial_aid_file_status IS NOT NULL AND financial_aid_file_status NOT IN ('COMPLETE','PENDING','INCOMPLETE')"));

    	
    	/*** external_student_test  ****/
    	statements.add(new ImmutablePair<String,String>("external_student_test has school_id that does not exist: ",
    			"select distinct school_id from external_student_test where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));
    	
    	/*** external_student_transcript  ****/
    	statements.add(new ImmutablePair<String,String>("external_student_transcript has school_id that does not exist: ",
    			"select distinct school_id from external_student_transcript where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));

    	/*** external_student_transcript  ****/
    	statements.add(new ImmutablePair<String,String>("external_student_transcript has school_id that does not exist: ",
    			"select distinct school_id from external_student_transcript where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));
    	/*** external_student_transcript_course  ****/
    	statements.add(new ImmutablePair<String,String>("external_student_transcript_course has term_code that does not exist: ",
    			"select distinct term_code from external_student_transcript_course where term_code IS NOT NULL AND term_code NOT IN (select distinct code from external_term)"));
    	statements.add(new ImmutablePair<String,String>("external_student_transcript_course has school_id that does not exist: ",
    			"select distinct school_id from external_student_transcript_course where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));
    	statements.add(new ImmutablePair<String,String>("external_student_transcript_course has faculty_school_id that does not exist: ",
    			"select distinct faculty_school_id from external_student_transcript_course where faculty_school_id IS NOT NULL AND faculty_school_id NOT IN (select distinct faculty_school_id from external_person)"));
    	statements.add(new ImmutablePair<String,String>("external_student_transcript_course has formatted_course that does not exist: ",
    			"select distinct formatted_course from external_student_transcript_course where formatted_course IS NOT NULL AND formatted_course NOT IN (select distinct formatted_course from external_person)"));

    	/*** external_student_financial_aid_file  ****/
    	statements.add(new ImmutablePair<String,String>("external_student_financial_aid_file has school_id that does not exist: ",
    			"select distinct school_id from external_student_financial_aid_file where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));
    	statements.add(new ImmutablePair<String,String>("external_student_financial_aid_file has status that does not exist: ",
    			"select school_id,file_status from external_student_financial_aid_file where file_status IS NOT NULL AND file_status NOT IN ('COMPLETE','PENDING','INCOMPLETE')"));
    	statements.add(new ImmutablePair<String,String>("external_student_financial_aid_file has status that does not exist: ",
    			"select school_id,financial_file_code from external_student_financial_aid_file where financial_file_code IS NOT NULL AND financial_file_code NOT IN (select distinct code from financial_aid_file)"));

    	/*** external_student_financial_aid_award_term  ****/
    	statements.add(new ImmutablePair<String,String>("external_student_financial_aid_award_term has school_id that does not exist: ",
    			"select distinct school_id from external_student_financial_aid_award_term where school_id IS NOT NULL AND school_id NOT IN (select distinct school_id from external_person)"));
    	statements.add(new ImmutablePair<String,String>("external_student_financial_aid_award_term has accepted that does not exist: ",
    			"select school_id,accepted from external_student_financial_aid_award_term where accepted IS NOT NULL AND accepted NOT IN ('Y','N','y','n')"));
    	statements.add(new ImmutablePair<String,String>("external_student_financial_aid_award_term has term code that does not exist: ",
    			"select school_id,term_code from external_student_financial_aid_award_term where term_code IS NOT NULL AND term_code NOT IN (select distinct code from external_term)"));

    	statements.add(new ImmutablePair<String,String>("Count of External Courses: ",
    			"SELECT COUNT(code) from external_course"));
    	return statements;
    }
    
    public void setValidateDatabase(Boolean  validateDatabase){
    	this.validateDatabase = validateDatabase;
    }
    
    @Override
    public void beforeStep(StepExecution arg0) {
        this.stepExecution = arg0;
    }

    @Override
    public ExitStatus afterStep(StepExecution arg0) {
        return ExitStatus.COMPLETED;
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
