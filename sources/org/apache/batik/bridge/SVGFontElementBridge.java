/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Bridge class for the &lt;font> element.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGFontElementBridge extends AbstractSVGBridge {

    /**
     * Constructs a new bridge for the &lt;font> element.
     */
    public SVGFontElementBridge() {
    }

    /**
     * Returns 'font'.
     */
    public String getLocalName() {
        return SVG_FONT_TAG;
    }

    /**
     * Constructs a new SVGGVTFont that represents the specified &lt;font> element
     * at the requested size.
     *
     * @param ctx The current bridge context.
     * @param fontElement The font element to base the SVGGVTFont construction on.
     * @param textElement The textElement that will use the new font.
     * @param size The size of the new font.
     * @param fontFace The font face object that contains the font attributes.
     *
     * @return The new SVGGVTFont.
     */
    public SVGGVTFont createFont(BridgeContext ctx,
                                 Element fontElement,
                                 Element textElement,
                                 float size,
                                 SVGFontFace fontFace) {


        // construct a list of glyph codes that this font can display and
        // a list of the glyph elements
        NodeList glyphElements = fontElement.getElementsByTagName(SVG_GLYPH_TAG);
        int numGlyphs = glyphElements.getLength();
        String[] glyphCodes = new String[numGlyphs];
        String[] glyphNames = new String[numGlyphs];
        Element[] glyphElementArray = new Element[numGlyphs];

        for (int i = 0; i < numGlyphs; i++) {
            Element glyphElement = (Element)glyphElements.item(i);
            glyphCodes[i] = glyphElement.getAttribute(SVG_UNICODE_ATTRIBUTE);
            glyphNames[i] = glyphElement.getAttribute(SVG_GLYPH_NAME_ATTRIBUTE);
            glyphElementArray[i] = glyphElement;
        }

        NodeList missingGlyphElements = fontElement.getElementsByTagName(SVG_MISSING_GLYPH_TAG);
        Element missingGlyphElement = null;
        if (missingGlyphElements.getLength() > 0) {
            missingGlyphElement = (Element)missingGlyphElements.item(0);
        }

        NodeList hkernElements = fontElement.getElementsByTagName(SVG_HKERN_TAG);
        Element[] hkernElementArray = new Element[hkernElements.getLength()];

        for (int i = 0; i < hkernElementArray.length; i++) {
            Element hkernElement = (Element)hkernElements.item(i);
            hkernElementArray[i] = hkernElement;
        }

        NodeList vkernElements = fontElement.getElementsByTagName(SVG_VKERN_TAG);
        Element[] vkernElementArray = new Element[vkernElements.getLength()];

        for (int i = 0; i < vkernElementArray.length; i++) {
            Element vkernElement = (Element)vkernElements.item(i);
            vkernElementArray[i] = vkernElement;
        }

        return new SVGGVTFont(size, fontFace, glyphCodes, glyphNames, ctx,
                              glyphElementArray, missingGlyphElement,
                              hkernElementArray, vkernElementArray, textElement);
    }
}
