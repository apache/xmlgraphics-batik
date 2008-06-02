<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="/">
    <document>
      <xsl:apply-templates select="*/node()"/>
    </document>
  </xsl:template>
 
  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="tests">
    <table class="tests">
      <tr class="tests-header">
        <td rowspan="{count(test) + 1}">
          <xsl:attribute name="class">
            <xsl:text>vertical-bar </xsl:text>
            <xsl:choose>
              <xsl:when test="@status"><xsl:value-of select="@status"/></xsl:when>
              <xsl:when test="yes">yes</xsl:when>
              <xsl:when test="partial">partial</xsl:when>
              <xsl:when test="no">no</xsl:when>
              <xsl:when test="not(*/yes) and not(*/partial) and */no">no</xsl:when>
              <xsl:when test="*/partial or */partial and */no or */yes and */no">partial</xsl:when>
              <xsl:otherwise>yes</xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </td>
        <th>
          SVG 1.1 test suite
        </th>
        <th/>
        <th/>
      </tr>
      <xsl:if test="notes">
        <tr class="section-note">
          <td></td>
          <td></td>
          <td>
            <xsl:apply-templates select="notes"/>
          </td>
        </tr>
      </xsl:if>
      <xsl:apply-templates>
        <xsl:sort select="@name"/>
      </xsl:apply-templates>
    </table>
  </xsl:template>

  <xsl:template match="elements | interfaces">
    <table class="elements">
      <xsl:apply-templates>
        <xsl:sort select="@ns"/>
        <xsl:sort select="@name"/>
      </xsl:apply-templates>
    </table>
  </xsl:template>

  <xsl:template match="object">
    <div class="object">
      <div class="object-header">
        <xsl:value-of select="@name"/>
      </div>
      <xsl:if test="prop">
        <div class="object-section-header">Properties</div>
        <xsl:apply-templates select="prop">
          <xsl:sort select="@name"/>
        </xsl:apply-templates>
      </xsl:if>
      <xsl:if test="func">
        <div class="object-section-header">Function properties</div>
        <xsl:apply-templates select="func">
          <xsl:sort select="@name"/>
        </xsl:apply-templates>
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template match="object/prop">
    <div class="object-property-header">
      <xsl:value-of select="@name"/>
    </div>
    <div class="object-property-desc">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="object/func">
    <div class="object-property-header">
      <xsl:value-of select="@name"/>
      <span class="object-property-func-params">
        <xsl:text>(</xsl:text>
        <xsl:value-of select="@params"/>
        <xsl:text>)</xsl:text>
      </span>
    </div>
    <div class="object-property-desc">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="element | interface">
    <tr class="element-header">
      <td rowspan="{count(attr | prop | op) + number(boolean(attr)) + number(boolean(prop)) + number(boolean(op)) + number(boolean(notes))+ 1}">
        <xsl:attribute name="class">
          <xsl:text>vertical-bar </xsl:text>
          <xsl:choose>
            <xsl:when test="@status"><xsl:value-of select="@status"/></xsl:when>
            <xsl:when test="yes">yes</xsl:when>
            <xsl:when test="partial">partial</xsl:when>
            <xsl:when test="no">no</xsl:when>
            <xsl:when test="not(*/yes) and not(*/partial) and */no">no</xsl:when>
            <xsl:when test="*/partial or */partial and */no or */yes and */no">partial</xsl:when>
            <xsl:otherwise>yes</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </td>
      <th>
        <xsl:variable name="element-name">
          <xsl:call-template name="qname">
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="ns" select="string(@ns)"/>
            <xsl:with-param name="prefixes" select="../prefixes"/>
          </xsl:call-template>
        </xsl:variable>
        <a name="{$element-name}"/>
        <xsl:value-of select="$element-name"/>
      </th>
      <th/>
      <th/>
    </tr>
    <xsl:if test="notes">
      <tr class="section-note">
        <td></td>
        <td></td>
        <td>
          <xsl:apply-templates select="notes"/>
        </td>
      </tr>
    </xsl:if>
    <xsl:if test="attr">
      <tr class="section-header">
        <th colspan="3">Attributes</th>
      </tr>
      <xsl:apply-templates select="attr">
        <xsl:sort select="@ns"/>
        <xsl:sort select="@name"/>
      </xsl:apply-templates>
    </xsl:if>
    <xsl:if test="prop">
      <tr class="section-header">
        <th colspan="3">Properties</th>
      </tr>
      <xsl:apply-templates select="prop"/>
    </xsl:if>
    <xsl:if test="op">
      <tr class="section-header">
        <th colspan="3">Operations</th>
      </tr>
      <xsl:apply-templates select="op"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="attr | element/prop | op | test">
    <tr class="attribute">
      <th class="attribute-name">
        <xsl:choose>
          <xsl:when test="local-name() = 'attr' and parent::element">
            <xsl:call-template name="qname">
              <xsl:with-param name="name" select="@name"/>
              <xsl:with-param name="ns" select="@ns"/>
              <xsl:with-param name="prefixes" select="../../prefixes"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test='self::test'>
            <a href='{../@uri}{@name}.svg'><xsl:value-of select='@name'/></a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@name"/>
          </xsl:otherwise>
        </xsl:choose>
      </th>
      <xsl:choose>
        <xsl:when test="yes"><td class="yes">yes</td></xsl:when>
        <xsl:when test="partial"><td class="partial">partial</td></xsl:when>
        <xsl:otherwise><td class="no">no</td></xsl:otherwise>
      </xsl:choose>
      <td class="attribute-note">
        <xsl:apply-templates select="*/node()"/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="prefixes"/>

  <xsl:template name="qname">
    <xsl:param name="name"/>
    <xsl:param name="ns"/>
    <xsl:param name="prefixes"/>
    <xsl:choose>
      <xsl:when test="$ns = ''">
        <xsl:value-of select="$name"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="prefix" select="$prefixes/prefix[@ns=$ns][1]/@prefix"/>
        <xsl:choose>
          <xsl:when test="$prefix = ''">
            <xsl:value-of select="concat('{', $ns, '}', $name)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="concat($prefix, ':', $name)"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="yes | partial | no">
    <span class="{local-name()}"><xsl:apply-templates/></span>
  </xsl:template>
</xsl:stylesheet>
