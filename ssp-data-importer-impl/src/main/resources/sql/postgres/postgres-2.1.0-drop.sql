--Spring batch framework tables
DROP TABLE  IF EXISTS BATCH_STEP_EXECUTION_CONTEXT;
DROP TABLE  IF EXISTS BATCH_JOB_EXECUTION_CONTEXT;
DROP TABLE  IF EXISTS BATCH_STEP_EXECUTION;
DROP TABLE  IF EXISTS BATCH_JOB_EXECUTION_PARAMS;
DROP TABLE  IF EXISTS BATCH_JOB_EXECUTION;
DROP TABLE  IF EXISTS BATCH_JOB_INSTANCE;

DROP SEQUENCE  IF EXISTS BATCH_STEP_EXECUTION_SEQ ;
DROP SEQUENCE  IF EXISTS BATCH_JOB_EXECUTION_SEQ ;
DROP SEQUENCE  IF EXISTS BATCH_JOB_SEQ ;

--Staging tables
DROP TABLE  IF EXISTS stg_external_course;
DROP TABLE  IF EXISTS stg_external_course_program;
DROP TABLE  IF EXISTS stg_external_course_requisite;
DROP TABLE  IF EXISTS stg_external_course_tag;
DROP TABLE  IF EXISTS stg_external_course_term;
DROP TABLE  IF EXISTS stg_external_department;
DROP TABLE  IF EXISTS stg_external_faculty_course;
DROP TABLE  IF EXISTS stg_external_faculty_course_roster;
DROP TABLE  IF EXISTS stg_external_person;
DROP TABLE  IF EXISTS stg_external_person_note;
DROP TABLE  IF EXISTS stg_external_person_planning_status;
DROP TABLE  IF EXISTS stg_external_program;
DROP TABLE  IF EXISTS stg_external_student_academic_program;
DROP TABLE  IF EXISTS stg_external_division;
DROP TABLE  IF EXISTS stg_external_registration_status_by_term;
DROP TABLE  IF EXISTS stg_external_student_financial_aid;
DROP TABLE  IF EXISTS stg_external_student_test;
DROP TABLE  IF EXISTS stg_external_student_transcript;
DROP TABLE  IF EXISTS stg_external_student_transcript_course;
DROP TABLE  IF EXISTS stg_external_student_transcript_term;
DROP TABLE  IF EXISTS stg_external_term;







