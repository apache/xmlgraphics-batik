/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

/**
 * Considers width/height to be in the attached object's bounding
 * box space.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class DimensionTransformerBoundingBox implements DimensionTransformer {

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
     * @param node the <tt>GralphicsNode</tt> whose bounding box
     *        defines the dimension's space
     */
    public DimensionTransformerBoundingBox(GraphicsNode node){
        if(node == null){
            throw new IllegalArgumentException();
        }

        this.node = node;
    }

    /**
     * Converts the input width to a width in user space
     */
    public float widthToUserSpace(float width){
        TransformDescriptor txf = getTransformDescriptor();
        return width*txf.sx;
    }

    /**
     * Converts the input height to a height in user space
     */
    public float heightToUserSpace(float height){
        TransformDescriptor txf = getTransformDescriptor();
        return height*txf.sy;
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
