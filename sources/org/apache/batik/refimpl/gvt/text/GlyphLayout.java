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
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.text.AttributedString;

import org.apache.batik.gvt.text.TextSpanLayout;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
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
    private Point2D advance;
    private Point2D offset;
    private Point2D prevCharPosition;
    protected Shape[] glyphLogicalBounds;

    /**
     * Creates the specified text layout using the
     * specified AttributedCharacterIterator and rendering context.
     * @param aci the AttributedCharacterIterator whose text is to
     *  be laid out
     * @param frc the FontRenderContext to use for generating glyphs.
     */
    public GlyphLayout(AttributedCharacterIterator aci, Point2D offset,
                          FontRenderContext frc) {
        this.aci = aci;
        this.frc = frc;
        this.font = getFont(aci);
        this.metrics = font.getLineMetrics(
                           aci, aci.getBeginIndex(), aci.getEndIndex(), frc);
        ci = new ReorderedCharacterIterator(aci);
        this.gv = font.createGlyphVector(frc, ci);
        this.gv.performDefaultLayout();
        this.offset = offset;
        doExplicitGlyphLayout();
    }

    /**
     * Paints the specified text layout using the
     * specified Graphics2D and rendering context.
     * @param g2d the Graphics2D to use
     * @param x the x position of the rendered layout origin.
     * @param y the y position of the rendered layout origin.
     */
    public void draw(Graphics2D g2d) {
        g2d.drawGlyphVector(gv, 0f, 0f);
    }

    /**
     * Returns the outline of the completed glyph layout, transformed
     * by an AffineTransform.
     * @param t an AffineTransform to apply to the outline before returning it.
     */
    public Shape getOutline() {
        return gv.getOutline(0f, 0f);
    }

    /**
     * Returns the current text position at the beginning
     * of glyph layout, before the application of explicit
     * glyph positioning attributes.
     */
    public Point2D getOffset() {
        return offset;
    }

    /**
     * Sets the text position used for the implicit origin
     * of glyph layout. Ignored if multiple explicit glyph
     * positioning attributes are present in ACI
     * (e.g. if the aci has multiple X or Y values).
     */
    public void setOffset(Point2D offset) {
        System.out.println("Offset set to "+offset);
        this.offset = offset;
    }

    /**
     * Returns the outline of the specified decorations on the glyphs,
     * transformed by an AffineTransform.
     * @param decorationType an integer indicating the type(s) of decorations
     *     included in this shape.  May be the result of "OR-ing" several
     *     values together:
     * e.g. <tt>DECORATION_UNDERLINE | DECORATION_STRIKETHROUGH</tt>
     */
    public Shape getDecorationOutline(int decorationType) {
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
        return g;
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
                DECORATION_ALL).getBounds2D());
    }

    /**
     * Returns the dimension of the completed glyph layout in the
     * primary text advance direction (e.g. width, for RTL or LTR text).
     * (This is the dimension that should be used for positioning
     * adjacent layouts.)
     */
    public float getAdvance() {
        return (float) advance.getX();
    }

    /**
     * Returns the current text position at the completion
     * of glyph layout.
     * (This is the position that should be used for positioning
     * adjacent layouts.)
     */
    public Point2D getAdvance2D() {
        return advance;
    }

    /**
     * Returns a Shape which encloses the currently selected glyphs
     * as specified by glyph indices <tt>begin/tt> and <tt>end</tt>.
     * @param begin the index of the first glyph in the contiguous selection.
     * @param end the index of the last glyph in the contiguous selection.
     */
    public Shape getLogicalHighlightShape(int begin, int end) {

        GeneralPath shape = null;
        begin = Math.max(0, begin);
        end = Math.min(end, gv.getNumGlyphs());

        if (begin == 0 && end == gv.getNumGlyphs()) {
           shape = new GeneralPath(getBounds());
        } else {
            for (int i=begin; i<end; ++i) {

                Shape gbounds = getGlyphLogicalBounds(i);
                Rectangle2D gbounds2d = gbounds.getBounds2D();

                if (shape == null) {
                   shape = new GeneralPath(gbounds2d);
                } else {
                   shape.append(gbounds2d, false);
                }

            }
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
            Shape gbounds = getGlyphLogicalBounds(i);

            Rectangle2D gbounds2d = gbounds.getBounds2D();

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

        // fallthrough: in text bbox but not on a glyph
        textHit = new TextHit(-1, false);

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


    protected Shape getGlyphLogicalBounds(int i) {

        // We can't use GlyphVector.getGlyphLogicalBounds(i)
        // since it seems to have a nasty bug!

        return glyphLogicalBounds[i];
    }


    // private

    private void computeGlyphLogicalBounds() {

        int c = gv.getNumGlyphs();

        glyphLogicalBounds = new Rectangle2D.Double[c];

        Rectangle2D.Double lbox = null;

        for (int i=0; i<c; ++i) {

            GlyphMetrics gm = gv.getGlyphMetrics(i);
            Rectangle2D gbounds2d = gm.getBounds2D();
            Point2D gpos = gv.getGlyphPosition(i);
            lbox = new Rectangle2D.Double(
                                    gpos.getX()+gbounds2d.getX(),
                                    gpos.getY()+gbounds2d.getY(),
                                    gbounds2d.getWidth(),
                                    gbounds2d.getHeight());

            glyphLogicalBounds[i] = lbox;
        }

        for (int i=0; i<c; ++i) {

            int begin = i;
            int end = begin;
            Point2D gpos = gv.getGlyphPosition(begin);

            // calculate a "run" over the same y nominal position,
            // over which the glyphs have positive 'x' advances.
            // (means that RTL "runs" are not yet supported, sorry)

            float y = (float) gpos.getY();
            float x = (float) gpos.getX();
            lbox = (Rectangle2D.Double) glyphLogicalBounds[begin];
            float miny = (float) lbox.getY();
            float maxy = (float) (lbox.getY() + lbox.getHeight());
            float currY = y;
            float currX = x;
            while (end<c) {
                lbox = (Rectangle2D.Double) glyphLogicalBounds[end];
                currY = (float) gv.getGlyphPosition(end).getY();
                currX = (float) gv.getGlyphPosition(end).getX();
                if ((currX < x) || (currY != y)) {
                    if (end > begin) --end;
                    break;
                }
                miny =
                  Math.min((float) lbox.getY(), miny);
                float h = (float) (lbox.getY() + lbox.getHeight());
                maxy =
                  Math.max(h, maxy);
                ++end;
            }
            i = end;

            Rectangle2D.Double lboxPrev = null;

            for (int n=begin; n<end; ++n) {

                // extend the vertical bbox for this run
                lbox = (Rectangle2D.Double) glyphLogicalBounds[n];

                x = (float) lbox.getX();
                // adjust left bounds if not first in run

                if (lboxPrev != null) {
                    x = (float) (x + (lboxPrev.getX()+lboxPrev.getWidth()))/2f;
                    glyphLogicalBounds[n-1] =
                         new Rectangle2D.Double(
                              lboxPrev.getX(), lboxPrev.getY(),
                              (double) (x - lboxPrev.getX()),
                              lboxPrev.getHeight());
                }

                lbox =
                     new Rectangle2D.Double(
                          (double) x, (double) miny,
                          lbox.getWidth()+(lbox.getX() - x), (double) (maxy - miny));

                lboxPrev = lbox;
                glyphLogicalBounds[n] = lbox;
            }
        }
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

    protected void doExplicitGlyphLayout() {
        char ch = aci.first();
        int i=0;
        float[] gp = new float[(gv.getNumGlyphs()+1)*2];
        gp = (float[]) gv.getGlyphPositions(0, gv.getNumGlyphs(), gp).clone();
        float curr_x_pos = gp[0] + (float) offset.getX();
        float curr_y_pos = gp[1] + (float) offset.getY();
        //System.out.print("Explicit layout for: ");
        while ((ch != CharacterIterator.DONE) && (i < gp.length/2)) {
            Float x = (Float) aci.getAttribute(
                             GVTAttributedCharacterIterator.TextAttribute.X);
            Float dx = (Float) aci.getAttribute(
                             GVTAttributedCharacterIterator.TextAttribute.DX);
            Float y = (Float) aci.getAttribute(
                             GVTAttributedCharacterIterator.TextAttribute.Y);
            Float dy = (Float) aci.getAttribute(
                             GVTAttributedCharacterIterator.TextAttribute.DY);
            if (x != null) {
                //System.out.println("X explicit");
                curr_x_pos = x.floatValue();
            } else if (dx != null) {
                //System.out.println("DX explicit");
                curr_x_pos += dx.floatValue();
            }

            if (y != null) {
                curr_y_pos = y.floatValue();
            } else if (dy != null) {
                curr_y_pos += dy.floatValue();
            } else if (i>0) {
                curr_y_pos += gp[i*2 + 1]-gp[i*2 - 1];
            }
            gv.setGlyphPosition(i, new Point2D.Float(curr_x_pos,  curr_y_pos));
            //System.out.print(ch);
            //System.out.print("["+curr_x_pos+","+curr_y_pos+"]");
            curr_x_pos += (float) gv.getGlyphMetrics(i).getAdvance();
            ch = aci.next();
            ++i;
        }
        //System.out.println();

        advance = new Point2D.Float((float) (curr_x_pos-offset.getX()),
                                    (float) (curr_y_pos-offset.getY()));
        computeGlyphLogicalBounds();
    }

}
