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
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.font.TextLayout;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.text.AttributedCharacterIterator;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.text.TextSpanLayout;
import org.apache.batik.gvt.text.TextHit;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;

/**
 * Implementation of TextSpanLayout that uses java.awt.font.TextLayout
 * for its internals.
 * @see java.awt.font.TextLayout
 * @see org.apache.batik.gvt.TextPainter.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class TextLayoutAdapter implements TextSpanLayout {

    private TextLayout layout;
    private AttributedCharacterIterator aci;
    private Point2D offset;

    public TextLayoutAdapter(TextLayout layout, Point2D offset, AttributedCharacterIterator aci) {
        this.layout = layout;
        this.aci = aci;
        this.offset = adjustOffset(offset);
    }

    /**
     * Paints the specified text layout using the
     * specified Graphics2D and rendering context.
     * @param g2d the Graphics2D to use
     */
    public void draw(Graphics2D g2d) {
        layout.draw(g2d, (float) offset.getX(), (float) offset.getY());
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
        this.offset = offset;
    }

    /**
     * Returns the outline of the completed glyph layout, transformed
     * by an AffineTransform.
     */
    public Shape getOutline() {
        AffineTransform nt = AffineTransform.getTranslateInstance(
               offset.getX(), offset.getY());
        return layout.getOutline(nt);
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
             g.append(getUnderlineShape(aci, layout), false);
        }
        if ((decorationType & DECORATION_STRIKETHROUGH) != 0) {
             g.append(getStrikethroughShape(aci, layout), false);
        }
        if ((decorationType & DECORATION_OVERLINE) != 0) {
             g.append(getOverlineShape(aci, layout), false);
        }
        AffineTransform nt = AffineTransform.getTranslateInstance(
               offset.getX(), offset.getY());
        return nt.createTransformedShape(g);
    }

    /**
     * Returns the rectangular bounds of the completed glyph layout.
     */
    public Rectangle2D getBounds() {
        Rectangle2D bounds = layout.getBounds();
        return new Rectangle2D.Double(bounds.getX()+offset.getX(),
                                      bounds.getY()+offset.getY(),
                                      bounds.getWidth(),
                                      bounds.getHeight());
    }

    /**
     * Returns the rectangular bounds of the completed glyph layout.
     */
    public Rectangle2D getDecoratedBounds() {
        Rectangle2D dbounds = getDecorationOutline(
          DECORATION_UNDERLINE|DECORATION_OVERLINE|DECORATION_STRIKETHROUGH
              ).getBounds2D();
       return dbounds.createUnion(getBounds());
    }

    /**
     * Returns the dimension of the completed glyph layout in the
     * primary text advance direction (e.g. width, for RTL or LTR text).
     * (This is the dimension that should be used for positioning
     * adjacent layouts.)
     */
    public float getAdvance() {
        return layout.getAdvance();
    }

    /**
     * Returns the current text position at the completion
     * of glyph layout.
     * (This is the position that should be used for positioning
     * adjacent layouts.)
     */
    public Point2D getAdvance2D() {
        return new Point2D.Float(layout.getAdvance(), 0f);
    }

    /**
     * Returns a Shape which encloses the currently selected glyphs
     * as specified by glyph indices <tt>begin/tt> and <tt>end</tt>.
     * @param begin the index of the first glyph in the contiguous selection.
     * @param end the index of the last glyph in the contiguous selection.
     */
    public Shape getLogicalHighlightShape(int begin, int end) {
        AffineTransform nt = AffineTransform.getTranslateInstance(
                                           offset.getX(), offset.getY());
        return nt.createTransformedShape(
                  layout.getLogicalHighlightShape(begin, end));
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
        TextHitInfo hit = layout.hitTestChar(x-(float) offset.getX(),
                                             y-(float) offset.getY());
        return new TextHit(hit.getCharIndex(), hit.isLeadingEdge());
    }

    public boolean isVertical() {
        return layout.isVertical();
    }

    public int getCharacterCount() {
        return layout.getCharacterCount();
    }

//protected

    /**
     * Returns a shape describing the overline decoration for a given ACI.
     * Does not rely on TextLayout's
     * internal strikethrough but computes it manually.
     */
    protected Shape getOverlineShape(AttributedCharacterIterator runaci,
                                          TextLayout layout) {
        double y = layout.getBaselineOffsets()[java.awt.Font.ROMAN_BASELINE] -
                layout.getAscent();
        Stroke overlineStroke =
            new BasicStroke(getDecorationThickness(runaci, layout));
        return overlineStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           0f, y,
                           layout.getAdvance(), y));
    }

    /**
     * Returns a shape describing the strikethrough line for a given ACI.
     * Does not rely on TextLayout's
     * internal strikethrough but computes it manually.
     */
    protected Shape getUnderlineShape(AttributedCharacterIterator runaci,
                                          TextLayout layout) {
        double y = (layout.getBaselineOffsets()[java.awt.Font.ROMAN_BASELINE]
                        + layout.getDescent())/2;
        Stroke underlineStroke =
            new BasicStroke(getDecorationThickness(runaci, layout));

        return underlineStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           0f, y,
                           layout.getAdvance(), y));
    }

    /**
     * Returns a shape describing the strikethrough line for a given ACI.
     * Does not rely on TextLayout's
     * internal strikethrough but computes it manually.
     */
    protected Shape getStrikethroughShape(AttributedCharacterIterator runaci,
                                          TextLayout layout) {
        double y = (layout.getBaselineOffsets()[java.awt.Font.ROMAN_BASELINE]
             - layout.getAscent())/3;
                     // XXX: 3 is a hack for cosmetic reasons
        // TODO: the strikethrough offset should be calculated
        // from the font instead!
        Stroke strikethroughStroke =
            new BasicStroke(getDecorationThickness(runaci, layout));

        return strikethroughStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           0f, y,
                           layout.getAdvance(), y));
    }

    protected float getDecorationThickness(AttributedCharacterIterator aci,
                                                         TextLayout layout) {
            // thickness divisor: text decoration thickness is
            // equal to the text advance divided by this number
            float thick_div;
            Object textWeight = aci.getAttribute(TextAttribute.WEIGHT);
            if (textWeight == TextAttribute.WEIGHT_REGULAR) {
                thick_div = 14f;
            } else if (textWeight == TextAttribute.WEIGHT_BOLD) {
                thick_div = 11f;
            } else if (textWeight == TextAttribute.WEIGHT_LIGHT) {
                thick_div = 18f;
            } else if (textWeight == TextAttribute.WEIGHT_DEMIBOLD) {
                thick_div = 12f;
            } else if (textWeight == TextAttribute.WEIGHT_DEMILIGHT) {
                thick_div = 16f;
            } else if (textWeight == TextAttribute.WEIGHT_EXTRABOLD) {
                thick_div = 10f;
            } else if (textWeight == TextAttribute.WEIGHT_EXTRA_LIGHT) {
                thick_div = 20f;
            } else if (textWeight == TextAttribute.WEIGHT_SEMIBOLD) {
                thick_div = 13f;
            } else if (textWeight == TextAttribute.WEIGHT_ULTRABOLD) {
                thick_div = 9f;
            } else {
                thick_div = 14f;
            }
        return layout.getAscent()/thick_div;
    }

// private

    private Point2D adjustOffset(Point2D p) {

        aci.first();
        Float X = (Float) aci.getAttribute(
                   GVTAttributedCharacterIterator.TextAttribute.X);
        Float Y = (Float) aci.getAttribute(
                   GVTAttributedCharacterIterator.TextAttribute.Y);

        if ((X == null) || (X.isNaN())) {
              X = new Float((float) p.getX());
        }

        if ((Y == null) || (Y.isNaN())) {
              Y = new Float((float) p.getY());
        }

        return new Point2D.Float(X.floatValue(), Y.floatValue());
    }

}
