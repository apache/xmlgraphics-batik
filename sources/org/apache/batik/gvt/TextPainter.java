/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.text.AttributedCharacterIterator;
import java.awt.image.renderable.RenderContext;
import java.awt.font.FontRenderContext;

import org.apache.batik.gvt.text.Mark;

/**
 * Renders the attributed character iterator of a <tt>TextNode</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface TextPainter {

    /**
     * Paints the specified attributed character iterator using the
     * specified Graphics2D and context and font context.
     * @param shape the shape to paint
     * @param g2d the Graphics2D to use
     * @param context rendering context.
     */
    void paint(AttributedCharacterIterator aci, TextNode.Anchor anchor,
               Graphics2D g2d, GraphicsNodeRenderContext context);

    public Mark selectAt(double x, double y, AttributedCharacterIterator aci, 
                         TextNode.Anchor anchor, 
                         GraphicsNodeRenderContext context);

    public Mark selectTo(double x, double y, Mark beginMark, 
                            AttributedCharacterIterator aci, 
                            TextNode.Anchor anchor, 
                            GraphicsNodeRenderContext context);

    public Mark selectAll(double x, double y,  
                            AttributedCharacterIterator aci, 
                            TextNode.Anchor anchor, 
                            GraphicsNodeRenderContext context);

    /*
     * Get an array of index pairs corresponding to the indices within an
     * AttributedCharacterIterator regions bounded by two Marks.
     * Note that the instances of Mark passed to this function
     * <em>must come</em>
     * from the same TextPainter that generated them via selectAt() and 
     * selectTo(), since the TextPainter implementation may rely on hidden
     * implementation details of its own Mark implementation.
     */
    public int[] getSelected(AttributedCharacterIterator aci,
                             Mark start, Mark finish);
}
