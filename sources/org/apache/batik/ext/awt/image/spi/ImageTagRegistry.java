/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.spi;

import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.color.ICCColorSpaceExt;
import org.apache.batik.util.ParsedURL;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.URLImageCache;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.renderable.ProfileRable;


public class ImageTagRegistry {
    List entries = new LinkedList();

    URLImageCache rawCache;
    URLImageCache imgCache;

    public ImageTagRegistry() {
        this(null, null);
    }

    public ImageTagRegistry(URLImageCache rawCache, URLImageCache imgCache) {
        if (rawCache == null)
            rawCache = new URLImageCache();
        if (imgCache == null)
            imgCache = new URLImageCache();

        this.rawCache= rawCache;
        this.imgCache= imgCache;
    }

    public void flushCache() {
        rawCache.flush();
        imgCache.flush();
    }

    public Filter readURL(ParsedURL purl) {
        return readURL(purl, null);
    }

    public Filter readURL(ParsedURL purl, ICCColorSpaceExt colorSpace) {
        boolean needRawData = (colorSpace != null);

        Filter      ret        = null;
        URLImageCache cache;
        if (needRawData) cache = rawCache;
        else             cache = imgCache;

        ret = cache.request(purl);
        if (ret != null) {
            // System.out.println("Image came from cache" + purl);
            if (colorSpace != null)
                ret = new ProfileRable(ret, colorSpace);
            return ret;
        }
        // System.out.println("Image didn't come from cache: " + purl);

        InputStream is         = null;
        boolean     openFailed = false;

        Iterator i;
        i = entries.iterator();
        while (i.hasNext()) {
            RegistryEntry re = (RegistryEntry)i.next();

            if (re instanceof URLRegistryEntry) {
                URLRegistryEntry ure = (URLRegistryEntry)re;
                if (ure.isCompatibleURL(purl)) {
                    ret = ure.handleURL(purl, needRawData);

                    // Check if we got an image.
                    if (ret != null) break;
                }
            } else if (re instanceof StreamRegistryEntry) {
                StreamRegistryEntry sre = (StreamRegistryEntry)re;
                // Quick out last time the open didn't work for this
                // URL so don't try again...
                if (openFailed) continue;

                try {
                    if (is == null) {
                        // Haven't opened the stream yet let's try.
                        try {
                            is = purl.openStream();
                        } catch(IOException ioe) {
                            // Couldn't open the stream, go to next entry.
                            openFailed = true;
                            continue;
                        }

                        if (!is.markSupported())
                            // Doesn't support mark so wrap with
                            // BufferedInputStream that does.
                            is = new BufferedInputStream(is);
                    }

                    if (sre.isCompatibleStream(is)) {
                        ret = sre.handleStream(is, needRawData);
                        if (ret != null) break;
                    }
                } catch (StreamCorruptedException sce) {
                    // Stream is messed up so setup to reopen it..
                    is = null;
                }
            }
        }
        
        if (ret == null) {
            cache.clear(purl);
            ret = getBrokenLinkImage();
        } else if (ret != getBrokenLinkImage()) {
            cache.put(purl, ret);

            if (colorSpace != null)
                ret = new ProfileRable(ret, colorSpace);
        }

        return ret;
    }
    
    public Filter readStream(InputStream is) {
        return readStream(is, null);
    }

    public Filter readStream(InputStream is, ICCColorSpaceExt colorSpace) {
        if (!is.markSupported())
            // Doesn't support mark so wrap with BufferedInputStream that does.
            is = new BufferedInputStream(is);

        boolean needRawData = (colorSpace != null);

        Filter ret = null;;

        Iterator i = entries.iterator();
        while (i.hasNext()) {
            RegistryEntry re = (RegistryEntry)i.next();
            if (! (re instanceof StreamRegistryEntry))
                continue;
            StreamRegistryEntry sre = (StreamRegistryEntry)re;

            try {
                if (sre.isCompatibleStream(is)) {
                    ret = sre.handleStream(is, needRawData);

                    if (ret != null) break;
                }
            } catch (StreamCorruptedException sce) {
                break;
            }
        }

        if (ret == null)
            ret = getBrokenLinkImage();
        else if ((colorSpace != null) &&
                 (ret != getBrokenLinkImage()))
            ret = new ProfileRable(ret, colorSpace);

        return ret;
    }

    public void register(RegistryEntry newRE) {
        float priority = newRE.getPriority();

        ListIterator li;
        li = entries.listIterator();
        while (li.hasNext()) {
            RegistryEntry re = (RegistryEntry)li.next();
            if (re.getPriority() > priority) {
                li.previous();
                li.add(newRE);
                return;
            }
        }
        li.add(newRE);
    }

    static ImageTagRegistry registry = new ImageTagRegistry();
    
    static {
        registry.register(new PNGRegistryEntry());
        registry.register(new JPEGRegistryEntry());
        registry.register(new JDKRegistryEntry());
    }

    public static ImageTagRegistry getRegistry() { 
        return registry;
    }

    static Filter brokenLinkImg = null;

    static public synchronized Filter getBrokenLinkImage() {
        if (brokenLinkImg != null)
            return brokenLinkImg;

        BufferedImage bi;
        bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
	
        g2d.setColor(new Color(255,255,255,190));
        g2d.fillRect(0, 0, 100, 100);
        g2d.setColor(Color.black);
        g2d.drawRect(2, 2, 96, 96);
        g2d.drawString("Broken Image", 6, 50);
        g2d.dispose();

        brokenLinkImg = new RedRable(GraphicsUtil.wrap(bi));
        return brokenLinkImg;
    }

}
