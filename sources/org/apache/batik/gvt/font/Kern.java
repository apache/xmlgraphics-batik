/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
     * @param secondGlyphCodes An array of glyph codes that are part of the
     * second set of glyphs in this kerning entry.
     * @param firstUnicodeRanges An array of unicode ranges that are part of the
     * first set of glyphs in this kerning entry.
     * @param secondUnicodeRanges An array of unicode ranges that are part of
     * the second set of glyphs in this kerning entry.
     * @param adjustValue The kerning adjustment (positive value means the space
     * between glyphs should decrease).  
     */
    public Kern(int[] firstGlyphCodes, 
		int[] secondGlyphCodes,
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
     * as first by this kerning entry. Returns false otherwise.
     *
     * @param glyphCode The id of the glyph to test.
     * @param glyphUnicode The unicode value of the glyph to test.
     * @return True if this glyph is in the list of first glyphs for the kerning
     * entry 
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
     * as second by this kerning entry. Returns false otherwise.
     *
     * @param glyphCode The id of the glyph to test.
     * @param glyphUnicode The unicode value of the glyph to test.

     * @return True if this glyph is in the list of second glyphs for the
     * kerning entry 
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
     * Returns the kerning adjustment value for this kerning entry (a positive
     * value means the space between characters should decrease).
     *
     * @return The kerning adjustment for this kerning entry.
     */
    public float getAdjustValue() {
        return kerningAdjust;
    }

}
