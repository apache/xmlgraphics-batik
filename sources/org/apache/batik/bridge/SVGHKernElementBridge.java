/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.font.HKern;
import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;hkern> element.
 *
 * @author <a href="mailto:dean.jackson@cmis.csiro.au">Dean Jackson</a>
 * @version $Id$
 */
public class SVGHKernElementBridge extends AbstractSVGBridge {

    /**
     * Constructs a new bridge for the &lt;hkern> element.
     */
    public SVGHKernElementBridge() {}

    /**
     * Returns 'hkern'.
     */
    public String getLocalName() {
        return SVG_HKERN_TAG;
    }

    public HKern createHKern(BridgeContext ctx,
                             Element hkernElement,
                             SVGFontFace fontFace,
                             SVGGVTFont svgGvtFont) {

        String g1 = hkernElement.getAttributeNS(null, SVG_G1_ATTRIBUTE);
        String g2 = hkernElement.getAttributeNS(null, SVG_G2_ATTRIBUTE);
        String k = hkernElement.getAttributeNS(null, SVG_K_ATTRIBUTE);
        if (k.length() == 0) {
            k = SVG_HKERN_K_DEFAULT_VALUE;
        }

        char character1 = svgGvtFont.unicodeForName(g1);
        char character2 = svgGvtFont.unicodeForName(g2);
        float kern = Float.valueOf(k).floatValue();

        return new HKern(new char[]{character1}, new char[]{character2}, kern);
    }
}
