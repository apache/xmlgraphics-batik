/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
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

/**
 * Renders the attributed character iterator of a <tt>TextNode</tt>.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class StrokingTextPainter extends BasicTextPainter {

    /**
     * Paints the specified attributed character iterator using the
     * specified Graphics2D and rendering context.
     * Note that the GraphicsNodeRenderContext contains a TextPainter reference.
     * @see org.apache.batik.gvt.TextPainter
     * @see org.apache.batik.gvt.GraphicsNodeRenderContext
     * @param shape the shape to paint
     * @param g2d the Graphics2D to use
     * @param context rendering context.
     */
    public void paint(AttributedCharacterIterator aci, Point2D location, TextNode.Anchor anchor,
               Graphics2D g2d, GraphicsNodeRenderContext context) {

        FontRenderContext frc = context.getFontRenderContext();

        Set extendedAtts = new HashSet();
        List textRuns = new ArrayList();
        float advance = 0f;
        extendedAtts.add(
                GVTAttributedCharacterIterator.TextAttribute.STROKE);
	extendedAtts.add(
		GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT);
        aci.first();
        /*
         * We iterate through the spans over extended attributes,
         * instantiating TextLayout elements as we go, and
         * accumulate an overall advance for the text display.
         */
        while (aci.current() != CharacterIterator.DONE) {
            float x = 0f;
            float y = 0f;
            /*
             * note that these can be superseded by X, Y attributes
             * but this hasn't been implemented yet
             */
            int start = aci.getRunStart(extendedAtts);
            int end = aci.getRunLimit(extendedAtts);

            AttributedCharacterIterator runaci =
                    new AttributedCharacterSpanIterator(aci, start, end);
	    
            TextLayout layout = new TextLayout(runaci, frc);
            TextRun run = new TextRun(layout, runaci);
            textRuns.add(run);

            advance += layout.getAdvance();

            // FIXME: not BIDI compliant yet!

            aci.setIndex(end);
        }

        float x = 0f;

        switch(anchor.getType()){
        case TextNode.Anchor.ANCHOR_MIDDLE:
            x = -advance/2;
            break;
        case TextNode.Anchor.ANCHOR_END:
            x = -advance;
        }

        /*
         * Adjust for Anchor (above), then
         * we render each of the TextLayout glyphsets
         * in turn.
         */
        for (int i=0; i<textRuns.size(); ++i) {
            TextRun textRun = (TextRun) textRuns.get(i);
            AttributedCharacterIterator runaci = textRun.getACI();
            runaci.first();

            // check if we need to fill this glyph
            Paint paint = (Paint) runaci.getAttribute(TextAttribute.FOREGROUND);
            if (paint != null) {
                textRun.getLayout().draw(g2d, 
		    (float)(location.getX() + x), (float)(location.getY()));
            }
            // check if we need to draw the outline of this glyph
            Stroke stroke = (Stroke) runaci.getAttribute(
                    GVTAttributedCharacterIterator.TextAttribute.STROKE);
            paint = (Paint) runaci.getAttribute(
                    GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT);
            if (stroke != null && paint != null) {
                AffineTransform t = AffineTransform.getTranslateInstance(
                                        location.getX() + x, location.getY());
                g2d.setStroke(stroke);
                g2d.setPaint(paint);
                g2d.draw(textRun.getLayout().getOutline(t));
            }
            x += textRun.getLayout().getAdvance();
        }

        // FIXME: Finish implementation! 
	// (Currently only understands STROKE, STROKE_PAINT)
    }

    /**
     * Inner convenience class for associating a TextLayout for
     * sub-spans, and the ACI which iterates over that subspan.
     */
    class TextRun {
        private AttributedCharacterIterator aci;
        private TextLayout layout;

        public TextRun(TextLayout layout, AttributedCharacterIterator aci) {
            this.layout = layout;
            this.aci = aci;
        }
        public AttributedCharacterIterator getACI() {
            return aci;
        }
        public TextLayout getLayout() {
            return layout;
        }

    }
}
