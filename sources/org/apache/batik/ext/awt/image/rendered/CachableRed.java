/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;

import java.awt.Shape;
import java.awt.Rectangle;

import java.awt.image.RenderedImage;

/**
 * This provides a number of extra methods that enable a system to
 * better analyse the dependencies between nodes in a render graph.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
*/
public interface CachableRed extends RenderedImage {

    /**
     * Returns the bounds of the current image.
     * This should be 'in sync' with getMinX, getMinY, getWidth, getHeight
     */
    public Rectangle getBounds();

    /**
     * Returns the region of input data is is required to generate
     * outputRgn.
     * @param srcIndex  The source to do the dependency calculation for.
     * @param outputRgn The region of output you are interested in
     * generating dependencies for.  The is given in the output pixel
     * coordiate system for this node.
     * @return The region of input required.  This is in the output pixel
     * coordinate system for the source indicated by srcIndex.
     */
    public Shape getDependencyRegion(int srcIndex, Rectangle outputRgn);

    /**
     * This calculates the region of output that is affected by a change
     * in a region of input.
     * @param srcIndex The input that inputRgn reflects changes in.
     * @param inputRgn the region of input that has changed, used to
     *  calculate the returned shape.  This is given in the pixel
     *  coordinate system of the source indicated by srcIndex.
     * @return The region of output that would be invalid given
     *  a change to inputRgn of the source selected by srcIndex.
     *  this is in the output pixel coordinate system of this node.
     */
    public Shape getDirtyRegion(int srcIndex, Rectangle inputRgn);
}

