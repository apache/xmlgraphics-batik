/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Dimension2D;

/**
 * A <tt>TransformedDimension</tt> is initialized with an width and a height
 * value and a <tt>DimensionTransformer</tt> the initial width and height
 * values are transformed the first time the x or y value
 * is requested.
 */
public class TransformedDimension extends Dimension2D{

    /**
     * The WIDTH of this <code>Dimension2D</code>.
     */
    private float width;

    /**
     * The HEIGHT of this <code>Dimension2D</code>.
     */
    private float height;

    /**
     * DimensionTransformer used to transform the dimension
     */
    public DimensionTransformer transformer;

    /**
     * Transformed value for width
     */
    private float width2;

    /**
     * Transformed value for height
     */
    private float height2;

    /**
     * Constructs and initializes a <code>Dimension2D</code> with
     * the specified width and height.
     * @param transformer transform applied to the input value
     *        when the width or height values are requested.
     */
    public TransformedDimension(float width, float height,
                                DimensionTransformer transformer) {
        this.width = width;
        this.height = height;

        if(transformer == null){
            throw new IllegalArgumentException();
        }

        this.transformer = transformer;
    }

    /**
     * Returns the WIDTH of this <code>Dimension2D</code> in
     * <code>double</code> precision.
     */
    public double getWidth() {
        transform();
        return (double) width2;
    }

    /**
     * Returns the HEIGHT of this <code>Dimension2D</code> in
     * <code>double</code> precision.
     * @return the HEIGHT of this <code>Dimension2D</code>.
     * @since 1.2
     */
    public double getHeight() {
        transform();
        return (double) height2;
    }

    /**
     * Sets the size of this <code>Dimension2D</code> instance
     */
    public void setSize(double width, double height) {
        this.width = (float)width;
        this.height = (float)height;
    }

    /**
     * Returns a <code>String</code> that represents the value
     * of this <code>Dimension2D</code>.
     * @return a string representation of this <code>Dimension2D</code>.
     */
    public String toString() {
        return "TransformedDimention["+ getWidth()+", "+ getHeight()+"]";
    }

    /**
     * Computes the width2 and height2
     */
    private void transform(){
        width2 = transformer.widthToUserSpace(width);
        height2 = transformer.heightToUserSpace(height);
    }

    public Object clone(){
        return new TransformedDimension(width, height, transformer);
    }

    public boolean equals(Object obj){
        boolean isEqual = false;
        if(obj != null && obj instanceof TransformedDimension){
            TransformedDimension o = (TransformedDimension)obj;
            isEqual = (o.width == width
                       &&
                       o.height == height
                       &&
                       o.transformer.equals(transformer));
        }
        return isEqual;
    }
}
