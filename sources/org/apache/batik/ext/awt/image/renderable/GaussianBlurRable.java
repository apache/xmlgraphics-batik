/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

/**
 * Implements a GaussianBlur operation, where the blur size is
 * defined by standard deviations along the x and y axis.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public interface GaussianBlurRable extends FilterColorInterpolation {

    /**
     * Returns the source to be Blurred
     */
    public Filter getSource();

    /**
     * Sets the source to be blurred.
     * @param src image to blurred.
     */
    public void setSource(Filter src);

    /**
     * The deviation along the x axis, in user space.
     * @param stdDeviationX should be greater than zero.
     */
    public void setStdDeviationX(double stdDeviationX);

    /**
     * The deviation along the y axis, in user space.
     * @param stdDeviationY should be greater than zero
     */
    public void setStdDeviationY(double stdDeviationY);

    /**
     * Returns the deviation along the x-axis, in user space.
     */
    public double getStdDeviationX();

    /**
     * Returns the deviation along the y-axis, in user space.
     */
    public double getStdDeviationY();
}
