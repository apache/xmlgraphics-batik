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
 * FilterAsAlphaRable implementation.
 * 
 * This will take any source Filter and convert it to an alpha channel
 * according the the SVG Mask operation.
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class FilterAsAlphaRable
    extends    AbstractRable {

    /**
     * Notice to source that we prefer an Alpha RGB Image.
     */
    public static Object VALUE_COLORSPACE_ARGB  = new Object();

    /**
     * Notice to source that we will not use Alpha Channel but 
     * we still want RGB data.
     */
    public static Object VALUE_COLORSPACE_RGB   = new Object();

    /**
     * Notice to source that we only want Greyscale data (no Alpha).
     */
    public static Object VALUE_COLORSPACE_GREY  = new Object();

    /**
     * Notice to source that we only want Greyscale data with
     * an alpha channel.
     */
    public static Object VALUE_COLORSPACE_AGREY = new Object();

    /**
     * Notice to source that we only want an alpha channel.
     * The source should simply render alpha (no conversion)
     */
    public static Object VALUE_COLORSPACE_ALPHA = new Object();

    /**
     * Notice to source that we only want an alpha channel.
     * The source should follow the SVG spec for how to
     * convert ARGB, RGB, Grey and AGrey to just an Alpha channel.
     */
    public static Object VALUE_COLORSPACE_ALPHA_CONVERT = new Object();

    public static RenderingHints.Key KEY_COLORSPACE = 
        new RenderingHints.Key(9876) {
                public boolean isCompatibleValue(Object val) {
                    if (val == VALUE_COLORSPACE_ARGB)          return true;
                    if (val == VALUE_COLORSPACE_RGB)           return true;
                    if (val == VALUE_COLORSPACE_GREY)          return true;
                    if (val == VALUE_COLORSPACE_AGREY)         return true;
                    if (val == VALUE_COLORSPACE_ALPHA)         return true; 
                    if (val == VALUE_COLORSPACE_ALPHA_CONVERT) return true; 
                    return false;
                }
            };

    public static final String PROPERTY_COLORSPACE = 
        "org.apache.batik.gvt.refimpl.filter.Colorspace";

    public FilterAsAlphaRable(Filter src) {
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
        if (aoi == null) {
            aoi = getBounds2D();
        }

        rh.put(KEY_COLORSPACE, VALUE_COLORSPACE_ALPHA_CONVERT);

        RenderedImage ri;
        ri = getSource().createRendering(new RenderContext(at, aoi, rh));
        if (ri == null) 
            return null;

        CachableRed cr = ConcreteRenderedImageCachableRed.wrap(ri);

        Object val = cr.getProperty(PROPERTY_COLORSPACE);
        if (val == VALUE_COLORSPACE_ALPHA_CONVERT) 
            return cr;

        return new FilterAsAlphaRed(cr);
    }
}
