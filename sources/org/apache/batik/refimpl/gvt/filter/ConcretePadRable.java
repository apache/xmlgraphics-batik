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
import org.apache.batik.gvt.filter.PadRable;
import org.apache.batik.gvt.filter.PadMode;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

/**
 * Concrete implementation of the PadRable interface.
 * This pads the image to a specified rectangle in user coord system.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class ConcretePadRable extends AbstractRable
    implements PadRable {

    PadMode           padMode;
    Rectangle2D       padRect;

    public ConcretePadRable(Filter src,
                            Rectangle2D padRect,
                            PadMode     padMode) {
        super.init(src, null);
        this.padRect = padRect;
        this.padMode = padMode;
    }

      /**
       * Returns the source to be affine.
       */
    public Filter getSource() {
        return (Filter)srcs.get(0);
    }

      /**
       * Sets the source to be affine.
       * @param src image to affine.
       */
    public void setSource(Filter src) {
        super.init(src, null);
    }

    public Rectangle2D getBounds2D() {
        return (Rectangle2D)padRect.clone();
    }

      /**
       * Set the current rectangle for padding.
       * @param rect the new rectangle to use for pad.
       */
    public void setPadRect(Rectangle2D rect) {
        this.padRect = rect;
    }

      /**
       * Get the current rectangle for padding
       * @returns Rectangle currently in use for pad.
       */
    public Rectangle2D getPadRect() {
        return (Rectangle2D)padRect.clone();
    }

      /**
       * Set the current extension mode for pad
       * @param mode the new pad mode
       */
    public void setPadMode(PadMode padMode) {
        touch();
        this.padMode = padMode;
    }

      /**
       * Get the current extension mode for pad
       * @returns Mode currently in use for pad
       */
    public PadMode getPadMode() {
        return padMode;
    }

    public RenderedImage createRendering(RenderContext rc) {
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        Filter src = getSource();
        Shape aoi = rc.getAreaOfInterest();

        if(aoi == null){
            aoi = getBounds2D();
        }

        // We only depend on our source for stuff that is inside
        // our bounds and his bounds (remember our bounds may be
        // tighter than his in one or both directions).
        Rectangle2D srect = src.getBounds2D();
        Rectangle2D rect  = getBounds2D();
        Rectangle2D arect = aoi.getBounds2D();
        Rectangle2D.intersect(arect, rect, arect);
        Rectangle2D.intersect(srect, arect, srect);

        RenderedImage result = null;
        if((srect.getWidth() > 0)
           &&
           (srect.getHeight() > 0)){
            AffineTransform at = rc.getTransform();
            
            RenderContext srcRC = new RenderContext(at, srect, rh);
            RenderedImage ri = src.createRendering(srcRC);
            
            if(ri != null){
                CachableRed cr = ConcreteRenderedImageCachableRed.wrap(ri);
                
                arect = at.createTransformedShape(arect).getBounds2D();
                
                System.out.println("Pad rect : " + arect);
                // Use arect (my bounds intersect area of interest)
                result = new PadRed(cr, arect.getBounds(), padMode, rh);
            }
        }

        System.out.println("ConcretePadRable done");
        return result;
    }

    public Shape getDependencyRegion(int srcIndex, Rectangle2D outputRgn) {
        if (srcIndex != 0)
            throw new IndexOutOfBoundsException("Affine only has one input");

        // We only depend on our source for stuff that is inside
        // our bounds and his bounds (remember our bounds may be
        // tighter than his in one or both directions).
        Rectangle2D srect = getSource().getBounds2D();
        Rectangle2D.intersect(srect, outputRgn, srect);
        Rectangle2D.intersect(srect, getBounds2D(), srect);
        return srect;
    }

    public Shape getDirtyRegion(int srcIndex, Rectangle2D inputRgn) {
        if (srcIndex != 0)
            throw new IndexOutOfBoundsException("Affine only has one input");

        inputRgn = (Rectangle2D)inputRgn.clone();
        // Changes in the input region don't propogate outside our
        // bounds.
        Rectangle2D.intersect(inputRgn, getBounds2D(), inputRgn);
        return inputRgn;
    }

}
