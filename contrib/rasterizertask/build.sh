#!/bin/sh
# --------------------------------------------------------------------------
# build.sh - build script for rasterizer task
#
#  $Id: build.sh
# --------------------------------------------------------------------------

# ----- Verify and Set Required Environment Variables -------------------------
   
if [ "$ANT_HOME" = "" ] ; then
  ANT_HOME=../..
fi

if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi



BATIK_HOME=../..


# ----- Set up classpath ---------------------------------------------------

CP=$JAVA_HOME/lib/tools.jar:$ANT_HOME/lib/build/ant_1_4_1.jar:$BATIK_HOME/lib/build/crimson-ant.jar:$BATIK_HOME/lib/build/jaxp.jar

CP=$CP:$BATIK_HOME/classes


# ----- Execute The Requested Build -------------------------------------------

TARGET=$1;
if [ $# != 0 ] ; then
  shift 1
fi

$JAVA_HOME/bin/java $ANT_OPTS -classpath $CP org.apache.tools.ant.Main -Dant.home=$ANT_HOME $TARGET -Dargs="$*"


# ----- Cleanup the environment --------------------------------------------


BATIK_HOME=
CP=

