/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Shape;
import java.awt.Graphics2D;

/**
 * Renders the shape of a <tt>ShapeNode</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface ShapePainter {

    /**
     * Paints the specified shape using the specified Graphics2D and context.
     * @param shape the shape to paint
     * @param g2d the Graphics2D to use
     * @param ctx the render context to use
     */
    void paint(Shape shape, Graphics2D g2d, GraphicsNodeRenderContext ctx);

    /**
     * Returns the area painted by this painter for a given input shape
     *
     * @param shape the shape to paint
     */
    Shape getPaintedArea(Shape shape);

    /**
     * Returns the painted outline by this painter for a given input shape
     *
     * @param shape the shape to paint
     */
    Shape getPaintedOutline(Shape shape);
}
