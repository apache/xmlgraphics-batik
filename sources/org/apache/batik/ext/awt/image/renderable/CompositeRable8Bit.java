/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.GraphicsUtil;

import java.util.List;
import java.util.Iterator;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;

/**
 * Composites a list of images according to a single composite rule.
 * the image are applied in the order they are in the List given.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class CompositeRable8Bit
    extends    AbstractRable
    implements CompositeRable {

    protected CompositeRule rule;
    protected ColorSpace    colorspace;
    protected boolean       csIsLinear;

    public CompositeRable8Bit(List srcs,
                              CompositeRule rule,
                              boolean csIsLinear) {
        super(srcs);

        this.rule = rule;
        this.csIsLinear = csIsLinear;
        if (csIsLinear)
            colorspace = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
        else
            colorspace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
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
       * @return Composite rule currently in use.
       */
    public CompositeRule getCompositeRule() {
        return this.rule;
    }

      /**
       * Set the colorspace to perform compositing in
       * @param cs ColorSpace to use.
       */
    public void setCompositeColorSpace(ColorSpace cs) {
        touch();
        if (cs == ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB))
            csIsLinear = true;
        else if (cs == ColorSpace.getInstance(ColorSpace.CS_sRGB))
            csIsLinear = false;
        else
            throw new IllegalArgumentException
                ("Unsupported ColorSpace for Composite: " + cs);
        this.colorspace = cs;
    }

      /**
       * Get the colorspace to that compositing will be performed in
       * @return ColorSpace for compositing.
       */
    public ColorSpace getCompositeColorSpace() {
        return this.colorspace;
    }


    protected RenderedImage createRenderingOver(RenderContext rc) {
        // System.out.println("Rendering Over: " + rule);

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        // update the current affine transform
        AffineTransform at = rc.getTransform();

        // Our bounds in device space...
        Rectangle r = at.createTransformedShape(getBounds2D()).getBounds();

        Shape aoi = rc.getAreaOfInterest();
        if (aoi != null) {
            // Get AOI bounds in device space...
            Rectangle aoiR = at.createTransformedShape(aoi).getBounds();
            if (r.intersects(aoiR) == false)
                return null;

            Rectangle2D.intersect(r, aoiR, r);
        }

        // This BufferedImage must be unpremultiplied or else when we
        // go to draw things get loopy.  If you want to try backing
        // this out check if the rendering problem persists by
        // bringing up samples/tests/feComposite.svg and rotating a
        // few degrees.  The zoom in/out and pan around a bit, it
        // ussually dies fairly quickly.
        BufferedImage bi;
        if (csIsLinear)
            bi = GraphicsUtil.makeLinearBufferedImage
            (r.width, r.height, false);
        else
            bi = new BufferedImage(r.width, r.height, 
                                   BufferedImage.TYPE_INT_ARGB);

        // Make sure we draw with what hints we have.
        Graphics2D g2d = GraphicsUtil.createGraphics(bi, rh);

        g2d.translate(-r.x, -r.y);
        g2d.clip(r);
        if (at != null)
            g2d.transform(at);

        Iterator i = srcs.iterator();
        while (i.hasNext()) {
            GraphicsUtil.drawImage(g2d, (Filter)i.next());
        }

        // System.out.println("Done Over: " + rule);
        return new BufferedImageCachableRed(bi, r.x, r.y);
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

        // System.out.println("Rendering General: " + rule);

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        // update the current affine transform
        AffineTransform at = rc.getTransform();

        Rectangle2D aoi = rc.getAreaOfInterest().getBounds2D();
        if (aoi == null) 
            aoi = getBounds2D();
        else {
            Rectangle2D bounds2d = getBounds2D();
            if (bounds2d.intersects(aoi) == false)
                return null;
                
            Rectangle2D.intersect(aoi, bounds2d, aoi);
        }

        // AOI bounds in device space.  Ideally we would limit r to
        // the actual region needed based on the current mode.  So for
        // IN we only need the intersection of all source.  For OUT we
        // only need the region under the last source (most of the
        // rest are r so I don't worry about it too much).
        Rectangle r = at.createTransformedShape(aoi).getBounds();

        BufferedImage bi;
        if (csIsLinear)
            bi = GraphicsUtil.makeLinearBufferedImage
            (r.width, r.height, true);
        else
            bi = new BufferedImage(r.width, r.height, 
                                   BufferedImage.TYPE_INT_ARGB_PRE);

        WritableRaster wr = bi.getRaster();

        Composite comp = new SVGComposite(rule);

        Graphics2D g2d = GraphicsUtil.createGraphics(bi, rh);
        // Make sure we draw with what hints we have.
        g2d.setComposite(comp);
        g2d.translate(-r.x, -r.y);
        g2d.clip(r);

        rc = new RenderContext(at, aoi, rh);
        
        Iterator i = srcs.iterator();
        boolean first = true;
        while (i.hasNext()) {
            // Get the source to work with...
            Filter filt = (Filter)i.next();

            // Get our sources image...
            RenderedImage ri = filt.createRendering(rc);

            if (ri == null)
                // Blank image...
                switch (rule.getRule()) {
                case CompositeRule.RULE_IN:
                    // For Mode IN One blank image kills all output
                    // (including any "future" images to be drawn).
                    return null;

                case CompositeRule.RULE_ARITHMETIC: {
                    BufferedImage blank;
                    if (csIsLinear)
                        blank = GraphicsUtil.makeLinearBufferedImage
                            (r.width, r.height, true);
                    else
                        blank = new BufferedImage
                            (r.width, r.height, 
                             BufferedImage.TYPE_INT_ARGB_PRE);
                    ri = new BufferedImageCachableRed(blank, r.x, r.y);
                }
                break;

                case CompositeRule.RULE_OUT:
                    {
                        // For mode OUT blank image clears output 
                        // up to this point.
                        g2d.setComposite(AlphaComposite.Clear);
                        g2d.setColor(new Color(0, 0, 0, 0));
                        g2d.fillRect(r.x, r.y, r.width, r.height);
                        g2d.setComposite(comp);
                    }
                    first = false;
                    continue;

                default:
                    // All other cases we simple pretend the image
                    // didn't exist (fully transparent image has no
                    // affect).
                    first = false;
                    continue;
            }
                

            CachableRed cr;
            cr = GraphicsUtil.wrap(ri);

            if (csIsLinear)
                cr = GraphicsUtil.convertToLsRGB(cr);
            else
                cr = GraphicsUtil.convertTosRGB(cr);

            if ((ri.getMinX()   != r.x)     || (ri.getMinY()   != r.y) ||
                (ri.getWidth()  != r.width) || (ri.getHeight() != r.height)) {
                cr = new PadRed(cr, r, PadMode.ZERO_PAD, rh);
            }

            if (first) {
                wr = wr.createWritableTranslatedChild(r.x, r.y);
                cr.copyData(wr);
                if (cr.getColorModel().isAlphaPremultiplied() == false) {
                    GraphicsUtil.coerceData(wr, cr.getColorModel(), true);
                }
                wr = bi.getRaster();
                first = false;
            } else {

                // If I allow any of the other modes to fall into
                // the draw case they fail miserably.  But OVER
                // is used alot and is significantly faster in the
                // draw case, and works as long as the source and dest
                // colorspace match (which the always will here).
                if (csIsLinear && (rule != CompositeRule.OVER)) {
                    // System.out.println("In manual");
                    Raster ras = cr.getData(r);
                    ras = ras.createTranslatedChild(0,0);
                    CompositeContext compCont;
                    compCont = comp.createContext(cr.getColorModel(),
                                                  bi.getColorModel(),
                                                  rh);
                    compCont.compose(ras, wr, wr);
                } else {
                    // System.out.println("In Draw");
                    GraphicsUtil.drawImage(g2d, cr);
                }
            }
                        

        }

        // System.out.println("Done General: " + rule);
        return new BufferedImageCachableRed(bi, r.x, r.y);
    }
}
