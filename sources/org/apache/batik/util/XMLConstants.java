/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.util;

/**
 * Contains common XML constants.
 *
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface XMLConstants {
    /**
     * The XML namespace URI.
     */
    String XML_NAMESPACE_URI =
        "http://www.w3.org/XML/1998/namespace";

    /**
     * The xmlns namespace URI.
     */
    String XMLNS_NAMESPACE_URI =
        "http://www.w3.org/2000/xmlns/";

    /**
     * The xmlns prefix
     */
    String XMLNS_PREFIX = "xmlns";

    /**
     * The xlink namespace URI
     */
    String XLINK_NAMESPACE_URI
        = "http://www.w3.org/1999/xlink";
    
    /**
     * The xlink prefix
     */
    String XLINK_PREFIX = "xlink";

    String XML_PREFIX = "xml";
    String XML_LANG_ATTRIBUTE  = XML_PREFIX + ":lang";
    String XML_SPACE_ATTRIBUTE = XML_PREFIX + ":space";

    String XML_DEFAULT_VALUE = "default";
    String XML_PRESERVE_VALUE = "preserve";
    
    String XML_TAB = "    ";
    String XML_OPEN_TAG_END_CHILDREN = " >";
    String XML_OPEN_TAG_END_NO_CHILDREN = " />";
    String XML_OPEN_TAG_START = "<";
    String XML_CLOSE_TAG_START = "</";
    String XML_CLOSE_TAG_END = ">";
    String XML_SPACE = " ";
    String XML_EQUAL_SIGN = "=";
    String XML_EQUAL_QUOT = "=\"";
    String XML_DOUBLE_QUOTE = "\"";
    char XML_CHAR_QUOT = '\"';
    char XML_CHAR_LT = '<';
    char XML_CHAR_GT = '>';
    char XML_CHAR_APOS = '\'';
    char XML_CHAR_AMP = '&';
    String XML_ENTITY_QUOT = "&quot;";
    String XML_ENTITY_LT = "&lt;";
    String XML_ENTITY_GT = "&gt;";
    String XML_ENTITY_APOS = "&apos;";
    String XML_ENTITY_AMP = "&amp;";

    String XML_CHAR_REF_PREFIX = "&#x";
    String XML_CHAR_REF_SUFFIX = ";";
}
