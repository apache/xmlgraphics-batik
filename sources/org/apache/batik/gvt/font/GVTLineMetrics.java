/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

import java.awt.font.LineMetrics;

/**
 * GVTLineMetrics is a GVT version of java.awt.font.LineMetrics.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class GVTLineMetrics {


    protected float ascent;
    protected int baselineIndex;
    protected float[] baselineOffsets;
    protected float descent;
    protected float height;
    protected float leading;
    protected int numChars;
    protected float strikethroughOffset;
    protected float strikethroughThickness;
    protected float underlineOffset;
    protected float underlineThickness;
    protected float overlineOffset;
    protected float overlineThickness;


    public GVTLineMetrics(LineMetrics lineMetrics) {

        this.ascent = lineMetrics.getAscent();
        this.baselineIndex = lineMetrics.getBaselineIndex();
        this.baselineOffsets = lineMetrics.getBaselineOffsets();
        this.descent = lineMetrics.getDescent();
        this.height = lineMetrics.getHeight();
        this.leading = lineMetrics.getLeading();
        this.numChars = lineMetrics.getNumChars();
        this.strikethroughOffset = lineMetrics.getStrikethroughOffset();
        this.strikethroughThickness = lineMetrics.getStrikethroughThickness();
        this.underlineOffset = lineMetrics.getUnderlineOffset();
        this.underlineThickness = lineMetrics.getUnderlineThickness();
        this.overlineOffset = -this.ascent;
        this.overlineThickness = this.underlineThickness;
    }

    public GVTLineMetrics(LineMetrics lineMetrics, float scaleFactor) {

        this.ascent = lineMetrics.getAscent() * scaleFactor;
        this.baselineIndex = lineMetrics.getBaselineIndex();
        this.baselineOffsets = lineMetrics.getBaselineOffsets();
        this.descent = lineMetrics.getDescent() * scaleFactor;
        this.height = lineMetrics.getHeight() * scaleFactor;
        this.leading = lineMetrics.getLeading();
        this.numChars = lineMetrics.getNumChars();
        this.strikethroughOffset = lineMetrics.getStrikethroughOffset() * scaleFactor;
        this.strikethroughThickness = lineMetrics.getStrikethroughThickness() * scaleFactor;
        this.underlineOffset = lineMetrics.getUnderlineOffset() * scaleFactor;
        this.underlineThickness = lineMetrics.getUnderlineThickness() * scaleFactor;
        this.overlineOffset = -this.ascent;
        this.overlineThickness = this.underlineThickness;
    }


    public GVTLineMetrics(float ascent, int baselineIndex, float[] baselineOffsets,
                          float descent, float height, float leading, int numChars,
                          float strikethroughOffset, float strikethroughThickness,
                          float underlineOffset, float underlineThickness,
                          float overlineOffset, float overlineThickness) {

        this.ascent = ascent;
        this.baselineIndex = baselineIndex;
        this.baselineOffsets = baselineOffsets;
        this.descent = descent;
        this.height = height;
        this.leading = leading;
        this.numChars = numChars;
        this.strikethroughOffset = strikethroughOffset;
        this.strikethroughThickness = strikethroughThickness;
        this.underlineOffset = underlineOffset;
        this.underlineThickness = underlineThickness;
        this.overlineOffset = overlineOffset;
        this.overlineThickness = overlineThickness;
    }

    /**
     * Returns the ascent of the text.
     */
    public float getAscent() {
        return ascent;
    }

    /**
     * Returns the baseline index of the text.
     */
    public int getBaselineIndex() {
        return baselineIndex;
    }

    /**
     * Returns the baseline offsets of the text, relative to the baseline of
     * the text.
     */
    public float[] getBaselineOffsets() {
        return baselineOffsets;
    }

    /**
     * Returns the descent of the text.
     */
    public float getDescent() {
        return descent;
    }

    /**
     * Returns the height of the text.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Returns the leading of the text.
     */
    public float getLeading() {
        return leading;
    }

    /**
     * Returns the number of characters in the text whose metrics are
     * encapsulated by this LineMetrics object.
     */
    public int getNumChars() {
        return numChars;
    }

    /**
     * Returns the position of the strike-through line relative to the baseline.
     */
    public float getStrikethroughOffset() {
        return strikethroughOffset;
    }

    /**
     * Returns the thickness of the strike-through line.
     */
    public float getStrikethroughThickness() {
        return strikethroughThickness;
    }

    /**
     * Returns the position of the underline relative to the baseline.
     */
    public float getUnderlineOffset() {
        return underlineOffset;
    }

    /**
     * Returns the thickness of the underline.
     */
    public float getUnderlineThickness() {
        return underlineThickness;
    }

    public float getOverlineOffset() {
        return overlineOffset;
    }

    public float getOverlineThickness() {
        return overlineThickness;
    }

}