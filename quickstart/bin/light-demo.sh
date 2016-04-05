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
JAVA_OPTS=" ${JAVA_OPTS} -javaagent:${LIGHT_HOME}/light.agent-0.0.1.jar"
JAVA_OPTS=" ${JAVA_OPTS} -Xms384m -Xmx1024m"
JAVA_OPTS=" ${JAVA_OPTS} -Dlight.config=${LIGHT_CONFIG}"
JAVA_OPTS=" ${JAVA_OPTS} -Dlog4j.configurationFile=${LIGHT_LOG_CONFIG}"
JAVA_OPTS=" ${JAVA_OPTS} -Dfile.encoding=UTF-8"

LIGHT_DEMO_JAR=${LIGHT_HOME}/light.demo-0.0.1.jar

if [ "$1" = "start" ]; then
        java ${JAVA_OPTS} -jar ${LIGHT_DEMO_JAR} start > /dev/null 2>&1 &
        echo "Starting Light demo is requested. For more information, see the log files."
elif [ "$1" = "run" ]; then
        java ${JAVA_OPTS} -jar ${LIGHT_DEMO_JAR} start
elif [ "$1" = "stop" ]; then
        java ${JAVA_OPTS} -jar ${LIGHT_DEMO_JAR} stop > /dev/null 2>&1 &
        echo "Stopping Light demo is requested. For more information, see the log files."
else
        echo "Usage: light-demo.sh [command]"
        echo "Available command : start, stop"
fi