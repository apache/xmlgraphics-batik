/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 * A shape painter that can be used to draw the outline of a shape.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class StrokeShapePainter implements ShapePainter {

    /** 
     * Shape painted by this painter.
     */
    protected Shape shape;

    /**
     * Stroked version of the shape.
     */
    protected Shape strokedShape;

    /**
     * The stroke attribute used to draw the outline of the shape.
     */
    protected Stroke stroke;

    /**
     * The paint attribute used to draw the outline of the shape.
     */
    protected Paint paint;

    /**
     * Constructs a new <tt>ShapePainter</tt> that can be used to draw the
     * outline of a <tt>Shape</tt>.
     *
     * @param shape shape to be painted by this painter.
     * Should not be null.
     */
    public StrokeShapePainter(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }

    /**
     * Sets the stroke used to draw the outline of a shape.
     *
     * @param newStroke the stroke object used to draw the outline of the shape
     */
    public void setStroke(Stroke newStroke) {
        this.stroke       = newStroke;
        this.strokedShape = null;
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
     * Graphics2D.
     *
     * @param g2d the Graphics2D to use 
     */
    public void paint(Graphics2D g2d) {
        if (stroke != null && paint != null) {
            g2d.setPaint(paint);
            g2d.setStroke(stroke);
            g2d.draw(shape);
        }
    }

    /**
     * Returns the area painted by this shape painter.
     */
    public Shape getPaintedArea(){
        if ((paint == null) || (stroke == null))
            return null;

        if (strokedShape == null)
            strokedShape = stroke.createStrokedShape(shape);

        return strokedShape;
    }

    /**
     * Returns the bounds of the area painted by this shape painter
     */
    public Rectangle2D getPaintedBounds2D() {
        Shape painted = getPaintedArea();
        if (painted == null)
            return null;

        return painted.getBounds2D();
    }

    /**
     * Returns the area covered by this shape painter (even if not painted).
     */
    public Shape getSensitiveArea(){
        if (stroke == null)
            return null;

        if (strokedShape == null)
            strokedShape = stroke.createStrokedShape(shape);

        return strokedShape;
    }

    /**
     * Returns the bounds of the area covered by this shape painte
     * (even if not painted).
     */
    public Rectangle2D getSensitiveBounds2D() {
        Shape sensitive = getSensitiveArea();
        if (sensitive == null)
            return null;

        return sensitive.getBounds2D();
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
        this.strokedShape = null;
    }

    /**
     * Gets the Shape this shape painter is associated with.
     *
     * @return shape associated with this painter.
     */
    public Shape getShape(){
        return shape;
    }
}
