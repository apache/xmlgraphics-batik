/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

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
     * Returns a light map, starting in (x, y) with dx, dy increments, a given
     * width and height, and z elevations stored in the fourth component on the 
     * N array.
     *
     * @param x x-axis coordinate where the light should be computed
     * @param y y-axis coordinate where the light should be computed
     * @param dx delta x for computing light vectors in user space
     * @param dy delta y for computing light vectors in user space
     * @param width number of samples to compute on the x axis
     * @param height number of samples to compute on the y axis
     * @param z array containing the z elevation for all the points
     *
     * @return an array of height rows, width columns where each element
     *         is an array of three components representing the x, y and z
     *         components of the light vector.
     */
    public double[][][] getLightMap(double x, double y, 
                                  final double dx, final double dy,
                                  final int width, final int height,
                                  final double[][][] z);

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

