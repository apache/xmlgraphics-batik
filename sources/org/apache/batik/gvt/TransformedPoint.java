/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Point2D;

/**
 * A <tt>TransformedPoint</tt> is initialized with an x and a y
 * value and a <tt>PointTransformer</tt> the initial x and y
 * values are transformed the first time the x or y value
 * is requested.
 */
public class TransformedPoint extends Point2D{
    /**
     * The X coordinate of this <code>Point2D</code>.
     */
    private float x;

    /**
     * The Y coordinate of this <code>Point2D</code>.
     */
    private float y;

    /**
     * PointTransformer used to transform coordinates
     */
    public PointTransformer transformer;

    /**
     * Transformed value for x
     */
    private float x2;

    /**
     * Transformed value for y
     */
    private float y2;

    /**
     * Constructs and initializes a <code>Point2D</code> with the
     * specified coordinates.
     * @param x,&nbsp;y the coordinates to which to set the newly
     * constructed <code>Point2D</code>
     * @param transformer transform applied to the input coordinate
     *        when the x or y values are requested.
     * @since 1.2
     */
    public TransformedPoint(float x, float y, PointTransformer transformer) {
        this.x = x;
        this.y = y;

        if(transformer == null){
            throw new IllegalArgumentException();
        }

        this.transformer = transformer;
    }

    /**
     * Returns the X coordinate of this <code>Point2D</code> in
     * <code>double</code> precision.
     * @return the X coordinate of this <code>Point2D</code>.
     * @since 1.2
     */
    public double getX() {
        transformPoint();
        return (double) x2;
    }

    /**
     * Returns the Y coordinate of this <code>Point2D</code> in
     * <code>double</code> precision.
     * @return the Y coordinate of this <code>Point2D</code>.
     * @since 1.2
     */
    public double getY() {
        transformPoint();
        return (double) y2;
    }

    /**
     * Sets the location of this <code>Point2D</code> to the
     * specified <code>double</code> coordinates.
     * @param x,&nbsp;y the coordinates to which to set this
     * <code>Point2D</code>
     * @since 1.2
     */
    public void setLocation(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    /**
     * Sets the location of this <code>Point2D</code> to the
     * specified <code>float</code> coordinates.
     * @param x,&nbsp;y the coordinates to which to set this
     * <code>Point2D</code>
     * @since 1.2
     */
    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a <code>String</code> that represents the value
     * of this <code>Point2D</code>.
     * @return a string representation of this <code>Point2D</code>.
     * @since 1.2
     */
    public String toString() {
        return "TransformedPoint["+ getX()+", "+ getY()+"]";
    }

    /**
     * Computes the x2 and y2
     */
    private void transformPoint(){
        Point2D.Float p = new Point2D.Float(x, y);
        transformer.toUserSpace(p);
        x2 = p.x;
        y2 = p.y;
    }

    public Object clone(){
        return new TransformedPoint(x, y, transformer);
    }

    public boolean equals(Object obj){
        boolean isEqual = false;
        if(obj != null && obj instanceof TransformedPoint){
            TransformedPoint o = (TransformedPoint)obj;
            isEqual = (o.x == x
                       &&
                       o.y == y
                       &&
                       o.transformer.equals(transformer));
        }
        return isEqual;
    }
}
