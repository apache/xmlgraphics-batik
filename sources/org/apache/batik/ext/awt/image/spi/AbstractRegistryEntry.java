/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.spi;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;

public abstract class AbstractRegistryEntry implements RegistryEntry{

    String name;
    float  priority;
    List   exts;
    
    public AbstractRegistryEntry(String    name,
                                 float     priority,
                                 String [] exts) {
        this.name     = name;
        this.priority = priority;
        this.exts     = new ArrayList(exts.length);
        for (int i=0; i<exts.length; i++) {
            this.exts.add(exts[i]);
        }

        this.exts = Collections.unmodifiableList(this.exts);
    }
			    
    public AbstractRegistryEntry(String name,
                                 float  priority,
                                 String ext) {
        this.name = name;
        this.priority = priority;
        this.exts = new ArrayList(1);
        this.exts.add(ext);
        this.exts = Collections.unmodifiableList(exts);
    }
			    

    public String getFormatName() {
        return name;
    }

    public List   getStandardExtensions() {
        return exts;
    }

    public float  getPriority() {
        return priority;
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
