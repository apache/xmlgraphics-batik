/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image;

import java.awt.Color;

/**
 * A light source which emits a light of constant intensity in all directions.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public class SpotLight implements Light {
    /**
     * The light position, in user space
     */
    private double lightX, lightY, lightZ;

    /**
     * Point where the light points to
     */
    private double pointAtX, pointAtY, pointAtZ;

    /**
     * Specular exponent (light focus)
     */
    private double specularExponent;

    /**
     * Limiting cone angle
     */
    private double limitingConeAngle, limitingCos;

    /**
     * Light color
     */
    private double[] color;

    /**
     * Light direction vector
     */
    private final double[] S = new double[3];

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
     * @return x-axis coordinate where the light points to
     */
    public double getPointAtX(){
        return pointAtX;
    }

    /**
     * @return y-axis coordinate where the light points to
     */
    public double getPointAtY(){
        return pointAtY;
    }

    /**
     * @return z-axis coordinate where the light points to
     */
    public double getPointAtZ(){
        return pointAtZ;
    }

    /**
     * @return light's specular exponent (focus)
     */
    public double getSpecularExponent(){
        return specularExponent;
    }

    /**
     * @return light's limiting cone angle
     */
    public double getLimitingConeAngle(){
        return limitingConeAngle;
    }

    /**
     * @return the light's color
     */
    public double[] getColor(){
        return color;
    }

    public SpotLight(final double lightX, final double lightY, final double lightZ,
                     final double pointAtX, final double pointAtY, final double pointAtZ,
                     final double specularExponent, final double limitingConeAngle,
                     final Color lightColor){
        this.lightX = lightX;
        this.lightY = lightY;
        this.lightZ = lightZ;
        this.pointAtX = pointAtX;
        this.pointAtY = pointAtY;
        this.pointAtZ = pointAtZ;
        this.specularExponent = specularExponent;
        this.limitingConeAngle = limitingConeAngle;
        this.limitingCos = Math.cos(limitingConeAngle*Math.PI/180.);

        setColor(lightColor);

        S[0] = pointAtX - lightX;
        S[1] = pointAtY - lightY;
        S[2] = pointAtZ - lightZ;

        double norm = Math.sqrt(S[0]*S[0]
                                + S[1]*S[1]
                                + S[2]*S[2]);

        S[0] /= norm;
        S[1] /= norm;
        S[2] /= norm;
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
        return false;
    }

    /**
     * Computes the light vector in (x, y, z)
     *
     * @param x x-axis coordinate where the light should be computed
     * @param y y-axis coordinate where the light should be computed
     * @param z z-axis coordinate where the light should be computed
     * @param L array of length 3 where the result is stored
     */
    public final void getLight(final double x, final double y, final double z, final double L[]){
        // Light Vector, L
        L[0] = lightX - x;
        L[1] = lightY - y;
        L[2] = lightZ - z;

        final double norm = Math.sqrt(L[0]*L[0] +
                                      L[1]*L[1] +
                                      L[2]*L[2]);

        L[0] /= norm;
        L[1] /= norm;
        L[2] /= norm;
        
        double LS = -(L[0]*S[0] + L[1]*S[1] + L[2]*S[2]);
        
        if(LS > limitingCos){
            double Iatt = limitingCos/LS;
            Iatt *= Iatt;
            Iatt *= Iatt;
            Iatt *= Iatt;
            Iatt *= Iatt;
            Iatt *= Iatt;
            Iatt *= Iatt; // akin Math.pow(Iatt, 64)

            Iatt = 1 - Iatt;
            LS = Iatt*Math.pow(LS, specularExponent);
            
            L[0] *= LS;
            L[1] *= LS;
            L[2] *= LS;
        }
        else{
            L[0] = 0;
            L[1] = 0;
            L[2] = 0;
        }
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
        if (ret == null) 
            ret = new double[width][3];

        for(int i=0; i<width; i++){
            getLight(x, y, z[i][3], ret[i]);
            x += dx;
        }

        return ret;
    }
}

