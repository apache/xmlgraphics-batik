/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;

/**
 * A font family class for AWT fonts.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class AWTFontFamily implements GVTFontFamily {

    protected GVTFontFace fontFace;
    protected Font   font;

    /**
     * Constructs an AWTFontFamily with the specified familyName.
     *
     * @param familyName The name of the font family.
     */
    public AWTFontFamily(GVTFontFace fontFace) {
        this.fontFace = fontFace;
    }

    /**
     * Constructs an AWTFontFamily with the specified familyName.
     *
     * @param familyName The name of the font family.
     */
    public AWTFontFamily(String familyName) {
        this(new GVTFontFace(familyName));
    }

    /**
     * Constructs an AWTFontFamily with the specified familyName.
     *
     * @param familyName The name of the font family.
     */
    public AWTFontFamily(GVTFontFace fontFace, Font font) {
        this.fontFace = fontFace;
        this.font     = font;
    }

    /**
     * Returns the font family name.
     *
     * @return The family name.
     */
    public String getFamilyName() {
        return fontFace.getFamilyName();
    }

    /**
     * Returns the font-face information for this font family.
     */
    public GVTFontFace getFontFace() {
        return fontFace;
    }

    /**
     * Derives a GVTFont object of the correct size.
     *
     * @param size The required size of the derived font.
     * @param aci  The character iterator that will be rendered using
     *             the derived font.  
     */
    public GVTFont deriveFont(float size, AttributedCharacterIterator aci) {
        if (font != null)
            return new AWTGVTFont(font, size);

        HashMap fontAttributes = new HashMap(aci.getAttributes());
        fontAttributes.put(TextAttribute.SIZE, new Float(size));
        fontAttributes.put(TextAttribute.FAMILY, fontFace.getFamilyName());
        fontAttributes.remove(GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
        return new AWTGVTFont(fontAttributes);
    }

}
