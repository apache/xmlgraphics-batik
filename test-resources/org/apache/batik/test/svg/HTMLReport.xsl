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
                <xsl:apply-templates/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="testReport">
        <table width="600" border="0" cellpadding="0" cellspacing="0" hspace="0" vspace="0">
          <tr bgcolor="black"><td>
	          <table width="600" border="0" cellpadding="0" cellspacing="1" hspace="0" vspace="0">
            <tr bgcolor="#cccccc">
                <td><b><xsl:value-of select="@testName" /></b></td>
            </tr>
            <tr bgcolor="white">
                <td><xsl:value-of select="@status" /></td>
            </tr>
            <tr bgcolor="white">
                <td><xsl:apply-templates/></td>
            </tr>
                </table>
          </td></tr>
        </table>
    </xsl:template>

    <xsl:template match="description">
        <table width="600" border="0" cellpadding="0" cellspacing="1" hspace="0" vspace="0">
            <xsl:apply-templates />
        </table>
    </xsl:template>

    <xsl:template match="genericEntry">
        <tr>
            <td><xsl:value-of select="@key" /></td>
            <td><xsl:value-of select="@value" /></td>
        </tr>
    </xsl:template>

    <xsl:template match="uriEntry">
        <tr>
            <td><xsl:value-of select="@key" /></td>
            <xsl:variable name="value" select="@value" />
            <td><a href="{$value}"><img height="150" src="{$value}" /></a></td>
        </tr>
    </xsl:template>

    <xsl:template match="fileEntry">
        <tr>
            <td><xsl:value-of select="@key" /></td>
            <xsl:variable name="value" select="@value" />
            <td><a href="{$value}"><img height="150" src="{$value}" /></a></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>