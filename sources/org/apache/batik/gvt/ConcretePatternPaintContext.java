/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.ext.awt.image.renderable.AffineRable;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.ext.awt.image.renderable.PadMode;
import org.apache.batik.ext.awt.image.renderable.PadRable;

import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.renderable.TileRable;
import org.apache.batik.ext.awt.image.renderable.TileRable8Bit;
import org.apache.batik.ext.awt.image.rendered.TileRed;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;

/**
 * <tt>PaintContext</tt> for the <tt>ConcretePatterPaint</tt>
 * paint implementation.
 *
 * @author <a href="vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ConcretePatternPaintContext implements PaintContext {
    /**
     * ColorModel for the Rasters created by this Paint
     */
    private ColorModel rasterCM = ColorModel.getRGBdefault();

    /**
     * Working Raster
     */
    private WritableRaster raster;

    /**
     * Tile
     */
    private RenderedImage tiled;

    /**
     * Tile info
     */
    private double tileX, tileY, tileWidth, tileHeight;

    private AffineTransform dev2usr;

    private AffineTransform padTxf;

    /**
     * @param destCM ColorModel that receives the paint data
     * @param usr2dev user space to device space transform
     * @param hints
     * @param node GraphicsNode generating the pixel pattern
     * @param nodeTransform additional transform to apply to the
     *        pattern content node.
     * @param patternTile region to which the pattern is constrained
     * @param maximumExtenet of the region tiled by this paint. In user space.
     * @param overflow controls whether the pattern region clips the
     *        pattern tile
     */
    public ConcretePatternPaintContext(ColorModel destCM,
                                       AffineTransform usr2dev,
                                       RenderingHints hints,
                                       GraphicsNode node,
                                       GraphicsNodeRenderContext gnrc,
                                       AffineTransform nodeTxf,
                                       Rectangle2D patternTile,
                                       boolean overflow){
        if(usr2dev == null){
            throw new IllegalArgumentException();
        }

        if(hints == null){
            hints = new RenderingHints(null);
        }

        if(node == null){
            throw new IllegalArgumentException();
        }

        if(patternTile == null){
            throw new IllegalArgumentException();
        }

        if(nodeTxf == null){
            nodeTxf = new AffineTransform();
        }

        Rectangle2D nodeBounds = node.getBounds(gnrc);
        Rectangle2D patternBounds = patternTile;
        tileX = patternBounds.getX();
        tileY = patternBounds.getY();
        tileWidth = patternBounds.getWidth();
        tileHeight = patternBounds.getHeight();

        //
        // adjustTxf applies the nodeTransform first, then
        // the translation to move the node rendering into
        // the pattern region space
        //
        AffineTransform adjustTxf = new AffineTransform();
        adjustTxf.translate(patternBounds.getX(),
                            patternBounds.getY());
        adjustTxf.concatenate(nodeTxf);

        GraphicsNodeRable gnr
            = new GraphicsNodeRable8Bit(node, gnrc);

        AffineRable atr
            = new AffineRable8Bit(gnr, adjustTxf);


        Shape aoiShape = (Shape)hints.get(RenderingHintsKeyExt.KEY_AREA_OF_INTEREST);
        Rectangle2D tiledRegion = (aoiShape == null? null : aoiShape.getBounds2D());

        Rectangle2D padBounds = (Rectangle2D)patternBounds.clone();
        if(overflow){
            //
            // When there is overflow, make sure we take the
            // full node bounds into account.
            //
            Rectangle2D adjustedNodeBounds
                = adjustTxf.createTransformedShape(nodeBounds).getBounds2D();

            //System.out.println("adjustedBounds : " + adjustedNodeBounds);
            padBounds.add(adjustedNodeBounds);
        }

        PadRable pad
            = new PadRable8Bit(atr,
                                   padBounds,
                                   PadMode.ZERO_PAD);

        TileRable tileRable
            = new TileRable8Bit(pad,
                                    tiledRegion,
                                    patternBounds,
                                    overflow);

        RenderContext rc = new RenderContext(usr2dev,
                                             tiledRegion,
                                             hints);

        tiled = tileRable.createRendering(rc);

        //System.out.println("Created rendering");
        if(tiled == null){
            //System.out.println("Tile was null");
            WritableRaster wr = rasterCM.createCompatibleWritableRaster(32, 32);
            tiled = new BufferedImage(rasterCM, wr, false, null);
        }
    }

    public void dispose(){
        raster = null;
    }

    public ColorModel getColorModel(){
        return rasterCM;
    }

    public Raster getRaster(int x, int y, int width, int height){
        if(raster == null){
            raster = rasterCM.createCompatibleWritableRaster(width, height);
        }
        else if(raster.getWidth() < width ||
                raster.getHeight() < height){
            raster = rasterCM.createCompatibleWritableRaster(width, height);
        }

        // System.out.println("getRaster : " + x + "/" + y + "/" + width + "/" + height);
        WritableRaster wr = raster.createWritableChild(raster.getMinX(), raster.getMinY(),
                                                       width, height, x, y, null);

        return tiled.copyData(wr);
    }
}
