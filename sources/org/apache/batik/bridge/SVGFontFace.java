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

    // TODO: change this list of parameters into a Map of some sort, will be
    // too many attributes to pass in individually

    /**
     * Constructes an SVGFontFace with the specfied font attributes.
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

    public String getFamilyName() {
        return familyName;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public float getUnitsPerEm() {
        return unitsPerEm;
    }

    public float getAscent() {
        return ascent;
    }

    public float getDescent() {
        return descent;
    }

    public float getStrikethroughPosition() {
        return strikethroughPosition;
    }
    public float getStrikethroughThickness() {
        return strikethroughThickness;
    }

    public float getUnderlinePosition() {
        return underlinePosition;
    }
    public float getUnderlineThickness() {
        return underlineThickness;
    }

    public float getOverlinePosition() {
        return overlinePosition;
    }
    public float getOverlineThickness() {
        return overlineThickness;
    }
}
