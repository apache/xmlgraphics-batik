/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
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
    public GVTFont getFont();

    /**
     * Returns the FontRenderContext associated with this GlyphVector.
     */
    public FontRenderContext getFontRenderContext();

    /**
     * Returns the glyphcode of the specified glyph.
     */
    public int getGlyphCode(int glyphIndex);

    /**
     * Returns an array of glyphcodes for the specified glyphs.
     */
    public int[] getGlyphCodes(int beginGlyphIndex, int numEntries,
                               int[] codeReturn);

    /**
     * Returns the justification information for the glyph at the specified
     * index into this GlyphVector.
     */
    public GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex);

    /**
     *  Returns the logical bounds of the specified glyph within this
     *  GlyphVector.
     */
    public Shape getGlyphLogicalBounds(int glyphIndex);

    /**
     * Returns the metrics of the glyph at the specified index into this
     * GlyphVector.
     */
    public GlyphMetrics getGlyphMetrics(int glyphIndex);

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of the specified glyph within this GlyphVector.
     */
    public Shape getGlyphOutline(int glyphIndex);

    /**
     * Returns the position of the specified glyph within this GlyphVector.
     */
    public Point2D getGlyphPosition(int glyphIndex);

    /**
     * Returns the default position of the glyph. This will be the position that
     * is set when the performDefaultLayout method is run.
     */
    public Point2D getDefaultGlyphPosition(int glyphIndex);

    /**
     * Returns an array of glyph positions for the specified glyphs
     */
    public float[] getGlyphPositions(int beginGlyphIndex, int numEntries,
                                     float[] positionReturn);

    /**
     * Gets the transform of the specified glyph within this GlyphVector.
     */
    public AffineTransform getGlyphTransform(int glyphIndex);

    /**
     * Returns the visual bounds of the specified glyph within the GlyphVector.
     */
    public Shape getGlyphVisualBounds(int glyphIndex);

    /**
     *  Returns the logical bounds of this GlyphVector.
     */
    public Rectangle2D getLogicalBounds();

    /**
     * Returns the number of glyphs in this GlyphVector.
     */
    public int getNumGlyphs();

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of this GlyphVector.
     */
    public Shape getOutline();

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of this GlyphVector, offset to x, y.
     */
    public Shape getOutline(float x, float y);

    /**
     * Returns the visual bounds of this GlyphVector The visual bounds is the
     * tightest rectangle enclosing all non-background pixels in the rendered
     * representation of this GlyphVector.
     */
    public Rectangle2D getVisualBounds();

    /**
     * Assigns default positions to each glyph in this GlyphVector.
     */
    public void performDefaultLayout();

    /**
     * Sets the position of the specified glyph within this GlyphVector.
     */
    public void setGlyphPosition(int glyphIndex, Point2D newPos);

    /**
     * Sets the transform of the specified glyph within this GlyphVector.
     */
    public void setGlyphTransform(int glyphIndex, AffineTransform newTX);

    /**
     * Tells the glyph vector whether or not to draw the specified glyph.
     */
    public void setGlyphVisible(int glyphIndex, boolean visible);

    /**
     * Returns the number of chars represented by the glyphs within the
     * specified range.
     * @param startGlyphIndex The index of the first glyph in the range.
     * @param endGlyphIndex The index of the last glyph in the range.
     * @return The number of chars.
     */
    public int getCharacterCount(int startGlyphIndex, int endGlyphIndex);

    /**
     * Draws the glyph vector.
     */
    public void draw(Graphics2D graphics2D, GraphicsNodeRenderContext context,
                     AttributedCharacterIterator aci);
}
