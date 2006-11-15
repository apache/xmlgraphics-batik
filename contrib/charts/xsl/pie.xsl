<?xml version="1.0"?>

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

<!-- ====================================================================== -->
<!-- Generate a simple pie chart.  Requires parser extensions for sin and   -->
<!-- cos.                                                                   -->
<!--                                                                        -->
<!-- @author john.r.morrison@ntworld.com                                    -->
<!-- @version $Id$                                                          -->
<!-- ====================================================================== -->

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:math="http://xml.apache.org/xslt/java">

<xsl:variable name="height" select="graph/meta/pie/height"/>
<xsl:variable name="width"  select="graph/meta/pie/width"/>

<xsl:variable name="radius">
  <xsl:choose>
    <xsl:when test="$height &lt; $width"><xsl:value-of select="2 * $height div 5"/></xsl:when>
    <xsl:otherwise><xsl:value-of select="2 * $width div 5"/></xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<xsl:variable name="360degrees" select="sum(/graph/data//y)"/>
<xsl:variable name="1degree"    select="360 div $360degrees"/>

<xsl:template match="graph">
  <svg
    width="{$width}"
    height="{$height}">

    <xsl:comment>radius      = <xsl:value-of select="$radius"/></xsl:comment>
    <xsl:comment>360 degrees = <xsl:value-of select="$360degrees"/></xsl:comment>
    <xsl:comment>1 degree    = <xsl:value-of select="$1degree"/></xsl:comment>

	  <g transform="matrix(1 0 0 1 {$width div 2} {$height div 2})">
      <xsl:apply-templates select="data/set"/>
	  </g>
  </svg>
</xsl:template>

<xsl:template match="set">
  
  <xsl:variable name="curpos" select="position()"/>    
  <xsl:variable name="angleStart" select="$1degree * sum(//set[position() &lt; $curpos]/values/*/y)"/>
  <xsl:variable name="angle" select="sum(descendant::y) * $1degree"/>

  <xsl:comment>angle start = <xsl:value-of select="$angleStart"/></xsl:comment>
  <xsl:comment>angle       = <xsl:value-of select="$angle"/></xsl:comment>
  <xsl:comment>angle end   = <xsl:value-of select="$angle + $angleStart"/></xsl:comment>

  <xsl:variable name="xystart">
    <xsl:call-template name="xy">
      <xsl:with-param name="angle" select="$angleStart"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="xyend">
    <xsl:call-template name="xy">
      <xsl:with-param name="angle" select="$angle + $angleStart"/>
    </xsl:call-template>
  </xsl:variable>

  <path
    d="M0, 0 L {$xystart} A {$radius}, {$radius} 0 0 1 {$xyend} z"
    style="fill:rgb({colour/red/text()},{colour/green/text()},{colour/blue/text()}); stroke-width:1"/>

</xsl:template>

<xsl:template name="xy">
  <xsl:param name="angle"/>

  <xsl:variable name="rad" select="math:java.lang.Math.toRadians($angle)"/>

  <xsl:value-of select="$radius * math:java.lang.Math.cos($rad)"/><xsl:text>, </xsl:text>
  <xsl:value-of select="$radius * math:java.lang.Math.sin($rad)"/>
</xsl:template>

</xsl:stylesheet>
