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

import java.awt.font.GlyphMetrics;
import java.awt.geom.Rectangle2D;

/**
 * GVTGlyphMetrics is essentially a wrapper class for java.awt.font.GlyphMetrics
 * with the addition of horizontal and vertical advance values.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class GVTGlyphMetrics {

    private GlyphMetrics gm;
    private float verticalAdvance;

    /**
     * Constructs a new GVTGlyphMetrics object based upon the specified
     * GlyphMetrics object and an additional vertical advance value.
     *
     * @param gm The glyph metrics.
     * @param verticalAdvance The vertical advance of the glyph.
     */
    public GVTGlyphMetrics(GlyphMetrics gm, float verticalAdvance) {
        this.gm = gm;
        this.verticalAdvance = verticalAdvance;
    }

    /**
     * Constructs a new GVTGlyphMetrics object using the specified parameters.
     *
     * @param horizontalAdvance The horizontal advance of the glyph.
     * @param verticalAdvance The vertical advance of the glyph.
     * @param bounds The black box bounds of the glyph.
     * @param glyphType The type of the glyph.
     */
    public GVTGlyphMetrics(float horizontalAdvance, 
                           float verticalAdvance,
                           Rectangle2D bounds, 
                           byte glyphType) {
        this.gm = new GlyphMetrics(horizontalAdvance, bounds, glyphType);
        this.verticalAdvance = verticalAdvance;
    }

    /**
     * Returns the horizontal advance of the glyph.
     */
    public float getHorizontalAdvance() {
        return gm.getAdvance();
    }

    /**
     * Returns the vertical advance of the glyph.
     */
    public float getVerticalAdvance() {
        return verticalAdvance;
    }

    /**
     * Returns the black box bounds of the glyph.
     */
    public Rectangle2D getBounds2D() {
        return gm.getBounds2D();
    }

    /**
     * Returns the left (top) side bearing of the glyph.
     */
    public float getLSB() {
        return gm.getLSB();
    }

    /**
     * Returns the right (bottom) side bearing of the glyph.
     */
    public float getRSB() {
        return gm.getRSB();
    }

    /**
     * Returns the raw glyph type code.
     */
    public int getType() {
        return gm.getType();
    }

    /**
     * Returns true if this is a combining glyph.
     */
    public boolean isCombining() {
        return gm.isCombining();
    }

    /**
     * Returns true if this is a component glyph.
     */
    public boolean isComponent() {
        return gm.isComponent();
    }

    /**
     * Returns true if this is a ligature glyph.
     */
    public boolean isLigature() {
        return gm.isLigature();
    }

    /**
     * Returns true if this is a standard glyph.
     */
    public boolean isStandard() {
        return gm.isStandard();
    }

    /**
     * Returns true if this is a whitespace glyph.
     */
    public boolean isWhitespace() {
        return gm.isWhitespace();
    }

}
