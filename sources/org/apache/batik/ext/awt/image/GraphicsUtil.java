/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image;

import java.awt.color.ColorSpace;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;

import org.apache.batik.gvt.filter.AffineRable;
import org.apache.batik.gvt.filter.CachableRed;
import org.apache.batik.gvt.filter.CompositeRable;
import org.apache.batik.gvt.filter.CompositeRule;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterChainRable;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

import org.apache.batik.gvt.filter.ConcreteBufferedImageCachableRed;
import org.apache.batik.gvt.filter.ConcreteRenderedImageCachableRed;
import org.apache.batik.gvt.filter.Any2LsRGBRed;
import org.apache.batik.gvt.filter.Any2sRGBRed;

/**
 * Set of utility methods for Graphics.
 * These generally bypass broken methods in Java2D or provide tweaked
 * implementations.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class GraphicsUtil {

    public static void drawImage(Graphics2D g2d,
                                 RenderedImage ri) {
        drawImage(g2d, wrap(ri));
    }

    public static void drawImage(Graphics2D g2d,
                                 CachableRed cr) {
        ColorModel  srcCM = cr.getColorModel();

        GraphicsConfiguration gc = g2d.getDeviceConfiguration();
        ColorModel g2dCM = gc.getColorModel();
        ColorSpace g2dCS = g2dCM.getColorSpace();

        if (g2dCS != srcCM.getColorSpace()) {
            if      (g2dCS == ColorSpace.getInstance(ColorSpace.CS_sRGB))
                cr = convertTosRGB(cr);
            else if (g2dCS == ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB))
                cr = convertToLsRGB(cr);
            srcCM = cr.getColorModel();
        }

        ColorModel drawCM = srcCM;
        if ((drawCM.hasAlpha()) && (g2dCM.hasAlpha())) {
            if (drawCM.isAlphaPremultiplied() !=
                g2dCM .isAlphaPremultiplied())
                drawCM = coerceColorModel(drawCM,
                                          g2dCM.isAlphaPremultiplied());
        }

        SampleModel srcSM = cr.getSampleModel();
        WritableRaster wr;
        wr = Raster.createWritableRaster(srcSM, new Point(0,0));
        BufferedImage bi = new BufferedImage
            (drawCM, wr, drawCM.isAlphaPremultiplied(), null);

        /*
        Graphics2D big2d = bi.createGraphics();
        // Fully transparent black.
        big2d.setColor(new java.awt.Color(0,0,0,0));
        big2d.setComposite(java.awt.AlphaComposite.Src);
        */

        int xt0 = cr.getMinTileX();
        int xt1 = xt0+cr.getNumXTiles();
        int yt0 = cr.getMinTileY();
        int yt1 = yt0+cr.getNumYTiles();
        int tw  = srcSM.getWidth();
        int th  = srcSM.getHeight();

        Rectangle crR = cr.getBounds();
        Rectangle tR  = new Rectangle(0,0,tw,th);
        Rectangle iR  = new Rectangle(0,0,0,0);

        if (false) {
            System.out.println("CRR: " + crR + " TG: [" +
                               xt0 +"," +
                               yt0 +"," +
                               xt1 +"," +
                               yt1 +"] Off: " +
                               cr.getTileGridXOffset() +"," +
                               cr.getTileGridXOffset());
        }

        DataBuffer db = wr.getDataBuffer();
        int yloc = yt0*th+cr.getTileGridYOffset();
        for (int y=yt0; y<yt1; y++) {
            int xloc = xt0*tw+cr.getTileGridXOffset();
            for (int x=xt0; x<xt1; x++) {
                wr = Raster.createWritableRaster(srcSM, db,
                                                 new Point(xloc, yloc));
                cr.copyData(wr);
                coerceData(wr, srcCM, drawCM.isAlphaPremultiplied());

                tR.x = xloc;
                tR.y = yloc;
                Rectangle2D.intersect(crR, tR, iR);

                // Make sure we only draw the region that was writting...
                BufferedImage subBI;
                subBI = bi.getSubimage(iR.x-xloc, iR.y-yloc,
                                       iR.width,  iR.height);
                if (false)
                    System.out.println("IR: " + iR);

                g2d.drawImage(subBI, null, iR.x, iR.y);
                // big2d.fillRect(0, 0, tw, th);
                xloc += tw;
            }
            yloc += th;
        }
    }

    public static void drawImage(Graphics2D    g2d,
                                 Filter        filter,
                                 RenderContext rc) {

        AffineTransform origDev  = g2d.getTransform();
        Shape           origClip = g2d.getClip();
        RenderingHints  origRH   = g2d.getRenderingHints();

        g2d.clip(rc.getAreaOfInterest());
        g2d.transform(rc.getTransform());
        g2d.setRenderingHints(rc.getRenderingHints());

        drawImage(g2d, filter);

        g2d.setTransform(origDev);
        g2d.setClip(origClip);
        g2d.setRenderingHints(origRH);
    }

    public static void drawImage(Graphics2D g2d,
                                 Filter     filter) {
        if (filter instanceof AffineRable) {
            AffineRable ar = (AffineRable)filter;

            AffineTransform at = g2d.getTransform();

            g2d.transform(ar.getAffine());
            drawImage(g2d, ar.getSource());

            g2d.setTransform(at);
            return;
        }

        // These optimizations only apply if we are using
        // SrcOver.  Otherwise things break...
        if (g2d.getComposite() == java.awt.AlphaComposite.SrcOver) {
            if (filter instanceof PadRable) {
                PadRable pr = (PadRable)filter;
                if (pr.getPadMode() == PadMode.ZERO_PAD) {
                    Rectangle2D padBounds = pr.getPadRect();

                    Shape clip = g2d.getClip();
                    g2d.clip(padBounds);
                    drawImage(g2d, pr.getSource());

                    g2d.setClip(clip);
                    return;
                }
            }
            else if (filter instanceof CompositeRable) {
                CompositeRable cr = (CompositeRable)filter;
                // For the over mode we can just draw them in order...
                if (cr.getCompositeRule() == CompositeRule.OVER) {
                    Vector srcs = cr.getSources();
                    Iterator i = srcs.iterator();
                    while (i.hasNext()) {
                        drawImage(g2d, (Filter)i.next());
                    }
                    return;
                }
            }
            else if (filter instanceof GraphicsNodeRable) {
                GraphicsNodeRable gnr = (GraphicsNodeRable)filter;
                if (gnr.getUsePrimitivePaint()) {
                    gnr.getGraphicsNode().primitivePaint
                        (g2d, GraphicsNodeRenderContext.
                         getGraphicsNodeRenderContext(g2d));
                } else {
                    try {
                        gnr.getGraphicsNode().paint
                            (g2d, GraphicsNodeRenderContext.
                             getGraphicsNodeRenderContext(g2d));
                    } catch (InterruptedException ie) {
                        // Don't do anything we just return...
                    }
                }
                // Primitive Paint did the work...
                return;
            }
            else if (filter instanceof FilterChainRable) {
                FilterChainRable fcr = (FilterChainRable)filter;
                PadRable pad = (PadRable)fcr.getSource();
                drawImage(g2d, pad);
                return;
            }
        }

        // Get our sources image...
        // System.out.println("UnOpt: " + filter);
        AffineTransform at = g2d.getTransform();
        RenderedImage ri = filter.createRendering
            (new RenderContext(at, g2d.getClip(), g2d.getRenderingHints()));

        if (ri == null)
            return;

        g2d.setTransform(new AffineTransform());
        drawImage(g2d, GraphicsUtil.wrap(ri));
        g2d.setTransform(at);
    }

    public final static ColorModel Linear_sRGB =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_LINEAR_RGB), 24,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0x0, false,
                             DataBuffer.TYPE_INT);
    public final static ColorModel Linear_sRGB_Pre =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_LINEAR_RGB), 32,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0xFF000000, true,
                             DataBuffer.TYPE_INT);
    public final static ColorModel Linear_sRGB_Unpre =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_LINEAR_RGB), 32,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0xFF000000, false,
                             DataBuffer.TYPE_INT);

    public final static ColorModel sRGB =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_sRGB), 24,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0x0, false,
                             DataBuffer.TYPE_INT);
    public final static ColorModel sRGB_Pre =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_sRGB), 32,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0xFF000000, true,
                             DataBuffer.TYPE_INT);
    public final static ColorModel sRGB_Unpre =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_sRGB), 32,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0xFF000000, false,
                             DataBuffer.TYPE_INT);

    public static ColorModel makeLinear_sRGBCM(boolean premult) {
        if (premult)
            return Linear_sRGB_Pre;
        return Linear_sRGB_Unpre;
    }

    public static BufferedImage makeLinearBufferedImage(int width,
                                                        int height,
                                                        boolean premult) {
        ColorModel cm = makeLinear_sRGBCM(premult);
        WritableRaster wr = cm.createCompatibleWritableRaster(width, height);
        return new BufferedImage(cm, wr, premult, null);
    }

    public static CachableRed convertToLsRGB(CachableRed src) {
        ColorModel cm = src.getColorModel();
        ColorSpace cs = cm.getColorSpace();
        if (cs == ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB))
            return src;

        return new Any2LsRGBRed(src);
    }

    public static CachableRed convertTosRGB(CachableRed src) {
        ColorModel cm = src.getColorModel();
        ColorSpace cs = cm.getColorSpace();
        if (cs == ColorSpace.getInstance(ColorSpace.CS_sRGB))
            return src;

        return new Any2sRGBRed(src);
    }

    public static CachableRed wrap(RenderedImage ri) {
        if (ri instanceof CachableRed)
            return (CachableRed) ri;
        if (ri instanceof BufferedImage)
            return new ConcreteBufferedImageCachableRed((BufferedImage)ri);
        return new ConcreteRenderedImageCachableRed(ri);
    }

    protected static void copyData_INT_PACK(Raster src, WritableRaster dst) {
        // System.out.println("Fast copyData");
        int x0 = dst.getMinX();
        if (x0 < src.getMinX()) x0 = src.getMinX();

        int y0 = dst.getMinY();
        if (y0 < src.getMinY()) y0 = src.getMinY();

        int x1 = dst.getMinX()+dst.getWidth()-1;
        if (x1 > src.getMinX()+src.getWidth()-1)
            x1 = src.getMinX()+src.getWidth()-1;

        int y1 = dst.getMinY()+dst.getHeight()-1;
        if (y1 > src.getMinY()+src.getHeight()-1)
            y1 = src.getMinY()+src.getHeight()-1;

        int width  = x1-x0+1;
        int height = y1-y0+1;

        SinglePixelPackedSampleModel srcSPPSM;
        srcSPPSM = (SinglePixelPackedSampleModel)src.getSampleModel();

        final int     srcScanStride = srcSPPSM.getScanlineStride();
        DataBufferInt srcDB         = (DataBufferInt)src.getDataBuffer();
        final int []  srcPixels     = srcDB.getBankData()[0];
        final int     srcBase =
            (srcDB.getOffset() +
             srcSPPSM.getOffset(x0-src.getSampleModelTranslateX(),
                                y0-src.getSampleModelTranslateY()));


        SinglePixelPackedSampleModel dstSPPSM;
        dstSPPSM = (SinglePixelPackedSampleModel)dst.getSampleModel();

        final int     dstScanStride = dstSPPSM.getScanlineStride();
        DataBufferInt dstDB         = (DataBufferInt)dst.getDataBuffer();
        final int []  dstPixels     = dstDB.getBankData()[0];
        final int     dstBase =
            (dstDB.getOffset() +
             dstSPPSM.getOffset(x0-dst.getSampleModelTranslateX(),
                                y0-dst.getSampleModelTranslateY()));

        if ((srcScanStride == dstScanStride) &&
            (srcScanStride == width)) {
            // System.out.println("VERY Fast copyData");

            System.arraycopy(srcPixels, srcBase, dstPixels, dstBase,
                             width*height);
        } else if (width > 20) {
            int srcSP = srcBase;
            int dstSP = dstBase;
            for (int y=0; y<height; y++) {
                System.arraycopy(srcPixels, srcSP, dstPixels, dstSP, width);
                srcSP += srcScanStride;
                dstSP += dstScanStride;
            }
        } else {
            for (int y=0; y<height; y++) {
                int srcSP = srcBase+y*srcScanStride;
                int dstSP = dstBase+y*dstScanStride;
                for (int x=0; x<width; x++)
                    dstPixels[dstSP++] = srcPixels[srcSP++];
            }
        }
    }

    public static void copyData(Raster src, WritableRaster dst) {
        if (is_INT_PACK_Data(src.getSampleModel(), false) &&
            is_INT_PACK_Data(dst.getSampleModel(), false)) {
            copyData_INT_PACK(src, dst);
            return;
        }
        System.out.println("Slow copyData");

        int x0 = dst.getMinX();
        if (x0 < src.getMinX()) x0 = src.getMinX();

        int y0 = dst.getMinY();
        if (y0 < src.getMinY()) y0 = src.getMinY();

        int x1 = dst.getMinX()+dst.getWidth()-1;
        if (x1 > src.getMinX()+src.getWidth()-1)
            x1 = src.getMinX()+src.getWidth()-1;

        int y1 = dst.getMinY()+dst.getHeight()-1;
        if (y1 > src.getMinY()+src.getHeight()-1)
            y1 = src.getMinY()+src.getHeight()-1;

        int width  = x1-x0+1;
        int height = y1-y0+1;

        Object data = null;

        for (int y = y0; y <= y1 ; y++)  {
            data = src.getDataElements(x0,y,width,1,data);
            dst.setDataElements(x0,y,width,1,data);
        }
    }

    public static WritableRaster copyRaster(Raster ras) {
        return copyRaster(ras, ras.getMinX(), ras.getMinY());
    }

    public static WritableRaster copyRaster(Raster ras, int minX, int minY) {
        WritableRaster newSrcWR;
        WritableRaster ret = Raster.createWritableRaster
            (ras.getSampleModel(),
             new Point(0,0));
        ret = ret.createWritableChild
            (ras.getMinX()-ras.getSampleModelTranslateX(),
             ras.getMinY()-ras.getSampleModelTranslateY(),
             ras.getWidth(), ras.getHeight(),
             minX, minY, null);

        // Use System.arraycopy to copy the data between the two...
        DataBuffer srcDB = ras.getDataBuffer();
        DataBuffer retDB = ret.getDataBuffer();
        if (srcDB.getDataType() != retDB.getDataType()) {
            throw new IllegalArgumentException
                ("New DataBuffer doesn't match original");
        }
        int len   = srcDB.getSize();
        int banks = srcDB.getNumBanks();
        int [] offsets = srcDB.getOffsets();
        for (int b=0; b< banks; b++) {
            switch (srcDB.getDataType()) {
            case DataBuffer.TYPE_BYTE: {
                DataBufferByte srcDBT = (DataBufferByte)srcDB;
                DataBufferByte retDBT = (DataBufferByte)retDB;
                System.arraycopy(srcDBT.getData(b), offsets[b],
                                 retDBT.getData(b), offsets[b], len);
            }
            case DataBuffer.TYPE_INT: {
                DataBufferInt srcDBT = (DataBufferInt)srcDB;
                DataBufferInt retDBT = (DataBufferInt)retDB;
                System.arraycopy(srcDBT.getData(b), offsets[b],
                                 retDBT.getData(b), offsets[b], len);
            }
            case DataBuffer.TYPE_SHORT: {
                DataBufferShort srcDBT = (DataBufferShort)srcDB;
                DataBufferShort retDBT = (DataBufferShort)retDB;
                System.arraycopy(srcDBT.getData(b), offsets[b],
                                 retDBT.getData(b), offsets[b], len);
            }
            case DataBuffer.TYPE_USHORT: {
                DataBufferUShort srcDBT = (DataBufferUShort)srcDB;
                DataBufferUShort retDBT = (DataBufferUShort)retDB;
                System.arraycopy(srcDBT.getData(b), offsets[b],
                                 retDBT.getData(b), offsets[b], len);
            }
            }
        }

        return ret;
    }

    public static WritableRaster makeRasterWritable(Raster ras) {
        return makeRasterWritable(ras, ras.getMinX(), ras.getMinY());
    }

    public static WritableRaster makeRasterWritable(Raster ras,
                                                    int minX, int minY) {
        WritableRaster ret = Raster.createWritableRaster
            (ras.getSampleModel(),
             ras.getDataBuffer(),
             new Point(0,0));
        ret = ret.createWritableChild
            (ras.getMinX()-ras.getSampleModelTranslateX(),
             ras.getMinY()-ras.getSampleModelTranslateY(),
             ras.getWidth(), ras.getHeight(),
             minX, minY, null);
        return ret;
    }

    public static ColorModel
        coerceColorModel(ColorModel cm, boolean newAlphaPreMult) {
        if (cm.isAlphaPremultiplied() == newAlphaPreMult)
            return cm;

        // Easiest way to build proper colormodel for new Alpha state...
        // Eventually this should switch on known ColorModel types and
        // only fall back on this hack when the CM type is unknown.
        WritableRaster wr = cm.createCompatibleWritableRaster(1,1);
        return cm.coerceData(wr, newAlphaPreMult);
    }

    /**
     * Coerces data within a bufferedImage to match newAlphaPreMult,
     * Note that this can not change the colormodel of bi so you
     *
     * @param wr The raster to change the state of.
     * @param cm The colormodel currently associated with data in wr.
     * @param newAlphaPreMult The desired state of alpha Premult for raster.
     * @return A new colormodel that matches newAlphaPreMult.
     */
    public static ColorModel
        coerceData(WritableRaster wr, ColorModel cm, boolean newAlphaPreMult) {

        // System.out.println("CoerceData: " + cm.isAlphaPremultiplied() +
        //                    " Out: " + newAlphaPreMult);
        if (cm.hasAlpha()== false)
            // Nothing to do no alpha channel
            return cm;

        if (cm.isAlphaPremultiplied() == newAlphaPreMult)
            // nothing to do alpha state matches...
            return cm;

        int [] pixel = null;
        int    bands = wr.getNumBands();
        float  norm;
        if (newAlphaPreMult) {
            if (is_INT_PACK_Data(wr.getSampleModel(), true))
                mult_INT_PACK_Data(wr);
            else {
                norm = 1/255;
                for (int y=0; y<wr.getHeight(); y++)
                    for (int x=0; x<wr.getWidth(); x++) {
                        pixel = wr.getPixel(x,y,pixel);
                        int a = pixel[bands-1];
                        if ((a >= 0) && (a < 255)) {
                            float alpha = a*norm;
                            for (int b=0; b<bands-1; b++)
                                pixel[b] = (int)(pixel[b]*alpha+0.5f);
                            wr.setPixel(x,y,pixel);
                        }
                    }
            }
        } else {
            if (is_INT_PACK_Data(wr.getSampleModel(), true))
                divide_INT_PACK_Data(wr);
            else {
                for (int y=0; y<wr.getHeight(); y++)
                    for (int x=0; x<wr.getWidth(); x++) {
                        pixel = wr.getPixel(x,y,pixel);
                        int a = pixel[bands-1];
                        if ((a > 0) && (a < 255)) {
                            float ialpha = 255/(float)a;
                            for (int b=0; b<bands-1; b++)
                                pixel[b] = (int)(pixel[b]*ialpha+0.5f);
                            wr.setPixel(x,y,pixel);
                        }
                    }
            }
        }

        return coerceColorModel(cm, newAlphaPreMult);
    }


    /**
     * Copies data from one bufferedImage to another paying attention
     * to the state of AlphaPreMultiplied.
     *
     * @param src The source
     * @param dst The destination
     */
    public static void
        copyData(BufferedImage src, BufferedImage dst) {
        Rectangle srcRect = new Rectangle(0, 0,
                                          src.getWidth(), src.getHeight());
        copyData(src, srcRect, dst, new Point(0,0));
    }


    /**
     * Copies data from one bufferedImage to another paying attention
     * to the state of AlphaPreMultiplied.
     *
     * @param src The source
     * @param srcRect The Rectangle of source data to be copied
     * @param dst The destination
     * @param dstP The Place for the upper left corner of srcRect in dst.
     */
    public static void
        copyData(BufferedImage src, Rectangle srcRect,
                 BufferedImage dst, Point destP) {

        ColorSpace srcCS = src.getColorModel().getColorSpace();
        ColorSpace dstCS = dst.getColorModel().getColorSpace();

        /*
        if (srcCS != dstCS)
            throw new IllegalArgumentException
                ("Images must be in the same ColorSpace in order "+
                 "to copy Data between them");
        */
        boolean srcAlpha = src.getColorModel().hasAlpha();
        boolean dstAlpha = dst.getColorModel().hasAlpha();

        // System.out.println("Src has: " + srcAlpha +
        //                    " is: " + src.isAlphaPremultiplied());
        //
        // System.out.println("Dst has: " + dstAlpha +
        //                    " is: " + dst.isAlphaPremultiplied());

        if (srcAlpha == dstAlpha)
            if ((srcAlpha == false) ||
                (src.isAlphaPremultiplied() == dst.isAlphaPremultiplied())) {
                // They match one another so just copy everything...
                dst.setData(src.getRaster());
                return;
            }

        int [] pixel = null;
        Raster         srcR  = src.getRaster();
        WritableRaster dstR  = dst.getRaster();
        int            bands = dstR.getNumBands();
        float          norm;

        int dx = destP.x-srcRect.x;
        int dy = destP.y-srcRect.y;

        int x0 = srcRect.x;
        int y0 = srcRect.y;
        int x1 = x0+srcRect.width-1;
        int y1 = y0+srcRect.height-1;

        if (!srcAlpha) {
            // Src has no alpha dest does so set alpha to 1.0 everywhere.
            int [] oPix = new int[bands];
            for (int y=y0; y<y1; y++)
                for (int x=x0; x<x1; x++) {
                    oPix[bands-1] = 255;
                    for (int b=0; b<bands-1; b++)
                        oPix[b] = pixel[b];
                    dstR.setPixel(x+dx, y+dy,oPix);
                }
        } else if (dstAlpha && dst.isAlphaPremultiplied()) {
            // Src and dest have Alpha but we need to multiply it for dst.
            norm = 1/(float)255;
            for (int y=y0; y<y1; y++)
                for (int x=x0; x<x1; x++) {
                    pixel = srcR.getPixel(x,y,pixel);
                    int a = pixel[bands-1];
                    if ((a >= 0) && (a < 255)) {
                        float alpha = a*norm;
                        for (int b=0; b<bands-1; b++)
                            pixel[b] = (int)(pixel[b]*alpha+0.5f);
                    }
                    dstR.setPixel(x+dx, y+dy,pixel);
                }
        } else if (dstAlpha && !dst.isAlphaPremultiplied()) {
            // Src and dest have Alpha but we need to divide it out for dst.
            for (int y=y0; y<y1; y++)
                for (int x=x0; x<x1; x++) {
                    pixel = srcR.getPixel(x,y,pixel);
                    int a = pixel[bands-1];
                    if ((a > 0) && (a < 255)) {
                        float ialpha = 255/(float)a;
                        for (int b=0; b<bands-1; b++)
                            pixel[b] = (int)(pixel[b]*ialpha+0.5f);
                    }
                    dstR.setPixel(x+dx, y+dy,pixel);
                }
        } else if (src.isAlphaPremultiplied()) {
            int [] oPix = new int[bands];
            // Src has alpha dest does not so unpremult and store...
            for (int y=y0; y<y1; y++)
                for (int x=x0; x<x1; x++) {
                    pixel = srcR.getPixel(x,y,pixel);
                    int a = pixel[bands];
                    if (a > 0) {
                        if (a < 255) {
                            float ialpha = 255/(float)a;
                            for (int b=0; b<bands-1; b++)
                                oPix[b] = (int)(pixel[b]*ialpha+0.5f);
                        } else {
                            for (int b=0; b<bands-1; b++)
                                oPix[b] = pixel[b];
                        }
                    } else {
                        for (int b=0; b<bands-1; b++)
                            oPix[b] = 255;
                    }
                    dstR.setPixel(x+dx, y+dy,oPix);
                }
        } else {
            // Src has unpremult alpha, dest does not have alpha,
            // just copy the color channels over.
            Rectangle dstRect = new Rectangle(destP.x, destP.y,
                                              srcRect.width, srcRect.height);
            for (int b=0; b<bands; b++)
                copyBand(srcR, srcRect, b,
                         dstR, dstRect, b);
        }
    }

    public static void copyBand(Raster         src, int srcBand,
                                WritableRaster dst, int dstBand) {

        Rectangle sR   = src.getBounds();
        Rectangle dR   = dst.getBounds();
        Rectangle cpR  = sR.intersection(dR);

        copyBand(src, cpR, srcBand, dst, cpR, dstBand);
    }

    public static void copyBand(Raster         src, Rectangle sR, int sBand,
                                WritableRaster dst, Rectangle dR, int dBand) {
        int dy = dR.y -sR.y;
        int dx = dR.x -sR.x;
        sR = sR.intersection(src.getBounds());
        dR = dR.intersection(dst.getBounds());
        int width, height;
        if (dR.width  < sR.width)  width  = dR.width;
        else                       width  = sR.width;
        if (dR.height < sR.height) height = dR.height;
        else                       height = sR.height;

        int x = sR.x+dx;
        int [] samples = null;
        for (int y=sR.y; y< sR.y+height; y++) {
            samples = src.getSamples(sR.x, y, width, 1, sBand, samples);
            dst.setSamples(x, y+dy, width, 1, dBand, samples);
        }
    }

    public static boolean is_INT_PACK_Data(SampleModel sm,
                                           boolean requireAlpha) {
          // Check ColorModel is of type DirectColorModel
        if(!(sm instanceof SinglePixelPackedSampleModel)) return false;

        // Check transfer type
        if(sm.getDataType() != DataBuffer.TYPE_INT)       return false;

        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)sm;

        int [] masks = sppsm.getBitMasks();
        if (masks.length == 3) {
            if (requireAlpha) return false;
        } else if (masks.length != 4)
            return false;

        if(masks[0] != 0x00ff0000) return false;
        if(masks[1] != 0x0000ff00) return false;
        if(masks[2] != 0x000000ff) return false;
        if ((masks.length == 4) &&
            (masks[3] != 0xff000000)) return false;

        return true;
   }

    protected static void divide_INT_PACK_Data(WritableRaster wr) {
        // System.out.println("Divide Int");

        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();

        final int width = wr.getWidth();

        final int scanStride = sppsm.getScanlineStride();
        DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        final int base
            = (db.getOffset() +
               sppsm.getOffset(wr.getMinX()-wr.getSampleModelTranslateX(),
                               wr.getMinY()-wr.getSampleModelTranslateY()));
        int n=0;
        // Access the pixel data array
        final int pixels[] = db.getBankData()[0];
        for (int y=0; y<wr.getHeight(); y++) {
            int sp = base + y*scanStride;
            final int end = sp + width;
            while (sp < end) {
                int pixel = pixels[sp];
                int a = pixel>>>24;
                if (a<=0) {
                    pixels[sp] = 0x00FFFFFF;
                }
                else if (a<255) {
                    int aFP = (0x00FF0000/a);
                    pixels[sp] =
                        ((a << 24) |
                         (((((pixel&0xFF0000)>>16)*aFP)&0xFF0000)    ) |
                         (((((pixel&0x00FF00)>>8) *aFP)&0xFF0000)>>8 ) |
                         (((((pixel&0x0000FF))    *aFP)&0xFF0000)>>16));
                }
                sp++;
            }
        }
    }

    protected static void mult_INT_PACK_Data(WritableRaster wr) {
        // System.out.println("Multiply Int: " + wr);

        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();

        final int width = wr.getWidth();

        final int scanStride = sppsm.getScanlineStride();
        DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        final int base
            = (db.getOffset() +
               sppsm.getOffset(wr.getMinX()-wr.getSampleModelTranslateX(),
                               wr.getMinY()-wr.getSampleModelTranslateY()));
        int n=0;
        // Access the pixel data array
        final int pixels[] = db.getBankData()[0];
        for (int y=0; y<wr.getHeight(); y++) {
            int sp = base + y*scanStride;
            final int end = sp + width;
            while (sp < end) {
                int pixel = pixels[sp];
                int a = pixel>>>24;
                if ((a>=0) && (a<255)) {
                    pixels[sp] = ((a << 24) |
                                  ((((pixel&0xFF0000)*a)>>8)&0xFF0000) |
                                  ((((pixel&0x00FF00)*a)>>8)&0x00FF00) |
                                  ((((pixel&0x0000FF)*a)>>8)&0x0000FF));
                }
                sp++;
            }
        }
    }
}
