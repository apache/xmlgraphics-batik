/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.CompositeRable;
import org.apache.batik.gvt.filter.CompositeRule;

import java.util.List;
import java.util.Iterator;

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.AlphaComposite;

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

    public RenderedImage createRendering(RenderContext rc) {
        if (srcs.size() == 0)
            return null;

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        // update the current affine transform
        AffineTransform at = rc.getTransform();

        Shape aoi = rc.getAreaOfInterest();

        // AOI bounds in device space...
        Rectangle r = at.createTransformedShape(aoi).getBounds();

        BufferedImage bi = new BufferedImage(r.width, r.height,
                                             BufferedImage.TYPE_INT_ARGB_PRE);

        Graphics2D g2d = bi.createGraphics();

        // Make sure we draw with what hints we have.
        g2d.setRenderingHints(rh);

        // Setup the composite rule...
        switch (rule.getRule()) {
        case CompositeRule.RULE_OVER:
            g2d.setComposite(AlphaComposite.SrcOver);
            break;
        case CompositeRule.RULE_IN:
            g2d.setComposite(AlphaComposite.SrcIn);
            break;
        case CompositeRule.RULE_OUT:
            g2d.setComposite(AlphaComposite.SrcOut);
            break;

        case CompositeRule.RULE_ATOP:
            throw new UnsupportedOperationException
                ("This SVG viewer currently does not support Atop Composite");
        case CompositeRule.RULE_XOR:
            throw new UnsupportedOperationException
                ("This SVG viewer currently does not support XOR Composite");
        case CompositeRule.RULE_ARITHMATIC:
            throw new UnsupportedOperationException
                ("This SVG viewer currently does " +
                 "not support Arithmatic Composite");
        default:
            throw new UnsupportedOperationException
                ("Unknown composite rule requested.");
        }

        // Remember the default transform
        AffineTransform g2dAT = g2d.getTransform();

        Iterator i = srcs.iterator();
        while (i.hasNext()) {
            // Get the source to work with...
            Filter cr = (Filter)i.next();

            // Build the Render context for our source..
            Rectangle2D srcR = cr.getBounds2D();
            srcR = srcR.createIntersection(aoi.getBounds2D());

            // Doesn't intersect don't bug it..
            if (srcR.isEmpty())
                continue;
            RenderContext srcRC = new RenderContext(at, srcR, rh);

            // Get our sources image...
            RenderedImage ri = cr.createRendering(srcRC);

            // No output image keep going...
            if (ri == null)
                continue;

            // g2D always draws images at 0,0, so make 0,0 the
            // localtion of ri's upper left pixel
            // g2d.translate(ri.getMinX(), ri.getMinY());

            g2d.drawRenderedImage(ri,null);  // Draw the image

            g2d.setTransform(g2dAT); // Restore transform
        }

        return bi;
    }
}
