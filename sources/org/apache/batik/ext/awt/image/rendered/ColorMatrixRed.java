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

import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.image.GraphicsUtil;

/**
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ColorMatrixRed extends AbstractRed{
    /**
     * Matrix to apply to color components
     */
    private float[][] matrix;

    public float[][] getMatrix(){
        return copyMatrix(matrix);
    }

    public void setMatrix(float[][] matrix){
        float[][] tmp = copyMatrix(matrix);

        if(tmp == null){
            throw new IllegalArgumentException();
        }

        if(tmp.length != 4){
            throw new IllegalArgumentException();
        }

        for(int i=0; i<4; i++){
            if(tmp[i].length != 5){
                throw new IllegalArgumentException("" + i + " : " + tmp[i].length);
            }
        }
        this.matrix = matrix;
    }

    private float[][] copyMatrix(float[][] m){
        if(m == null){
            return null;
        }

        float[][] cm = new float[m.length][];
        for(int i=0; i<m.length; i++){
            if(m[i] != null){
                cm[i] = new float[m[i].length];
                System.arraycopy(m[i], 0, cm[i], 0, m[i].length);
            }
        }

        return cm;
    }

    public ColorMatrixRed(CachableRed src, float[][] matrix){
        setMatrix(matrix);

        ColorModel srcCM = src.getColorModel();
        ColorSpace srcCS = null;
        if (srcCM != null)
            srcCS = srcCM.getColorSpace();
        ColorModel cm;
        if (srcCS == null)
            cm = GraphicsUtil.Linear_sRGB_Unpre;
        else {
            if (srcCS == ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB))
                cm = GraphicsUtil.Linear_sRGB_Unpre;
            else
                cm = GraphicsUtil.sRGB_Unpre;
        }

        SampleModel sm =
            cm.createCompatibleSampleModel(src.getWidth(),
                                           src.getHeight());

        init(src, src.getBounds(), cm, sm,
             src.getTileGridXOffset(), src.getTileGridYOffset(), null);
    }


    public WritableRaster copyData(WritableRaster wr){
        //System.out.println("Getting data for : " + wr.getWidth() + "/" + wr.getHeight() + "/" + wr.getMinX() + "/" + wr.getMinY());

        //
        // First, get source data
        //
        CachableRed src = (CachableRed)getSources().elementAt(0);
        // System.out.println("Hello");
        // System.out.println("src class : " + src.getClass().getName());
        // System.out.println("this : " + this);
        wr = src.copyData(wr);
        // System.out.println("Hi");
        //System.out.println("Source was : " + wr.getWidth() + "/" + wr.getHeight()+ "/" + wr.getMinX() + "/" + wr.getMinY());

        // Unpremultiply data if required
        ColorModel cm = src.getColorModel();
        GraphicsUtil.coerceData(wr, cm, false);

        //
        // Now, process pixel values
        //
        final int minX = wr.getMinX();
        final int minY = wr.getMinY();
        final int w = wr.getWidth();
        final int h = wr.getHeight();
        DataBufferInt dbf = (DataBufferInt)wr.getDataBuffer();
        final int[] pixels = dbf.getBankData()[0];

        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();

        final int offset =
            (dbf.getOffset() +
             sppsm.getOffset(minX-wr.getSampleModelTranslateX(),
                             minY-wr.getSampleModelTranslateY()));

        // final int offset = dbf.getOffset();

        final int scanStride =
            ((SinglePixelPackedSampleModel)wr.getSampleModel())
            .getScanlineStride();
        final int adjust = scanStride - w;
        int p = offset;
        int r=0, g=0, b=0, a=0, dr=0, dg=0, db=0, da=0;
        int i=0, j=0;
        int pel = 0;

        final float a00=matrix[0][0]/255f, a01=matrix[0][1]/255f, a02=matrix[0][2]/255f, a03=matrix[0][3]/255f, a04=matrix[0][4]/255f;
        final float a10=matrix[1][0]/255f, a11=matrix[1][1]/255f, a12=matrix[1][2]/255f, a13=matrix[1][3]/255f, a14=matrix[1][4]/255f;
        final float a20=matrix[2][0]/255f, a21=matrix[2][1]/255f, a22=matrix[2][2]/255f, a23=matrix[2][3]/255f, a24=matrix[2][4]/255f;
        final float a30=matrix[3][0]/255f, a31=matrix[3][1]/255f, a32=matrix[3][2]/255f, a33=matrix[3][3]/255f, a34=matrix[3][4]/255f;

        for(i=0; i<h; i++){
            for(j=0; j<w; j++){
                pel = pixels[p];

                a = pel >>> 24;
                r = (pel >> 16) & 0xff;
                g = (pel >> 8 ) & 0xff;
                b =  pel        & 0xff;

                dr = (int)((a00*r + a01*g + a02*b + a03*a + a04)*255);
                dg = (int)((a10*r + a11*g + a12*b + a13*a + a14)*255);
                db = (int)((a20*r + a21*g + a22*b + a23*a + a24)*255);
                da = (int)((a30*r + a31*g + a32*b + a33*a + a34)*255);

                /*dr = dr > 255 ? 255 : dr < 0 ? 0 : dr;
                dg = dg > 255 ? 255 : dg < 0 ? 0 : dg;
                db = db > 255 ? 255 : db < 0 ? 0 : db;
                da = da > 255 ? 255 : da < 0 ? 0 : da;*/


                // If any high bits are set we are not in range.
                // If the highest bit is set then we are negative so
                // clamp to zero else we are > 255 so clamp to 255.
                if ((dr & 0xFFFFFF00) != 0)
                    dr = ((dr & 0x80000000) != 0)?0:255;
                if ((dg & 0xFFFFFF00) != 0)
                    dg = ((dg & 0x80000000) != 0)?0:255;
                if ((db & 0xFFFFFF00) != 0)
                    db = ((db & 0x80000000) != 0)?0:255;
                if ((da & 0xFFFFFF00) != 0)
                    da = ((da & 0x80000000) != 0)?0:255;

                pixels[p++] = (da << 24
                               |
                               dr << 16
                               |
                               dg << 8
                               |
                               db);

            }
            p += adjust;
        }

        //System.out.println("Result is : " + wr.getWidth() + "/" + wr.getHeight()+ "/" + wr.getMinX() + "/" + wr.getMinY());
        return wr;
    }

}
