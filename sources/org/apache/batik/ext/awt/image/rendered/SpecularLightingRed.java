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


import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.Light;

/**
 * 
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SpecularLightingRed extends AbstractTiledRed{
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

    /**
     * true if calculations should be performed in linear sRGB
     */
    private boolean linear;
     

    public SpecularLightingRed(double ks,
                               double specularExponent,
                               Light light,
                               BumpMap bumpMap,
                               Rectangle litRegion,
                               double scaleX, double scaleY,
                               boolean linear) {
        this.ks = ks;
        this.specularExponent = specularExponent;
        this.light = light;
        this.bumpMap = bumpMap;
        this.litRegion = litRegion;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.linear = linear;

        ColorModel cm;
        if (linear)
            cm = GraphicsUtil.Linear_sRGB_Unpre;
        else
            cm = GraphicsUtil.sRGB_Unpre;

        int tw = litRegion.width;
        int th = litRegion.height;
        int defSz = AbstractTiledRed.getDefaultTileSize();
        if (tw > defSz) tw = defSz;
        if (th > defSz) th = defSz;
        SampleModel sm = cm.createCompatibleSampleModel(tw, th);
                                             
        init((CachableRed)null, litRegion, cm, sm,
             litRegion.x, litRegion.y, null);
    }

    public WritableRaster copyData(WritableRaster wr) {
        copyToRaster(wr);
        return wr;
    }

    public void genRect(WritableRaster wr) {
        // Copy variable on stack for faster access in thight loop
        final double scaleX = this.scaleX;
        final double scaleY = this.scaleY;

        final double[] lightColor = light.getColor(linear);

        final int w = wr.getWidth();
        final int h = wr.getHeight();
        final int minX = wr.getMinX();
        final int minY = wr.getMinY();

        final DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        final int[] pixels = db.getBankData()[0];

        final SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();

        final int offset = 
            (db.getOffset() +
             sppsm.getOffset(minX-wr.getSampleModelTranslateX(), 
                             minY-wr.getSampleModelTranslateY()));
        // int offset = db.getOffset();
        final int scanStride = sppsm.getScanlineStride();
        final int adjust = scanStride - w;
        int p = offset;
        int r=0, g=0, b=0, a=0;
        int i=0, j=0;

        // x and y are in user space
        double x = scaleX*minX;
        double y = scaleY*minY;
        double ksPwNHns = 0, norm = 0;

        final double[][][] NA = bumpMap.getNormalArray(minX, minY, w, h);

        // System.out.println("Entering Specular Lighting");
        if(!light.isConstant()){
            final double[][] LA = new double[w][3];
            for(i=0; i<h; i++){
                // System.out.println("Row: " + i);
                final double [][] NR = NA[i];
                light.getLightRow(x, y+i*scaleY, scaleX, w, NR, LA);
                for (j=0; j<w; j++){
                    // Get Normal 
                    final double [] N = NR[j];
                    
                    // Get Light Vector
                    final double [] L = LA[j];

                    // Compute Half-way vector
                    L[2] += 1;
                    norm = Math.sqrt(L[0]*L[0] + L[1]*L[1] + L[2]*L[2]);
                    if(norm > 0){
                        double iNorm = 1.0/norm;
                        L[0] *= iNorm;
                        L[1] *= iNorm;
                        L[2] *= iNorm;
                    }
                    
                    ksPwNHns = 255.*ks*Math.pow(N[0]*L[0] + 
                                                N[1]*L[1] + 
                                                N[2]*L[2], specularExponent);
                    
                    r = (int)(ksPwNHns*lightColor[0]);
                    g = (int)(ksPwNHns*lightColor[1]);
                    b = (int)(ksPwNHns*lightColor[2]);
                    
                    
                    // If any high bits are set we are not in range.
                    // If the highest bit is set then we are negative so
                    // clamp to zero else we are > 255 so clamp to 255.
                    if ((r & 0xFFFFFF00) != 0)
                        r = ((r & 0x80000000) != 0)?0:255;
                    if ((g & 0xFFFFFF00) != 0)
                        g = ((g & 0x80000000) != 0)?0:255;
                    if ((b & 0xFFFFFF00) != 0)
                        b = ((b & 0x80000000) != 0)?0:255;
                    
                    a = r > g ? r : g;
                    a = a > b ? a : b;
                    
                    pixels[p++] = (a << 24
                                   |
                                   r << 16
                                   |
                                   g << 8
                                   |
                                   b);
                    
                }
                p += adjust;
            }
        }
        else{
            // Get constant light vector
            final double[] L = new double[3];
            light.getLight(0, 0, 0, L);

            // Compute Half-way vector
            L[2] += 1;
            norm = Math.sqrt(L[0]*L[0] + L[1]*L[1] + L[2]*L[2]);
            if(norm > 0){
                L[0] /= norm;
                L[1] /= norm;
                L[2] /= norm;
            }

            for(i=0; i<h; i++){
                final double [][] NR = NA[i];
                for(j=0; j<w; j++){
                    // Get Normal 
                    final double [] N = NR[j];
                    
                    ksPwNHns = 255.*ks*Math.pow(N[0]*L[0] + N[1]*L[1] + N[2]*L[2],
                                                specularExponent);
                    
                    r = (int)(ksPwNHns*lightColor[0]);
                    g = (int)(ksPwNHns*lightColor[1]);
                    b = (int)(ksPwNHns*lightColor[2]);
                    
                    // If any high bits are set we are not in range.
                    // If the highest bit is set then we are negative so
                    // clamp to zero else we are > 255 so clamp to 255.
                    if ((r & 0xFFFFFF00) != 0)
                        r = ((r & 0x80000000) != 0)?0:255;
                    if ((g & 0xFFFFFF00) != 0)
                        g = ((g & 0x80000000) != 0)?0:255;
                    if ((b & 0xFFFFFF00) != 0)
                        b = ((b & 0x80000000) != 0)?0:255;
                    
                    a = r > g ? r : g;
                    a = a > b ? a : b;
                    
                    pixels[p++] = (a << 24
                                   |
                                   r << 16
                                   |
                                   g << 8
                                   |
                                   b);
                    
                }
                p += adjust;
            }
        }
        // System.out.println("Exiting Specular Lighting");
    }
}
