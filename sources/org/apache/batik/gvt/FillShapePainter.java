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
import java.awt.Paint;

/**
 * A shape painter that can be used to fill a shape.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class FillShapePainter implements ShapePainter {

    /** 
     * The Shape to be painted.
     */
    protected Shape shape;

    /** 
     * The paint attribute used to fill the shape.
     */
    protected Paint paint;

    /**
     * Constructs a new <tt>FillShapePainter</tt> that can be used to fill
     * a <tt>Shape</tt>.
     *
     * @param shape Shape to be painted by this painter
     * Should not be null.  
     */
    public FillShapePainter(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
	this.shape = shape;
    }

    /**
     * Sets the paint used to fill a shape.
     *
     * @param newPaint the paint object used to fill the shape
     */
    public void setPaint(Paint newPaint) {
        this.paint = newPaint;
    }

    /**
     * Paints the specified shape using the specified Graphics2D.
     *
     * @param g2d the Graphics2D to use
     */
     public void paint(Graphics2D g2d, GraphicsNodeRenderContext ctx) {
        if (paint != null) {
            g2d.setPaint(paint);
            g2d.fill(shape);
        }
    }

    /**
     * Returns the area painted by this shape painter.
     */
    public Shape getPaintedArea(){
        return shape;
    }

    /**
     * Sets the Shape this shape painter is associated with.
     *
     * @param shape new shape this painter should be associated with.
     * Should not be null.  
     */
    public void setShape(Shape shape){
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }

    /**
     * Gets the Shape this shape painter is associated with.
     *
     * @return shape associated with this Painter.
     */
    public Shape getShape(){
        return shape;
    }
}
