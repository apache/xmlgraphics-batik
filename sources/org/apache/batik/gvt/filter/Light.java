/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import java.awt.Color;

/**
 * Top level interface to model a light element. A light is responsible for
 * computing the light vector on a given point of a surface. A light is
 * typically in a 3 dimensional space and the methods assumes the surface
 * is at elevation 0.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public interface Light {
    /**
     * @return true if the light is constant over the whole surface
     */
    public boolean isConstant();

    /**
     * Computes the light vector in (x, y)
     *
     * @param x x-axis coordinate where the light should be computed
     * @param y y-axis coordinate where the light should be computed
     * @param z z-axis coordinate where the light should be computed
     * @param L array of length 3 where the result is stored
     */
    public void getLight(final double x, final double y, final double z, final double L[]);

    /**
     * @return the Light's color. The return value is an array of normalized
     *         RGB intensities.
     */
    public double[] getColor();

    /**
     * Sets the light color to a new value
     */
    public void setColor(Color color);
}

