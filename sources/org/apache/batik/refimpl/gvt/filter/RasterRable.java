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
import org.apache.batik.util.svg.Base64Decoder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.net.URL;

import java.awt.Color;
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

import java.io.EOFException;
import java.io.IOException;

/**
 * RasterRable This is used to wrap a Rendered Image back into the
 * RenderableImage world.
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class RasterRable
    extends    AbstractRable {
    public final static String BASE64        = "base64,";

    CachableRed src;

    public RasterRable(CachableRed src) {
        super((Filter)null);
        this.src = src;
    }

    Thread thread = null;

    public RasterRable(final URL url) {
        super((Filter)null);

        thread = new URLImageLoader(url);
        thread.start();
    }

    public RasterRable(final String base64Data) {
        this(base64Data, 0, base64Data.length());
    }

    public RasterRable(final String base64Data, int start) {
        this(base64Data, start, base64Data.length()-start);
    }

    public RasterRable(final String base64Data, int start, int length) {
        super((Filter)null);

        thread = new Base64ImageLoader(base64Data, start, length);
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

    /**
     * This creates a RasterRable from a URL.  Currently the URL
     * must point to an image that can be interpreted by the Core JDK
     * as a java.awt.Image, via Toolkit.createImage(...);
     * @param url the url to load
     * @param bounds The bounds of the image
     */
    public static Filter create(URL url, Rectangle2D bounds) {
        return new RasterRable(url);
    }

    /**
     * This creates a RasterRable from embedded base64 data.  After
     * decoding the data must be in a format that can be interpreted
     * by the Core JDK as a java.awt.Image, via the
     * Toolkit.createImage(...) function.
     * @param dataUrl the data url containing the complete base64
     *                encoded image data.
     * @param bounds The bounds of the image 
     */
    public static Filter create(String dataUrl, Rectangle2D bounds){
        //
        // Using the data protocol
        //
        int start = dataUrl.indexOf(BASE64);
        if(start == -1)
            // It's not base64 data, Hmm...
            return null;

        start += BASE64.length();

        return new RasterRable(dataUrl, start);
    }

    // Logically these belong to the ImageLoader but it can't
    // have static members, so the live here.
    static Component mediaComponent = new Label() { };
    static MediaTracker mediaTracker = new MediaTracker(mediaComponent);
    static int id = 0;

    /**
     * This is a base class for defered loading of images.
     * It handles most/all of the threading issues for the subclasses.
     */
    protected abstract class ImageLoader extends Thread {

        /**
         * Constructor, does nothing.
         */
        public ImageLoader() { }

        /**
         * Subclass should implement this to return a java.awt.Image
         * instance when called.
         * This is used by the default load method.
         */
        public abstract Image createImage();

        /**
         * Default load method, handles decoding the Image instance
         * into a BufferedImage.
         */
        public BufferedImage load() {

            try {
                Image img = createImage();

                if (img == null) 
                    // No Image was created, something is wrong...
                    return null;

                // In some cases the image returned will be a
                // BufferedImage. In which case we don't need to copy
                // it to ours.
                if (img instanceof BufferedImage)
                    return (BufferedImage)img;

                // Setup for using the mediaTracker.
                int myID;
                synchronized (mediaTracker) {
                    myID = id++;
                }

                // Add our image to the media tracker and wait....
                mediaTracker.addImage(img, myID);
                while (true) {
                    try {
                        mediaTracker.waitForID(myID);
                    }
                    catch(InterruptedException ie) {
                        // Something woke us up but the image
                        // isn't done yet, so try again.
                        continue;
                    };
                
                    // All done!
                    break;
                }

                // Clean up our registraction
                mediaTracker.removeImage(img, myID);

                // Build the image to return.
                BufferedImage bi = null;
                bi = new BufferedImage(img.getWidth(null),
                                       img.getHeight(null),
                                       BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bi.createGraphics();

                g2d.drawImage(img, 0, 0, null);
                g2d.dispose();
                
                return bi;
            }
            catch (Exception e) { 
                // Just catch everything.
            }

            // Only get here if there is an error, return null.
            return null;
        }

        public void run() {
            // Load the BufferedImage
            BufferedImage bi = load();

            if (bi == null) {
                // Something wrong, We Couldn't load the image,
                // display a 'broken' image, place-holder...
                bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bi.createGraphics();

                g2d.setColor(new Color(255,255,255,190));
                g2d.fillRect(0, 0, 100, 100);
                g2d.setColor(Color.black);
                g2d.drawRect(2, 2, 96, 96);
                g2d.drawString("Broken Image", 6, 50);
                g2d.dispose();
            }

            // Store the result in the RasterRable, and wake up 
            // anyone who is waiting for our image..
            synchronized (this) {
                src = ConcreteRenderedImageCachableRed.wrap(bi);
                this.notifyAll();
            }
        }
    }

    /**
     * This subclass is based off loading images from a URL
     * It takes care to cache images so if the same image
     * is used many times subsequent retrievals won't create
     * duplicate images.
     */
    protected class URLImageLoader extends ImageLoader {

        /** The URL to load */
        protected URL           url;

        /** The URL cache we are associated with */
        protected URLImageCache cache;

        /** 
         * Load an image from a url.
         * This will use the default URL cache.
         * @param url The url of the image to load (must be readable
         *            with Toolkit.createImage(url).
         */
        public URLImageLoader(URL url) {
            this(url, URLImageCache.getDefaultCache());
        }

        /** 
         * Load an image from a url.
         * @param url The url of the image to load (must be readable
         *            with Toolkit.createImage(url).
         * @param cache The Url cache to store the result in.
         */
        public URLImageLoader(URL url,
                              URLImageCache cache) {
            this.cache = cache;
            this.url   = url;
        }

        /**
         * Calls Toolkit.createImage and returns the result.
         */
        public Image createImage() {
            return Toolkit.getDefaultToolkit().createImage(url);
        }

        /**
         * This extends the baseclass load by checking the cache
         * first and if the image isn't there it calls the baseclass
         * load (to do most of the work) and puts the result in
         * the URL cache if it succeeds.
         */
        public BufferedImage load() {
            BufferedImage bi = cache.request(url);
            
            if (bi != null) 
                return bi;

            bi = super.load();

            if (bi != null)
                cache.put(url, bi); // Let other people use our work..
            else 
                // Something wrong, We Couldn't loda the image.
                // This is debateable but I'm going to clear my entry
                // rather than put the 'broken link' image here...
                cache.clear(url);

            return bi;
        }
    }

    /**
     * This subclass is based off loading images from base64 encoded
     * data in a String.
     */
    protected class Base64ImageLoader extends ImageLoader {

        protected String base64Data;
        int start, length;

        /**
         * Decode an image from a base64 encoded string.
         * The complete contents of the string will be used.
         * @param base64Data the image data encoded with base64 in a string.
         */
        public Base64ImageLoader(String base64Data) {
            this.base64Data = base64Data;
            this.start      = 0; 
            this.length     = base64Data.length();;
        }

        /**
         * Decode an image from a base64 encoded string.
         * Only the indicated portion of the string will be considered.
         * @param base64Data The image data encoded with base64 in a string.
         * @param start      The starting offset for decoding.
         * @param length     The extent of the data to decode.
         */
        public Base64ImageLoader(String base64Data, int start, int length) {
            this.base64Data = base64Data;
            this.start      = start; 
            this.length     = length;
        }

        /**
         * This decodes the data into a byte array and
         * passes that to Toolkit.createImage.
         */
        public Image createImage() {
            try{
                InputStream is;
                is = new ByteArrayInputStream(base64Data.getBytes(),
                                              start, length);

                Base64Decoder decoder = new Base64Decoder();
                byte imageBuffer[] = null;
                imageBuffer = decoder.decodeBuffer(is);
                return Toolkit.getDefaultToolkit().createImage(imageBuffer);
            }catch(EOFException eofe) {
                return null;
            }catch(IOException ioe) {
                return null;
            }
        }
    }
}
