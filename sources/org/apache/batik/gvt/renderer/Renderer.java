/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import org.apache.batik.gvt.GraphicsNode;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.Shape;

import java.util.List;

/**
 * Interface for GVT Renderers.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public interface Renderer {

    /**
     * This associates the given GVT Tree with this renderer.
     * Any previous tree association is forgotten.
     * Not certain if this should be just GraphicsNode, or CanvasGraphicsNode.
     */
    public void setTree(GraphicsNode treeRoot);

    /**
     * Returns the GVT tree associated with this renderer
     */
    public GraphicsNode getTree();

    /**
     * Repaints the associated GVT tree at least under <tt>area</tt>.
     * 
     * @param areas the region to be repainted, in the current user
     * space coordinate system.  
     */
    public void repaint(Shape area);

    /**
     * Repaints the associated GVT tree at least in areas under the
     * list of <tt>areas</tt>.
     * 
     * @param areas a List of regions to be repainted, in the current
     * user space coordinate system.  
     */
    public void repaint(List areas);

    /**
     * Sets the transform from the current user space (as defined by
     * the top node of the GVT tree, to the associated device space.
     */
    public void setTransform(AffineTransform usr2dev);

    /**
     * Returns a copy of the transform from the current user space (as
     * defined by the top node of the GVT tree) to the device space (1
     * unit = 1/72nd of an inch / 1 pixel, roughly speaking
     */
    public AffineTransform getTransform();

    /**
     * Returns true if the Renderer is currently doubleBuffering is
     * rendering requests.  If it is then getOffscreen will only
     * return completed renderings (or null if nothing is available).  
     */
    public boolean isDoubleBuffered();

    /**
     * Turns on/off double buffering in renderer.  Turning off
     * double buffering makes it possible to see the ongoing results
     * of a render operation.
     .  */
    public void setDoubleBuffered(boolean isDoubleBuffered);

    /**
     * Cause the renderer to ask to be removed from external reference
     * lists, de-register as a listener to events, etc. so that
     * in the absence of other existing references, it can be
     * removed by the garbage collector.
     */
    public void dispose();

}

