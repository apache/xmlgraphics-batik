/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;


import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.image.GraphicsUtil;
/**
 * This implements CachableRed based on a BufferedImage.
 * You can use this to wrap a BufferedImage that you want to
 * appear as a CachableRed.
 * It essentially ignores the dependency and dirty region methods.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class BufferedImageCachableRed extends AbstractRed {
    // The bufferedImage that we wrap...
    BufferedImage bi;

    /**
     * Construct an instance of CachableRed around a BufferedImage.
     */
    public BufferedImageCachableRed(BufferedImage bi) {
        super((CachableRed)null, 
              new Rectangle(bi.getMinX(),  bi.getMinY(),
                            bi.getWidth(), bi.getHeight()),
              bi.getColorModel(), bi.getSampleModel(), 
              bi.getMinX(), bi.getMinY(), null);

        this.bi = bi;
    }

    public BufferedImageCachableRed(BufferedImage bi, 
                                            int xloc, int yloc) {
        super((CachableRed)null, new Rectangle(xloc,  yloc,
                                               bi.getWidth(), 
                                               bi.getHeight()),
              bi.getColorModel(), bi.getSampleModel(), xloc, yloc, null);

        this.bi = bi;
    }

    public Rectangle getBounds() {
        return new Rectangle(getMinX(),
                             getMinY(),
                             getWidth(),
                             getHeight());
    }

    /**
     * fetch the bufferedImage from this node.
     */
    public BufferedImage getBufferedImage() {
        return bi;
    }

    public Object getProperty(String name) {
        return bi.getProperty(name);
    }

    public String [] getPropertyNames() {
        return bi.getPropertyNames();
    }

    public Raster getTile(int tileX, int tileY) {
        return bi.getTile(tileX,tileY);
    }

    public Raster getData() {
        Raster r = bi.getData();
        return r.createTranslatedChild(getMinX(), getMinY());
    }

    public Raster getData(Rectangle rect) {
        Rectangle r = (Rectangle)rect.clone();

        if (r.intersects(getBounds()) == false)
            return null;
        r = r.intersection(getBounds());
        r.translate(-getMinX(), - getMinY());

        Raster ret = bi.getData(r);
        return ret.createTranslatedChild(ret.getMinX()+getMinX(), 
                                         ret.getMinY()+getMinY());
    }

    public WritableRaster copyData(WritableRaster wr) {
        WritableRaster wr2 = wr.createWritableTranslatedChild
            (wr.getMinX()-getMinX(),
             wr.getMinY()-getMinY());

        GraphicsUtil.copyData(bi.getRaster(), wr2);

        /* This was the original code. This is _bad_ since it causes a
         * multiply and divide of the alpha channel to do the draw
         * operation.  I believe that at some point I switched to
         * drawImage in order to avoid some issues with
         * BufferedImage's copyData implementation but I can't
         * reproduce them now. Anyway I'm now using GraphicsUtil which
         * should generally be as fast if not faster...
         */
        /*
          BufferedImage dest;
         dest = new BufferedImage(bi.getColorModel(), 
                                  wr.createWritableTranslatedChild(0,0), 
                                  bi.getColorModel().isAlphaPremultiplied(), 
                                  null);
         java.awt.Graphics2D g2d = dest.createGraphics();
         g2d.drawImage(bi, null, getMinX()-wr.getMinX(), 
                       getMinY()-wr.getMinY());
         g2d.dispose(); 
         */
        return wr;
    }
}
