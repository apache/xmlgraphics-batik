/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg.renderable;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import java.lang.ref.SoftReference;

import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.util.ParsedURL;

/**
 * RasterRable This is used to wrap a Rendered Image back into the
 * RenderableImage world.
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class MultiResRable
    extends    AbstractRable {
    SoftReference [] srcs;
    ParsedURL     [] srcURLs;
    Dimension     [] sizes;
    Rectangle2D      bounds;

    public MultiResRable(ParsedURL []srcURLs,
                         Dimension [] sizes) {
        super((Filter)null);
        this.srcURLs = new ParsedURL[srcURLs.length];
        this.sizes   = new Dimension[srcURLs.length];
        for (int i=0; i<srcURLs.length; i++) {
            this.srcURLs[i] = srcURLs[i];
            if (i < sizes.length) 
                this.sizes[i] = sizes[i];
            // System.out.println("Sz: " + this.sizes[i]);
            // System.out.println("URL: " + this.srcURLs[i]);
        }

        this.srcs = new SoftReference[srcURLs.length];
        bounds = new Rectangle2D.Float(0, 0, sizes[0].width, sizes[0].height);
    }

    public Rectangle2D getBounds2D() {
        return bounds;
    }

    public RenderedImage getImage(int idx, RenderContext rc) {
        // System.out.println("Getting: " + idx);
        Filter f = null;
        if (srcs[idx] != null) {
            Object o = srcs[idx].get();
            if (o != null) f= (Filter)o;
        }
        
        if (f == null) {
            // System.out.println("Reading: " + srcURLs[idx]);
            f = ImageTagRegistry.getRegistry().readURL(srcURLs[idx]);
            srcs[idx] = new SoftReference(f);
        }

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        double sx = bounds.getWidth() /(double)f.getWidth();
        double sy = bounds.getHeight()/(double)f.getHeight();
        
        // System.out.println("Scale: [" + sx + ", " + sy + "]");
        AffineTransform at = rc.getTransform();
        at.scale(sx, sy);

        // Map the area of interest to our input...
        Shape aoi = rc.getAreaOfInterest();
        if (aoi != null) {
            AffineTransform invAt = AffineTransform.getScaleInstance
                (1/sx, 1/sy);
            aoi = invAt.createTransformedShape(aoi);
        }

        return f.createRendering(new RenderContext(at, aoi, rh));
    }

    public RenderedImage createRendering(RenderContext rc) {
        // get the current affine transform
        AffineTransform at = rc.getTransform();

        double det = Math.sqrt(at.getDeterminant());

        if (det >= 1.0) return getImage(0, rc);
        
        double w = bounds.getWidth()*det;
        for (int i=1; i<sizes.length; i++) {
            if (w > sizes[i].width) 
                return getImage(i-1, rc);
        }

        return getImage(srcURLs.length-1, rc);
    }
}    

