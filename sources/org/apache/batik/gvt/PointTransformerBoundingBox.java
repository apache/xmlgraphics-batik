/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This <tt>PointTransfomer</tt> implementation is used when the
 * point's space is in the associated node's bounding box space.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class PointTransformerBoundingBox implements PointTransformer {

    /**
     * <tt>GraphicsNode</tt> whose bounding box defines the
     * point space
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
     *        defines the point's space.
     */
    public PointTransformerBoundingBox(GraphicsNode node){
        if(node == null){
            throw new IllegalArgumentException();
        }

        this.node = node;
    }

    /**
     * Converts the input point to the point space
     */
    public Point2D toPointSpace(Point2D point){
        TransformDescriptor txf = getTransformDescriptor();

        if(txf.sx > 0){
            txf.sx = 1/txf.sx;
        }
        else{
            txf.sx = 0;
        }

        if(txf.sy > 0){
            txf.sy = 1/txf.sy;
        }
        else{
            txf.sy = 0;
        }

        point.setLocation(txf.sx*(point.getX() - txf.tx),
                          txf.sy*(point.getY() - txf.ty));
        return point;
    }

    /**
     * Converts the input point to user space
     */
    public Point2D toUserSpace(Point2D point){
        TransformDescriptor txf = getTransformDescriptor();
        point.setLocation(txf.sx*point.getX() + txf.tx,
                          txf.sy*point.getY() + txf.ty);
        return point;
    }

    /**
     * Returns the transform for the current <tt>GraphicsNode</tt>
     * bounding box.
     */
    private TransformDescriptor getTransformDescriptor(){
        Rectangle2D bounds = node.getPrimitiveBounds();
        if(bounds == null){
            throw new Error();
        }

        TransformDescriptor desc = new TransformDescriptor();
        if(desc == null){
            throw new Error();
        }

        desc.tx = (float)bounds.getX();
        desc.ty = (float)bounds.getY();
        desc.sx = (float)bounds.getWidth();
        desc.sy = (float)bounds.getHeight();

        return desc;
    }
}
