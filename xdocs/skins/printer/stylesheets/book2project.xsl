<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- match the root book element -->
  <xsl:template match="book">
    <project>

      <parameter name="copyright" value="{@copyright}"/>
      <parameter name="name" value="{@software}"/>

      <!-- copy all resources to the targets -->
      <process source="sbk:/style/resources/" producer="directory">
        <processor name="xslt">
          <parameter name="stylesheet" value="sbk:/style/stylesheets/directory2project.xsl"/>
          <parameter name="base" value="resources/"/>
        </processor>
      </process>

      <xsl:apply-templates/>

    </project>
  </xsl:template>

  <xsl:template match="page|hidden">
    <process source="{@source}" producer="parser">
      <processor name="xslt">
        <parameter name="stylesheet" value="sbk:/style/stylesheets/scan4resources.xsl"/>
      </processor>
    </process>

    <create source="{@source}" target="{@id}.html" producer="parser" printer="html">
      <processor name="xslt">
        <parameter name="id" value="{@id}"/>
        <parameter name="stylesheet" value="sbk:/style/stylesheets/document2html.xsl"/>
      </processor>
    </create>
  </xsl:template>

  <xsl:template match="spec">
    <process source="{@source}" producer="parser">
      <processor name="xslt">
        <parameter name="stylesheet" value="sbk:/style/stylesheets/scan4resources.xsl"/>
      </processor>
    </process>

    <create source="{@source}" target="{@id}.html" producer="parser" printer="html">
      <processor name="xslt">
        <parameter name="id" value="{@id}"/>
        <parameter name="stylesheet" value="sbk:/style/stylesheets/spec2html.xsl"/>
      </processor>
    </create>
  </xsl:template>
  
  <xsl:template match="changes|faqs|todo">
    <process source="{@source}" producer="parser">
      <processor name="xslt">
        <parameter name="stylesheet" value="sbk:/style/stylesheets/{name(.)}2document.xsl"/>
      </processor>
      <processor name="xslt">
        <parameter name="stylesheet" value="sbk:/style/stylesheets/scan4resources.xsl"/>
      </processor>
    </process>

    <create source="{@source}" target="{@id}.html" producer="parser" printer="html">
      <processor name="xslt">
        <parameter name="stylesheet" value="sbk:/style/stylesheets/{name(.)}2document.xsl"/>
      </processor>
      <processor name="xslt">
        <parameter name="id" value="{@id}"/>
        <parameter name="stylesheet" value="sbk:/style/stylesheets/document2html.xsl"/>
      </processor>
    </create>
  </xsl:template>
</xsl:stylesheet>