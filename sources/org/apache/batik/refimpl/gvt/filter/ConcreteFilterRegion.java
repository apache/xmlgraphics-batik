/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.FilterRegion;

/**
 * This class implements the <tt>FilterRegion</tt> interface. It
 * keeps a reference to a <tt>GraphicsNode</tt> and computes the 
 * region as follows. An initial region is computed from the 
 * <tt>GraphicsNode</tt>. Then, if the x, y, width and height 
 * overrides are defined, they are used as replacements in the
 * initially computed region.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ConcreteFilterRegion implements FilterRegion{
    /**
     * x override
     */
    private Float x;

    /**
     * y override
     */
    private Float y;

    /**
     * width override
     */
    private Float width;

    /**
     * height override
     */
    private Float height;

    /**
     * Node
     */
    private GraphicsNode node;

    /**
     * @param node from which the filter region should be
     *        computed.
     * @param x x override
     * @param y y override
     * @param width width override
     * @param height height override
     */
    public ConcreteFilterRegion(GraphicsNode node,
                                Float x,
                                Float y,
                                Float width,
                                Float height){
        this.node = node;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the filter region built with the current 
     * bounds of the referenced <tt>GraphicsNode</tt>
     */
    public Rectangle2D getRegion(){
        // Initialize a rectangle with the GraphicsNode bounds
        Rectangle2D nodeBounds = node.getBounds();
        Rectangle2D.Float bounds 
            = new Rectangle2D.Float((float)nodeBounds.getX(),
                                    (float)nodeBounds.getY(),
                                    (float)nodeBounds.getWidth(),
                                    (float)nodeBounds.getHeight());

       if(x != null){
            bounds.x = x.floatValue();
        }

        if(y != null){
            bounds.y = y.floatValue();
        }

        if(width != null){
            bounds.width = width.floatValue();
        }

        if(height != null){
            bounds.height = height.floatValue();
        }

        return bounds;
    }
}
