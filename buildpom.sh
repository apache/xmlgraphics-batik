#!/bin/bash
# See buildpoms script. This is an auxiliar script used by buildpoms.sh.
desc="Batik module description"
case "$1" in
'batik-anim')
desc="Batik animation engine"
;;
'batik-awt-util')
desc="Batik AWT utilities"
;;
'batik-bridge')
desc="Batik bridge classes"
;;
'batik-codec')
desc="Batik image codecs"
;;
'batik-css')
desc="Batik CSS engine"
;;
'batik-dom')
desc="Batik DOM implementation"
;;
'batik-extension')
desc="Batik extension classes"
;;
'batik-ext')
desc="Batik external code"
;;
'batik-gui-util')
desc="Batik GUI utility classes"
;;
'batik-gvt')
desc="Batik GVT (Graphics Vector Tree)"
;;
'batik-parser')
desc="Batik SVG microsyntax parser library"
;;
'batik-rasterizer-ext')
desc="Batik SVG rasterizer application with extensions"
;;
'batik-rasterizer')
desc="Batik SVG rasterizer application"
;;
'batik-script')
desc="Batik scripting language classes"
;;
'batik-slideshow')
desc="Batik SVG slideshow application"
;;
'batik-squiggle-ext')
desc="Batik SVG browser application with extensions"
;;
'batik-squiggle')
desc="Batik SVG browser application"
;;
'batik-svg-dom')
desc="Batik SVG DOM implementation"
;;
'batik-svggen')
desc="Batik Java2D SVG generator"
;;
'batik-svgpp')
desc="Batik SVG pretty-printer application"
;;
'batik-swing')
desc="Batik SVG Swing components"
;;
'batik-transcoder')
desc="Batik SVG transcoder classes"
;;
'batik-ttf2svg')
desc="Batik TrueType Font to SVG Font converter application"
;;
'batik-util')
desc="Batik utility library"
;;
'batik-xml')
desc="Batik XML utility library"
;;
*)
desc="Batik module description"
;;
esac
echo '<?xml version="1.0"?>
<!--

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

-->

<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

  <modelVersion>4.0.0</modelVersion>
  <groupId>org.apache.xmlgraphics</groupId>
  <artifactId>'$1'</artifactId>
  <version>@version@</version>
  <packaging>jar</packaging>
  <name>'$desc'</name>
  <url>http://xmlgraphics.apache.org/batik/</url>
  <inceptionYear>2000</inceptionYear>

  <mailingLists>
    <mailingList>
      <name>Batik Users List</name>
      <subscribe>batik-users-subscribe@xmlgraphics.apache.org</subscribe>
      <unsubscribe>batik-users-unsubscribe@xmlgraphics.apache.org</unsubscribe>
      <archive>http://mail-archives.apache.org/mod_mbox/xmlgraphics-batik-users/</archive>
    </mailingList>
    <mailingList>
      <name>Batik Developer List</name>
      <subscribe>batik-dev-subscribe@xmlgraphics.apache.org</subscribe>
      <unsubscribe>batik-dev-unsubscribe@xmlgraphics.apache.org</unsubscribe>
      <archive>http://mail-archives.apache.org/mod_mbox/xmlgraphics-batik-dev/</archive>
    </mailingList>
    <mailingList>
      <name>Batik Commit List</name>
      <subscribe>batik-commits-subscribe@xmlgraphics.apache.org</subscribe>
      <unsubscribe>batik-commits-unsubscribe@xmlgraphics.apache.org</unsubscribe>
      <archive>http://mail-archives.apache.org/mod_mbox/xmlgraphics-batik-commits/</archive>
    </mailingList>
  </mailingLists>

  <licenses>
    <license>
      <name>The Apache Software License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>

  <scm>
    <connection>scm:svn:http://svn.apache.org/repos/asf/xmlgraphics/batik/trunk</connection>
    <developerConnection>scm:svn:https://svn.apache.org/repos/asf/xmlgraphics/batik/trunk</developerConnection>
    <url>http://svn.apache.org/viewvc/xmlgraphics/batik/trunk/?root=Apache-SVN</url>
  </scm>

  <organization>
    <name>Apache Software Foundation</name>
    <url>http://www.apache.org/</url>
  </organization>

  <dependencies>'
grep jar checkdeps/dot/$1-svn-trunk.jar.dot | grep -v java | grep org.apache.batik | awk '{print $4}' | sort -u | sed s/xalan-2.7.0/xalan/g | sed s/-1.3.04//g | sed s/'-svn-trunk.jar)";'//g | sed s/'.jar)";'//g | sed s/'('//g | awk '{version = "@version@"; groupId = "org.apache.xmlgraphics"; if ($1 == "xml-apis" || $1 == "xml-apis-ext") {version = "1.3.04"; groupId = "xml-apis"} else if ($1 == "xmlgraphics-commons") {version = "@xgcVersion@"} else if ($1 == "xalan") {version = "2.7.0"; groupId = "xalan";}; printf("    <dependency>\n      <groupId>%s</groupId>\n      <artifactId>%s</artifactId>\n      <version>%s</version>\n    </dependency>\n", groupId, $1, version);}'
echo '  </dependencies>
</project>'

