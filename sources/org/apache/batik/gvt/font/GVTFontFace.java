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

import org.apache.batik.util.SVGConstants;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class GVTFontFace implements SVGConstants {
    protected String familyName;
    protected float unitsPerEm;
    protected String fontWeight;
    protected String fontStyle;
    protected String fontVariant;
    protected String fontStretch;
    protected float slope;
    protected String panose1;
    protected float ascent;
    protected float descent;
    protected float strikethroughPosition;
    protected float strikethroughThickness;
    protected float underlinePosition;
    protected float underlineThickness;
    protected float overlinePosition;
    protected float overlineThickness;

    /**
     * Constructes an GVTFontFace with the specfied font-face attributes.
     */
    public GVTFontFace
        (String familyName, float unitsPerEm, String fontWeight,
         String fontStyle, String fontVariant, String fontStretch,
         float slope, String panose1, float ascent, float descent,
         float strikethroughPosition, float strikethroughThickness,
         float underlinePosition,     float underlineThickness,
         float overlinePosition,      float overlineThickness) {
        this.familyName = familyName;
        this.unitsPerEm = unitsPerEm;
        this.fontWeight = fontWeight;
        this.fontStyle = fontStyle;
        this.fontVariant = fontVariant;
        this.fontStretch = fontStretch;
        this.slope = slope;
        this.panose1 = panose1;
        this.ascent = ascent;
        this.descent = descent;
        this.strikethroughPosition = strikethroughPosition;
        this.strikethroughThickness = strikethroughThickness;
        this.underlinePosition = underlinePosition;
        this.underlineThickness = underlineThickness;
        this.overlinePosition = overlinePosition;
        this.overlineThickness = overlineThickness;
    }

    /**
     * Constructs an SVGFontFace with default values for all the
     * font-face attributes other than familyName
     */
    public GVTFontFace(String familyName) {
        this(familyName, 1000, 
             SVG_FONT_FACE_FONT_WEIGHT_DEFAULT_VALUE,
             SVG_FONT_FACE_FONT_STYLE_DEFAULT_VALUE,
             SVG_FONT_FACE_FONT_VARIANT_DEFAULT_VALUE,
             SVG_FONT_FACE_FONT_STRETCH_DEFAULT_VALUE,
             0, SVG_FONT_FACE_PANOSE_1_DEFAULT_VALUE, 
             800, 200, 300, 50, -75, 50, 800, 50);
    }

    /**
     * Returns the family name of this font, it may contain more than one.
     */
    public String getFamilyName() {
        return familyName;
    }

    public boolean hasFamilyName(String family) {
        String ffname = familyName;
        if (ffname.length() < family.length()) {
            return false;
        }

        ffname = ffname.toLowerCase();

        int idx = ffname.indexOf(family.toLowerCase());

        if (idx == -1) {
            return false;
        }

        // see if the family name is not the part of a bigger family name.
        if (ffname.length() > family.length()) {
            boolean quote = false;
            if (idx > 0) {
                char c = ffname.charAt(idx - 1);
                switch (c) {
                default:
                    return false;
                case ' ':
                    loop: for (int i = idx - 2; i >= 0; --i) {
                        switch (ffname.charAt(i)) {
                        default:
                            return false;
                        case ' ':
                            continue;
                        case '"':
                        case '\'':
                            quote = true;
                            break loop;
                        }
                    }
                    break;
                case '"':
                case '\'':
                    quote = true;
                case ',':
                }
            }
            if (idx + family.length() < ffname.length()) {
                char c = ffname.charAt(idx + family.length());
                switch (c) {
                default:
                    return false;
                case ' ':
                    loop: for (int i = idx + family.length() + 1;
                         i < ffname.length(); i++) {
                        switch (ffname.charAt(i)) {
                        default:
                            return false;
                        case ' ':
                            continue;
                        case '"':
                        case '\'':
                            if (!quote) {
                                return false;
                            }
                            break loop;
                        }
                    }
                    break;
                case '"':
                case '\'':
                    if (!quote) {
                        return false;
                    }
                case ',':
                }
            }
        }
        return true;
    }

    /**
     * Returns the font-weight.
     */
    public String getFontWeight() {
        return fontWeight;
    }

    /**
     * Returns the font-style.
     */
    public String getFontStyle() {
        return fontStyle;
    }

    /**
     * The number of coordinate units on the em square for this font.
     */
    public float getUnitsPerEm() {
        return unitsPerEm;
    }

    /**
     * Returns the maximum unaccented height of the font within the font
     * coordinate system.
     */
    public float getAscent() {
        return ascent;
    }

    /**
     * Returns the maximum unaccented depth of the font within the font
     * coordinate system.
     */
    public float getDescent() {
        return descent;
    }

    /**
     * Returns the position of the strikethrough decoration.
     */
    public float getStrikethroughPosition() {
        return strikethroughPosition;
    }

    /**
     * Returns the stroke thickness to use when drawing a strikethrough.
     */
    public float getStrikethroughThickness() {
        return strikethroughThickness;
    }

    /**
     * Returns the position of the underline decoration.
     */
    public float getUnderlinePosition() {
        return underlinePosition;
    }

    /**
     * Returns the stroke thickness to use when drawing a underline.
     */
    public float getUnderlineThickness() {
        return underlineThickness;
    }

    /**
     * Returns the position of the overline decoration.
     */
    public float getOverlinePosition() {
        return overlinePosition;
    }

    /**
     * Returns the stroke thickness to use when drawing a overline.
     */
    public float getOverlineThickness() {
        return overlineThickness;
    }
}
