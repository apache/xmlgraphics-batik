/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

import org.apache.batik.gvt.filter.CachableRed;
import org.apache.batik.gvt.filter.Light;
import org.apache.batik.refimpl.gvt.filter.BumpMap;
import org.apache.batik.util.awt.image.GraphicsUtil;

/**
 * 
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class DiffuseLightingRed extends AbstractRed{
    /**
     * Diffuse lighting constant
     */
    private double kd;

    /**
     * Light used for diffuse lighting
     */
    private Light light;

    /**
     * BumpMap source
     */
    private BumpMap bumpMap;

    /**
     * Device space to user space scale factors, along
     * each axis
     */
    private double scaleX, scaleY;

    /**
     * LitRegion
     */
    private Rectangle litRegion;

    public DiffuseLightingRed(double kd,
                              Light light,
                              BumpMap bumpMap,
                              Rectangle litRegion,
                              double scaleX, double scaleY){
        this.kd = kd;
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
        int r=0, g=0, b=0;
        int i=0, j=0;
        int minX = wr.getMinX();
        int minY = wr.getMinY();

        System.out.println("Getting diffuse red : " + minX + "/" + minY + "/" + w + "/" + h);
        double x = scaleX*minX;
        double y = scaleY*minY;
        double NL = 0;

        double[] L = new double[3], N;
        double[][][] NA = bumpMap.getNormalArray(minX, minY, w, h);

        System.out.println(".....");
        for(i=0; i<h; i++){
            for(j=0; j<w; j++){
                // Get Normal 
                N = NA[i][j];

                // Get Light Vector
                light.getLight(x, y, N[3], L);

                NL = 255.*kd*(N[0]*L[0] + N[1]*L[1] + N[2]*L[2]);

                r = (int)(NL*lightColor[0]);
                g = (int)(NL*lightColor[1]);
                b = (int)(NL*lightColor[2]);

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);


                pixels[p++] = (0xff000000
                               |
                               r << 16
                               |
                               g << 8
                               |
                               b);

                x += scaleX;
            }
            p += adjust;
            x = scaleX*minX;
            y += scaleY;
        }
        
        return wr;
    }

}
