/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.Paint;

import org.apache.batik.gvt.StrokeShapePainter;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

/**
 * The default implementation of the <tt>StrokeShapePainter</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ConcreteStrokeShapePainter implements StrokeShapePainter {

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
    public ConcreteStrokeShapePainter() {}

    public void setStroke(Stroke newStroke) {
        this.stroke = newStroke;
    }

    public void setPaint(Paint newPaint) {
        this.paint = newPaint;
    }

    public void paint(Shape shape,
                      Graphics2D g2d,
                      GraphicsNodeRenderContext ctx) {
        if (stroke != null && paint != null) {
            g2d.setPaint(paint);
            g2d.setStroke(stroke);

            //            Shape sShape = stroke.createStrokedShape(shape);
            //            g2d.fill(sShape);
            g2d.draw(shape);
        }
    }

    public Shape getPaintedArea(Shape shape){
        if(paint != null){
            return stroke.createStrokedShape(shape);
        }
        else{
            return shape;
        }
    }

    public Shape getPaintedOutline(Shape shape){
        return stroke.createStrokedShape(shape);
    }

}
