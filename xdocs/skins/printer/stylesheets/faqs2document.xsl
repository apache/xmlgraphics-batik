<?xml version="1.0"?>

<!--

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

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

  <xsl:template match="faqs">
   <document>
    <header>
     <title><xsl:value-of select="@title"/></title>
    </header>
    <body>
      <s1 title="Questions">
       <ul>
        <xsl:apply-templates select="faq" mode="index"/>
       </ul>
      </s1>
      <s1 title="Answers">
        <xsl:apply-templates select="faq"/>
      </s1>
    </body>
   </document>  
  </xsl:template>

  <xsl:template match="faq" mode="index">
    <li>
      <jump anchor="faq-{position()}">
        <xsl:value-of select="question"/>
      </jump>
    </li>
  </xsl:template>

  <xsl:template match="faq">
    <anchor id="faq-{position()}"/>
    <s2 title="{question}">
      <xsl:apply-templates/>
    </s2>
  </xsl:template>

  <xsl:template match="question">
    <!-- ignored since already used -->
  </xsl:template>

  <xsl:template match="answer">
    <xsl:apply-templates/>
  </xsl:template>

</xsl:stylesheet>