#!/bin/sh
# -----------------------------------------------------------------------------
# build.sh - Unix Build Script for Apache Batik
#
# $Id$
# -----------------------------------------------------------------------------

# ----- Verify and Set Required Environment Variables -------------------------
   
if [ "$ANT_HOME" = "" ] ; then
  ANT_HOME=.
fi

if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

# ----- Set Up The Runtime Classpath ------------------------------------------

CP=$JAVA_HOME/lib/tools.jar:$ANT_HOME/lib/build/ant_1_3.jar:./lib/build/parser.jar:./lib/build/jaxp.jar
 
# ----- Execute The Requested Build -------------------------------------------

TARGET=$1;
shift 1

$JAVA_HOME/bin/java $ANT_OPTS -classpath $CP org.apache.tools.ant.Main -Dant.home=$ANT_HOME $TARGET -Dargs="$*"

