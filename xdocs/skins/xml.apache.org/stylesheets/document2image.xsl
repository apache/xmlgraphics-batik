<?xml version="1.0"?>

<!--

   Copyright 2000-2001 The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

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
