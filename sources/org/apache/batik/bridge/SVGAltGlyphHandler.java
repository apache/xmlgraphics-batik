/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;

import org.apache.batik.gvt.font.AltGlyphHandler;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.font.SVGGVTGlyphVector;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

/**
 * SVG font altGlyph handler. This class handles the creation of an alternate
 * GVTGlyphVector for the altGlyph element.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGAltGlyphHandler implements AltGlyphHandler, SVGConstants {

    private BridgeContext ctx;
    private Element textElement;

    /**
     * Constructs an SVGAltGlyphHandler.
     *
     * @param ctx The bridge context, this is needed during rendering to find
     * any referenced glyph elements.
     * @param textElement The element that contains text to be replaced by the
     * alternate glyphs. This should be an altGlyph element.
     */
    public SVGAltGlyphHandler(BridgeContext ctx, Element textElement) {
        this.ctx = ctx;
        this.textElement = textElement;
    }

    /**
     * Creates a glyph vector containing the alternate glyphs.
     *
     * @param frc The current font render context.
     * @param fontSize The required font size.
     * @return The GVTGlyphVector containing the alternate glyphs, or null if
     * the alternate glyphs could not be found.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc, float fontSize,
                                     AttributedCharacterIterator aci) {
        try {
            if (textElement.getTagName().equals(SVG_ALT_GLYPH_TAG)) {
                SVGAltGlyphElementBridge altGlyphBridge
                    = (SVGAltGlyphElementBridge)ctx.getBridge(textElement);
                Glyph[] glyphArray
                    = altGlyphBridge.createAltGlyphArray(ctx, textElement, fontSize, aci);
                if (glyphArray != null) {
                    return new SVGGVTGlyphVector(null, glyphArray, frc);
                }
            }
        } catch (SecurityException e) {
            ctx.getUserAgent().displayError(e);
            // Throw exception because we do not want to continue
            // processing. In the case of a SecurityException, the 
            // end user would get a lot of exception like this one.
            throw e;
        }

        return null;
    }
}

