/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;

import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.WritableRaster;

import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.ConvolveMatrixRable;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.CachableRed;

/**
 * Convolves an image with a convolution matrix.
 *
 * Known limitations:
 *   Does not support bias other than zero - pending 16bit pathway
 *   Does not support edgeMode="wrap" - pending Tile code.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class ConcreteConvolveMatrixRable 
    extends    AbstractRable 
    implements ConvolveMatrixRable
{

    Kernel kernel;
    Point  target;
    float bias;
    PadMode edgeMode;
    float [] kernelUnitLength = new float[2];

    boolean preserveAlpha = false;;

    public ConcreteConvolveMatrixRable(Filter source) {
        super(source);
    }

    public Filter getSource() {
        return (Filter)getSources().get(0);
    }

    public void setSource(Filter src) {
        init(src);
    }


    /**
     * Returns the Convolution Kernel in use
     */
    public Kernel getKernel() {
        return kernel;
    }

    /**
     * Sets the Convolution Kernel to use.
     * @param k Kernel to use for convolution.
     */
    public void setKernel(Kernel k) {
        touch();
        this.kernel = k;
    }

    public Point getTarget() {
        return (Point)target.clone();
    }

    public void setTarget(Point pt) {
        this.target = (Point)pt.clone();
    }

    /**
     * Returns the shift value to apply to the result of convolution
     */
    public double getBias() {
        return bias;
    }
    
    /**
     * Returns the shift value to apply to the result of convolution
     */
    public void setBias(double bias) {
        touch();
        this.bias = (float)bias;
    }

    /**
     * Returns the current edge handling mode.
     */
    public PadMode getEdgeMode() {
        return edgeMode;
    }
    
    /**
     * Sets the current edge handling mode.
     */
    public void setEdgeMode(PadMode edgeMode) {
        touch();
        this.edgeMode = edgeMode;
    }

    /**
     * Returns the [x,y] distance in user space between kernel values
     */
    public double [] getKernelUnitLength() {
        if (kernelUnitLength == null) 
            return null;

        double [] ret = new double[2];
        ret[0] = kernelUnitLength[0];
        ret[1] = kernelUnitLength[1];
        return ret;
    }

    /**
     * Sets the [x,y] distance in user space between kernel values
     * If set to zero then device space will be used.
     */
    public void setKernelUnitLength(double [] kernelUnitLength) {
        touch();
        if (kernelUnitLength == null) {
            this.kernelUnitLength = null;
            return;
        }

        if (this.kernelUnitLength == null)
            this.kernelUnitLength = new float[2];
            
        this.kernelUnitLength[0] = (float)kernelUnitLength[0];
        this.kernelUnitLength[1] = (float)kernelUnitLength[1];
    }

    /**
     * Returns false if the convolution should affect the Alpha channel
     */
    public boolean getPreserveAlpha() {
        return preserveAlpha;
    }

    /**
     * Sets Alpha channel handling.
     * A value of False indicates that the convolution should apply to
     * the Alpha Channel
     */
    public void setPreserveAlpha(boolean preserveAlpha) {
        touch();
        this.preserveAlpha = preserveAlpha;
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

        // The Scale is the "hypotonose" of the matrix vectors.  This
        // represents the complete scaling value from user to an
        // intermediate space that is scaled similarly to device
        // space.
        double scaleX = Math.sqrt(sx*sx + shy*shy);
        double scaleY = Math.sqrt(sy*sy + shx*shx);

        // These values represent the scale factor to the intermediate
        // coordinate system where we will apply our convolution.
        if (kernelUnitLength != null) {
            if (kernelUnitLength[0] > 0.0) 
                scaleX = 1/kernelUnitLength[0];
        
            if (kernelUnitLength[1] > 0.0) 
                scaleY = 1/kernelUnitLength[1];
        }
        
        Shape aoi = rc.getAreaOfInterest();
        if(aoi == null)
            aoi = getBounds2D();

        Rectangle2D r = aoi.getBounds2D();

        int kw = kernel.getWidth();
        int kh = kernel.getHeight();
        int kx = target.x;
        int ky = target.y;

        // Grow the region in usr space.
        { 
            double rx0 = r.getX() -(kx/scaleX);
            double ry0 = r.getY() -(ky/scaleY);
            double rx1 = rx0 + r.getWidth()  + (kw-1)/scaleX;
            double ry1 = ry0 + r.getHeight() + (kh-1)/scaleY;
            r = new Rectangle2D.Double(Math.floor(rx0),
                                       Math.floor(ry0),
                                       Math.ceil (rx1-Math.floor(rx0)),
                                       Math.ceil (ry1-Math.floor(ry0)));
        }
        // This will be the affine transform between our usr space and
        // an intermediate space which is scaled according to
        // kernelUnitLength and is axially aligned with our user
        // space.
        AffineTransform srcAt 
            = AffineTransform.getScaleInstance(scaleX, scaleY);

        // Here we update the translate to account for the phase shift
        // (if any) introduced by setting targetX, targetY in SVG.
        tx += target.x - kernel.getXOrigin();
        ty += target.y - kernel.getYOrigin();

        // This is the affine transform between our intermediate
        // coordinate space (where the convolution takes place) and
        // the real device space, or null (if we don't need an
        // intermediate space).

        // The shear/rotation simply divides out the
        // common scale factor in the matrix.
        AffineTransform resAt = new AffineTransform(sx/scaleX, shy/scaleX,
                                                    shx/scaleY, sy/scaleY,
                                                    tx, ty);

        RenderedImage ri;
        ri = getSource().createRendering(new RenderContext(srcAt, r, rh));
        if (ri == null)
            return null;

        CachableRed cr;
        cr = ConcreteRenderedImageCachableRed.wrap(ri);

        Shape devShape = srcAt.createTransformedShape(aoi);
        r = devShape.getBounds2D();
        r = new Rectangle2D.Double(Math.floor(r.getX()-kx), 
                                   Math.floor(r.getY()-ky),
                                   Math.ceil (r.getX()+r.getWidth())-
                                   Math.floor(r.getX())+(kw-1), 
                                   Math.ceil (r.getY()+r.getHeight())-
                                   Math.floor(r.getY())+(kh-1));

        if (!r.getBounds().equals(cr.getBounds())) {
            if (edgeMode == PadMode.WRAP)
                throw new IllegalArgumentException
                    ("edgeMode=\"wrap\" is not supported by ConvolveMatrix.");
            cr = new PadRed(cr, r.getBounds(), edgeMode, rh);
        }

        if (bias != 0.0)
            throw new IllegalArgumentException
                ("Only bias equals zero is supported in ConvolveMatrix.");
        
        BufferedImageOp op = new ConvolveOp(kernel, 
                                            ConvolveOp.EDGE_NO_OP,
                                            rh);

        ColorModel cm = cr.getColorModel();

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

        cr = new ConcreteBufferedImageCachableRed(destBI,
                                                  (int)r.getX(),
                                                  (int)r.getY());
        cr = new PadRed(cr, r.getBounds(), PadMode.ZERO_PAD, rh);
        if (!resAt.isIdentity())
            cr = new AffineRed(cr, resAt, null);

        return cr;
    }
    
}
