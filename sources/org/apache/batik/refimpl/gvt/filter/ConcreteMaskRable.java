/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.GraphicsNode;

import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.CachableRed;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.gvt.filter.PadMode;

import java.awt.Shape;

import java.awt.RenderingHints;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.RenderedImage;

import java.awt.image.renderable.RenderContext;

/**
 * MaskRable implementation
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class ConcreteMaskRable
    extends    AbstractRable
    implements Mask {

    /**
     * The node who's outline specifies our mask.
     */
    protected GraphicsNode mask;

    /**
     * Region to which the mask applies
     */
    protected FilterRegion filterRegion;

    public ConcreteMaskRable(Filter src, GraphicsNode mask, FilterRegion filterRegion) {
        super(src, null);
        setMaskNode(mask);
        setFilterRegion(filterRegion);
    }

    /**
     * The source to be masked by the mask node.
     * @param src The Image to be masked.
     */
    public void setSource(Filter src) {
        init(src, null);
    }

    /**
     * This returns the current image being masked by the mask node.
     * @returns The image to mask
     */
    public Filter getSource() {
        return (Filter)getSources().get(0);
    }

    /**
     * The region to which this mask applies
     */
    public FilterRegion getFilterRegion(){
        return filterRegion;
    }

    /**
     * Returns the filter region to which this mask applies
     */
    public void setFilterRegion(FilterRegion filterRegion){
        if(filterRegion == null){
            throw new IllegalArgumentException();
        }

        this.filterRegion = filterRegion;
    }

    /**
     * Set the masking image to that described by gn.
     * If gn is an rgba image then the alpha is premultiplied and then
     * the rgb is converted to alpha via the standard feColorMatrix
     * rgb to luminance conversion.
     * In the case of an rgb only image, just the rgb to luminance
     * conversion is performed.
     * @param gn The graphics node that defines the mask image.
     */
    public void setMaskNode(GraphicsNode mask) {
        touch();
        this.mask = mask;
    }

      /**
       * Returns the Graphics node that the mask operation will use to
       * define the masking image.
       * @return The graphics node that defines the mask image.
       */
    public GraphicsNode getMaskNode() {
        return mask;
    }

    /**
     * Pass-through: returns the source's bounds
     */
    public Rectangle2D getBounds2D(){
        return filterRegion.getRegion();
        // return getSource().getBounds2D();
    }

    public RenderedImage createRendering(RenderContext rc) {
        Filter maskSrc = new ConcreteGraphicsNodeRable(getMaskNode());
        maskSrc = new FilterAsAlphaRable(maskSrc);

        AffineTransform at = rc.getTransform();
        AffineTransform adjustTxf = new AffineTransform();
        
        // Adjust the transform to fit the source region
        // into this mask's region. Add an additional 
        // transform so that maskSrc bounds match the desired
        // maskBounds, as defined by the filterRegion
        Rectangle2D maskSrcBounds = maskSrc.getBounds2D();
        Rectangle2D maskBounds = getBounds2D();

        if(maskSrcBounds.getWidth() == 0 
           ||
           maskSrcBounds.getHeight() == 0
           ||
           maskBounds.getWidth() == 0
           ||
           maskBounds.getHeight() == 0){
            return null;
        }

        adjustTxf.translate(maskBounds.getX(),
                            maskBounds.getY());

        adjustTxf.scale(maskBounds.getWidth()/maskSrcBounds.getWidth(),
                        maskBounds.getHeight()/maskSrcBounds.getHeight());

        adjustTxf.translate(- maskSrcBounds.getX(),
                            - maskSrcBounds.getY());

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        Shape aoi = rc.getAreaOfInterest();
        if(aoi == null)
            aoi = getBounds2D();

        RenderedImage ri;

        ri = getSource().createRendering(new RenderContext(at, aoi, rh));
        CachableRed cr = ConcreteRenderedImageCachableRed.wrap(ri);

        //
        // Compute mask transform and new area of interest for
        // mask.
        //
        Shape maskAoi = aoi;
        try{
            maskAoi = adjustTxf.createInverse().createTransformedShape(aoi);
        }catch(NoninvertibleTransformException e){
            // With the checks made above, this should never happen
            throw new Error();
        }

        AffineTransform maskTxf = new AffineTransform(at);
        maskTxf.concatenate(adjustTxf);
        ri = maskSrc.createRendering(new RenderContext(maskTxf, maskAoi, rh));
        CachableRed maskCr = ConcreteRenderedImageCachableRed.wrap(ri);

        CachableRed ret = new MultiplyAlphaRed(cr, maskCr);

        ret = new PadRed(ret, cr.getBounds(), PadMode.ZERO_PAD, rh);

        return ret;
    }
}
