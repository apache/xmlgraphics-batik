/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.spi;

import java.net.URL;
import java.net.MalformedURLException;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.util.ParsedURL;

import java.awt.Toolkit;
import java.awt.MediaTracker;
import java.awt.Label;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;


/**
 * This Image tag registy entry is setup to wrap the core JDK
 * Image stream tools.  
 */
public class JDKRegistryEntry extends AbstractRegistryEntry 
    implements URLRegistryEntry {

    /**
     * The priority of this entry.
     * This entry should in most cases be the last entry.
     * but if one wishes one could set a priority higher and be called
     * afterwords
     */
    public final static float PRIORITY = 
        1000*MagicNumberRegistryEntry.PRIORITY;

    JDKRegistryEntry() {
        super ("JDK", PRIORITY, new String[0]);
    }

    /**
     * Check if the Stream references an image that can be handled by
     * this format handler.  The input stream passed in should be
     * assumed to support mark and reset.
     *
     * If this method throws a StreamCorruptedException then the
     * InputStream will be closed and a new one opened (if possible).
     *
     * This method should only throw a StreamCorruptedException if it
     * is unable to restore the state of the InputStream
     * (i.e. mark/reset fails basically).  
     */
    public boolean isCompatibleURL(ParsedURL purl) {
        try {
            URL url = new URL(purl.toString());
        } catch (MalformedURLException mue) {
            // No sense in trying it if we can't build a URL out of it.
            return false;
        }
        return true;
    }

    /**
     * Decode the URL into a RenderableImage
     *
     * @param is The input stream that contains the image.
     * @param needRawData If true the image returned should not have
     *                    any default color correction the file may 
     *                    specify applied.  
     */
    public Filter handleURL(ParsedURL purl, boolean needRawData) {
        
        final DeferRable       dr         = new DeferRable();
	
        URL              url;
        try {
            url = new URL(purl.toString());
        } catch (MalformedURLException mue) {
            return null;
        }

        Toolkit tk = Toolkit.getDefaultToolkit();
        final Image img = tk.createImage(url);
        if (img == null)
            return null;

        Thread t = new Thread() {
                
                public RenderedImage loadImage(Image img) {
                    // In some cases the image will be a
                    // BufferedImage (subclass of RenderedImage).
                    if (img instanceof RenderedImage)
                        return (RenderedImage)img;

                    // Setup the mediaTracker.
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

                    // Build the image to .
                    BufferedImage bi = null;
                    bi = new BufferedImage(img.getWidth(null),
                                           img.getHeight(null),
                                           BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = bi.createGraphics();

                    g2d.drawImage(img, 0, 0, null);
                    g2d.dispose();
                    return bi;
                }

                public void run() {
                    Filter filt;
                    RenderedImage ri = loadImage(img);
                    
                    filt = new RedRable(GraphicsUtil.wrap(ri));
                    dr.setSource(filt);
                }
            };
        t.start();
        return dr;
    }

    // Stuff for Image Loading.
    static Component mediaComponent = new Label() { };
    static MediaTracker mediaTracker = new MediaTracker(mediaComponent);
    static int id = 0;
}

