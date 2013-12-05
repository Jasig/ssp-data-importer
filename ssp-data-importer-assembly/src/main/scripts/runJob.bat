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

@echo off

rem This is a more configurable version of the file of the same name included in the CLI example in the SpringBatch source
rem distro.
rem
rem Can either pass args on the command line or source them from a file next to this one named setJobEnv.sh
rem
rem E.g. from the command line:
rem
rem  $> set "CONFIG_DIR=c:\ssp\ssp-import-conf"
rem  $> runJob.bat
rem
rem Or in a file:
rem
rem  $> echo set "CONFIG_DIR=c:\ssp\ssp-import-conf" > ./setJobEnv.sh
rem  $> runJob.bat
rem
rem You do not need to be in this directory when executing this file.
rem
rem Options will persist for the lifetime of your command window. To unset all of the options used by this file,
rem except for JAVA_HOME, run the 'unset.bat' script in this directory.
rem
rem Options are:
rem
rem  JAVA_HOME - Path to dir containing the java executable. Required.
rem
rem  CLASSPATH - Standard java classpath config option. Should rarely, if ever, need to change.Optional. Defaults to
rem              '..\lib\*' relative to this file.
rem
rem  CONFIG_DIR - Override individual config properties with contents of %CONFIG_DIR%/ssp-importer.properties, if that
rem               file exists. Optional. Defaults to C:\ssp\ssp-import-conf
rem
rem  LOGBACK_FILE - Override the complete logging coniguration with the file at this path, if it exists. Optional.
rem                 Defaults to %CONFIG_DIR%/logback.xml
rem
rem  LOG_HOME - If using the default logging config, logs will collect in this directory. Optional. Defaults to ..\logs
rem             relative to this directory.
rem
rem  JOB_PATH - Spring psuedo-url pointing to the main ApplicationContext file for the job to be run. Should rarely, if
rem             ever, need to change. Optional. Defaults to classpath:/launch-context.xml
rem
rem  JOB_IDENTIFIER - Name of the job to run. Should rarely, if ever, need to change. Optional. Defaults to importJob
rem
rem  MAIN - Fully qualified name of the Java class to run. Should rarely, if ever, need to change. Optional. Defaults
rem         to org.jasig.ssp.util.importer.job.Main
rem
rem  JVM_OPTS - Additional arguments to pass to the JVM, e.g. to adjust heap size. Optional.
rem
rem  PROGRAM_OPTS - Additional arguments to pass to the application after JOB_PATH and JOB_IDENTIFIER. Optional.
rem
rem Defaults are applied after setJobEnv.bat has been sourced.

SET "CURRENT_DIR=%~dp0%"
SET "CURRENT_DIR=%CURRENT_DIR:~0,-1%

IF EXIST "%CURRENT_DIR%\setJobEnv.bat" (
  CALL "%CURRENT_DIR%\setJobEnv.bat"
)

IF NOT DEFINED CLASSPATH (
  SET "CLASSPATH=%CURRENT_DIR%\..\lib\*"
)
rem echo "CLASSPATH: %CLASSPATH%"

IF NOT DEFINED CONFIG_DIR (
  SET "CONFIG_DIR=C:\ssp\ssp-import-conf"
)
rem ECHO "CONFIG_DIR: %CONFIG_DIR%"

IF NOT DEFINED LOGBACK_FILE (
  SET "LOGBACK_FILE=%CONFIG_DIR%\logback.xml"
)
rem ECHO "LOGBACK_FILE: %LOGBACK_FILE%"

IF NOT DEFINED LOG_HOME (
  SET "LOG_HOME=%CURRENT_DIR%\..\logs"
)
rem ECHO "LOG_HOME: %LOG_HOME%"

IF NOT DEFINED JOB_PATH (
  SET "JOB_PATH=classpath:/launch-context.xml"
)
rem ECHO "JOB_PATH: %JOB_PATH%"

IF NOT DEFINED JOB_IDENTIFIER (
  SET "JOB_IDENTIFIER=importJob"
)
rem ECHO "JOB_IDENTIFIER: %JOB_IDENTIFIER%"

IF NOT DEFINED MAIN (
  SET "MAIN=org.jasig.ssp.util.importer.job.Main"
)
rem ECHO "MAIN: %MAIN%"

IF NOT DEFINED JAVA_HOME (
    echo Error: JAVA_HOME environment variable is not set.
    EXIT /B
)

"%JAVA_HOME%/bin/java" -cp "%CLASSPATH%" "-Dssp.importer.configdir=%CONFIG_DIR%" "-Dlogback.configurationFile=%LOGBACK_FILE%" "-Dlog.home=%LOG_HOME%" %JVM_OPTS% %MAIN% %JOB_PATH% %JOB_IDENTIFIER% %PROGRAM_OPTS%