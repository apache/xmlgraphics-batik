/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.font.AltGlyphHandler;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.SVGGVTGlyphVector;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.util.SVGConstants;

import java.awt.font.FontRenderContext;
import org.w3c.dom.Element;

/**
 * SVG font altGlyph handler.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGAltGlyphHandler implements AltGlyphHandler, SVGConstants {

    BridgeContext ctx;
    Element textElement;

    public SVGAltGlyphHandler(BridgeContext ctx, Element textElement) {
        this.ctx = ctx;
        this.textElement = textElement;
    }

    public GVTGlyphVector createGlyphVector(FontRenderContext frc, float fontSize) {
        if (textElement.getTagName().equals(SVG_ALT_GLYPH_TAG)) {
            SVGAltGlyphElementBridge altGlyphBridge = (SVGAltGlyphElementBridge)ctx.getBridge(textElement);
            Glyph[] glyphArray = altGlyphBridge.createAltGlyphArray(ctx, textElement, fontSize);
            if (glyphArray != null) {
                return new SVGGVTGlyphVector(null, glyphArray, frc);
            }
        }
        return null;
    }
}

