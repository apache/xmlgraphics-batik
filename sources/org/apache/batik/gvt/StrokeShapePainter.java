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
import java.awt.Stroke;
import java.awt.Paint;

/**
 * A shape painter that can be used to draw the outline of a shape.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class StrokeShapePainter implements ShapePainter {

    /**
     * The stroke attribute used to draw the outline of the shape.
     */
    protected Stroke stroke;

    /**
     * The paint attribute used to draw the outline of the shape.
     */
    protected Paint paint;

    /**
     * Constructs a new <tt>ShapePainter</tt> that can be used to draw
     * the outline of a <tt>Shape</tt>.
     */
    public StrokeShapePainter() {}

    /**
     * Sets the stroke used to draw the outline of a shape.
     *
     * @param newStroke the stroke object used to draw the outline of the shape
     */
    public void setStroke(Stroke newStroke) {
        this.stroke = newStroke;
    }

    /**
     * Sets the paint used to fill a shape.
     *
     * @param newPaint the paint object used to draw the shape
     */
    public void setPaint(Paint newPaint) {
        this.paint = newPaint;
    }

    /**
     * Paints the outline of the specified shape using the specified
     * Graphics2D and context.
     *
     * @param shape the shape to paint
     * @param g2d the Graphics2D to use
     * @param ctx the render context to use
     */
    public void paint(Shape shape,
                      Graphics2D g2d,
                      GraphicsNodeRenderContext ctx) {
        if (stroke != null && paint != null) {
            g2d.setPaint(paint);
            g2d.setStroke(stroke);
            g2d.draw(shape);
        }
    }

    /**
     * Returns the area painted by this painter for a given input shape
     *
     * @param shape the shape to paint
     */
    public Shape getPaintedArea(Shape shape){
        if(paint != null && stroke != null){
            return stroke.createStrokedShape(shape);
        } else {
            return shape;
        }
    }
}
