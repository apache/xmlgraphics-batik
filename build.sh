#!/bin/sh
# -----------------------------------------------------------------------------
# build.sh - Unix Build Script for Apache Batik
#
# $Id: build.sh,v 1.10.2.13 2000/09/28 19:53:04 rossb Exp $
# -----------------------------------------------------------------------------

# ----- Verify and Set Required Environment Variables -------------------------
   
if [ "$ANT_HOME" = "" ] ; then
  ANT_HOME=./lib
fi

if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

# ----- Set Up The Runtime Classpath ------------------------------------------

CP=$JAVA_HOME/lib/tools.jar:$ANT_HOME/lib/ant_1_2.jar:./lib/crimson.jar:./lib/jaxp.jar
 
# ----- Make sure Ant script is executable ------------------------------------

chmod 0755 $ANT_HOME/bin/antRun

# ----- Execute The Requested Build -------------------------------------------

$JAVA_HOME/bin/java $ANT_OPTS -classpath $CP org.apache.tools.ant.Main -Dant.home=$ANT_HOME $*



