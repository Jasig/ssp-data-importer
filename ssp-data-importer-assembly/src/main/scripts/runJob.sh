#!/bin/bash

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
# Options are:
#
#  JAVA_HOME - Path to dir containing the java executable. Required.
#
#  CLASSPATH - Standard java classpath config option. Should rarely, if ever, need to change.Optional. Defaults to
#              '../lib/*' relative to this file.
#
#  CONFIG_DIR - Override individual config properties with contents of ${CONFIG_DIR}/ssp-importer.properties, if that
#               file exists. Optional. Defaults to /opt/ssp-import-conf
#
#  LOGBACK_FILE - Override the complete logging coniguration with the file at this path, if it exists. Optional.
#                 Defaults to ${CONFIG_DIR}/logback.xml
#
#  LOG_HOME - If using the default logging config, logs will collect in this directory. Optional. Defaults to "../logs"
#             relative to this directory.
#
#  JOB_PATH - Spring psuedo-url pointing to the main ApplicationContext file for the job to be run. Should rarely, if
#             ever, need to change. Optional. Defaults to classpath:/launch-context.xml
#
#  JOB_IDENTIFIER - Name of the job to run. Should rarely, if ever, need to change. Optional. Defaults to importJob
#
#  MAIN - Fully qualified name of the Java class to run. Should rarely, if ever, need to change. Optional. Defaults
#         to org.jasig.ssp.util.importer.job.Main
#
#  JVM_OPTS - Additional arguments to pass to the JVM, e.g. to adjust heap size. Optional.
#
#  PROGRAM_OPTS - Additional arguments to pass to the application after JOB_PATH and JOB_IDENTIFIER. Optional.
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
CONFIG_DIR=${CONFIG_DIR:-/opt/ssp-import-conf}
LOGBACK_FILE=${LOGBACK_FILE:-"${CONFIG_DIR}/logback.xml"}
LOG_HOME=${LOG_HOME:-"$DIR/../logs"}
JOB_PATH=${JOB_PATH:-"classpath:/launch-context.xml"}
JOB_IDENTIFIER=${JOB_IDENTIFIER:-"importJob"}
MAIN=${MAIN:-"org.jasig.ssp.util.importer.job.Main"}

if [ "$JAVA_HOME" = "" ]; then
  echo "Error: JAVA_HOME environment variable is not set."
  exit 1
fi

$JAVA_HOME/bin/java \
  -cp "${CLASSPATH}" \
  -Dssp.importer.configdir="${CONFIG_DIR}" \
  -Dlogback.configurationFile="${LOGBACK_FILE}" \
  -Dlog.home="${LOG_HOME}" \
  ${JVM_OPTS} \
  "${MAIN}" \
  "${JOB_PATH}" \
  "${JOB_IDENTIFIER}" \
  ${PROGRAM_OPTS}
