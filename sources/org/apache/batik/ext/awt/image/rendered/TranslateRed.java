/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;


import org.apache.batik.ext.awt.image.GraphicsUtil;

import java.awt.Rectangle;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
/**
 * This implements CachableRed based on a BufferedImage.
 * You can use this to wrap a BufferedImage that you want to
 * appear as a CachableRed.
 * It essentially ignores the dependency and dirty region methods.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class TranslateRed extends AbstractRed {
    
    protected int deltaX;
    protected int deltaY;

    /**
     * Construct an instance of CachableRed around a BufferedImage.
     */
    public TranslateRed(CachableRed cr, int xloc, int yloc) {
        super(cr, new Rectangle(xloc,  yloc,
                                cr.getWidth(), cr.getHeight()),
              cr.getColorModel(), cr.getSampleModel(), 
              cr.getTileGridXOffset()+xloc-cr.getMinX(), 
              cr.getTileGridYOffset()+yloc-cr.getMinY(), 
              null);
        deltaX = xloc-cr.getMinX();
        deltaY = yloc-cr.getMinY();
    }

    public int getDeltaX() { return deltaX; }
    public int getDeltaY() { return deltaY; }

    /**
     * fetch the source image for this node.
     */
    public CachableRed getSource() {
        return (CachableRed)getSources().get(0);
    }

    public Object getProperty(String name) {
        return getSource().getProperty(name);
    }

    public String [] getPropertyNames() {
        return getSource().getPropertyNames();
    }

    public Raster getTile(int tileX, int tileY) {
        Raster r = getSource().getTile(tileX, tileY);
        
        return r.createTranslatedChild(r.getMinX()+deltaX,
                                       r.getMinY()+deltaY);
    }

    public Raster getData() {
        Raster r = getSource().getData();
        return r.createTranslatedChild(r.getMinX()+deltaX,
                                       r.getMinY()+deltaY);
    }

    public Raster getData(Rectangle rect) {
        Rectangle r = (Rectangle)rect.clone();
        r.translate(-deltaX, -deltaY);
        Raster ret = getSource().getData(r);
        return ret.createTranslatedChild(ret.getMinX()+deltaX,
                                         ret.getMinY()+deltaY);
    }

    public WritableRaster copyData(WritableRaster wr) {
        WritableRaster wr2 = wr.createWritableTranslatedChild
            (wr.getMinX()-deltaX, wr.getMinY()-deltaY);

        getSource().copyData(wr2);

        return wr;
    }
}
