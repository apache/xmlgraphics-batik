/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.spi;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;

public class DefaultBrokenLinkProvider 
    implements BrokenLinkProvider {

    static Filter brokenLinkImg = null;
        

    public Filter getBrokenLinkImage(String code, Object [] params) {
        synchronized (DefaultBrokenLinkProvider.class) {
            if (brokenLinkImg != null)
                return brokenLinkImg;

            BufferedImage bi;
            bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

            // Put the broken link property in the image so people know
            // This isn't the "real" image.
            Hashtable ht = new Hashtable();
            ht.put(BROKEN_LINK_PROPERTY, "BrokenLink");
            bi = new BufferedImage(bi.getColorModel(), bi.getRaster(),
                                   bi.isAlphaPremultiplied(),
                                   ht);
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
}
