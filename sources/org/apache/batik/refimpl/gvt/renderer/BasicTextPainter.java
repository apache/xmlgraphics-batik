/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.renderer;

import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.TextPainter;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
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
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;

/**
 * Basic implementation of TextPainter which
 * renders the attributed character iterator of a <tt>TextNode</tt>.
 * Suitable for use with "standard" java.awt.font.TextAttributes only.
 * @see java.awt.font.TextAttribute
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @author <a href="vincent.hardy@sun.com>>Vincent Hardy</a>
 * @version $Id$
 */
public class BasicTextPainter implements TextPainter {

    /**
     * Paints the specified text node using the
     * specified Graphics2D and rendering context.
     * @see org.apache.batik.gvt.TextPainter
     * @param node the text node to paint
     * @param g2d the Graphics2D to use
     * @param context the rendering context.
     */
    public void paint(TextNode node,
                      Graphics2D g2d, GraphicsNodeRenderContext context) {

        FontRenderContext frc = context.getFontRenderContext();
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        Point2D location = node.getLocation();
        TextNode.Anchor anchor = node.getAnchor();

        /* XXX:  The code below only
         *     works for J2SE base implementation of AttributeCharacterIterator
         */
        TextLayout layout = new TextLayout(aci, frc);
        float advance = layout.getAdvance();
        float tx = 0f;

        switch(anchor.getType()){
        case TextNode.Anchor.ANCHOR_MIDDLE:
            tx = -advance/2;
            break;
        case TextNode.Anchor.ANCHOR_END:
            tx = -advance;
        }
        layout.draw(g2d, (float)(location.getX() + tx),
                         (float)location.getY());
    }

    /**
     * Given an X, y coordinate, text anchor type,
     * AttributedCharacterIterator, and GraphicsNodeRenderContext,
     * return a Mark which encapsulates a "selection start" action.
     * The standard order of method calls for selection is:
     * selectAt(); [selectTo(),...], selectTo(); getSelection().
     */
    public org.apache.batik.gvt.text.Mark selectAt(double x, double y,
                         AttributedCharacterIterator aci,
                         TextNode.Anchor anchor,
                         GraphicsNodeRenderContext context) {
        return hitTest(x, y, aci, anchor, context);
    }

    /**
     * Given an X, y coordinate, text anchor type, starting Mark,
     * AttributedCharacterIterator, and GraphicsNodeRenderContext,
     * return a Mark which encapsulates a "selection continued" action.
     * The standard order of method calls for selection is:
     * selectAt(); [selectTo(),...], selectTo(); getSelection().
     */
    public org.apache.batik.gvt.text.Mark selectTo(double x, double y,
                            org.apache.batik.gvt.text.Mark beginMark,
                            AttributedCharacterIterator aci,
                            TextNode.Anchor anchor,
                            GraphicsNodeRenderContext context) {
        org.apache.batik.gvt.text.Mark newMark =
             hitTest(x, y, aci, anchor, context);

        BasicTextPainter.Mark begin;
        BasicTextPainter.Mark end;
        try {
            begin = (BasicTextPainter.Mark) beginMark;
            end = (BasicTextPainter.Mark) newMark;
        } catch (ClassCastException cce) {
            throw new
            Error("This Mark was not instantiated by this TextPainter class!");
        }
        return newMark;
    }

    /**
     * Select the entire contents of an
     * AttributedCharacterIterator, and
     * return a Mark which encapsulates that selection action.
     */
    public org.apache.batik.gvt.text.Mark selectAll(double x, double y,
                            AttributedCharacterIterator aci,
                            TextNode.Anchor anchor,
                            GraphicsNodeRenderContext context) {
        org.apache.batik.gvt.text.Mark newMark =
                              hitTest(x, y, aci, anchor, context);
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
        TextLayout layout = null;
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
    public Shape getHighlightShape(org.apache.batik.gvt.text.Mark beginMark,
                                   org.apache.batik.gvt.text.Mark endMark,
                                   Point2D location, TextNode.Anchor anchor) {

        // TODO: later we can return more complex things like
        // noncontiguous selections

        BasicTextPainter.Mark begin;
        BasicTextPainter.Mark end;
        try {
            begin = (BasicTextPainter.Mark) beginMark;
            end = (BasicTextPainter.Mark) endMark;
        } catch (ClassCastException cce) {
            throw new
            Error("This Mark was not instantiated by this TextPainter class!");
        }

        Shape highlightShape = null;
        TextLayout layout = null;
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
                                    lasthit,
                                    layout.getBounds());

            double tx = location.getX();
            double ty = location.getY();
            if (anchor == TextNode.Anchor.MIDDLE) {
                tx -= layout.getAdvance()/2;
            } else if (anchor == TextNode.Anchor.END) {
                tx -= layout.getAdvance();
            }
            AffineTransform t =
                    AffineTransform.getTranslateInstance(tx, ty);
            highlightShape = t.createTransformedShape(highlightShape);
        }
        return highlightShape;
    }


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
         return getBounds(node, frc, true, true);
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
     protected Rectangle2D getBounds(TextNode node,
               FontRenderContext context,
               boolean includeDecoration,
               boolean includeStrokeWidth) {

         AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
         java.awt.font.TextLayout layout
                    = new java.awt.font.TextLayout(aci,
                      new java.awt.font.FontRenderContext(
                                            new AffineTransform(),
                                                true, true));

         Point2D location = node.getLocation();
         float tx = (float) location.getX();
         float ty = (float) location.getY();

         TextNode.Anchor anchor = node.getAnchor();
         if (anchor == TextNode.Anchor.MIDDLE) {
                tx -= layout.getAdvance()/2;
         } else if (anchor == TextNode.Anchor.END) {
                tx -= layout.getAdvance();
         }

         Rectangle2D layoutBounds = layout.getBounds();
         Rectangle2D bounds = new Rectangle2D.Float(
                              (float) (tx+layoutBounds.getX()),
                              (float) (ty+layoutBounds.getY()),
                              (float) layout.getAdvance(),
                              (float) layoutBounds.getHeight());

         if (includeDecoration) {

             double decorationThickness = getDecorationThickness(aci, layout);

             if (aci.getAttribute(GVTAttributedCharacterIterator.
                                        TextAttribute.UNDERLINE) != null) {
                 double y =
                    layout.getDescent()/2 + decorationThickness/2f;
                 bounds.setRect(bounds.getX(), bounds.getY(),
                              bounds.getWidth(), bounds.getHeight()+y);
             }

             if (aci.getAttribute(GVTAttributedCharacterIterator.
                                     TextAttribute.OVERLINE) != null) {

                 double dy =
                      layout.getAscent()*0.1 + decorationThickness/2f;
                 bounds.setRect(bounds.getX(), bounds.getY(),
                       bounds.getWidth(), bounds.getHeight()+dy);
             }
         }

         if (includeStrokeWidth) {
             BasicStroke stroke = (BasicStroke) aci.getAttribute(
                        GVTAttributedCharacterIterator.TextAttribute.STROKE);
             if (stroke != null) {
                  float strokeHalfThickness = stroke.getLineWidth();
                  // XXX: sometimes half width is not enough to add (ex: miters!)
                  bounds.setRect(
                      bounds.getX()-strokeHalfThickness,
                      bounds.getY()-strokeHalfThickness,
                      bounds.getWidth()+strokeHalfThickness,
                      bounds.getHeight()+strokeHalfThickness);
             }
         }

         return bounds;

     }

   private Shape shape = null;

   private Shape decoratedShape = null;

   /*
    * Get a Shape in userspace coords which defines the textnode glyph outlines.
    * @param node the TextNode to measure
    * @param frc the font rendering context.
    * @param includeDecoration whether to include text decoration
    *            outlines.
    */
    public Shape getOutline(TextNode node, FontRenderContext frc,
                                    boolean includeDecoration) {
        Shape outline;
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();

        java.awt.font.TextLayout layout
                = new java.awt.font.TextLayout(aci,
                      new java.awt.font.FontRenderContext(
                                            new AffineTransform(),
                                                          true,
                                                          true));
        Point2D location = node.getLocation();
        double tx = location.getX();
        double ty = location.getY();
        TextNode.Anchor anchor = node.getAnchor();
        if (anchor ==TextNode.Anchor.MIDDLE) {
            tx -= layout.getAdvance()/2;
        } else if (anchor == TextNode.Anchor.END) {
            tx -= layout.getAdvance();
        }
        AffineTransform t = AffineTransform.getTranslateInstance(tx, ty);
        outline = layout.getOutline(t);

        /*        if (includeDecoration) {
            if (!(outline instanceof GeneralPath)) {
                outline = new GeneralPath(outline);
            }

            if (aci.getAttribute(GVTAttributedCharacterIterator.
                                         TextAttribute.UNDERLINE) != null) {
                ((GeneralPath) outline).append(getUnderlineShape(aci, layout, location), false);
            }

            if (aci.getAttribute(GVTAttributedCharacterIterator.
                                     TextAttribute.OVERLINE) != null) {
                ((GeneralPath) outline).append(getOverlineShape(aci, layout, location), false);
            }
            } */
        System.out.println("Getting outline: "+outline.getBounds());
        return outline;
    }

   /*
    * Get a Shape in userspace coords which defines the textnode glyph outlines.
    * @param node the TextNode to measure
    * @param frc the font rendering context.
    */
    public Shape getShape(TextNode node, FontRenderContext frc) {
        if (shape == null) {
            shape = getOutline(node, frc, false);
        }
        return shape;
    }

   /*
    * Get a Shape in userspace coords which defines the
    * decorated textnode glyph outlines.
    * @param node the TextNode to measure
    * @param frc the font rendering context.
    */
    public Shape getDecoratedShape(TextNode node, FontRenderContext frc) {
        if (decoratedShape == null) {
            decoratedShape = getOutline(node, frc, true);
        }
        return decoratedShape;
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
        Shape outline;
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();

        BasicStroke stroke = (BasicStroke) aci.getAttribute(
                        GVTAttributedCharacterIterator.TextAttribute.STROKE);

        if (stroke != null) {

            java.awt.font.TextLayout layout
                = new java.awt.font.TextLayout(aci,
                      new java.awt.font.FontRenderContext(
                                            new AffineTransform(),
                                                          true,
                                                          true));
            Point2D location = node.getLocation();
            double tx = location.getX();
            double ty = location.getY();
            TextNode.Anchor anchor = node.getAnchor();
            if (anchor == TextNode.Anchor.MIDDLE) {
                tx -= layout.getAdvance()/2;
            } else if (anchor == TextNode.Anchor.END) {
                tx -= layout.getAdvance();
            }
            AffineTransform t = AffineTransform.getTranslateInstance(tx, ty);
            outline = layout.getOutline(t);

            if (includeDecoration) {
                if (!(outline instanceof GeneralPath)) {
                    outline = new GeneralPath(outline);
                }

                if (aci.getAttribute(GVTAttributedCharacterIterator.
                                         TextAttribute.UNDERLINE) != null) {
                    ((GeneralPath) outline).append(getUnderlineShape(aci, layout, location), false);
                }

                if (aci.getAttribute(GVTAttributedCharacterIterator.
                                     TextAttribute.OVERLINE) != null) {
                    ((GeneralPath) outline).append(getOverlineShape(aci, layout, location), false);
                }
            }

            outline = stroke.createStrokedShape(outline);

        } else {
            outline = null;
        }

        return outline;
    }

    /**
     * Returns a shape describing the overline decoration for a given ACI.
     * Does not rely on TextLayout's
     * internal strikethrough but computes it manually.
     */
    protected Shape getOverlineShape(AttributedCharacterIterator runaci,
                                          TextLayout layout,
                                          Point2D location) {
        double y = location.getY() +
            layout.getBaselineOffsets()[java.awt.Font.ROMAN_BASELINE] -
                layout.getAscent();
        Stroke overlineStroke =
            new BasicStroke(getDecorationThickness(runaci, layout));
        return overlineStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           location.getX(), y,
                           location.getX()+layout.getAdvance(), y));
    }

    /**
     * Returns a shape describing the strikethrough line for a given ACI.
     * Does not rely on TextLayout's
     * internal strikethrough but computes it manually.
     */
    protected Shape getUnderlineShape(AttributedCharacterIterator runaci,
                                          TextLayout layout,
                                          Point2D location) {
        double y = location.getY() +
                   (layout.getBaselineOffsets()[java.awt.Font.ROMAN_BASELINE]
                        + layout.getDescent())/2;
        Stroke underlineStroke =
            new BasicStroke(getDecorationThickness(runaci, layout));

        return underlineStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           location.getX(), y,
                           location.getX()+layout.getAdvance(), y));
    }

    /**
     * Returns a shape describing the strikethrough line for a given ACI.
     * Does not rely on TextLayout's
     * internal strikethrough but computes it manually.
     */
    protected Shape getStrikethroughShape(AttributedCharacterIterator runaci,
                                          TextLayout layout,
                                          Point2D location) {
        double y = location.getY()+
            (layout.getBaselineOffsets()[java.awt.Font.ROMAN_BASELINE]
             - layout.getAscent())/3;
                     // XXX: 3 is a hack for cosmetic reasons
        // TODO: the strikethrough offset should be calculated
        // from the font instead!
        Stroke strikethroughStroke =
            new BasicStroke(getDecorationThickness(runaci, layout));

        return strikethroughStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           location.getX(), y,
                           location.getX()+layout.getAdvance(), y));
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

    private Mark cachedMark = null;
    private AttributedCharacterIterator cachedACI = null;
    private TextHitInfo cachedHit = null;

    private org.apache.batik.gvt.text.Mark hitTest(
                         double x, double y, AttributedCharacterIterator aci,
                         TextNode.Anchor anchor,
                         GraphicsNodeRenderContext context) {

        FontRenderContext frc = context.getFontRenderContext();
        TextLayout layout = new TextLayout(aci, frc);
        float advance = layout.getAdvance();
        float tx = 0f;

        switch(anchor.getType()){
        case TextNode.Anchor.ANCHOR_MIDDLE:
            tx = advance/2;
            break;
        case TextNode.Anchor.ANCHOR_END:
            tx = advance;
        }

        TextHitInfo textHit =
            layout.hitTestChar((float) (x+tx), (float) y, layout.getBounds());

        //System.out.println("Testing point: "+(x+tx)+",; "+y);
        //System.out.println("    in layout "+layout.getBounds());
        //System.out.println("    with advance: "+layout.getAdvance());
        //if (textHit != null) System.out.println("HIT : "+textHit);

        if ((aci != cachedACI) ||
            (textHit == null) ||
            (textHit.getInsertionIndex() != cachedHit.getInsertionIndex())) {
            cachedMark = new BasicTextPainter.Mark(x, y, layout, textHit);
            cachedACI = aci;
            cachedHit = textHit;
        } // else old mark is still valid, return it.

        return cachedMark;
    }



    /**
     * This TextPainter's implementation of the Mark interface.
     */
    class Mark implements org.apache.batik.gvt.text.Mark {

        private TextHitInfo hit;
        private TextLayout layout;
        private double x;
        private double y;

        Mark(double x, double y, TextLayout layout, TextHitInfo hit) {
            this.x = x;
            this.y = y;
            this.layout = layout;
            this.hit = hit;
        }


        TextHitInfo getHit() {
            return hit;
        }

        TextLayout getLayout() {
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
