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

--Spring batch framework tables

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[BATCH_STEP_EXECUTION_CONTEXT]') AND type in (N'U'))
DROP TABLE  dbo.BATCH_STEP_EXECUTION_CONTEXT ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[BATCH_JOB_EXECUTION_CONTEXT]') AND type in (N'U'))
DROP TABLE  dbo.BATCH_JOB_EXECUTION_CONTEXT ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[BATCH_STEP_EXECUTION]') AND type in (N'U'))
DROP TABLE  dbo.BATCH_STEP_EXECUTION ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[BATCH_JOB_EXECUTION_PARAMS]') AND type in (N'U'))
DROP TABLE  dbo.BATCH_JOB_EXECUTION_PARAMS ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[BATCH_JOB_EXECUTION]') AND type in (N'U'))
DROP TABLE  dbo.BATCH_JOB_EXECUTION ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[BATCH_JOB_INSTANCE]') AND type in (N'U'))
DROP TABLE  dbo.BATCH_JOB_INSTANCE ;

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[BATCH_STEP_EXECUTION_SEQ]') AND type in (N'U'))
DROP TABLE  dbo.BATCH_STEP_EXECUTION_SEQ ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[BATCH_JOB_EXECUTION_SEQ]') AND type in (N'U'))
DROP TABLE  dbo.BATCH_JOB_EXECUTION_SEQ ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[BATCH_JOB_SEQ]') AND type in (N'U'))
DROP TABLE  dbo.BATCH_JOB_SEQ ;


--Staging tables

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_course]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_course ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_course_program]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_course_program ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_course_requisite]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_course_requisite ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_course_tag]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_course_tag ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_course_term]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_course_term ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_department]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_department ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_division]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_division ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_faculty_course]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_faculty_course ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_faculty_course_roster]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_faculty_course_roster ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_person]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_person ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_person_note]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_person_note ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_person_planning_status]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_person_planning_status ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_program]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_program ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_registration_status_by_term]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_registration_status_by_term ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_student_academic_program]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_student_academic_program ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_student_financial_aid]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_student_financial_aid ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_student_test]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_student_test ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_student_transcript]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_student_transcript ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_student_transcript_course]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_student_transcript_course ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_student_transcript_term]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_student_transcript_term ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_term]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_term ;

/* ADDED TABLES 2.3 **/
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_student_financial_aid_award_term]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_student_financial_aid_award_term ;
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_student_financial_aid_file]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_student_financial_aid_file ;

/* ADDED TABLES 2.4 **/
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_substitutable_course]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_substitutable_course ;

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stg_external_catalog_year]') AND type in (N'U'))
DROP TABLE  dbo.stg_external_catalog_year ;

