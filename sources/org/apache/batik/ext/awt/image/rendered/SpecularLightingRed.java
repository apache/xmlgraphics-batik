/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;


import org.apache.batik.ext.awt.image.renderable.Light;
import org.apache.batik.ext.awt.image.renderable.BumpMap;
import org.apache.batik.ext.awt.image.GraphicsUtil;

/**
 * 
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SpecularLightingRed extends AbstractRed{
    /**
     * Specular lighting constant
     */
    private double ks;

    /**
     * Specular lighting exponent
     */
    private double specularExponent;

    /**
     * Light used for specular lighting
     */
    private Light light;

    /**
     * BumpMap source
     */
    private BumpMap bumpMap;

    /**
     * Device space to user space scale factors, along
     * each axis.
     */
    private double scaleX, scaleY;

    /**
     * LitRegion
     */
    private Rectangle litRegion;

    public SpecularLightingRed(double ks,
                               double specularExponent,
                               Light light,
                               BumpMap bumpMap,
                               Rectangle litRegion,
                               double scaleX, double scaleY){
        this.ks = ks;
        this.specularExponent = specularExponent;
        this.light = light;
        this.bumpMap = bumpMap;
        this.litRegion = litRegion;
        this.scaleX = scaleX;
        this.scaleY = scaleY;

        /*ColorModel cm 
            = new DirectColorModel(32, 
                                   0x00ff0000,// Red
                                   0x0000ff00,// Green
                                   0x000000ff,// Blue
                                   0xff000000 // Alpha
                                   );*/

        ColorModel cm = GraphicsUtil.Linear_sRGB_Unpre;

        SampleModel sm = 
            cm.createCompatibleSampleModel(litRegion.width,
                                           litRegion.height);
                                             
        init((CachableRed)null, litRegion, cm, sm,
             litRegion.x, litRegion.y, null);
    }

    /**
     * Lookup tables for RGB lookups. The linearToSRGBLut is used
     * when noise values are considered to be on a linearScale. The
     * linearToLinear table is used when the values are considered to
     * be on the sRGB scale to begin with.
     */
    /*private static final int linearToSRGBLut[] = new int[256];
    private static final double GAMMA = 1./2.4;
    static{
        for(int i=0; i<256; i++){
            double value = i/255.;
            if(value <= 0.0031308)
                value *= 12.92;
            else
                value = 1.055 * Math.pow(value, GAMMA) - 0.055;

            linearToSRGBLut[i] = (int)Math.round(value*255.);
        }
        }*/

    public WritableRaster copyData(WritableRaster wr){
        double[] lightColor = light.getColor();
        
        int w = wr.getWidth();
        int h = wr.getHeight();
        DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        int[] pixels = db.getBankData()[0];
        int offset = db.getOffset();
        int scanStride = 
            ((SinglePixelPackedSampleModel)wr.getSampleModel())
            .getScanlineStride();
        int adjust = scanStride - w;
        int p = offset;
        int r=0, g=0, b=0, a=0;
        int i=0, j=0;
        int minX = wr.getMinX();
        int minY = wr.getMinY();

        // x and y are in user space
        double x = scaleX*minX;
        double y = scaleY*minY;
        double ksPwNHns = 0, norm = 0;

        double[] L = new double[3], N;
        double[][][] NA = bumpMap.getNormalArray(minX, minY, w, h);

        for(i=0; i<h; i++){
            for(j=0; j<w; j++){
                // Get Normal 
                N = NA[i][j];

                // Get Light Vector
                light.getLight(x, y, N[3], L);
                // System.out.println("L : " + L[0] + "/" + L[1] + "/" + L[2]);

                // Compute Half-way vector
                L[2] += 1;
                norm = Math.sqrt(L[0]*L[0] + L[1]*L[1] + L[2]*L[2]);
                if(norm > 0){
                    L[0] /= norm;
                    L[1] /= norm;
                    L[2] /= norm;
                }

                // System.out.println("N : " + N[0] + "/" + N[1] + "/" + N[2]);
                // System.out.println("L : " + L[0] + "/" + L[1] + "/" + L[2]);
 
                ksPwNHns = 255.*ks*Math.pow(N[0]*L[0] + N[1]*L[1] + N[2]*L[2],
                                            specularExponent);

                r = (int)(ksPwNHns*lightColor[0]);
                g = (int)(ksPwNHns*lightColor[1]);
                b = (int)(ksPwNHns*lightColor[2]);

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                a = r > g ? r : g;
                a = a > b ? a : b;

                pixels[p++] = (a << 24
                               |
                               r << 16
                               |
                               g << 8
                               |
                               b);

                x += scaleX;
            }
            x = scaleX*minX;
            y += scaleY;
            p += adjust;
        }
        
        return wr;
    }

}
