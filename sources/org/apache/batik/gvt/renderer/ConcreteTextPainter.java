/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import java.awt.Graphics2D;
import java.text.AttributedCharacterIterator;
import java.awt.image.renderable.RenderContext;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;

import org.apache.batik.gvt.TextPainter;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

/**
 * Renders the attributed character iterator of a <tt>TextNode</tt>.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ConcreteTextPainter extends BasicTextPainter {
    /**
     * Paints the specified attributed character iterator using the
     * specified Graphics2D and context and font context.
     * @param shape the shape to paint
     * @param g2d the Graphics2D to use
     * @param context rendering context.
     */
    public void paint(AttributedCharacterIterator aci, Point2D location, TextNode.Anchor anchor, 
                      Graphics2D g2d, GraphicsNodeRenderContext context){
        // Compute aci size to be able to draw it
        TextLayout layout = new TextLayout(aci, context.getFontRenderContext());
        float advance = layout.getAdvance();
        float tx = 0;

        switch(anchor.getType()){
        case TextNode.Anchor.ANCHOR_MIDDLE:
            tx = -advance/2;
            break;
        case TextNode.Anchor.ANCHOR_END:
            tx = -advance;
        }
        layout.draw(g2d, (float)(location.getX() + tx), (float)(location.getY()));
    }

}
