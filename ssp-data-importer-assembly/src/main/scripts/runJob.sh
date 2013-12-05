#!/bin/bash
#
# Licensed to Jasig under one or more contributor license
# agreements. See the NOTICE file distributed with this work
# for additional information regarding copyright ownership.
# Jasig licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a
# copy of the License at:
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on
# an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#


# This is a more configurable version of the file of the same name included in the CLI example in the SpringBatch source
# distro.
#
# Can either pass args on the command line or source them from a file next to this one named setJobEnv.sh
#
# E.g. from the command line:
#
#  $> CONFIG_DIR=/opt/ssp-import-conf ./runJob.sh
#
# Or in a file:
#
#  $> echo "CONFIG_DIR=/opt/ssp-import-conf" > ./setJobEnv.sh && ./runJob.sh
#
# You do not need to be in in this directory when executing this file.
#
# Options are (roughly ordered by descending importance):
#
#  JAVA_HOME - Path to dir containing the java executable. Required.
#
#  CONFIG_DIR - Override individual config properties with contents of ${CONFIG_DIR}/ssp-importer.properties, if that
#               file exists. Optional. Defaults to ../conf relative to this directory.
#
#  LOGBACK_FILE - Override the complete logging coniguration with the file at this path, if it exists. Optional.
#                 Defaults to ${CONFIG_DIR}/logback.xml
#
#  LOG_HOME - If using the default logging config, logs will collect in this directory. Optional. Defaults to ../logs
#             relative to this directory.
#
#  PROFILES - Comma-delimited list of enabled Spring profiles. Only recognized values are 'postgres' and 'sqlserver'.
#             This is an optional var and defaults to 'postgres', so should only need to modify this if *not* targeting
#             a Postgres db, in which case it should be set to:
#
#               PROFILES=sqlserver
#
#             This is equivalent to:
#
#               JVM_OPTS="-Dspring.profiles.active=sqlserver"
#
#  PROCESS_DIR - Override the location to which uploaded files are initially moved from the monitored dir. Optional.
#                Defaults to "../importjob/process" relative to this directory. Note that if you need to change this
#                location, you must do so here rather than in ${CONFIG_DIR}/ssp-importer.properties. Also, the value
#                must be prefixed with "file:". E.g. "PROCESS_DIR=file:/opt/ssp/importjob/process"
#
#  UPSERT_DIR - Override the location to files are moved after validation in PROCESS_DIR. Optional.
#               Defaults to "../importjob/upsert" relative to this directory. Note that if you need to change this
#               location, you must do so here rather than in ${CONFIG_DIR}/ssp-importer.properties. Also, the value
#               must be prefixed with "file:". E.g. "UPSERT_DIR=file:/opt/ssp/importjob/upsert"
#
#  ARCHIVE_DIR - Override the location to files are moved after handling in PROCESS_DIR and UPSERT_DIR. Optional.
#                Defaults to "../importjob/archive" relative to this directory. Note that if you need to change this
#                location, you must do so here rather than in ${CONFIG_DIR}/ssp-importer.properties. Also, the value
#                must be prefixed with "file:". E.g. "ARCHIVE_DIR=file:/opt/ssp/importjob/archive"
#
#  JVM_OPTS - Additional arguments to pass to the JVM, e.g. to adjust heap size. Can also be used to set arbitary
#             application config options. Values set here override anything set in
#             ${CONFIG_DIR}/ssp-importer.properties. E.g.
#
#                 JVM_OPTS="-Dbatch.jdbc.url=jdbc:postgresql://127.0.0.1:5432/ssp \
#                           -Dbatch.jdbc.driver=org.postgresql.Driver"
#
#             This var is optional.
#
#  PROGRAM_OPTS - Additional arguments to pass to the application after JOB_PATH and JOB_IDENTIFIER. Optional.
#
#  CLASSPATH - Standard java classpath config option. Should rarely, if ever, need to change.Optional. Defaults to
#              '../lib/*' relative to this file.
#
#  JOB_PATH - Spring psuedo-url pointing to the main ApplicationContext file for the job to be run. Should rarely, if
#             ever, need to change. Optional. Defaults to classpath:/launch-context.xml
#
#  JOB_IDENTIFIER - Name of the job to run. Should rarely, if ever, need to change. Optional. Defaults to importJob
#
#  MAIN - Fully qualified name of the Java class to run. Should rarely, if ever, need to change. Optional. Defaults
#         to org.jasig.ssp.util.importer.job.Main
#
# Defaults are applied after setJobEnv.sh has been sourced.

maybe_source_file() {
  if [ ! "" == "$1" -a -f "$1" ]; then
    . "$1"
  fi
}

DIR=$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

maybe_source_file "$DIR/setJobEnv.sh"

CLASSPATH=${CLASSPATH:-"${DIR}"/../'lib/*'}
CONFIG_DIR=${CONFIG_DIR:-"${DIR}/../conf"}
LOGBACK_FILE=${LOGBACK_FILE:-"${CONFIG_DIR}/logback.xml"}
LOG_HOME=${LOG_HOME:-"${DIR}/../logs"}
JOB_PATH=${JOB_PATH:-"classpath:/launch-context.xml"}
JOB_IDENTIFIER=${JOB_IDENTIFIER:-"importJob"}
MAIN=${MAIN:-"org.jasig.ssp.util.importer.job.Main"}
PROCESS_DIR=${PROCESS_DIR:-"file:${DIR}/../importjob/process"}
UPSERT_DIR=${UPSERT_DIR:-"file:${DIR}/../importjob/upsert"}
ARCHIVE_DIR=${ARCHIVE_DIR:-"file:${DIR}/../importjob/archive"}
PROFILES=${PROFILES:-"postgres"}

if [ "$JAVA_HOME" = "" ]; then
  echo "Error: JAVA_HOME environment variable is not set."
  exit 1
fi

$JAVA_HOME/bin/java \
  -cp "${CLASSPATH}" \
  -Dssp.importer.configdir="${CONFIG_DIR}" \
  -Dlogback.configurationFile="${LOGBACK_FILE}" \
  -Dlog.home="${LOG_HOME}" \
  -Dbatch.tables.process.folder="${PROCESS_DIR}" \
  -Dbatch.tables.upsert.folder="${UPSERT_DIR}" \
  -Dbatch.tables.archive.folder="${ARCHIVE_DIR}" \
  -Dspring.profiles.active="${PROFILES}" \
  ${JVM_OPTS} \
  "${MAIN}" \
  "${JOB_PATH}" \
  "${JOB_IDENTIFIER}" \
  ${PROGRAM_OPTS}
