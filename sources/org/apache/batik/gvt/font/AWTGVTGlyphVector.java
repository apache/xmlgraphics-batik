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
import java.awt.geom.GeneralPath;
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

    // need to keep track of the glyphTransforms since GlyphVector doesn't seem to
    private AffineTransform[] glyphTransforms;

    // this is to record the default glyph positions
    private Point2D[] defaultGlyphPositions;

    // these are for caching the glyph outlines
    private Shape[] glyphOutlines;
    private Shape[] glyphVisualBounds;
    private Shape[] glyphLogicalBounds;
    private boolean[] glyphVisible;
    private GeneralPath outline;

    /**
     * Creates and new AWTGVTGlyphVector from the specified GlyphVector
     * and AWTGVTFont objects.
     */
    public AWTGVTGlyphVector(GlyphVector glyphVector, AWTGVTFont font) {
        awtGlyphVector = glyphVector;
        gvtFont = font;
        int numGlyphs = glyphVector.getNumGlyphs();
        outline = null;
        glyphTransforms = new AffineTransform[numGlyphs];
        defaultGlyphPositions = new Point2D.Float[numGlyphs];
        glyphOutlines = new Shape[numGlyphs];
        glyphVisualBounds = new Shape[numGlyphs];
        glyphLogicalBounds = new Shape[numGlyphs];
        glyphVisible = new boolean[numGlyphs];
        for (int i = 0; i < numGlyphs; i++) {
            glyphVisible[i] = true;
        }
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
        if (glyphLogicalBounds[glyphIndex] == null && glyphVisible[glyphIndex]) {
            computeGlyphLogicalBounds();
        }
        return glyphLogicalBounds[glyphIndex];
    }


    private void computeGlyphLogicalBounds() {

        GVTLineMetrics lineMetrics = gvtFont.getLineMetrics("By", awtGlyphVector.getFontRenderContext());
        float ascent = lineMetrics.getAscent();
        float descent = lineMetrics.getDescent();

        for (int i = 0; i < getNumGlyphs(); i++) {

            if (glyphVisible[i]) {

                AffineTransform glyphTransform = getGlyphTransform(i);

                if (glyphTransform == null) {

                    GlyphMetrics glyphMetrics = getGlyphMetrics(i);

                    float glyphX = (float)(getGlyphPosition(i).getX());
                    float glyphY =  (float)getGlyphPosition(i).getY() - ascent;
                    float glyphWidth = glyphMetrics.getAdvance();
                    float glyphHeight = ascent + descent;

                    glyphLogicalBounds[i] = new Rectangle2D.Double(glyphX, glyphY,
                                                     glyphWidth, glyphHeight);

                } else {  // the glyph is transformed so just return the neat bounds

                    Shape glyphOutline = awtGlyphVector.getGlyphOutline(i);
                    Rectangle2D glyphBounds = glyphOutline.getBounds2D();

                    AffineTransform tr = AffineTransform.getTranslateInstance(getGlyphPosition(i).getX(),
                                                                          getGlyphPosition(i).getY());
                    tr.concatenate(glyphTransform);
                    glyphLogicalBounds[i] = tr.createTransformedShape(glyphBounds);
                }
            } else {
                // the glyph is not drawn
                glyphLogicalBounds[i] = null;
            }
        }

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
        if (glyphOutlines[glyphIndex] == null) {
            Shape glyphOutline = awtGlyphVector.getGlyphOutline(glyphIndex);
            AffineTransform tr = AffineTransform.getTranslateInstance(getGlyphPosition(glyphIndex).getX(),
                                                                      getGlyphPosition(glyphIndex).getY());
            AffineTransform glyphTransform = getGlyphTransform(glyphIndex);
            if (glyphTransform != null) {
                tr.concatenate(glyphTransform);
            }
            glyphOutlines[glyphIndex] = tr.createTransformedShape(glyphOutline);
        }
        return glyphOutlines[glyphIndex];
    }

    /**
     * Returns the position of the specified glyph within this GlyphVector.
     */
    public Point2D getGlyphPosition(int glyphIndex) {
        return awtGlyphVector.getGlyphPosition(glyphIndex);
    }

    /**
     * Returns the default position of the glyph. This will be the position that
     * is set when the performDefaultLayout method is run.
     */
    public Point2D getDefaultGlyphPosition(int glyphIndex) {
        if (defaultGlyphPositions[glyphIndex] == null) {
            performDefaultLayout();
        }
        return defaultGlyphPositions[glyphIndex];
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
        return glyphTransforms[glyphIndex];
    }

    /**
     * Returns the visual bounds of the specified glyph within the GlyphVector.
     */
    public Shape getGlyphVisualBounds(int glyphIndex) {
        if (glyphVisualBounds[glyphIndex] == null) {
            Shape glyphOutline = awtGlyphVector.getGlyphOutline(glyphIndex);
            Rectangle2D glyphBounds = glyphOutline.getBounds2D();
            AffineTransform tr = AffineTransform.getTranslateInstance(getGlyphPosition(glyphIndex).getX(),
                                                                      getGlyphPosition(glyphIndex).getY());
            AffineTransform glyphTransform = getGlyphTransform(glyphIndex);
            if (glyphTransform != null) {
                tr.concatenate(glyphTransform);
            }
            glyphVisualBounds[glyphIndex] = tr.createTransformedShape(glyphBounds);
        }
        return glyphVisualBounds[glyphIndex];
    }

    /**
     *  Returns the logical bounds of this GlyphVector.
     */
    public Rectangle2D getLogicalBounds() {
        Shape outline = getOutline();
        Rectangle2D bounds = outline.getBounds2D();
        Point2D firstPos = getGlyphPosition(0);
        bounds.setRect(bounds.getX()-firstPos.getX(), bounds.getY()-firstPos.getY(),
                            bounds.getWidth(), bounds.getHeight());
        return bounds;
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
        if (outline == null) {
            outline = new GeneralPath();
            for (int i = 0; i < getNumGlyphs(); i++) {
                if (glyphVisible[i]) {
                    Shape glyphOutline = getGlyphOutline(i);
                    outline.append(glyphOutline, false);
                }
            }
        }
        return outline;
    }

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of this GlyphVector, offset to x, y.
     */
    public Shape getOutline(float x, float y) {
        Shape outline = getOutline();
        AffineTransform tr = AffineTransform.getTranslateInstance(x,y);
        outline = tr.createTransformedShape(outline);
        return outline;
    }

    /**
     * Returns the visual bounds of this GlyphVector The visual bounds is the
     * tightest rectangle enclosing all non-background pixels in the rendered
     * representation of this GlyphVector.
     */
    public Rectangle2D getVisualBounds() {
        Shape outline = getOutline();
        return outline.getBounds2D();
    }

    /**
     * Assigns default positions to each glyph in this GlyphVector.
     */
    public void performDefaultLayout() {
        awtGlyphVector.performDefaultLayout();
        outline = null;
        for (int i = 0; i < getNumGlyphs(); i++) {
            defaultGlyphPositions[i] = getGlyphPosition(i);
            glyphTransforms[i] = null;
            glyphVisualBounds[i] = null;
            glyphLogicalBounds[i] = null;
            glyphOutlines[i] = null;
        }
    }

    /**
     * Sets the position of the specified glyph within this GlyphVector.
     */
    public void setGlyphPosition(int glyphIndex, Point2D newPos) {
        awtGlyphVector.setGlyphPosition(glyphIndex, newPos);
        outline = null;
        glyphVisualBounds[glyphIndex] = null;
        glyphLogicalBounds[glyphIndex] = null;
        glyphOutlines[glyphIndex] = null;
    }

    /**
     * Sets the transform of the specified glyph within this GlyphVector.
     */
    public void setGlyphTransform(int glyphIndex, AffineTransform newTX) {
        glyphTransforms[glyphIndex] = newTX;
        outline = null;
        glyphVisualBounds[glyphIndex] = null;
        glyphLogicalBounds[glyphIndex] = null;
        glyphOutlines[glyphIndex] = null;
    }

    /**
     * Tells the glyph vector whether or not to draw the specified glyph.
     */
    public void setGlyphVisible(int glyphIndex, boolean visible) {
        glyphVisible[glyphIndex] = visible;
        outline = null;
        glyphVisualBounds[glyphIndex] = null;
        glyphLogicalBounds[glyphIndex] = null;
        glyphOutlines[glyphIndex] = null;
    }

    /**
     * Returns the number of chars represented by the glyphs within the
     * specified range.
     * @param startGlyphIndex The index of the first glyph in the range.
     * @param endGlyphIndex The index of the last glyph in the range.
     * @return The number of chars.
     */
    public int getCharacterCount(int startGlyphIndex, int endGlyphIndex) {
        if (startGlyphIndex < 0) {
            startGlyphIndex = 0;
        }
        if (endGlyphIndex > getNumGlyphs()-1) {
            endGlyphIndex = getNumGlyphs()-1;
        }
        return endGlyphIndex - startGlyphIndex + 1;
    }


    /**
     * Draws this glyph vector.
     */
    public void draw(Graphics2D graphics2D, GraphicsNodeRenderContext context,
                     AttributedCharacterIterator aci) {

        aci.first();
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
