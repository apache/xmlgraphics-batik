/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.text;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.font.GlyphVector;
import java.awt.font.GlyphMetrics;
import java.awt.font.LineMetrics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.text.AttributedString;

import org.apache.batik.gvt.text.TextSpanLayout;
import org.apache.batik.gvt.text.TextHit;

/**
 * Implementation of TextSpanLayout which uses java.awt.font.GlyphVector.
 * @see org.apache.batik.gvt.TextSpanLayout.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class GlyphLayout implements TextSpanLayout {

    private GlyphVector gv;
    private Font font;
    private LineMetrics metrics;
    private AttributedCharacterIterator aci;
    private CharacterIterator ci;
    private FontRenderContext frc;

    /**
     * Paints the specified text layout using the
     * specified Graphics2D and rendering context.
     * @param g2d the Graphics2D to use
     * @param x the x position of the rendered layout origin.
     * @param y the y position of the rendered layout origin.
     */
    public GlyphLayout(AttributedCharacterIterator aci, FontRenderContext frc) {
        this.aci = aci;
        this.frc = frc;
        this.font = getFont(aci);
        this.metrics = font.getLineMetrics(
                           aci, aci.getBeginIndex(), aci.getEndIndex(), frc);
        ci = new ReorderedCharacterIterator(aci);
        this.gv = font.createGlyphVector(frc, ci);
        this.gv.performDefaultLayout();
    }

    /**
     * Paints the specified text layout using the
     * specified Graphics2D and rendering context.
     * @param g2d the Graphics2D to use
     * @param x the x position of the rendered layout origin.
     * @param y the y position of the rendered layout origin.
     */
    public void draw(Graphics2D g2d, float x, float y) {
        g2d.drawGlyphVector(gv, x, y);
    }

    /**
     * Returns the outline of the completed glyph layout, transformed
     * by an AffineTransform.
     * @param t an AffineTransform to apply to the outline before returning it.
     */
    public Shape getOutline(AffineTransform t) {
        Shape s;

        if (t.getType() == AffineTransform.TYPE_TRANSLATION) {
           s = gv.getOutline((float) t.getTranslateX(), 
                             (float) t.getTranslateY());
        } else {
           s = t.createTransformedShape(gv.getOutline(0f, 0f));
        }
        return s;
    }

    /**
     * Returns the outline of the specified decorations on the glyphs, 
     * transformed by an AffineTransform.
     * @param decorationType an integer indicating the type(s) of decorations
     *     included in this shape.  May be the result of "OR-ing" several
     *     values together: 
     * e.g. <tt>DECORATION_UNDERLINE | DECORATION_STRIKETHROUGH</tt>
     * @param t an AffineTransform to apply to the outline before returning it.
     */
    public Shape getDecorationOutline(int decorationType, AffineTransform t) {
        GeneralPath g = new GeneralPath();
        if ((decorationType & DECORATION_UNDERLINE) != 0) {
             g.append(getUnderlineShape(), false);
        }
        if ((decorationType & DECORATION_STRIKETHROUGH) != 0) {
             g.append(getStrikethroughShape(), false);
        }
        if ((decorationType & DECORATION_OVERLINE) != 0) {
             g.append(getOverlineShape(), false);
        }
        return t.createTransformedShape(g);
    }

    /**
     * Returns the rectangular bounds of the completed glyph layout.
     */
    public Rectangle2D getBounds() {
        return gv.getVisualBounds();
    }

    /**
     * Returns the rectangular bounds of the completed glyph layout,
     * inclusive of "decoration" (underline, overline, etc.)
     */
    public Rectangle2D getDecoratedBounds() {
        return getBounds().createUnion(
            getDecorationOutline(
                DECORATION_ALL, new AffineTransform()).getBounds2D());
    }

    /**
     * Returns the dimension of the completed glyph layout in the
     * primary text advance direction (e.g. width, for RTL or LTR text).
     * (This is the dimension that should be used for positioning 
     * adjacent layouts.)
     */
    public float getAdvance() {
        return (float) gv.getLogicalBounds().getWidth();
    }

    /**
     * Returns a Shape which encloses the currently selected glyphs
     * as specified by glyph indices <tt>begin/tt> and <tt>end</tt>.
     * @param begin the index of the first glyph in the contiguous selection.
     * @param end the index of the last glyph in the contiguous selection.
     */
    public Shape getLogicalHighlightShape(int begin, int end) {

        Rectangle2D shape = null;
        begin = Math.max(0, begin);
        end = Math.min(end, gv.getNumGlyphs());
        for (int i=begin; i<end; ++i) {
            Shape gbounds = gv.getGlyphLogicalBounds(i);
            // XXX !!! there is a bug somewhere in GlyphVector!
            // the glyph logical bounds returned above don't contain the
            // glyph !
  
            Rectangle2D gbounds2d = gbounds.getBounds2D();
            gbounds = new Rectangle2D.Double(gbounds2d.getX(), gbounds2d.getY(),
                                    gbounds2d.getWidth(),
                                    gbounds2d.getHeight()-gbounds2d.getY());
            gbounds2d = gbounds.getBounds2D();
            if (shape == null) {
               shape = gbounds2d;
            } else {
               shape.add(gbounds2d);
            }
            // XXX: FIXME: should not always be a rectangle 
            // (oblique text case, for instance)
            // also, gbounds2d does not seem to be a correct
            // enclosing rectangle in all cases (maybe we need to use
            // a transform ?)

        }

        return shape;
    }
    
    /**
     * Perform hit testing for coordinate at x, y.
     * @return a TextHit object encapsulating the character index for
     *     successful hits and whether the hit is on the character 
     *     leading edge.
     * @param x the x coordinate of the point to be tested.
     * @param y the y coordinate of the point to be tested.
     */
    public TextHit hitTestChar(float x, float y) {
        int begin = 0;
        int end = gv.getNumGlyphs();
        TextHit textHit;
        GlyphMetrics gm;
        float maxX = (float) gv.getVisualBounds().getX();
        for (int i=begin; i<end; ++i) {
            Shape gbounds = gv.getGlyphLogicalBounds(i);

            // XXX !!! there is a bug somewhere in GlyphVector!
            // The gbounds above is not correct!
  
            Rectangle2D gbounds2d = gbounds.getBounds2D();
            gbounds = new Rectangle2D.Double(gbounds2d.getX(), gbounds2d.getY(),
                                    gbounds2d.getWidth(),
                                    gbounds2d.getHeight()-gbounds2d.getY());
            gbounds2d = gbounds.getBounds2D();

            if (gbounds2d.getX()+gbounds2d.getWidth() > maxX) {
                maxX = (float) (gbounds2d.getX()+gbounds2d.getWidth());
            }

            //System.out.println("x,y: "+x+" "+y);
            //System.out.println("glyph "+i+" - bounds "+gbounds);

            if (gbounds.contains(x, y)) {
                gm = gv.getGlyphMetrics(i);
                boolean isRightHalf = 
                    (x > (gbounds2d.getX()+(gbounds2d.getWidth()/2d)));
                boolean isLeadingEdge = !isRightHalf;
                textHit = new TextHit(i, isLeadingEdge);
              //System.out.println("Hit at "+i+", leadingEdge "+isLeadingEdge);
                return textHit;
            }
        }
        // XXX: fallthrough below is invalid for text whose primary 
        // direction is not LTR
        if (x >= maxX) {
            textHit = new TextHit(end, false);
        } else {
            textHit = new TextHit(0, true);
        }

        return textHit;
    }

    /**
     * Returns true if the advance direction of this text is vertical.
     */
    public boolean isVertical() {
        // TODO: Implement this!
        return false;
    }

    /**
     * Returns the number of characters in this layout.
     */
    public int getCharacterCount() {
        return gv.getNumGlyphs();
        // XXX: probably wrong for CTL, work to be done here!
    }

//inner classes

    protected class ReorderedCharacterIterator implements CharacterIterator {

        private int ndx;
        private int begin;
        private int end;
        private char[] c;

        ReorderedCharacterIterator(AttributedCharacterIterator aci) {

            int aciIndex = aci.getBeginIndex();
            begin = 0;
            end = aci.getEndIndex()-aciIndex;
            ndx = begin;
            c = new char[end-begin];

            // set increment and initial array index according to 
            // run direction of this aci

            int inc = (aci.getAttribute(TextAttribute.RUN_DIRECTION) == 
                                    TextAttribute.RUN_DIRECTION_LTR) ? 1 : -1;

            ndx = (inc > 0) ? begin : end-1;

            // reordering section
            char ch = aci.first(); 
            while (ch != CharacterIterator.DONE) {

                 // get BiDi embedding
                 Integer embed = (Integer) aci.getAttribute(TextAttribute.BIDI_EMBEDDING);  
                 //System.out.println("BiDi embedding level : "+embed);
                 // get BiDi span
                 int runLimit = aci.getRunLimit(TextAttribute.BIDI_EMBEDDING);  
                
                 boolean isReversed = false;
                 int runEndNdx = ndx;

                 if (embed != null) {
                     isReversed = (Math.abs(Math.IEEEremainder((double)embed.intValue(), 2d)) < 0.1) ? false : true;
                     if (isReversed) {                         
                         runEndNdx = ndx + inc*(runLimit-aciIndex);
                         inc = -inc;
                         ndx = runEndNdx + inc;
                     }
                 }

                 for (;aciIndex < runLimit; ch=aci.next(),++aciIndex) {
                      c[ndx] = ch;
                      //System.out.println("Setting c["+ndx+"] to "+ch);
                      ndx += inc;
                 }
                 if (isReversed) { // undo the reversal, run is done
                      ndx = runEndNdx;
                      inc = -inc;
                 }
            }

            aci.first();
            ndx = begin;
        }

        public Object clone() {
            return new ReorderedCharacterIterator(
                           (AttributedCharacterIterator) aci.clone());
        }

        public char current() {
            return c[ndx];
        }

        public char first() {
            ndx=begin;
            return c[ndx];
        }

        public int getBeginIndex() {
            return begin;
        }

        public int getEndIndex() {
            return end;
        }

        public int getIndex() {
            return ndx;
        }

        public char last() {
            ndx = end-1;
            return c[end-1];
        }

        public char next() {
            ++ndx;
            if (ndx >= end) {
                ndx = end;
                return CharacterIterator.DONE;
            }
            else return c[ndx];
        }

        public char previous() {
            --ndx;
            if (ndx < begin) {
                ndx = begin;
                return c[ndx];
            }
            else return c[ndx];
        }

        public char setIndex(int position) {
            if (position < begin || position > end) {
                throw new IllegalArgumentException();
            }
            ndx = position;
            return c[ndx];
        }
    }

//protected

    /**
     * Returns a shape describing the overline decoration for a given ACI.
     */
    protected Shape getOverlineShape() {
        double y = metrics.getBaselineOffsets()[java.awt.Font.ROMAN_BASELINE] -
                metrics.getAscent();
        Stroke overlineStroke =
            new BasicStroke(metrics.getUnderlineThickness());
        return overlineStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           0f, y,
                           getAdvance(), y));
    }

    /**
     * Returns a shape describing the strikethrough line for a given ACI.
     */
    protected Shape getUnderlineShape() {
        double y = metrics.getUnderlineOffset();
        Stroke underlineStroke =
            new BasicStroke(metrics.getUnderlineThickness());

        return underlineStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           0f, y,
                           getAdvance(), y));
    }

    /**
     * Returns a shape describing the strikethrough line for a given ACI.
     */
    protected Shape getStrikethroughShape() {
        double y = metrics.getStrikethroughOffset();
        Stroke strikethroughStroke =
            new BasicStroke(metrics.getStrikethroughThickness());

        return strikethroughStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           0f, y,
                           getAdvance(), y));
    }

    protected Font getFont(AttributedCharacterIterator aci) {
        aci.first();
        return new Font(aci.getAttributes());
    }

}
