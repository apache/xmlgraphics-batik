/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.TileRable;

public class ConcreteTileRable extends AbstractRable implements TileRable{
    /**
     * Tile region
     */
    private FilterRegion tileRegion;

    /**
     * Tiled region
     */
    private FilterRegion tiledRegion;

    /**
     * Controls whether the tileRegion clips the source
     * or not
     */
    private boolean overflow;

    /**
     * Returns the tile region
     */
    public FilterRegion getTileRegion(){
        return tileRegion;
    }

    /**
     * Sets the tile region
     */
    public void setTileRegion(FilterRegion tileRegion){
        if(tileRegion == null){
            throw new IllegalArgumentException();
        }
        this.tileRegion = tileRegion;
    }

    /**
     * Returns the tiled region
     */
    public FilterRegion getTiledRegion(){
        return tiledRegion;
    }

    /**
     * Sets the tiled region
     */
    public void setTiledRegion(FilterRegion tiledRegion){
        if(tiledRegion == null){
            throw new IllegalArgumentException();
        }
        this.tiledRegion = tiledRegion;
    }

    /**
     * Returns the overflow strategy
     */
    public boolean isOverflow(){
        return overflow;
    }

    /**
     * Sets the overflow strategy
     */
    public void setOverflow(boolean overflow){
        this.overflow = overflow;
    }

    /**
     * Default constructor
     */
    public ConcreteTileRable(Filter source, 
                             FilterRegion tiledRegion,
                             FilterRegion tileRegion,
                             boolean overflow){
        super(source);

        setTileRegion(tileRegion);
        setTiledRegion(tiledRegion);
        setOverflow(overflow);
    }

    /**
     * Sets the filter source
     */
    public void setSource(Filter src){
        init(src);
    }

    /**
     * Return's the tile source
     */
    public Filter getSource(){
        return (Filter)srcs.get(0);
    }

    /**
     * Returns this filter's bounds
     */
    public Rectangle2D getBounds2D(){
        return tiledRegion.getRegion();
    }

    /**
     * Creates a rendering for the given render context.
     * This filter gets a rendering from its source and
     * tiles it into its own filter region.
     * 
     * The procedure used to tile is as follows. A tile
     * is created which has its origin in the upper left
     * corner of the tiled region. That tile is separated 
     * into 4 areas: top-left, top-right, bottom-left and
     * bottom-right. Each of these areas is mapped to 
     * some input area from the source.
     * If the source is smaller than the tiled area, then
     * a single rendering is requested from the source.
     * If the source's width or height is bigger than that
     * of the tiled area, then separate renderings are 
     * requested from the source.
     * 
     */
    public RenderedImage createRendering(RenderContext rc){
        // Rendered result
        RenderedImage result = null;

        AffineTransform usr2dev = rc.getTransform();

        // Hints
        RenderingHints hints = rc.getRenderingHints();
        if(hints == null){
            hints = new RenderingHints(null);
        }

        hints = new RenderingHints(hints);

        // The region actually tiles is the intersection
        // of the tiledRegion and the area of interest
        Rectangle2D tiledRect = getBounds2D();
        Shape aoiShape = rc.getAreaOfInterest();
        Rectangle2D aoiRect = aoiShape.getBounds2D();
        Rectangle2D.intersect(tiledRect, aoiRect, tiledRect);

        // Get the tile rectangle in user space
        Rectangle2D tileRect = tileRegion.getRegion();

        System.out.println("tileRect : " + tileRect);
        System.out.println("tiledRect: " + tiledRect);

        if((tileRect.getWidth() > 0)
           &&
           (tileRect.getHeight() > 0)
           &&
           (tiledRect.getWidth() > 0)
           &&
           (tiledRect.getHeight() > 0)){
            
            //
            // (tiledX, tiledY)
            //                    <----------- min(tileWidth, tiledWidth) --------------->
            //                    ^ +----------+-----------------------------------------+
            //                    | +    A'    +                     B'                  +
            //                    | +----------+-----------------------------------------+
            // min(tileHeight,    | +          +                                         +
            //     tiledHeight)   | +          +                                         +
            //                    | +    C'    +                     D'                  +
            //                    | +          +                                         +
            //                    ^ +----------+-----------------------------------------+
            //
            // Maps to, in the tile:
            //
            // (tileX, tileY)
            // 
            //                    <-----------             tileWidth               --------------->
            //                    ^ +-----------------------------------------+--------+----------+
            //                    | +                                         +        +          |
            //     tiledHeight    | +                                         +        +          |
            //                    | +                     D                   +        +    C     |
            //                    | +                                         +        +          |
            //                    | +-----------------------------------------+--------+----------|
            //                    | +                                         |        |          |
            //                    | +                                         |        |          |
            //                    | +-----------------------------------------+--------+----------+
            //                    | |                     B                   +        +    A     |
            //                    ^ +-----------------------------------------+--------+----------+
            
            // w  = min(tileWidth, tiledWidth)
            // h  = min(tileHeight, tiledHeight)
            // dx = tileWidth  - (tiledX - tileX)%tileWidth;
            // dy = tileHeight - (tiledY - tileY)%tileHeight;
            //
            // A = (tileX + tileWidth - dx, tileY + tileHeight - dy, dx, dy)
            // B = (tileX, tileY + tileHeight - dy, w - dx, dy)
            // C = (tileX + tileWidth - dx, tileY, dx, h - dy)
            // D = (tileX, tileY, w - dx, h - dy)

            double tileX = tileRect.getX();
            double tileY = tileRect.getY();
            double tileWidth = tileRect.getWidth();
            double tileHeight = tileRect.getHeight();
            
            double tiledX = tiledRect.getX();
            double tiledY = tiledRect.getY();
            double tiledWidth = tiledRect.getWidth();
            double tiledHeight = tiledRect.getHeight();

            double w = Math.min(tileWidth, tiledWidth);
            double h = Math.min(tileHeight, tiledHeight);
            double dx = (tiledX - tileX)%tileWidth;
            double dy = (tiledY - tileY)%tileHeight;

            if(dx > 0){
                dx = tileWidth - dx;
            }
            else{
                dx *= -1;
            }

            if(dy > 0){
                dy = tileHeight - dy;
            }
            else{
                dy *= -1;
            }

            System.out.println("dx / dy / w / h : " + dx + " / " + dy + " / " + w + " / " + h);

            Rectangle2D.Double A = new Rectangle2D.Double(tileX + tileWidth - dx, tileY + tileHeight - dy, dx, dy);
            Rectangle2D.Double B = new Rectangle2D.Double(tileX, tileY + tileHeight - dy, w - dx, dy);
            Rectangle2D.Double C = new Rectangle2D.Double(tileX + tileWidth - dx, tileY, dx, h - dy);
            Rectangle2D.Double D = new Rectangle2D.Double(tileX, tileY, w - dx, h - dy);

            Rectangle2D realTileRect 
                = new Rectangle2D.Double(tiledRect.getX(),
                                         tiledRect.getY(),
                                         w, h);

            System.out.println("A rect    : " + A);
            System.out.println("B rect    : " + B);
            System.out.println("C rect    : " + C);
            System.out.println("D rect    : " + D);
            System.out.println("realTileR : " + realTileRect);

            // A, B, C and D are the four user space are that make the
            // tile that will be used. We create a rendering for each of these areas that is
            // not empty (i.e., with either width or height equal to zero)
            RenderedImage ARed = null, BRed = null, CRed = null, DRed = null;
            Filter source = getSource();

            if(A.getWidth() > 0 && A.getHeight() > 0){
                System.out.println("Rendering A");
                Rectangle devA = usr2dev.createTransformedShape(A).getBounds();
                if(devA.width > 0 && devA.height > 0){
                    AffineTransform ATxf = new AffineTransform(usr2dev);
                    ATxf.translate(-A.x + tiledX,
                                   -A.y + tiledY);

                    Shape aoi = A;
                    if(overflow){
                        aoi = new Rectangle2D.Double(A.x, 
                                                     A.y,
                                                     tiledWidth,
                                                     tiledHeight);
                    }

                    hints.put(GraphicsNode.KEY_AREA_OF_INTEREST,
                              aoi);

                    RenderContext arc 
                        = new RenderContext(ATxf, aoi, hints);

                    ARed = source.createRendering(arc);
                }
            }

            if(B.getWidth() > 0 && B.getHeight() > 0){
                System.out.println("Rendering B");
                Rectangle devB = usr2dev.createTransformedShape(B).getBounds();
                if(devB.width > 0 && devB.height > 0){
                    AffineTransform BTxf = new AffineTransform(usr2dev);
                    BTxf.translate(-B.x + (tiledX + dx),
                                   -B.y + tiledY);

                    Shape aoi = B;
                    if(overflow){
                        aoi = new Rectangle2D.Double(B.x - tiledWidth + w - dx,
                                                     B.y,
                                                     tiledWidth,
                                                     tiledHeight);
                    }

                    hints.put(GraphicsNode.KEY_AREA_OF_INTEREST,
                              aoi);

                    RenderContext brc 
                        = new RenderContext(BTxf, aoi, hints);

                    BRed = source.createRendering(brc);
                }
            }

            if(C.getWidth() > 0 && C.getHeight() > 0){
                System.out.println("Rendering C");
                Rectangle devC = usr2dev.createTransformedShape(C).getBounds();
                if(devC.width > 0 && devC.height > 0){
                    AffineTransform CTxf = new AffineTransform(usr2dev);
                    CTxf.translate(-C.x + tiledX,
                                   -C.x + (tiledY + dy));

                    Shape aoi = C;
                    if(overflow){
                        aoi = new Rectangle2D.Double(C.x,
                                                     C.y - tileHeight + h - dy,
                                                     tiledWidth,
                                                     tiledHeight);
                    }

                    hints.put(GraphicsNode.KEY_AREA_OF_INTEREST,
                              aoi);

                    RenderContext crc 
                        = new RenderContext(CTxf, aoi, hints);

                    CRed = source.createRendering(crc);
                }
            }

            if(D.getWidth() > 0 && D.getHeight() > 0){
                System.out.println("Rendering D");
                Rectangle devD = usr2dev.createTransformedShape(D).getBounds();
                if(devD.width > 0 && devD.height > 0){
                    AffineTransform DTxf = new AffineTransform(usr2dev);
                    DTxf.translate(-D.x + (tiledX + dx),
                                   -D.y + (tiledY + dy));

                    Shape aoi = D;
                    if(overflow){
                        aoi = new Rectangle2D.Double(D.x - tileWidth + w - dx,
                                                     D.y - tileHeight + h - dy,
                                                     tiledWidth,
                                                     tiledHeight);
                    }

                    hints.put(GraphicsNode.KEY_AREA_OF_INTEREST,
                              aoi);

                    RenderContext drc 
                        = new RenderContext(DTxf, aoi, hints);

                    DRed = source.createRendering(drc);
                }
            }

            //
            // Now, combine ARed, BRed, CRed and DRed into a single
            // RenderedImage that will be tiled
            //
            final Rectangle realTileRectDev 
                = usr2dev.createTransformedShape(realTileRect).getBounds();

            BufferedImage realTileBI
                = new BufferedImage(realTileRectDev.width,
                                    realTileRectDev.height,
                                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = realTileBI.createGraphics();
            g.translate(-realTileRectDev.x,
                        -realTileRectDev.y);

            System.out.println("realTileRectDev " + realTileRectDev);

            AffineTransform redTxf = new AffineTransform();
            Point2D.Double redVec = new Point2D.Double();
            if(ARed != null){
                System.out.println("Drawing A");
                g.drawRenderedImage(ARed, redTxf);
            }
            if(BRed != null){
                System.out.println("Drawing B");
                redVec.x = dx;
                redVec.y = 0;
                usr2dev.deltaTransform(redVec, redVec);
                redTxf.setToTranslation(redVec.x, redVec.y);
                g.drawRenderedImage(BRed, redTxf);
            }
            if(CRed != null){
                System.out.println("Drawing C");
                redVec.x = 0;
                redVec.y = dy;
                usr2dev.deltaTransform(redVec, redVec);
                redTxf.setToTranslation(redVec.x, redVec.y);
                g.drawRenderedImage(CRed, redTxf);
            }
            if(DRed != null){
                System.out.println("Drawing D");
                redVec.x = dx;
                redVec.y = dy;
                usr2dev.deltaTransform(redVec, redVec);
                redTxf.setToTranslation(redVec.x, redVec.y);
                System.out.println("redVec : " + redVec.x + " / " +  redVec.y);
                System.out.println("DRed   : " + DRed.getMinX() + " / " + DRed.getMinY() 
                                   + " / " + DRed.getWidth() + " / " + DRed.getHeight());
                g.drawRenderedImage(DRed, redTxf);
                /*g.setPaint(java.awt.Color.red);
                g.fillRect(DRed.getMinX(), DRed.getMinY(),
                DRed.getWidth(), DRed.getHeight());*/
            }

            RenderedImage realTile = new ConcreteBufferedImageCachableRed(realTileBI){
                    public int getMinX(){
                        return realTileRectDev.x;
                    }
                    
                    public int getMinY(){
                        return realTileRectDev.y;
                    }
                };

            //
            // Now, realTile is anchored in the upper left corner of the 
            // tiled area. 
            //
            Rectangle tiledRectDev 
                = usr2dev.createTransformedShape(tiledRect).getBounds();

            System.out.println("tiledRectDev    : " + tiledRectDev);

            BufferedImage tiledRed 
                = new BufferedImage(tiledRectDev.width,
                                    tiledRectDev.height,
                                    BufferedImage.TYPE_INT_ARGB);

            double curX = 0, curY = 0;
            Point2D.Double tileOrigin = new Point2D.Double();
            AffineTransform tileTxf = new AffineTransform();

            g = tiledRed.createGraphics();
            // g.setPaint(java.awt.Color.yellow);
            g.translate(-tiledRectDev.x,
                        -tiledRectDev.y);
            /*g.fillRect(tiledRectDev.x, tiledRectDev.y,
              tiledRectDev.width, tiledRectDev.height);*/

            while(curY < tiledHeight){
                while(curX < tiledWidth){
                    tileOrigin.x = curX;
                    tileOrigin.y = curY;

                    usr2dev.deltaTransform(tileOrigin,
                                           tileOrigin);
                    
                    tileTxf.setToTranslation(tileOrigin.x,
                                             tileOrigin.y);
                    
                    System.out.println("tileTxf : " + tileOrigin.x + " / " + tileOrigin.y);
                    g.drawRenderedImage(realTile, tileTxf);
                    // g.setPaint(java.awt.Color.red);
                    // g.fillRect(realTile.getMinX(), realTile.getMinY(), realTile.getWidth(), realTile.getHeight());
                    curX += w;
                }
                curY += h;
                curX = 0;
            }
            
            final int minX = tiledRectDev.x;
            final int minY = tiledRectDev.y;

            result = new ConcreteBufferedImageCachableRed(tiledRed){
                    public int getMinX(){
                        return minX;
                    }
                    
                    public int getMinY(){
                        return minY;
                    }
                };
        }

        return result;
    }
}
