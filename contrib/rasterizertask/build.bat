@ECHO OFF
REM ----------------------------------------------------------------------------
REM Copyright 2001 The Apache Software Foundation
REM 
REM    Licensed under the Apache License, Version 2.0 (the "License");
REM    you may not use this file except in compliance with the License.
REM    You may obtain a copy of the License at
REM 
REM        http://www.apache.org/licenses/LICENSE-2.0
REM 
REM    Unless required by applicable law or agreed to in writing, software
REM    distributed under the License is distributed on an "AS IS" BASIS,
REM    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM    See the License for the specific language governing permissions and
REM    limitations under the License.
REM 
REM --------------------------------------------------------------------------

REM --------------------------------------------------------------------------
REM  build.bat - MS-DOS build script for rasterizer task
REM
REM  $Id$
REM --------------------------------------------------------------------------

REM ----- Verify and set required environment variables ----------------------

IF NOT "%JAVA_HOME%" == "" GOTO gotJavaHome
ECHO You must set JAVA_HOME to point at your JDK installation.
GOTO cleanup
:gotJavaHome

IF NOT "%ANT_HOME%" == "" GOTO gotAntHome
SET ANT_HOME=..\..
:gotAntHome

SET BATIK_HOME=.\..\..


REM ----- Set up classpath ---------------------------------------------------

SET CP=%JAVA_HOME%\lib\tools.jar;%ANT_HOME%\lib\build\ant_1_4_1.jar;%BATIK_HOME%\lib\build\crimson-ant.jar;%BATIK_HOME%\lib\build\jaxp.jar
SET CP=%CP%;%BATIK_HOME%\classes


REM ----- Execute ------------------------------------------------------------

%JAVA_HOME%\bin\java.exe %ANT_OPTS% -classpath %CP% org.apache.tools.ant.Main -Dant.home=%ANT_HOME% %1 -Dargs="%2 %3 %4 %5 %6 %7 %8 %9"


REM ----- Cleanup the environment --------------------------------------------

:cleanup
SET BATIK_HOME=
SET CP=

