/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.w3c.dom.Element;
import java.text.AttributedCharacterIterator;

/**
 * A font family class for SVG fonts.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGFontFamily implements GVTFontFamily {

    protected SVGFontFace fontFace;
    protected Element fontElement;
    protected BridgeContext ctx;

    /**
     * Constructs an SVGFontFamily.
     *
     * @param fontFace The font face object that describes this font family.
     * @param fontElement The element that contains the font data for this family.
     * @param ctx The bridge context. This is required for lazily loading the
     * font data at render time.
     */
    public SVGFontFamily(SVGFontFace fontFace,
                         Element fontElement,
                         BridgeContext ctx) {
        this.fontFace = fontFace;
        this.fontElement = fontElement;
        this.ctx = ctx;
    }

    /**
     * Returns the family name of this font.
     *
     * @return The font family name.
     */
    public String getFamilyName() {
        return fontFace.getFamilyName();
    }

    /**
     * Returns the font-face associated with this font family.
     *
     * @return The font face.
     */
    public SVGFontFace getFontFace() {
        return fontFace;
    }

    /**
     * Derives a GVTFont object of the correct size.
     *
     * @param size The required size of the derived font.
     * @param aci The character iterator containing the text to be rendered
     * using the derived font.
     *
     * @return The derived font.
     */
    public GVTFont deriveFont(float size, AttributedCharacterIterator aci) {
        SVGFontElementBridge fontBridge
               = (SVGFontElementBridge)ctx.getBridge(fontElement);
        Element textElement = (Element)aci.getAttributes().get(
            GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
        return fontBridge.createFont(ctx, fontElement, textElement, size, fontFace);
    }
}