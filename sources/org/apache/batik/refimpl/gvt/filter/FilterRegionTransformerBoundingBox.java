/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.geom.Rectangle2D;

import org.apache.batik.gvt.GraphicsNode;

/**
 * This implementation of <tt>FilterRegionTransformer</tt> 
 * is used when the input <tt>FilterRegion</tt> space is the
 * such that the bounding box of the <tt>GraphicsNode</tt>
 * it references is (0, 0, 1, 1).
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class FilterRegionTransformerBoundingBox implements FilterRegionTransformer {
    /**
     * <tt>GraphicsNode</tt> whose bounding box is used to 
     * define the <tt>FilterRegion</tt> space.
     */
    private GraphicsNode node;

    /**
     * The following inner class is used to hold the computation
     * of the scale and translation components of the transform
     * applied by this transformer
     */
    private class TransformDescriptor {
        public float tx;
        public float ty;
        public float sx;
        public float sy;
    }

    /**
     * @param node the <tt>GraphicsNode</tt> whose bounding box
     *        defines the <tt>FilterRegion</tt> space.
     */
    public FilterRegionTransformerBoundingBox(GraphicsNode node){
        if(node == null){
            throw new IllegalArgumentException();
        }

        this.node = node;
    }

    /**
     * Converts the input region to the filter region space.
     */
    public Rectangle2D toFilterRegionSpace(Rectangle2D region){
        TransformDescriptor txf = getTransformDescriptor();
        if(txf.sx > 0){
            txf.sx = 1/txf.sx;
        }
        else{
            // Degenerate case. Map to zero.
            txf.sx = 0;
        }

        if(txf.sy > 0){
            txf.sy = 1/txf.sy;
        }
        else{
            // Degenerate case. Map to zero
            txf.sy = 0;
        }

        region.setRect(txf.sx*(region.getX() - txf.tx),
                       txf.sy*(region.getY() - txf.ty),
                       region.getWidth()*txf.sx,
                       region.getHeight()*txf.sy);
        return region;
    }

    /**
     * Converts the input region to user space
     */
    public Rectangle2D toUserSpace(Rectangle2D region){
        TransformDescriptor txf = getTransformDescriptor();
        region.setRect(txf.sx*region.getX() + txf.tx,
                       txf.sy*region.getY() + txf.ty,
                       region.getWidth()*txf.sx,
                       region.getHeight()*txf.sy);
        return region;
    }

    /**
     * Returns the transform for the current <tt>GraphicsNode</tt>
     * bounding box.
     */
    private TransformDescriptor getTransformDescriptor(){
        Rectangle2D bounds = node.getPrimitiveBounds();
        TransformDescriptor desc = new TransformDescriptor();
        desc.tx = (float)bounds.getX();
        desc.ty = (float)bounds.getY();
        desc.sx = (float)bounds.getWidth();
        desc.sy = (float)bounds.getHeight();

        return desc;
    }
}
