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
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import java.text.AttributedCharacterIterator;
import java.awt.Paint;
import java.awt.Stroke;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;

/**
 * A GVTGlyphVector class for SVG fonts.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public final class SVGGVTGlyphVector implements GVTGlyphVector {

    private GVTFont font;
    private Glyph[] glyphs;
    private FontRenderContext frc;
    private GeneralPath outline;
    private Rectangle2D logicalBounds;
    private Shape[] glyphLogicalBounds;
    private boolean[] glyphVisible;

    /**
     * Constructs an SVGGVTGlyphVector.
     *
     * @param font The font that is creating this glyph vector.
     * @param glyphs An array containing the glyphs that form the basis for this
     * glyph vector.
     * @param frc The current font render context.
     */
    public SVGGVTGlyphVector(GVTFont font, Glyph[] glyphs, FontRenderContext frc) {
        this.font = font;
        this.glyphs = glyphs;
        this.frc = frc;
        outline = null;
        logicalBounds = null;
        glyphLogicalBounds = new Shape[glyphs.length];
        glyphVisible = new boolean[glyphs.length];
        for (int i = 0; i < glyphs.length; i++) {
            glyphVisible[i] = true;
        }
    }

    /**
     * Returns the Font associated with this GlyphVector.
     */
    public GVTFont getFont() {
        return font;
    }

    /**
     * Returns the FontRenderContext associated with this GlyphVector.
     */
    public FontRenderContext getFontRenderContext() {
        return frc;
    }

    /**
     * Returns the glyphcode of the specified glyph.
     */
    public int getGlyphCode(int glyphIndex) throws IndexOutOfBoundsException {
        if (glyphIndex < 0 || glyphIndex > (glyphs.length-1)) {
            throw new IndexOutOfBoundsException("glyphIndex " + glyphIndex
                      + " is out of bounds, should be between 0 and "
                      + (glyphs.length-1));
        }
        return glyphs[glyphIndex].getGlyphCode();
    }

    /**
     * Returns an array of glyphcodes for the specified glyphs.
     */
    public int[] getGlyphCodes(int beginGlyphIndex, int numEntries,
                               int[] codeReturn)
                               throws IndexOutOfBoundsException,
                                       IllegalArgumentException {
        if (numEntries < 0) {
            throw new IllegalArgumentException("numEntries argument value, "
                      + numEntries + ", is illegal. It must be > 0.");
        }
        if (beginGlyphIndex < 0) {
            throw new IndexOutOfBoundsException("beginGlyphIndex " + beginGlyphIndex
                      + " is out of bounds, should be between 0 and "
                      + (glyphs.length-1));
        }
        if ((beginGlyphIndex+numEntries) > glyphs.length) {
             throw new IndexOutOfBoundsException("beginGlyphIndex + numEntries ("
                       + beginGlyphIndex + "+" + numEntries
                       + ") exceeds the number of glpyhs in this GlyphVector");
        }
        if (codeReturn == null) {
            codeReturn = new int[numEntries];
        }
        for (int i = beginGlyphIndex; i < (beginGlyphIndex+numEntries); i++) {
            codeReturn[i-beginGlyphIndex] = glyphs[i].getGlyphCode();
        }
        return codeReturn;
    }

    /**
     * Returns the justification information for the glyph at the specified
     * index into this GlyphVector.
     */
    public GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex) {
        if (glyphIndex < 0 || (glyphIndex > glyphs.length-1)) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex
            + ", is out of bounds. Should be between 0 and " + (glyphs.length-1) + ".");
        }
        return null;
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

        float ascent = 0;
        float descent = 0;
        if (font != null) {
            // font will only be null if this glyph vector is for an altGlyph
            GVTLineMetrics lineMetrics = font.getLineMetrics("By", frc);
            ascent = lineMetrics.getAscent();
            descent = lineMetrics.getDescent();
            if (descent < 0) {
                // make descent a positive value
                descent = -descent;
            }
        }
        Shape[] tempLogicalBounds = new Shape[getNumGlyphs()];
        boolean[] rotated = new boolean[getNumGlyphs()];

        double maxWidth = -1;
        double maxHeight = -1;

        for (int i = 0; i < getNumGlyphs(); i++) {

            if (glyphVisible[i]) {

                AffineTransform glyphTransform = getGlyphTransform(i);
                GVTGlyphMetrics glyphMetrics = getGlyphMetrics(i);

                if (glyphTransform == null && ascent != 0) {

                    float glyphX = (float)(getGlyphPosition(i).getX());
                    float glyphY =  (float)getGlyphPosition(i).getY() - ascent;
                    float glyphWidth = glyphMetrics.getHorizontalAdvance();
                    float glyphHeight = ascent + descent;

                    tempLogicalBounds[i] = new Rectangle2D.Double(glyphX, glyphY,
                                                                  glyphWidth, glyphHeight);
                    if (glyphWidth > maxWidth) maxWidth = glyphWidth;
                    if (glyphHeight > maxHeight) maxHeight = glyphHeight;
                    rotated[i] = false;

                } else {  // the glyph is transformed

                    Rectangle2D glyphBounds = glyphMetrics.getBounds2D();
                    AffineTransform tr = AffineTransform.getTranslateInstance(getGlyphPosition(i).getX(),
                                                                              getGlyphPosition(i).getY());
                    if (glyphTransform != null) {
                        tr.concatenate(glyphTransform);
                    }

                    tempLogicalBounds[i] = tr.createTransformedShape(glyphBounds);

                    // store three corner points so we can determine whether the glyph is rotated
                    Point2D p1 = new Point2D.Double(glyphBounds.getMinX(), glyphBounds.getMinY());
                    Point2D p2 = new Point2D.Double(glyphBounds.getMaxX(), glyphBounds.getMinY());
                    Point2D p3 = new Point2D.Double(glyphBounds.getMinX(), glyphBounds.getMaxY());

                    Point2D tp1 = new Point2D.Double();
                    Point2D tp2 = new Point2D.Double();
                    Point2D tp3 = new Point2D.Double();
                    tr.transform(p1, tp1);
                    tr.transform(p2, tp2);
                    tr.transform(p3, tp3);

                    if ((Math.abs(tp1.getX() - tp2.getX()) < 0.001
                        || Math.abs(tp1.getX() - tp3.getX()) < 0.001)
                        && (Math.abs(tp1.getY() - tp2.getY()) < 0.001
                        || Math.abs(tp1.getY() - tp3.getY()) < 0.001)) {
                        rotated[i] = false;
                    } else {
                        rotated[i] = true;
                    }
                    if (glyphBounds.isEmpty()) {
                        if (i > 0) {
                            // can't tell if rotated or not, make it the same as the
                            // previous glyph
                            rotated[i] = rotated[i-1];
                        } else {
                            rotated[i] = true;
                        }
                    }

                    Rectangle2D rectBounds = tempLogicalBounds[i].getBounds2D();
                    if (rectBounds.getWidth() > maxWidth) maxWidth = rectBounds.getWidth();
                    if (rectBounds.getHeight() > maxHeight) maxHeight = rectBounds.getHeight();
                }
            } else {
                // the glyph is not drawn
                tempLogicalBounds[i] = null;
            }
        }

        // if appropriate, join adjacent glyph logical bounds
        GeneralPath logicalBoundsPath = new GeneralPath();
        for (int i = 0; i < getNumGlyphs(); i++) {
            if (tempLogicalBounds[i] != null) {
                logicalBoundsPath.append(tempLogicalBounds[i], false);
            }
        }
        Rectangle2D fullBounds = logicalBoundsPath.getBounds2D();

        if (fullBounds.getHeight() < maxHeight*1.5) {
            // make all glyphs tops and bottoms the same as the full bounds
            for (int i = 0; i < getNumGlyphs(); i++) {
                // first make sure that the glyph logical bounds are not rotated
                if (!rotated[i] && tempLogicalBounds[i] != null) {
                    Rectangle2D glyphBounds = tempLogicalBounds[i].getBounds2D();
                    double x = glyphBounds.getMinX();
                    double width = glyphBounds.getWidth();
                    if (i < getNumGlyphs()-1 && tempLogicalBounds[i+1] != null) {
                        // make this glyph extend to the start of the next one
                        Rectangle2D nextGlyphBounds = tempLogicalBounds[i+1].getBounds2D();
                        if (nextGlyphBounds.getX() > x) { // going left to right
                            width = nextGlyphBounds.getX() - x;
                        } else {
                            double newGlyphX = nextGlyphBounds.getX() + nextGlyphBounds.getWidth();
                            width += (x - newGlyphX);
                            x = newGlyphX;
                        }
                    }
                    tempLogicalBounds[i] = new Rectangle2D.Double(x, fullBounds.getMinY(),
                                                                  width, fullBounds.getHeight());
                }
            }
        }
        if (fullBounds.getWidth() < maxWidth*1.5) {
            // make all glyphs left and right edges the same as the full bounds
            for (int i = 0; i < getNumGlyphs(); i++) {
                // first make sure that the glyph logical bounds are not rotated
                if (!rotated[i] && tempLogicalBounds[i] != null) {
                    Rectangle2D glyphBounds = tempLogicalBounds[i].getBounds2D();
                    double y = glyphBounds.getMinY();
                    double height = glyphBounds.getHeight();
                    if (i < getNumGlyphs()-1 && tempLogicalBounds[i+1] != null) {
                        // make this glyph extend to the start of the next one
                        Rectangle2D nextGlyphBounds = tempLogicalBounds[i+1].getBounds2D();
                        if (nextGlyphBounds.getY() > y) { // going top to bottom
                            height = nextGlyphBounds.getY() - y;
                        } else {
                            double newGlyphY = nextGlyphBounds.getY() + nextGlyphBounds.getHeight();
                            height += (y - newGlyphY);
                            y = newGlyphY;
                        }
                    }
                    tempLogicalBounds[i] = new Rectangle2D.Double(fullBounds.getMinX(), y,
                                                                  fullBounds.getWidth(), height);
                }
            }
        }

        for (int i = 0; i < getNumGlyphs(); i++) {
            glyphLogicalBounds[i] = tempLogicalBounds[i];
        }
    }

    /**
     * Returns the metrics of the glyph at the specified index into this
     * GlyphVector.
     */
    public GVTGlyphMetrics getGlyphMetrics(int glyphIndex) {

        if (glyphIndex < 0 || (glyphIndex > glyphs.length-1)) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex
            + ", is out of bounds. Should be between 0 and " + (glyphs.length-1) + ".");
        }

        // check to see if we should kern this glyph
        // I return the kerning information in the glyph metrics
        // as a first pass at implementation (I don't want to
        // fiddle with layout too much right now).

        if (glyphIndex < glyphs.length - 1) {
            // check for kerning
            if (font != null) {
                float hkern = font.getHKern(glyphs[glyphIndex].getGlyphCode(),
                                            glyphs[glyphIndex+1].getGlyphCode());
                float vkern = font.getVKern(glyphs[glyphIndex].getGlyphCode(),
                                            glyphs[glyphIndex+1].getGlyphCode());
                return glyphs[glyphIndex].getGlyphMetrics(hkern, vkern);
            }
        }

        // get a normal metrics
        return glyphs[glyphIndex].getGlyphMetrics();
    }

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of the specified glyph within this GlyphVector.
     */
    public Shape getGlyphOutline(int glyphIndex) {
        if (glyphIndex < 0 || (glyphIndex > glyphs.length-1)) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex
            + ", is out of bounds. Should be between 0 and " + (glyphs.length-1) + ".");
        }
        return glyphs[glyphIndex].getOutline();
    }

    /**
     * Returns the position of the specified glyph within this GlyphVector.
     */
    public Point2D getGlyphPosition(int glyphIndex) {
        if (glyphIndex < 0 || (glyphIndex > glyphs.length-1)) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex
            + ", is out of bounds. Should be between 0 and " + (glyphs.length-1) + ".");
        }
        return glyphs[glyphIndex].getPosition();
    }


    /**
     * Returns an array of glyph positions for the specified glyphs
     */
    public float[] getGlyphPositions(int beginGlyphIndex, int numEntries,
                                     float[] positionReturn) {
         if (numEntries < 0) {
            throw new IllegalArgumentException("numEntries argument value, "
                      + numEntries + ", is illegal. It must be > 0.");
        }
        if (beginGlyphIndex < 0) {
            throw new IndexOutOfBoundsException("beginGlyphIndex " + beginGlyphIndex
                      + " is out of bounds, should be between 0 and "
                      + (glyphs.length-1));
        }
        if ((beginGlyphIndex+numEntries) > glyphs.length) {
             throw new IndexOutOfBoundsException("beginGlyphIndex + numEntries ("
                       + beginGlyphIndex + "+" + numEntries
                       + ") exceeds the number of glpyhs in this GlyphVector");
        }
        if (positionReturn == null) {
            positionReturn = new float[numEntries*2];
        }
        for (int i = beginGlyphIndex; i < (beginGlyphIndex+numEntries); i++) {
            Point2D glyphPos = glyphs[i].getPosition();
            positionReturn[(i-beginGlyphIndex)*2] = (float)glyphPos.getX();
            positionReturn[(i-beginGlyphIndex)*2 + 1] = (float)glyphPos.getY();
        }
        return positionReturn;
    }

    /**
     * Gets the transform of the specified glyph within this GlyphVector.
     */
    public AffineTransform getGlyphTransform(int glyphIndex) {
        if (glyphIndex < 0 || (glyphIndex > glyphs.length-1)) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex
            + ", is out of bounds. Should be between 0 and " + (glyphs.length-1) + ".");
        }
        return glyphs[glyphIndex].getTransform();
    }

    /**
     * Returns the visual bounds of the specified glyph within the GlyphVector.
     */
    public Shape getGlyphVisualBounds(int glyphIndex) {
        if (glyphIndex < 0 || (glyphIndex > glyphs.length-1)) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex
            + ", is out of bounds. Should be between 0 and " + (glyphs.length-1) + ".");
        }
        return glyphs[glyphIndex].getOutline();
    }

   /**
     *  Returns the logical bounds of this GlyphVector.
     */
    public Rectangle2D getLogicalBounds() {
        if (logicalBounds == null) {
            GeneralPath logicalBoundsPath = new GeneralPath();
            for (int i = 0; i < getNumGlyphs(); i++) {
                Shape glyphLogicalBounds = getGlyphLogicalBounds(i);
                if (glyphLogicalBounds != null) {
                    logicalBoundsPath.append(glyphLogicalBounds, false);
                }
            }
            logicalBounds = logicalBoundsPath.getBounds2D();
        }
        return logicalBounds;
    }

    /**
     * Returns the number of glyphs in this GlyphVector.
     */
    public int getNumGlyphs() {
        if (glyphs != null) {
            return glyphs.length;
        }
        return 0;
    }

    /**
     * Returns a Shape whose interior corresponds to the visual representation
     * of this GlyphVector.
     */
    public Shape getOutline() {
        if (outline == null) {
            outline = new GeneralPath();
            for (int i = 0; i < glyphs.length; i++) {
                if (glyphVisible[i]) {
                    Shape glyphOutline = glyphs[i].getOutline();
                    if (glyphOutline != null) {
                        outline.append(glyphOutline, false);
                    }
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
        Shape translatedOutline = tr.createTransformedShape(outline);
        return translatedOutline;
    }

    /**
     * Returns the visual bounds of this GlyphVector The visual bounds is the
     * tightest rectangle enclosing all non-background pixels in the rendered
     * representation of this GlyphVector.
     */
    public Rectangle2D getVisualBounds() {
        return getOutline().getBounds2D();
    }

    /**
     * Assigns default positions to each glyph in this GlyphVector. The default
     * layout is horizontal.
     */
    public void performDefaultLayout() {
        float currentX = 0;
        float currentY = 0;
        for (int i = 0; i < glyphs.length; i++) {
            glyphs[i].setPosition(new Point2D.Float(currentX, currentY));
            glyphs[i].setTransform(null);
            glyphLogicalBounds[i] = null;
            currentX += glyphs[i].getHorizAdvX();
            logicalBounds = null;
            outline = null;
        }
    }

    /**
     * Sets the position of the specified glyph within this GlyphVector.
     */
    public void setGlyphPosition(int glyphIndex, Point2D newPos)
                                 throws IndexOutOfBoundsException {
        if (glyphIndex < 0 || (glyphIndex > glyphs.length-1)) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex
            + ", is out of bounds. Should be between 0 and " + (glyphs.length-1) + ".");
        }
        glyphs[glyphIndex].setPosition(newPos);
        glyphLogicalBounds[glyphIndex] = null;
        outline = null;
        logicalBounds = null;
    }

    /**
     * Sets the transform of the specified glyph within this GlyphVector.
     */
    public void setGlyphTransform(int glyphIndex, AffineTransform newTX) {
        if (glyphIndex < 0 || (glyphIndex > glyphs.length-1)) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex
            + ", is out of bounds. Should be between 0 and " + (glyphs.length-1) + ".");
        }
        glyphs[glyphIndex].setTransform(newTX);
        glyphLogicalBounds[glyphIndex] = null;
        outline = null;
        logicalBounds = null;
    }

    /**
     * Tells the glyph vector whether or not to draw the specified glyph.
     */
    public void setGlyphVisible(int glyphIndex, boolean visible) {
        glyphVisible[glyphIndex] = visible;
        outline = null;
        logicalBounds = null;
        glyphLogicalBounds[glyphIndex] = null;
    }

    /**
     * Returns the number of chars represented by the glyphs within the
     * specified range.
     * @param startGlyphIndex The index of the first glyph in the range.
     * @param endGlyphIndex The index of the last glyph in the range.
     * @return The number of chars.
     */
    public int getCharacterCount(int startGlyphIndex, int endGlyphIndex) {
        int numChars = 0;
        if (startGlyphIndex < 0) {
            startGlyphIndex = 0;
        }
        if (endGlyphIndex > glyphs.length-1) {
            endGlyphIndex = glyphs.length-1;
        }
        for (int i = startGlyphIndex; i <= endGlyphIndex; i++) {
            String glyphUnicode = glyphs[i].getUnicode();
            numChars += glyphUnicode.length();
        }
        return numChars;
    }

    /**
     * Draws this glyph vector.
     */
    public void draw(Graphics2D graphics2D, GraphicsNodeRenderContext context,
                     AttributedCharacterIterator aci) {
        for (int i = 0; i < glyphs.length; i++) {
            if (glyphVisible[i]) {
                glyphs[i].draw(graphics2D, context);
            }
        }
    }
}

