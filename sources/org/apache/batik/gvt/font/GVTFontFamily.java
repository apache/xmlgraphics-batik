/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

import java.text.AttributedCharacterIterator;

/**
 * An interface for all font family classes.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public interface GVTFontFamily {

    /**
     * Returns the font family name.
     *
     * @return The family name.
     */
    String getFamilyName();

    /**
     * Returns the FontFace for this fontFamily instance.
     */
    GVTFontFace getFontFace();

    /**
     * Derives a GVTFont object of the correct size.
     *
     * @param size The required size of the derived font.
     * @param aci The character iterator that will be rendered using
     * the derived font.
     */
    GVTFont deriveFont(float size, AttributedCharacterIterator aci);

}
