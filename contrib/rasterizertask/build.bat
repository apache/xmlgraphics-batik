@ECHO OFF
REM --------------------------------------------------------------------------
REM  Copyright (C) The Apache Software Foundation. All rights reserved.        
REM  ------------------------------------------------------------------------- 
REM  This software is published under the terms of the Apache Software License 
REM  version 1.1, a copy of which has been included with this distribution in  
REM  the LICENSE file.                                                         
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

