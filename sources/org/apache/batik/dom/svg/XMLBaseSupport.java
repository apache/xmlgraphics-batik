/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.net.URL;

import org.apache.batik.css.engine.CSSImportedElementRoot;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.XMLConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class provides support for the xml:base attribute.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class XMLBaseSupport implements XMLConstants {
    
    /**
     * This class does not need to be instanciated.
     */
    protected XMLBaseSupport() {
    }

    /**
     * Returns the xml:base attribute value of the given element.
     */
    public static String getXMLBase(Element elt) {
        return elt.getAttributeNS(XML_NAMESPACE_URI, "base");
    }

    /**
     * Returns the xml:base attribute value of the given element
     * Resolving any dependency on parent bases if needed.
     */
    public static String getCascadedXMLBase(Element elt) {
        String base = null;
        Node n = elt.getParentNode();
        while (n != null) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                base = getCascadedXMLBase((Element)n);
                break;
            }
            if (n instanceof CSSImportedElementRoot) {
                n = ((CSSImportedElementRoot)n).getCSSParentElement();
            } else {
                n = n.getParentNode();
            }
        }
        if (base == null) {
            SVGOMDocument svgDoc;
            svgDoc = (SVGOMDocument)elt.getOwnerDocument();
            URL url = svgDoc.getURLObject();
            if (url != null) {
                base = url.toString();
            }
        }
        Attr attr = elt.getAttributeNodeNS(XML_NAMESPACE_URI, "base");
        if (attr != null) {
            if (base == null) {
                base = attr.getNodeValue();
            } else {
                base = new ParsedURL(base, attr.getNodeValue()).toString();
            }
        }
        return base;
    }

}
