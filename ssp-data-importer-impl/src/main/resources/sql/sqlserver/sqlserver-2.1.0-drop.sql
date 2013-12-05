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

DROP TABLE  dbo.BATCH_STEP_EXECUTION_CONTEXT ;
DROP TABLE  dbo.BATCH_JOB_EXECUTION_CONTEXT ;
DROP TABLE  dbo.BATCH_STEP_EXECUTION ;
DROP TABLE  dbo.BATCH_JOB_EXECUTION_PARAMS ;
DROP TABLE  dbo.BATCH_JOB_EXECUTION ;
DROP TABLE  dbo.BATCH_JOB_INSTANCE ;

DROP TABLE  dbo.BATCH_STEP_EXECUTION_SEQ ;
DROP TABLE  dbo.BATCH_JOB_EXECUTION_SEQ ;
DROP TABLE  dbo.BATCH_JOB_SEQ ;


--Staging tables

DROP TABLE  dbo.stg_external_course ;
DROP TABLE  dbo.stg_external_course_program ;
DROP TABLE  dbo.stg_external_course_requisite ;
DROP TABLE  dbo.stg_external_course_tag ;
DROP TABLE  dbo.stg_external_course_term ;
DROP TABLE  dbo.stg_external_department ;
DROP TABLE  dbo.stg_external_division ;
DROP TABLE  dbo.stg_external_faculty_course ;
DROP TABLE  dbo.stg_external_faculty_course_roster ;
DROP TABLE  dbo.stg_external_person ;
DROP TABLE  dbo.stg_external_person_note ;
DROP TABLE  dbo.stg_external_person_planning_status ;
DROP TABLE  dbo.stg_external_program ;
DROP TABLE  dbo.stg_external_registration_status_by_term ;
DROP TABLE  dbo.stg_external_student_academic_program ;
DROP TABLE  dbo.stg_external_student_financial_aid ;
DROP TABLE  dbo.stg_external_student_test ;
DROP TABLE  dbo.stg_external_student_transcript ;
DROP TABLE  dbo.stg_external_student_transcript_course ;
DROP TABLE  dbo.stg_external_student_transcript_term ;
DROP TABLE  dbo.stg_external_term ;








