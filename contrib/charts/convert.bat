@echo off
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
