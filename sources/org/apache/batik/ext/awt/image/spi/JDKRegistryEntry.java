/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.ext.awt.image.spi;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.util.ParsedURL;

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

    public JDKRegistryEntry() {
        super ("JDK", PRIORITY, new String[0], new String [] {"image/gif"});
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
            new URL(purl.toString());
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
        
        final URL url;
        try {
            url = new URL(purl.toString());
        } catch (MalformedURLException mue) {
            return null;
        }

        final DeferRable  dr  = new DeferRable();
        final String      errCode;
        final Object []   errParam;
        if (purl != null) {
            errCode  = ERR_URL_FORMAT_UNREADABLE;
            errParam = new Object[] {"JDK", url};
        } else {
            errCode  = ERR_STREAM_FORMAT_UNREADABLE;
            errParam = new Object[] {"JDK"};
        }

        Thread t = new Thread() {
                public void run() {
                    Filter filt = null;

                    Toolkit tk = Toolkit.getDefaultToolkit();
                    Image img = tk.createImage(url);

                    if (img != null) {
                        RenderedImage ri = loadImage(img, dr);
                        if (ri != null) {
                            filt = new RedRable(GraphicsUtil.wrap(ri));
                        }
                    }

                    if (filt == null)
                        filt = ImageTagRegistry.getBrokenLinkImage
                            (this, errCode, errParam);
                    
                    dr.setSource(filt);
                }
            };
        t.start();
        return dr;
    }

    // Stuff for Image Loading.
    public RenderedImage loadImage(Image img, final DeferRable  dr) {
        // In some cases the image will be a
        // BufferedImage (subclass of RenderedImage).
        if (img instanceof RenderedImage)
            return (RenderedImage)img;

        MyImgObs observer = new MyImgObs();
        Toolkit.getDefaultToolkit().prepareImage(img, -1, -1, observer);
        observer.waitTilWidthHeightDone();
        if (observer.imageError)
            return null;
        int width  = observer.width;
        int height = observer.height;
        dr.setBounds(new Rectangle2D.Double(0, 0, width, height));

        // Build the image to draw into.
        BufferedImage bi = new BufferedImage
            (width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        
        // Wait till the image is fully loaded.
        observer.waitTilImageDone();
        if (observer.imageError)
            return null;
        dr.setProperties(new HashMap());

        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return bi;
    }


    public class MyImgObs implements ImageObserver {
        boolean widthDone = false;
        boolean heightDone = false;
        boolean imageDone = false;
        int width = -1;
        int height = -1;
        boolean imageError = false;

        int IMG_BITS = ALLBITS|ERROR|ABORT;

        public void clear() {
            width=-1;
            height=-1;
            widthDone = false;
            heightDone = false;
            imageDone       = false;
        }

        public boolean imageUpdate(Image img, int infoflags, 
                                   int x, int y, int width, int height) {
            synchronized (this) {
                boolean notify = false;

                if ((infoflags & WIDTH)   != 0) this.width  = width;
                if ((infoflags & HEIGHT)  != 0) this.height = height;

                if ((infoflags & ALLBITS) != 0) {
                    this.width  = width;
                    this.height = height;
                }

                if ((infoflags & IMG_BITS) != 0) {
                    if ((!widthDone) || (!heightDone) || (!imageDone)) {
                        widthDone  = true;
                        heightDone = true;
                        imageDone  = true;
                        notify     = true;
                    }
                    if ((infoflags & ERROR) != 0) {
                        imageError = true;
                    }
                }


                if ((!widthDone) && (this.width != -1)) {
                    notify = true;
                    widthDone = true;
                }
                if ((!heightDone) && (this.height != -1)) {
                    notify = true;
                    heightDone = true;
                }

                if (notify)
                    notifyAll();
            }
            return true;
        }

        public synchronized void waitTilWidthHeightDone() {
            while ((!widthDone) || (!heightDone)) {
                try {
                    // Wait for someone to set xxxDone
                    wait();
                }
                catch(InterruptedException ie) { 
                    // Loop around again see if src is set now...
                }
            }
        }
        public synchronized void waitTilWidthDone() {
            while (!widthDone) {
                try {
                    // Wait for someone to set xxxDone
                    wait();
                }
                catch(InterruptedException ie) { 
                    // Loop around again see if src is set now...
                }
            }
        }
        public synchronized void waitTilHeightDone() {
            while (!heightDone) {
                try {
                    // Wait for someone to set xxxDone
                    wait();
                }
                catch(InterruptedException ie) { 
                    // Loop around again see if src is set now...
                }
            }
        }

        public synchronized void waitTilImageDone() {
            while (!imageDone) {
                try {
                    // Wait for someone to set xxxDone
                    wait();
                }
                catch(InterruptedException ie) { 
                    // Loop around again see if src is set now...
                }
            }
        }
    }

}
