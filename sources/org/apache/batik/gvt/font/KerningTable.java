/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;


/**
 * The KerningTable class holds a kerning table (a collection of Kern
 * elements). It provides a more convenient method of looking up
 * kerning values when laying out glyphs.
 *
 * @author <a href="mailto:dean.jackson@cmis.csiro.au">Dean Jackson</a>
 * @version $Id$
 */
public class KerningTable {

    private Kern[] entries;

    /**
     * Creates a KerningTable from an array of Kern entries.
     *
     * @param entries The array of Kern objects that represent the kerning
     * entries for the font that this kerning table belongs to.
     */

    public KerningTable(Kern[] entries) {
        this.entries = entries;
    }

    /**
     * Returns the amount of kerning that should be added between the
     * given glyphs. Returns 0 if the glyphs should not be kerned.
     *
     * @param glyphCode1 The id of the first glyph in the kerning pair
     * @param glyphCode2 The id of the second glyph in the kerning pair
     * @param glyphUnicode1 The unicode value of the first glyph in the kerning pair
     * @param glyphUnicode2 The unicode vlaue of the second glyph in the kerning pair
     * @return The amount of kerning to be added when laying out the glyphs.
     */

    public float getKerningValue(int glyphCode1, int glyphCode2,
                                 String glyphUnicode1, String glyphUnicode2) {
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].matchesFirstGlyph(glyphCode1, glyphUnicode1) &&
                entries[i].matchesSecondGlyph(glyphCode2, glyphUnicode2)) {
                return entries[i].getAdjustValue();
            }
        }
        return 0f;
    }

}
