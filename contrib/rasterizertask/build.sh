#!/bin/sh
# -----------------------------------------------------------------------------
#
#   Licensed to the Apache Software Foundation (ASF) under one or more
#   contributor license agreements.  See the NOTICE file distributed with
#   this work for additional information regarding copyright ownership.
#   The ASF licenses this file to You under the Apache License, Version 2.0
#   (the "License"); you may not use this file except in compliance with
#   the License.  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
#
# build.sh - Unix Build Script for Rasterizer Task
#
# $Id$
# --------------------------------------------------------------------------

# ----- Verify and Set Required Environment Variables -------------------------
   
if [ "$ANT_HOME" = "" ] ; then
  ANT_HOME=../..
fi

if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
esac

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
fi


BATIK_HOME=../..


# ----- Set up classpath ---------------------------------------------------

CP=$JAVA_HOME/lib/tools.jar:$ANT_HOME/lib/build/ant-1.6.5.jar:$ANT_HOME/lib/build/ant-launcher-1.6.5.jar:$BATIK_HOME/lib/build/crimson-1.1.3.jar

CP=$CP:$BATIK_HOME/classes

if $cygwin; then
  JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
  CP=`cygpath --path --windows "$CP"`
fi

# ----- Execute The Requested Build -------------------------------------------

TARGET=$1;
if [ $# != 0 ] ; then
  shift 1
fi

"$JAVA_HOME/bin/java" $ANT_OPTS -classpath "$CP" org.apache.tools.ant.Main -emacs -Dant.home=$ANT_HOME $TARGET -Dargs="$*"


# ----- Cleanup the environment --------------------------------------------


BATIK_HOME=
CP=

