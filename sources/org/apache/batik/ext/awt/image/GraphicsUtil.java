/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image;

import java.awt.color.ColorSpace;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
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
import java.awt.image.renderable.RenderableImage;

import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.filter.GraphicsNodeRable;

import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PaintRable;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.Any2LsRGBRed;
import org.apache.batik.ext.awt.image.rendered.Any2sRGBRed;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.MultiplyAlphaRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.TranslateRed;
import org.apache.batik.ext.awt.image.SVGComposite;


/**
 * Set of utility methods for Graphics.
 * These generally bypass broken methods in Java2D or provide tweaked
 * implementations.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class GraphicsUtil {

    public static AffineTransform IDENTITY = new AffineTransform();

    /**
     * Draws <tt>ri</tt> into <tt>g2d</tt>.  It does this be
     * requesting tiles from <tt>ri</tt> and drawing them individually
     * in <tt>g2d</tt> it also takes care of some colorspace and alpha
     * issues.
     * @param g2d The Graphics2D to draw into.
     * @param ri  The image to be drawn.
     */
    public static void drawImage(Graphics2D g2d,
                                 RenderedImage ri) {
        drawImage(g2d, wrap(ri));
    }

    /**
     * Draws <tt>cr</tt> into <tt>g2d</tt>.  It does this be
     * requesting tiles from <tt>ri</tt> and drawing them individually
     * in <tt>g2d</tt> it also takes care of some colorspace and alpha
     * issues.
     * @param g2d The Graphics2D to draw into.
     * @param cr  The image to be drawn.
     */
    public static void drawImage(Graphics2D g2d,
                                 CachableRed cr) {

        // System.out.println("DrawImage G: " + g2d);

        ColorModel  srcCM = cr.getColorModel();

        AffineTransform at = null;
        while (true) {
            if (cr instanceof AffineRed) {
                AffineRed ar = (AffineRed)cr;
                if (at == null)
                    at = ar.getTransform();
                else
                    at.concatenate(ar.getTransform());
                cr = ar.getSource();
                continue;
            } else if (cr instanceof TranslateRed) {
                TranslateRed tr = (TranslateRed)cr;
                // System.out.println("testing Translate");
                int dx = tr.getDeltaX();
                int dy = tr.getDeltaY();
                if (at == null) 
                    at = AffineTransform.getTranslateInstance(dx, dy);
                else 
                    at.translate(dx, dy);
                cr = tr.getSource();
                continue;
            }
            break;
        }

        AffineTransform g2dAt   = g2d.getTransform();
        if ((at == null) || (at.isIdentity()))
            at = g2dAt;
        else
            at.preConcatenate(g2dAt);
    
        if (false) {
            System.out.println("CR: " + cr);
            System.out.println("CRR: " + cr.getBounds());
        }

        // Scaling down so do it before color conversion.
        double determinant = at.getDeterminant();
        if (!at.isIdentity() && (determinant <= 1.0)) {
            if (at.getType() != AffineTransform.TYPE_TRANSLATION)
                cr = new AffineRed(cr, at, g2d.getRenderingHints());
            else {
                int xloc = cr.getMinX() + (int)at.getTranslateX();
                int yloc = cr.getMinY() + (int)at.getTranslateY();
                cr = new TranslateRed(cr, xloc, yloc);
            }
        }
    
        ColorSpace g2dCS = getDestinationColorSpace(g2d);
        if (g2dCS == null)
            // Assume device is sRGB
            g2dCS = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        if (g2dCS != srcCM.getColorSpace()) {
            // System.out.println("srcCS: " + srcCM.getColorSpace());
            // System.out.println("g2dCS: " + g2dCS);
            // System.out.println("sRGB: " +
            //                    ColorSpace.getInstance(ColorSpace.CS_sRGB));
            // System.out.println("LsRGB: " +
            //                    ColorSpace.getInstance
            //                    (ColorSpace.CS_LINEAR_RGB));
            if      (g2dCS == ColorSpace.getInstance(ColorSpace.CS_sRGB))
                cr = convertTosRGB(cr);
            else if (g2dCS == ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB))
                cr = convertToLsRGB(cr);
        }

        // Scaling up so do it after color conversion.
        if (!at.isIdentity() && (determinant > 1.0))
            cr = new AffineRed(cr, at, g2d.getRenderingHints());

        // Now CR is in device space, so clear the g2d transform.
        g2d.setTransform(IDENTITY);

        Rectangle crR  = cr.getBounds();
        Shape     clip = g2d.getClip();

        try { 
            Rectangle clipR;
            if (clip == null) {
                clip  = crR;
                clipR = crR;
            } else {
                clipR   = clip.getBounds();
            
                if (clipR.intersects(crR) == false)
                    return; // Nothing to draw...
                clipR = clipR.intersection(crR);
            }

            Rectangle gcR = getDestinationBounds(g2d);
            if (gcR != null) {
                if (clipR.intersects(gcR) == false)
                    return; // Nothing to draw...
                clipR = clipR.intersection(gcR);
            }

            if ( false) {
                // There has been a problem where the render tries to
                // request a zero pixel high region (due to a bug in the
                // complex clip handling).  I have at least temporarily
                // worked around this by changing the alpha state in
                // CompositeRable which changes code paths enough that the
                // renderer doesn't try to construct a zero height
                // SampleModel (which dies).
                //
                // However I suspect that this fix is fragile (other code
                // paths may trigger the bug), eventually we may need to
                // reinstate this code, which handles the clipping for the
                // Graphics2D.
                if ((clip != null) &&
                    !(clip instanceof Rectangle2D)) {

                    // This is now the clip in device space...
                    clip = g2d.getClip();

                    if (clip instanceof Rectangle2D)
                        // Simple clip rect...
                        cr = new PadRed(cr, clipR, PadMode.ZERO_PAD, null);
                    else {
                        // Complex clip...
                        // System.out.println("Clip:" + clip);
                        // System.out.println("ClipR: " + clipR);
                        // System.out.println("crR: " + cr.getBounds());
                        // System.out.println("at: " + at);

                        if (clipR.intersects(cr.getBounds()) == false)
                            return; // Nothing to draw...
                        clipR = clipR.intersection(cr.getBounds());

                        BufferedImage bi = new BufferedImage
                            (clipR.width, clipR.height,
                             BufferedImage.TYPE_BYTE_GRAY);

                        Graphics2D big2d = createGraphics
                            (bi, g2d.getRenderingHints());

                        big2d.translate(-clipR.x, -clipR.y);
                        big2d.setPaint(Color.white);
                        big2d.fill(clip);
                        big2d.dispose();

                        CachableRed cCr;
                        cCr = new BufferedImageCachableRed(bi, clipR.x, 
                                                           clipR.y);
                        cr     = new MultiplyAlphaRed     (cr, cCr);
                    }
                    g2d.setClip(null);
                }
            }

            srcCM = cr.getColorModel();
            ColorModel g2dCM = getDestinationColorModel(g2d);
            ColorModel drawCM = srcCM;
            if (g2dCM == null) {
                // If we can't find out about our device assume
                // it's not premultiplied (Just because this
                // seems to work for us!).
                drawCM = coerceColorModel(drawCM, false);
            } else if (drawCM.hasAlpha() && g2dCM.hasAlpha() &&
                       (drawCM.isAlphaPremultiplied() !=
                        g2dCM .isAlphaPremultiplied())) {
                drawCM = coerceColorModel(drawCM,
                                          g2dCM.isAlphaPremultiplied());
            }

            SampleModel srcSM = cr.getSampleModel();
            WritableRaster wr;
            wr = Raster.createWritableRaster(srcSM, new Point(0,0));
            BufferedImage bi = new BufferedImage
                (drawCM, wr, drawCM.isAlphaPremultiplied(), null);

            int xt0 = cr.getMinTileX();
            int xt1 = xt0+cr.getNumXTiles();
            int yt0 = cr.getMinTileY();
            int yt1 = yt0+cr.getNumYTiles();
            int tw  = srcSM.getWidth();
            int th  = srcSM.getHeight();

            Rectangle tR  = new Rectangle(0,0,tw,th);
            Rectangle iR  = new Rectangle(0,0,0,0);

            if (false) {
                System.out.println("CR: " + cr);
                System.out.println("CRR: " + crR + " TG: [" +
                                   xt0 +"," +
                                   yt0 +"," +
                                   xt1 +"," +
                                   yt1 +"] Off: " +
                                   cr.getTileGridXOffset() +"," +
                                   cr.getTileGridYOffset());
            }

            DataBuffer db = wr.getDataBuffer();
            int yloc = yt0*th+cr.getTileGridYOffset();
            int skip = (clipR.y-yloc)/th;
            if (skip <0) skip = 0;
            yt0+=skip;

            int xloc = xt0*tw+cr.getTileGridXOffset();
            skip = (clipR.x-xloc)/tw;
            if (skip <0) skip = 0;
            xt0+=skip;

            int endX = clipR.x+clipR.width-1;
            int endY = clipR.y+clipR.height-1;
            
            if (false) {
                System.out.println("clipR: " + clipR + " TG: [" +
                                   xt0 +"," +
                                   yt0 +"," +
                                   xt1 +"," +
                                   yt1 +"] Off: " +
                                   cr.getTileGridXOffset() +"," +
                                   cr.getTileGridYOffset());
            }

            // System.out.println("Starting Draw: " + cr);
            long startTime = System.currentTimeMillis();

            yloc = yt0*th+cr.getTileGridYOffset();
            for (int y=yt0; y<yt1; y++, yloc += th) {
                if (yloc > endY) break;
                xloc = xt0*tw+cr.getTileGridXOffset();
                for (int x=xt0; x<xt1; x++, xloc+=tw) {
                    if (xloc > endX) break;
                    tR.x = xloc;
                    tR.y = yloc;
                    Rectangle2D.intersect(crR, tR, iR);
                    
                    WritableRaster twr;
                    twr = wr.createWritableChild(0, 0,
                                                 iR.width, iR.height,
                                                 iR.x, iR.y, null);

                    // System.out.println("Generating tile: " + twr);
                    cr.copyData(twr);
                    coerceData(twr, srcCM, drawCM.isAlphaPremultiplied());

                    // Make sure we only draw the region that was written...
                    BufferedImage subBI;
                    subBI = bi.getSubimage(0, 0, iR.width,  iR.height);
                    if (false) {
                        System.out.println("Drawing: " + tR);
                        System.out.println("IR: "      + iR);
                    }

                    AffineTransform trans;
                    trans = AffineTransform.getTranslateInstance(iR.x, iR.y);
                    g2d.drawImage(subBI, trans, null);
                    // big2d.fillRect(0, 0, tw, th);
                }
            }

            long endTime = System.currentTimeMillis();
            // System.out.println("Time: " + (endTime-startTime));
        } finally {
            g2d.setTransform(g2dAt);
        }
        // System.out.println("Finished Draw");
    }

    /**
     * Draws a <tt>Filter</tt> (<tt>RenderableImage</tt>) into a
     * Graphics 2D after taking into account a particular
     * <tt>RenderContext</tt>.<p>
     *
     * This method also attempts to unwind the rendering chain a bit.
     * So it knows about certain operations (like affine, pad,
     * composite), rather than applying each of these operations in
     * turn it accounts for there affects through modifications to the
     * Graphics2D. This avoids generating lots of intermediate images.
     *
     * @param g2d    The Graphics to draw into.
     * @param filter The filter to draw
     * @param rc The render context that controls the drawing operation.
     */
    public static void drawImage(Graphics2D      g2d,
                                 RenderableImage filter,
                                 RenderContext   rc) {

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

    /**
     * Draws a <tt>Filter</tt> (<tt>RenderableImage</tt>) into a
     * Graphics 2D.<p>
     *
     * This method also attempts to unwind the rendering chain a bit.
     * So it knows about certain operations (like affine, pad,
     * composite), rather than applying each of these operations in
     * turn it accounts for there affects through modifications to the
     * Graphics2D.  This avoids generating lots of intermediate images.
     *
     * @param g2d    The Graphics to draw into.
     * @param filter The filter to draw
     */
    public static void drawImage(Graphics2D g2d,
                                 RenderableImage filter) {
        if (filter instanceof PaintRable) {
            PaintRable pr = (PaintRable)filter;
            if (pr.paintRable(g2d))
                // paintRable succeeded so we are done...
                return;
        }

        // Get our sources image...
        // System.out.println("UnOpt: " + filter);
        AffineTransform at = g2d.getTransform();
        RenderedImage ri = filter.createRendering
            (new RenderContext(at, g2d.getClip(), g2d.getRenderingHints()));

        if (ri == null)
            return;

        g2d.setTransform(IDENTITY);
        drawImage(g2d, GraphicsUtil.wrap(ri));
        g2d.setTransform(at);
    }

    /**
     * This is a wrapper around the system's
     * BufferedImage.createGraphics that arranges for bi to be stored
     * in a Rendering hint in the returned Graphics2D.
     * This allows for accurate determination of the 'devices' size,
     * and colorspace.  

     * @param bi The BufferedImage that the returned Graphics should
     *           draw into.
     * @return A Graphics2D that draws into BufferedImage with <tt>bi</tt>
     *         stored in a rendering hint.
     */
    public static Graphics2D createGraphics(BufferedImage bi, 
                                            RenderingHints hints) {
        Graphics2D g2d = bi.createGraphics();
        if (hints != null)
            g2d.addRenderingHints(hints);
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, bi);
        g2d.clip(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
        return g2d;
    }


    public static Graphics2D createGraphics(BufferedImage bi) {
        Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, bi);
        g2d.clip(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
        return g2d;
    }


    public final static boolean WARN_DESTINATION = true;

    public static BufferedImage getDestination(Graphics2D g2d) {
        Object o = g2d.getRenderingHint
            (RenderingHintsKeyExt.KEY_BUFFERED_IMAGE);
        if (o != null)
            return (BufferedImage)o;

        // Check if this is a BufferedImage G2d if so throw an error...
        GraphicsConfiguration gc = g2d.getDeviceConfiguration();
        GraphicsDevice gd = gc.getDevice();
        if (WARN_DESTINATION &&
            (gd.getType() == GraphicsDevice.TYPE_IMAGE_BUFFER))
            // throw new IllegalArgumentException
            System.out.println
                ("Graphics2D from BufferedImage lacks BUFFERED_IMAGE hint");

        return null;
    }

    public static ColorModel getDestinationColorModel(Graphics2D g2d) {
        BufferedImage bi = getDestination(g2d);
        if (bi != null)
            return bi.getColorModel();

        GraphicsConfiguration gc = g2d.getDeviceConfiguration();

        // We are going to a BufferedImage but no hint was provided
        // so we can't determine the destination Color Model.
        if (gc.getDevice().getType() == GraphicsDevice.TYPE_IMAGE_BUFFER)
            return null;

        return gc.getColorModel();
    }

    public static ColorSpace getDestinationColorSpace(Graphics2D g2d) {
        ColorModel cm = getDestinationColorModel(g2d);
        if (cm == null)
            return null;

        return cm.getColorSpace();
    }

    public static Rectangle getDestinationBounds(Graphics2D g2d) {
        BufferedImage bi = getDestination(g2d);
        if (bi != null)
            return new Rectangle(0, 0, bi.getWidth(), bi.getHeight());

        GraphicsConfiguration gc = g2d.getDeviceConfiguration();

        // We are going to a BufferedImage but no hint was provided
        // so we can't determine the destination bounds.
        if (gc.getDevice().getType() == GraphicsDevice.TYPE_IMAGE_BUFFER)
            return null;

        return gc.getBounds();
    }


    /**
     * Standard prebuilt Linear_sRGB color model with no alpha */
    public final static ColorModel Linear_sRGB =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_LINEAR_RGB), 24,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0x0, false,
                             DataBuffer.TYPE_INT);
    /**
     * Standard prebuilt Linear_sRGB color model with premultiplied alpha.
     */
    public final static ColorModel Linear_sRGB_Pre =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_LINEAR_RGB), 32,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0xFF000000, true,
                             DataBuffer.TYPE_INT);
    /**
     * Standard prebuilt Linear_sRGB color model with unpremultiplied alpha.
     */
    public final static ColorModel Linear_sRGB_Unpre =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_LINEAR_RGB), 32,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0xFF000000, false,
                             DataBuffer.TYPE_INT);

    /**
     * Standard prebuilt sRGB color model with no alpha.
     */
    public final static ColorModel sRGB =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_sRGB), 24,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0x0, false,
                             DataBuffer.TYPE_INT);
    /**
     * Standard prebuilt sRGB color model with premultiplied alpha.
     */
    public final static ColorModel sRGB_Pre =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_sRGB), 32,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0xFF000000, true,
                             DataBuffer.TYPE_INT);
    /**
     * Standard prebuilt sRGB color model with unpremultiplied alpha.
     */
    public final static ColorModel sRGB_Unpre =
        new DirectColorModel(ColorSpace.getInstance
                             (ColorSpace.CS_sRGB), 32,
                             0x00FF0000, 0x0000FF00,
                             0x000000FF, 0xFF000000, false,
                             DataBuffer.TYPE_INT);

    /**
     * Method that returns either Linear_sRGB_Pre or Linear_sRGB_UnPre
     * based on premult flag.
     * @param premult True if the ColorModel should have premultiplied alpha.
     * @return        a ColorMdoel with Linear sRGB colorSpace and
     *                the alpha channel set in accordance with
     *                <tt>premult</tt>
     */
    public static ColorModel makeLinear_sRGBCM(boolean premult) {
        if (premult)
            return Linear_sRGB_Pre;
        return Linear_sRGB_Unpre;
    }

    /**
     * Constructs a BufferedImage with a linear sRGB colorModel, and alpha.
     * @param width   The desired width of the BufferedImage
     * @param height  The desired height of the BufferedImage
     * @param premult The desired state of alpha premultiplied
     * @return        The requested BufferedImage.
     */
    public static BufferedImage makeLinearBufferedImage(int width,
                                                        int height,
                                                        boolean premult) {
        ColorModel cm = makeLinear_sRGBCM(premult);
        WritableRaster wr = cm.createCompatibleWritableRaster(width, height);
        return new BufferedImage(cm, wr, premult, null);
    }

    /**
     * This method will return a CacheableRed that has it's data in
     * the linear sRGB colorspace. If <tt>src</tt> is already in
     * linear sRGB then this method does nothing and returns <tt>src</tt>.
     * Otherwise it creates a transform that will convert
     * <tt>src</tt>'s output to linear sRGB and returns that CacheableRed.
     *
     * @param src The image to convert to linear sRGB.
     * @return    An equivilant image to <tt>src</tt> who's data is in
     *            linear sRGB.
     */
    public static CachableRed convertToLsRGB(CachableRed src) {
        ColorModel cm = src.getColorModel();
        ColorSpace cs = cm.getColorSpace();
        if (cs == ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB))
            return src;

        return new Any2LsRGBRed(src);
    }

    /**
     * This method will return a CacheableRed that has it's data in
     * the sRGB colorspace. If <tt>src</tt> is already in
     * sRGB then this method does nothing and returns <tt>src</tt>.
     * Otherwise it creates a transform that will convert
     * <tt>src</tt>'s output to sRGB and returns that CacheableRed.
     *
     * @param src The image to convert to sRGB.
     * @return    An equivilant image to <tt>src</tt> who's data is in sRGB.
     */
    public static CachableRed convertTosRGB(CachableRed src) {
        ColorModel cm = src.getColorModel();
        ColorSpace cs = cm.getColorSpace();
        if (cs == ColorSpace.getInstance(ColorSpace.CS_sRGB))
            return src;

        return new Any2sRGBRed(src);
    }

    /**
     * Convertes any RenderedImage to a CacheableRed.  <p>
     * If <tt>ri</tt> is already a CacheableRed it casts it down and
     * returns it.<p>
     *
     * In cases where <tt>ri</tt> is not already a CacheableRed it
     * wraps <tt>ri</tt> with a helper class.  The wrapped
     * CacheableRed "Pretends" that it has no sources since it has no
     * way of inteligently handling the dependency/dirty region calls
     * if it exposed the source.
     * @param ri The RenderedImage to convert.
     * @return   a CacheableRed that contains the same data as ri.
     */
    public static CachableRed wrap(RenderedImage ri) {
        if (ri instanceof CachableRed)
            return (CachableRed) ri;
        if (ri instanceof BufferedImage)
            return new BufferedImageCachableRed((BufferedImage)ri);
        return new RenderedImageCachableRed(ri);
    }

    /**
     * An internal optimized version of copyData designed to work on
     * Integer packed data with a SinglePixelPackedSampleModel.  Only
     * the region of overlap between src and dst is copied.
     *
     * Calls to this should be preflighted with is_INT_PACK_Data
     * on both src and dest (requireAlpha can be false).
     *
     * @param src The source of the data
     * @param dst The destination for the data.
     */
    public static void copyData_INT_PACK(Raster src, WritableRaster dst) {
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

    public static void copyData_FALLBACK(Raster src, WritableRaster dst) {
        // System.out.println("Fallback copyData");

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

    /**
     * Copies data from one raster to another. Only the region of
     * overlap between src and dst is copied.  <tt>Src</tt> and
     * <tt>Dst</tt> must have compatible SampleModels.
     *
     * @param src The source of the data
     * @param dst The destination for the data.
     */
    public static void copyData(Raster src, WritableRaster dst) {
        if (is_INT_PACK_Data(src.getSampleModel(), false) &&
            is_INT_PACK_Data(dst.getSampleModel(), false)) {
            copyData_INT_PACK(src, dst);
            return;
        }

        copyData_FALLBACK(src, dst);
    }

    /**
     * Creates a new raster that has a <b>copy</b> of the data in
     * <tt>ras</tt>.  This is highly optimized for speed.  There is
     * no provision for changing any aspect of the SampleModel.
     *
     * This method should be used when you need to change the contents
     * of a Raster that you do not "own" (ie the result of a
     * <tt>getData</tt> call).
     * @param ras The Raster to copy.
     * @return    A writable copy of <tt>ras</tt>
     */
    public static WritableRaster copyRaster(Raster ras) {
        return copyRaster(ras, ras.getMinX(), ras.getMinY());
    }


    /**
     * Creates a new raster that has a <b>copy</b> of the data in
     * <tt>ras</tt>.  This is highly optimized for speed.  There is
     * no provision for changing any aspect of the SampleModel.
     * However you can specify a new location for the returned raster.
     *
     * This method should be used when you need to change the contents
     * of a Raster that you do not "own" (ie the result of a
     * <tt>getData</tt> call).
     *
     * @param ras The Raster to copy.
     *
     * @param minX The x location for the upper left corner of the
     *             returned WritableRaster.
     *
     * @param minY The y location for the upper left corner of the
     *             returned WritableRaster.
     *
     * @return    A writable copy of <tt>ras</tt>
     */
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

    /**
     * Coerces <tt>ras</tt> to be writable.  The returned Raster continues to
     * reference the DataBuffer from ras, so modifications to the returned
     * WritableRaster will be seen in ras.<p>
     *
     * This method should only be used if you need a WritableRaster due to
     * an interface (such as to construct a BufferedImage), but have no
     * intention of modifying the contents of the returned Raster.  If
     * you have any doubt about other users of the data in <tt>ras</tt>,
     * use copyRaster (above).
     * @param ras The raster to make writable.
     * @return    A Writable version of ras (shares DataBuffer with
     *            <tt>ras</tt>).
     */
    public static WritableRaster makeRasterWritable(Raster ras) {
        return makeRasterWritable(ras, ras.getMinX(), ras.getMinY());
    }

    /**
     * Coerces <tt>ras</tt> to be writable.  The returned Raster continues to
     * reference the DataBuffer from ras, so modifications to the returned
     * WritableRaster will be seen in ras.<p>
     *
     * You can specify a new location for the returned WritableRaster, this
     * is especially useful for constructing BufferedImages which require
     * the Raster to be at (0,0).
     *
     * This method should only be used if you need a WritableRaster due to
     * an interface (such as to construct a BufferedImage), but have no
     * intention of modifying the contents of the returned Raster.  If
     * you have any doubt about other users of the data in <tt>ras</tt>,
     * use copyRaster (above).
     *
     * @param ras The raster to make writable.
     *
     * @param minX The x location for the upper left corner of the
     *             returned WritableRaster.
     *
     * @param minY The y location for the upper left corner of the
     *             returned WritableRaster.
     *
     * @return A Writable version of <tT>ras</tt> with it's upper left
     *         hand coordinate set to minX, minY (shares it's DataBuffer
     *         with <tt>ras</tt>).
     */
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

    /**
     * Create a new ColorModel with it's alpha premultiplied state matching
     * newAlphaPreMult.
     * @param cm The ColorModel to change the alpha premult state of.
     * @param newAlphaPreMult The new state of alpha premult.
     * @return   A new colorModel that has isAlphaPremultiplied()
     *           equal to newAlphaPreMult.
     */
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
