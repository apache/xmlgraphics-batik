/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.CachableRed;

import java.util.Vector;

import java.awt.Shape;
import java.awt.Rectangle;

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;


/**
 * This implements CachableRed around a RenderedImage.
 * You can use this to wrap a RenderedImage that you want to
 * appear as a CachableRed.
 * It essentially ignores the dependency and dirty region methods.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class ConcreteRenderedImageCachableRed implements CachableRed {

    public static CachableRed wrap(RenderedImage ri) {
        if (ri instanceof CachableRed)
            return (CachableRed) ri;
        if (ri instanceof BufferedImage)
            return new ConcreteBufferedImageCachableRed((BufferedImage)ri);
        return new ConcreteRenderedImageCachableRed(ri);
    }

    private RenderedImage src;
    private Vector srcs = new Vector(0);

    public ConcreteRenderedImageCachableRed(RenderedImage src) {
        if(src == null){
            throw new IllegalArgumentException();
        }
        this.src = src;
    }

    public Vector getSources() {
        return srcs; // should always be empty...
    }

    public Rectangle getBounds() {
        return new Rectangle(getMinX(),
                             getMinY(),
                             getWidth(),
                             getHeight());
    }

    public int getMinX() {
        return src.getMinX();
    }
    public int getMinY() {
        return src.getMinY();
    }

    public int getWidth() {
        return src.getWidth();
    }
    public int getHeight() {
        return src.getHeight();
    }

    public ColorModel getColorModel() {
        return src.getColorModel();
    }

    public SampleModel getSampleModel() {
        return src.getSampleModel();
    }

    public int getMinTileX() {
        return src.getMinTileX();
    }
    public int getMinTileY() {
        return src.getMinTileY();
    }

    public int getNumXTiles() {
        return src.getNumXTiles();
    }
    public int getNumYTiles() {
        return src.getNumYTiles();
    }

    public int getTileGridXOffset() {
        return src.getTileGridXOffset();
    }

    public int getTileGridYOffset() {
        return src.getTileGridYOffset();
    }

    public int getTileWidth() {
        return src.getTileWidth();
    }
    public int getTileHeight() {
        return src.getTileHeight();
    }

    public Object getProperty(String name) {
        return src.getProperty(name);
    }

    public String[] getPropertyNames() {
        return src.getPropertyNames();
    }

    public Raster getTile(int tileX, int tileY) {
        return src.getTile(tileX, tileY);
    }

    public WritableRaster copyData(WritableRaster raster) {
        return src.copyData(raster);
    }

    public Raster getData() {
        return src.getData();
    }

    public Raster getData(Rectangle rect) {
        return src.getData(rect);
    }

    public Shape getDependencyRegion(int srcIndex, Rectangle outputRgn) {
        throw new IndexOutOfBoundsException
            ("Nonexistant source requested.");
    }

    public Shape getDirtyRegion(int srcIndex, Rectangle inputRgn) {
        throw new IndexOutOfBoundsException
            ("Nonexistant source requested.");
    }
}
