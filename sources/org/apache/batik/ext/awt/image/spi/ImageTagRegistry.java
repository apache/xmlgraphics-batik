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
import java.util.Iterator;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;

import java.net.URL;

import org.apache.batik.ext.awt.image.renderable.Filter;

public class ImageTagRegistry {
    List entries = new LinkedList();

    public ImageTagRegistry() {
    }

    Filter readURL(URL url) {
        Iterator i;
        i = entries.iterator();
        while (i.hasNext()) {
            RegistryEntry re = (RegistryEntry)i.next();
            if (! (re instanceof URLRegistryEntry))
                continue;
            URLRegistryEntry ure = (URLRegistryEntry)re;
            if (ure.isCompatibleURL(url)) {
                return ure.handleURL(url);
            }
        }
	
        InputStream is = null;
        i = entries.iterator();
        while (i.hasNext()) {
            RegistryEntry re = (RegistryEntry)i.next();
            if (! (re instanceof StreamRegistryEntry))
                continue;
            StreamRegistryEntry sre = (StreamRegistryEntry)re;

            try {
                if (is == null) {
                    try {
                        is = url.openStream();
                    } catch(IOException ioe) {
                        // Couldn't open the stream...
                        return null;
                    }

                    if (!is.markSupported())
                        // Doesn't support mark so wrap with
                        // BufferedInputStream that does.
                        is = new BufferedInputStream(is);
                }

                if (sre.isCompatibleStream(is))
                    return sre.handleStream(is);
            } catch (StreamCorruptedException sce) {
                // Stream is messed up so setup to reopen it..
                is = null;
            }
        }
        return null;
    }
    
    Filter readStream(InputStream is) {
        if (!is.markSupported())
            // Doesn't support mark so wrap with BufferedInputStream that does.
            is = new BufferedInputStream(is);

        Iterator i = entries.iterator();
        while (i.hasNext()) {
            RegistryEntry re = (RegistryEntry)i.next();
            if (! (re instanceof StreamRegistryEntry))
                continue;
            StreamRegistryEntry sre = (StreamRegistryEntry)re;

            try {
                if (sre.isCompatibleStream(is))
                    return sre.handleStream(is);
            } catch (StreamCorruptedException sce) {
                // Stream is messed up so we can't try and continue.
                return null;
            }
        }
        return null;
    }

    public void register(RegistryEntry re) {
        entries.add(re);
    }

    static ImageTagRegistry registry = null;
    
    public synchronized static ImageTagRegistry getRegistry() { 
        if (registry != null) 
            return registry;

        registry = new ImageTagRegistry();
        registry.register(new PNGRegistryEntry());
        registry.register(new JPEGRegistryEntry());
        return registry;
    }
}
