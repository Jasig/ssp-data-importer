<!--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

-->
ssp-data-importer
==================

`ssp-data-importer` is a tool for importing csv data for the external tables into the SSP database.
This program inserts data from `csv` files into a database. `csv` files have some latitude in parsing including whitespace, separator, and quote.
Currently, the program supports only updates and inserts. It is possible to upload partial data updating specific columns.
When used as a method to update already existing data, the only requirement is that the columns containing key values be supplied.

High-Level Workflow
===================

The `ssp-data-importer` is expected to be used as part of a cronjob or other periodic method for running the program, e.g. Windows Task Scheduler.
The job should be set up to run with a period that is appropriate for how often the external tables are to be updated.
It is important that the upload to the input folder be timed to the running of the cronjob.

1. A folder that will contain the `.csv` files (input folder) to import is monitored at runtime.
1. If the folder contains `.csv` files and the files have had a sufficient soak time since modification, the files are transfered to a folder (process folder).
1. Files in the process folder are validated. Validation is on a column basis. All errors in a given row are documented for the final job-level report. Validation is based on database metadata constraints.
1. Validated rows are written to a second folder (upsert folder).
1. Files in the upsert folder are inserted to staging tables in the database.
1. Additional validation steps are taken on the complete data set evaluating for inconsistencies and any potential duplications.
1. The validated staging tables are then used to update/insert (upsert) data into the corresponding `external_*` tables.
1. A report is generated and emailed giving pertinent information including any validation errors, total lines per table processed etc.
1. Finally, staging tables are truncated, processing and upset folders are removed and the processed files are archived.

Pre-requisites
==============

    At a minimum you'll need a Java JDK install. This process can vary widely from
    platform to platform, so you're on you're own for that one. This program requires JDK 1.6+.
    You will need a postgres 9.1+ or SQL Server 2008+ database
    To build you will need a maven distribution.

BUILDING
========
    ssp-data-importer is a maven project. It can be built and tested using the standard mvn commands.
    To build for distribution, an assembly/descriptor.xml is supplied, as well as a bat and sh script for running the
    importer.

Running the Program
===================

    It is recommended that the supplied scripts: runJob.bat (Windows) or runJob.sh (Unix) be used to run ssp-csv-importer.
    arguments that need to be supplied:
         argument for database profile -Dspring.profiles.active=profile: postgres|sqlserver
         argument for location of override properties file -Dssp.importer.configdir=/location/of/override/properties/file/folder/

    to run hourly using crontab:
    in command line type:
    ```
    # crontab -e to edit
    then add the following line
    0 0-23 * * * /root/scripts/sh/runJob.sh -Dspring.profiles.active=postgres -Dssp.importer.configdir=/ssp-properties/
    ```


PROPERTIES FILE DESCRIPTION
===========================
    There are a number of properties that are required for the program to run properly. All * values must be set as defaults
    are not supplied.

    #FOLDER LOCATIONS NOTE: file: must be used for location identification.
    *batch.tables.input.folder=file:/location/of/input/folder/          full path to folder that will contain initial csv files
    *batch.tables.process.folder=file:/location/of/process/folder/	    full path to folder where csv files will be processed
    *batch.tables.upsert.folder=file:/location/of/upsert/folder         full path to folder where csv files will be upserted
    *batch.tables.archive.folder=file:/location/of/archive              full path to archive folder

    #FILE LOCATIONS
    batch.tables.input.files=${batch.tables.input.folder}*.csv          full path to uploaded files
    batch.tables.process.files=${batch.tables.process.folder}*.csv      full path to files to process
    batch.tables.upsert.files=${batch.tables.upsert.folder}*.csv        full path to files to upsert

    #INITIALIZATION
    batch.tables.lagTimeBeforeStartInMinutes=10                         set minutes file unmodified before beginning processing    (default: 10)

    #ARCHIVING
    batch.tables.retain.input.archive= true                            turn archiving on default is true
    batch.tables.archive=UNIQUE                                        what files to archive, ALL, NONE, UNIQUE  default: UNIQUE

    #TOLERANCE
    batch.rawitem.skip.limit=10                                         number of lines generating validation errors to allow during read/write
    batch.rawitem.commit.interval=100                                   size of the batch

    batch.upsertitem.skip.limit=10                                      number of lines generating validation errors to allow during upsert
    batch.upsertitem.commit.interval=100                                size of batch.  Larger batch sizes will reduce processing time make errors less specific

    #TESTING
    batch.table.input.duplicate=false
    exists.only.for.testing.1=default.value.1
    exists.only.for.testing.2=default.value.2

    #DATABASE
    db_name=ssp                                                        Default is ssp
    batch.jdbc.url=jdbc:postgresql://127.0.0.1:5432/${db_name}         The full URL to the source database (default:jdbc:postgresql://localhost:5432/ssp)
    batch.jdbc.driver=org.postgresql.Driver                            The driver to be used example: (default:org.postgresql.Driver) or net.sourceforge.jtds.jdbc.Driver
    batch.jdbc.user=sspadmin									       The username for the source database  (default:sspadmin)
    batch.jdbc.password=sspadmin                                       The password for the source database. (default:sspadmin)

    #REPORTING
    batch.title=ssp_import

    #EMAIL NOTIFICATION  NOTE: ** indicates required if batch.sentMail is true)
    batch.sendEmail=true                                                Activate email (default: true)
    **batch.smtp.host=localhost					     					host name (default: localhost)
    **batch.smtp.port=25                                                 port  (default: 25)
    **batch.smtp.protocol=smtp                                           protocol to be used (default: smtp)
    **batch.smtp.username=sysadmin                                       username (default: sysadmin)
    **batch.smtp.password=password                                       password (default: password)

    #RECIPIENTS - COMMA SEPERATED                                       address as string.  Address must follow RFC822 syntax. (default: "SSP DATA IMPORTER"<sysadmin@localhost>)
    **batch.email.recipients="SSP DATA IMPORTER"<sysadmin@localhost>
    **batch.email.replyTo="SSP DATA IMPORTER"<sysadmin@localhost>

CSV FILE FORMAT
===============

1. Name of csv file MUST correspond to the name of the external_table that is to be imported. example: external_person.csv
2. format supported is comma delimite.  Quote character is " and the escape character is also "

2. Each file MUST have an initial line containing the column names in the format of the external table. example
   for external_faculty_course first line would contain:
   faculty_school_id,term_code,formatted_course,title,section_code,section_number

3. Partial data updates are supported. However, in addition to the data being updated the key values for the specific table MUST be supplied.

   For SSP version 2.0 keys for each external table:
   <table>
   <tbody >
   <tr><th>TABLE</th><th>KEY</th></tr>
    <tr>
        <td>external_course</td><td>code</td>
    </tr>
    <tr>
        <td>external_course_program</td><td>program_code, course_code</td>
    </tr>
    <tr>
        <td>external_course_requisite</td><td>requiring_course_code, required_course_code, requisite_code</td>
    </tr>
    <tr>
        <td>external_course_tag</td><td>course_code, tag</td>
    </tr>
    <tr>
        <td>external_course_term</td>
        <td>course_code, term_code</td>
    </tr>
    <tr>
        <td>external_department</td>
        <td>code</td>
    </tr>
    <tr>
        <td>external_division</td>
        <td>code</td>
    </tr>
    <tr>
        <td>external_faculty_course</td>
        <td>(NONE) recommended faculty_school_id,term_code,formatted_course,title</td>
    </tr>
    <tr>
        <td>external_faculty_course_roster</td>
        <td>(NONE) recommended faculty_school_id,school_id,term_code,formatted_course,title</td>
    </tr>
    <tr>
        <td>external_person</td>
        <td>school_id</td>
    </tr>
    <tr>
        <td>external_person_note</td>
        <td>code</td>
    </tr>
    <tr>
        <td>external_person_planning_status</td>
        <td>school_id</td>
    </tr>
    <tr>
        <td>external_program</td>
        <td>code</td>
    </tr>
    <tr>
        <td>external_registration_status_by_term</td>
        <td>school_id,term_code</td>
    </tr>
    <tr>
        <td>external_student_academic_program</td>
        <td>school_id, degree_code, program_code</td>
    </tr>
    <tr>
        <td>external_student_financial_aid</td>
        <td>school_id</td>
    </tr>
    <tr>
        <td>external_student_test</td>
        <td>school_id, test_code, sub_test_code, test_date, discriminator</td>
    </tr>
    <tr>
        <td>external_student_transcript</td>
        <td>school_id</td>
    </tr>
    <tr>
        <td>external_student_transcript_course</td>
        <td>school_id, term_code, formatted_course, section_code</td>
    </tr>
    <tr>
        <td>external_student_transcript_term</td>
        <td>school_id, term_code</td>
    </tr>
    <tr>
        <td>external_term</td>
        <td>code</td>
    </tr>
    </tbody>
</table>

For a complete list of column names and constraints please see the relevant mappings for your version of SSP.


[Data Integration Mappings for Version 2.1.0:](https://wiki.jasig.org/display/SSP/SSP+v2.1.0+Data+Integration+Mapping)
[Data Integration Mappings for Version 2.0.1:](https://wiki.jasig.org/download/attachments/57574117/SIS%20Data%20Mappings%20v2.0.xlsx?version=8&modificationDate=1363628409239&api=v2)
[Data Integration Mappings for Version 2.0.0](https://wiki.jasig.org/display/SSP/SSP+v2.0.0+Data+Integration+Mapping)
[Data Integration Mappings for Version 1.2.1](https://wiki.jasig.org/display/SSP/SSP+v1.2.1+Data+Integration+Mapping)

4. For questions on csv formatting please see [expected csv format](http://edoceo.com/utilitas/csv-file-format).
   Empty (including those with all whitespace) strings will be entered as null values.

   acceptable examples:
   1. no quotes
   faculty_school_id,term_code,formatted_course,title,section_code,section_number
   sherman123,FA12,Biology 101,Introduction To Biology,,

   2. All quotes (note the enclosed comma in column 1):
   "faculty_school_id","term_code","formatted_course","title","section_code","section_number"
   "sherman, 123","FA12","Biology 101","Introduction To Biology","",""

   2. All quotes escaped quote(note the enclosed comma in column 1):
   "faculty_school_id","term_code","formatted_course","title","section_code","section_number"
   "sherman"", 123","FA12","this is a string "", with escaped quotes","Introduction To Biology","",""


