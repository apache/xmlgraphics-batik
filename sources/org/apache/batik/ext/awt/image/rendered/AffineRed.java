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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.image.GraphicsUtil;

/**
 * This is an implementation of an affine operation as a RenderedImage.
 * Right now the implementation makes use of the AffineBufferedImageOp
 * to do the work.  Eventually this may move to be more tiled in nature.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class AffineRed extends AbstractRed {

    RenderingHints  hints;
    AffineTransform src2me;
    AffineTransform me2src;

    public AffineTransform getTransform() {
        return (AffineTransform)src2me.clone();
    }

    public CachableRed getSource() {
        return (CachableRed)getSources().get(0);
    }

    public AffineRed(CachableRed     src,
                     AffineTransform src2me,
                     RenderingHints  hints) {
        super(); // We _must_ call init...

        this.src2me = src2me;
        this.hints  = hints;

        try {
            me2src = src2me.createInverse();
        } catch (NoninvertibleTransformException nite) {
            me2src = null;
        }

        // Calculate my bounds by applying the affine transform to
        // my input data..codec/
        Rectangle srcBounds = src.getBounds();
        // srcBounds.grow(-1,-1);
        Rectangle myBounds;
        myBounds = src2me.createTransformedShape(srcBounds).getBounds();

        // If the output buffer is not premultiplied in certain cases it
        // fails to properly divide out the Alpha (it always does
        // the affine on premultiplied data), hence you get ugly
        // back aliasing effects...
        ColorModel cm = fixColorModel(src);

        // fix my sample model so it makes sense given my size.
        SampleModel sm = fixSampleModel(src, cm, myBounds);

        Point2D pt = new Point2D.Float(src.getTileGridXOffset(),
                                       src.getTileGridYOffset());
        pt = src2me.transform(pt, null);
        
        // Finish initializing our base class...
        init(src, myBounds, cm, sm,
             (int)pt.getX(), (int)pt.getY(), null);
    }

    public WritableRaster copyData(WritableRaster wr) {

        // System.out.println("Affine CopyData:" + wr);

        // copyToRaster(wr);
        PadRed.ZeroRecter zr = PadRed.ZeroRecter.getZeroRecter(wr);
        zr.zeroRect(new Rectangle(wr.getMinX(), wr.getMinY(), 
                                  wr.getWidth(), wr.getHeight()));
        genRect(wr);
        return wr;
    }

    public Raster getTile(int x, int y) {
        if (me2src == null)
            return null;

        int tx = tileGridXOff+x*tileWidth;
        int ty = tileGridYOff+y*tileHeight;
        Point pt = new Point(tx, ty);
        WritableRaster wr = Raster.createWritableRaster(sm, pt);
        genRect(wr);
        
        return wr;
    }

    public void genRect(WritableRaster wr) {
        if (me2src == null)
            return;

        Rectangle srcR 
            = me2src.createTransformedShape(wr.getBounds()).getBounds();

        // System.out.println("Affine wrR: " + wr.getBounds());
        // System.out.println("Affine srcR: " + srcR);

        // Outset by two pixels so we get context for interpolation...
        srcR.setBounds(srcR.x-1, srcR.y-1, srcR.width+2, srcR.height+2);

        // Don't try and get data from src that it doesn't have...
        CachableRed src = (CachableRed)getSources().get(0);

        // Raster srcRas = src.getData(srcR);

        if (srcR.intersects(src.getBounds()) == false)
            return;
        Raster srcRas = src.getData(srcR.intersection(src.getBounds()));

        if (srcRas == null)
            return;

        // This works around the problem that the buffered ops
        // completely ignore the coords of the Rasters passed in.
        AffineTransform aff = (AffineTransform)src2me.clone();

        // Translate what is at 0,0 (which will be what our current
        // minX/Y is) to our current minX,minY.
        aff.concatenate(AffineTransform.getTranslateInstance
                        (srcRas.getMinX(), srcRas.getMinY()));

        Point2D srcPt = new Point2D.Float(wr.getMinX(), wr.getMinY());
        srcPt         = me2src.transform(srcPt, null);

        Point2D destPt = new Point2D.Double(srcPt.getX()-srcRas.getMinX(), 
                                            srcPt.getY()-srcRas.getMinY());

        destPt = aff.transform(destPt, null);


        // Translate what will be at minX,minY to zero, zero
        // which where java2d will think the real minX,minY is.
        aff.preConcatenate(AffineTransform.getTranslateInstance
                           (-destPt.getX(), -destPt.getY()));

        AffineTransformOp op = new AffineTransformOp(aff, hints);

        BufferedImage srcBI, myBI;
        ColorModel srcCM = src.getColorModel();
        ColorModel myCM = getColorModel();

        WritableRaster srcWR = (WritableRaster)srcRas;
        // If the output buffer is not premultiplied in certain cases
        // it fails to properly divide out the Alpha (it always does
        // the affine on premultiplied data). We help it out by
        // premultiplying for it.
        srcCM = GraphicsUtil.coerceData(srcWR, srcCM, true);
        srcBI = new BufferedImage(srcCM,
                                  srcWR.createWritableTranslatedChild(0,0),
                                  srcCM.isAlphaPremultiplied(), null);

        myBI = new BufferedImage(myCM,wr.createWritableTranslatedChild(0,0),
                                 myCM.isAlphaPremultiplied(), null);

        op.filter(srcBI, myBI);

        // if ((count % 40) == 0) {
        //     org.apache.batik.ImageDisplay.showImage("Src: " , srcBI);
        //     org.apache.batik.ImageDisplay.showImage("Dst: " , myBI);
        // }
        // count++;
    }

    // int count=0;

    protected static ColorModel fixColorModel(CachableRed src) {
        ColorModel  cm = src.getColorModel();

        if (cm.hasAlpha()) {
            if (!cm.isAlphaPremultiplied())
                cm = GraphicsUtil.coerceColorModel(cm, true);
            return cm;
        }

        ColorSpace cs = cm.getColorSpace();

        int b = src.getSampleModel().getNumBands()+1;
        if (b == 4) {
            int [] masks = new int[4];
            for (int i=0; i < b-1; i++) 
                masks[i] = 0xFF0000 >> (8*i);
            masks[3] = 0xFF << (8*(b-1));

            return new DirectColorModel(cs, 8*b, masks[0], masks[1], 
                                        masks[2], masks[3],
                                        true, DataBuffer.TYPE_INT);
        }

        int [] bits = new int[b];
        for (int i=0; i<b; i++)
            bits[i] = 8;
        return new ComponentColorModel(cs, bits, true, true, 
                                       Transparency.TRANSLUCENT,
                                       DataBuffer.TYPE_INT);
        
    }

    /**
         * This function 'fixes' the source's sample model.
         * right now it just ensures that the sample model isn't
         * much larger than my width.
         */
    protected SampleModel fixSampleModel(CachableRed src,
                                         ColorModel  cm,
                                         Rectangle   bounds) {
        SampleModel sm = src.getSampleModel();
        int defSz = AbstractTiledRed.getDefaultTileSize();

        int w = sm.getWidth();
        if (w < defSz) w = defSz;
        if (w > bounds.width)  w = bounds.width;
        int h = sm.getHeight();
        if (h < defSz) h = defSz;
        if (h > bounds.height) h = bounds.height;

        if ((w <= 0) || (h <= 0)) {
            w = 1;
            h = 1;
        }

        return cm.createCompatibleSampleModel(w, h);
    }
}
