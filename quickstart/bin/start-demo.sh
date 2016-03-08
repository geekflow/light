#!/bin/sh

LIGHT_HOME=${PWD}/../.
JAVA_OPTS="$JAVA_OPTS -javaagent:${LIGHT_HOME}/light.agent-0.0.1.jar

export LIGHT_HOME
export JAVA_OPTS

java $JAVA_OPTS -jar light.demo-0.0.1.jar