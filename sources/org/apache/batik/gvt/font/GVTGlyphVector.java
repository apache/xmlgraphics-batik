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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;

/**
 * An interface for all GVT GlyphVector classes.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public interface GVTGlyphVector {

    /**
     * Returns the Font associated with this GlyphVector.
     */
    GVTFont getFont();

    /**
     * Returns the FontRenderContext associated with this GlyphVector.
     */
    FontRenderContext getFontRenderContext();

    /**
     * Returns the glyphcode of the specified glyph.
     */
    int getGlyphCode(int glyphIndex);

    /**
     * Returns an array of glyphcodes for the specified glyphs.
     */
    int[] getGlyphCodes(int beginGlyphIndex, int numEntries, int[] codeReturn);

    /**
     * Returns the justification information for the glyph at the specified
     * index into this GlyphVector.
     */
    GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex);

    /**
     *  Returns the logical bounds of the specified glyph within this
     *  GlyphVector.  This is a good bound for hit detection and 
     *  highlighting it is not tight in any sense, and in some (rare) 
     * cases may exclude parts of the glyph.
     */
    Shape getGlyphLogicalBounds(int glyphIndex);

    /**
     * Returns the metrics of the glyph at the specified index into this
     * GlyphVector.
     */
    GVTGlyphMetrics getGlyphMetrics(int glyphIndex);

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of the specified glyph within this GlyphVector.
     */
    Shape getGlyphOutline(int glyphIndex);

    /**
     * Returns the position of the specified glyph within this GlyphVector.
     */
    Point2D getGlyphPosition(int glyphIndex);

    /**
     * Returns an array of glyph positions for the specified glyphs
     */
    float[] getGlyphPositions(int beginGlyphIndex, 
			      int numEntries,
			      float[] positionReturn);

    /**
     * Gets the transform of the specified glyph within this GlyphVector.
     */
    AffineTransform getGlyphTransform(int glyphIndex);

    /**
     * Returns the visual bounds of the specified glyph within the GlyphVector.
     */
    Shape getGlyphVisualBounds(int glyphIndex);

    /**
     *  Returns the logical bounds of this GlyphVector.  This is a
     *  good bound for hit detection and highlighting it is not tight
     *  in any sense, and in some (rare) * cases may exclude parts of
     *  the glyph.
     */
    Rectangle2D getLogicalBounds();

    /**
     * Returns the number of glyphs in this GlyphVector.
     */
    int getNumGlyphs();

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of this GlyphVector.
     */
    Shape getOutline();

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of this GlyphVector, offset to x, y.
     */
    Shape getOutline(float x, float y);

    /**
     * Returns the visual bounds of this GlyphVector The visual bounds is the
     * tightest rectangle enclosing all non-background pixels in the rendered
     * representation of this GlyphVector.
     */
    Rectangle2D getGeometricBounds();

    /**
     * Returns a tight bounds on the GylphVector including stroking.
     * @param aci Required to get painting attributes of glyphVector.
     */
    Rectangle2D getBounds2D(AttributedCharacterIterator aci);

    /**
     * Assigns default positions to each glyph in this GlyphVector.
     */
    void performDefaultLayout();

    /**
     * Sets the position of the specified glyph within this GlyphVector.
     */
    void setGlyphPosition(int glyphIndex, Point2D newPos);

    /**
     * Sets the transform of the specified glyph within this GlyphVector.
     */
    void setGlyphTransform(int glyphIndex, AffineTransform newTX);

    /**
     * Tells the glyph vector whether or not to draw the specified glyph.
     */
    void setGlyphVisible(int glyphIndex, boolean visible);

    /**
     * Returns true if specified glyph will be drawn.
     */
    public boolean isGlyphVisible(int glyphIndex);

    /**
     * Returns the number of chars represented by the glyphs within the
     * specified range.
     *
     * @param startGlyphIndex The index of the first glyph in the range.
     * @param endGlyphIndex The index of the last glyph in the range.
     * @return The number of chars.
     */
    int getCharacterCount(int startGlyphIndex, int endGlyphIndex);

    /**
     * Draws the glyph vector.
     */
    void draw(Graphics2D graphics2D,
              AttributedCharacterIterator aci);
}
