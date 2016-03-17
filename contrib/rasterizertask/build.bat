@ECHO OFF
:: ----------------------------------------------------------------------------
::
::   Licensed to the Apache Software Foundation (ASF) under one or more
::   contributor license agreements.  See the NOTICE file distributed with
::   this work for additional information regarding copyright ownership.
::   The ASF licenses this file to You under the Apache License, Version 2.0
::   (the "License"); you may not use this file except in compliance with
::   the License.  You may obtain a copy of the License at
::
::       http://www.apache.org/licenses/LICENSE-2.0
::
::   Unless required by applicable law or agreed to in writing, software
::   distributed under the License is distributed on an "AS IS" BASIS,
::   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
::   See the License for the specific language governing permissions and
::   limitations under the License.
::
:: build.bat - MS-DOS build script for rasterizer task
::
:: $Id$
:: --------------------------------------------------------------------------

:: ----- Verify and set required environment variables ----------------------

IF NOT "%JAVA_HOME%" == "" GOTO gotJavaHome
ECHO You must set JAVA_HOME to point at your JDK installation.
GOTO cleanup
:gotJavaHome

IF NOT "%ANT_HOME%" == "" GOTO gotAntHome
SET ANT_HOME=..\..
:gotAntHome

SET BATIK_HOME=.\..\..


:: ----- Set up classpath ---------------------------------------------------

SET CP="%JAVA_HOME%\lib\tools.jar";"%ANT_HOME%\lib\build\ant-1.6.5.jar";"%ANT_HOME%\lib\build\ant-launcher-1.6.5.jar";"%BATIK_HOME%\lib\build\crimson-1.1.3.jar"
SET CP=%CP%;%BATIK_HOME%\classes


:: ----- Execute ------------------------------------------------------------

"%JAVA_HOME%\bin\java.exe" %ANT_OPTS% -classpath %CP% org.apache.tools.ant.Main -Dant.home=%ANT_HOME% %1 -Dargs="%2 %3 %4 %5 %6 %7 %8 %9"


:: ----- Cleanup the environment --------------------------------------------

:cleanup
SET BATIK_HOME=
SET CP=

