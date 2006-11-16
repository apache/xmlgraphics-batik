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
<!-- Generate a column graph (bars up the ways)                             -->
<!--                                                                        -->
<!-- @author john.r.morrison@ntworld.com                                    -->
<!-- @version $Id$                                                          -->
<!-- ====================================================================== -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:variable name="height" select="graph/meta/column/height"/>
<xsl:variable name="width"  select="graph/meta/column/width"/>

<xsl:variable name="margin">10</xsl:variable>
<xsl:variable name="labelWidthY">50</xsl:variable>
<xsl:variable name="labelWidthX">20</xsl:variable>

<xsl:variable name="chartHeight" select="$height - (2 * $margin) - $labelWidthX"/>
<xsl:variable name="chartWidth"  select="$width  - (2 * $margin) - $labelWidthY"/>

<xsl:variable name="barWidth" select="1 div count(//set)"/>

<xsl:variable name="verticalRangeMax">
  <xsl:for-each select="//y">
    <xsl:sort order="descending" data-type="number" select="text()"/>
    <xsl:if test="position() = 1"><xsl:value-of select="."/></xsl:if>
  </xsl:for-each>
</xsl:variable>

<xsl:variable name="horizontalRangeMax">
  <xsl:for-each select="//x">
    <xsl:sort order="descending" data-type="number" select="text()"/>
    <xsl:if test="position() = 1"><xsl:value-of select="text() + 1"/></xsl:if>
  </xsl:for-each>
</xsl:variable>

<xsl:variable name="verticalScale">
  <xsl:value-of select="$chartHeight div $verticalRangeMax"/>
</xsl:variable>

<xsl:variable name="horizontalScale">
  <xsl:value-of select="$chartWidth  div $horizontalRangeMax"/>
</xsl:variable>

<xsl:template match="graph">
  <svg
    width="{$width}"
    height="{$height}">

    <g transform="matrix(1 0 0 -1 {$margin + $labelWidthY} {$height - ($margin + $labelWidthX)})">
      <xsl:call-template name="verticalGridlines">
        <xsl:with-param name="value">0</xsl:with-param>
        <xsl:with-param name="step">1</xsl:with-param>
      </xsl:call-template>

      <xsl:call-template name="horizontalGridlines">
        <xsl:with-param name="value">0</xsl:with-param>
        <xsl:with-param name="step">20000</xsl:with-param>
      </xsl:call-template>

      <xsl:apply-templates select="data/*"/>
    </g>
  </svg>
</xsl:template>

<xsl:template name="verticalGridlines">
  <xsl:param name="value">0</xsl:param>
  <xsl:param name="step"/>

  <line
    id="verticalGridline_{$value}"
    x1="{$value * $horizontalScale}"
    y1="-2"
    x2="{$value * $horizontalScale}"
    y2="{$verticalRangeMax * $verticalScale}"
    style="fill:none;stroke:black;stroke-width:1"/>

  <text
    transform="matrix(1 0 0 -1 0 -{$labelWidthX})"
    x="{$value * $horizontalScale}"
    y="0"
    style="font-size:12;fill:black">
    <xsl:value-of select="//meta/axis/x/value[position() = $value + 1]"/>
  </text>

  <xsl:if test="$value + $step &lt;= $horizontalRangeMax">
    <xsl:call-template name="verticalGridlines">
      <xsl:with-param name="value"><xsl:value-of select="$value + $step"/></xsl:with-param>
      <xsl:with-param name="step"><xsl:value-of select="$step"/></xsl:with-param>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<xsl:template name="horizontalGridlines">
  <xsl:param name="value">0</xsl:param>
  <xsl:param name="step"/>

  <line
    id="horizontalGridline_{$value}"
    x1="-2"
    y1="{$value * $verticalScale}"
    x2="{$horizontalRangeMax * $horizontalScale}"
    y2="{$value * $verticalScale}"
    style="fill:none;stroke:black;stroke-width:1"/>

  <text
    transform="matrix(1 0 0 -1 -{$margin} -5)"
    x="0"
    y="-{$value * $verticalScale}"
    style="text-anchor:end;font-size:12;fill:black">
    <xsl:value-of select="$value"/>
  </text>

  <xsl:if test="$value + $step &lt;= $verticalRangeMax">
    <xsl:call-template name="horizontalGridlines">
      <xsl:with-param name="value"><xsl:value-of select="$value + $step"/></xsl:with-param>
      <xsl:with-param name="step"><xsl:value-of select="$step"/></xsl:with-param>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<xsl:template match="set">
  <xsl:variable name="red"   select="colour/red/text()"/>
  <xsl:variable name="green" select="colour/green/text()"/>
  <xsl:variable name="blue"  select="colour/blue/text()"/>

  <xsl:variable name="label" select="label"/>
  <xsl:variable name="col"   select="position() - 1"/>

  <xsl:for-each select="values/*">
    <rect
      id="{$label}-{generate-id(.)}"
      x="{(x + ($barWidth * $col)) * $horizontalScale}"
      y="0"
      width="{$barWidth * $horizontalScale}"
      height="{y * $verticalScale}"
      rx="0"
      ry="0"
      style="stroke-width:1;stroke:rgb(0,0,0);fill:rgb({$red},{$green},{$blue})"/>
  </xsl:for-each>

</xsl:template>

</xsl:stylesheet>
