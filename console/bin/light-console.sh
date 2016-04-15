#!/bin/sh

# ----------------------------------------------------------------------
# ----- Customizable Variables -----------------------------------------
# ----------------------------------------------------------------------
# JAVA_HOME=
LIGHT_HOME=$(dirname $(cd "$(dirname "$0")" && pwd))
LIGHT_CONFIG=${LIGHT_HOME}/config/light.conf
LIGHT_LOG_CONFIG=${LIGHT_HOME}/config/log4j2.xml
# ----------------------------------------------------------------------

# ----------------------------------------------------------------------
# ----- Do not touch below this line!-----------------------------------
# ----------------------------------------------------------------------
export LIGHT_HOME
cd ${LIGHT_HOME}
PATH=${JAVA_HOME}:${PATH}
JAVA_OPTS=" ${JAVA_OPTS} -Xms384m -Xmx1024m"
JAVA_OPTS=" ${JAVA_OPTS} -Dlight.config=${LIGHT_CONFIG}"
JAVA_OPTS=" ${JAVA_OPTS} -Dlog4j.configurationFile=${LIGHT_LOG_CONFIG}"
JAVA_OPTS=" ${JAVA_OPTS} -Dfile.encoding=UTF-8"

LIGHT_CONSOLE_JAR=${LIGHT_HOME}/libs/light.console-0.0.1.jar

java ${JAVA_OPTS} -jar ${LIGHT_CONSOLE_JAR}
