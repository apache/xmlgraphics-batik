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
import java.awt.Paint;
import java.awt.Stroke;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;

/**
 * This is a wrapper class for a java.awt.font.GlyphVector instance.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public final class AWTGVTGlyphVector implements GVTGlyphVector {

    private final GlyphVector awtGlyphVector;
    private final AWTGVTFont gvtFont;


    ///temporary
    public GlyphVector getAWTGlyphVector() {
        return awtGlyphVector;
    }


    /**
     * Creates and new AWTGVTGlyphVector from the specified GlyphVector
     * and AWTGVTFont objects.
     */
    public AWTGVTGlyphVector(GlyphVector glyphVector, AWTGVTFont font) {
        awtGlyphVector = glyphVector;
        gvtFont = font;
    }

    /**
     * Returns the GVTFont associated with this GVTGlyphVector.
     */
    public GVTFont getFont() {
        return gvtFont;
    }

    /**
     * Returns the FontRenderContext associated with this GlyphVector.
     */
    public FontRenderContext getFontRenderContext() {
        return awtGlyphVector.getFontRenderContext();
    }

    /**
     * Returns the glyphcode of the specified glyph.
     */
    public int getGlyphCode(int glyphIndex) {
        return awtGlyphVector.getGlyphCode(glyphIndex);
    }

    /**
     * Returns an array of glyphcodes for the specified glyphs.
     */
    public int[] getGlyphCodes(int beginGlyphIndex, int numEntries,
                               int[] codeReturn) {
        return awtGlyphVector.getGlyphCodes(beginGlyphIndex, numEntries,
                                            codeReturn);
    }

    /**
     * Returns the justification information for the glyph at the specified
     * index into this GlyphVector.
     */
    public GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex) {
        return awtGlyphVector.getGlyphJustificationInfo(glyphIndex);
    }

    /**
     *  Returns the logical bounds of the specified glyph within this
     *  GlyphVector.
     */
    public Shape getGlyphLogicalBounds(int glyphIndex) {
        return awtGlyphVector.getGlyphLogicalBounds(glyphIndex);
    }

    /**
     * Returns the metrics of the glyph at the specified index into this
     * GVTGlyphVector.
     */
    public GlyphMetrics getGlyphMetrics(int glyphIndex) {
        return awtGlyphVector.getGlyphMetrics(glyphIndex);
    }

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of the specified glyph within this GlyphVector.
     */
    public Shape getGlyphOutline(int glyphIndex) {
        return awtGlyphVector.getGlyphOutline(glyphIndex);
    }

    /**
     * Returns the position of the specified glyph within this GlyphVector.
     */
    public Point2D getGlyphPosition(int glyphIndex) {
        return awtGlyphVector.getGlyphPosition(glyphIndex);
    }

    /**
     * Returns an array of glyph positions for the specified glyphs
     */
    public float[] getGlyphPositions(int beginGlyphIndex, int numEntries,
                                     float[] positionReturn) {
        return awtGlyphVector.getGlyphPositions(beginGlyphIndex, numEntries,
                                                positionReturn);
    }

    /**
     * Gets the transform of the specified glyph within this GlyphVector.
     */
    public AffineTransform getGlyphTransform(int glyphIndex) {
        return awtGlyphVector.getGlyphTransform(glyphIndex);
    }

    /**
     * Returns the visual bounds of the specified glyph within the GlyphVector.
     */
    public Shape getGlyphVisualBounds(int glyphIndex) {
        return awtGlyphVector.getGlyphVisualBounds(glyphIndex);
    }

    /**
     *  Returns the logical bounds of this GlyphVector.
     */
    public Rectangle2D getLogicalBounds() {
        return awtGlyphVector.getLogicalBounds();
    }

    /**
     * Returns the number of glyphs in this GlyphVector.
     */
    public int getNumGlyphs() {
        return awtGlyphVector.getNumGlyphs();
    }

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of this GlyphVector.
     */
    public Shape getOutline() {
        return awtGlyphVector.getOutline();
    }

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of this GlyphVector, offset to x, y.
     */
    public Shape getOutline(float x, float y) {
        return awtGlyphVector.getOutline(x, y);
    }

    /**
     * Returns the visual bounds of this GlyphVector The visual bounds is the
     * tightest rectangle enclosing all non-background pixels in the rendered
     * representation of this GlyphVector.
     */
    public Rectangle2D getVisualBounds() {
        return awtGlyphVector.getVisualBounds();
    }

    /**
     * Assigns default positions to each glyph in this GlyphVector.
     */
    public void performDefaultLayout() {
        awtGlyphVector.getVisualBounds();
    }

    /**
     * Sets the position of the specified glyph within this GlyphVector.
     */
    public void setGlyphPosition(int glyphIndex, Point2D newPos) {
        awtGlyphVector.setGlyphPosition(glyphIndex, newPos);
    }

    /**
     * Sets the transform of the specified glyph within this GlyphVector.
     */
    public void setGlyphTransform(int glyphIndex, AffineTransform newTX) {
        awtGlyphVector.setGlyphTransform(glyphIndex, newTX);
    }

    /**
     * Draws this glyph vector.
     */
    public void draw(Graphics2D graphics2D, GraphicsNodeRenderContext context,
                     AttributedCharacterIterator aci) {

        Shape outline = getOutline();

        // check if we need to fill this glyph
        Paint paint = (Paint) aci.getAttribute(TextAttribute.FOREGROUND);
        if (paint != null) {
            graphics2D.setPaint(paint);
            graphics2D.fill(outline);
        }

        // check if we need to draw the outline of this glyph
        Stroke stroke = (Stroke) aci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE);
        paint = (Paint) aci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT);
        if (stroke != null && paint != null) {
            graphics2D.setStroke(stroke);
            graphics2D.setPaint(paint);
            graphics2D.draw(outline);
        }

    }
}
