<?xml version="1.0"?>

<!--

   Copyright 2000-2001 The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

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

 <xsl:import href="copyover.xsl"/>

 <xsl:param name="stylebook.project"/>
 <xsl:param name="copyright"/>
 <xsl:param name="id"/>

<!-- ====================================================================== -->
<!-- document section -->
<!-- ====================================================================== -->

 <xsl:template match="/">
  <!-- checks if this is the included document to avoid neverending loop -->
  <xsl:if test="not(book)">
    <html>
      <head>
        <title><xsl:value-of select="document/header/title"/></title>
        <link rel="stylesheet" type="text/css" href="resources/simple.css" title="Simple Style"/>
      </head>
      <body>

        <!-- THE MAIN PANEL (SIDEBAR AND CONTENT) -->
        <table id="main-panel">
          <tr>
            <!-- THE SIDE BAR -->
            <td id="side-bar" valign="top">
              <xsl:apply-templates select="document($stylebook.project)"/>
            </td>
            <!-- THE CONTENT PANEL -->
            <td id="content-panel">
              <xsl:apply-templates/>
            </td>
          </tr>
        </table>
      </body>
    </html>
   </xsl:if>
   
   <xsl:if test="book">
    <xsl:apply-templates/>
   </xsl:if>
  </xsl:template>

<!-- ====================================================================== -->
<!-- book section -->
<!-- ====================================================================== -->

  <xsl:template match="page|faqs|changes|todo|spec">
    <xsl:if test="@id=$id">
      <xsl:value-of select="@label"/>
    </xsl:if>
    <xsl:if test="@id!=$id">
      <a href="{@id}.html"><xsl:value-of select="@label"/></a>
    </xsl:if>
    <br/>
  </xsl:template>

  <xsl:template match="external">
    <a href="{@href}"><xsl:value-of select="@label"/></a><br/>
  </xsl:template>

  <xsl:template match="separator">
    <hr/>
  </xsl:template>
  
<!-- ====================================================================== -->
<!-- header section -->
<!-- ====================================================================== -->

 <xsl:template match="header">
  <!-- ignore on general document -->
 </xsl:template>

<!-- ====================================================================== -->
<!-- body section -->
<!-- ====================================================================== -->

  <xsl:template match="s1">
   <h1><xsl:value-of select="@title"/></h1>
	<div id="s1"><xsl:apply-templates/></div>
  </xsl:template>

  <xsl:template match="s2">
   <h2><xsl:value-of select="@title"/></h2>
	<div id="s2"><xsl:apply-templates/></div>
  </xsl:template>

  <xsl:template match="s3">
   <h3><xsl:value-of select="@title"/></h3>
	<div id="s3"><xsl:apply-templates/></div>
  </xsl:template>

  <xsl:template match="s4">
   <h4><xsl:value-of select="@title"/></h4>
	<div id="s4"><xsl:apply-templates/></div>
  </xsl:template>

<!-- ====================================================================== -->
<!-- footer section -->
<!-- ====================================================================== -->

 <xsl:template match="footer">
  <!-- ignore on general documents -->
 </xsl:template>

<!-- ====================================================================== -->
<!-- paragraph section -->
<!-- ====================================================================== -->

  <xsl:template match="note">
   <p class="note"><xsl:apply-templates/></p>
  </xsl:template>

  <xsl:template match="source">
   <pre class="source"><xsl:apply-templates/></pre>
  </xsl:template>

  <xsl:template match="fixme">
   <!-- ignore on documentation -->
  </xsl:template>

<!-- ====================================================================== -->
<!-- list section -->
<!-- ====================================================================== -->

 <xsl:template match="sl">
  <ul>
   <xsl:apply-templates/>
  </ul>
 </xsl:template>

 <xsl:template match="dt">
  <li>
   <strong><xsl:value-of select="."/></strong>
   <xsl:text> - </xsl:text>
   <xsl:apply-templates select="dd"/>   
  </li>
 </xsl:template>
 
<!-- ====================================================================== -->
<!-- table section -->
<!-- ====================================================================== -->

<!-- since we cloned the XHTML model, we don't need any futher styling      -->

<!-- ====================================================================== -->
<!-- markup section -->
<!-- ====================================================================== -->

<!-- since we cloned the XHTML model, we don't need any futher styling      -->
 
<!-- ====================================================================== -->
<!-- images section -->
<!-- ====================================================================== -->

 <xsl:template match="figure|img|icon">
  <img src="{@src}" alt="{@alt}" class="{name(.)}"/>
 </xsl:template>
 
<!-- ====================================================================== -->
<!-- links section -->
<!-- ====================================================================== -->

 <xsl:template match="link">
   <a href="{@href}"><xsl:apply-templates/></a>
 </xsl:template>

 <xsl:template match="connect">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="jump">
   <a href="{@href}#{@anchor}"><xsl:apply-templates/></a>
 </xsl:template>

 <xsl:template match="fork">
   <a href="{@href}" target="_blank"><xsl:apply-templates/></a>
 </xsl:template>

 <xsl:template match="anchor">
   <a name="{@id}"><xsl:comment>anchor</xsl:comment></a>
 </xsl:template>  

</xsl:stylesheet>