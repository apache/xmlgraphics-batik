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
 * with a region in <tt>FilterRegion</tt> space and a 
 * <tt>FilterRegionTransformer</tt> to transform this region
 * into user space coordinates upon calls to <tt>getRegion</tt>
 * 
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class FilterChainRegion implements FilterRegion {
    /**
     * This region's coordinates, in <tt>FilterRegion</tt> space
     */
    private float x, y, width, height;

    /**
     * <tt>FilterRegion</tt> space to user space transformer, to
     * be used for this <tt>FilterChainRegion</tt>
     * @see #getRegion
     */
    private FilterRegionTransformer chainRegionTransformer;

    /**
     * @param x x-coordinate for this filter chain region
     * @param y y-coordinate for this filter chain region
     * @param width wdith for this filter chain region
     * @param height height for this filter chain region
     * @param chainRegionTransformer filter chain space to user space 
     *        transformer
     */
    public FilterChainRegion(float x,
                             float y,
                             float width,
                             float height,
                             FilterRegionTransformer chainRegionTransformer){
        if(chainRegionTransformer == null){
            throw new IllegalArgumentException();
        }

        this.chainRegionTransformer = chainRegionTransformer;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /*
     * Returns this object's filter region, in user space
     */
    public Rectangle2D getRegion(){
        Rectangle2D bounds = new Rectangle2D.Float(x, y, width, height);
        return chainRegionTransformer.toUserSpace(bounds);
    }
}
