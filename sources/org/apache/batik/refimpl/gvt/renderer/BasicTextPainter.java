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
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Composite;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;

/**
 * Renders the attributed character iterator of a <tt>TextNode</tt>.
 * Suitable for use with "standard" TextAttributes only.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @author <a href="vincent.hardy@sun.com>>Vincent Hardy</a>
 * @version $Id$
 */
public class BasicTextPainter implements TextPainter {

/*
 * Strongly considering changing this API to take a TextNode rather
 * than ACI, etc.
 */

    /**
     * Paints the specified attributed character iterator using the
     * specified Graphics2D and rendering context.
     * Note that the GraphicsNodeRenderContext contains a TextPainter
     * reference.
     * @see org.apache.batik.gvt.TextPainter
     * @see org.apache.batik.gvt.GraphicsNodeRenderContext
     * @param shape the shape to paint
     * @param anchor the TextNode.Anchor convention used
     * @param g2d the Graphics2D to use
     * @param context rendering context.
     */
    public void paint(AttributedCharacterIterator aci, Point2D location,
               TextNode.Anchor anchor,
                   Graphics2D g2d, GraphicsNodeRenderContext context) {

        FontRenderContext frc = context.getFontRenderContext();

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

    public org.apache.batik.gvt.text.Mark selectAt(double x, double y,
                         AttributedCharacterIterator aci,
                         TextNode.Anchor anchor,
                         GraphicsNodeRenderContext context) {
        return hitTest(x, y, aci, anchor, context);
    }

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
        TextLayout layout = null;
        if (begin != null) {
            layout = begin.getLayout();
        }
        if (layout != null) {
            Shape highlightShape;
            highlightShape = layout.getVisualHighlightShape(begin.getHit(),
                                                             end.getHit());
            /* TODO: set highlight in Mark, or in TextNode.
             * The latter will require a change to the TextPainter
             * interfaces to
             * take a TextNode as a param rather than an ACI.
             *
             * textnode.setHighlightShape(highlightShape);
             *
             * Issue: if TextNode keeps a cache of highlight shape,
             * then in the unlikely event that there are two renderers
             * concurrently
             * running, both of which do highlighting, weird stuff will happen.
             */
        }
        return newMark;
    }

    public org.apache.batik.gvt.text.Mark selectAll(double x, double y,
                            AttributedCharacterIterator aci,
                            TextNode.Anchor anchor,
                            GraphicsNodeRenderContext context) {
        org.apache.batik.gvt.text.Mark newMark =
                              hitTest(x, y, aci, anchor, context);

        /* TODO: set highlight in Mark, or in TextNode.
         * The latter will require a change to the TextPainter interfaces to
         * take a TextNode as a param rather than an ACI.
         *
         * Issue: if TextNode keeps a cache of highlight shape,
         * then in the unlikely event that there are two renderers concurrently
         * running, both of which do highlighting, weird stuff will happen.
         */

        // Shape highlightShape = textnode.getBounds();
        // textnode.setHighlightShape(highlightShape);

        return newMark;
    }

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
            return layout.getLogicalRangesForVisualSelection(begin.getHit(),
                                                             end.getHit());
        } else {
            return null;
        }
    }

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
        //      System.out.println("Testing point: "+(x+tx)+",; "+y);
        //      System.out.println("    in layout "+layout.getBounds());
        //      System.out.println("    with advance: "+layout.getAdvance());
        TextHitInfo textHit =
            layout.hitTestChar((float) (x+tx), (float) y, layout.getBounds());
        //      if (textHit != null) System.out.println("HIT : "+textHit);
        return new BasicTextPainter.Mark(x, y, layout, textHit);
    }

    /**
     * Draws the highlight for a selected region.
     */
    private void drawHighlight(Graphics2D g2d, Shape shape) {
        ; // not implemented
    }

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
