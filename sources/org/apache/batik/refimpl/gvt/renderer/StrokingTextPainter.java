/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.renderer;

import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.TextPainter;
import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextSpanLayout;

/**
 * More sophisticated implementation of TextPainter which
 * renders the attributed character iterator of a <tt>TextNode</tt>.
 * <em>StrokingTextPainter includes support for stroke, fill, opacity,
 * text-decoration, and other attributes, but does not handle
 * explicit per-glyph positioning.</em>
 * @see org.apache.batik.gvt.TextPainter
 * @see org.apache.batik.gvt.text.GVTAttributedCharacterIterator
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class StrokingTextPainter extends BasicTextPainter {

    static Set extendedAtts = new HashSet();

    static {
        extendedAtts.add(GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
    }

    /**
     * Paints the specified attributed character iterator using the
     * specified Graphics2D and rendering context.
     * Note that the GraphicsNodeRenderContext contains a TextPainter
     * reference.
     * @see org.apache.batik.gvt.TextPainter
     * @see org.apache.batik.gvt.GraphicsNodeRenderContext
     * @param shape the shape to paint
     * @param g2d the Graphics2D to use
     * @param context the rendering context.
     */
    public void paint(TextNode node, Graphics2D g2d,
                           GraphicsNodeRenderContext context) {

        FontRenderContext frc = context.getFontRenderContext();
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        Point2D location = node.getLocation();
        TextNode.Anchor anchor = node.getAnchor();
        List textRuns = new ArrayList();
        double xadvance = 0d;
        Point2D advance = new Point2D.Float(0f, 0f);

        aci.first();
        /*
         * We iterate through the spans over extended attributes,
         * instantiating TextLayout elements as we go, and
         * accumulate an overall advance for the text display.
         */
        while (aci.current() != CharacterIterator.DONE) {

            int start = aci.getRunStart(extendedAtts);
            int end = aci.getRunLimit(extendedAtts);

            AttributedCharacterIterator runaci =
                    new AttributedCharacterSpanIterator(aci, start, end);

            TextSpanLayout layout = getTextLayoutFactory().createTextLayout(runaci, advance, frc);

            if (layout.isVertical()) {
                AttributedString as = new AttributedString(runaci);
                if (runaci.getAttribute(GVTAttributedCharacterIterator.
                                        TextAttribute.UNDERLINE) != null) {
                    as.addAttribute(TextAttribute.UNDERLINE,
                                    TextAttribute.UNDERLINE_ON);
                }
                if (runaci.getAttribute(GVTAttributedCharacterIterator.
                                        TextAttribute.STRIKETHROUGH) != null) {
                    as.addAttribute(TextAttribute.STRIKETHROUGH,
                                    TextAttribute.STRIKETHROUGH_ON);
                }
                runaci = as.getIterator();
            }

            TextRun run = new TextRun(layout, runaci);

            textRuns.add(run);

            Point2D layoutAdvance = layout.getAdvance2D();
            advance = new Point2D.Float(
                       (float) (advance.getX()+layoutAdvance.getX()), 
                       (float) (advance.getY()+layoutAdvance.getY()));

            xadvance = advance.getX();

            // FIXME: not BIDI compliant yet!

            aci.setIndex(end);
        }

        double x = 0d;
        double y = 0d;

        // XXX: horizontal layouts only!

        switch(anchor.getType()){
        case TextNode.Anchor.ANCHOR_MIDDLE:
            x = -xadvance/2d;
            break;
        case TextNode.Anchor.ANCHOR_END:
            x = -xadvance;
        }

        /*
         * Adjust for Anchor (above), then
         * we render each of the TextLayout glyphsets
         * in turn.
         */
        for (int i=0; i<textRuns.size(); ++i) {
            TextRun textRun = (TextRun) textRuns.get(i);
            AttributedCharacterIterator runaci = textRun.getACI();
            TextSpanLayout layout = textRun.getLayout();
            runaci.first();
            //System.out.print("Painting text: ");
            //for (int j=runaci.getBeginIndex(); j<runaci.getEndIndex(); ++j) {
            //    System.out.print(runaci.setIndex(j));
            //}
            //System.out.println("");
            runaci.first();

            Composite opacity = (Composite)
                      runaci.getAttribute(GVTAttributedCharacterIterator.
                                                  TextAttribute.OPACITY);
            if (opacity != null) {
                g2d.setComposite(opacity);
            }

            boolean underline =
                (runaci.getAttribute(GVTAttributedCharacterIterator.
                                     TextAttribute.UNDERLINE) != null);

            // paint over-and-underlines first, then layer glyphs over them

            if (underline && !layout.isVertical()) {
                paintUnderline(textRun, location, x, g2d);
            }
            boolean overline =
                (runaci.getAttribute(GVTAttributedCharacterIterator.
                                     TextAttribute.OVERLINE) != null);

            if (overline && !layout.isVertical()) {
                paintOverline(textRun, location, x, g2d);
            }


            AffineTransform tx = AffineTransform.getTranslateInstance(
                                        location.getX() + x, 
                                        location.getY() + y);
            Shape outline = layout.getOutline(tx);

            // check if we need to fill this glyph
            Paint paint = (Paint)
                              runaci.getAttribute(TextAttribute.FOREGROUND);
            if (paint != null) {
                g2d.setPaint(paint);
                g2d.fill(outline);
            }

            // check if we need to draw the outline of this glyph
            Stroke stroke = (Stroke) runaci.getAttribute(
                    GVTAttributedCharacterIterator.TextAttribute.STROKE);
            paint = (Paint) runaci.getAttribute(
                    GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT);
            if (stroke != null && paint != null) {
                g2d.setStroke(stroke);
                g2d.setPaint(paint);
                g2d.draw(outline);
            }
            boolean strikethrough =
                (runaci.getAttribute(GVTAttributedCharacterIterator.
                    TextAttribute.STRIKETHROUGH) ==
                        GVTAttributedCharacterIterator.
                            TextAttribute.STRIKETHROUGH_ON);
            // paint strikethrough last
            if (strikethrough && !layout.isVertical()) {
                paintStrikethrough(textRun, location, x, g2d);
            }
        }

        // FIXME: Finish implementation!
        // (Currently only understands STROKE, STROKE_PAINT, UNDERLINE atts,
        // FOREGROUND, OVERLINE)
        // TODO: FONT_VARIANT, SUPERSCRIPT, SUBSCRIPT...
    }

    /**
     * Paints the overline for a given ACI.
     */
    private void paintOverline(TextRun textRun, Point2D location,
                     double xoffset, Graphics2D g2d) {
        AttributedCharacterIterator runaci = textRun.getACI();
        TextSpanLayout layout = textRun.getLayout();
        java.awt.Shape overlineShape =
                layout.getDecorationOutline(
                           TextSpanLayout.DECORATION_OVERLINE,
                              AffineTransform.getTranslateInstance(
                                  location.getX()+xoffset, location.getY()));

        Paint paint = (Paint) runaci.getAttribute(
            TextAttribute.FOREGROUND);
        if (paint != null) {
            g2d.setPaint(paint);
            g2d.fill(overlineShape);
        }
        Stroke stroke = (Stroke) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE);
        paint = (Paint) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT);
        if ((stroke != null) && (paint != null)) {
            g2d.setStroke(stroke);
            g2d.setPaint(paint);
            g2d.draw(overlineShape);
        }
        // TODO: performance and concision
    }

    /**
     * Paints the underline for a given ACI - does not rely on TextLayout's
     * internal underlining but computes the underline manually, allowing
     * the underline fill and stroke to differ from that of the text glyphs.
     */
    private void paintUnderline(TextRun textRun, Point2D location,
                    double xoffset, Graphics2D g2d) {

        AttributedCharacterIterator runaci = textRun.getACI();
        TextSpanLayout layout = textRun.getLayout();

        Shape underlineShape = layout.getDecorationOutline(
                           TextSpanLayout.DECORATION_UNDERLINE,
                              AffineTransform.getTranslateInstance(
                                  location.getX()+xoffset, location.getY()));

        // TODO: change getAdvance to getVisibleAdvance for
        // ACIs which do not inherit their underline attribute
        // (not sure how to implement this yet)

        Paint paint = (Paint) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_PAINT);
        if (paint != null) {
            g2d.setPaint(paint);
            g2d.fill(underlineShape);
            //    System.out.println("Filling "+underlineShape+" with paint "+paint);
        }
        Stroke stroke = (Stroke) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_STROKE);
        paint = (Paint) runaci.getAttribute(
            GVTAttributedCharacterIterator.
                TextAttribute.UNDERLINE_STROKE_PAINT);
        if ((stroke != null) && (paint != null)) {
            g2d.setStroke(stroke);
            g2d.setPaint(paint);
            g2d.draw(underlineShape);
        }

    }

    /**
     * Paints the strikethrough line for a given ACI - does not
     * rely on TextLayout's
     * internal strikethrough but computes it manually.
     */
    private void paintStrikethrough(TextRun textRun, Point2D location,
                     double xoffset, Graphics2D g2d) {

        AttributedCharacterIterator runaci = textRun.getACI();
        TextSpanLayout layout = textRun.getLayout();

        java.awt.Shape strikethroughShape =
                layout.getDecorationOutline(
                           TextSpanLayout.DECORATION_STRIKETHROUGH,
                              AffineTransform.getTranslateInstance(
                                  location.getX()+xoffset, location.getY()));

        Paint paint = (Paint) runaci.getAttribute(
            TextAttribute.FOREGROUND);
        if (paint != null) {
            g2d.setPaint(paint);
            g2d.fill(strikethroughShape);
        }
        Stroke stroke = (Stroke) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE);
        paint = (Paint) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT);
        if ((stroke != null) && (paint != null)) {
            g2d.setStroke(stroke);
            g2d.setPaint(paint);
            g2d.draw(strikethroughShape);
        }
    }

    /**
     * Inner convenience class for associating a TextLayout for
     * sub-spans, and the ACI which iterates over that subspan.
     */
    class TextRun {
        private AttributedCharacterIterator aci;
        private TextSpanLayout layout;

        public TextRun(TextSpanLayout layout, AttributedCharacterIterator aci) {
            this.layout = layout;
            this.aci = aci;
        }
        public AttributedCharacterIterator getACI() {
            return aci;
        }
        public TextSpanLayout getLayout() {
            return layout;
        }

    }
}




