#!/bin/sh

if [ "$1" = "" ] ; then
  echo
  echo "Usage  : ./convert.sh xml-filename xslt-stylesheet"
  echo
  echo "Example: ./convert.sh data bar"
  echo "    Will convert file xml/data.xml with stylesheet xsl/bar.xsl into out/data-bar.svg"
  exit 1
fi

if [ "$JAVA_HOME" = "" ] ; then
  echo "ERROR: JAVA_HOME not found in your environment."
  echo
  echo "Please, set the JAVA_HOME variable in your environment to match the"
  echo "location of the Java Virtual Machine you want to use."
  exit 1
fi

LOCALCLASSPATH=.
for i in ../../lib/build/xalan*.jar ; do
  LOCALCLASSPATH=${LOCALCLASSPATH}:$i
done
for i in ../../lib/build/xerces*.jar ; do
  LOCALCLASSPATH=${LOCALCLASSPATH}:$i
done

echo "Using classpath: $LOCALCLASSPATH"

echo "$JAVA_HOME/bin/java -classpath $LOCALCLASSPATH -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl org.apache.xalan.xslt.Process -IN xml/$1.xml -XSL xsl/$2.xsl -OUT out/$1-$2.svg -EDUMP"
$JAVA_HOME/bin/java -classpath $LOCALCLASSPATH -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl org.apache.xalan.xslt.Process -IN xml/$1.xml -XSL xsl/$2.xsl -OUT out/$1-$2.svg -EDUMP

echo
echo "have a nice day ;-)"
