/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;

/**
 * An interface for handling altGlyphs.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public interface AltGlyphHandler {

    /**
     * Creates a glyph vector containing the alternate glyphs.
     *
     * @param frc The current font render context.
     * @param fontSize The required font size.
     * @return The GVTGlyphVector containing the alternate glyphs, or null if
     * the alternate glyphs could not be found.
     */
    GVTGlyphVector createGlyphVector(FontRenderContext frc, float fontSize,
                                     AttributedCharacterIterator aci);

}
