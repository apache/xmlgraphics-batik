/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

/**
 * The Kern class describes an entry in the "kerning table". It provides
 * a kerning value to be used when laying out characters side
 * by side. It may be used for either horizontal or vertical kerning.
 *
 * @author <a href="mailto:dean.jackson@cmis.csiro.au">Dean Jackson</a>
 * @version $Id$
 */
public class Kern {

    private int[] firstGlyphCodes;
    private int[] secondGlyphCodes;
    private UnicodeRange[] firstUnicodeRanges;
    private UnicodeRange[] secondUnicodeRanges;
    private float kerningAdjust;

    /**
     * Creates a Kern object with the given glyph arrays
     * and kerning value. The first and second sets of glyphs for this kerning
     * entry consist of the union of glyphs in the glyph code arrays and the
     * unicode ranges.
     *
     * @param firstGlyphCodes An array of glyph codes that are part of the first
     * set of glyphs in this kerning entry.
     * @param secondGlyphCodes An array of glyph codes that are part of the second
     * set of glyphs in this kerning entry.
     * @param firstUnicodeRanges An array of unicode ranges that are part of the
     * first set of glyphs in this kerning entry.
     * @param secondUnicodeRanges An array of unicode ranges that are part of
     * the second set of glyphs in this kerning entry.
     * @param adjustValue The kerning adjustment (positive value means the space
     * between glyphs should decrease).
     */
    public Kern(int[] firstGlyphCodes, int[] secondGlyphCodes,
                UnicodeRange[] firstUnicodeRanges,
                UnicodeRange[] secondUnicodeRanges,
                float adjustValue) {

        this.firstGlyphCodes = firstGlyphCodes;
        this.secondGlyphCodes = secondGlyphCodes;
        this.firstUnicodeRanges = firstUnicodeRanges;
        this.secondUnicodeRanges = secondUnicodeRanges;
        this.kerningAdjust = adjustValue;
    }

    /**
     * Returns true if the specified glyph is one of the glyphs considered
     * as first by this kerning pair. Returns false otherwise.
     *
     * @param glyphCode The id of the glyph to test.
     * @param glyphUnicode The unicode value of the glyph to test.
     * @return True if this glyph is in the list of first glyphs for the kerning entry
     */
    public boolean matchesFirstGlyph(int glyphCode, String glyphUnicode) {
        for (int i = 0; i < firstGlyphCodes.length; i++) {
            if (firstGlyphCodes[i] == glyphCode) {
                return true;
            }
        }
        for (int i = 0; i < firstUnicodeRanges.length; i++) {
            if (firstUnicodeRanges[i].contains(glyphUnicode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the specified glyph is one of the glyphs considered
     * as second by this kerning pair. Returns false otherwise.
     *
     * @param glyphCode The id of the glyph to test.
     * @param glyphUnicode The unicode value of the glyph to test.
     * @return True if this glyph is in the list of second glyphs for the kerning entry
     */
    public boolean matchesSecondGlyph(int glyphCode, String glyphUnicode) {

        for (int i = 0; i < secondGlyphCodes.length; i++) {
            if (secondGlyphCodes[i] == glyphCode) {
                return true;
            }
        }
        for (int i = 0; i < secondUnicodeRanges.length; i++) {
            if (secondUnicodeRanges[i].contains(glyphUnicode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Give the kerning adjustment value for this entry (positive
     * value means the space between characters should decrease)
     */
    public float getAdjustValue() {
        return kerningAdjust;
    }

}
