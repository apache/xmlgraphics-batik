/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The default implementation of the <tt>CompositeShapePainter</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class CompositeShapePainter implements ShapePainter {

    /** The enclosed <tt>ShapePainter</tt>s of this composite shape painter. */
    protected ShapePainter [] painters;
    /** The number of shape painter. */
    protected int count;

    /**
     * Constructs a new empty <tt>CompositeShapePainter</tt>.
     */
    public CompositeShapePainter() {}

    /**
     * Adds the specified shape painter.
     * @param shapePainter the shape painter to add
     */
    public void addShapePainter(ShapePainter shapePainter) {
        if (shapePainter == null) {
            throw new IllegalArgumentException("ShapePainter can't be null");
        }
        if (painters == null) {
            painters = new ShapePainter[2];
        }
        if (count == painters.length) {
            ShapePainter [] newPainters = new ShapePainter[(count*3)/2 + 1];
            System.arraycopy(painters, 0, newPainters, 0, count);
            painters = newPainters;
        }
        painters[count++] = shapePainter;
    }

    /**
     * Paints the specified shape using the specified Graphics2D and context.
     * @param shape the shape to paint
     * @param g2d the Graphics2D to use
     * @param ctx the render context to use
     */
    public void paint(Shape shape,
                      Graphics2D g2d,
                      GraphicsNodeRenderContext ctx) {
        if (painters != null) {
            for (int i=0; i < count; ++i) {
                painters[i].paint(shape, g2d, ctx);
            }
        }
    }

    /**
     * Returns the area painted by this painter for a given input shape
     * @param shape the shape to paint
     */
    public Shape getPaintedArea(Shape shape){
        // <!> FIX ME: Use of GeneralPath is a work around Area problems.
        GeneralPath paintedArea = new GeneralPath();
        if (painters != null) {
            for (int i=0; i < count; ++i) {
                paintedArea.append(painters[i].getPaintedArea(shape), false);
            }
        }
        return paintedArea;
    }
}
