<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

  <xsl:param name="label"/>

  <xsl:template match="/">
    <xsl:variable name="label">
      <xsl:if test="//header/title">
        <xsl:value-of select="//header/title"/>
      </xsl:if>
    </xsl:variable>
    
    <image width="456" height="35" bgcolor="0086b2">
      <text font="Arial" size="29" x="454" y="8" halign="right" valign="top" color="004080" text="{$label}"/>
      <text font="Arial" size="29" x="452" y="6" halign="right" valign="top" color="ffffff" text="{$label}"/>
    </image>
  </xsl:template>

</xsl:stylesheet>
