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
import java.awt.Point;
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


import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.TileGenerator;
import org.apache.batik.ext.awt.image.rendered.TileStore;
import org.apache.batik.ext.awt.image.rendered.TileCache;

/**
 * This filter simply tiles its tile starting from the upper
 * left corner of the tiled region.
 * 
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class TileRed extends AbstractRed implements TileGenerator {
    static final AffineTransform IDENTITY = new AffineTransform();

    /**
     * Area tiled by this filter. 
     */
    Rectangle tiledRegion;

    int xStep;
    int yStep;

    TileStore tiles;

    private RenderingHints  hints;

    /**
     * Tile
     */
    RenderedImage  tile   = null;
    WritableRaster raster = null;


    public TileRed(RenderedImage tile,
                   Rectangle tiledRegion) {
        this(tile, tiledRegion, tile.getWidth(), tile.getHeight(), null);
    }

    public TileRed(RenderedImage tile,
                   Rectangle tiledRegion,
                   RenderingHints hints) {
        this(tile, tiledRegion, tile.getWidth(), tile.getHeight(), hints);
    }

    public TileRed(RenderedImage tile, 
                   Rectangle tiledRegion,
                   int xStep, int yStep) {
        this(tile, tiledRegion, xStep, yStep, null);
    }

    public TileRed(RenderedImage tile, 
                   Rectangle tiledRegion,
                   int xStep, int yStep,
                   RenderingHints hints) {
        if(tiledRegion == null){
            throw new IllegalArgumentException();
        }

        if(tile == null){
            throw new IllegalArgumentException();
        }

        // org.apache.batik.test.gvt.ImageDisplay.showImage("Tile: ", tile);
        this.tiledRegion = tiledRegion;
        this.xStep       = xStep;
        this.yStep       = yStep;
        this.hints       = hints;

        SampleModel sm = fixSampleModel(tile, tiledRegion);
        ColorModel cm = tile.getColorModel();


        if ((2.0*sm.getWidth()*sm.getHeight()) > (xStep*(double)yStep))
            {
                sm = sm.createCompatibleSampleModel(xStep, yStep);
                raster = Raster.createWritableRaster
                    (sm, new Point(tile.getMinX(), tile.getMinY()));
            }
        
        // Initialize our base class We set our bounds be we will
        // respond with data for any area we cover.  This is needed
        // because the userRegion passed into PatterPaintContext
        // doesn't account for stroke So we use that as a basis but
        // when the context asks us for stuff outside that region we
        // complie.
        init((CachableRed)null, tiledRegion, cm, sm, 
             tile.getMinX(), tile.getMinY(), null);

        if (raster != null) {
            fillRasterFrom(raster, tile);
            this.tile = null;  // don't need it (It's in the raster).
        }
        else {
            this.tile        = tile;
            tiles = TileCache.getTileMap(this);
        }
    }

    public WritableRaster copyData(WritableRaster wr) {
        int tx0 = getXTile(wr.getMinX());
        int ty0 = getYTile(wr.getMinY());
        int tx1 = getXTile(wr.getMinX()+wr.getWidth() -1);
        int ty1 = getYTile(wr.getMinY()+wr.getHeight()-1);

        final boolean is_INT_PACK = 
            GraphicsUtil.is_INT_PACK_Data(getSampleModel(), false);

        for (int y=ty0; y<=ty1; y++)
            for (int x=tx0; x<=tx1; x++) {
                Raster r = getTile(x, y);
                if (is_INT_PACK)
                    GraphicsUtil.copyData_INT_PACK(r, wr);
                else
                    GraphicsUtil.copyData_FALLBACK(r, wr);
            }
        return wr;
    }


    public Raster getTile(int x, int y) {
        
        if (raster!=null) {
            // We have a Single raster that we translate where needed
            // position.  So just offest appropriately.
            int tx = tileGridXOff+x*tileWidth;
            int ty = tileGridYOff+y*tileHeight;
            return raster.createTranslatedChild(tx, ty);
        }

        // System.out.println("Checking Cache [" + x + "," + y + "]");
        return tiles.getTile(x,y);
    }

    public Raster genTile(int x, int y) {
        // System.out.println("Cache Miss     [" + x + "," + y + "]");
        int tx = tileGridXOff+x*tileWidth;
        int ty = tileGridYOff+y*tileHeight;
        
        if (raster!=null) {
            // We have a Single raster that we translate where needed
            // position.  So just offest appropriately.
            return raster.createTranslatedChild(tx, ty);
        }

        Point pt = new Point(tx, ty);
        WritableRaster wr = Raster.createWritableRaster(sm, pt);
        fillRasterFrom(wr, tile);
        return wr;
    }

    public WritableRaster fillRasterFrom(WritableRaster wr, RenderedImage src){
        // System.out.println("Getting Raster : " + count + " " + wr.getMinX() + "/" + wr.getMinY() + "/" + wr.getWidth() + "/" + wr.getHeight());
        // System.out.println("Tile           : " + tile.getMinX() + "/" + tile.getMinY() + "/" + tile.getWidth() + "/" + tile.getHeight());

        ColorModel cm = getColorModel();
        BufferedImage bi
            = new BufferedImage(cm,
                                wr.createWritableTranslatedChild(0, 0),
                                cm.isAlphaPremultiplied(), null);

        Graphics2D g = GraphicsUtil.createGraphics(bi, hints);

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
        double tileTx, tileTy;
        int x1 = src.getMinX()+src.getWidth()-1;
        int y1 = src.getMinY()+src.getHeight()-1;

        tileTx = Math.ceil(((minX-x1)/xStep))*xStep;
        tileTy = Math.ceil(((minY-y1)/yStep))*yStep;

        g.translate(tileTx, tileTy);

        double curX = tileTx - wr.getMinX() + src.getMinX();
        double curY = tileTy - wr.getMinY() + src.getMinY();
        int col = 0;

        // System.out.println("Src : " + src.getWidth()+"x"+src.getHeight());
        // System.out.println("tileTx/tileTy : " + tileTx + " / " + tileTy);
        while(curY <= maxY){
            while(curX <= maxX){
                // System.out.println("curX/curY : " + curX + " / " + curY);
                // System.out.println("transform : " + 
                //                    g.getTransform().getTranslateX() + 
                //                    " / " + 
                //                    g.getTransform().getTranslateY());
                GraphicsUtil.drawImage(g, src);
                curX += xStep;
                g.translate(xStep, 0);
                col++;
            }
            curY += yStep;
            g.translate(-col*xStep, yStep);
            curX -= col*xStep;
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
        int defSz = AbstractTiledRed.getDefaultTileSize();
        SampleModel sm = src.getSampleModel();
        int w = sm.getWidth();
        if (w < defSz) w = defSz;
        if (w > bounds.width)  w = bounds.width;
        int h = sm.getHeight();
        if (h < defSz) h = defSz;
        if (h > bounds.height) h = bounds.height;
        return sm.createCompatibleSampleModel(w, h);
    }
}
