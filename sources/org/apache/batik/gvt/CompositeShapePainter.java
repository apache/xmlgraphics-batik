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
 * A shape painter which consists of multiple shape painters.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class CompositeShapePainter implements ShapePainter {
    /**
     * The shape associated with this painter
     */
    protected Shape shape;

    /** The enclosed <tt>ShapePainter</tt>s of this composite shape painter. */
    protected ShapePainter [] painters;
    /** The number of shape painter. */
    protected int count;

    /**
     * Constructs a new empty <tt>CompositeShapePainter</tt>.
     */
    public CompositeShapePainter(Shape shape) {
        if(shape == null){
            throw new IllegalArgumentException();
        }

        this.shape = shape;
    }

    /**
     * Adds the specified shape painter.
     * @param shapePainter the shape painter to add
     */
    public void addShapePainter(ShapePainter shapePainter) {
        if (shapePainter == null) {
            return;
        }
        if(this.shape != shapePainter.getShape()){
            shapePainter.setShape(shape);
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
     * @param g2d the Graphics2D to use
     * @param ctx the render context to use
     */
    public void paint(Graphics2D g2d,
                      GraphicsNodeRenderContext ctx) {
        if (painters != null) {
            for (int i=0; i < count; ++i) {
                painters[i].paint(g2d, ctx);
            }
        }
    }

    /**
     * Returns the area painted by this painter
     */
    public Shape getPaintedArea(GraphicsNodeRenderContext rc){
        // <!> FIX ME: Use of GeneralPath is a work around Area problems.
        if (painters != null) {
            GeneralPath paintedArea = new GeneralPath();
            for (int i=0; i < count; ++i) {
                Shape s = painters[i].getPaintedArea(rc);
                if (s != null) {
                    paintedArea.append(s, false);
                }
            }
            return paintedArea;
        } else {
            return null;
        }
    }

    /**
     * Sets the Shape this painter is associated with.
     * @param shape new shape this painter should be associated with.
     *        should not be null.
     */
    public void setShape(Shape shape){
        if (painters != null) {
            for (int i=0; i < count; ++i) {
                painters[i].setShape(shape);
            }
        }
        this.shape = shape;
    }

    /**
     * Gets the Shape this painter is associated with.
     *
     * @return shape associated with this Painter.
     */
    public Shape getShape(){
        return shape;
    }
}
