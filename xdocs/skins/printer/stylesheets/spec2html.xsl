<?xml version="1.0"?>

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
