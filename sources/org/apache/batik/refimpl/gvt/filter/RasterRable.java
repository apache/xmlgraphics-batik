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
import org.apache.batik.gvt.filter.PadMode;
import java.net.URL;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Image;
import java.awt.Toolkit;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.renderable.RenderContext;

/**
 * RasterRable This is used to wrap a Rendered Image back into the 
 * RenderableImage world.
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class RasterRable
    extends    AbstractRable {

    CachableRed src;

    public RasterRable(CachableRed src) {
        super((Filter)null);
        this.src = src;
    }

    Thread thread = null;

    public RasterRable(final URL url) {
        super((Filter)null);

        thread = new ImageLoader(url);
        thread.start();
    }



    public synchronized CachableRed getSource() {
        if (thread != null) {
            synchronized (thread) {
                while (src == null) {
                    try {
                        thread.wait();
                    }
                    catch(InterruptedException ie) { }
                }
            }
            thread = null;
        }
        return src;
    }
    
    public Rectangle2D getBounds2D() {
        return getSource().getBounds();
    }

    public RenderedImage createRendering(RenderContext rc) {
        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        Shape aoi = rc.getAreaOfInterest();
        if(aoi == null) aoi = getBounds2D();

        // get the current affine transform
        AffineTransform at = rc.getTransform();

        // Get the device bounds, we will crop the affine to those bounds.
        Shape devAOI = at.createTransformedShape(aoi);

        CachableRed cr = new AffineRed(getSource(), at, rh);
        cr = new PadRed(cr, devAOI.getBounds(), PadMode.ZERO_PAD, rh);

        return cr;
    }

    public static Filter create(URL url, Rectangle2D bounds) {
        return new RasterRable(url);
    }

    static Component mediaComponent = new Label() { };
    static MediaTracker mediaTracker = new MediaTracker(mediaComponent);
    static Toolkit tk = Toolkit.getDefaultToolkit();
    static int id = 0;

    protected class ImageLoader extends Thread {

        protected URL           url;
        protected URLImageCache cache;

        public ImageLoader(URL url,
                           URLImageCache cache) { 
            this.url   = url; 
            this.cache = cache;
        }

        public ImageLoader(URL url) { 
            this.url   = url; 
            this.cache = URLImageCache.getDefaultCache();
        }

        public void run() {

            BufferedImage bi = cache.request(url);

            if (bi == null) {
                // TODO: We should probably manage a cache of
                // referenced images whenever possible.
                Image img = tk.createImage(url);
                int myID;
                synchronized (this) {
                    myID = id++;
                }

                mediaTracker.addImage(img, myID);
                while (true) {
                    try {
                        mediaTracker.waitForID(myID);
                    }
                    catch(InterruptedException ie) {
                        continue;
                    };
                    
                    break;
                }

                bi = new BufferedImage(img.getWidth(null), 
                                       img.getHeight(null),
                                       BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bi.createGraphics();
                g2d.drawImage(img, 0, 0, null);

                cache.put(url,bi);
            }

            synchronized (this) {
                src = ConcreteRenderedImageCachableRed.wrap(bi);
                this.notifyAll();
            }
        }
    }

}
