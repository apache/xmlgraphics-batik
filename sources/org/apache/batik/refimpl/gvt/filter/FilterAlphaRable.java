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

import java.awt.RenderingHints;
import java.awt.Shape;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.RenderedImage;

import java.awt.image.renderable.RenderContext;

/**
 * FilterAlphaRable implementation.
 * 
 * This will take any source Filter and convert it to an alpha channel
 * image according to the SVG SourceAlpha Filter description.
 * This sets RGB to black and Alpha to the source image's alpha channel.
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class FilterAlphaRable
    extends    AbstractRable {

    public FilterAlphaRable(Filter src) {
        super(src, null);
    }

    public Filter getSource() {
        return (Filter)getSources().get(0);
    }

    /**
     * Pass-through: returns the source's bounds
     */
    public Rectangle2D getBounds2D(){
        return getSource().getBounds2D();
    }

    public RenderedImage createRendering(RenderContext rc) {
        // Source gets my usr2dev transform
        AffineTransform at = rc.getTransform();

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        // if we didn't have an aoi specify our bounds as the aoi.
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null)
            aoi = getBounds2D();

        // We only want it's alpha channel...
        rh.put(FilterAsAlphaRable.KEY_COLORSPACE, 
               FilterAsAlphaRable.VALUE_COLORSPACE_ALPHA);

        RenderedImage ri;
        ri = getSource().createRendering(new RenderContext(at, aoi, rh));
        
        CachableRed cr = ConcreteRenderedImageCachableRed.wrap(ri);

        Object val = cr.getProperty(FilterAsAlphaRable.PROPERTY_COLORSPACE);
        if (val == FilterAsAlphaRable.VALUE_COLORSPACE_ALPHA) 
            return cr; // It listened to us...

        return new FilterAlphaRed(cr);
    }
}
