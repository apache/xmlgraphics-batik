/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.renderer;

import org.apache.batik.gvt.TextPainter;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.refimpl.AttributedCharacterSpanIterator;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.Stroke;
import java.text.CharacterIterator;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.util.Set;
import java.util.HashSet;

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
    public void paint(AttributedCharacterIterator aci, TextNode.Anchor anchor, 
               Graphics2D g2d, GraphicsNodeRenderContext context) {

        FontRenderContext frc = context.getFontRenderContext();
        Set extendedAtts = new HashSet();
        extendedAtts.add(
                GVTAttributedCharacterIterator.TextAttribute.STROKE);
        aci.first();
        while (aci.current() != CharacterIterator.DONE) {
            float x = 0f;
            float y = 0f;
            /* 
             * note that these can be superseded by X, Y attributes 
             * but this hasn't been implemented yet
             */
            int start = aci.getRunStart(extendedAtts);
            int end = aci.getRunLimit(extendedAtts);
            Stroke stroke = (Stroke) aci.getAttribute( 
                    GVTAttributedCharacterIterator.TextAttribute.STROKE);
            AttributedCharacterIterator runaci = 
                    new AttributedCharacterSpanIterator(aci, start, end); 
            TextLayout layout = new TextLayout(runaci, frc);
            g2d.setStroke(stroke);
            layout.draw(g2d, x, y);
            x += layout.getBounds().getWidth();
            // FIXME: not BIDI compliant yet!
            aci.setIndex(end);
        }
        // FIXME: Finish implementation! (Currently only understands STROKE)
    }

}
