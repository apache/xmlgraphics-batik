@echo off
:: ----------------------------------------------------------------------------
:: Copyright 2001 The Apache Software Foundation
:: 
::    Licensed under the Apache License, Version 2.0 (the "License");
::    you may not use this file except in compliance with the License.
::    You may obtain a copy of the License at
:: 
::        http://www.apache.org/licenses/LICENSE-2.0
:: 
::    Unless required by applicable law or agreed to in writing, software
::    distributed under the License is distributed on an "AS IS" BASIS,
::    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
::    See the License for the specific language governing permissions and
::    limitations under the License.
:: 
:: ----------------------------------------------------------------------------

if '%1'=='' goto usage

set LOCALCLASSPATH=.
for %%1 in (..\..\lib\build\xalan*.jar) do call lcp.bat %%1
for %%1 in (..\..\lib\build\xerces*.jar) do call lcp.bat %%1

echo Using classpath: %LOCALCLASSPATH%

java -classpath %LOCALCLASSPATH% -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl org.apache.xalan.xslt.Process -IN xml\%1.xml -XSL xsl\%2.xsl -OUT out\%1-%2.svg -EDUMP
goto end

:usage
echo.
echo Usage:   convert.bat xml-filename xslt-stylesheet
echo.
echo Example: convert.bat data bar
echo     Will convert file xml\data.xml with stylesheet xsl\bar.xsl into out\data-bar.svg

:end
echo.
echo have a nice day ;-)
