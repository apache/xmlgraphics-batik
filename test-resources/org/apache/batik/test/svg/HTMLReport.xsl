<?xml version="1.0" standalone='no'?>
<!-- ========================================================================= -->
<!-- Copyright (C) The Apache Software Foundation. All rights reserved.        -->
<!--                                                                           -->
<!-- This software is published under the terms of the Apache Software License -->
<!-- version 1.1, a copy of which has been included with this distribution in  -->
<!-- the LICENSE file.                                                         -->
<!-- ========================================================================= -->

<!-- ========================================================================= -->
<!-- @author vincent.hardy@eng.sun.com                                         -->
<!-- @version $Id$ -->
<!-- ========================================================================= -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xlink="http://www.w3.org/2000/xlink/namespace/" >

    <xsl:template match="/">
        <html>
            <head>
            </head>

            <body>
        <table width="600" border="0" cellpadding="0" cellspacing="0" hspace="0" vspace="0">
          <tr bgcolor="black"><td>
	          <table width="600" border="0" cellpadding="1" cellspacing="1" hspace="0" vspace="0">

                <xsl:apply-templates/>
                </table>
          </td></tr>
        </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="testReport | testSuiteReport">
        <xsl:param name="prefix" />
        <xsl:variable name="childrenTests" select="description/testReport" />
        <xsl:variable name="childrenTestSuites" select="description/testSuiteReport" />
        <xsl:variable name="childrenTestsCount" select="count($childrenTests) + count($childrenTestSuites)" />

        <a>
            <xsl:attribute name="href">
                <xsl:value-of select="@id" />
            </xsl:attribute>
        </a>

        <xsl:choose>
        <xsl:when test="$childrenTestsCount &gt; 0 or @status='failed'">
            <tr bgcolor="#cccccc">
                <td colspan="2"><b><xsl:value-of select="$prefix" /><xsl:value-of select="@testName" /></b></td>
            </tr>
            <tr bgcolor="white">
                <td colspan="2">
                    <xsl:value-of select="$prefix" />

                    <!-- Plain Status -->
                    <xsl:value-of select="@status" />

                    <xsl:choose>
                        <xsl:when test="@status='failed'">
                            &#160;(<xsl:value-of select="@errorCode" />)
                        </xsl:when>
                    </xsl:choose>

                    <!-- If this is a composite report, add counts of success/failures -->
                    <xsl:choose>
                        <xsl:when test="$childrenTestsCount &gt; 0" >
                            <xsl:variable name="passedChildrenTests" 
                                          select="description/testReport[attribute::status='passed']" />
                            <xsl:variable name="passedChildrenTestSuites" 
                                          select="description/testSuiteReport[attribute::status='passed']" />

                            -- Success Rate :&#160;<xsl:value-of select=" count($passedChildrenTests) + count($passedChildrenTestSuites)" /> / 
                            <xsl:value-of select="$childrenTestsCount" />
                        </xsl:when>
                    </xsl:choose>
                </td>
            </tr>

            <tr bgcolor="white">
                <td><xsl:apply-templates>
                        <xsl:with-param name="prefix" select="$prefix"/>
                    </xsl:apply-templates></td>
            </tr>
        </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="description">
        <xsl:param name="prefix">&#160;</xsl:param>
        <xsl:apply-templates select="genericEntry | uriEntry | fileEntry">
            <xsl:with-param name="prefix" select="$prefix" />
        </xsl:apply-templates>
        <xsl:apply-templates select="testReport | testSuiteReport">
            <xsl:with-param name="prefix">
                <xsl:value-of select="$prefix"/>&#160;&#160;&#160;
            </xsl:with-param>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="genericEntry">
        <xsl:param name="prefix">&#160;</xsl:param>
        <tr bgcolor="white">
            <td><xsl:value-of select="$prefix" /><xsl:value-of select="@key" /></td>
            <td><xsl:value-of select="@value" /></td>
        </tr>
    </xsl:template>

    <xsl:template match="uriEntry">
        <xsl:param name="prefix">&#160;</xsl:param>
        <tr bgcolor="white" margin-left="50pt">
            <td><xsl:value-of select="$prefix" /><xsl:value-of select="@key" /></td>
            <xsl:variable name="value" select="@value" />
            <td><a target="image" href="{$value}"><img height="150" src="{$value}" /></a></td>
        </tr>
    </xsl:template>

    <xsl:template match="fileEntry">
        <xsl:param name="prefix">&#160;</xsl:param>
        <tr bgcolor="white">
            <td><xsl:value-of select="$prefix" /><xsl:value-of select="@key" /></td>
            <xsl:variable name="value" select="@value" />
            <td><a target="image" href="{$value}"><img height="150" src="{$value}" /></a></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>