/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.CachableRed;

import java.awt.Point;
import java.awt.Rectangle;

import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;


/**
 * This is an implementation of a Pad operation as a RenderedImage.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class PadRed extends AbstractRed {

    static final boolean DEBUG=false;

    RenderingHints hints;

    /**
     * Construct A Rendered Pad operation.  If the pad is smaller than
     * the original image size then this devolves to a Crop.
     *
     * @param src     The image to pad/crop
     * @param bounds  The bounds of the result (same coord system as src).
     * @param padMode The pad mode to use (currently ignored).
     * @param hints The hints to use for drawing 'pad' area.
     */
    public PadRed(CachableRed    src,
                  Rectangle      bounds,
                  PadMode        padMode,
                  RenderingHints hints) {
        super(src,bounds,src.getColorModel(),
              fixSampleModel(src, bounds),
              bounds.x, bounds.y,
              null);

        if (DEBUG) {
            System.out.println("Src: " + src + " Bounds: " + bounds + 
                               " Off: " +
                               src.getTileGridXOffset() + ", " +
                               src.getTileGridYOffset());
        }
        this.hints = hints;
    }

    public WritableRaster copyData(WritableRaster wr) {
        // Get my source.
        CachableRed src = (CachableRed)getSources().get(0);

        Rectangle srcR = src.getBounds();
        Rectangle wrR  = wr.getBounds();

        Rectangle r = wrR.intersection(srcR);

        if(!r.isEmpty()){
            // Limit the raster I send to my source to his rect.
            WritableRaster srcWR;
            srcWR = wr.createWritableChild(r.x, r.y, r.width, r.height,
                                           r.x, r.y, null);
            src.copyData(srcWR);
        }

        // NOTE: I'm ignoring the pad mode here. I really need
        //       to check what the mode is and pad out the edges
        //       of wr.  For now I just zero them...
        BufferedImage bi;
        bi = new BufferedImage(getColorModel(), 
                               wr.createWritableTranslatedChild(0,0),
                               getColorModel().isAlphaPremultiplied(),
                               null);

        Graphics2D g2d = bi.createGraphics();
        // Make sure we draw with our hints.
        if (hints != null) g2d.setRenderingHints(hints);
        // Overwrite whatever is there.
        g2d.setComposite(AlphaComposite.Src);
        // Fully transparent black.
        g2d.setColor(new Color(0,0,0,0));
                           
        int x      = wrR.x;
        int y      = wrR.y;
        int width  = wrR.width;
        int height = wrR.height;

        // Position x, y at the topleft of the bufferedImage...
        g2d.translate(-x, -y);

        // We split the edge drawing up into four parts.
        //
        //  +-----------------------------+
        //  | 1    | 2                    |
        //  |      +---------------+------|
        //  /      /               /4     /
        //  /      /               /      /
        //  /      /               /      /
        //  /      /               /      /
        //  |      +---------------+------|
        //  |      |  3                   |
        //  +-----------------------------+
        //
        //  We update our x,y, width, height as we go along so
        //  we 'forget' about the parts we have already painted...


        // Draw #1
        if (DEBUG) {
            System.out.println("WrR: " + wrR + " srcR: " + srcR);
            g2d.setColor(new Color(255,0,0,128));
        }
        if (x < srcR.x) {
            int w = srcR.x-x;
            if (w > width) w=width;
            g2d.fillRect(x, y, w, height);
            x+=w;
            width-=w;
        }

        // Draw #2
        if (DEBUG) {
            System.out.println("WrR: [" + 
                               x + "," + y + "," + width + "," + height + 
                               "] s rcR: " + srcR);
            g2d.setColor(new Color(0,0,255,128));
        }
        if (y < srcR.y) {
            int h = srcR.y-y;
            if (h > height) h=height;
            g2d.fillRect(x, y, width, h);
            y+=h;
            height-=h;
        }

        // Draw #3
        if (DEBUG) {
            System.out.println("WrR: [" + 
                               x + "," + y + "," + width + "," + height + 
                               "] srcR: " + srcR);
            g2d.setColor(new Color(0,255,0,128));
        }
        if (y+height > srcR.y+srcR.height) {
            int h = (y+height) - (srcR.y+srcR.height);
            if (h > height) h=height;

            int y0 = y+height-h; // the +/-1 cancel (?)

            g2d.fillRect(x, y0, width, h);
            height-=h;
        }

        // Draw #4
        if (DEBUG) {
            System.out.println("WrR: [" + 
                               x + "," + y + "," + width + "," + height + 
                               "] srcR: " + srcR);
            g2d.setColor(new Color(255,255,0,128));
        }
        if (x+width > srcR.x+srcR.width) {
            int w = (x+width) - (srcR.x+srcR.width);
            if (w > width) w=width;
            int x0 = x+width-w; // the +/-1 cancel (?)

            g2d.fillRect(x0, y, w, height);
            width-=w;
        }

        return wr;
    }

        /**
         * This function 'fixes' the source's sample model.
         * right now it just ensures that the sample model isn't
         * much larger than my width.
         */
    protected static SampleModel fixSampleModel(CachableRed src,
                                                Rectangle   bounds) {
        SampleModel sm = src.getSampleModel();
        int w = sm.getWidth();
        if (w < 256) w = 256;
        if      (w > bounds.width)  w = bounds.width;
        int h = sm.getHeight();
        if (h < 256) h = 256;
        if (h > bounds.height) h = bounds.height;

        return sm.createCompatibleSampleModel(w, h);
    }
}
