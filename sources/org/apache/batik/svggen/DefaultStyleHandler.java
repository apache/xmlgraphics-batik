/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import org.apache.batik.util.SVGConstants;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Element;

/**
 * The <code>DefaultStyleHandler</code> class provides the default
 * way to style an SVG <code>Element</code>.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class DefaultStyleHandler implements StyleHandler, SVGConstants {
    /**
     * Static initializer for which attributes should be ignored on
     * some elements.
     */
    static HashMap ignoreAttributes = new HashMap();

    static {
        Vector textAttributes = new Vector();
        textAttributes.addElement(SVG_FONT_SIZE_ATTRIBUTE);
        textAttributes.addElement(SVG_FONT_FAMILY_ATTRIBUTE);
        textAttributes.addElement(SVG_FONT_STYLE_ATTRIBUTE);
        textAttributes.addElement(SVG_FONT_WEIGHT_ATTRIBUTE);

        ignoreAttributes.put(SVG_RECT_TAG, textAttributes);
        ignoreAttributes.put(SVG_CIRCLE_TAG, textAttributes);
        ignoreAttributes.put(SVG_ELLIPSE_TAG, textAttributes);
        ignoreAttributes.put(SVG_POLYGON_TAG, textAttributes);
        ignoreAttributes.put(SVG_POLYGON_TAG, textAttributes);
        ignoreAttributes.put(SVG_LINE_TAG, textAttributes);
        ignoreAttributes.put(SVG_PATH_TAG, textAttributes);
    }

    /**
     * Sets the style described by <code>styleMap</code> on the given
     * <code>element</code>. That is sets the xml attributes with their
     * styled value.
     * @param element the SVG <code>Element</code> to be styled.
     * @param styleMap the <code>Map</code> containing pairs of style
     * property names, style values.
     */
    public void setStyle(Element element, Map styleMap,
                         SVGGeneratorContext generatorContext) {
        String tagName = element.getTagName();
        Iterator iter = styleMap.keySet().iterator();
        String styleName = null;
        while (iter.hasNext()) {
            styleName = (String)iter.next();
            if (element.getAttributeNS(null, styleName).length() == 0){
                if (appliesTo(styleName, tagName)) {
                    element.setAttributeNS(null, styleName,
                                           (String)styleMap.get(styleName));
                }
            }
        }
    }

    /**
     * Controls whether or not a given attribute applies to a particular 
     * element.
     */
    protected boolean appliesTo(String styleName, String tagName) {
        Vector v = (Vector)ignoreAttributes.get(tagName);
        if (v == null) {
            return true;
        } else {
            return !v.contains(styleName);
        }
    }
}
