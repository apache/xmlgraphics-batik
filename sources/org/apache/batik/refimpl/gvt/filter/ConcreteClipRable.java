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
import org.apache.batik.gvt.filter.CachableRed;
import org.apache.batik.gvt.filter.Clip;
import org.apache.batik.gvt.filter.PadMode;

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;

import java.awt.image.renderable.RenderContext;

/**
 * ClipRable implementation
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class ConcreteClipRable
    extends    AbstractRable 
    implements Clip {

    /**
     * The node who's outline specifies our mask.
     */
    protected Shape clipPath;

    public ConcreteClipRable(Filter src, Shape clipPath) {
        super(src, null);
        setClipPath(clipPath);
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
     * Set the clip path to use.
     * The path will be filled with opaque white.
     * @param clipPath The clip path to use
     */
    public void setClipPath(Shape clipPath) {
        touch();
        this.clipPath = clipPath;
    }

      /**
       * Returns the Shape that the cliprable will use to
       * define the clip path.
       * @return The shape that defines the clip path.
       */
    public Shape getClipPath() {
        return clipPath;
    }

    /**
     * Pass-through: returns the source's bounds
     */
    public Rectangle2D getBounds2D(){
        return getSource().getBounds2D();
    }

    public RenderedImage createRendering(RenderContext rc) {

        AffineTransform usr2dev = rc.getTransform();

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null)  rh = new RenderingHints(null);

        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) aoi = getBounds2D();

        Rectangle2D rect = getBounds2D();
        Rectangle2D.intersect(rect, clipPath.getBounds2D(), rect);
        Rectangle2D.intersect(rect, aoi.getBounds2D(), rect);

        Rectangle devR = usr2dev.createTransformedShape(rect).getBounds();

        if ((devR.width == 0) || (devR.height == 0))
            return null;
        
        BufferedImage bi = new BufferedImage(devR.width, devR.height,
                                             BufferedImage.TYPE_BYTE_GRAY);

        Shape devShape = usr2dev.createTransformedShape(getClipPath());
        Rectangle devAOIR;
        devAOIR = usr2dev.createTransformedShape(aoi).getBounds();

        Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHints(rh);
        if (false) {
            java.util.Set s = rh.keySet();
            java.util.Iterator i = s.iterator();
            while (i.hasNext()) {
                Object o = i.next();
                System.out.println("XXX: " + o + " -> " + rh.get(o));
            }
        }
        g2d.translate(-devR.x, -devR.y);
        g2d.setPaint(Color.white);
        g2d.fill(devShape);
        g2d.dispose();

        RenderedImage ri;
        ri = getSource().createRendering(new RenderContext(usr2dev, rect, rh));

        CachableRed cr, clipCr;
        cr = ConcreteRenderedImageCachableRed.wrap(ri);
        clipCr = new ConcreteBufferedImageCachableRed(bi, devR.x, devR.y);
        CachableRed ret = new MultiplyAlphaRed(cr, clipCr);

          // Pad back out to the proper size...
        ret = new PadRed(ret, devAOIR, PadMode.ZERO_PAD, rh);

        return ret;
    }
}
