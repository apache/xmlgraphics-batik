/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import org.apache.batik.gvt.GraphicsNode;

/**
 * Implements a Morphology operation, where the kernel size is
 * defined by radius along the x and y axis.
 *
 * @author <a href="mailto:sheng.pei@eng.sun.com>Sheng Pei</a>
 * @version $Id$
 */
public interface MorphologyRable extends Filter {
    /**
     * Returns the source to be offset.
     */
    public Filter getSource();

    /**
     * Sets the source to be offset.
     * @param src image to offset.
     */
    public void setSource(Filter src);

    /**
     * The radius along the x axis, in user space.
     * @param radiusX should be greater than zero.
     */
    public void setRadiusX(double radiusX);

    /**
     * The radius along the y axis, in user space.
     * @param radiusY should be greater than zero.
     */
    public void setRadiusY(double radiusY);

    /**
     * The switch that determines if the operation
     * is to "dilate" or "erode".
     * @param doDilation do "dilation" when true and "erosion" when false
     */
    public void setDoDilation(boolean doDilation);

    /**
     * Returns whether the operation is "dilation" or not("erosion")
     */
    public boolean getDoDilation();

    /**
     * Returns the radius along the x-axis, in user space.
     */
    public double getRadiusX();

    /**
     * Returns the radius along the y-axis, in user space.
     */
    public double getRadiusY();
}
