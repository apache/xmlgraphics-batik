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
                <link rel="stylesheet" type="text/css" media="screen" href="../../style/style.css" />
            </head>

            <body style="background-image: url(../../images/background.png);">

        <h1>Regard Test Report -- 
            <xsl:value-of select="count(/descendant::testReport[@status='passed'])" />/<xsl:value-of select="count(/descendant::testReport)" />
        </h1>

        <hr noshade="noshade" size="1" width="600" align="left"/>

        <!-- ======= -->
        <!-- Summary -->
        <!-- ======= -->
        <xsl:call-template name="summary" />
        &#160;<br />

        <!-- ======= -->
        <!-- Details -->
        <!-- ======= -->
        <xsl:call-template name="details" />

            </body>
        </html>

    </xsl:template>

    <xsl:template name="details">

        <h2>Report Details</h2>

        <xsl:apply-templates/>

    </xsl:template>

    <xsl:template name="summary">
    
        <h2>Failed Leaf Tests:</h2>

        <xsl:call-template name="failedTestsLinks">
            <xsl:with-param name="failedNodes" select="/descendant::testReport[@status='failed']" />
        </xsl:call-template>

        <hr noshade="noshade" size="1" width="600" align="left" />

    </xsl:template>

    <xsl:template name="failedTestsLinks">
        <xsl:param name="failedNodes" />
        <ol>
        <xsl:for-each select="$failedNodes">
            <li>
                <a>
                    <xsl:attribute name="href">#<xsl:value-of select="@id" /></xsl:attribute>
                    <xsl:value-of select="@testName" />
                </a>  

            </li>
        </xsl:for-each>
        </ol>                   


    </xsl:template>

    <xsl:template match="testReport | testSuiteReport">
        <xsl:variable name="childrenTests" select="description/testReport" />  

        <xsl:variable name="childrenTestSuites" select="description/testSuiteReport" />
        <xsl:variable name="childrenTestsCount" select="count($childrenTests) + count($childrenTestSuites)" />

        <a>
            <xsl:attribute name="name">
                <xsl:value-of select="@id" />
            </xsl:attribute>
        </a>

        
        <xsl:choose>
        <xsl:when test="$childrenTestsCount = 0 and @status='failed'">
                    <table bgcolor="black" vspace="0" hspace="0" cellspacing="0" cellpadding="0" border="0" width="600">
                    <tr><td>
                    <table bgcolor="black" vspace="0" hspace="0" cellspacing="1" cellpadding="2" border="0" width="600">
                        <tr bgcolor="#eeeeee">     
                            <td colspan="2"><img align="bottom" src="../../images/deco.png" width="16" height="16" />&#160;

                                <font><xsl:attribute name="class">title<xsl:value-of select="@status"/></xsl:attribute>&#160;<xsl:value-of select="@testName" /></font>

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

                                    -- &#160;<xsl:value-of select=" count($passedChildrenTests) + count($passedChildrenTestSuites)" /> / 
                                    <xsl:value-of select="$childrenTestsCount" />
                                    </xsl:when>
                                </xsl:choose>

                            </td>
                        </tr>
                        <xsl:apply-templates />
                    </table></td></tr></table>
                        <br />

        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates />
        </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xsl:template match="description">
        <xsl:apply-templates select="genericEntry | uriEntry | fileEntry" />

        <xsl:apply-templates select="testReport | testSuiteReport">
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="genericEntry">
        <tr bgcolor="white">
            <td><xsl:value-of select="@key" /></td>
            <td><xsl:value-of select="@value" /></td>
        </tr>
    </xsl:template>

    <xsl:template match="uriEntry">
        <tr bgcolor="white" margin-left="50pt">
            <td><xsl:value-of select="@key" /></td>
            <xsl:variable name="value" select="@value" />
            <td><a target="image" href="{$value}"><img height="150" src="{$value}" /></a></td>
        </tr>
    </xsl:template>

    <xsl:template match="fileEntry">
        <tr bgcolor="white">
            <td><xsl:value-of select="@key" /></td>
            <xsl:variable name="value" select="@value" />
            <td><a target="image" href="{$value}"><img height="150" src="{$value}" /></a></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>