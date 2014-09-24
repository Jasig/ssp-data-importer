--
-- Licensed to Jasig under one or more contributor license
-- agreements. See the NOTICE file distributed with this work
-- for additional information regarding copyright ownership.
-- Jasig licenses this file to you under the Apache License,
-- Version 2.0 (the "License"); you may not use this file
-- except in compliance with the License. You may obtain a
-- copy of the License at:
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on
-- an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET search_path = public, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = false;
CREATE TABLE BATCH_JOB_INSTANCE  (
    JOB_INSTANCE_ID BIGINT  NOT NULL PRIMARY KEY ,
    VERSION BIGINT ,
    JOB_NAME VARCHAR(100) NOT NULL,
    JOB_KEY VARCHAR(32) NOT NULL,
    constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
) ;
CREATE TABLE BATCH_JOB_EXECUTION  (
    JOB_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
    VERSION BIGINT  ,
    JOB_INSTANCE_ID BIGINT NOT NULL,
    CREATE_TIME TIMESTAMP NOT NULL,
    START_TIME TIMESTAMP DEFAULT NULL ,
    END_TIME TIMESTAMP DEFAULT NULL ,
    STATUS VARCHAR(10) ,
    EXIT_CODE VARCHAR(100) ,
    EXIT_MESSAGE VARCHAR(2500) ,
    LAST_UPDATED TIMESTAMP,
    constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
    references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) ;
CREATE TABLE BATCH_JOB_EXECUTION_PARAMS  (
    JOB_EXECUTION_ID BIGINT NOT NULL ,
    TYPE_CD VARCHAR(6) NOT NULL ,
    KEY_NAME VARCHAR(100) NOT NULL ,
    STRING_VAL VARCHAR(250) ,
    DATE_VAL TIMESTAMP DEFAULT NULL ,
    LONG_VAL BIGINT ,
    DOUBLE_VAL DOUBLE PRECISION ,
    IDENTIFYING CHAR(1) NOT NULL ,
    constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
    references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;
CREATE TABLE BATCH_STEP_EXECUTION  (
    STEP_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
    VERSION BIGINT NOT NULL,
    STEP_NAME VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID BIGINT NOT NULL,
    START_TIME TIMESTAMP NOT NULL ,
    END_TIME TIMESTAMP DEFAULT NULL ,
    STATUS VARCHAR(10) ,
    COMMIT_COUNT BIGINT ,
    READ_COUNT BIGINT ,
    FILTER_COUNT BIGINT ,
    WRITE_COUNT BIGINT ,
    READ_SKIP_COUNT BIGINT ,
    WRITE_SKIP_COUNT BIGINT ,
    PROCESS_SKIP_COUNT BIGINT ,
    ROLLBACK_COUNT BIGINT ,
    EXIT_CODE VARCHAR(100) ,
    EXIT_MESSAGE VARCHAR(2500) ,
    LAST_UPDATED TIMESTAMP,
    constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
    references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;
CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT  (
    STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT ,
    constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
    references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
) ;
CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT  (
    JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT ,
    constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
    references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;
CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
REVOKE ALL ON TABLE BATCH_JOB_INSTANCE FROM PUBLIC;
REVOKE ALL ON TABLE BATCH_JOB_INSTANCE FROM sspadmin;
GRANT ALL ON TABLE BATCH_JOB_INSTANCE TO sspadmin;
GRANT ALL ON TABLE BATCH_JOB_INSTANCE TO ssp;
REVOKE ALL ON TABLE BATCH_JOB_EXECUTION FROM PUBLIC;
REVOKE ALL ON TABLE BATCH_JOB_EXECUTION FROM sspadmin;
GRANT ALL ON TABLE BATCH_JOB_EXECUTION TO sspadmin;
GRANT ALL ON TABLE BATCH_JOB_EXECUTION TO ssp;
REVOKE ALL ON TABLE BATCH_JOB_EXECUTION_PARAMS FROM PUBLIC;
REVOKE ALL ON TABLE BATCH_JOB_EXECUTION_PARAMS FROM sspadmin;
GRANT ALL ON TABLE BATCH_JOB_EXECUTION_PARAMS TO sspadmin;
GRANT ALL ON TABLE BATCH_JOB_EXECUTION_PARAMS TO ssp;
REVOKE ALL ON TABLE BATCH_STEP_EXECUTION FROM PUBLIC;
REVOKE ALL ON TABLE BATCH_STEP_EXECUTION FROM sspadmin;
GRANT ALL ON TABLE BATCH_STEP_EXECUTION TO sspadmin;
GRANT ALL ON TABLE BATCH_STEP_EXECUTION TO ssp;
REVOKE ALL ON TABLE BATCH_STEP_EXECUTION_CONTEXT FROM PUBLIC;
REVOKE ALL ON TABLE BATCH_STEP_EXECUTION_CONTEXT FROM sspadmin;
GRANT ALL ON TABLE BATCH_STEP_EXECUTION_CONTEXT TO sspadmin;
GRANT ALL ON TABLE BATCH_STEP_EXECUTION_CONTEXT TO ssp;
REVOKE ALL ON TABLE BATCH_JOB_EXECUTION_CONTEXT FROM PUBLIC;
REVOKE ALL ON TABLE BATCH_JOB_EXECUTION_CONTEXT FROM sspadmin;
GRANT ALL ON TABLE BATCH_JOB_EXECUTION_CONTEXT TO sspadmin;
GRANT ALL ON TABLE BATCH_JOB_EXECUTION_CONTEXT TO ssp;
--Staging tables
CREATE TABLE stg_external_course (
    batch_id BIGINT ,
    code character varying(50) NOT NULL,
    formatted_course character varying(35) NOT NULL,
    subject_abbreviation character varying(10) NOT NULL,
    title character varying(100) NOT NULL,
    description character varying(2500) NOT NULL,
    max_credit_hours numeric(9,2) NOT NULL,
    min_credit_hours numeric(9,2) NOT NULL,
    number character varying(15) DEFAULT '0'::character varying NOT NULL,
    is_dev character(1) DEFAULT 'N'::bpchar NOT NULL,
    academic_link character varying(2000),
    department_code character varying(50),
    division_code character varying(50),
    master_syllabus_link character varying(2000)
);
CREATE TABLE stg_external_course_program (
    batch_id BIGINT ,
    course_code character varying(50) NOT NULL,
    program_code character varying(50) NOT NULL,
    program_name character varying(100) NOT NULL
);
CREATE TABLE stg_external_course_requisite (
    batch_id BIGINT ,
    requiring_course_code character varying(50) NOT NULL,
    required_course_code character varying(50) NOT NULL,
    required_formatted_course character varying(35) NOT NULL,
    requisite_code character varying(8) NOT NULL
);
CREATE TABLE stg_external_course_tag (
    batch_id BIGINT ,
    course_code character varying(50) NOT NULL,
    tag character varying(100) NOT NULL
);
CREATE TABLE stg_external_course_term (
    batch_id BIGINT ,
    course_code character varying(50) NOT NULL,
    term_code character varying(25) NOT NULL
);
CREATE TABLE stg_external_department (
    batch_id BIGINT ,
    code character varying(50) NOT NULL,
    name character varying(100) NOT NULL
);
CREATE TABLE stg_external_division (
    batch_id BIGINT ,
    code character varying(50) NOT NULL,
    name character varying(100) NOT NULL
);
CREATE TABLE stg_external_faculty_course (
    batch_id BIGINT ,
    faculty_school_id character varying(50) NOT NULL,
    term_code character varying(25) NOT NULL,
    formatted_course character varying(35) NOT NULL,
    title character varying(100) NOT NULL,
    section_code character varying(128),
    section_number character varying(10)
);
CREATE TABLE stg_external_faculty_course_roster (
    batch_id BIGINT ,
    faculty_school_id character varying(50) NOT NULL,
    school_id character varying(50) NOT NULL,
    first_name character varying(50) NOT NULL,
    middle_name character varying(50),
    last_name character varying(50) NOT NULL,
    primary_email_address character varying(100) NOT NULL,
    term_code character varying(25) NOT NULL,
    formatted_course character varying(35) NOT NULL,
    status_code character varying(2),
    section_code character varying(128),
    section_number character varying(10)
);
CREATE TABLE stg_external_person (
    batch_id BIGINT ,
    school_id character varying(50) NOT NULL,
    username character varying(50) NOT NULL,
    first_name character varying(50) NOT NULL,
    middle_name character varying(50),
    last_name character varying(50) NOT NULL,
    birth_date date,
    primary_email_address character varying(100),
    address_line_1 character varying(50),
    address_line_2 character varying(50),
    city character varying(50),
    state character(2),
    zip_code character varying(10),
    home_phone character varying(25),
    work_phone character varying(25),
    office_location character varying(50),
    office_hours character varying(50),
    department_name character varying(100),
    actual_start_term character varying(20),
    actual_start_year integer,
    marital_status character varying(80),
    ethnicity character varying(80),
    gender character(1),
    is_local character(1),
    balance_owed numeric(9,2),
    coach_school_id character varying(50),
    cell_phone character varying(25),
    photo_url character varying(250),
    residency_county character varying(50),
    f1_status character(1),
    non_local_address character(1) DEFAULT 'N'::bpchar NOT NULL,
    student_type_code character varying(10),
    race_code character varying(10)
);
CREATE TABLE stg_external_person_note (
    batch_id BIGINT ,
    code character varying(50) NOT NULL,
    school_id character varying(50) NOT NULL,
    note_type character varying(35) NOT NULL,
    author character varying(80) NOT NULL,
    department character varying(80),
    date_note_taken date NOT NULL,
    note text NOT NULL
);
CREATE TABLE stg_external_person_planning_status (
    batch_id BIGINT ,
    school_id character varying(50) NOT NULL,
    status character varying(8) NOT NULL,
    status_reason character varying(255)
);
CREATE TABLE stg_external_program (
    batch_id BIGINT ,
    code character varying(50) NOT NULL,
    name character varying(100) NOT NULL
);
CREATE TABLE stg_external_registration_status_by_term (
    batch_id BIGINT ,
    school_id character varying(50) NOT NULL,
    term_code character varying(25) NOT NULL,
    registered_course_count integer NOT NULL,
    tuition_paid character(1)
);
CREATE TABLE stg_external_student_academic_program (
    batch_id BIGINT ,
    school_id character varying(50) NOT NULL,
    degree_code character varying(10) NOT NULL,
    degree_name character varying(100) NOT NULL,
    program_code character varying(50) NOT NULL,
    program_name character varying(100) NOT NULL,
    intended_program_at_admit character varying(100)
);
CREATE TABLE stg_external_student_financial_aid (
    batch_id BIGINT ,
    school_id character varying(50) NOT NULL,
    financial_aid_gpa numeric(9,2) NOT NULL,
    gpa_20_b_hrs_needed numeric(9,2),
    gpa_20_a_hrs_needed numeric(9,2),
    needed_for_67ptc_completion numeric(9,2),
    current_year_financial_aid_award character(1),
    sap_status character(1),
    fafsa_date date,
    financial_aid_remaining numeric(9,2),
    original_loan_amount numeric(9,2),
    remaining_loan_amount numeric(9,2),
    sap_status_code character varying(25),
    institutional_loan_amount numeric(9,2),
    eligible_federal_aid character(1),
    financial_aid_file_status character varying(10),
    terms_left integer
);
CREATE TABLE stg_external_student_financial_aid_award_term (
    batch_id BIGINT ,
    school_id character varying(50) NOT NULL,
    accepted character(1) NOT NULL,
    term_code character varying(25) NOT NULL
);
CREATE TABLE stg_external_student_financial_aid_file (
    batch_id BIGINT ,
    school_id character varying(50) NOT NULL,
    file_status character varying(10) NOT NULL,
    financial_file_code character varying(25) NOT NULL
);
CREATE TABLE stg_external_student_test (
    batch_id BIGINT ,
    school_id character varying(50) NOT NULL,
    test_name character varying(50) NOT NULL,
    test_code character varying(25) NOT NULL,
    sub_test_code character varying(25) NOT NULL,
    sub_test_name character varying(50),
    test_date date NOT NULL,
    score numeric(9,2) NOT NULL,
    status character varying(25) NOT NULL,
    discriminator character varying(1) DEFAULT '1'::character varying NOT NULL,
    outcome character varying(255)
);
CREATE TABLE stg_external_student_transcript (
    batch_id BIGINT ,
    school_id character varying(50) NOT NULL,
    credit_hours_for_gpa numeric(9,2),
    credit_hours_earned numeric(9,2),
    credit_hours_attempted numeric(9,2),
    total_quality_points numeric(9,2),
    grade_point_average numeric(9,2) NOT NULL,
    academic_standing character varying(50),
    credit_hours_not_completed numeric(9,2),
    credit_completion_rate numeric(9,2),
    gpa_trend_indicator character varying(25),
    current_restrictions character varying(100)
);
CREATE TABLE stg_external_student_transcript_course (
    batch_id BIGINT ,
    school_id character varying(50) NOT NULL,
    subject_abbreviation character varying(10) NOT NULL,
    number character varying(15) NOT NULL,
    formatted_course character varying(35) NOT NULL,
    section_number character varying(10),
    title character varying(100),
    description character varying(2500),
    grade character varying(10),
    credit_earned numeric(9,2),
    term_code character varying(25) NOT NULL,
    credit_type character varying(25),
    first_name character varying(50) NOT NULL,
    middle_name character varying(50),
    last_name character varying(50) NOT NULL,
    audited character(1),
    status_code character varying(2),
    section_code character varying(128) NOT NULL,
    faculty_school_id character varying(50),
    course_code character varying(50)
);
CREATE TABLE stg_external_student_transcript_term (
    batch_id BIGINT ,
    school_id character varying(50) NOT NULL,
    credit_hours_for_gpa numeric(9,2),
    credit_hours_earned numeric(9,2),
    credit_hours_attempted numeric(9,2),
    credit_hours_not_completed numeric(9,2),
    credit_completion_rate numeric(9,2),
    total_quality_points numeric(9,2),
    grade_point_average numeric(9,2) NOT NULL,
    term_code character varying(25) NOT NULL
);
CREATE TABLE stg_external_term (
    batch_id BIGINT ,
    name character varying(80) NOT NULL,
    code character varying(25) NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    report_year integer NOT NULL,
    registration_start_date date,
    registration_end_date date
);


CREATE TABLE stg_external_substitutable_course (
    batch_id BIGINT ,
    term_code character varying(25),
    program_code  character varying(50),
    source_formatted_course character varying(35) NOT NULL,
    source_course_code  character varying(50) ,
    source_course_title character varying(100) ,
    source_credit_hours  numeric(9,2) ,
    target_formatted_course  character varying(35) NOT NULL,
    target_course_code character varying(50),
    target_course_title  character varying(100),
    target_credit_hours  numeric(9,2)
);

CREATE TABLE stg_external_catalog_year (
    batch_id BIGINT,
    code character varying(50),
    name character varying(50)
);


ALTER TABLE ONLY stg_external_course
    ADD CONSTRAINT stg_external_course_pkey PRIMARY KEY (code);
ALTER TABLE ONLY stg_external_course_program
    ADD CONSTRAINT stg_external_course_program_pkey PRIMARY KEY (program_code, course_code);
ALTER TABLE ONLY stg_external_course_requisite
    ADD CONSTRAINT stg_external_course_requisite_pkey PRIMARY KEY (requiring_course_code, required_course_code, requisite_code);
ALTER TABLE ONLY stg_external_course_tag
    ADD CONSTRAINT stg_external_course_tag_pkey PRIMARY KEY (course_code, tag);
ALTER TABLE ONLY stg_external_course_term
    ADD CONSTRAINT stg_external_course_term_pkey PRIMARY KEY (course_code, term_code);
ALTER TABLE ONLY stg_external_department
    ADD CONSTRAINT stg_external_department_pkey PRIMARY KEY (code);
ALTER TABLE ONLY stg_external_division
    ADD CONSTRAINT stg_external_division_pkey PRIMARY KEY (code);
ALTER TABLE ONLY stg_external_person
    ADD CONSTRAINT stg_external_person_pkey PRIMARY KEY (school_id);
ALTER TABLE ONLY stg_external_person_planning_status
    ADD CONSTRAINT stg_external_person_planning_status_pkey PRIMARY KEY (school_id);
ALTER TABLE ONLY stg_external_person
    ADD CONSTRAINT stg_external_person_school_id_key UNIQUE (school_id);
ALTER TABLE ONLY stg_external_person
    ADD CONSTRAINT stg_external_person_username_key UNIQUE (username);
ALTER TABLE ONLY stg_external_program
    ADD CONSTRAINT stg_external_program_code_key UNIQUE (code);
ALTER TABLE ONLY stg_external_program
    ADD CONSTRAINT stg_external_program_pkey PRIMARY KEY (code);
ALTER TABLE ONLY stg_external_registration_status_by_term
    ADD CONSTRAINT stg_external_registration_status_by_term_pkey PRIMARY KEY (school_id, term_code);;
ALTER TABLE ONLY stg_external_student_academic_program
    ADD CONSTRAINT stg_external_student_academic_program_pkey PRIMARY KEY (school_id, degree_code, program_code);
ALTER TABLE ONLY stg_external_student_financial_aid_award_term
    ADD CONSTRAINT stg_external_student_fin_award_term_pkey PRIMARY KEY (school_id, term_code);
ALTER TABLE ONLY stg_external_student_financial_aid_file
    ADD CONSTRAINT stg_external_student_financial_aid_file_pkey PRIMARY KEY (school_id, financial_file_code);
ALTER TABLE ONLY stg_external_student_financial_aid
    ADD CONSTRAINT stg_external_student_financial_aid_pkey PRIMARY KEY (school_id);
ALTER TABLE ONLY stg_external_student_test
    ADD CONSTRAINT stg_external_student_test_pk PRIMARY KEY (school_id, test_code, sub_test_code, test_date, discriminator);
ALTER TABLE ONLY stg_external_student_transcript_course
    ADD CONSTRAINT stg_external_student_transcript_course_pkey PRIMARY KEY (school_id, term_code, formatted_course, section_code);
ALTER TABLE ONLY stg_external_student_transcript
    ADD CONSTRAINT stg_external_student_transcript_pkey PRIMARY KEY (school_id);
ALTER TABLE ONLY stg_external_student_transcript_term
    ADD CONSTRAINT stg_external_student_transcript_term_pkey PRIMARY KEY (school_id, term_code);
ALTER TABLE ONLY stg_external_term
    ADD CONSTRAINT stg_external_term_pkey PRIMARY KEY (code);
ALTER TABLE ONLY stg_external_faculty_course
    ADD CONSTRAINT stg_external_faculty_course_pkey PRIMARY KEY (faculty_school_id, section_code);
ALTER TABLE ONLY stg_external_faculty_course
    ADD CONSTRAINT stg_external_faculty_course_unq UNIQUE (faculty_school_id, section_code);
ALTER TABLE ONLY stg_external_faculty_course_roster
    ADD CONSTRAINT stg_external_faculty_course_roster_pkey PRIMARY KEY (school_id, section_code);
ALTER TABLE ONLY stg_external_faculty_course_roster
    ADD CONSTRAINT stg_stg_external_faculty_course_roster_unq UNIQUE (school_id, section_code); 
ALTER TABLE ONLY stg_external_substitutable_course
    ADD CONSTRAINT stg_stg_external_substitutable_course_pkey PRIMARY KEY (source_formatted_course, target_formatted_course);
ALTER TABLE ONLY stg_external_substitutable_course
    ADD CONSTRAINT stg_stg_external_substitutable_course_unq UNIQUE (source_formatted_course, target_formatted_course);  
ALTER TABLE ONLY stg_external_catalog_year
	ADD CONSTRAINT stg_stg_external_catalog_year_pkey PRIMARY KEY (code,name);
    
REVOKE ALL ON TABLE stg_external_course FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_course FROM sspadmin;
GRANT ALL ON TABLE stg_external_course TO sspadmin;
GRANT ALL ON TABLE stg_external_course TO ssp;
REVOKE ALL ON TABLE stg_external_course_program FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_course_program FROM sspadmin;
GRANT ALL ON TABLE stg_external_course_program TO sspadmin;
GRANT ALL ON TABLE stg_external_course_program TO ssp;
REVOKE ALL ON TABLE stg_external_course_requisite FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_course_requisite FROM sspadmin;
GRANT ALL ON TABLE stg_external_course_requisite TO sspadmin;
GRANT ALL ON TABLE stg_external_course_requisite TO ssp;
REVOKE ALL ON TABLE stg_external_course_tag FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_course_tag FROM sspadmin;
GRANT ALL ON TABLE stg_external_course_tag TO sspadmin;
GRANT ALL ON TABLE stg_external_course_tag TO ssp;
REVOKE ALL ON TABLE stg_external_course_term FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_course_term FROM sspadmin;
GRANT ALL ON TABLE stg_external_course_term TO sspadmin;
GRANT ALL ON TABLE stg_external_course_term TO ssp;
REVOKE ALL ON TABLE stg_external_department FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_department FROM sspadmin;
GRANT ALL ON TABLE stg_external_department TO sspadmin;
GRANT ALL ON TABLE stg_external_department TO ssp;
REVOKE ALL ON TABLE stg_external_division FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_division FROM sspadmin;
GRANT ALL ON TABLE stg_external_division TO sspadmin;
GRANT ALL ON TABLE stg_external_division TO ssp;
REVOKE ALL ON TABLE stg_external_faculty_course FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_faculty_course FROM sspadmin;
GRANT ALL ON TABLE stg_external_faculty_course TO sspadmin;
GRANT ALL ON TABLE stg_external_faculty_course TO ssp;
REVOKE ALL ON TABLE stg_external_faculty_course_roster FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_faculty_course_roster FROM sspadmin;
GRANT ALL ON TABLE stg_external_faculty_course_roster TO sspadmin;
GRANT ALL ON TABLE stg_external_faculty_course_roster TO ssp;
REVOKE ALL ON TABLE stg_external_person FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_person FROM sspadmin;
GRANT ALL ON TABLE stg_external_person TO sspadmin;
GRANT ALL ON TABLE stg_external_person TO ssp;
REVOKE ALL ON TABLE stg_external_person_note FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_person_note FROM sspadmin;
GRANT ALL ON TABLE stg_external_person_note TO sspadmin;
GRANT ALL ON TABLE stg_external_person_note TO ssp;
REVOKE ALL ON TABLE stg_external_person_planning_status FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_person_planning_status FROM sspadmin;
GRANT ALL ON TABLE stg_external_person_planning_status TO sspadmin;
GRANT ALL ON TABLE stg_external_person_planning_status TO ssp;
REVOKE ALL ON TABLE stg_external_program FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_program FROM sspadmin;
GRANT ALL ON TABLE stg_external_program TO sspadmin;
GRANT ALL ON TABLE stg_external_program TO ssp;
REVOKE ALL ON TABLE stg_external_registration_status_by_term FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_registration_status_by_term FROM sspadmin;
GRANT ALL ON TABLE stg_external_registration_status_by_term TO sspadmin;
GRANT ALL ON TABLE stg_external_registration_status_by_term TO ssp;
REVOKE ALL ON TABLE stg_external_student_academic_program FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_student_academic_program FROM sspadmin;
GRANT ALL ON TABLE stg_external_student_academic_program TO sspadmin;
GRANT ALL ON TABLE stg_external_student_academic_program TO ssp;
REVOKE ALL ON TABLE stg_external_student_financial_aid FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_student_financial_aid FROM sspadmin;
GRANT ALL ON TABLE stg_external_student_financial_aid TO sspadmin;
GRANT ALL ON TABLE stg_external_student_financial_aid TO ssp;
REVOKE ALL ON TABLE stg_external_student_financial_aid_award_term FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_student_financial_aid_award_term FROM sspadmin;
GRANT ALL ON TABLE stg_external_student_financial_aid_award_term TO sspadmin;
GRANT ALL ON TABLE stg_external_student_financial_aid_award_term TO ssp;
REVOKE ALL ON TABLE stg_external_student_financial_aid_file FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_student_financial_aid_file FROM sspadmin;
GRANT ALL ON TABLE stg_external_student_financial_aid_file TO sspadmin;
GRANT ALL ON TABLE stg_external_student_financial_aid_file TO ssp;
REVOKE ALL ON TABLE stg_external_student_test FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_student_test FROM sspadmin;
GRANT ALL ON TABLE stg_external_student_test TO sspadmin;
GRANT ALL ON TABLE stg_external_student_test TO ssp;
REVOKE ALL ON TABLE stg_external_student_transcript FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_student_transcript FROM sspadmin;
GRANT ALL ON TABLE stg_external_student_transcript TO sspadmin;
GRANT ALL ON TABLE stg_external_student_transcript TO ssp;
REVOKE ALL ON TABLE stg_external_student_transcript_course FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_student_transcript_course FROM sspadmin;
GRANT ALL ON TABLE stg_external_student_transcript_course TO sspadmin;
GRANT ALL ON TABLE stg_external_student_transcript_course TO ssp;
REVOKE ALL ON TABLE stg_external_student_transcript_term FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_student_transcript_term FROM sspadmin;
GRANT ALL ON TABLE stg_external_student_transcript_term TO sspadmin;
GRANT ALL ON TABLE stg_external_student_transcript_term TO ssp;
REVOKE ALL ON TABLE stg_external_term FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_term FROM sspadmin;
GRANT ALL ON TABLE stg_external_term TO sspadmin;
GRANT ALL ON TABLE stg_external_term TO ssp;
REVOKE ALL ON TABLE stg_external_substitutable_course FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_substitutable_course FROM sspadmin;
GRANT ALL ON TABLE stg_external_substitutable_course TO sspadmin;
GRANT ALL ON TABLE stg_external_substitutable_course TO ssp;
REVOKE ALL ON TABLE stg_external_calendar_year FROM PUBLIC;
REVOKE ALL ON TABLE stg_external_calendar_year FROM sspadmin;
GRANT ALL ON TABLE stg_external_calendar_year TO sspadmin;
GRANT ALL ON TABLE stg_external_calendar_year TO ssp;

CREATE TABLE external_course (
    code character varying(50) NOT NULL,
    formatted_course character varying(35) NOT NULL,
    subject_abbreviation character varying(10) NOT NULL,
    title character varying(100) NOT NULL,
    description character varying(2500) NOT NULL,
    max_credit_hours numeric(9,2) NOT NULL,
    min_credit_hours numeric(9,2) NOT NULL,
    number character varying(15) DEFAULT '0'::character varying NOT NULL,
    is_dev character(1) DEFAULT 'N'::bpchar NOT NULL,
    academic_link character varying(2000),
    department_code character varying(50),
    division_code character varying(50),
    master_syllabus_link character varying(2000)
);
ALTER TABLE public.external_course OWNER TO sspadmin;
CREATE TABLE external_course_program (
    course_code character varying(50) NOT NULL,
    program_code character varying(50) NOT NULL,
    program_name character varying(100) NOT NULL
);
ALTER TABLE public.external_course_program OWNER TO sspadmin;
CREATE TABLE external_course_requisite (
    requiring_course_code character varying(50) NOT NULL,
    required_course_code character varying(50) NOT NULL,
    required_formatted_course character varying(35) NOT NULL,
    requisite_code character varying(8) NOT NULL
);
ALTER TABLE public.external_course_requisite OWNER TO sspadmin;
CREATE TABLE external_course_tag (
    course_code character varying(50) NOT NULL,
    tag character varying(100) NOT NULL
);
ALTER TABLE public.external_course_tag OWNER TO sspadmin;
CREATE TABLE external_course_term (
    course_code character varying(50) NOT NULL,
    term_code character varying(25) NOT NULL
);
ALTER TABLE public.external_course_term OWNER TO sspadmin;
CREATE TABLE external_department (
    code character varying(50) NOT NULL,
    name character varying(100) NOT NULL
);
ALTER TABLE public.external_department OWNER TO sspadmin;
CREATE TABLE external_division (
    code character varying(50) NOT NULL,
    name character varying(100) NOT NULL
);
ALTER TABLE public.external_division OWNER TO sspadmin;
CREATE TABLE external_faculty_course (
    faculty_school_id character varying(50) NOT NULL,
    term_code character varying(25) NOT NULL,
    formatted_course character varying(35) NOT NULL,
    title character varying(100) NOT NULL,
    section_code character varying(128),
    section_number character varying(10)
);
ALTER TABLE public.external_faculty_course OWNER TO sspadmin;

COMMENT ON COLUMN external_faculty_course.faculty_school_id IS 'The school_id used to identify a faculty member.';

COMMENT ON COLUMN external_faculty_course.term_code IS 'Specifies what term the course was taught by the faculty member. Equivalent to external_term.code value.';

COMMENT ON COLUMN external_faculty_course.formatted_course IS 'The formatted course subject abbreviation and number';

COMMENT ON COLUMN external_faculty_course.title IS 'The title of the course.';
CREATE TABLE external_faculty_course_roster (
    faculty_school_id character varying(50) NOT NULL,
    school_id character varying(50) NOT NULL,
    first_name character varying(50) NOT NULL,
    middle_name character varying(50),
    last_name character varying(50) NOT NULL,
    primary_email_address character varying(100) NOT NULL,
    term_code character varying(25) NOT NULL,
    formatted_course character varying(35) NOT NULL,
    status_code character varying(2),
    section_code character varying(128),
    section_number character varying(10)
);
ALTER TABLE public.external_faculty_course_roster OWNER TO sspadmin;

COMMENT ON COLUMN external_faculty_course_roster.faculty_school_id IS 'The school_id used to identify a faculty member.';

COMMENT ON COLUMN external_faculty_course_roster.school_id IS 'Student''s school id.';

COMMENT ON COLUMN external_faculty_course_roster.first_name IS 'Student''s first name.';

COMMENT ON COLUMN external_faculty_course_roster.middle_name IS 'Student''s middle name.';

COMMENT ON COLUMN external_faculty_course_roster.last_name IS 'Student''s last name.';

COMMENT ON COLUMN external_faculty_course_roster.primary_email_address IS 'Student''s primary email address. Typically the institution-assigned email address.';

COMMENT ON COLUMN external_faculty_course_roster.term_code IS 'Specifies what term the course was taught by the faculty member. Equivalent to external_term.code value.';

COMMENT ON COLUMN external_faculty_course_roster.formatted_course IS 'The formatted course subject abbreviation and number';
CREATE TABLE external_person (
    school_id character varying(50) NOT NULL,
    username character varying(50) NOT NULL,
    first_name character varying(50) NOT NULL,
    middle_name character varying(50),
    last_name character varying(50) NOT NULL,
    birth_date date,
    primary_email_address character varying(100),
    address_line_1 character varying(50),
    address_line_2 character varying(50),
    city character varying(50),
    state character(2),
    zip_code character varying(10),
    home_phone character varying(25),
    work_phone character varying(25),
    office_location character varying(50),
    office_hours character varying(50),
    department_name character varying(100),
    actual_start_term character varying(20),
    actual_start_year integer,
    marital_status character varying(80),
    ethnicity character varying(80),
    gender character(1),
    is_local character(1),
    balance_owed numeric(9,2),
    coach_school_id character varying(50),
    cell_phone character varying(25),
    photo_url character varying(250),
    residency_county character varying(50),
    f1_status character(1),
    non_local_address character(1) DEFAULT 'N'::bpchar NOT NULL,
    student_type_code character varying(10),
    race_code character varying(10)
);
ALTER TABLE public.external_person OWNER TO sspadmin;
CREATE TABLE external_person_note (
    code character varying(50) NOT NULL,
    school_id character varying(50) NOT NULL,
    note_type character varying(35) NOT NULL,
    author character varying(80) NOT NULL,
    department character varying(80),
    date_note_taken date NOT NULL,
    note text NOT NULL
);
ALTER TABLE public.external_person_note OWNER TO sspadmin;
CREATE TABLE external_person_planning_status (
    school_id character varying(50) NOT NULL,
    status character varying(8) NOT NULL,
    status_reason character varying(255)
);
ALTER TABLE public.external_person_planning_status OWNER TO sspadmin;
CREATE TABLE external_program (
    code character varying(50) NOT NULL,
    name character varying(100) NOT NULL
);
ALTER TABLE public.external_program OWNER TO sspadmin;
CREATE TABLE external_registration_status_by_term (
    school_id character varying(50) NOT NULL,
    term_code character varying(25) NOT NULL,
    registered_course_count integer NOT NULL,
    tuition_paid character(1)
);
ALTER TABLE public.external_registration_status_by_term OWNER TO sspadmin;
CREATE TABLE external_student_academic_program (
    school_id character varying(50) NOT NULL,
    degree_code character varying(10) NOT NULL,
    degree_name character varying(100) NOT NULL,
    program_code character varying(50) NOT NULL,
    program_name character varying(100) NOT NULL,
    intended_program_at_admit character varying(100)
);
ALTER TABLE public.external_student_academic_program OWNER TO sspadmin;
CREATE TABLE external_student_financial_aid (
    school_id character varying(50) NOT NULL,
    financial_aid_gpa numeric(9,2) NOT NULL,
    gpa_20_b_hrs_needed numeric(9,2),
    gpa_20_a_hrs_needed numeric(9,2),
    needed_for_67ptc_completion numeric(9,2),
    current_year_financial_aid_award character(1),
    sap_status character(1),
    fafsa_date date,
    financial_aid_remaining numeric(9,2),
    original_loan_amount numeric(9,2),
    remaining_loan_amount numeric(9,2),
    sap_status_code character varying(25),
    institutional_loan_amount numeric(9,2),
    eligible_federal_aid character(1),
    financial_aid_file_status character varying(10),
    terms_left integer
);
ALTER TABLE public.external_student_financial_aid OWNER TO sspadmin;
CREATE TABLE external_student_financial_aid_award_term (
    school_id character varying(50) NOT NULL,
    accepted character(1) NOT NULL,
    term_code character varying(25) NOT NULL
);
ALTER TABLE public.external_student_financial_aid_award_term OWNER TO sspadmin;
CREATE TABLE external_student_financial_aid_file (
    school_id character varying(50) NOT NULL,
    file_status character varying(10) NOT NULL,
    financial_file_code character varying(25) NOT NULL
);
ALTER TABLE public.external_student_financial_aid_file OWNER TO sspadmin;
CREATE TABLE external_student_test (
    school_id character varying(50) NOT NULL,
    test_name character varying(50) NOT NULL,
    test_code character varying(25) NOT NULL,
    sub_test_code character varying(25) NOT NULL,
    sub_test_name character varying(50),
    test_date date NOT NULL,
    score numeric(9,2) NOT NULL,
    status character varying(25) NOT NULL,
    discriminator character varying(1) DEFAULT '1'::character varying NOT NULL,
    outcome character varying(255)
);
ALTER TABLE public.external_student_test OWNER TO sspadmin;
CREATE TABLE external_student_transcript (
    school_id character varying(50) NOT NULL,
    credit_hours_for_gpa numeric(9,2),
    credit_hours_earned numeric(9,2),
    credit_hours_attempted numeric(9,2),
    total_quality_points numeric(9,2),
    grade_point_average numeric(9,2) NOT NULL,
    academic_standing character varying(50),
    credit_hours_not_completed numeric(9,2),
    credit_completion_rate numeric(9,2),
    gpa_trend_indicator character varying(25),
    current_restrictions character varying(100)
);
ALTER TABLE public.external_student_transcript OWNER TO sspadmin;
CREATE TABLE external_student_transcript_course (
    school_id character varying(50) NOT NULL,
    subject_abbreviation character varying(10) NOT NULL,
    number character varying(15) NOT NULL,
    formatted_course character varying(35) NOT NULL,
    section_number character varying(10),
    title character varying(100),
    description character varying(2500),
    grade character varying(10) NOT NULL,
    credit_earned numeric(9,2),
    term_code character varying(25) NOT NULL,
    credit_type character varying(25),
    first_name character varying(50) NOT NULL,
    middle_name character varying(50),
    last_name character varying(50) NOT NULL,
    audited character(1),
    status_code character varying(2),
    section_code character varying(128) NOT NULL,
    faculty_school_id character varying(50),
    course_code character varying(50)
);
ALTER TABLE public.external_student_transcript_course OWNER TO sspadmin;
CREATE TABLE external_student_transcript_term (
    school_id character varying(50) NOT NULL,
    credit_hours_for_gpa numeric(9,2),
    credit_hours_earned numeric(9,2),
    credit_hours_attempted numeric(9,2),
    credit_hours_not_completed numeric(9,2),
    credit_completion_rate numeric(9,2),
    total_quality_points numeric(9,2),
    grade_point_average numeric(9,2) NOT NULL,
    term_code character varying(25) NOT NULL
);
ALTER TABLE public.external_student_transcript_term OWNER TO sspadmin;
CREATE TABLE external_term (
    name character varying(80) NOT NULL,
    code character varying(25) NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    report_year integer NOT NULL,
    registration_start_date date,
    registration_end_date date
);
ALTER TABLE public.external_term OWNER TO sspadmin;
CREATE TABLE marital_status (
    name character varying(50) NOT NULL
);
ALTER TABLE public.marital_status OWNER TO sspadmin;
CREATE TABLE ethnicity (
    name character varying(50) NOT NULL
);
ALTER TABLE public.ethnicity OWNER TO sspadmin;
CREATE TABLE student_type (
    code character varying(50) NOT NULL
);
ALTER TABLE public.student_type OWNER TO sspadmin;
CREATE TABLE race (
    code character varying(50) NOT NULL
);

CREATE TABLE sap_status (
    code character varying(50) NOT NULL
);

CREATE TABLE financial_aid_file (
    code character varying(50) NOT NULL
);


CREATE TABLE external_substitutable_course (
    term_code character varying(25),
    program_code  character varying(50),
    source_formatted_course character varying(35) NOT NULL,
    source_course_code  character varying(50) ,
    source_course_title character varying(100) ,
    source_credit_hours  numeric(9,2) ,
    target_formatted_course  character varying(35) NOT NULL,
    target_course_code character varying(50),
    target_course_title  character varying(100),
    target_credit_hours  numeric(9,2)
);

ALTER TABLE public.race OWNER TO sspadmin;
ALTER TABLE public.sap_status OWNER TO sspadmin;
ALTER TABLE public.financial_aid_file OWNER TO sspadmin;
ALTER TABLE ONLY external_course
    ADD CONSTRAINT external_course_code_key UNIQUE (code);
ALTER TABLE ONLY external_course
    ADD CONSTRAINT external_course_pkey PRIMARY KEY (code);
ALTER TABLE ONLY external_course_program
    ADD CONSTRAINT external_course_program_pkey PRIMARY KEY (program_code, course_code);
ALTER TABLE ONLY external_course_program
    ADD CONSTRAINT external_course_program_program_code_course_code_key UNIQUE (program_code, course_code);
ALTER TABLE ONLY external_course_requisite
    ADD CONSTRAINT external_course_requisite_pkey PRIMARY KEY (requiring_course_code, required_course_code, requisite_code);
ALTER TABLE ONLY external_course_requisite
    ADD CONSTRAINT external_course_requisite_requiring_course_code_required_co_key UNIQUE (requiring_course_code, required_course_code, requisite_code);
ALTER TABLE ONLY external_course_tag
    ADD CONSTRAINT external_course_tag_course_code_tag_key UNIQUE (course_code, tag);
ALTER TABLE ONLY external_course_tag
    ADD CONSTRAINT external_course_tag_pkey PRIMARY KEY (course_code, tag);
ALTER TABLE ONLY external_course_term
    ADD CONSTRAINT external_course_term_course_code_term_code_key UNIQUE (course_code, term_code);
ALTER TABLE ONLY external_course_term
    ADD CONSTRAINT external_course_term_pkey PRIMARY KEY (course_code, term_code);
ALTER TABLE ONLY external_department
    ADD CONSTRAINT external_department_code_key UNIQUE (code);
ALTER TABLE ONLY external_department
    ADD CONSTRAINT external_department_pkey PRIMARY KEY (code);
ALTER TABLE ONLY external_division
    ADD CONSTRAINT external_division_code_key UNIQUE (code);
ALTER TABLE ONLY external_division
    ADD CONSTRAINT external_division_pkey PRIMARY KEY (code);
ALTER TABLE ONLY external_person_note
    ADD CONSTRAINT external_person_note_code_key UNIQUE (code);
ALTER TABLE ONLY external_person_note
    ADD CONSTRAINT external_person_note_pkey PRIMARY KEY (code);
ALTER TABLE ONLY external_person
    ADD CONSTRAINT external_person_pkey PRIMARY KEY (school_id);
ALTER TABLE ONLY external_person_planning_status
    ADD CONSTRAINT external_person_planning_status_pkey PRIMARY KEY (school_id);
ALTER TABLE ONLY external_person_planning_status
    ADD CONSTRAINT external_person_planning_status_school_id_key UNIQUE (school_id);
ALTER TABLE ONLY external_person
    ADD CONSTRAINT external_person_school_id_key UNIQUE (school_id);
ALTER TABLE ONLY external_person
    ADD CONSTRAINT external_person_username_key UNIQUE (username);
ALTER TABLE ONLY external_program
    ADD CONSTRAINT external_program_code_key UNIQUE (code);
ALTER TABLE ONLY external_program
    ADD CONSTRAINT external_program_pkey PRIMARY KEY (code);
ALTER TABLE ONLY external_registration_status_by_term
    ADD CONSTRAINT external_registration_status_by_term_pkey PRIMARY KEY (school_id, term_code);
ALTER TABLE ONLY external_registration_status_by_term
    ADD CONSTRAINT external_registration_status_by_term_school_id_term_code_key UNIQUE (school_id, term_code);
ALTER TABLE ONLY external_student_academic_program
    ADD CONSTRAINT external_student_academic_pro_school_id_degree_code_program_key UNIQUE (school_id, degree_code, program_code);
ALTER TABLE ONLY external_student_academic_program
    ADD CONSTRAINT external_student_academic_program_pkey PRIMARY KEY (school_id, degree_code, program_code);
ALTER TABLE ONLY external_student_financial_aid_award_term
    ADD CONSTRAINT external_student_financial_aid_award_term_pkey PRIMARY KEY (school_id, term_code);
ALTER TABLE ONLY external_student_financial_aid_file
    ADD CONSTRAINT external_student_financial_aid_file_pkey PRIMARY KEY (school_id, financial_file_code);
ALTER TABLE ONLY external_student_financial_aid
    ADD CONSTRAINT external_student_financial_aid_pkey PRIMARY KEY (school_id);
ALTER TABLE ONLY external_student_financial_aid
    ADD CONSTRAINT external_student_financial_aid_school_id_key UNIQUE (school_id);
ALTER TABLE ONLY external_student_financial_aid
    ADD CONSTRAINT external_student_financial_aid_school_id_key1 UNIQUE (school_id);
ALTER TABLE ONLY external_student_test
    ADD CONSTRAINT external_student_test_pk PRIMARY KEY (school_id, test_code, sub_test_code, test_date, discriminator);
ALTER TABLE ONLY external_student_transcript_course
    ADD CONSTRAINT external_student_transcript_c_school_id_term_code_formatted_key UNIQUE (school_id, term_code, formatted_course, section_code);
ALTER TABLE ONLY external_student_transcript_course
    ADD CONSTRAINT external_student_transcript_course_pkey PRIMARY KEY (school_id, term_code, formatted_course, section_code);
ALTER TABLE ONLY external_student_transcript
    ADD CONSTRAINT external_student_transcript_pkey PRIMARY KEY (school_id);
ALTER TABLE ONLY external_student_transcript
    ADD CONSTRAINT external_student_transcript_school_id_key UNIQUE (school_id);
ALTER TABLE ONLY external_student_transcript_term
    ADD CONSTRAINT external_student_transcript_term_pkey PRIMARY KEY (school_id, term_code);
ALTER TABLE ONLY external_student_transcript_term
    ADD CONSTRAINT external_student_transcript_term_school_id_term_code_key UNIQUE (school_id, term_code);
ALTER TABLE ONLY external_term
    ADD CONSTRAINT external_term_code_key UNIQUE (code);
ALTER TABLE ONLY external_term
    ADD CONSTRAINT external_term_pkey PRIMARY KEY (code);
 ALTER TABLE ONLY marital_status
    ADD CONSTRAINT marital_status_name_key UNIQUE (name);
ALTER TABLE ONLY marital_status
    ADD CONSTRAINT marital_status_name_pkey PRIMARY KEY (name);


CREATE INDEX idx_external_faculty_course_business_key ON external_faculty_course USING btree (faculty_school_id, formatted_course, term_code);



CREATE INDEX idx_external_faculty_course_roster_business_key ON external_faculty_course_roster USING btree (faculty_school_id, formatted_course, term_code, school_id);




CREATE INDEX idx_external_person_school_id ON external_person USING btree (school_id);




CREATE INDEX idx_external_person_username ON external_person USING btree (username);




CREATE INDEX idx_external_registration_status_by_term_business_key ON external_registration_status_by_term USING btree (school_id, term_code, registered_course_count);
   
    
    
 ALTER TABLE ONLY ethnicity
    ADD CONSTRAINT ethnicity_name_key UNIQUE (name);
ALTER TABLE ONLY ethnicity
    ADD CONSTRAINT ethnicity_name_pkey PRIMARY KEY (name);
    
    
    
 ALTER TABLE ONLY student_type
    ADD CONSTRAINT student_type_code_key UNIQUE (code);
ALTER TABLE ONLY student_type
    ADD CONSTRAINT student_type_code_pkey PRIMARY KEY (code);

 ALTER TABLE ONLY race
    ADD CONSTRAINT race_code_key UNIQUE (code);
ALTER TABLE ONLY race
    ADD CONSTRAINT race_code_pkey PRIMARY KEY (code);
 ALTER TABLE ONLY sap_status
    ADD CONSTRAINT sap_status_key UNIQUE (code);
ALTER TABLE ONLY sap_status
    ADD CONSTRAINT sap_status_pkey PRIMARY KEY (code);

ALTER TABLE ONLY financial_aid_file
    ADD CONSTRAINT financial_aid_file_key UNIQUE (code);
ALTER TABLE ONLY financial_aid_file
    ADD CONSTRAINT financial_aid_file_pkey PRIMARY KEY (code);

REVOKE ALL ON TABLE external_course FROM PUBLIC;
REVOKE ALL ON TABLE external_course FROM sspadmin;
GRANT ALL ON TABLE external_course TO sspadmin;
GRANT ALL ON TABLE external_course TO ssp;
REVOKE ALL ON TABLE external_course_program FROM PUBLIC;
REVOKE ALL ON TABLE external_course_program FROM sspadmin;
GRANT ALL ON TABLE external_course_program TO sspadmin;
GRANT ALL ON TABLE external_course_program TO ssp;
REVOKE ALL ON TABLE external_course_requisite FROM PUBLIC;
REVOKE ALL ON TABLE external_course_requisite FROM sspadmin;
GRANT ALL ON TABLE external_course_requisite TO sspadmin;
GRANT ALL ON TABLE external_course_requisite TO ssp;
REVOKE ALL ON TABLE external_course_tag FROM PUBLIC;
REVOKE ALL ON TABLE external_course_tag FROM sspadmin;
GRANT ALL ON TABLE external_course_tag TO sspadmin;
GRANT ALL ON TABLE external_course_tag TO ssp;
REVOKE ALL ON TABLE external_course_term FROM PUBLIC;
REVOKE ALL ON TABLE external_course_term FROM sspadmin;
GRANT ALL ON TABLE external_course_term TO sspadmin;
GRANT ALL ON TABLE external_course_term TO ssp;
REVOKE ALL ON TABLE external_department FROM PUBLIC;
REVOKE ALL ON TABLE external_department FROM sspadmin;
GRANT ALL ON TABLE external_department TO sspadmin;
GRANT ALL ON TABLE external_department TO ssp;
REVOKE ALL ON TABLE external_division FROM PUBLIC;
REVOKE ALL ON TABLE external_division FROM sspadmin;
GRANT ALL ON TABLE external_division TO sspadmin;
GRANT ALL ON TABLE external_division TO ssp;
REVOKE ALL ON TABLE external_faculty_course FROM PUBLIC;
REVOKE ALL ON TABLE external_faculty_course FROM sspadmin;
GRANT ALL ON TABLE external_faculty_course TO sspadmin;
GRANT ALL ON TABLE external_faculty_course TO ssp;
REVOKE ALL ON TABLE external_faculty_course_roster FROM PUBLIC;
REVOKE ALL ON TABLE external_faculty_course_roster FROM sspadmin;
GRANT ALL ON TABLE external_faculty_course_roster TO sspadmin;
GRANT ALL ON TABLE external_faculty_course_roster TO ssp;
REVOKE ALL ON TABLE external_person FROM PUBLIC;
REVOKE ALL ON TABLE external_person FROM sspadmin;
GRANT ALL ON TABLE external_person TO sspadmin;
GRANT ALL ON TABLE external_person TO ssp;
REVOKE ALL ON TABLE external_person_note FROM PUBLIC;
REVOKE ALL ON TABLE external_person_note FROM sspadmin;
GRANT ALL ON TABLE external_person_note TO sspadmin;
GRANT ALL ON TABLE external_person_note TO ssp;
REVOKE ALL ON TABLE external_person_planning_status FROM PUBLIC;
REVOKE ALL ON TABLE external_person_planning_status FROM sspadmin;
GRANT ALL ON TABLE external_person_planning_status TO sspadmin;
GRANT ALL ON TABLE external_person_planning_status TO ssp;
REVOKE ALL ON TABLE external_program FROM PUBLIC;
REVOKE ALL ON TABLE external_program FROM sspadmin;
GRANT ALL ON TABLE external_program TO sspadmin;
GRANT ALL ON TABLE external_program TO ssp;
REVOKE ALL ON TABLE external_registration_status_by_term FROM PUBLIC;
REVOKE ALL ON TABLE external_registration_status_by_term FROM sspadmin;
GRANT ALL ON TABLE external_registration_status_by_term TO sspadmin;
GRANT ALL ON TABLE external_registration_status_by_term TO ssp;
REVOKE ALL ON TABLE external_student_academic_program FROM PUBLIC;
REVOKE ALL ON TABLE external_student_academic_program FROM sspadmin;
GRANT ALL ON TABLE external_student_academic_program TO sspadmin;
GRANT ALL ON TABLE external_student_academic_program TO ssp;
REVOKE ALL ON TABLE external_student_financial_aid FROM PUBLIC;
REVOKE ALL ON TABLE external_student_financial_aid FROM sspadmin;
GRANT ALL ON TABLE external_student_financial_aid TO sspadmin;
GRANT ALL ON TABLE external_student_financial_aid TO ssp;
REVOKE ALL ON TABLE external_student_financial_aid_award_term FROM PUBLIC;
REVOKE ALL ON TABLE external_student_financial_aid_award_term FROM sspadmin;
GRANT ALL ON TABLE external_student_financial_aid_award_term TO sspadmin;
GRANT ALL ON TABLE external_student_financial_aid_award_term TO ssp;
REVOKE ALL ON TABLE external_student_financial_aid_file FROM PUBLIC;
REVOKE ALL ON TABLE external_student_financial_aid_file FROM sspadmin;
GRANT ALL ON TABLE external_student_financial_aid_file TO sspadmin;
GRANT ALL ON TABLE external_student_financial_aid_file TO ssp;
REVOKE ALL ON TABLE external_student_test FROM PUBLIC;
REVOKE ALL ON TABLE external_student_test FROM sspadmin;
GRANT ALL ON TABLE external_student_test TO sspadmin;
GRANT ALL ON TABLE external_student_test TO ssp;
REVOKE ALL ON TABLE external_student_transcript FROM PUBLIC;
REVOKE ALL ON TABLE external_student_transcript FROM sspadmin;
GRANT ALL ON TABLE external_student_transcript TO sspadmin;
GRANT ALL ON TABLE external_student_transcript TO ssp;
REVOKE ALL ON TABLE external_student_transcript_course FROM PUBLIC;
REVOKE ALL ON TABLE external_student_transcript_course FROM sspadmin;
GRANT ALL ON TABLE external_student_transcript_course TO sspadmin;
GRANT ALL ON TABLE external_student_transcript_course TO ssp;
REVOKE ALL ON TABLE external_student_transcript_term FROM PUBLIC;
REVOKE ALL ON TABLE external_student_transcript_term FROM sspadmin;
GRANT ALL ON TABLE external_student_transcript_term TO sspadmin;
GRANT ALL ON TABLE external_student_transcript_term TO ssp;
REVOKE ALL ON TABLE external_term FROM PUBLIC;
REVOKE ALL ON TABLE external_term FROM sspadmin;
GRANT ALL ON TABLE external_term TO sspadmin;
GRANT ALL ON TABLE external_term TO ssp;
REVOKE ALL ON TABLE marital_status FROM PUBLIC;
REVOKE ALL ON TABLE marital_status FROM sspadmin;
GRANT ALL ON TABLE marital_status TO sspadmin;
GRANT ALL ON TABLE marital_status TO ssp;
REVOKE ALL ON TABLE ethnicity FROM PUBLIC;
REVOKE ALL ON TABLE ethnicity FROM sspadmin;
GRANT ALL ON TABLE ethnicity TO sspadmin;
GRANT ALL ON TABLE ethnicity TO ssp;
REVOKE ALL ON TABLE student_type FROM PUBLIC;
REVOKE ALL ON TABLE student_type FROM sspadmin;
GRANT ALL ON TABLE student_type TO sspadmin;
GRANT ALL ON TABLE student_type TO ssp;
REVOKE ALL ON TABLE race FROM PUBLIC;
REVOKE ALL ON TABLE race FROM sspadmin;
GRANT ALL ON TABLE race TO sspadmin;
GRANT ALL ON TABLE race TO ssp;
REVOKE ALL ON TABLE sap_status FROM PUBLIC;
REVOKE ALL ON TABLE sap_status FROM sspadmin;
GRANT ALL ON TABLE sap_status TO sspadmin;
GRANT ALL ON TABLE sap_status TO ssp;
REVOKE ALL ON TABLE financial_aid_file FROM PUBLIC;
REVOKE ALL ON TABLE financial_aid_file FROM sspadmin;
GRANT ALL ON TABLE financial_aid_file TO sspadmin;
GRANT ALL ON TABLE financial_aid_file TO ssp;
REVOKE ALL ON TABLE external_substitutable_course FROM PUBLIC;
REVOKE ALL ON TABLE external_substitutable_course FROM sspadmin;
GRANT ALL ON TABLE external_substitutable_course TO sspadmin;
GRANT ALL ON TABLE external_substitutable_course TO ssp;
