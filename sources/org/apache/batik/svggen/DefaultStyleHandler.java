/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * The <code>DefaultStyleHandler</code> class provides the default
 * way to style an SVG <code>Element</code>.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class DefaultStyleHandler implements StyleHandler {
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
            if (element.getAttributeNS(null, styleName).length() == 0)
                element.setAttributeNS(null, styleName,
                                       (String)styleMap.get(styleName));
        }
    }
}
