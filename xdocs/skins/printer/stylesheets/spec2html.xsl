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

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

<!-- ====================================================================== -->
<!-- inherit the document2html templates -->
<!-- ====================================================================== -->

 <xsl:import href="document2html.xsl"/>

<!-- ====================================================================== -->
<!-- header -->
<!-- ====================================================================== -->

 <xsl:template match="header">
   <table class="header">
    <tr>
     <td><b>Authors</b></td>
    </tr>
    <xsl:for-each select="authors/person">
     <tr>
      <td><b><xsl:value-of select="@name"/></b> - <xsl:value-of select="@email"/></td>
     </tr>
    </xsl:for-each>
    <tr>
     <td>
      <b>Status</b>
     </td>
    </tr>
    <tr>
     <td><b><xsl:value-of select="type"/> - <xsl:value-of select="version"/></b></td>
    </tr>
    <tr>
     <td><b>Notice</b></td>
    </tr>
    <tr>
     <td><xsl:value-of select="notice"/></td>
    </tr>
    <tr>
     <td><b>Abstract</b></td>
    </tr>
    <tr>
     <td><xsl:value-of select="abstract"/></td>
    </tr>
   </table>
 </xsl:template>

<!-- ====================================================================== -->
<!-- appendices section -->
<!-- ====================================================================== -->

 <xsl:template match="appendices">
  <xsl:apply-templates/>
 </xsl:template>

<!-- ====================================================================== -->
<!-- bibliography -->
<!-- ====================================================================== -->

 <xsl:template match="bl">
  <ul class="biblio">
   <xsl:apply-templates/>
  </ul>
 </xsl:template>

 <xsl:template match="bi">
  <li class="biblio">
   <b>
    <xsl:text>[</xsl:text>
     <a href="{@href}"><xsl:value-of select="@name"/></a>
    <xsl:text>]</xsl:text>
   </b>
   <xsl:text> &quot;</xsl:text>
   <xsl:value-of select="@title"/>
   <xsl:text>&quot;, </xsl:text>
   <xsl:value-of select="@authors"/>
   <xsl:if test="@date">
    <xsl:text>, </xsl:text>
    <xsl:value-of select="@date"/>
   </xsl:if>
  </li>
 </xsl:template>

</xsl:stylesheet>
