/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.ext.awt.image;

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
     * Returns a row of the light map, starting at (x, y) with dx
     * increments, a given width, and z elevations stored in the
     * fourth component on the N array.
     *
     * @param x x-axis coordinate where the light should be computed
     * @param y y-axis coordinate where the light should be computed
     * @param dx delta x for computing light vectors in user space
     * @param width number of samples to compute on the x axis
     * @param z array containing the z elevation for all the points
     * @param lightRwo array to store the light info to, if null it will
     *                 be allocated for you and returned.
     *
     * @return an array width columns where each element
     *         is an array of three components representing the x, y and z
     *         components of the light vector.  */
    public double[][] getLightRow(double x, double y, 
                                  final double dx, final int width,
                                  final double[][] z,
                                  final double[][] lightRow);

    /**
     * @param  linear if true the color is returned in the Linear sRGB
     *                colorspace otherwise the color is in the gamma
     *                corrected sRGB color space.
     * @return the light's color 
     */
    public double[] getColor(boolean linear);

    /**
     * Sets the light color to a new value
     */
    public void setColor(Color color);
}

