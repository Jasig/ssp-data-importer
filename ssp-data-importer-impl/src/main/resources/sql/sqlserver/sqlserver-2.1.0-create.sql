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



CREATE TABLE dbo.BATCH_JOB_INSTANCE  (
	JOB_INSTANCE_ID BIGINT  NOT NULL PRIMARY KEY ,
	VERSION BIGINT ,
	JOB_NAME VARCHAR(100) NOT NULL,
	JOB_KEY VARCHAR(32) NOT NULL,
	constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
); 



CREATE TABLE dbo.BATCH_JOB_EXECUTION  (
	JOB_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
	VERSION BIGINT  ,
	JOB_INSTANCE_ID BIGINT NOT NULL,
	CREATE_TIME DATETIME NOT NULL,
	START_TIME DATETIME DEFAULT NULL ,
	END_TIME DATETIME DEFAULT NULL ,
	STATUS VARCHAR(10) ,
	EXIT_CODE VARCHAR(100) ,
	EXIT_MESSAGE VARCHAR(2500) ,
	LAST_UPDATED DATETIME,
	constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
	references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
);

CREATE TABLE dbo.BATCH_JOB_EXECUTION_PARAMS  (
	JOB_EXECUTION_ID BIGINT NOT NULL ,
	TYPE_CD VARCHAR(6) NOT NULL ,
	KEY_NAME VARCHAR(100) NOT NULL ,
	STRING_VAL VARCHAR(250) ,
	DATE_VAL DATETIME DEFAULT NULL ,
	LONG_VAL BIGINT ,
	DOUBLE_VAL DOUBLE PRECISION ,
	IDENTIFYING CHAR(1) NOT NULL ,
	constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

CREATE TABLE dbo.BATCH_STEP_EXECUTION  (
	STEP_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
	VERSION BIGINT NOT NULL,
	STEP_NAME VARCHAR(100) NOT NULL,
	JOB_EXECUTION_ID BIGINT NOT NULL,
	START_TIME DATETIME NOT NULL ,
	END_TIME DATETIME DEFAULT NULL ,
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
	LAST_UPDATED DATETIME,
	constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

CREATE TABLE dbo.BATCH_STEP_EXECUTION_CONTEXT  (
	STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
	SHORT_CONTEXT VARCHAR(2500) NOT NULL,
	SERIALIZED_CONTEXT TEXT ,
	constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
	references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
);

CREATE TABLE dbo.BATCH_JOB_EXECUTION_CONTEXT  (
	JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
	SHORT_CONTEXT VARCHAR(2500) NOT NULL,
	SERIALIZED_CONTEXT TEXT ,
	constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

CREATE TABLE dbo.BATCH_STEP_EXECUTION_SEQ (ID BIGINT IDENTITY);
CREATE TABLE dbo.BATCH_JOB_EXECUTION_SEQ (ID BIGINT IDENTITY);
CREATE TABLE dbo.BATCH_JOB_SEQ (ID BIGINT IDENTITY);

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

SET ANSI_PADDING ON
;

CREATE TABLE [dbo].[stg_external_course](
	[batch_id] [bigint],
	[code] [nvarchar](50) NOT NULL,
	[formatted_course] [nvarchar](35) NULL,
	[subject_abbreviation] [nvarchar](10) NULL,
	[title] [nvarchar](100) NULL,
	[description] [nvarchar](2500) NULL,
	[max_credit_hours] [decimal](9, 2) NULL,
	[min_credit_hours] [decimal](9, 2) NULL,
	[number] [nvarchar](15) NULL,
	[is_dev] [char](1) NOT NULL,
	[academic_link] [nvarchar](2000) NULL,
	[department_code] [nvarchar](50) NULL,
	[division_code] [nvarchar](50) NULL,
	[master_syllabus_link] [nvarchar](2000) NULL,
PRIMARY KEY CLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_PADDING OFF
;

ALTER TABLE [dbo].[stg_external_course] ADD  CONSTRAINT [DF_stg_external_course_number]  DEFAULT ('0') FOR [number]
;

ALTER TABLE [dbo].[stg_external_course] ADD  CONSTRAINT [DF_stg_external_course_is_dev_new]  DEFAULT ('N') FOR [is_dev]
;


CREATE TABLE [dbo].[stg_external_course_program](
	[batch_id] [bigint],
	[course_code] [nvarchar](50) NOT NULL,
	[program_code] [nvarchar](50) NOT NULL,
	[program_name] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[program_code] ASC,
	[course_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[program_code] ASC,
	[course_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_course_requisite](
	[batch_id] [bigint],
	[requiring_course_code] [nvarchar](50) NOT NULL,
	[required_course_code] [nvarchar](50) NOT NULL,
	[required_formatted_course] [nvarchar](35) NOT NULL,
	[requisite_code] [nvarchar](8) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[requiring_course_code] ASC,
	[required_course_code] ASC,
	[requisite_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[requiring_course_code] ASC,
	[required_course_code] ASC,
	[requisite_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_course_tag](
	[batch_id] [bigint],
	[course_code] [nvarchar](50) NOT NULL,
	[tag] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[course_code] ASC,
	[tag] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[course_code] ASC,
	[tag] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_course_term](
	[batch_id] [bigint],
	[course_code] [nvarchar](50) NOT NULL,
	[term_code] [nvarchar](25) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[course_code] ASC,
	[term_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[course_code] ASC,
	[term_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_department](
	[batch_id] [bigint],
	[code] [nvarchar](50) NOT NULL,
	[name] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_division](
	[batch_id] [bigint],
	[code] [nvarchar](50) NOT NULL,
	[name] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_faculty_course](
	[batch_id] [bigint],
	[faculty_school_id] [nvarchar](50) NOT NULL,
	[term_code] [nvarchar](25) NOT NULL,
	[formatted_course] [nvarchar](35) NOT NULL,
	[title] [nvarchar](100) NOT NULL,
	[section_code] [nvarchar](128) NOT NULL,
	[section_number] [nvarchar](10) NULL,
	PRIMARY KEY CLUSTERED 
(
	[faculty_school_id] ASC,
	[section_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[faculty_school_id] ASC,
	[section_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_faculty_course_roster](
	[batch_id] [bigint],
	[faculty_school_id] [nvarchar](50) NOT NULL,
	[school_id] [nvarchar](50) NOT NULL,
	[first_name] [nvarchar](50) NOT NULL,
	[middle_name] [nvarchar](50) NULL,
	[last_name] [nvarchar](50) NOT NULL,
	[primary_email_address] [nvarchar](100) NULL,
	[term_code] [nvarchar](25) NOT NULL,
	[formatted_course] [nvarchar](35) NOT NULL,
	[status_code] [nvarchar](2) NULL,
	[section_code] [nvarchar](128) NOT NULL,
	[section_number] [nvarchar](10) NULL,
	PRIMARY KEY CLUSTERED 
(
	[school_id] ASC,
	[section_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[school_id] ASC,
	[section_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

SET ANSI_PADDING ON
;

CREATE TABLE [dbo].[stg_external_person](
	[batch_id] [bigint],
	[school_id] [nvarchar](50) NOT NULL,
	[username] [nvarchar](50) NOT NULL,
	[first_name] [nvarchar](50) NOT NULL,
	[middle_name] [nvarchar](50) NULL,
	[last_name] [nvarchar](50) NOT NULL,
	[birth_date] [date] NULL,
	[primary_email_address] [nvarchar](100) NULL,
	[address_line_1] [nvarchar](50) NULL,
	[address_line_2] [nvarchar](50) NULL,
	[city] [nvarchar](50) NULL,
	[state] [char](2) NULL,
	[zip_code] [nvarchar](10) NULL,
	[home_phone] [nvarchar](25) NULL,
	[work_phone] [nvarchar](25) NULL,
	[office_location] [nvarchar](50) NULL,
	[office_hours] [nvarchar](50) NULL,
	[department_name] [nvarchar](100) NULL,
	[actual_start_term] [nvarchar](20) NULL,
	[actual_start_year] [int] NULL,
	[marital_status] [nvarchar](80) NULL,
	[ethnicity] [nvarchar](80) NULL,
	[gender] [char](1) NULL,
	[is_local] [char](1) NULL,
	[balance_owed] [decimal](9, 2) NULL,
	[coach_school_id] [nvarchar](50) NULL,
	[cell_phone] [nvarchar](25) NULL,
	[photo_url] [nvarchar](250) NULL,
	[residency_county] [nvarchar](50) NULL,
	[f1_status] [char](1) NULL,
	[non_local_address] [char](1) NOT NULL,
	[student_type_code] [nvarchar](10) NULL,
	[race_code] [nvarchar](10) NULL,
PRIMARY KEY CLUSTERED 
(
	[school_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[school_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_PADDING OFF
;

ALTER TABLE [dbo].[stg_external_person] ADD  CONSTRAINT [DF_stg_external_person_non_local_address_new]  DEFAULT ('N') FOR [non_local_address]
;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_person_note](
	[batch_id] [bigint],
	[code] [nvarchar](50) NOT NULL,
	[school_id] [nvarchar](50) NOT NULL,
	[note_type] [nvarchar](35) NOT NULL,
	[author] [nvarchar](80) NOT NULL,
	[department] [nvarchar](80) NULL,
	[date_note_taken] [date] NOT NULL,
	[note] [nvarchar](max) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_person_planning_status](
	[batch_id] [bigint],
	[school_id] [nvarchar](50) NOT NULL,
	[status] [nvarchar](8) NOT NULL,
	[status_reason] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[school_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[school_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_program](
	[batch_id] [bigint],
	[code] [nvarchar](50) NOT NULL,
	[name] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_registration_status_by_term](
	[batch_id] [bigint],
	[school_id] [nvarchar](50) NOT NULL,
	[term_code] [nvarchar](25) NOT NULL,
	[registered_course_count] [int] NOT NULL,
	[tuition_paid] [nchar](1) NULL,
PRIMARY KEY CLUSTERED 
(
	[school_id] ASC,
	[term_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[school_id] ASC,
	[term_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_student_academic_program](
	[batch_id] [bigint],
	[school_id] [nvarchar](50) NOT NULL,
	[degree_code] [nvarchar](10) NOT NULL,
	[degree_name] [nvarchar](100) NOT NULL,
	[program_code] [nvarchar](50) NOT NULL,
	[program_name] [nvarchar](100) NOT NULL,
	[intended_program_at_admit] [nvarchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[school_id] ASC,
	[degree_code] ASC,
	[program_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[school_id] ASC,
	[degree_code] ASC,
	[program_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

SET ANSI_PADDING ON
;

CREATE TABLE [dbo].[stg_external_student_financial_aid](
	[batch_id] [bigint],
	[school_id] [nvarchar](50) NOT NULL,
	[financial_aid_gpa] [decimal](9, 2) NOT NULL,
	[gpa_20_b_hrs_needed] [decimal](9, 2) NULL,
	[gpa_20_a_hrs_needed] [decimal](9, 2) NULL,
	[needed_for_67ptc_completion] [decimal](9, 2) NULL,
	[current_year_financial_aid_award] [char](1) NULL,
	[sap_status] [char](1) NULL,
	[fafsa_date] [datetime] NULL,
	[financial_aid_remaining] [decimal](9, 2) NULL,
	[original_loan_amount] [decimal](9, 2) NULL,
	[remaining_loan_amount] [decimal](9, 2) NULL,
PRIMARY KEY CLUSTERED 
(
	[school_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[school_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[school_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_PADDING OFF
;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_student_test](
	[batch_id] [bigint],
	[school_id] [nvarchar](50) NOT NULL,
	[test_name] [nvarchar](50) NOT NULL,
	[test_code] [nvarchar](25) NOT NULL,
	[sub_test_code] [nvarchar](25) NOT NULL,
	[sub_test_name] [nvarchar](50) NULL,
	[test_date] [date] NOT NULL,
	[score] [decimal](9, 2) NOT NULL,
	[status] [nvarchar](25) NOT NULL,
	[discriminator] [nvarchar](1) NOT NULL,
 CONSTRAINT [stg_external_student_test_pk] PRIMARY KEY CLUSTERED 
(
	[school_id] ASC,
	[test_code] ASC,
	[sub_test_code] ASC,
	[test_date] ASC,
	[discriminator] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

ALTER TABLE [dbo].[stg_external_student_test] ADD  CONSTRAINT [DF_stg_external_student_test_discriminator]  DEFAULT ('1') FOR [discriminator]
;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_student_transcript](
	[batch_id] [bigint],
	[school_id] [nvarchar](50) NOT NULL,
	[credit_hours_for_gpa] [decimal](9, 2) NULL,
	[credit_hours_earned] [decimal](9, 2) NULL,
	[credit_hours_attempted] [decimal](9, 2) NULL,
	[total_quality_points] [decimal](9, 2) NULL,
	[grade_point_average] [decimal](9, 2) NOT NULL,
	[academic_standing] [nvarchar](50) NULL,
	[credit_hours_not_completed] [decimal](9, 2) NULL,
	[credit_completion_rate] [decimal](9, 2) NULL,
	[gpa_trend_indicator] [nvarchar](25) NULL,
	[current_restrictions] [nvarchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[school_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[school_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

SET ANSI_PADDING ON
;

CREATE TABLE [dbo].[stg_external_student_transcript_course](
	[batch_id] [bigint],
	[school_id] [nvarchar](50) NOT NULL,
	[subject_abbreviation] [nvarchar](10) NOT NULL,
	[number] [nvarchar](15) NOT NULL,
	[formatted_course] [nvarchar](35) NOT NULL,
	[section_number] [nvarchar](10) NULL,
	[title] [nvarchar](100) NULL,
	[description] [nvarchar](2500) NULL,
	[grade] [nvarchar](10) NULL,
	[credit_earned] [decimal](9, 2) NULL,
	[term_code] [nvarchar](25) NOT NULL,
	[credit_type] [nvarchar](25) NULL,
	[first_name] [nvarchar](50) NOT NULL,
	[middle_name] [nvarchar](50) NULL,
	[last_name] [nvarchar](50) NOT NULL,
	[audited] [char](1) NULL,
	[status_code] [nvarchar](2) NULL,
	[section_code] [nvarchar](50) NOT NULL,
	[faculty_school_id] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[school_id] ASC,
	[term_code] ASC,
	[formatted_course] ASC,
	[section_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[school_id] ASC,
	[term_code] ASC,
	[formatted_course] ASC,
	[section_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_PADDING OFF
;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_student_transcript_term](
	[batch_id] [bigint],
	[school_id] [nvarchar](50) NOT NULL,
	[credit_hours_for_gpa] [decimal](9, 2) NULL,
	[credit_hours_earned] [decimal](9, 2) NULL,
	[credit_hours_attempted] [decimal](9, 2) NULL,
	[credit_hours_not_completed] [decimal](9, 2) NULL,
	[credit_completion_rate] [decimal](9, 2) NULL,
	[total_quality_points] [decimal](9, 2) NULL,
	[grade_point_average] [decimal](9, 2) NOT NULL,
	[term_code] [nvarchar](25) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[school_id] ASC,
	[term_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[school_id] ASC,
	[term_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;

SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;

CREATE TABLE [dbo].[stg_external_term](
	[batch_id] [bigint],
	[name] [nvarchar](80) NOT NULL,
	[code] [nvarchar](25) NOT NULL,
	[start_date] [date] NULL,
	[end_date] [date] NULL,
	[report_year] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

;







