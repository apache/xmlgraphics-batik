/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.CachableRed;
import org.apache.batik.gvt.filter.GaussianBlurRable;
import org.apache.batik.gvt.filter.PadMode;

import java.awt.Shape;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Rectangle;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.renderable.RenderContext;

/**
 * GaussianBlurRable implementation
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public class ConcreteGaussianBlurRable
    extends    AbstractRable
    implements GaussianBlurRable {
    /**
     * Deviation along the x-axis
     */
    private double stdDeviationX;

    /**
     * Deviation along the y-axis
     */
    private double stdDeviationY;

    public ConcreteGaussianBlurRable(Filter src,
                                     double stdevX, double stdevY) {
        super(src, null);
        setStdDeviationX(stdevX);
        setStdDeviationY(stdevY);
    }

    /**
     * The deviation along the x axis, in user space.
     * @param stdDeviationX should be greater than zero.
     */
    public void setStdDeviationX(double stdDeviationX){
        if(stdDeviationX < 0){
            throw new IllegalArgumentException();
        }

        touch();
        this.stdDeviationX = stdDeviationX;
    }

    /**
     * The deviation along the y axis, in user space.
     * @param stdDeviationY should be greater than zero
     */
    public void setStdDeviationY(double stdDeviationY){
        if(stdDeviationY < 0){
            throw new IllegalArgumentException();
        }
        touch();
        this.stdDeviationY = stdDeviationY;
    }

    /**
     * Returns the deviation along the x-axis, in user space.
     */
    public double getStdDeviationX(){
        return stdDeviationX;
    }

    /**
     * Returns the deviation along the y-axis, in user space.
     */
    public double getStdDeviationY(){
        return stdDeviationY;
    }

    /**
     * Sets the source of the blur operation
     */
    public void setSource(Filter src){
        init(src, null);
    }

    /**
     * Pass-through: returns the source's bounds
     */
    public Rectangle2D getBounds2D(){
        return getSource().getBounds2D();
    }

    /**
     * Returns the source of the blur operation
     */
    public Filter getSource(){
        return (Filter)getSources().get(0);
    }

    public RenderedImage createRendering(RenderContext rc) {
        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        // update the current affine transform
        AffineTransform at = rc.getTransform();


        // This splits out the scale and applies it
        // prior to the Gaussian.  Then after appying the gaussian
        // it applies the shear (rotation) and translation components.
        double sx = at.getScaleX();
        double sy = at.getScaleY();

        double shx = at.getShearX();
        double shy = at.getShearY();

        double tx = at.getTranslateX();
        double ty = at.getTranslateY();

        // The Scale is the "hypotonose" of the matrix vectors.
        double scaleX = Math.sqrt(sx*sx + shx*shx);
        double scaleY = Math.sqrt(sy*sy + shy*shy);

        AffineTransform srcAt;
        srcAt = AffineTransform.getScaleInstance(scaleX, scaleY);

        double sdx = stdDeviationX*scaleX;
        double sdy = stdDeviationY*scaleY;

        GaussianBlurOp op = new GaussianBlurOp(sdx, sdy, rh);

        // This is the affine transform between our intermediate
        // coordinate space and the real device space.
        AffineTransform resAt;
        // The shear/rotation simply divides out the
        // common scale factor in the matrix.
        resAt = new AffineTransform( sx/scaleX, shy/scaleY,
                                    shx/scaleX,  sy/scaleY,
                                    tx, ty);
        
        Shape aoi = rc.getAreaOfInterest();
        if(aoi == null)
            aoi = getBounds2D();

        double blurRadX = op.getRadiusX();
        double blurRadY = op.getRadiusY();

        Rectangle2D r = aoi.getBounds2D();
        r = new Rectangle2D.Double(r.getX()-blurRadX, 
                                   r.getY()-blurRadY,
                                   r.getWidth() +2*blurRadX, 
                                   r.getHeight()+2*blurRadY);

        RenderedImage ri;
        ri = getSource().createRendering(new RenderContext(srcAt, r, rh));
        
        CachableRed cr;
        cr = new ConcreteRenderedImageCachableRed(ri);

        r = cr.getBounds();
        r = new Rectangle2D.Double(r.getX()-blurRadX, 
                                   r.getY()-blurRadY,
                                   r.getWidth() +2*blurRadX, 
                                   r.getHeight()+2*blurRadY);
        cr = new PadRed(cr, r.getBounds(), PadMode.ZERO_PAD, rh);
        
        // System.out.println("Src: " + cr.getBounds());

        ColorModel cm = ri.getColorModel();

        // OK this is a bit of a cheat. We Pull the DataBuffer out of
        // The read-only raster that getData gives us. And use it to
        // build a WritableRaster.  This avoids a copy of the data.
        Raster rr = cr.getData();
        Point  pt = new Point(0,0);
        WritableRaster wr = Raster.createWritableRaster(rr.getSampleModel(),
                                                        rr.getDataBuffer(),
                                                        pt);
        
        BufferedImage srcBI;
        srcBI = new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);
        
        BufferedImage destBI;
        destBI = op.filter(srcBI, null);

        final int rrMinX = cr.getMinX();
        final int rrMinY = cr.getMinY();

        cr = new ConcreteBufferedImageCachableRed(destBI) {
                public int getMinX(){
                    return rrMinX;
                }
                
                public int getMinY(){
                    return rrMinY;
                }
            };

        if (!resAt.isIdentity())
            cr = new AffineRed(cr, resAt, rh);
        
        // System.out.println("Res: " + cr.getBounds());

        return cr;
    }

    /**
     * Returns the region of input data is is required to generate
     * outputRgn.
     * @param srcIndex  The source to do the dependency calculation for.
     * @param outputRgn The region of output you are interested in
     *  generating dependencies for.  The is given in the user coordiate
     *  system for this node.
     * @return The region of input required.  This is in the user
     * coordinate system for the source indicated by srcIndex.
     */
    public Shape getDependencyRegion(int srcIndex, Rectangle2D outputRgn){
        // For GaussianBlur, the output is equal to the input. Therefore,
        // the dependency region is the intersection of the outputRgn
        // with the source.
        // NOTE: This needs to grow the region!!!
        //       Gausian actually needs a larget area of input than
        //       it outputs.
        Rectangle2D dependencyRegion = null;
        if(srcIndex == 0){
            // There is only one source in GaussianBlur
            // Intersect with output region
            dependencyRegion = outputRgn.createIntersection(getBounds2D());
        }

        return dependencyRegion;
    }

    /**
     * This calculates the region of output that is affected by a change
     * in a region of input.
     * @param srcIndex The input that inputRgn reflects changes in.
     * @param inputRgn the region of input that has changed, used to
     *  calculate the returned shape.  This is given in the user
     *  coordinate system of the source indicated by srcIndex.
     * @return The region of output that would be invalid given
     *  a change to inputRgn of the source selected by srcIndex.
     *  this is in the user coordinate system of this node.
     */
    public Shape getDirtyRegion(int srcIndex, Rectangle2D inputRgn){
        // For GaussianBlur, the output is equal to the input. Therefore
        // the dependency region is the intersection of the inputRgn
        // with the source.
        // NOTE: This needs to grow the region!!!
        //       Changes in the input region affect a larger area of
        //       output than the input.
        Rectangle2D dirtyRegion = null;
        if(srcIndex == 0){
            // There is only one source in GaussianBlur
            // Intersect with output region
            dirtyRegion = inputRgn.createIntersection(getBounds2D());
        }

        return dirtyRegion;
    }


}
