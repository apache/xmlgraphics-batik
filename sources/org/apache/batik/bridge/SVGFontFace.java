/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * This class represents a &lt;font-face> element.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGFontFace {

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
     * Constructes an SVGFontFace with the specfied font-face attributes.
     */
    public SVGFontFace(String familyName, float unitsPerEm, String fontWeight,
                       String fontStyle, String fontVariant, String fontStretch,
                       float slope, String panose1, float ascent, float descent,
                       float strikethroughPosition, float strikethroughThickness,
                       float underlinePosition, float underlineThickness,
                       float overlinePosition, float overlineThickness) {

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
     * Returns the family name of this font, it may contain more than one.
     */
    public String getFamilyName() {
        return familyName;
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
