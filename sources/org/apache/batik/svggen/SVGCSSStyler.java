/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.util.Vector;

import org.w3c.dom.*;

/**
 * This utility class converts a standard SVG document that uses
 * attribute into one that uses the CSS style attribute instead
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGCSSStyler implements SVGSyntax{
    static private String CSS_PROPERTY_VALUE_SEPARATOR = ":";
    static private String CSS_RULE_SEPARATOR = ";";
    static private String SPACE = " ";

    /**
     * Invoking this method removes all the styling attributes
     * (such as 'fill' or 'fill-opacity') from the input element
     * and its descendant and replaces them with their CSS2
     * property counterparts.
     * @param node SVG Node to be converted to use style
     *
     */
    public static void style(Node node){
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null){
            // Has to be an Element, as it has attributes
            // According to spec.
            Element element = (Element)node;
            StringBuffer styleAttrBuffer = new StringBuffer();
            int nAttr = attributes.getLength();
            Vector toBeRemoved = new Vector();
            for(int i=0; i<nAttr; i++){
                Attr attr = (Attr)attributes.item(i);
                if(SVGStylingAttributes.set.contains(attr.getName())){
                    // System.out.println("Found new style attribute");
                    styleAttrBuffer.append(attr.getName());
                    styleAttrBuffer.append(CSS_PROPERTY_VALUE_SEPARATOR);
                    styleAttrBuffer.append(attr.getValue());
                    styleAttrBuffer.append(CSS_RULE_SEPARATOR);
                    styleAttrBuffer.append(SPACE);
                    toBeRemoved.addElement(attr.getName());
                }
            }

            if(styleAttrBuffer.length() > 0){
                                // System.out.println("Setting style attribute on node: " + styleAttrBuffer.toString().trim());
                                // There were some styling attributes
                element.setAttributeNS(SVG_NAMESPACE_URI, ATTR_STYLE, styleAttrBuffer.toString().trim());

                int n = toBeRemoved.size();
                for(int i=0; i<n; i++)
                    element.removeAttribute((String)toBeRemoved.elementAt(i));
            }
            // else
            // System.out.println("NO STYLE PROPERTIES");
        }

        // Now, process child elements
        NodeList children = node.getChildNodes();
        int nChildren = children.getLength();
        for(int i=0; i<nChildren; i++){
            Node child = children.item(i);
            style(child);
        }

    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception{
        SVGGraphics2D g = new SVGGraphics2D(TestUtil.getDocumentPrototype());
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                           java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        // Text
        g.setPaint(new java.awt.Color(103, 103, 152));
        g.fillRect(10, 10, 200, 50);
        g.setPaint(java.awt.Color.white);
        g.setFont(new java.awt.Font("SunSansCondensed-Heavy", java.awt.Font.PLAIN, 20));
        g.drawString("Hello Java 2D to SVG", 40, 40);

        g.stream(new java.io.OutputStreamWriter(System.out));
    }
}
