/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.CachableRed;

import java.awt.Point;
import java.awt.Rectangle;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

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

        // fix my sample model so it makes sense given my size.
        SampleModel sm = fixSampleModel(src, myBounds);

        // Try to keep our tile grids aligned
        Point pt = new Point(src.getTileGridXOffset(),
                             src.getTileGridYOffset());
        Point2D pt2d = src2me.transform(pt, null);

        // Finish initializing our base class...
        init(src, myBounds, src.getColorModel(), sm,
             (int)(pt.getX()+0.5), (int)(pt.getY()+0.5), null);
    }

    public WritableRaster copyData(WritableRaster wr) {
        if (me2src == null)
            return wr;

        Rectangle srcR;
        srcR = me2src.createTransformedShape(wr.getBounds()).getBounds();

        WritableRaster srcWR = wr.createCompatibleWritableRaster(srcR);

        CachableRed src = (CachableRed)getSources().get(0);
        src.copyData(srcWR);

        // NOTE: This may be _WAY_ off. The idea is to
        // try to work around the problem that the buffered ops
        // completely ignore the coords of the Rasters passed in.
        AffineTransform aff = (AffineTransform)src2me.clone();

        Point2D tmpPt;

        tmpPt = new Point(srcWR.getMinX(), srcWR.getMinY());
        tmpPt = aff.transform(tmpPt, null);

        // Translate what is at 0,0 (which will be what our current
        // minX/Y is) to our current minX,minY.
        aff.concatenate(AffineTransform.getTranslateInstance
                        (srcWR.getMinX(), srcWR.getMinY()));

        tmpPt = new Point(0, 0);
        tmpPt = aff.transform(tmpPt, null);

        Point2D srcPt = me2src.transform(new Point(wr.getMinX(), wr.getMinY()), null);

        Point2D destPt = new Point2D.Double(srcPt.getX()-srcWR.getMinX(), 
                                            srcPt.getY()-srcWR.getMinY());

        destPt = aff.transform(destPt, null);


        // Translate what will be at minX,minY to zero, zero
        // which where java2d will think the real minX,minY is.
        aff.preConcatenate(AffineTransform.getTranslateInstance
                           (-destPt.getX(), -destPt.getY()));

        destPt = aff.transform(new Point(0,0), null);
        AffineTransform invAff=null;
        try {
            invAff = aff.createInverse();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        srcPt = invAff.transform(new Point(0,0), null);

        AffineTransformOp op = new AffineTransformOp(aff, hints);

        BufferedImage srcBI, myBI;
        ColorModel srcCM = src.getColorModel();
        srcBI = new BufferedImage(srcCM,srcWR.createWritableTranslatedChild(0,0),
                                  srcCM.isAlphaPremultiplied(), null);

        ColorModel myCM = getColorModel();
        myBI = new BufferedImage(myCM,wr.createWritableTranslatedChild(0,0),
                                 myCM.isAlphaPremultiplied(), null);

        op.filter(srcBI, myBI);

        return wr;
    }

        /**
         * This function 'fixes' the source's sample model.
         * right now it just ensures that the sample model isn't
         * much larger than my width.
         */
    protected static SampleModel fixSampleModel(CachableRed src,
                                                Rectangle   bounds) {
        SampleModel sm = src.getSampleModel();
        int w = sm.getWidth();
        if (w > bounds.width)  w = bounds.width;
        int h = sm.getHeight();
        if (h > bounds.height) h = bounds.height;
        return sm.createCompatibleSampleModel(w, h);
    }
}
