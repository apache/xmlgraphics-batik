/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.geom.Rectangle2D;

import org.apache.batik.gvt.filter.FilterRegion;

/**
 * This implementation of <tt>FilterRegion</tt> is initialized
 * with set of attributes that define its filter region. The
 * <tt>FilterRegion</tt> default it gets in its constructor is
 * used to compute x, y, width or height values if none is 
 * provide (i.e., if the input values are null).
 * The <tt>FilterRegionTransformer</tt> is used to transform 
 * coordinates to user space.
 * 
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class FilterPrimitiveRegion implements FilterRegion {
    /**
     * This region's coordinates, in <tt>FilterRegion</tt>. Null
     * values mean that the corresponding value should be used
     */
    private Float x, y, width, height;

    /**
     * This input region is used to compute default values in
     * case one of the region attributes (x, y, width or height)
     * is not defined.
     */
    private FilterRegion defaultRegion;

    /**
     * <tt>FilterRegion</tt> space to user space transformer, to
     * be used for this <tt>FilterPrimitiveRegion</tt>
     * @see #getRegion
     */
    private FilterRegionTransformer primitiveRegionTransformer;

    /**
     * @param x x-coordinate for this filter chain region
     * @param y y-coordinate for this filter chain region
     * @param width wdith for this filter chain region
     * @param height height for this filter chain region
     * @param primitiveRegionTransformer filter chain space to user space 
     *        transformer
     * @param defaultRegion used to compute default value, in case the input
     *        x, y, width or height values are undefined.
     */
    public FilterPrimitiveRegion(Float x,
                                 Float y,
                                 Float width,
                                 Float height,
                                 FilterRegionTransformer primitiveRegionTransformer,
                                 FilterRegion defaultRegion){
        if(primitiveRegionTransformer == null){
            throw new IllegalArgumentException();
        }

        if(defaultRegion == null){
            throw new IllegalArgumentException();
        }
        
        this.primitiveRegionTransformer = primitiveRegionTransformer;
        this.defaultRegion = defaultRegion;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /*
     * Returns this object's filter region, in user space.
     */
    public Rectangle2D getRegion(){
        Rectangle2D defaultBounds = defaultRegion.getRegion();
        defaultBounds  = primitiveRegionTransformer.toFilterRegionSpace(defaultBounds);

        Float x = this.x;
        Float y = this.y;
        Float width = this.width;
        Float height = this.height;

        if(x == null){
            x = new Float((float)defaultBounds.getX());
        }
        if(y == null){
            y = new Float((float)defaultBounds.getY());
        }
        if(width == null){
            width = new Float((float)defaultBounds.getWidth());
        }
        if(height == null){
            height = new Float((float)defaultBounds.getHeight());
        }
        Rectangle2D bounds = new Rectangle2D.Float(x.floatValue(),
                                                   y.floatValue(), 
                                                   width.floatValue(), 
                                                   height.floatValue());

        bounds = primitiveRegionTransformer.toUserSpace(bounds);
        return bounds;
    }
}
