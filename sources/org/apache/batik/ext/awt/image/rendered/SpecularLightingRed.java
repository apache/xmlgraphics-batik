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

package org.apache.batik.ext.awt.image.rendered;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
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
        double norm = 0;

        int pixel = 0, tmp;
        double mult;
        mult = (lightColor[0]>lightColor[1])?lightColor[0]:lightColor[1];
        mult = (mult>lightColor[2])?mult:lightColor[2];
        
        double scale = 255/mult;
        pixel = (int)(lightColor[0]*scale+0.5);
        tmp   = (int)(lightColor[1]*scale+0.5);
        pixel = pixel<<8 | tmp;
        tmp   = (int)(lightColor[2]*scale+0.5);
        pixel = pixel<<8 | tmp;

        mult*=255*ks;

        // System.out.println("Pixel: 0x" + Integer.toHexString(pixel));

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
                    norm = L[0]*L[0] + L[1]*L[1] + L[2]*L[2];
                    if(norm == 0) 
                        a = (int)(mult+0.5);
                    else {
                        norm = Math.sqrt(norm);
                        a = (int)(mult*Math.pow((N[0]*L[0] + 
                                                 N[1]*L[1] + N[2]*L[2])/norm, 
                                                specularExponent) + 0.5);
                        if ((a & 0xFFFFFF00) != 0)
                            a = ((a & 0x80000000) != 0)?0:255;
                    }
                    pixels[p++] = (a << 24 | pixel);
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
                    
                    a = (int)(mult*Math.pow(N[0]*L[0] + N[1]*L[1] + N[2]*L[2], 
                                            specularExponent) + 0.5);
                    
                    if ((a & 0xFFFFFF00) != 0)
                        a = ((a & 0x80000000) != 0)?0:255;

                    pixels[p++] = (a << 24 | pixel);
                }
                p += adjust;
            }
        }
        // System.out.println("Exiting Specular Lighting");
    }
}
