/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.List;

import org.apache.batik.gvt.font.GVTFontFamily;
import org.w3c.dom.Element;

/**
 * This class represents a &lt;font-face> element or @font-face rule
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGFontFace extends FontFace {

    Element fontFaceElement;
    GVTFontFamily fontFamily = null;
    /**
     * Constructes an SVGFontFace with the specfied font-face attributes.
     */
    public SVGFontFace
        (Element fontFaceElement, List srcs,
         String familyName, float unitsPerEm, String fontWeight,
         String fontStyle, String fontVariant, String fontStretch,
         float slope, String panose1, float ascent, float descent,
         float strikethroughPosition, float strikethroughThickness,
         float underlinePosition, float underlineThickness,
         float overlinePosition, float overlineThickness) {
        super(srcs,
              familyName, unitsPerEm, fontWeight, 
              fontStyle, fontVariant, fontStretch, 
              slope, panose1, ascent, descent,
              strikethroughPosition, strikethroughThickness,
              underlinePosition, underlineThickness,
              overlinePosition, overlineThickness);
        this.fontFaceElement = fontFaceElement;
    }

    /**
     * Returns the font associated with this rule or element.
     */
    public GVTFontFamily getFontFamily(BridgeContext ctx) {
        if (fontFamily != null)
            return fontFamily;

        Element fontElt = SVGUtilities.getParentElement(fontFaceElement);
        if (fontElt.getNamespaceURI().equals(SVG_NAMESPACE_URI) &&
            fontElt.getLocalName().equals(SVG_FONT_TAG)) {
            return new SVGFontFamily(this, fontElt, ctx);
        }

        fontFamily = super.getFontFamily(ctx);
        return fontFamily;
    }

    public Element getFontFaceElement() {
        return fontFaceElement;
    }

    /**
     * Default implementation uses the root element of the document 
     * associated with BridgeContext.  This is useful for CSS case.
     */
    protected Element getBaseElement(BridgeContext ctx) {
        if (fontFaceElement != null) 
            return fontFaceElement;
        return super.getBaseElement(ctx);
    }

}
