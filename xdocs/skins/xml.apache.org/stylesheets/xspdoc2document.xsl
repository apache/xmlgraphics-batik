<?xml version="1.0"?>

<xsl:stylesheet 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xspdoc="http://apache.org/cocoon/XSPDoc/v1"
 version="1.0"
>

 <xsl:template match="xspdoc:desc">
  <dt><xsl:value-of select="following-sibling::xsl:template/@match"/></dt>
  <dd><xsl:apply-templates select="text()"/></dd>
 </xsl:template>
 
 <xsl:template match="/xsl:stylesheet">
  <document>
   <header>
    <title><xsl:apply-templates select="xspdoc:title"/></title>
   </header>
   <body>
    <s2 title="Template Descriptions">
     <dl>
      <xsl:apply-templates select="xspdoc:desc"/>
     </dl>
    </s2>
   </body>
  </document>
 </xsl:template>

 <xsl:template match="*"/>

</xsl:stylesheet>
