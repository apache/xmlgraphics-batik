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
