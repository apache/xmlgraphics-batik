/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.CachableRed;

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
public class ConcreteBufferedImageCachableRed extends AbstractRed {
    // The bufferedImage that we wrap...
    BufferedImage bi;

    /**
     * Construct an instance of CachableRed around a BufferedImage.
     */
    public ConcreteBufferedImageCachableRed(BufferedImage bi) {
        super((CachableRed)null, new Rectangle(bi.getMinX(),  bi.getMinY(),
                                  bi.getWidth(), bi.getHeight()),
              bi.getColorModel(), bi.getSampleModel(), 0, 0, null);

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
        r.translate(-getMinX(), - getMinY());
        Raster ret = bi.getData(r);
        return ret.createTranslatedChild(rect.x, rect.y);
    }

    public WritableRaster copyData(WritableRaster wr) {
        BufferedImage dest = new BufferedImage(bi.getColorModel(), wr.createWritableTranslatedChild(0,0), 
                                               bi.getColorModel().isAlphaPremultiplied(), null);
        java.awt.Graphics2D g2d = dest.createGraphics();
        g2d.drawImage(bi, java.awt.geom.AffineTransform.getTranslateInstance(getMinX()-wr.getMinX(), getMinY()-wr.getMinY()), null);
        g2d.dispose();
        return wr;
    }

}
