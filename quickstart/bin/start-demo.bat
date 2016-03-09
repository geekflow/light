@echo off

rem ----------------------------------------------------------------------
rem ----- Customizable Variables -----------------------------------------
rem ----------------------------------------------------------------------
rem set JAVA_HOME=
set LIGHT_HOME=%~dp0
rem set LIGHT_CONFIG=%LIGHT_HOME%\config/light.conf
rem set LIGHT_LOG_CONFIG=%LIGHT_HOME%\config/log4j2.xml
rem ----------------------------------------------------------------------

rem ----------------------------------------------------------------------
rem ----- Do not touch below this line!-----------------------------------
rem ----------------------------------------------------------------------
cd %LIGHT_HOME%\bin
rem set PATH=%JAVA_HOME%\bin;%PATH%
set JAVA_OPTS=%JAVA_OPTS% -javaagent:%LIGHT_HOME%\light.agent-0.0.1.jar
set JAVA_OPTS=%JAVA_OPTS% -Xms256m -Xmx512m
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8

java %JAVA_OPTS% -jar light.demo-0.0.1.jar