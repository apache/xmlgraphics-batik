/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.CachableRed;
import org.apache.batik.gvt.filter.CompositeRable;
import org.apache.batik.gvt.filter.CompositeRule;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.util.awt.image.GraphicsUtil;

import java.util.List;
import java.util.Iterator;

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import java.awt.image.renderable.RenderContext;

/**
 * Composites a list of images according to a single composite rule.
 * the image are applied in the order they are in the List given.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class ConcreteCompositeRable
    extends    AbstractRable
    implements CompositeRable {

    protected CompositeRule rule;

    public ConcreteCompositeRable(List srcs,
                                  CompositeRule rule) {
        super(srcs);

        this.rule = rule;
    }

      /**
       * The sources to be composited togeather.
       * @param srcs The list of images to be composited by the composite rule.
       */
    public void setSources(List srcs) {
        init(srcs, null);
    }

      /**
       * Set the composite rule to use for combining the sources.
       * @param cr Composite rule to use.
       */
    public void setCompositeRule(CompositeRule cr) {
        touch();
        this.rule = rule;
    }

      /**
       * Get the composite rule in use for combining the sources.
       * @returns Composite rule currently in use.
       */
    public CompositeRule getCompositeRule() {
        return this.rule;
    }


    protected RenderedImage createRenderingOver(RenderContext rc) {
        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        // update the current affine transform
        AffineTransform at = rc.getTransform();

        // Our bounds in device space...
        Rectangle r = at.createTransformedShape(getBounds2D()).getBounds();

        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) 
            aoi = getBounds2D();
        else {
            // Get AOI bounds in device space...
            Rectangle aoiR = at.createTransformedShape(aoi).getBounds();
            Rectangle2D.intersect(r, aoiR, r);
            aoi = getBounds2D().createIntersection(aoi.getBounds2D());
        }

        AffineTransform translate = 
            AffineTransform.getTranslateInstance(-r.x, -r.y);

        BufferedImage bi = GraphicsUtil.makeLinearBufferedImage
            (r.width, r.height, true);

        Graphics2D g2d = bi.createGraphics();

        // Make sure we draw with what hints we have.
        g2d.setRenderingHints(rh);
        g2d.setTransform(translate);
        g2d.transform(at);

        Iterator i = srcs.iterator();
        while (i.hasNext()) {
            GraphicsUtil.drawImage(g2d, (Filter)i.next());
        }

        return new ConcreteBufferedImageCachableRed(bi, r.x, r.y);
    }


    public RenderedImage createRendering(RenderContext rc) {
        if (srcs.size() == 0)
            return null;


        // This is an optimized version that does two things.
        // 1) It only renders the portion needed for display
        // 2) It uses GraphicsUtil.drawImage to draw the Renderable
        //    sources.  This is useful since it often allows it
        //    to bypass intermediate images...
        if (rule == CompositeRule.OVER)
            return createRenderingOver(rc);

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        // update the current affine transform
        AffineTransform at = rc.getTransform();

        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) aoi = getBounds2D();

        // AOI bounds in device space...
        Rectangle r = at.createTransformedShape(aoi).getBounds();

        if ((r.width <= 0) || (r.height <= 0))
            return null;

        // I originally had this be premultipled but someone was
        // multiplying the alpha each time a composite was done (I'm
        // guessing it was trying to unmultiply composite, remultiply,
        // but the unmultiply failed to do anything...).
        BufferedImage bi = GraphicsUtil.makeLinearBufferedImage
            (r.width, r.height, false);

        Graphics2D g2d = bi.createGraphics();
        g2d.translate(-r.x, -r.y);

        // Make sure we draw with what hints we have.
        g2d.setRenderingHints(rh);

        Iterator i = srcs.iterator();
        boolean first = true;
        while (i.hasNext()) {
            // Get the source to work with...
            Filter filt = (Filter)i.next();

            // Get our sources image...
            RenderedImage ri = filt.createRendering(rc);
            // No output image keep going...
            if (ri == null)
                continue;
            CachableRed cr = ConcreteRenderedImageCachableRed.wrap(ri);
            cr = GraphicsUtil.convertToLsRGB(cr);

            if ((ri.getMinX()   != r.x)     || (ri.getMinY()   != r.y) ||
                (ri.getWidth()  != r.width) || (ri.getHeight() != r.height)) {
                cr = new PadRed(cr, r, PadMode.ZERO_PAD, rh);
            }

            GraphicsUtil.drawImage(g2d, cr);

            if (first) {
                  // After the first image we set the composite rule.
                g2d.setComposite(new SVGComposite(rule));
                first = false;
            }
                        

        }

        return new ConcreteBufferedImageCachableRed(bi, r.x, r.y);
    }
}
