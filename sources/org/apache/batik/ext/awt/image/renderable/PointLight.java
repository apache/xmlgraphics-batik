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
 * A light source which emits a light of constant intensity in all directions.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public class PointLight implements Light {
    /**
     * The light position, in user space
     */
    private double lightX, lightY, lightZ;

    /**
     * Light color
     */
    private double[] color;

    /**
     * @return the light's x position
     */
    public double getLightX(){
        return lightX;
    }

    /**
     * @return the light's y position
     */
    public double getLightY(){
        return lightY;
    }

    /**
     * @return the light's z position
     */
    public double getLightZ(){
        return lightZ;
    }

    /**
     * @return the light's color
     */
    public double[] getColor(){
        return color;
    }

    public PointLight(double lightX, double lightY, double lightZ,
                      Color lightColor){
        this.lightX = lightX;
        this.lightY = lightY;
        this.lightZ = lightZ;
        setColor(lightColor);
    }

    /**
     * Sets the new light color
     */
    public void setColor(Color newColor){
        double[] color = new double[3];
        color[0] = newColor.getRed()/255.;
        color[1] = newColor.getGreen()/255.;
        color[2] = newColor.getBlue()/255.;

        this.color = color;
    }

    /**
     * @return true if the light is constant over the whole surface
     */
    public boolean isConstant(){
        return true;
    }

    /**
     * Computes the light vector in (x, y, z)
     *
     * @param x x-axis coordinate where the light should be computed
     * @param y y-axis coordinate where the light should be computed
     * @param z z-axis coordinate where the light should be computed
     * @param L array of length 3 where the result is stored
     */
    public void getLight(final double x, final double y, final double z, final double L[]){
        L[0] = lightX - x;
        L[1] = lightY - y;
        L[2] = lightZ - z;

        double norm = Math.sqrt(L[0]*L[0] +
                                L[1]*L[1] +
                                L[2]*L[2]);

        L[0] /= norm;
        L[1] /= norm;
        L[2] /= norm;
    }
}

