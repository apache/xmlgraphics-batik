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

    /**
     * true if calculations should be performed in linear sRGB
     */
    private boolean linear;


    public DiffuseLightingRed(double kd,
                              Light light,
                              BumpMap bumpMap,
                              Rectangle litRegion,
                              double scaleX, double scaleY,
                              boolean linear){
        this.kd = kd;
        this.light = light;
        this.bumpMap = bumpMap;
        this.litRegion = litRegion;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.linear = linear;

        ColorModel cm;
        if (linear)
            cm = GraphicsUtil.Linear_sRGB_Pre;
        else
            cm = GraphicsUtil.sRGB_Pre;

        SampleModel sm = 
            cm.createCompatibleSampleModel(litRegion.width,
                                           litRegion.height);
                                             
        init((CachableRed)null, litRegion, cm, sm,
             litRegion.x, litRegion.y, null);
    }

    public WritableRaster copyData(WritableRaster wr){
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

        final int scanStride = sppsm.getScanlineStride();
        final int adjust = scanStride - w;
        int p = offset;
        int r=0, g=0, b=0;
        int i=0, j=0;

        // System.out.println("Getting diffuse red : " + minX + "/" + minY + "/" + w + "/" + h);
        double x = scaleX*minX;
        double y = scaleY*minY;
        double NL = 0;

        // final double[] L = new double[3];
        final double[][][] NA = bumpMap.getNormalArray(minX, minY, w, h);
        if(!light.isConstant()){
            final double[][] LA = new double[w][3];

            for(i=0; i<h; i++){
                final double [][] NR = NA[i];
                light.getLightRow(x, y+i*scaleY, scaleX, w, NR, LA);
                for(j=0; j<w; j++){
                    // Get Normal 
                    final double [] N = NR[j];
                    
                    // Get Light Vector
                    final double [] L = LA[j];
                    
                    NL = 255.*kd*(N[0]*L[0] + N[1]*L[1] + N[2]*L[2]);
                    
                    r = (int)(NL*lightColor[0]);
                    g = (int)(NL*lightColor[1]);
                    b = (int)(NL*lightColor[2]);
                    
                    // If any high bits are set we are not in range.
                    // If the highest bit is set then we are negative so
                    // clamp to zero else we are > 255 so clamp to 255.
                    if ((r & 0xFFFFFF00) != 0)
                        r = ((r & 0x80000000) != 0)?0:255;
                    if ((g & 0xFFFFFF00) != 0)
                        g = ((g & 0x80000000) != 0)?0:255;
                    if ((b & 0xFFFFFF00) != 0)
                        b = ((b & 0x80000000) != 0)?0:255;
                    
                    pixels[p++] = (0xff000000
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
            // System.out.println(">>>>>>>> Processing constant light ...");
            // Constant light
            final double[] L = new double[3];
            light.getLight(0, 0, 0, L);

            for(i=0; i<h; i++){
                final double [][] NR = NA[i];
                for(j=0; j<w; j++){
                    // Get Normal 
                    final double[] N = NR[j];
                    
                    NL = 255.*kd*(N[0]*L[0] + N[1]*L[1] + N[2]*L[2]);
                    
                    r = (int)(NL*lightColor[0]);
                    g = (int)(NL*lightColor[1]);
                    b = (int)(NL*lightColor[2]);
                    
                    // If any high bits are set we are not in range.
                    // If the highest bit is set then we are negative so
                    // clamp to zero else we are > 255 so clamp to 255.
                    if ((r & 0xFFFFFF00) != 0)
                        r = ((r & 0x80000000) != 0)?0:255;
                    if ((g & 0xFFFFFF00) != 0)
                        g = ((g & 0x80000000) != 0)?0:255;
                    if ((b & 0xFFFFFF00) != 0)
                        b = ((b & 0x80000000) != 0)?0:255;
                    
                    pixels[p++] = (0xff000000
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
        
        return wr;
    }

}
