/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;

import org.apache.batik.gvt.filter.CompositeRule;

/**
 * This provides an implementation of all the composite rules in SVG.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class SVGComposite
    implements Composite {

    CompositeRule rule;

    public SVGComposite(CompositeRule rule) {
        this.rule = rule;
    }

    public CompositeContext createContext(ColorModel srcCM,
                                          ColorModel dstCM,
                                          RenderingHints hints) {
        switch (rule.getRule()) {
        case CompositeRule.RULE_OVER:
            return AlphaComposite.SrcOver.createContext(srcCM, dstCM, hints);
        case CompositeRule.RULE_IN:
            return AlphaComposite.SrcIn.  createContext(srcCM, dstCM, hints);
        case CompositeRule.RULE_OUT:
            return AlphaComposite.SrcOut. createContext(srcCM, dstCM, hints);

        case CompositeRule.RULE_ATOP:
            return new AtopCompositeContext(srcCM, dstCM);

        case CompositeRule.RULE_XOR:
            return new XorCompositeContext(srcCM, dstCM);

        case CompositeRule.RULE_ARITHMETIC:
            float [] coeff = rule.getCoefficients();
            return new ArithCompositeContext(coeff[0], coeff[1],
                                             coeff[2], coeff[3]);
        default:
            throw new UnsupportedOperationException
                ("Unknown composite rule requested.");
        }
        
    }

    public static class AtopCompositeContext 
        implements  CompositeContext {
        ColorModel srcCM, dstCM;
        AtopCompositeContext(ColorModel srcCM, ColorModel dstCM) {
            this.srcCM = srcCM;
            this.dstCM = dstCM;
        }

        public void dispose() { 
            srcCM = null;
            dstCM = null;
        }

        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int [] srcPix = null;
            int [] dstPix = null;

            int x=dstOut.getMinX();
            int w=dstOut.getWidth();

            int y0=dstOut.getMinY();
            int y1=y0 + dstOut.getHeight();

            ColorModel srcPreCM = srcCM;
            if (!srcCM.isAlphaPremultiplied()) {
                BufferedImage bi = new BufferedImage
                    (srcCM, (WritableRaster)src, false, null);
                srcPreCM = GaussianBlurOp.coerceData(bi, true);
            }
            ColorModel dstPreCM = dstCM;
            if (!dstCM.isAlphaPremultiplied()) {
                BufferedImage bi = new BufferedImage
                    (dstCM, (WritableRaster)dstIn, false, null);
                dstPreCM = GaussianBlurOp.coerceData(bi, true);
            }

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int srcM = dstPix[sp+3];
                    final int dstM = 256-srcPix[sp+3];
                    dstPix[sp] = (srcPix[sp]*srcM + dstPix[sp]*dstM)>>8; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + dstPix[sp]*dstM)>>8; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + dstPix[sp]*dstM)>>8; ++sp;
                    ++sp;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }

            if (!srcCM.isAlphaPremultiplied()) {
                BufferedImage bi = new BufferedImage
                    (srcPreCM, (WritableRaster)src, true, null);
                GaussianBlurOp.coerceData(bi, false);
            }
            if (!dstCM.isAlphaPremultiplied()) {
                BufferedImage bi = new BufferedImage
                    (dstPreCM, (WritableRaster)dstIn, true, null);
                GaussianBlurOp.coerceData(bi, false);
                if (dstIn != dstOut) {
                    bi = new BufferedImage
                        (dstPreCM, (WritableRaster)dstOut, true, null);
                    GaussianBlurOp.coerceData(bi, false);
                }
            }
        }
    }

    public static class XorCompositeContext 
        implements  CompositeContext {
        ColorModel srcCM, dstCM;

        XorCompositeContext(ColorModel srcCM, ColorModel dstCM) {
            this.srcCM = srcCM;
            this.dstCM = dstCM;
        }

        public void dispose() { 
            srcCM = null;
            dstCM = null;
        }

        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int [] srcPix = null;
            int [] dstPix = null;

            int x=dstOut.getMinX();
            int w=dstOut.getWidth();

            int y0=dstOut.getMinY();
            int y1=y0 + dstOut.getHeight();

            ColorModel srcPreCM = srcCM;
            if (!srcCM.isAlphaPremultiplied()) {
                BufferedImage bi = new BufferedImage
                    (srcCM, (WritableRaster)src, false, null);
                srcPreCM = GaussianBlurOp.coerceData(bi, true);
            }
            ColorModel dstPreCM = dstCM;
            if (!dstCM.isAlphaPremultiplied()) {
                BufferedImage bi = new BufferedImage
                    (dstCM, (WritableRaster)dstIn, false, null);
                dstPreCM = GaussianBlurOp.coerceData(bi, true);
            }

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int srcA = srcPix[sp+3];
                    final int dstA = dstPix[sp+3];
                    if (dstA == 0) {
                        if (srcA == 0) {
                            dstPix[sp] = 0; ++sp;
                            dstPix[sp] = 0; ++sp;
                            dstPix[sp] = 0; ++sp;
                            dstPix[sp] = 0; ++sp;
                        } else {
                            dstPix[sp] = srcPix[sp]; ++sp;
                            dstPix[sp] = srcPix[sp]; ++sp;
                            dstPix[sp] = srcPix[sp]; ++sp;
                            dstPix[sp] = srcPix[sp]; ++sp;
                        }
                    } else {
                        if (srcA == 0) {
                            dstPix[sp] = dstPix[sp]; ++sp;
                            dstPix[sp] = dstPix[sp]; ++sp;
                            dstPix[sp] = dstPix[sp]; ++sp;
                            dstPix[sp] = dstPix[sp]; ++sp;
                        } else {
                            final int srcM = 256-dstA;
                            final int dstM = 256-srcA;

                            dstPix[sp] = (srcPix[sp]*srcM + 
                                          dstPix[sp]*dstM)>>8; ++sp;
                            dstPix[sp] = (srcPix[sp]*srcM + 
                                          dstPix[sp]*dstM)>>8; ++sp;
                            dstPix[sp] = (srcPix[sp]*srcM + 
                                          dstPix[sp]*dstM)>>8; ++sp;
                            dstPix[sp] = (srcPix[sp]*srcM + 
                                          dstPix[sp]*dstM)>>8; ++sp;
                        }
                    }
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }

            if (!srcCM.isAlphaPremultiplied()) {
                BufferedImage bi = new BufferedImage
                    (srcPreCM, (WritableRaster)src, true, null);
                GaussianBlurOp.coerceData(bi, false);
            }
            if (!dstCM.isAlphaPremultiplied()) {
                BufferedImage bi = new BufferedImage
                    (dstPreCM, (WritableRaster)dstIn, true, null);
                GaussianBlurOp.coerceData(bi, false);
                if (dstIn != dstOut) {
                    bi = new BufferedImage
                        (dstPreCM, (WritableRaster)dstOut, true, null);
                    GaussianBlurOp.coerceData(bi, false);
                }
            }
        }
    }

    public static class ArithCompositeContext 
        implements  CompositeContext {
        float k1, k2, k3, k4;
        ArithCompositeContext(float k1, float k2, float k3, float k4) {
            this.k1 = k1;
            this.k2 = k2;
            this.k3 = k3;
            this.k4 = k4;
        }

        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int [] srcPix = null;
            int [] dstPix = null;

            int x=dstOut.getMinX();
            int w=dstOut.getWidth();

            int y0=dstOut.getMinY();
            int y1=y0 + dstOut.getHeight();
            
            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                for (int i=0; i<srcPix.length; i++) {
                    dstPix[i] =(int)((k1*srcPix[i]*dstPix[i])/255 +
                                     k2*srcPix[i] + k3*dstPix[i] + 
                                     k4*255);
                    if      (dstPix[i] < 0)   dstPix[i] = 0;
                    else if (dstPix[i] > 255) dstPix[i] = 255;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }
        }

        public void dispose() { };
    }
}
