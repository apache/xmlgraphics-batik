/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.ext.awt.image.renderable.PadMode;
import org.apache.batik.ext.awt.image.GraphicsUtil;

import java.awt.Point;
import java.awt.Rectangle;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;

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

    TileGrid tiles;

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
        // my input data..
        Rectangle myBounds;
        myBounds = src2me.createTransformedShape(src.getBounds()).getBounds();

        // If the output buffer is not premultiplied in certain cases it
        // fails to properly divide out the Alpha (it always does
        // the affine on premultiplied data), hence you get ugly
        // back aliasing effects...
        ColorModel cm = src.getColorModel();
        cm = GraphicsUtil.coerceColorModel(cm, true);

        // fix my sample model so it makes sense given my size.
        SampleModel sm = fixSampleModel(src, myBounds);

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

        // if (srcR.intersects(src.getBounds()) == false)
        //     return;
        // 
        // srcR = srcR.intersection(src.getBounds());
        
        Raster srcRas = src.getData(srcR);

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
        GraphicsUtil.coerceData(srcWR, srcCM, true);
        srcBI = new BufferedImage(myCM,
                                  srcWR.createWritableTranslatedChild(0,0),
                                  myCM.isAlphaPremultiplied(), null);

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

        /**
         * This function 'fixes' the source's sample model.
         * right now it just ensures that the sample model isn't
         * much larger than my width.
         */
    protected static SampleModel fixSampleModel(CachableRed src,
                                                Rectangle   bounds) {
        SampleModel sm = src.getSampleModel();
        int w = sm.getWidth();
        if (w < 256) w = 256;
        if (w > bounds.width)  w = bounds.width;
        int h = sm.getHeight();
        if (h < 256) h = 256;
        if (h > bounds.height) h = bounds.height;
        return sm.createCompatibleSampleModel(w, h);
    }
}
