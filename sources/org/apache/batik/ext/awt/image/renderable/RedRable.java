/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;

/**
 * RasterRable This is used to wrap a Rendered Image back into the
 * RenderableImage world.
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class RedRable
    extends    AbstractRable {
    CachableRed src;

    public RedRable(CachableRed src) {
        super((Filter)null);
        this.src = src;
    }

    public CachableRed getSource() {
        return src;
    }

    public Rectangle2D getBounds2D() {
        return getSource().getBounds();
    }

    public RenderedImage createDefaultRendering() {
        return getSource();
    }


    public RenderedImage createRendering(RenderContext rc) {
        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        Shape aoi = rc.getAreaOfInterest();
        Rectangle aoiR;
        if (aoi != null) 
            aoiR = aoi.getBounds();
        else
            aoiR = getBounds2D().getBounds();

        // get the current affine transform
        AffineTransform at = rc.getTransform();

        // For high quality output we should really apply a Gaussian
        // Blur when we are scaling the image down significantly this
        // helps to prevent aliasing in the result image.
        CachableRed cr = getSource();

        if (aoiR.intersects(cr.getBounds()) == false)
            return null;
        aoiR = aoiR.intersection(cr.getBounds());

        // Get the device bounds, we will crop the affine to those
        // bounds.
        Rectangle devAOI = at.createTransformedShape(aoiR).getBounds();

        return new AffineRed(cr, at, rh);
    }
}
