/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

import org.apache.batik.gvt.filter.CachableRed;
import org.apache.batik.gvt.filter.Light;
import org.apache.batik.gvt.filter.BumpMap;

/**
 * Default BumpMap implementation.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public class DefaultBumpMap implements BumpMap {
    /**
     * Image whose alpha channel is used for the 
     * normal calculation
     */
    private RenderedImage texture;

    /**
     * Surface scale used in the normal computation
     */
    private double surfaceScale, surfaceScaleX, surfaceScaleY;

    /**
     * User space to device space scale factors
     */
    private double scaleX, scaleY;

    /**
     * Stores the normals for this bumpMap.
     * scaleX and scaleY are the user space to device 
     * space scales.
     */
    public DefaultBumpMap(RenderedImage texture,
                          double surfaceScale,
                          double scaleX, double scaleY){
        this.texture = texture;
        this.surfaceScaleX = surfaceScale*scaleX;
        this.surfaceScaleY = surfaceScale*scaleY;
        this.surfaceScale = surfaceScale;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    /**
     * @return surface scale used by this bump map.
     */
    public double getSurfaceScale(){
        return surfaceScale;
    }

    /**
     * @param x x-axis coordinate for which the normal is computed
     * @param y y-axis coordinate for which the normal is computed
     */
    public double[][][] getNormalArray
        (int x, int y, 
         int w, int h)
    {
        Raster r = texture.getData(new Rectangle(x, y, w, h));

        double[][][] N = new double[h][w][4];
        double[] n;

        DataBufferInt db = (DataBufferInt)r.getDataBuffer();
        int[] pixels = db.getBankData()[0];
        int offset = db.getOffset();
        int scanStride = 
        ((SinglePixelPackedSampleModel)r.getSampleModel())
        .getScanlineStride();
        int adjust = scanStride - w;
        int p = offset + scanStride;
        int a = 0;
        int i=0, j=0;
        double prpc=0, prcc=0, prnc=0;
        double crpc=0, crcc=0, crnc=0;
        double nrpc=0, nrcc=0, nrnc=0;
        double norm;

        if(w > 1){
            for(i=1; i<h-1; i++){
                
                crcc = ((pixels[p] >> 24) & 0xff)/255.;
                crnc = ((pixels[p+1] >> 24) & 0xff)/255.;
                prcc = ((pixels[p - scanStride] >> 24) & 0xff)/255.;
                prnc = ((pixels[p - scanStride + 1] >> 24) & 0xff)/255.;
                nrcc = ((pixels[p + scanStride] >> 24) & 0xff)/255.;
                nrnc = ((pixels[p + scanStride + 1] >> 24) & 0xff)/255.;
                
                p++; // start in 1, not 0.
                for(j=1; j<w-1; j++){
                    prpc = prcc;
                    crpc = crcc;
                    nrpc = nrcc;
                    prcc = prnc;
                    crcc = crnc;
                    nrcc = nrnc;
                    
                    crnc = ((pixels[p+1] >> 24) & 0xff)/255.;
                    prnc = ((pixels[p - scanStride + 1] >> 24) & 0xff)/255.;
                    nrnc = ((pixels[p + scanStride + 1] >> 24) & 0xff)/255.;
                    
                    n = N[i][j];
                    n[0] = - surfaceScaleX * 1/4*(( prnc + 2*crnc + nrnc)
                                                  - (prpc + 2*crpc + nrpc));
                    n[1] = - surfaceScaleY * 1/4*(( nrpc + 2*nrcc + nrnc)
                                                  - (prpc + 2*prcc + prnc));
                    
                    norm = Math.sqrt(n[0]*n[0] + n[1]*n[1] + 1);
                    n[0] /= norm;
                    n[1] /= norm;
                    n[2] = 1/norm;
                    n[3] = crcc*surfaceScale;
                    p++;
                }
                p += adjust + 1;
            }

            /*}catch(ArrayIndexOutOfBoundsException e){
              System.err.println("w : " + w);
              System.err.println("h : " + h);
              System.err.println("i : " + i);
              System.err.println("j : " + j);
              System.err.println("scanStride : " + scanStride);
              e.printStackTrace();
              throw new Error();
              }*/

            // Process pixels on the border
            if(h>1){
                p = offset;

                // Top left pixel, in (0, 0);
                n = N[0][0];

                crcc = ((pixels[p] >> 24) & 0xff)/255.;
                crnc = ((pixels[p+1] >> 24) & 0xff)/255.;
                nrcc = ((pixels[p + scanStride] >> 24) & 0xff)/255.;
                nrnc = ((pixels[p + scanStride + 1] >> 24) & 0xff)/255.;
        
                n[0] = - surfaceScaleX * 2./3. *((2*crnc + nrnc - 2*crcc - nrcc));
                n[1] = - surfaceScaleY * 2./3. *((2*nrcc + nrnc - 2*crcc - crnc));
                norm = Math.sqrt(n[0]*n[0] + n[1]*n[1] + 1);
                n[0] /= norm;
                n[1] /= norm;
                n[2] = 1/norm;
                n[3] = crcc*surfaceScale;

                // Top row...
                p++;
                for(j=1; j<w-1; j++){
                    crpc = crcc;
                    nrpc = nrcc;
                    crcc = crnc;
                    nrcc = nrnc;
            
                    crnc = ((pixels[p+1] >> 24) & 0xff)/255.;
                    nrnc = ((pixels[p + scanStride + 1] >> 24) & 0xff)/255.;
            
                    n = N[0][j];
                    n[0] = - surfaceScaleX * 1/3. * (( 2*crnc + nrnc)
                                                     - (2*crpc + nrpc));
                    n[1] = - surfaceScaleY * 1/2. *(( nrpc + 2*nrcc + nrnc)
                                                    - (crpc + 2*crcc + crnc));
            
                    norm = Math.sqrt(n[0]*n[0] + n[1]*n[1] + 1);
                    n[0] /= norm;
                    n[1] /= norm;
                    n[2] = 1/norm;
                    n[3] = crcc*surfaceScale;
                    p++;
            
                }
        
                // Last pixel of top row
                if(w > 1){
                    n = N[0][j];
                    crpc = crcc;
                    nrpc = nrcc;
                    crcc = crnc;
                    nrcc = nrnc;
                    n[0] = - surfaceScaleX * 2./3. *(( 2*crcc + nrcc)
                                                     - (2*crpc + nrpc));
                    n[1] = - surfaceScaleY * 2./3. *(( 2*nrcc + nrpc)
                                                     - (2*crcc + crpc));
            
                    norm = Math.sqrt(n[0]*n[0] + n[1]*n[1] + 1);
                    n[0] /= norm;
                    n[1] /= norm;
                    n[2] = 1/norm;
                    n[3] = crcc*surfaceScale;
                }

                // Now, process left column, from (0, 1) to (0, h-1)
                p = offset;
                crcc = ((pixels[p] >> 24) & 0xff)/255.;
                crnc = ((pixels[p+1] >> 24) & 0xff)/255.;
                nrcc = ((pixels[p + scanStride] >> 24) & 0xff)/255.;
                nrnc = ((pixels[p + scanStride + 1] >> 24) & 0xff)/255.;
                p += scanStride;

                for(i=1; i<h-1; i++){
                    prcc = crcc;
                    crcc = nrcc;
                    prnc = crnc;
                    crnc = nrnc;
            
                    nrcc = ((pixels[p + scanStride] >> 24) & 0xff)/255.;
                    nrnc = ((pixels[p + scanStride + 1] >> 24) & 0xff)/255.;
            
                    n = N[i][0];
                    n[0] = - surfaceScaleX * 1/2 *(( prnc + 2*crnc + nrnc)
                                                   - (prcc + 2*crcc + nrcc));
                    n[1] = - surfaceScaleY * 1/3 *(( 2*prcc + prnc)
                                                   - ( 2*crcc + crnc));
            
                    norm = Math.sqrt(n[0]*n[0] + n[1]*n[1] + 1);
                    n[0] /= norm;
                    n[1] /= norm;
                    n[2] = 1/norm;
                    n[3] = crcc*surfaceScale;
                    p += scanStride;
            
                }

                // Now, proces right column, from (w-1, 1) to (w-1, h-1)
                p = offset + scanStride -1;
                crcc = ((pixels[p] >> 24) & 0xff)/255.;
                crpc = ((pixels[p-1] >> 24) & 0xff)/255.;
                nrcc = ((pixels[p + scanStride] >> 24) & 0xff)/255.;
                nrpc = ((pixels[p + scanStride - 1] >> 24) & 0xff)/255.;
                p += scanStride;
        
                for(i=1; i<h-1; i++){
                    prcc = crcc;
                    prpc = crpc;
                    crcc = nrcc;
                    crpc = nrpc;
            
                    nrcc = ((pixels[p + scanStride] >> 24) & 0xff)/255.;
                    nrpc = ((pixels[p + scanStride - 1] >> 24) & 0xff)/255.;
            
                    n = N[i][w-1];
                    n[0] = - surfaceScaleX * 1/2.*(( prcc + 2*crcc + nrcc)
                                                   - (prpc + 2*crpc + nrpc));
                    n[1] = - surfaceScaleY * 1/3.*(( nrpc + 2*nrcc)
                                                   - ( prpc + 2*prcc));
            
                    norm = Math.sqrt(n[0]*n[0] + n[1]*n[1] + 1);
                    n[0] /= norm;
                    n[1] /= norm;
                    n[2] = 1/norm;
                    n[3] = crcc*surfaceScale;
                    p += scanStride;
                }

                // Process first pixel of last row
                p = offset + (h-1)*scanStride;
                crcc = ((pixels[p] >> 24) & 0xff)/255.;
                crnc = ((pixels[p+1] >> 24) & 0xff)/255.;
                prcc = ((pixels[p - scanStride] >> 24) & 0xff)/255.;
                prnc = ((pixels[p - scanStride + 1] >> 24) & 0xff)/255.;
        
                n = N[h-1][0];

                n[0] = - surfaceScaleX * 2.*3. * ((2*crnc + prnc - 2*crcc - prcc));
                n[1] = - surfaceScaleY * 2.*3. * ((2*crcc + crnc - 2*prcc - prnc));
                norm = Math.sqrt(n[0]*n[0] + n[1]*n[1] + 1);
                n[0] /= norm;
                n[1] /= norm;
                n[2] = 1/norm;
                n[3] = crcc*surfaceScale;
        
                // Bottom row...
                p++;
                for(j=1; j<w-1; j++){
                    crpc = crcc;
                    prpc = prcc;
                    crcc = crnc;
                    prcc = prnc;
            
                    crnc = ((pixels[p+1] >> 24) & 0xff)/255.;
                    prnc = ((pixels[p - scanStride + 1] >> 24) & 0xff)/255.;
            
                    n = N[h-1][j];
                    n[0] = - surfaceScaleX * 1/3.*(( 2*crnc + prnc)
                                                   - (2*crpc + prpc));
                    n[1] = - surfaceScaleY * 1/2.*(( crpc + 2*crcc + crnc)
                                                   - (prpc + 2*prcc + prnc));
            
                    norm = Math.sqrt(n[0]*n[0] + n[1]*n[1] + 1);
                    n[0] /= norm;
                    n[1] /= norm;
                    n[2] = 1/norm;
                    n[3] = crcc*surfaceScale;
                    p++;
                }

                // Bottom right corner
                crpc = crcc;
                prpc = prcc;
                crcc = crnc;
                prcc = prnc;
            
                n = N[h-1][w-1];
                n[0] = - surfaceScaleX * 2./3. *(( 2*crcc + prcc)
                                                 - (2*crpc + prpc));
                n[1] = - surfaceScaleY * 2./3. *(( 2*crcc + crpc)
                                                 - (2*prcc + prpc));
            
                norm = Math.sqrt(n[0]*n[0] + n[1]*n[1] + 1);
                n[0] /= norm;
                n[1] /= norm;
                n[2] = 1/norm;
                n[3] = crcc*surfaceScale;
            }
        }

        return N;
    }
    
    /*
     * @return true if the normal is constant over the surface
     */
    public boolean isConstant(Rectangle area){
        return false;
    }
}

