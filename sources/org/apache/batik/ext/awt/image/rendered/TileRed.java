/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;



/**
 * This filter simply tiles its tile starting from the upper
 * left corner of the tiled region.
 * 
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class TileRed extends AbstractRed {
    static final AffineTransform IDENTITY = new AffineTransform();

    /**
     * Area tiled by this filter. 
     */
    Rectangle tiledRegion;

    /**
     * Tile
     */
    RenderedImage tile;

    public TileRed(Rectangle tiledRegion,
                   RenderedImage tile){

        if(tiledRegion == null){
            throw new IllegalArgumentException();
        }

        if(tile == null){
            throw new IllegalArgumentException();
        }

        this.tiledRegion = tiledRegion;
        this.tile = tile;

        SampleModel sm = fixSampleModel(tile, tiledRegion);
        ColorModel cm = tile.getColorModel();
        
        // Initialize our base class
        init((CachableRed)null, tiledRegion, cm, sm, 
             tiledRegion.x, tiledRegion.y, null);
    }

    java.awt.Color colors[] = { new java.awt.Color(255, 0, 0, 128),
                                new java.awt.Color(0, 255, 0, 128),
                                new java.awt.Color(0, 0, 255, 128),
                                new java.awt.Color(255, 255, 0, 128) };
    int count;

    public WritableRaster copyData(WritableRaster wr){
        // System.out.println("Getting Raster : " + count + " " + wr.getMinX() + "/" + wr.getMinY() + "/" + wr.getWidth() + "/" + wr.getHeight());
        // System.out.println("Tile           : " + tile.getMinX() + "/" + tile.getMinY() + "/" + tile.getWidth() + "/" + tile.getHeight());

        ColorModel cm = getColorModel();
        BufferedImage bi
            = new BufferedImage(cm,
                                wr.createWritableTranslatedChild(0, 0),
                                cm.isAlphaPremultiplied(), null);

        Graphics2D g = bi.createGraphics();
        int tw = tile.getWidth();
        int th = tile.getHeight();
        int curX = 0, curY = 0;
        int minX = wr.getMinX(), minY = wr.getMinY();
        int maxY = wr.getHeight();
        int maxX = wr.getWidth();

        g.setComposite(AlphaComposite.Clear);
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, maxX, maxY);
        g.setComposite(AlphaComposite.SrcOver);

        g.translate(-minX, -minY);

        // Process initial translate so that tile is
        // painted to the left of the raster top-left 
        // corner on the first drawRenderedImage
        int tileTx = Math.round(((wr.getMinX() - tile.getMinX())/tile.getWidth()))*tile.getWidth();
        int tileTy = Math.round(((wr.getMinY() - tile.getMinY())/tile.getHeight()))*tile.getHeight();

        if(tileTx < 0){
            tileTx -= tile.getWidth();
        }

        if(tileTy < 0){
            tileTy -= tile.getHeight();
        }

        g.translate(tileTx, tileTy);

        curX = tileTx - wr.getMinX() + tile.getMinX();
        curY = tileTy - wr.getMinY() + tile.getMinY();
        int col = 0;

        // System.out.println("tileTx/tileTy : " + tileTx + " / " + tileTy);
        while(curY <= maxY){
            while(curX <= maxX){
                // System.out.println("curX/curY : " + curX + " / " + curY);
                // System.out.println("transform : " + g.getTransform().getTranslateX() + " / " + g.getTransform().getTranslateY());
                g.drawRenderedImage(tile, IDENTITY);
                curX += tw;
                g.translate(tw, 0);
                col++;
            }
            curY += th;
            g.translate(-col*tw, th);
            curX -= col*tw;
            col = 0;
        }
        
        /*g.setTransform(new AffineTransform());
        g.setPaint(colors[count++]);
        count %= colors.length;

        g.fillRect(0, 0, maxX, maxY);*/

        return wr;
    }

    /**
     * This function 'fixes' the source's sample model.
     * right now it just ensures that the sample model isn't
     * much larger than my width.
     */
    protected static SampleModel fixSampleModel(RenderedImage src,
                                                Rectangle   bounds) {
        SampleModel sm = src.getSampleModel();
        int w = sm.getWidth();
        if (w < 256) w = 256;
        if (w > bounds.width)  w = bounds.width;
        int h = sm.getHeight();
        if (h < 256) h = 256;
        if (h > bounds.height) h = bounds.height;
        return sm.createCompatibleSampleModel(w, h);
    }

}
