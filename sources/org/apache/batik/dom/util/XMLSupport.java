/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.util;

import java.net.URL;

import org.apache.batik.util.XMLConstants;
import org.apache.batik.util.ParsedURL;

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

public class XMLSupport implements XMLConstants {
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
     * Returns the xml:base attribute value of the given element
     * Resolving any dependency on parent bases if needed.
     */
    public static String getXMLBase(Element elt) {
        String base = null;
        Node n = elt;
        while (true) {
            if (n.getParentNode() != null)
                n = n.getParentNode();
            else if (n instanceof org.apache.batik.css.HiddenChildElement)
                n = ((org.apache.batik.css.HiddenChildElement)n)
                    .getParentElement();
            else 
                break; 
                
            // new Exception("N: " + n).printStackTrace();
            if (n== null) break;
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                base = getXMLBase((Element)n);
                break;
            }
        }

        if (base == null) {
            // try to load the image as an svg document
            org.apache.batik.dom.svg.SVGOMDocument svgDoc;
            svgDoc = (org.apache.batik.dom.svg.SVGOMDocument)
                elt.getOwnerDocument();
            URL url = svgDoc.getURLObject();
            if (url != null)
                base = url.toString();
        }
        Attr attr = elt.getAttributeNodeNS(XML_NAMESPACE_URI, "base");
        if (attr != null) {
            // System.out.println("Base: " + base + 
            //                    " attr: " + attr.getNodeValue());
            if (base == null) 
                base = attr.getNodeValue();
            else 
                base = new ParsedURL(base, attr.getNodeValue()).toString();
        }
        return base;
    }

    public static void setXMLBase(Element elt, String base) {
        elt.setAttributeNS(XML_NAMESPACE_URI, "base", base);
    }
        

    /**
     * Strips the white spaces in the given string according to the xml:space
     * attribute recommended behaviour when it has the 'default' value.
     */
    public static String defaultXMLSpace(String data) {
	StringBuffer result = new StringBuffer();
	boolean space = false;
	for (int i = 0; i < data.length(); i++) {
	    char c = data.charAt(i);
	    switch (c) {
	    case 10:
	    case 13:
		space = false;
		break;
	    case ' ':
	    case '\t':
		if (!space) {
		    result.append(' ');
		    space = true;
		}
		break;
	    default:
		result.append(c);
		space = false;
	    }
	}
	return result.toString().trim();
    }

    /**
     * Strips the white spaces in the given string according to the xml:space
     * attribute recommended behaviour when it has the 'preserve' value.
     */
    public static String preserveXMLSpace(String data) {
	StringBuffer result = new StringBuffer();
	for (int i = 0; i < data.length(); i++) {
	    char c = data.charAt(i);
	    switch (c) {
	    case 10:
	    case 13:
	    case '\t':
		result.append(' ');
		break;
	    default:
		result.append(c);
	    }
	}
	return result.toString();
    }
}
