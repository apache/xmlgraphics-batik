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
import org.apache.batik.gvt.filter.PadMode;

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
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

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
        if (aoi == null) aoi = getBounds2D();

        // AOI bounds in device space...
        Rectangle r = at.createTransformedShape(aoi).getBounds();

        BufferedImage bi = new BufferedImage(r.width, r.height,
                                             BufferedImage.TYPE_INT_ARGB_PRE);

        Graphics2D g2d = bi.createGraphics();

        // Make sure we draw with what hints we have.
        g2d.setRenderingHints(rh);

        Iterator i = srcs.iterator();
        boolean first = true;
        while (i.hasNext()) {
            // Get the source to work with...
            Filter cr = (Filter)i.next();

            // Get our sources image...
            RenderedImage ri = cr.createRendering(rc);
            // No output image keep going...
            if (ri == null)
                continue;

            if ((ri.getMinX()   != r.x)     || (ri.getMinY()   != r.y) ||
                (ri.getWidth()  != r.width) || (ri.getHeight() != r.height)) {
                ri = new PadRed(ConcreteRenderedImageCachableRed.wrap(ri), 
                                r, PadMode.ZERO_PAD, rh);
            }
              // Draw RenderedImage has problems....
              // This works around them...
            BufferedImage bri;
            WritableRaster wr = (WritableRaster)ri.getData();
            ColorModel cm = ri.getColorModel();
            bri = new BufferedImage(cm, wr.createWritableTranslatedChild(0,0),
                                    cm.isAlphaPremultiplied(), null);

            if (false) {
                System.out.println("Ri: " + ri + " Loc: (" +
                                   ri.getMinX() + ", " +
                                   ri.getMinY() + ", " +
                                   ri.getWidth() + ", " +
                                   ri.getHeight() + ")");
            }

            g2d.drawImage(bri, null, ri.getMinX()-r.x, ri.getMinY()-r.y);
            if (first) {
                  // After the first image we set the composite rule.
                g2d.setComposite(new SVGComposite(rule));
                first = false;
            }
                        

        }

        return new ConcreteBufferedImageCachableRed(bi, r.x, r.y);
    }
}
