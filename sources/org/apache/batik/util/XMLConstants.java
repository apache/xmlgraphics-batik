/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

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
