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
When used as a method to update already existing data, the only requirement is that the columns containing primary key values be supplied.

Source code in the `org.jasig.ssp.util.importer.validation.map.metadata` package is based on source code generously shared by the [Jarb Framework](http://jarbframework.org/). Special thanks to Jeroen van Schagen.

Release History
===============

v1.0.0
------
Dec 13, 2013 - Initial release

v1.1.0
------
Sept 30, 2014 - Minor maintenance release.

1. DDL support for SSP 2.3.0 - 2.5.2
1. Workarounds for `external_*` tables lacking primary keys

Re #2, `ssp-data-importer` now generates synthetic primary keys using all available columns for any `stg_external_*` table which lacks a physical primary key. `external_substitutable_course` is the only such table at this time. It cannot be assigned a physical primary key under the current design because columns which should be included allow nulls. This has two consequences for CSV file generators:

1. All `external_substitutable_course` columns must be included in every `external_substitutable_course.csv`, and
2. That table is essentially "insert-only" because a change to any given column implies a new primary key. Repetitions of the same key are effectively no-ops.

v1.1.1
------
Nov 19, 2019 - Minor maintenance release.

1. SSP-3275 - Added From email address as a configuration value to fix an issue with new versions of MS Exchange


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
1. Finally, processing and upsert folders are removed and the processed files are archived.

Pre-Requisites
==============

At a minimum you'll need a Java JDK 1.6 install.
JDK 1.7+ should work fine beginning with the Importer 1.1.0 release.
The Java installation process can vary widely from platform to platform, so you're on you're own for that one.
`ssp-data-importer` can reuse the same Java installation as SSP if installed on the same host.

`ssp-data-importer` must be able to open a JDBC connection to the SSP database and execute DML and DDL statements.
SSP currently supports Postgres 9.1+ and SQLServer 2008 R2, with unofficial support for SQLServer 2012 (i.e. production systems are known to run against SQLServer 2012 but the product is not tested against that version).

To build the application from source you will need a Maven 3+ installation.
Deployers should not expect to build the application from source, though.

Installing and Running
======================

## Download

The application is distributed in `.tar.gz` and `.zip` formats.

Released binaries are attached to [this page](https://wiki.jasig.org/display/SSP/SSP+Data+Importer)

Or releases and snapshots can be downloaded from the Sonatype Maven repositories.
Click [here](https://oss.sonatype.org/index.html#nexus-search;quick~ssp-data-importer) to browse all available artifacts there.  

The download URL pattern for released binaries in Sonatype is:

`https://oss.sonatype.org/service/local/repositories/releases/content/org/jasig/ssp/util/importer/ssp-data-importer-assembly/{version}/ssp-data-importer-assembly-{version}-bin.{format}`

For example, the `tar.gz` for version 1.1.0 is at:

https://oss.sonatype.org/service/local/repositories/releases/content/org/jasig/ssp/util/importer/ssp-data-importer-assembly/1.0.0/ssp-data-importer-assembly-1.1.0-bin.tar.gz

And the `.zip` is at:

https://oss.sonatype.org/service/local/repositories/releases/content/org/jasig/ssp/util/importer/ssp-data-importer-assembly/1.1.0/ssp-data-importer-assembly-1.1.0-bin.zip

### Linux Download

To download and unroll on Linux:

```bash
# Create installation directory. Can be anywhere you like.
# Example here assumes you'll install below a standard SSP dir.
$> mkdir -p /opt/ssp
$> cd /opt/ssp

# Download and extract the binary.
# See URL examples above.
$> wget https://oss.sonatype.org/service/local/repositories/releases/content/org/jasig/ssp/util/importer/ssp-data-importer-assembly/${version}/ssp-data-importer-assembly-${version}-bin.tar.gz
# This will create a directory named ssp-data-importer
$> tar -xzvf ssp-data-importer-assembly-${version}-bin.tar.gz

# Change into the extracted binary to configure it
$> cd ssp-data-importer
```

### Windows Download

Open a browser and paste the version specific URL into the location bar. See above for concrete examples:

`https://oss.sonatype.org/service/local/repositories/releases/content/org/jasig/ssp/util/importer/ssp-data-importer-assembly/${version}/ssp-data-importer-assembly-${version}-bin.zip`

Using Windows Explorer, find the downloaded zip and extract it.

Find the `ssp-data-importer` directory in the extracted zip and copy it to the intended installation directory, e.g. below `c:\ssp\`.

## Database Initialization

`ssp-data-importer` writes to the SSP database, but requires two types of additional tables:

1. SpringBatch `JobRepository` tables - Tracks job progress and ensures multiple copies of the same job aren't running at the same time
2. Staging tables - Mirror images of `external_*` tables, but always with primary keys. Batch contents are inserted into these tables before being inserted or updated to the "real" `external_*` tables. This acts as an additional validation step and reduces network traffic required to determine whether any given row should be treated as an insert or an update.

Both of these types of tables need to be created out of band. The application will not attempt to create them at startup.

**Step 1:** Extract the DDL files for your database platform and SSP version.

`ssp-data-importer` includes DDL files for setting up and tearing down its `JobRepository` and "Staging" tables, with one pair of db platform-specific DDL files for each supported revison of SSP's `external_*` tables. Use the following table to find the DDL files appropriate for your SSP installation. All paths are relative to the root of the `ssp-data-importer-impl-{version}.jar`:

| SSP Versions | Importer DDL | 
| ------------ | ------------ |
| 2.0.0, 2.0.1, 2.1.0, 2.2.0 | <ul><li>`./sql/postgres/postgres-2.1.0-create.sql`</li><li>`./sql/postgres/postgres-2.1.0-drop.sql`</li><li>`./sql/sqlserver/sqlserver-2.1.0-create.sql`</li><li>`./sql/sqlserver/sqlserver-2.1.0-drop.sql`</li></ul> |
| 2.3.0 | <ul><li>`./sql/postgres/postgres-2.3.0-create.sql`</li><li>`./sql/postgres/postgres-2.3.0-drop.sql`</li><li>`./sql/sqlserver/sqlserver-2.3.0-create.sql`</li><li>`./sql/sqlserver/sqlserver-2.3.0-drop.sql`</li></ul> |
| 2.4.0, 2.4.1, 2.4.2 | <ul><li>`./sql/postgres/postgres-2.4.0-create.sql`</li><li>`./sql/postgres/postgres-2.4.0-drop.sql`</li><li>`./sql/sqlserver/sqlserver-2.4.0-create.sql`</li><li>`./sql/sqlserver/sqlserver-2.4.0-drop.sql`</li></ul> |
| 2.5.0, 2.5.1, 2.5.2 | <ul><li>`./| 2.7 | <ul><li>`./sql/postgres/postgres-2.7.0-create.sql`</li><li>`./sql/postgres/postgres-2.7.0-drop.sql`</li><li>`./sql/sqlserver/sqlserver-2.7.0-create.sql`</li><li>`./sql/sqlserver/sqlserver-2.7.0-drop.sql`</li></ul> |
sql/postgres/postgres-2.5.0-create.sql`</li><li>`./sql/postgres/postgres-2.5.0-drop.sql`</li><li>`./sql/sqlserver/sqlserver-2.5.0-create.sql`</li><li>`./sql/sqlserver/sqlserver-2.5.0-drop.sql`</li></ul> |
| 2.6 | <ul><li>`./sql/postgres/postgres-2.6.0-create.sql`</li><li>`./sql/postgres/postgres-2.6.0-drop.sql`</li><li>`./sql/sqlserver/sqlserver-2.6.0-create.sql`</li><li>`./sql/sqlserver/sqlserver-2.6.0-drop.sql`</li></ul> |
| 2.7 | <ul><li>`./sql/postgres/postgres-2.7.0-create.sql`</li><li>`./sql/postgres/postgres-2.7.0-drop.sql`</li><li>`./sql/sqlserver/sqlserver-2.7.0-create.sql`</li><li>`./sql/sqlserver/sqlserver-2.7.0-drop.sql`</li></ul> |
| 2.8 | <ul><li>`./sql/postgres/postgres-2.8.0-create.sql`</li><li>`./sql/postgres/postgres-2.8.0-drop.sql`</li><li>`./sql/sqlserver/sqlserver-2.8.0-create.sql`</li><li>`./sql/sqlserver/sqlserver-2.8.0-drop.sql`</li></ul> |

In most cases a single pair of `ssp-data-importer` DDL files supports all SSP patch releases within a minor release, and sometimes even across minor releases. If your SSP version isn't listed above, try the DDL for the most recent minor+patch release _below_ your release. E.g. if you're trying SSP 2.6.0 but a pair of DDL files isn't yet specifically targeted at that SSP version, try the 2.5.0 DDL.

The examples below assume you have a command prompt open and your current directory is the `ssp-data-importer` installation directory.

If you're upgrading an existing Importer installation, you'll typically first run the drop DDL scripts specific to your existing install. Then run the create scripts for your new, target SSP version. For example, to upgrade from SSP 2.1.0/2.2.0 to SSP 2.5.x:

For Postgres:
```
# Will extract the file to ./sql/postgres/postgres-2.1.0-drop.sql
$> ${JAVA_HOME}/bin/jar -xf lib/ssp-data-importer-impl-1.1.0.jar sql/postgres/postgres-2.1.0-drop.sql
```
For SQLServer:
```
# Will extract the file to ./sql/sqlserver/sqlserver-2.1.0-drop.sql
$> %JAVA_HOME%\bin\jar -xf lib\ssp-data-importer-impl-1.1.0.jar sql\sqlserver\sqlserver-2.1.0-drop.sql
```
Or if you are using a JRE which doesn't include the `jar` utility, you can unzip jar files using your environment's standard zip management tools.
E.g. `unzip` on Linux.
Or on Windows take a copy of the jar file, rename it to end in `.zip`, and then extract the `sql` file/s using Windows Explorer.


For Postgres:
```
# Will extract the file to ./sql/postgres/postgres-2.5.0-create.sql
$> ${JAVA_HOME}/bin/jar -xf lib/ssp-data-importer-impl-1.1.0.jar sql/postgres/postgres-2.5.0-create.sql
```
For SQLServer:
```
# Will extract the file to ./sql/sqlserver/sqlserver-2.5.0-create.sql
$> %JAVA_HOME%\bin\jar -xf lib\ssp-data-importer-impl-1.1.0.jar sql\sqlserver\sqlserver-2.5.0-create.sql
```


**Step 2:** Modify DDL to match your database.

In particular, you may wish to find and replace the following, depending on the particulars of your database configuration:

1. Usernames: DDL assumes `sspadmin` and `ssp` usernames by default.
2. Postgres-specific config, e.g. tablespace. See top of `postgres-2.5.0-create.sql`
3. SQLServer schema: find and replace all occurrances of `[dbo]`
4. SQLServer partition scheme name or file group for indexes (defaults to `[PRIMARY]`)

**Step 3:** Execute DDL

For SQLServer, this likely involves opening a connection to your database using SQLServer Management Studio, pasting your (possibly) customized `sqlserver-2.1.0-drop.sql` into a query window, and running the query window. Then repeat, using your (possibly) customized `sqlserver-2.5.0-create.sql`.

For Postgres, if you have the `psql` command line tool installed:
```
$> psql -h <hostname> -p <port> -U <username> -d <databasename> -f ./sql/postgres/postgres-2.1.0-drop.sql
$> psql -h <hostname> -p <port> -U <username> -d <databasename> -f ./sql/postgres/postgres-2.5.0-create.sql
```


## Application Installation

The following assumes the JDK is already installed and the current network configuration allows the host on which the application to open a JDBC connection to the SSP database.
`ssp-data-importer` can be installed on the same host as SSP or on another host altogether. The application's default memory footprint is very small (JVM-default heap sizing is usually fine), but can be expected to saturate a single CPU during its execution.
These instructions also assume the directory to be monitored has already been mounted/created. This directory would typically be located on shared storage or otherwise support third-party upload, e.g. via SCP or FTP.

### Linux

```bash
# A bin/setJobEnv.sh can be used to configure both the launch script and
# the application itself. Available options are listed in runJob.js.
# The only required override is to JAVA_HOME. But the launch script does
# set several application config defaults as system properties. System
# properties override values set in properties files. So if you need to
# override any of the following application properties, you'll
# need to do so in bin/setJobEnv.sh using the shell vars listed below:
#
#   PROCESS_DIR -> batch.tables.process.folder
#   UPSERT_DIR -> batch.tables.upsert.folder
#   ARCHIVE_DIR -> batch.tables.archive.folder
#
# It's often easiest to just set all non-sensitive config in
# bin/setJobEnv.sh by passing arbitrary system properties in
# JVM_OPTS. E.g.:
#
#   JVM_OPTS="-Dbatch.tables.input.folder=file:/opt/ssp/upload
#             -Dbatch.jdbc.url=jdbc:postgresql://127.0.0.1:5432/ssp \
#             -Dbatch.jdbc.driver=org.postgresql.Driver"
#
$> vim bin/setJobEnv.sh

# Create a file with local overrides for the app config itself.
# Again, it's often easiest to set all app config as system
# properties in bin/setJobEnv.sh and use this file just for
# sensitive configuration you do not want to appear on the process
# command line, e.g. batch.jdbc.password and batch.smtp.password.
# See the "Application Configuration Options" section below for
# all available properties.
$> vim conf/ssp-importer.properties

# For logging configuration see the "Logging" section below.

# Make the bin/runJob.sh script executable
$> chmod +x bin/runJob.sh

# To run hourly using cron add the following line to your crontab
# Generating an email from cron is typically not necessary because
# the application generates an email on both successful and errored
# executions. Check its log files for more detail. But when first
# experimenting with this app, you might want to leave off the 2>&1
# so cron will attempt to email errors.
#
#   0 0-23 * * * /opt/ssp/ssp-data-importer/bin/runJob.sh >> /dev/null 2>&1
#
# NB The application does not protect against multiple instances of
# itself running at the same time. Consider wrapping the runJob.sh
# script in a 'flock' command to protect against simultaneous
# executions. E.g.
#
#   0 0-23 * * * flock -n /tmp/ssp-data-importer.lck /opt/ssp/ssp-data-importer/bin/runJob.sh >> /dev/null 2>&1
$> crontab -e

# Or to test the application just execute runJob.sh.
```

### Windows

Assuming that installation location, create a file at `c:\ssp\ssp-data-importer\bin\setJobEnv.bat` and set local overrides to any of the options described by `c:\ssp\ssp-data-importer\bin\runJob.bat`. E.g.

```
rem NB For SQLServer be sure to override both spring.profiles.active and batch.jdbc.url
rem as shown here. Each 'set' should be a single line. They are wrapped here for readability.
set "JVM_OPTS="-Dspring.profiles.active=sqlserver"
               "-Dbatch.jdbc.url=jdbc:jtds:sqlserver://127.0.0.1:1433/ssp"
               "-Dbatch.jdbc.driver=net.sourceforge.jtds.jdbc.Driver"
               "-Dbatch.tables.input.folder=file:c:\ssp\upload""
set "JAVA_HOME=C:\Program Files (x86)\Java\jre6"
```

See the Linux installation notes above re `setJobEnv.sh` for more detail on which options can/should be set in `setJobEnv.bat`.

Assuming the same installation location, create a file at `c:\ssp\ssp-data-importer\conf\ssp-importer.properties` and set local application config overrides.
As with Linux installs, it is usually easiest to set most config in `setJobEnv.bat` and use `ssp-importer.properties` for sensitive config, e.g. database and SMTP passwords.

For logging configuration see the "Logging" section below.

To test the application, open a Cmd window (Start -> search for 'Cmd', or Start -> All Programs -> Accessories -> Command Prompt) and run `c:\ssp\ssp-data-importer\bin\runJob.bat`.
Note that this *will* attempt to connect to your database and create the necessary tables. But as long as there are no files in the monitored directory, no further database writes will occur.

To configure the job to run on a schedule, launch the Windows Task Manager (Start -> search for "Task Manager").

1. Task Manager -> Actions -> Create a Basic Task
2. Name: SSP Data Importer
3. Click 'Next'
4. Trigger: Daily
5. Click 'Next'
6. Set the desired time of day and recurrance policies
7. Click 'Next'
8. Action: Start a program
9. Click 'Next'
10. Browse to and select `c:\ssp\ssp-data-importer\bin\runJob.bat`
11. Can leave 'Add arguments' nor 'Start in' fields blank
12. Click 'Next'
13. Check 'Show properties'
14. Click 'Finish'
15. Select 'Run whether user is logged on or not'
16. Click 'OK'
17. Enter password
18. Click 'OK'

## Logging

`ssp-data-importer` uses [Logback](http://logback.qos.ch/manual/configuration.html) as its logging framework.
The default Logback configuration file is embedded in the `ssp-data-importer-impl-<version>.jar` file at `./logback.xml`.
That default configuration will output `INFO` messages "and worse" to stdout/stderr and to a daily rolled file: `<install>/logs/ssp-data-importer.log`.

If you just need to change the directory in which logs collect, the easiest way to do that is to change the `LOG_HOME` shell var in `setJobEnv.[sh|bat]`.

For more complex changes, extract the default file as a starting point:

```
$> cd <install>/conf
$> $JAVA_HOME/bin/jar -xf ../lib/ssp-data-importer-impl-${version}.jar logback.xml
```

Or if you are using a JRE which doesn't include the `jar` utility, you can unzip that jar file using your environment's standard zip management tools.
E.g. `unzip` on Linux.
Or on Windows take a copy of the jar file, rename it to end in `.zip`, and then extract the `logback.xml` file/s using Windows Explorer.

Edit the extracted file to suit your needs. The Logback project has thorough [documentation](http://logback.qos.ch/manual/configuration.html). Or contact the [ssp-user](https://wiki.jasig.org/display/JSG/ssp-user) mailing list with questions.

By default, `ssp-data-importer` will look for custom log config at `$CONFIG_DIR/logback.xml`.
So if you haven't modified the `CONFIG_DIR` env var and place your modified file in `<install>/conf`, `ssp-data-importer` will automatically pick up your changes on its next execution.
Or if you would rather place your overrides in an entirely different location, specify that complete path by setting `LOGBACK_FILE` IN `setJobEnv.[sh|bat]`.

For security reasons the app does not, by default, log SQL statements.
For debugging purposes, though, the default logging configuration accepts a `log.query.appender` system property which can be used to enable query logging.
The `LOG_QUERY_APPENDER` env var can also be used to set this property.
Acceptable values, which should be self-explanatory, are `consoleAppender`, `fileAppender`, and `devNullAppender`

Application Configuration Options
=================================

There are a number of properties that are required for the program to run properly.

```properties
#FOLDER LOCATIONS
# NB: For all 'batch.tables.*.folder' properties, the 'file:' prefix must be present.

# Full path to folder that will contain initial csv files.
# **REQUIRED AND HAS NO DEFAULT**
batch.tables.input.folder=file:/location/of/input/folder/

# Full path to folder where csv files will be processed
# **IGNORED IF USING runJob.[sh|bat]. Override in setJobEnv.[sh|bat] in that case**
batch.tables.process.folder=file:/location/of/process/folder/

# Full path to folder where csv files will be upserted
# **IGNORED IF USING runJob.[sh|bat]. Override in setJobEnv.[sh|bat] in that case**
batch.tables.upsert.folder=file:/location/of/upsert/folder

# Full path to archive folder
# **IGNORED IF USING runJob.[sh|bat]. Override in setJobEnv.[sh|bat] in that case**
batch.tables.archive.folder=file:/location/of/archive

#INITIALIZATION
# Set minutes file unmodified before beginning processing    (default: 10)
batch.tables.lagTimeBeforeStartInMinutes=10

#ARCHIVING
# Turn archiving on default is true
batch.tables.retain.input.archive=true
# Injected into archive file names. Also Injected into the subject of per-job reports.
# Use this to discriminate between archives and reports from mulitple deployments.
batch.title=ssp_import

# What files to archive, ALL, NONE, UNIQUE  default: UNIQUE
batch.tables.archive=UNIQUE

#TOLERANCE
# Number of lines generating validation errors to allow during read/write
batch.rawitem.skip.limit=10

# Number of lines to be processed as a unit during initial validation.
batch.rawitem.commit.interval=100

# Number of lines generating validation errors to allow during upsert
batch.upsertitem.skip.limit=10

# Number of lines to be prorcessed as a unit during database interactions.
# Larger batch sizes will reduce processing time make errors less specific
# and increase memory footprint
batch.upsertitem.commit.interval=100

#DATABASE
# NB: In addition to specifying driver and connection coordinates below,
#     you must explicitly enable the database-specific Spring profile
#     by setting a 'spring.active.profiles' JVM system property. If
#     you are using runJob.[sh|bat], this is done by setting a shell var:
#
#       PROFILES=[postgres|sqlserver]

# The full URL to the target database. Modify to refer to SSP's database
# The default value (ssp-change-me) is intended to ensure that
# tests never accidentally overwrite data in a "real" database
batch.jdbc.url=jdbc:postgresql://127.0.0.1:5432/ssp-change-me

# The JDBC driver. Valid values:
#   org.postgresql.Driver
#   net.sourceforge.jtds.jdbc.Driver
batch.jdbc.driver=org.postgresql.Driver

# Username for connections to the db specified by ${batch.jdbc.url}.
# Must be allowed to execute both DML and DDL
batch.jdbc.user=sspadmin

# Password for connections to the db specified by ${batch.jdbc.url}
# and ${batch.jdbc.user}. Must be allowed to execute both DML and DDL
batch.jdbc.password=sspadmin

#EMAIL NOTIFICATION
# Activate email
batch.sendEmail=true

# SMTP Host name
# **REQUIRED IF batch.sentMail=true**
batch.smtp.host=localhost

# SMTP port
# **REQUIRED IF batch.sentMail=true**
batch.smtp.port=25

# SMTP protocol
# **REQUIRED IF batch.sentMail=true**
batch.smtp.protocol=smtp

# SMTP username
# **REQUIRED IF batch.sentMail=true**
batch.smtp.username=sysadmin

# SMTP password
# **REQUIRED IF batch.sentMail=true**
batch.smtp.password=password

# Comma separated list of report recipients. Addresses must follow RFC822 syntax.
# **REQUIRED IF batch.sentMail=true**
batch.email.recipients="SSP DATA IMPORTER"<sysadmin@localhost>

# # **REQUIRED IF batch.sentMail=true**
batch.email.replyTo="SSP DATA IMPORTER"<sysadmin@localhost>

# # **REQUIRED IF batch.sentMail=true** and for certain email servers
batch.email.from="SSP DATA IMPORTER"<sysadmin@localhost>

#TESTING
batch.table.input.duplicate=false
exists.only.for.testing.1=default.value.1
exists.only.for.testing.2=default.value.2
```

CSV FILE FORMAT
===============

1. The name of each uploaded `csv` file MUST correspond to the name of the SSP `external_*` table to target. Example: the (valid) contents of `external_person.csv` will be written to the `external_person` database table.
1. Comma (,) is the only supported delimiter character. Use the quote character (") to wrap fields which may themselves contain commas. Repeat the quote character ("") to escape quote characters embedded in field values.
1. Each file MUST have an initial line containing the column names in the format of the external table. Example: for `external_faculty_course`, the first line of `external_faculty_course.csv` would contain: `faculty_school_id,term_code,formatted_course,title,section_code,section_number`
1. Partial data updates are supported. However, in addition to the data being updated the key values for the specific table MUST be supplied.
1. **Multi-line records are not currently supported.** 

For SSP version 2.2, here are the keys for each external table:

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
        <td>(NONE) recommended faculty_school_id,section_code</td>
    </tr>
    <tr>
        <td>external_faculty_course_roster</td>
        <td>(NONE) recommended school_id,section_code</td>
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

[Data Integration Mappings for Version 2.5.x](https://wiki.jasig.org/display/SSP/SSP+v2.5.0+Data+Integration+Mapping)

[Data Integration Mappings for Version 2.4.x](https://wiki.jasig.org/display/SSP/SSP+v2.4.0+Data+Integration+Mapping)

[Data Integration Mappings for Version 2.3.x](https://wiki.jasig.org/display/SSP/SSP+v2.3.0+Data+Integration+Mapping)

[Data Integration Mappings for Version 2.1.0](https://wiki.jasig.org/display/SSP/SSP+v2.1.0+Data+Integration+Mapping) (No change for 2.2.x at this writing.)

[Data Integration Mappings for Version 2.0.1:](https://wiki.jasig.org/download/attachments/57574117/SIS%20Data%20Mappings%20v2.0.xlsx?version=8&modificationDate=1363628409239&api=v2)

[Data Integration Mappings for Version 2.0.0](https://wiki.jasig.org/display/SSP/SSP+v2.0.0+Data+Integration+Mapping)

For questions on csv formatting please see [expected csv format](http://edoceo.com/utilitas/csv-file-format).

Empty (including those with all whitespace) strings will be entered as null values.

Acceptable examples:

**No quotes:**

```csv
faculty_school_id,term_code,formatted_course,title,section_code,section_number
sherman123,FA12,BIO101,Introduction To Biology,BIO101-FA12-001,
```

**All quotes (note the enclosed comma in column 1):**

```csv
"faculty_school_id","term_code","formatted_course","title","section_code","section_number"
"sherman, 123","FA12","BIO101,"Introduction To Biology","BIO101-FA12-001",""
```

**All quotes escaped quote(note the enclosed comma in column 1):**

```
"faculty_school_id","term_code","formatted_course","title","section_code","section_number"
"sherman"", 123","FA12","this is a string "", with escaped quotes","Introduction To Biology","BIO101-FA12-001",""
```
**The suggested DATE format:**
```
YYYY-MM-DD  examples: 2012-12-31, 2012-08-09
```

**Note:** All date fields are implemented as date in both SQLServer and PostreSQL deployments.

Building
========

This section is for developers working on `ssp-data-importer` code.
It should not be necessary to build `ssp-data-importer` from source for typical installations.

`ssp-data-importer` is a Maven project.
It can be built and tested using the standard `mvn` command.
Note that nearly all tests are integration tests and rely on a database.
The tests do not provision the database itself since it makes no sense for this application to test against a "dummy," embedded database.
To build without tests:

```
%> mvn -DskipTests=true clean install
```

To build with tests you'll need a dedicated test database (usually local).
The test database must meet several conditions prior to running the tests:

1. It must exist, and
2. `${batch.jdbc.user}` must be able to execute DML and DDL on it, and
3. `external_*` tables must already exist in it.

For the 1.0.0 and 1.1.0 `ssp-data-importer` releases, you are responsible for creating the `external_*` tables yourself.
The easiest way to do this is to dump these tables from an existing SSP install. On Postgres:

```bash
$> pg_dump -t 'external_*' -s ${MY_SSP_DB_NAME} > /tmp/ssp-external-tables.sql
$> psql -U sspadmin -d ${MY_IMPORTER_TEST_DB_NAME} -f /tmp/ssp-external-tables.sql
```

Or in SQLServer Management Studio, right click on the source database, select Tasks -> Generate Script.
Select all the `external_*` tables and complete the wizard accepting all the defaults, specifying a target file path that you'll remember.
Then open that file in SQLServer Management Studio and change the `USE` statement at the top to specify your test database's name.
Then execute the updated script.

Then create a `ssp-importer.properties` file specifying your test db connection coordinates. E.g. for Postgres:

```properties
batch.jdbc.url=jdbc:postgresql://127.0.0.1:5432/${MY_IMPORTER_TEST_DB_NAME}
batch.jdbc.driver=org.postgresql.Driver
batch.jdbc.user=sspadmin
batch.jdbc.password=sspadmin
```

Or SQLServer:
```properties
batch.jdbc.url=jdbc:jtds:sqlserver://127.0.0.1:1433/${MY_IMPORTER_TEST_DB_NAME}
batch.jdbc.driver=net.sourceforge.jtds.jdbc.Driver
batch.jdbc.user=sspadmin
batch.jdbc.password=sspadmin

```

Then specify the location of that file and the database platform type as arguments to the `mvn` command. Note that `properties-directory` must point to the _directory_ containing the `ssp-importer.properties` file you created above. And that file _must_ be named `ssp-importer.properties`.

```
%> mvn -Dproperties-directory=/path/to/your/importer/config/directory \
-Dspring-profile=[sqlserver|postgres] \
clean install
```

The default `spring-profile` is "postgres".
