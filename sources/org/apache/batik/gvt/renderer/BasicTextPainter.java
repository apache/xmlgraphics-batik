/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.TextPainter;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextSpanLayout;
import org.apache.batik.gvt.text.TextLayoutFactory;
import org.apache.batik.gvt.text.TextHit;
import org.apache.batik.gvt.text.ConcreteTextLayoutFactory;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Composite;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import java.awt.font.FontRenderContext;

/**
 * Basic implementation of TextPainter which
 * renders the attributed character iterator of a <tt>TextNode</tt>.
 * Suitable for use with "standard" java.awt.font.TextAttributes only.
 * @see java.awt.font.TextAttribute
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @author <a href="vincent.hardy@sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public abstract class BasicTextPainter implements TextPainter {

    /**
     * Paints the specified text node using the
     * specified Graphics2D and rendering context.
     * @see org.apache.batik.gvt.TextPainter
     * @param node the text node to paint
     * @param g2d the Graphics2D to use
     * @param context the rendering context.
     */
    public abstract void paint(TextNode node,
                      Graphics2D g2d, GraphicsNodeRenderContext context); // {
/*
        FontRenderContext frc = context.getFontRenderContext();
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        /* XXX:  The code below only
         *     works for J2SE base implementation of AttributeCharacterIterator
         */
/*
        TextSpanLayout layout =
            getOffsetAdjustedTextLayout(aci, node.getLocation(), frc);

        layout.draw(g2d);
    }
*/
    private static TextLayoutFactory textLayoutFactory =
                               new ConcreteTextLayoutFactory();

    protected TextLayoutFactory getTextLayoutFactory() {
        return textLayoutFactory;
    }

    /**
     * Given an X, y coordinate,
     * AttributedCharacterIterator, and GraphicsNodeRenderContext,
     * return a Mark which encapsulates a "selection start" action.
     * The standard order of method calls for selection is:
     * selectAt(); [selectTo(),...], selectTo(); getSelection().
     */
    public org.apache.batik.gvt.text.Mark selectAt(double x, double y,
                         AttributedCharacterIterator aci,
                         TextNode node,
                         GraphicsNodeRenderContext context) {

        org.apache.batik.gvt.text.Mark
              newMark = hitTest(x, y, aci, node, context);
        cachedHit = null;
        return newMark;
    }

    /**
     * Given an X, y coordinate, starting Mark,
     * AttributedCharacterIterator, and GraphicsNodeRenderContext,
     * return a Mark which encapsulates a "selection continued" action.
     * The standard order of method calls for selection is:
     * selectAt(); [selectTo(),...], selectTo(); getSelection().
     */
    public org.apache.batik.gvt.text.Mark selectTo(double x, double y,
                            org.apache.batik.gvt.text.Mark beginMark,
                            AttributedCharacterIterator aci,
                            TextNode node,
                            GraphicsNodeRenderContext context) {
        org.apache.batik.gvt.text.Mark newMark =
             hitTest(x, y, aci, node, context);

        return newMark;
    }

    /**
     * Select the entire contents of an
     * AttributedCharacterIterator, and
     * return a Mark which encapsulates that selection action.
     */
    public org.apache.batik.gvt.text.Mark selectAll(double x, double y,
                            AttributedCharacterIterator aci,
                            TextNode node,
                            GraphicsNodeRenderContext context) {
        org.apache.batik.gvt.text.Mark newMark =
                              hitTest(x, y, aci, node, context);
        return newMark;
    }

    /**
     * Returns an array of ints representing begin/end index pairs into
     * an AttributedCharacterIterator which represents the text
     * selection delineated by two Mark instances.
     * <em>Note: The Mark instances passed must have been instantiated by
     * an instance of this enclosing TextPainter implementation.</em>
     */
    public int[] getSelected(AttributedCharacterIterator aci,
                             org.apache.batik.gvt.text.Mark start,
                             org.apache.batik.gvt.text.Mark finish) {
        BasicTextPainter.Mark begin;
        BasicTextPainter.Mark end;
        try {
            begin = (BasicTextPainter.Mark) start;
            end = (BasicTextPainter.Mark) finish;
        } catch (ClassCastException cce) {
            throw new
            Error("This Mark was not instantiated by this TextPainter class!");
        }
        TextSpanLayout layout = null;
        if (begin != null) {
            layout = begin.getLayout();
        }
        if (layout != null) {
            int[] indices = null;
            try {
                indices = new int[2];
                indices[0] = (begin.getHit().isLeadingEdge()) ?
                              begin.getHit().getCharIndex() :
                              begin.getHit().getCharIndex()+1;
                indices[1] = (end.getHit().isLeadingEdge()) ?
                              end.getHit().getCharIndex() :
                              end.getHit().getCharIndex()+1;
                if (indices[0] > indices[1]) {
                    int temp = indices[0];
                    indices[0] = indices[1];
                    indices[1] = temp;
                }
            } catch (Exception e) {
                return null;
            }
            if (indices[0] < 0) {
                indices[0] = 0;
            }

            return indices;
        } else {
            return null;
        }
    }

    /**
     * Return a Shape, in the coordinate system of the text layout,
     * which encloses the text selection delineated by two Mark instances.
     * <em>Note: The Mark instances passed must have been instantiated by
     * an instance of this enclosing TextPainter implementation.</em>
     */
    public abstract Shape getHighlightShape(org.apache.batik.gvt.text.Mark beginMark,
                                   org.apache.batik.gvt.text.Mark endMark);// {

        // TODO: later we can return more complex things like
        // noncontiguous selections

   /*     BasicTextPainter.Mark begin;
        BasicTextPainter.Mark end;
        try {
            begin = (BasicTextPainter.Mark) beginMark;
            end = (BasicTextPainter.Mark) endMark;
        } catch (ClassCastException cce) {
            throw new
            Error("This Mark was not instantiated by this TextPainter class!");
        }

        Shape highlightShape = null;
        TextSpanLayout layout = null;
        if (begin != null) {
            layout = begin.getLayout();
        }

        if (layout != null) {
            int firsthit = 0;
            int lasthit = 0;
            if (begin != end) {
                firsthit = (begin.getHit().isLeadingEdge()) ?
                              begin.getHit().getCharIndex() :
                              begin.getHit().getCharIndex()+1;
                lasthit = (end.getHit().isLeadingEdge()) ?
                              end.getHit().getCharIndex() :
                              end.getHit().getCharIndex()+1;
                if (firsthit > lasthit) {
                    int temp = firsthit;
                    firsthit = lasthit;
                    lasthit = temp;
                }
            } else {
                lasthit = layout.getCharacterCount();
            }
            if (firsthit < 0) {
                firsthit = 0;
            }
            highlightShape = layout.getLogicalHighlightShape(
                                    firsthit,
                                    lasthit);

        }
        return highlightShape;
    }
*/

    /*
     * Get a Rectangle2D in userspace coords which encloses the textnode
     * glyphs composed from an AttributedCharacterIterator.
     * @param node the TextNode to measure
     * @param g2d the Graphics2D to use
     * @param context rendering context.
     */
     public Rectangle2D getBounds(TextNode node,
               FontRenderContext frc) {
         return getBounds(node, frc, false, false);
     }

    /*
     * Get a Rectangle2D in userspace coords which encloses the textnode
     * glyphs composed from an AttributedCharacterIterator, inclusive of
     * glyph decoration (underline, overline, strikethrough).
     * @param node the TextNode to measure
     * @param g2d the Graphics2D to use
     * @param context rendering context.
     */
     public Rectangle2D getDecoratedBounds(TextNode node,
               FontRenderContext frc) {
         return getBounds(node, frc, true, false);
     }

    /*
     * Get a Rectangle2D in userspace coords which encloses the
     * textnode glyphs (as-painted, inclusive of decoration and stroke, but
     * exclusive of filters, etc.) composed from an AttributedCharacterIterator.
     * @param node the TextNode to measure
     * @param g2d the Graphics2D to use
     * @param context rendering context.
     */
     public Rectangle2D getPaintedBounds(TextNode node,
               FontRenderContext frc) {
         Rectangle2D r = getBounds(node, frc, true, true);
         return r;
     }

    /*
     * Get a Rectangle2D in userspace coords which encloses the textnode
     * glyphs composed from an AttributedCharacterIterator.
     * @param node the TextNode to measure
     * @param g2d the Graphics2D to use
     * @param context rendering context.
     * @param includeDecoration whether to include text decoration
     *            in bounds computation.
     * @param includeStrokeWidth whether to include the effect of stroke width
     *            in bounds computation.
     */
     protected abstract Rectangle2D getBounds(TextNode node,
               FontRenderContext context,
               boolean includeDecoration,
               boolean includeStrokeWidth); //{
/*
         AttributedCharacterIterator aci =
             node.getAttributedCharacterIterator();
         TextSpanLayout layout =
             getOffsetAdjustedTextLayout(aci, node.getLocation(), context);

         Rectangle2D bounds;

         if (includeStrokeWidth) {
             Shape s = getStrokeOutline(node, context, includeDecoration);
             if (s != null) {
                 bounds = s.getBounds2D();
             } else {
                 bounds = layout.getBounds();
             }
         } else {
             if (includeDecoration) {
                 bounds = layout.getDecoratedBounds();
             } else {
                 bounds = layout.getBounds();
             }
         }
        return bounds;

     }
*/
   /*
    * Get a Shape in userspace coords which defines the textnode glyph outlines.
    * @param node the TextNode to measure
    * @param frc the font rendering context.
    * @param includeDecoration whether to include text decoration
    *            outlines.
    */
    protected abstract Shape getOutline(TextNode node, FontRenderContext frc,
                                    boolean includeDecoration); // {
/*        Shape outline;
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        TextSpanLayout layout =
            getOffsetAdjustedTextLayout(aci, node.getLocation(), frc);

        outline = layout.getOutline();

        if (includeDecoration) {
            int decorationTypes = 0;
            if (aci.getAttribute(GVTAttributedCharacterIterator.
                                        TextAttribute.UNDERLINE) != null) {
                decorationTypes |= TextSpanLayout.DECORATION_UNDERLINE;
            }
            if (aci.getAttribute(GVTAttributedCharacterIterator.
                                        TextAttribute.OVERLINE) != null) {
                decorationTypes |= TextSpanLayout.DECORATION_OVERLINE;
            }
            if (aci.getAttribute(GVTAttributedCharacterIterator.
                                     TextAttribute.STRIKETHROUGH) != null) {
                decorationTypes |= TextSpanLayout.DECORATION_STRIKETHROUGH;
            }
            if (decorationTypes != 0) {
                if (!(outline instanceof GeneralPath)) {
                    outline = new GeneralPath(outline);
                }
                ((GeneralPath) outline).setWindingRule(
                                           GeneralPath.WIND_NON_ZERO);
                ((GeneralPath) outline).append(
                    layout.getDecorationOutline(decorationTypes), false);
            }

        }

        return outline;
    }
*/
   /*
    * Get a Shape in userspace coords which defines the textnode glyph outlines.
    * @param node the TextNode to measure
    * @param frc the font rendering context.
    */
    public Shape getShape(TextNode node, FontRenderContext frc) {
        return getOutline(node, frc, false);
    }

   /*
    * Get a Shape in userspace coords which defines the
    * decorated textnode glyph outlines.
    * @param node the TextNode to measure
    * @param frc the font rendering context.
    */
    public Shape getDecoratedShape(TextNode node, FontRenderContext frc) {
          return getOutline(node, frc, true);
    }

   /*
    * Get a Shape in userspace coords which defines the
    * stroked textnode glyph outlines.
    * @param node the TextNode to measure
    * @param frc the font rendering context.
    * @param includeDecoration whether to include text decoration
    *            outlines.
    */
    protected Shape getStrokeOutline(TextNode node, FontRenderContext frc,
                                    boolean includeDecoration) {

        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        BasicStroke stroke = (BasicStroke) aci.getAttribute(
                        GVTAttributedCharacterIterator.TextAttribute.STROKE);
        Shape outline = getOutline(node, frc, includeDecoration);
        if (outline != null && (stroke != null)) {
            outline = stroke.createStrokedShape(outline);
        } else {
            outline = null;
        }
        return outline;
    }

    /*
     * Note: this method only works if the layout is constructed
     * from the entire "text chunk"!  If there are multiple
     * text chunks in this node, this code will fail.
     * TODO: fix/replace, removing dependencies on this code!
     */

 /*   private TextSpanLayout getOffsetAdjustedTextLayout(
                     AttributedCharacterIterator aci,
                     Point2D location,
                     FontRenderContext frc) {

        TextSpanLayout layout = getTextLayoutFactory().createTextLayout(aci,
                          location,
                          new java.awt.font.FontRenderContext(
                                            new AffineTransform(),
                                                          true,
                                                          true));

        TextNode.Anchor anchor = (TextNode.Anchor) aci.getAttribute(
                     GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE);
        int anchorType = TextNode.Anchor.ANCHOR_START;
        if (anchor != null) anchorType = anchor.getType();

        Point2D advance = layout.getAdvance2D();

        float dx = 0f;
        float dy = 0f;

        switch(anchorType){
        case TextNode.Anchor.ANCHOR_MIDDLE:
            dx = (float) (-advance.getX()/2d);
            dy = (float) (-advance.getY()/2d);
            break;
        case TextNode.Anchor.ANCHOR_END:
            dx = (float) (-advance.getX());
            dy = (float) (-advance.getY());
            break;
        default:
            // leave untouched
        }

        Point2D offset = layout.getOffset();

        if (layout.isVertical()) {
            layout.setOffset(new Point2D.Float(
                     (float) offset.getX(), (float) offset.getY()+dy));
        } else {
            layout.setOffset(new Point2D.Float(
                    (float) offset.getX()+dx, (float) offset.getY()));
        }

        return layout;
    }
*/

    protected Mark cachedMark = null;
    protected AttributedCharacterIterator cachedACI = null;
    protected TextHit cachedHit = null;


    protected abstract org.apache.batik.gvt.text.Mark hitTest(
                         double x, double y, AttributedCharacterIterator aci,
                         TextNode node,
                         GraphicsNodeRenderContext context);// {
/*
        FontRenderContext frc = context.getFontRenderContext();

        TextSpanLayout layout =
            getOffsetAdjustedTextLayout(aci, node.getLocation(),  frc);

        TextHit textHit =
            layout.hitTestChar((float) x, (float) y);

        // Note that a texthit char index of -1 signals that the
        // hit, though within the text element bounds, did not
        // coincide with a glyph.
        if ((aci != cachedACI) ||
            (textHit == null) ||
            (cachedHit == null) ||
            ((textHit.getCharIndex() != -1) &&
            (textHit.getInsertionIndex() != cachedHit.getInsertionIndex()))) {
            cachedMark = new BasicTextPainter.Mark(x, y, layout, textHit);
            cachedACI = aci;
            cachedHit = textHit;
        } // else old mark is still valid, return it.

        return cachedMark;
    }

*/

    /**
     * This TextPainter's implementation of the Mark interface.
     */
    class Mark implements org.apache.batik.gvt.text.Mark {

        private TextHit hit;
        private TextSpanLayout layout;
        private double x;
        private double y;

        Mark(double x, double y, TextSpanLayout layout, TextHit hit) {
            this.x = x;
            this.y = y;
            this.layout = layout;
            this.hit = hit;
        }


        TextHit getHit() {
            return hit;
        }

        TextSpanLayout getLayout() {
            return layout;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

    }
}
