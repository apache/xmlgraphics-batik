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
import org.apache.batik.ext.awt.image.rendered.TranslateRed;
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

    public Object getProperty(String name) {
        return src.getProperty(name);
    }

    public String [] getPropertyNames() {
        return src.getPropertyNames();
    }

    public Rectangle2D getBounds2D() {
        return getSource().getBounds();
    }

    public RenderedImage createDefaultRendering() {
        return getSource();
    }


    public RenderedImage createRendering(RenderContext rc) {
        // System.out.println("RedRable Create Rendering: " + this);

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

        if (at.isIdentity()) {
            // System.out.println("Using as is");
            return cr;
        }

        if ((at.getScaleX() == 1.0) && (at.getScaleY() == 1.0) &&
            (at.getShearX() == 0.0) && (at.getShearY() == 0.0)) {
            int xloc = (int)(cr.getMinX()+at.getTranslateX());
            int yloc = (int)(cr.getMinY()+at.getTranslateY());
            double dx = xloc - (cr.getMinX()+at.getTranslateX());
            double dy = yloc - (cr.getMinY()+at.getTranslateY());
            if (((dx > -0.0001) && (dx < 0.0001)) &&
                ((dy > -0.0001) && (dy < 0.0001))) {
                // System.out.println("Using TranslateRed");
                return new TranslateRed(cr, xloc, yloc);
            }
        }

        // System.out.println("Using Full affine: " + at);
        return new AffineRed(cr, at, rh);
    }
}
