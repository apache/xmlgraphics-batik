/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.ext.awt.image;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.ext.awt.image.renderable.PaintRable;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.xmlgraphics.image.rendered.BufferedImageCachableRed;
import org.apache.xmlgraphics.image.rendered.CachableRed;
import org.apache.xmlgraphics.image.rendered.FormatRed;
import org.apache.batik.ext.awt.image.rendered.TranslateRed;

/**
 * Set of utility methods for Graphics.
 * These generally bypass broken methods in Java2D or provide tweaked
 * implementations.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class GraphicsUtil extends org.apache.xmlgraphics.image.GraphicsUtil {

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

        ColorModel srcCM = cr.getColorModel();
        ColorModel g2dCM = getDestinationColorModel(g2d);
        ColorSpace g2dCS = null;
        if (g2dCM != null)
            g2dCS = g2dCM.getColorSpace();
        if (g2dCS == null)
            // Assume device is sRGB
            g2dCS = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        ColorModel drawCM = g2dCM;
        if ((g2dCM == null) || !g2dCM.hasAlpha()) {
            // If we can't find out about our device or the device
            // does not support alpha just use SRGB unpremultiplied
            // (Just because this seems to work for us).
            drawCM = sRGB_Unpre;
        }

        if (cr instanceof BufferedImageCachableRed) {
            // There is a huge win if we can use the BI directly here.
            // This results in something like a 10x performance gain
            // for images, the best thing is this is the common case.
            if (g2dCS.equals(srcCM.getColorSpace()) &&
                drawCM.equals(srcCM)) {
                // System.err.println("Fast Case");
                g2d.setTransform(at);
                BufferedImageCachableRed bicr;
                bicr = (BufferedImageCachableRed)cr;
                g2d.drawImage(bicr.getBufferedImage(),
                              bicr.getMinX(), bicr.getMinY(), null);
                g2d.setTransform(g2dAt);
                return;
            }
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
                cr = convertTosRGB(cr);
        }
        srcCM = cr.getColorModel();
        if (!drawCM.equals(srcCM))
            cr = FormatRed.construct(cr, drawCM);

        // Scaling up so do it after color conversion.
        if (!at.isIdentity() && (determinant > 1.0))
            cr = new AffineRed(cr, at, g2d.getRenderingHints());

        // Now CR is in device space, so clear the g2d transform.
        g2d.setTransform(IDENTITY);

        // Ugly Hack alert.  This Makes it use our SrcOver implementation
        // Which doesn't seem to have as many bugs as the JDK one when
        // going between different src's and destinations (of course it's
        // also a lot slower).
        Composite g2dComposite = g2d.getComposite();
        if (g2d.getRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING) ==
            RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING) {
            if (SVGComposite.OVER.equals(g2dComposite)) {
                g2d.setComposite(SVGComposite.OVER);
            }
        }
        Rectangle crR  = cr.getBounds();
        Shape     clip = g2d.getClip();

        try {
            Rectangle clipR;
            if (clip == null) {
                clip  = crR;
                clipR = crR;
            } else {
                clipR   = clip.getBounds();

                if ( ! clipR.intersects(crR) )
                    return; // Nothing to draw...
                clipR = clipR.intersection(crR);
            }

            Rectangle gcR = getDestinationBounds(g2d);
            // System.out.println("ClipRects: " + clipR + " -> " + gcR);
            if (gcR != null) {
                if ( ! clipR.intersects(gcR) )
                    return; // Nothing to draw...
                clipR = clipR.intersection(gcR);
            }

            // System.out.println("Starting Draw: " + cr);
            // long startTime = System.currentTimeMillis();

            boolean useDrawRenderedImage = false;

            srcCM = cr.getColorModel();
            SampleModel srcSM = cr.getSampleModel();
            if ((srcSM.getWidth()*srcSM.getHeight()) >=
                (clipR.width*clipR.height))
                // if srcSM tiles are around the clip size
                // then just draw the renderedImage
                useDrawRenderedImage = true;

            Object atpHint = g2d.getRenderingHint
                (RenderingHintsKeyExt.KEY_AVOID_TILE_PAINTING);

            if (atpHint == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_ON)
                useDrawRenderedImage = true; //for PDF and PS transcoders

            if (atpHint == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_OFF)
                useDrawRenderedImage = false;


            WritableRaster wr;
            if (useDrawRenderedImage) {
                // This can be significantly faster but can also
                // require much more memory, so we only use it when
                // the clip size is smaller than the tile size.
                Raster r = cr.getData(clipR);
                wr = ((WritableRaster)r).createWritableChild
                    (clipR.x, clipR.y, clipR.width, clipR.height,
                     0, 0, null);

                BufferedImage bi = new BufferedImage
                    (srcCM, wr, srcCM.isAlphaPremultiplied(), null);

                // Any of the drawImage calls that take an
                // Affine are prone to the 'CGGStackRestore: gstack
                // underflow' bug on Mac OS X.  This should work
                // around that problem.
                g2d.drawImage(bi, clipR.x, clipR.y, null);
            } else {
                // Use tiles to draw image...
                wr = Raster.createWritableRaster(srcSM, new Point(0,0));
                BufferedImage bi = new BufferedImage
                    (srcCM, wr, srcCM.isAlphaPremultiplied(), null);

                int xt0 = cr.getMinTileX();
                int xt1 = xt0+cr.getNumXTiles();
                int yt0 = cr.getMinTileY();
                int yt1 = yt0+cr.getNumYTiles();
                int tw  = srcSM.getWidth();
                int th  = srcSM.getHeight();

                Rectangle tR  = new Rectangle(0,0,tw,th);
                Rectangle iR  = new Rectangle(0,0,0,0);

                if (false) {
                    System.err.println("SrcCM: " + srcCM);
                    System.err.println("CR: " + cr);
                    System.err.println("CRR: " + crR + " TG: [" +
                                       xt0 + ',' +
                                       yt0 + ',' +
                                       xt1 + ',' +
                                       yt1 +"] Off: " +
                                       cr.getTileGridXOffset() + ',' +
                                       cr.getTileGridYOffset());
                }

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
                                       xt0 + ',' +
                                       yt0 + ',' +
                                       xt1 + ',' +
                                       yt1 +"] Off: " +
                                       cr.getTileGridXOffset() + ',' +
                                       cr.getTileGridYOffset());
                }


                yloc = yt0*th+cr.getTileGridYOffset();
                int minX = xt0*tw+cr.getTileGridXOffset();
                int xStep = tw;
                xloc = minX;
                for (int y=yt0; y<yt1; y++, yloc += th) {
                    if (yloc > endY) break;
                    for (int x=xt0; x<xt1; x++, xloc+=xStep) {
                        if ((xloc<minX) || (xloc > endX)) break;
                        tR.x = xloc;
                        tR.y = yloc;
                        Rectangle2D.intersect(crR, tR, iR);

                        WritableRaster twr;
                        twr = wr.createWritableChild(0, 0,
                                                     iR.width, iR.height,
                                                     iR.x, iR.y, null);

                        // System.out.println("Generating tile: " + twr);
                        cr.copyData(twr);

                        // Make sure we only draw the region that was written.
                        BufferedImage subBI;
                        subBI = bi.getSubimage(0, 0, iR.width,  iR.height);

                        if (false) {
                            System.out.println("Drawing: " + tR);
                            System.out.println("IR: "      + iR);
                        }

                        // For some reason using the transform version
                        // causes a gStackUnderflow error but if I just
                        // use the drawImage with an x & y it works.
                        g2d.drawImage(subBI, iR.x, iR.y, null);
                        // AffineTransform trans
                        //  = AffineTransform.getTranslateInstance(iR.x, iR.y);
                        // g2d.drawImage(subBI, trans, null);

                        // String label = "sub [" + x + ", " + y + "]: ";
                        // org.ImageDisplay.showImage
                        //     (label, subBI);
                    }
                    xStep = -xStep; // Reverse directions.
                    xloc += xStep;   // Get back in bounds.
                }
            }
            // long endTime = System.currentTimeMillis();
            // System.out.println("Time: " + (endTime-startTime));


        } finally {
            g2d.setTransform(g2dAt);
            g2d.setComposite(g2dComposite);
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
     * turn it accounts for their affects through modifications to the
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

        Shape clip = rc.getAreaOfInterest();
        if (clip != null)
            g2d.clip(clip);
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
     * turn it accounts for their affects through modifications to the
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
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE,
                             new WeakReference(bi));
        g2d.clip(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
        return g2d;
    }


    public static Graphics2D createGraphics(BufferedImage bi) {
        Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE,
                             new WeakReference(bi));
        g2d.clip(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
        return g2d;
    }


    public static final boolean WARN_DESTINATION;

    static {
        boolean warn = true;
        try {
            String s = System.getProperty
                ("org.apache.batik.warn_destination", "true");
            warn = Boolean.valueOf(s).booleanValue();
        } catch (SecurityException se) {
        } catch (NumberFormatException nfe) {
        } finally {
            WARN_DESTINATION = warn;
        }
    }

    public static BufferedImage getDestination(Graphics2D g2d) {
        Object o = g2d.getRenderingHint
            (RenderingHintsKeyExt.KEY_BUFFERED_IMAGE);
        if (o != null)
            return (BufferedImage)(((Reference)o).get());

        // Check if this is a BufferedImage G2d if so throw an error...
        GraphicsConfiguration gc = g2d.getDeviceConfiguration();
        GraphicsDevice gd = gc.getDevice();
        if (WARN_DESTINATION &&
            (gd.getType() == GraphicsDevice.TYPE_IMAGE_BUFFER) &&
            (g2d.getRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING) !=
                RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING))
            // throw new IllegalArgumentException
            System.err.println
                ("Graphics2D from BufferedImage lacks BUFFERED_IMAGE hint");

        return null;
    }

    public static ColorModel getDestinationColorModel(Graphics2D g2d) {
        BufferedImage bi = getDestination(g2d);
        if (bi != null)
            return bi.getColorModel();

        GraphicsConfiguration gc = g2d.getDeviceConfiguration();
        if (gc == null)
            return null; // Can't tell

        // We are going to a BufferedImage but no hint was provided
        // so we can't determine the destination Color Model.
        if (gc.getDevice().getType() == GraphicsDevice.TYPE_IMAGE_BUFFER) {
            if (g2d.getRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING) ==
                RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING)
                return sRGB_Unpre;

            // System.out.println("CM: " + gc.getColorModel());
            // System.out.println("CS: " + gc.getColorModel().getColorSpace());
            return null;
        }

        return gc.getColorModel();
    }

    public static ColorSpace getDestinationColorSpace(Graphics2D g2d) {
        ColorModel cm = getDestinationColorModel(g2d);
        if (cm != null) return cm.getColorSpace();

        return null;
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

        // This is a JDK 1.3ism, so we will just return null...
        // return gc.getBounds();
        return null;
    }
}
