@REM
@REM Licensed to Jasig under one or more contributor license
@REM agreements. See the NOTICE file distributed with this work
@REM for additional information regarding copyright ownership.
@REM Jasig licenses this file to you under the Apache License,
@REM Version 2.0 (the "License"); you may not use this file
@REM except in compliance with the License. You may obtain a
@REM copy of the License at:
@REM
@REM http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on
@REM an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied. See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM

@ECHO off

@REM This is a more configurable version of the file of the same name included in the CLI example in the SpringBatch source
@REM distro.
@REM
@REM Can either pass args on the command line or source them from a file next to this one named setJobEnv.sh
@REM
@REM E.g. from the command line:
@REM
@REM  $> set "CONFIG_DIR=c:\ssp\ssp-import-conf"
@REM  $> runJob.bat
@REM
@REM Or in a file:
@REM
@REM  $> echo set "CONFIG_DIR=c:\ssp\ssp-import-conf" > ./setJobEnv.sh
@REM  $> runJob.bat
@REM
@REM You do not need to be in this directory when executing this file.
@REM
@REM Options will persist for the lifetime of your command window. To unset all of the options used by this file,
@REM except for JAVA_HOME, run the 'unset.bat' script in this directory.
@REM
@REM Options are:
@REM
@REM  JAVA_HOME - Path to dir containing the java executable. Required.
@REM
@REM  CLASSPATH - Standard java classpath config option. Should rarely, if ever, need to change.Optional. Defaults to
@REM              '..\lib\*' relative to this file.
@REM
@REM  CONFIG_DIR - Override individual config properties with contents of %CONFIG_DIR%/ssp-importer.properties, if that
@REM               file exists. Optional. Defaults to ..\conf relative to this directory.
@REM
@REM  LOGBACK_FILE - Override the complete logging coniguration with the file at this path, if it exists. Optional.
@REM                 Defaults to %CONFIG_DIR%/logback.xml
@REM
@REM  LOG_HOME - If using the default logging config, logs will collect in this directory. Optional. Defaults to ..\logs
@REM             relative to this directory.
@REM
@REM  JOB_PATH - Spring psuedo-url pointing to the main ApplicationContext file for the job to be run. Should rarely, if
@REM             ever, need to change. Optional. Defaults to classpath:/launch-context.xml
@REM
@REM  JOB_IDENTIFIER - Name of the job to run. Should rarely, if ever, need to change. Optional. Defaults to importJob
@REM
@REM  MAIN - Fully qualified name of the Java class to run. Should rarely, if ever, need to change. Optional. Defaults
@REM         to org.jasig.ssp.util.importer.job.Main
@REM
@REM  PROCESS_DIR - Override the location to which uploaded files are initially moved from the monitored dir. Optional.
@REM                Defaults to "..\importjob\process" relative to this directory. Note that if you need to change this
@REM                location, you must do so here rather than in %CONFIG_DIR%\ssp-importer.properties
@REM
@REM  UPSERT_DIR - Override the location to files are moved after validation in PROCESS_DIR. Optional.
@REM               Defaults to "..\importjob\upsert" relative to this directory. Note that if you need to change this
@REM               location, you must do so here rather than in %CONFIG_DIR%\ssp-importer.properties
@REM
@REM  ARCHIVE_DIR - Override the location to files are moved after handling in PROCESS_DIR and UPSERT_DIR. Optional.
@REM                Defaults to "..\importjob\archive" relative to this directory. Note that if you need to change this
@REM                location, you must do so here rather than in %CONFIG_DIR%\ssp-importer.properties
@REM
@REM  JVM_OPTS - Additional arguments to pass to the JVM, e.g. to adjust heap size. Can also be used to set arbitary
@REM             application config options. Values set here override anything set in
@REM             %CONFIG_DIR%\ssp-importer.properties. E.g.
@REM
@REM               set "JVM_OPTS="-Dbatch.jdbc.url=jdbc:postgresql://127.0.0.1:5432/ssp" "-Dbatch.jdbc.driver=org.postgresql.Driver""
@REM
@REM             This var is optional.
@REM
@REM Defaults are applied after setJobEnv.bat has been sourced.

SET "CURRENT_DIR=%~dp0%"
SET "CURRENT_DIR=%CURRENT_DIR:~0,-1%

IF EXIST "%CURRENT_DIR%\setJobEnv.bat" (
  CALL "%CURRENT_DIR%\setJobEnv.bat"
)

IF NOT DEFINED CLASSPATH (
  SET "CLASSPATH=%CURRENT_DIR%\..\lib\*"
)
@REM echo "CLASSPATH: %CLASSPATH%"

IF NOT DEFINED CONFIG_DIR (
  SET "CONFIG_DIR=%CURRENT_DIR%\..\conf"
)
@REM ECHO "CONFIG_DIR: %CONFIG_DIR%"

IF NOT DEFINED LOGBACK_FILE (
  SET "LOGBACK_FILE=%CONFIG_DIR%\logback.xml"
)
@REM ECHO "LOGBACK_FILE: %LOGBACK_FILE%"

IF NOT DEFINED LOG_HOME (
  SET "LOG_HOME=%CURRENT_DIR%\..\logs"
)
@REM ECHO "LOG_HOME: %LOG_HOME%"

IF NOT DEFINED JOB_PATH (
  SET "JOB_PATH=classpath:/launch-context.xml"
)
@REM ECHO "JOB_PATH: %JOB_PATH%"

IF NOT DEFINED JOB_IDENTIFIER (
  SET "JOB_IDENTIFIER=importJob"
)
@REM ECHO "JOB_IDENTIFIER: %JOB_IDENTIFIER%"

IF NOT DEFINED MAIN (
  SET "MAIN=org.jasig.ssp.util.importer.job.Main"
)
@REM ECHO "MAIN: %MAIN%"

IF NOT DEFINED PROCESS_DIR (
  SET "PROCESS_DIR=%CURRENT_DIR%\..\importjob\process"
)
@REM ECHO "PROCESS_DIR: %PROCESS_DIR%"

IF NOT DEFINED UPSERT_DIR (
  SET "UPSERT_DIR=%CURRENT_DIR%\..\importjob\upsert"
)
@REM ECHO "UPSERT_DIR: %UPSERT_DIR%"

IF NOT DEFINED ARCHIVE_DIR (
  SET "ARCHIVE_DIR=%CURRENT_DIR%\..\importjob\archive"
)
@REM ECHO "ARCHIVE_DIR: %ARCHIVE_DIR%"

IF NOT DEFINED JAVA_HOME (
    echo Error: JAVA_HOME environment variable is not set.
    EXIT /B
)

"%JAVA_HOME%/bin/java" -cp "%CLASSPATH%" "-Dssp.importer.configdir=%CONFIG_DIR%" "-Dlogback.configurationFile=%LOGBACK_FILE%" "-Dlog.home=%LOG_HOME%" "-Dbatch.tables.process.folder=%PROCESS_DIR%" "-Dbatch.tables.upsert.folder=%UPSERT_DIR%" "-Dbatch.tables.archive.folder=%ARCHIVE_DIR%" %JVM_OPTS% %MAIN% %JOB_PATH% %JOB_IDENTIFIER% %PROGRAM_OPTS%