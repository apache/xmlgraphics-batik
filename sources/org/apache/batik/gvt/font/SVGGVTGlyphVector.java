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
import org.apache.batik.bridge.SVGGVTFont;
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

     public GlyphVector getAWTGlyphVector() {
        return null;
    }

    public SVGGVTGlyphVector(GVTFont font, Glyph[] glyphs, FontRenderContext frc) {
       this.font = font;
       this.glyphs = glyphs;
     //  System.out.println("glyph vector has " + glyphs.length + " glyphs");
       this.frc = frc;
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
        // get outline of glyph and translate so that it is relative to the
        // positon of the first glyph
        Shape glyphOutline = getGlyphOutline(glyphIndex);
        Point2D firstPos = glyphs[0].getPosition();
        AffineTransform tr = AffineTransform.getTranslateInstance(-firstPos.getX(), -firstPos.getY());
        return tr.createTransformedShape(glyphOutline);
    }

    /**
     * Returns the metrics of the glyph at the specified index into this
     * GlyphVector.
     */
    public GlyphMetrics getGlyphMetrics(int glyphIndex) {
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
            if (font instanceof SVGGVTFont) {
                float kern = ((SVGGVTFont)font).getKerning(glyphs[glyphIndex].getUnicode(),
                                                           glyphs[glyphIndex+1].getUnicode());
                return glyphs[glyphIndex].getGlyphMetrics(kern);
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
        // get outline of glyph vector and translate so that it is relative to the
        // positon of the first glyph
        Shape outline = getOutline();
        Point2D firstPos = glyphs[0].getPosition();
        AffineTransform tr = AffineTransform.getTranslateInstance(-firstPos.getX(), -firstPos.getY());
        return tr.createTransformedShape(outline).getBounds2D();
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
        GeneralPath outline = new GeneralPath();
        for (int i = 0; i < glyphs.length; i++) {
            Shape glyphOutline = glyphs[i].getOutline();
            if (glyphOutline != null) {
                outline.append(glyphOutline, false);
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
     * Assigns default positions to each glyph in this GlyphVector.
     */
    public void performDefaultLayout() {
        float currentX = 0;
        float currentY = 0;
        for (int i = 0; i < glyphs.length; i++) {
            glyphs[i].setPosition(new Point2D.Float(currentX, currentY));
            currentX += glyphs[i].getHorizAdvX();
          //  currentY += glyphs[i].getVertAdvY();  // only do this for vertical text
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
    }

    /**
     * Draws this glyph vector.
     */
    public void draw(Graphics2D graphics2D, GraphicsNodeRenderContext context,
                     AttributedCharacterIterator aci) {

   /*     System.out.print("drawing SVGGVTGlyphVector for: '");
         for (char c = aci.first(); c != aci.DONE; c = aci.next()) {
            System.out.print(c);
        }
        System.out.println("'");
     */
        for (int i = 0; i < glyphs.length; i++) {
            glyphs[i].draw(graphics2D, context);
        }
    }
}
