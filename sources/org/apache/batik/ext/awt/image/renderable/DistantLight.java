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
 * A light source placed at the infinity, such that the light angle is
 * constant over the whole surface.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public class DistantLight implements Light {
    /**
     * The azimuth of the distant light, i.e., the angle of the light vector
     * on the (X, Y) plane
     */
    private double azimuth;

    /**
     * The elevation of the distant light, i.e., the angle of the light
     * vector on the (X, Z) plane.
     */
    private double elevation;

    /**
     * Light vector
     */
    private double Lx, Ly, Lz;

    /**
     * Light color
     */
    private double[] color;

    /**
     * @return the DistantLight's azimuth
     */
    public double getAzimuth(){
        return azimuth;
    }

    /**
     * @return the DistantLight's elevation
     */
    public double getElevation(){
        return elevation;
    }

    /**
     * @return the light's color
     */
    public double[] getColor(){
        return color;
    }

    public DistantLight(double azimuth, double elevation, Color color){
        this.azimuth = azimuth;
        this.elevation = elevation;
        setColor(color);

        Lx = Math.cos(Math.PI*azimuth/180.)*Math.cos(Math.PI*elevation/180.);
        Ly = Math.sin(Math.PI*azimuth/180.)*Math.cos(Math.PI*elevation/180.);
        Lz = Math.sin(Math.PI*elevation/180);
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
     * Computes the light vector in (x, y)
     *
     * @param x x-axis coordinate where the light should be computed
     * @param y y-axis coordinate where the light should be computed
     * @param L array of length 3 where the result is stored
     */
    public void getLight(final double x, final double y, final double z, final double L[]){
        L[0] = Lx;
        L[1] = Ly;
        L[2] = Lz;
    }

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
     */
    public double[][][] getLightMap(double x, double y, 
                                  final double dx, final double dy,
                                  final int width, final int height,
                                  final double[][][] z)
    {
        double[][][] L = new double[height][][];

        for(int i=0; i<height; i++){
            L[i] = getLightRow(x, y, dx, width, z[i], null);
            y += dy;
        }

        return L;
    }

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
                                  final double[][] lightRow) {
        double [][] ret = lightRow;

        if (ret == null) {
            ret = new double[width][];

            double[] CL = new double[3];
            getLight(0, 0, 0, CL);

            for(int i=0; i<width; i++){
                ret[i] = CL;
            }
        } else {
            final double lx = Lx;
            final double ly = Ly;
            final double lz = Lz;

            for(int i=0; i<width; i++){
                ret[i][0] = lx;
                ret[i][1] = ly;
                ret[i][2] = lz;
            }
        }

        return ret;
    }
}

