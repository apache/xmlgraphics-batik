<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/XSL/Transform/1.0">

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
        <p class="legal">Cocoon Documentation</p>
        <h1 class="title"><xsl:value-of select="document/header/title"/></h1>
        <xsl:apply-templates/>
        <p class="legal">Copyright &#169; <xsl:value-of select="$copyright"/>.<br/>All rights reserved.</p>
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
   <pre><xsl:apply-templates/></pre>
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
   <xsl:value-of select="following::dd"/>
  </li>
 </xsl:template>

 <xsl:template match="dd">
  <!-- ignore since already used -->
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