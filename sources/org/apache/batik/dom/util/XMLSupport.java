/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.util;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class provides support for XML features.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class XMLSupport {
    /**
     * The XML namespace URI.
     */
    public final static String XML_NAMESPACE_URI =
	"http://www.w3.org/XML/1998/namespace";

    /**
     * The xmlns namespace URI.
     */
    public final static String XMLNS_NAMESPACE_URI =
	"http://www.w3.org/2000/xmlns/";

    /**
     * This class do not need to be instanciated.
     */
    protected XMLSupport() {
    }

    /**
     * Returns the xml:lang attribute value of the given element.
     */
    public static String getXMLLang(Element elt) {
	Attr attr = elt.getAttributeNodeNS(XML_NAMESPACE_URI, "lang");
	if (attr != null) {
	    return attr.getNodeValue();
	}
	for (Node n = elt.getParentNode(); n != null; n = n.getParentNode()) {
	    if (n.getNodeType() == Node.ELEMENT_NODE) {
		attr = ((Element)n).getAttributeNodeNS(XML_NAMESPACE_URI,
                                                       "lang");
		if (attr != null) {
		    return attr.getNodeValue();
		}
	    }
	}
	return "en";
    }

    /**
     * Sets the xml:lang attribute value of the given element.
     */
    public static void setXMLLang(Element elt, String lang) {
	elt.setAttributeNS(XML_NAMESPACE_URI, "lang", lang);
    }
    
    /**
     * Returns the xml:space attribute value of the given element.
     */
    public static String getXMLSpace(Element elt) {
	Attr attr = elt.getAttributeNodeNS(XML_NAMESPACE_URI, "space");
	if (attr != null) {
	    return attr.getNodeValue();
	}
	for (Node n = elt.getParentNode(); n != null; n = n.getParentNode()) {
	    if (n.getNodeType() == Node.ELEMENT_NODE) {
		attr = ((Element)n).getAttributeNodeNS(XML_NAMESPACE_URI,
                                                       "space");
		if (attr != null) {
		    return attr.getNodeValue();
		}
	    }
	}
	return "default";
    }

    /**
     * Sets the xml:space attribute value of the given element.
     */
    public static void setXMLSpace(Element elt, String space)
        throws DOMException {
	if (!"default".equals(space) && !"preserve".equals(space)) {
	    throw new DOMException(DOMException.SYNTAX_ERR,
				   "Invalid attribute Value: " + space);
	}
	elt.setAttributeNS(XML_NAMESPACE_URI, "space", space);
    }

    /**
     * Strips the white spaces in the given string according to the xml:space
     * attribute recommended behaviour when it has the 'default' value.
     */
    public static String defaultXMLSpace(String data) {
	String result = "";
	boolean space = false;
	for (int i = 0; i < data.length(); i++) {
	    char c = data.charAt(i);
	    switch (c) {
	    case 10:
	    case 13:
		break;
	    case ' ':
	    case '\t':
		if (!space) {
		    result += ' ';
		    space = true;
		}
		break;
	    default:
		result += c;
		space = false;
	    }
	}
	return result.trim();
    }

    /**
     * Strips the white spaces in the given string according to the xml:space
     * attribute recommended behaviour when it has the 'preserve' value.
     */
    public static String preserveXMLSpace(String data) {
	String result = "";
	for (int i = 0; i < data.length(); i++) {
	    char c = data.charAt(i);
	    switch (c) {
	    case 10:
	    case 13:
	    case '\t':
		result += ' ';
		break;
	    default:
		result += c;
	    }
	}
	return result;
    }
}
