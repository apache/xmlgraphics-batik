/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.ext.awt.image.GraphicsUtil;

import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.CachableRed;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.gvt.filter.PadRable;
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
    protected Rectangle2D filterRegion;

    public ConcreteMaskRable(Filter src, GraphicsNode mask, 
                             Rectangle2D filterRegion) {
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
    public Rectangle2D getFilterRegion(){
        return (Rectangle2D)filterRegion.clone();
    }

    /**
     * Returns the filter region to which this mask applies
     */
    public void setFilterRegion(Rectangle2D filterRegion){
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
        return (Rectangle2D)filterRegion.clone();
    }

    public RenderedImage createRendering(RenderContext rc) {

        GraphicsNodeRenderContext gnrc = 
               GraphicsNodeRenderContext.getGraphicsNodeRenderContext(rc);
        //
        // Get the mask content
        //
        Filter maskSrc = new ConcreteGraphicsNodeRable(getMaskNode(), gnrc);
        PadRable maskPad = new ConcretePadRable(maskSrc, getBounds2D(),
                                                PadMode.ZERO_PAD);
        maskSrc = new FilterAsAlphaRable(maskPad);
        RenderedImage ri = maskSrc.createRendering(rc);
        if (ri == null)
            return null;

        CachableRed maskCr = ConcreteRenderedImageCachableRed.wrap(ri);

        //
        // Get the masked content
        //
        PadRable maskedPad = new ConcretePadRable(getSource(),
                                                  getBounds2D(),
                                                  PadMode.ZERO_PAD);

        ri = maskedPad.createRendering(rc);
        if (ri == null)
            return null;

        CachableRed cr;
        cr = GraphicsUtil.wrap(ri);
        cr = GraphicsUtil.convertToLsRGB(cr);

        // org.apache.batik.test.gvt.ImageDisplay.showImage("Src: ", cr);
        // org.apache.batik.test.gvt.ImageDisplay.showImage("Mask: ", maskCr);

        CachableRed ret = new MultiplyAlphaRed(cr, maskCr);

        // org.apache.batik.test.gvt.ImageDisplay.showImage("Masked: ", ret);


        // ret = new PadRed(ret, cr.getBounds(), PadMode.ZERO_PAD, rh);

        return ret;
    }
}
