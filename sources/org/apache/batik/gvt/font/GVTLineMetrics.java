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

    /**
     * Constructs a GVTLineMetrics object based on the specified line metrics.
     *
     * @param lineMetrics The lineMetrics object that this metrics object will
     * be based upon.
     */
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


    /**
     * Constructs a GVTLineMetrics object based on the specified line metrics
     * with a scale factor applied.
     *
     * @param lineMetrics The lineMetrics object that this metrics object will
     * be based upon.
     * @param scaleFactor The scale factor to apply to all metrics.
     */
    public GVTLineMetrics(LineMetrics lineMetrics, float scaleFactor) {
        this.ascent = lineMetrics.getAscent() * scaleFactor;
        this.baselineIndex = lineMetrics.getBaselineIndex();
        this.baselineOffsets = lineMetrics.getBaselineOffsets();
        for (int i=0; i<baselineOffsets.length; i++) {
            this.baselineOffsets[i] *= scaleFactor;
        }
        this.descent = lineMetrics.getDescent() * scaleFactor;
        this.height = lineMetrics.getHeight() * scaleFactor;
        this.leading = lineMetrics.getLeading();
        this.numChars = lineMetrics.getNumChars();
        this.strikethroughOffset = 
	    lineMetrics.getStrikethroughOffset() * scaleFactor;
        this.strikethroughThickness = 
	    lineMetrics.getStrikethroughThickness() * scaleFactor;
        this.underlineOffset = lineMetrics.getUnderlineOffset() * scaleFactor;
        this.underlineThickness = 
	    lineMetrics.getUnderlineThickness() * scaleFactor;
        this.overlineOffset = -this.ascent;
        this.overlineThickness = this.underlineThickness;
    }


    /**
     * Constructs a GVTLineMetrics object with the specified attributes.
     */
    public GVTLineMetrics(float ascent, 
			  int baselineIndex, 
			  float[] baselineOffsets,
                          float descent, 
			  float height, 
			  float leading, int numChars,
                          float strikethroughOffset, 
			  float strikethroughThickness,
                          float underlineOffset, 
			  float underlineThickness,
                          float overlineOffset, 
			  float overlineThickness) {

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

    /**
     * Returns the position of the overline relative to the baseline.
     */
    public float getOverlineOffset() {
        return overlineOffset;
    }

    /**
     * Returns the thickness of the overline.
     */
    public float getOverlineThickness() {
        return overlineThickness;
    }

}
