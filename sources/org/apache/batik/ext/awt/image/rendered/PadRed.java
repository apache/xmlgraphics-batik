/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.ext.awt.image.renderable.PadMode;

import org.apache.batik.ext.awt.image.GraphicsUtil;

import java.awt.Point;
import java.awt.Rectangle;

import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;


/**
 * This is an implementation of a Pad operation as a RenderedImage.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class PadRed extends AbstractRed {

    static final boolean DEBUG=false;

    PadMode padMode;
    RenderingHints hints;

    /**
     * Construct A Rendered Pad operation.  If the pad is smaller than
     * the original image size then this devolves to a Crop.
     *
     * @param src     The image to pad/crop
     * @param bounds  The bounds of the result (same coord system as src).
     * @param padMode The pad mode to use (currently ignored).
     * @param hints The hints to use for drawing 'pad' area.
     */
    public PadRed(CachableRed    src,
                  Rectangle      bounds,
                  PadMode        padMode,
                  RenderingHints hints) {
        super(src,bounds,src.getColorModel(),
              fixSampleModel(src, bounds),
              bounds.x, bounds.y,
              null);

        this.padMode = padMode;

        if (DEBUG) {
            System.out.println("Src: " + src + " Bounds: " + bounds + 
                               " Off: " +
                               src.getTileGridXOffset() + ", " +
                               src.getTileGridYOffset());
        }
        this.hints = hints;

    }

    public WritableRaster copyData(WritableRaster wr) {
        // Get my source.
        CachableRed src = (CachableRed)getSources().get(0);

        Rectangle srcR = src.getBounds();
        Rectangle wrR  = wr.getBounds();

        if (wrR.intersects(srcR)) {
            Rectangle r = wrR.intersection(srcR);
        
            // Limit the raster I send to my source to his rect.
            WritableRaster srcWR;
            srcWR = wr.createWritableChild(r.x, r.y, r.width, r.height,
                                           r.x, r.y, null);
            src.copyData(srcWR);
        }

        if (padMode == PadMode.ZERO_PAD) {
            handleZero(wr);
        } else if (padMode == PadMode.REPLICATE) {
            handleReplicate(wr);
        } else if (padMode == PadMode.WRAP) {
            handleWrap(wr);
        }

        return wr;
    }

    protected static class ZeroRecter {
        WritableRaster wr;
        int bands;
        public ZeroRecter(WritableRaster wr) {
            this.wr = wr;
            this.bands = wr.getSampleModel().getNumBands();
        }
        public void zeroRect(Rectangle r) {
            int [] zeros = new int[r.width*bands];
            for (int y=0; y<r.height; y++) {
                wr.setPixels(r.x, r.y+y, r.width, 1, zeros);
            }
        }

        public static ZeroRecter getZeroRecter(WritableRaster wr) {
            if (GraphicsUtil.is_INT_PACK_Data(wr.getSampleModel(), false))
                return new ZeroRecter_INT_PACK(wr);
            else
                return new ZeroRecter(wr);
        }

    }

    protected static class ZeroRecter_INT_PACK extends ZeroRecter {
        final int base;
        final int scanStride;
        final int pixels[];

        public ZeroRecter_INT_PACK(WritableRaster wr) {
            super(wr);

            SinglePixelPackedSampleModel sppsm;
            sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();

            scanStride = sppsm.getScanlineStride();
            DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
            base = (db.getOffset() + 
                    sppsm.getOffset(0-wr.getSampleModelTranslateX(), 
                                    0-wr.getSampleModelTranslateY()));

            pixels = db.getBankData()[0];
        }

        public void zeroRect(Rectangle r) {
            final int rbase = base+r.x + r.y*scanStride;

            if ((r.width > 10) && (r.height>5)) {
                // Longer runs use arraycopy...
                int [] zeros = new int[r.width];

                // Access the pixel data array
                for (int y=0; y<r.height; y++) {
                    int sp = rbase + y*scanStride;
                    System.arraycopy(zeros, 0, pixels, sp, zeros.length);
                }
            } else {
                // Small runs quicker to avoid func call.
                for (int y=0; y<r.height; y++) {
                    int sp = rbase + y*scanStride;
                    final int end = sp + r.width;
                    while (sp < end)
                        pixels[sp++] = 0;
                }
            }
        }
    }

    protected void handleZero(WritableRaster wr) {
        // Get my source.
        CachableRed src  = (CachableRed)getSources().get(0);
        Rectangle   srcR = src.getBounds();
        Rectangle   wrR  = wr.getBounds();

        ZeroRecter zr = ZeroRecter.getZeroRecter(wr);

        // area rect (covers the area left to handle).
        Rectangle ar = new Rectangle(wrR.x, wrR.y, wrR.width, wrR.height);
        // draw rect (used for calls to zeroRect);
        Rectangle dr = new Rectangle(wrR.x, wrR.y, wrR.width, wrR.height);

        // We split the edge drawing up into four parts.
        //
        //  +-----------------------------+
        //  | 1    | 2                    |
        //  |      +---------------+------|
        //  /      /               /4     /
        //  /      /               /      /
        //  /      /               /      /
        //  /      /               /      /
        //  |      +---------------+------|
        //  |      |  3                   |
        //  +-----------------------------+
        //
        //  We update our x,y, width, height as we go along so
        //  we 'forget' about the parts we have already painted...

        // Draw #1
        if (DEBUG) {
            System.out.println("WrR: " + wrR + " srcR: " + srcR);
            // g2d.setColor(new Color(255,0,0,128));
        }
        if (ar.x < srcR.x) {
            int w = srcR.x-ar.x;
            if (w > ar.width) w=ar.width;
            // g2d.fillRect(x, y, w, height);
            dr.width = w;
            zr.zeroRect(dr);

            ar.x+=w;
            ar.width-=w;
        }

        // Draw #2
        if (DEBUG) {
            System.out.println("WrR: [" + 
                               ar.x + "," + ar.y + "," + 
                               ar.width + "," + ar.height + 
                               "] s rcR: " + srcR);
            // g2d.setColor(new Color(0,0,255,128));
        }
        if (ar.y < srcR.y) {
            int h = srcR.y-ar.y;
            if (h > ar.height) h=ar.height;
            // g2d.fillRect(x, y, width, h);
            dr.x      = ar.x;
            dr.y      = ar.y;
            dr.width  = ar.width;
            dr.height = h;
            zr.zeroRect(dr);

            ar.y     +=h;
            ar.height-=h;
        }

        // Draw #3
        if (DEBUG) {
            System.out.println("WrR: [" + 
                               ar.x + "," + ar.y + "," + 
                               ar.width + "," + ar.height + 
                               "] srcR: " + srcR);
            // g2d.setColor(new Color(0,255,0,128));
        }
        if (ar.y+ar.height > srcR.y+srcR.height) {
            int h = (ar.y+ar.height) - (srcR.y+srcR.height);
            if (h > ar.height) h=ar.height;

            int y0 = ar.y+ar.height-h; // the +/-1 cancel (?)

            // g2d.fillRect(x, y0, width, h);
            dr.x      = ar.x;
            dr.y      = y0;
            dr.width  = ar.width;
            dr.height = h;
            zr.zeroRect(dr);

            ar.height -= h;
        }

        // Draw #4
        if (DEBUG) {
            System.out.println("WrR: [" + 
                               ar.x + "," + ar.y + "," + 
                               ar.width + "," + ar.height + 
                               "] srcR: " + srcR);
            // g2d.setColor(new Color(255,255,0,128));
        }
        if (ar.x+ar.width > srcR.x+srcR.width) {
            int w = (ar.x+ar.width) - (srcR.x+srcR.width);
            if (w > ar.width) w=ar.width;
            int x0 = ar.x+ar.width-w; // the +/-1 cancel (?)

            // g2d.fillRect(x0, y, w, height);
            dr.x      = x0;
            dr.y      = ar.y;
            dr.width  = w;
            dr.height = ar.height;
            zr.zeroRect(dr);
            
            ar.width-=w;
        }
    }


    protected void handleReplicate(WritableRaster wr) {
        // Get my source.
        CachableRed src  = (CachableRed)getSources().get(0);
        Rectangle   srcR = src.getBounds();
        Rectangle   wrR  = wr.getBounds();

        int x      = wrR.x;
        int y      = wrR.y;
        int width  = wrR.width;
        int height = wrR.height;

        Rectangle   r;
        if (wrR.intersects(srcR))
            r = wrR.intersection(srcR);
        else
            r = new Rectangle(0,0,0,0);

        // We split the edge drawing up into four parts.
        //
        //  +-----------------------------+
        //  | 3    | 1             | 4    |
        //  |      +---------------+      |
        //  /      /               /      /
        //  /      / src           /      /
        //  /      /               /      /
        //  /      /               /      /
        //  |      +---------------+      |
        //  |      | 2             |      |
        //  +-----------------------------+
        // 

        // Draw #1
        if (y < srcR.y) {
            int repW = r.width;
            int repX = r.x;
            int wrX  = r.x;
            int wrY  = y;
            if (x+width-1 <= srcR.x) {
                // we are off to the left of src. so set repX to the
                // left most pixel...
                repW = 1;
                repX = srcR.x;
                wrX  = x+width-1;
            } else if (x >= srcR.x+srcR.width) {
                // we are off to the right of src, so set repX to
                // the right most pixel
                repW = 1;
                repX = srcR.x+srcR.width-1;
                wrX  = x;
            }

            // This fills the top row of section 1 from src (we
            // go to src instead of getting the data from wr because
            // in some cases wr will be completely off the top of src
            WritableRaster wr1 = wr.createWritableChild(wrX, wrY,
                                                        repW, 1, 
                                                        repX, srcR.y, null);
            src.copyData(wr1);
            wrY++;

            int endY = srcR.y;
            if (y+height < endY) endY = y+height;

            if (wrY < endY) {
                int [] pixels = wr.getPixels(wrX, wrY-1, 
                                             repW, 1, (int [])null);
                while (wrY < srcR.y) {
                    wr.setPixels(wrX, wrY, repW, 1, pixels);
                    wrY++;
                }
            }
        }
        
        // Draw #2
        if ((y+height) > (srcR.y+srcR.height)) {
            int repW = r.width;
            int repX = r.x;
            int repY = srcR.y+srcR.height-1;

            int wrX  = r.x;
            int wrY  = srcR.y+srcR.height;
            if (wrY < y) wrY = y;

            if (x+width <= srcR.x) {
                // we are off to the left of src. so set repX to the
                // left most pixel...
                repW = 1;
                repX = srcR.x;
                wrX  = x+width-1;
            } else if (x >= srcR.x+srcR.width) {
                // we are off to the right of src, so set repX to
                // the right most pixel
                repW = 1;
                repX = srcR.x+srcR.width-1;
                wrX  = x;
            }

            if (DEBUG) {
                System.out.println("wr: "  + wr.getBounds());
                System.out.println("req: [" + wrX + ", " + wrY + ", " + 
                                   repW + ", 1]");
            }

            // First we get the top row of pixels from src. (we
            // go to src instead of getting the data from wr because
            // in some cases wr will be completely off the bottom of src).
            WritableRaster wr1 = wr.createWritableChild(wrX, wrY,
                                                        repW, 1, 
                                                        repX, repY, null);
            // This fills the top row of section 2 from src 
            src.copyData(wr1);
            wrY++;

            int endY = y+height;
            if (wrY < endY) {
                // This fills the rest of section 2 from the first line.
                int [] pixels = wr.getPixels(wrX, wrY-1, 
                                             repW, 1, (int [])null);
                while (wrY < endY) {
                    wr.setPixels(wrX, wrY, repW, 1, pixels);
                    wrY++;
                }
            }
        }
                                                        
        // Draw #3
        if (x < srcR.x) {
            // We are garunteed that we have a column of pixels down
            // the edge of 1 and src.  We simply replicate this column
            // out to the edges of 2.
            int wrX = srcR.x;
            if (x+width <= srcR.x) {
                wrX = x+width-1;
            }

            int xLoc = x;
            int [] pixels = wr.getPixels(wrX, y, 1, height, (int [])null);
            while (xLoc < wrX) {
                wr.setPixels(xLoc, y, 1, height, pixels);
                xLoc++;
            }
        }

        // Draw #4
        if (x+width > srcR.x+srcR.width) {
            // We are garunteed that we have a column of pixels down
            // the edge of 1 and src.  We simply replicate this column
            // out to the edges of 3.
            int wrX = srcR.x+srcR.width-1;
            if (x >= srcR.x+srcR.width) {
                wrX = x;
            }

            int xLoc = wrX+1;
            int endX = x+width-1;
            int [] pixels = wr.getPixels(wrX, y, 1, height, (int [])null);
            while (xLoc < endX) {
                wr.setPixels(xLoc, y, 1, height, pixels);
                xLoc++;
            }
        }
    }

    protected void handleWrap(WritableRaster wr) {

        handleZero(wr);
    }

        /**
         * This function 'fixes' the source's sample model.
         * right now it just ensures that the sample model isn't
         * much larger than my width.
         */
    protected static SampleModel fixSampleModel(CachableRed src,
                                                Rectangle   bounds) {
        SampleModel sm = src.getSampleModel();
        int w = sm.getWidth();
        if (w < 256) w = 256;
        if      (w > bounds.width)  w = bounds.width;
        int h = sm.getHeight();
        if (h < 256) h = 256;
        if (h > bounds.height) h = bounds.height;

        // System.out.println("Pad SMSz: " + w + "x" + h);

        return sm.createCompatibleSampleModel(w, h);
    }
}
