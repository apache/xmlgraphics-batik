/*

   Copyright 2001-2003  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.gvt.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;

import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPaintInfo;

/**
 * A GVTGlyphVector class for SVG fonts.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public final class SVGGVTGlyphVector implements GVTGlyphVector {

    public static final AttributedCharacterIterator.Attribute PAINT_INFO 
        = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;

    private GVTFont           font;
    private Glyph[]           glyphs;
    private FontRenderContext frc;
    private GeneralPath       outline;
    private Rectangle2D       logicalBounds;
    private Rectangle2D       bounds2D;
    private Shape[]           glyphLogicalBounds;
    private boolean[]         glyphVisible;
    private Point2D           endPos;
    private TextPaintInfo     cacheTPI;

    /**
     * Constructs an SVGGVTGlyphVector.
     *
     * @param font The font that is creating this glyph vector.
     * @param glyphs An array containing the glyphs that form the basis for this
     * glyph vector.
     * @param frc The current font render context.
     */
    public SVGGVTGlyphVector(GVTFont font, Glyph[] glyphs, 
                             FontRenderContext frc) {
        this.font = font;
        this.glyphs = glyphs;
        this.frc = frc;
        outline = null;
        bounds2D = null;
        logicalBounds = null;
        glyphLogicalBounds = new Shape[glyphs.length];
        glyphVisible = new boolean[glyphs.length];
        for (int i = 0; i < glyphs.length; i++) {
            glyphVisible[i] = true;
        }

        endPos = glyphs[glyphs.length-1].getPosition();
        endPos = new Point2D.Float
            ((float)(endPos.getX()+glyphs[glyphs.length-1].getHorizAdvX()),
             (float)endPos.getY());
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

        if (ascent == 0) {
            float maxAscent  = 0;
            float maxDescent = 0;
            for (int i = 0; i < getNumGlyphs(); i++) {
                if (!glyphVisible[i]) continue;
                GVTGlyphMetrics glyphMetrics = getGlyphMetrics(i);
                Rectangle2D     glyphBounds  = glyphMetrics.getBounds2D();
                ascent = (float)(-glyphBounds.getMinY());
                descent = (float)(glyphBounds.getHeight()-ascent);
                if (ascent > maxAscent)   maxAscent = ascent;
                if (descent > maxDescent) maxDescent = descent;
            }
            ascent  = maxAscent;
            descent = maxDescent;
        }

        Shape[] tempLogicalBounds = new Shape[getNumGlyphs()];
        boolean[] rotated = new boolean[getNumGlyphs()];
        boolean[] flippedH = new boolean[getNumGlyphs()];
        boolean[] flippedV = new boolean[getNumGlyphs()];

        double maxWidth = -1;
        double maxHeight = -1;

        for (int i = 0; i < getNumGlyphs(); i++) {

            if (!glyphVisible[i]) {
                // the glyph is not drawn
                tempLogicalBounds[i] = null;
                continue;
            }

            AffineTransform glyphTransform = getGlyphTransform(i);
            GVTGlyphMetrics glyphMetrics   = getGlyphMetrics(i);
            Rectangle2D glyphBounds = new Rectangle2D.Double
                (0, -ascent, glyphMetrics.getHorizontalAdvance(), 
                 ascent+descent);

            if (glyphBounds.isEmpty()) {
                // can't tell if rotated or not, make it
                // the same as the previous glyph, if we have one...
                if (i > 0) {
                    rotated[i] = rotated[i-1];
                    flippedH[i] = flippedH[i-1];
                    flippedV[i] = flippedV[i-1];
                } else {
                    rotated [i] = true;
                    flippedH[i] = false;
                    flippedV[i] = false;
                }
            } else {
                // get three corner points so we can determine
                // whether the glyph is rotated
                Point2D p1 = new Point2D.Double(glyphBounds.getMinX(), 
                                                glyphBounds.getMinY());
                Point2D p2 = new Point2D.Double(glyphBounds.getMaxX(), 
                                                glyphBounds.getMinY());
                Point2D p3 = new Point2D.Double(glyphBounds.getMinX(), 
                                                glyphBounds.getMaxY());
                    
                AffineTransform tr = AffineTransform.getTranslateInstance
                    (getGlyphPosition(i).getX(),
                     getGlyphPosition(i).getY());
                    
                if (glyphTransform != null)
                    tr.concatenate(glyphTransform);

                tempLogicalBounds[i] = 
                    tr.createTransformedShape(glyphBounds);
                    
                Point2D tp1 = new Point2D.Double();
                Point2D tp2 = new Point2D.Double();
                Point2D tp3 = new Point2D.Double();
                tr.transform(p1, tp1);
                tr.transform(p2, tp2);
                tr.transform(p3, tp3);
                double tdx12 = tp1.getX()-tp2.getX();
                double tdx13 = tp1.getX()-tp3.getX();
                double tdy12 = tp1.getY()-tp2.getY();
                double tdy13 = tp1.getY()-tp3.getY();

                if ((Math.abs(tdx12) < 0.001) &&
                    (Math.abs(tdy13) < 0.001)) {
                    // If these are both zero then it is axially aligned 
                    // on it's "side"...
                    rotated[i] = false;
                    double dx13 = p1.getX()-p3.getX();
                    double dy12 = p1.getY()-p2.getY();
                    if (Math.abs(tdx13+dx13) < 0.001) flippedH[i] = true;
                    if (Math.abs(tdy12+dy12) < 0.001) flippedV[i] = true;
                        
                } else if ((Math.abs(tdx13) < 0.001) &&
                           (Math.abs(tdy12) < 0.001)) {
                    // If these are both zero then it is axially aligned 
                    // vertically.
                    rotated[i] = false;
                    double dx12 = p1.getX()-p2.getX();
                    double dy13 = p1.getY()-p3.getY();
                    if (Math.abs(tdx12+dx12) < 0.001) flippedH[i] = true;
                    if (Math.abs(tdy13+dy13) < 0.001) flippedV[i] = true;
                } else {
                    rotated [i] = true;
                    flippedH[i] = false;
                    flippedV[i] = false;
                }
                    
                Rectangle2D rectBounds;
                rectBounds = tempLogicalBounds[i].getBounds2D();
                if (rectBounds.getWidth() > maxWidth) 
                    maxWidth = rectBounds.getWidth();
                if (rectBounds.getHeight() > maxHeight)
                    maxHeight = rectBounds.getHeight();
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
                // first make sure that the glyph logical bounds are
                // not rotated
                if (rotated[i]) continue;
                if (tempLogicalBounds[i] == null) continue;

                Rectangle2D glyphBounds = tempLogicalBounds[i].getBounds2D();

                double x = glyphBounds.getMinX();
                double width = glyphBounds.getWidth();

                if ((i < getNumGlyphs()-1) && 
                    (tempLogicalBounds[i+1] != null)) {
                    // make this glyph extend to the start of the next one
                    Rectangle2D nextGlyphBounds = 
                        tempLogicalBounds[i+1].getBounds2D();

                    if (nextGlyphBounds.getX() > x) { 
                        // going left to right (this is pretty hoky)
                        width = nextGlyphBounds.getX() - x;
                    } else {
                        double newGlyphX = (nextGlyphBounds.getX() + 
                                            nextGlyphBounds.getWidth());
                        width += (x - newGlyphX);
                        x = newGlyphX;
                    }
                }

                float x0 = (float)x;
                float x1 = (float)(x0+width);
                float y0 = (float)fullBounds.getMinY();
                float y1 = (float)(y0+fullBounds.getHeight());
                // Build the bounds rect the way things expect to see it...
                if (flippedH[i]) {
                    if (flippedV[i]) {
                        GeneralPath gp = new GeneralPath();
                        gp.moveTo(x1,y1);
                        gp.lineTo(x0,y1);
                        gp.lineTo(x0,y0);
                        gp.lineTo(x1,y0);
                        gp.lineTo(x1,y1);
                        gp.closePath();
                        tempLogicalBounds[i] = gp;
                    } else {
                        GeneralPath gp = new GeneralPath();
                        gp.moveTo(x1,y0);
                        gp.lineTo(x0,y0);
                        gp.lineTo(x0,y1);
                        gp.lineTo(x1,y1);
                        gp.lineTo(x1,y0);
                        gp.closePath();
                        tempLogicalBounds[i] = gp;
                    }
                } else {
                    if (flippedV[i]) {
                        GeneralPath gp = new GeneralPath();
                        gp.moveTo(x0,y1);
                        gp.lineTo(x1,y1);
                        gp.lineTo(x1,y0);
                        gp.lineTo(x0,y0);
                        gp.lineTo(x0,y1);
                        gp.closePath();
                        tempLogicalBounds[i] = gp;
                    } else {
                        tempLogicalBounds[i] = new Rectangle2D.Double
                            (x0, y0, x1-x0, y1-y0);
                    }
                }
            }
        } else if (fullBounds.getWidth() < maxWidth*1.5) {
            // make all glyphs left and right edges the same as the full bounds
            for (int i = 0; i < getNumGlyphs(); i++) {
                // first make sure that the glyph logical bounds are
                // not rotated
                if (rotated[i]) continue;
                if (tempLogicalBounds[i] == null) continue;

                Rectangle2D glyphBounds = tempLogicalBounds[i].getBounds2D();
                double      y           = glyphBounds.getMinY();
                double      height      = glyphBounds.getHeight();

                if ((i < getNumGlyphs()-1) && 
                    (tempLogicalBounds[i+1] != null)) {
                    // make this glyph extend to the start of the next one
                    Rectangle2D nextGlyphBounds = 
                        tempLogicalBounds[i+1].getBounds2D();
                    if (nextGlyphBounds.getY() > y) { // going top to bottom
                        height = nextGlyphBounds.getY() - y;
                    } else {
                        double newGlyphY = (nextGlyphBounds.getY() + 
                                            nextGlyphBounds.getHeight());
                        height += (y - newGlyphY);
                        y = newGlyphY;
                    }
                }

                float x0 = (float)fullBounds.getMinX();
                float x1 = (float)(x0+fullBounds.getWidth());
                float y0 = (float)y;
                float y1 = (float)(y0+height);
                // Build the rect the way things expect to see it...
                if (flippedH[i]) {
                    if (flippedV[i]) {
                        GeneralPath gp = new GeneralPath();
                        gp.moveTo(x1,y1);
                        gp.lineTo(x0,y1);
                        gp.lineTo(x0,y0);
                        gp.lineTo(x1,y0);
                        gp.lineTo(x1,y1);
                        gp.closePath();
                        tempLogicalBounds[i] = gp;
                    } else {
                        GeneralPath gp = new GeneralPath();
                        gp.moveTo(x1,y0);
                        gp.lineTo(x0,y0);
                        gp.lineTo(x0,y1);
                        gp.lineTo(x1,y1);
                        gp.lineTo(x1,y0);
                        gp.closePath();
                        tempLogicalBounds[i] = gp;
                    }
                } else {
                    if (flippedV[i]) {
                        GeneralPath gp = new GeneralPath();
                        gp.moveTo(x0,y1);
                        gp.lineTo(x1,y1);
                        gp.lineTo(x1,y0);
                        gp.lineTo(x0,y0);
                        gp.lineTo(x0,y1);
                        gp.closePath();
                        tempLogicalBounds[i] = gp;
                    } else {
                        tempLogicalBounds[i] = new Rectangle2D.Double
                            (x0, y0, x1-x0, y1-y0);
                    }
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
        if (glyphIndex == glyphs.length)
            return endPos;

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
        if ((beginGlyphIndex+numEntries) > glyphs.length+1) {
             throw new IndexOutOfBoundsException("beginGlyphIndex + numEntries ("
                       + beginGlyphIndex + "+" + numEntries
                       + ") exceeds the number of glpyhs in this GlyphVector");
        }
        if (positionReturn == null) {
            positionReturn = new float[numEntries*2];
        }
        if ((beginGlyphIndex+numEntries) == glyphs.length+1) {
            numEntries--;
            positionReturn[numEntries*2]   = (float)endPos.getX();
            positionReturn[numEntries*2+1] = (float)endPos.getY();
        }
        for (int i = beginGlyphIndex; i < (beginGlyphIndex+numEntries); i++) {
            Point2D glyphPos;
            glyphPos = glyphs[i].getPosition();
            positionReturn[(i-beginGlyphIndex)*2]     = (float)glyphPos.getX();
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
     * Returns a tight bounds on the GylphVector including stroking.
     */
    public Rectangle2D getBounds2D(AttributedCharacterIterator aci) {
        // System.out.println("GlyphVector.getBounds2D Called: " + this);
        aci.first();
        TextPaintInfo tpi = (TextPaintInfo)aci.getAttribute(PAINT_INFO);
        if ((bounds2D != null) &&
            TextPaintInfo.equivilent(tpi, cacheTPI))
            return bounds2D;

        Rectangle2D b=null;
        for (int i = 0; i < getNumGlyphs(); i++) {
            if (!glyphVisible[i])  continue;

            Rectangle2D glyphBounds = glyphs[i].getBounds2D();
            // System.out.println("GB["+i+"]: " + glyphBounds);
            if (glyphBounds == null) continue;
            if (b == null) b=glyphBounds;
            else b = glyphBounds.createUnion(b);
        }

        bounds2D = b;
        if ( bounds2D == null ){
            bounds2D = new Rectangle2D.Float();
        }
        cacheTPI = tpi;
        return bounds2D;
    }

    /**
     *  Returns the logical bounds of this GlyphVector.
     * This is a bound useful for hit detection and highlighting.
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
     * Returns the geometric bounds of this GlyphVector. The geometric
     * bounds is the tightest rectangle enclosing the geometry of the
     * glyph vector (not including stroke).
     */
    public Rectangle2D getGeometricBounds() {
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
            bounds2D = null;
        }
        endPos = new Point2D.Float(currentX, currentY);
    }

    /**
     * Sets the position of the specified glyph within this GlyphVector.
     */
    public void setGlyphPosition(int glyphIndex, Point2D newPos)
                                 throws IndexOutOfBoundsException {
        if (glyphIndex == glyphs.length) {
            endPos = (Point2D)newPos.clone();
            return;
        }

        if (glyphIndex < 0 || (glyphIndex > glyphs.length-1)) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex
            + ", is out of bounds. Should be between 0 and " + (glyphs.length-1) + ".");
        }
        glyphs[glyphIndex].setPosition(newPos);
        glyphLogicalBounds[glyphIndex] = null;
        outline = null;
        bounds2D = null;
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
        bounds2D = null;
        logicalBounds = null;
    }

    /**
     * Tells the glyph vector whether or not to draw the specified glyph.
     */
    public void setGlyphVisible(int glyphIndex, boolean visible) {
        if (visible == glyphVisible[glyphIndex]) 
            return;

        glyphVisible[glyphIndex] = visible;
        outline = null;
        bounds2D = null;
        logicalBounds = null;
        glyphLogicalBounds[glyphIndex] = null;
    }

    /**
     * Returns true if specified glyph will be rendered.
     */
    public boolean isGlyphVisible(int glyphIndex) {
        return glyphVisible[glyphIndex];
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
            Glyph glyph = glyphs[i];
            if (glyph.getGlyphCode() == -1) {
                // Missing glyph mapps to just one char...
                numChars++;
            } else {
                String glyphUnicode = glyph.getUnicode();
                numChars += glyphUnicode.length();
            }
        }
        return numChars;
    }

    /**
     * Draws this glyph vector.
     */
    public void draw(Graphics2D graphics2D, 
                     AttributedCharacterIterator aci) {
        for (int i = 0; i < glyphs.length; i++) {
            if (glyphVisible[i]) {
                glyphs[i].draw(graphics2D);
            }
        }
    }
}

