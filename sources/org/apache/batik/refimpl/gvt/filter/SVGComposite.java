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
import org.apache.batik.util.awt.image.GraphicsUtil;

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
            return new ArithCompositeContext(srcCM, dstCM,
                                             coeff[0], coeff[1],
                                             coeff[2], coeff[3]);

        case CompositeRule.RULE_MULTIPLY:
            return new MultiplyCompositeContext(srcCM, dstCM);

        case CompositeRule.RULE_SCREEN:
            return new ScreenCompositeContext(srcCM, dstCM);

        case CompositeRule.RULE_DARKEN:
            return new DarkenCompositeContext(srcCM, dstCM);

        case CompositeRule.RULE_LIGHTEN:
            return new LightenCompositeContext(srcCM, dstCM);

        default:
            throw new UnsupportedOperationException
                ("Unknown composite rule requested.");
        }
        
    }

    public static abstract class AlphaPreCompositeContext 
        implements CompositeContext {

        ColorModel srcCM, dstCM;
        AlphaPreCompositeContext(ColorModel srcCM, ColorModel dstCM) {
            this.srcCM = srcCM;
            this.dstCM = dstCM;
        }

        public void dispose() { 
            srcCM = null;
            dstCM = null;
        }

        protected abstract void precompose(Raster src, Raster dstIn, 
                                           WritableRaster dstOut);

        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            ColorModel srcPreCM = srcCM;
            if (!srcCM.isAlphaPremultiplied())
                srcPreCM = GraphicsUtil.coerceData((WritableRaster)src, 
                                                   srcCM, true);

            ColorModel dstPreCM = dstCM;
            if (!dstCM.isAlphaPremultiplied())
                dstPreCM = GraphicsUtil.coerceData((WritableRaster)dstIn,
                                                   dstCM, true);

            
            precompose(src, dstIn, dstOut);

            if (!srcCM.isAlphaPremultiplied())
                GraphicsUtil.coerceData((WritableRaster)src, 
                                        srcPreCM, false);

            if (!dstCM.isAlphaPremultiplied()) {
                GraphicsUtil.coerceData(dstOut, dstPreCM, false);
                
                if (dstIn != dstOut)
                    GraphicsUtil.coerceData((WritableRaster)dstIn, 
                                            dstPreCM, false);
            }
        }
    }

    public static class AtopCompositeContext 
        extends AlphaPreCompositeContext {
        AtopCompositeContext(ColorModel srcCM, ColorModel dstCM) {
            super(srcCM, dstCM);
        }

        public void precompose(Raster src, Raster dstIn, 
                               WritableRaster dstOut) {
            int [] srcPix = null;
            int [] dstPix = null;

            int x=dstOut.getMinX();
            int w=dstOut.getWidth();

            int y0=dstOut.getMinY();
            int y1=y0 + dstOut.getHeight();

            final int norm = (1<<24)/255;
            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int srcM = (    dstPix[sp+3])*norm;
                    final int dstM = (255-srcPix[sp+3])*norm;
                    dstPix[sp] =(srcPix[sp]*srcM + dstPix[sp]*dstM)>>>24; ++sp;
                    dstPix[sp] =(srcPix[sp]*srcM + dstPix[sp]*dstM)>>>24; ++sp;
                    dstPix[sp] =(srcPix[sp]*srcM + dstPix[sp]*dstM)>>>24; ++sp;
                    ++sp;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }

        }
    }

    public static class XorCompositeContext 
        extends AlphaPreCompositeContext {

        XorCompositeContext(ColorModel srcCM, ColorModel dstCM) {
            super(srcCM, dstCM);
        }

        public void precompose(Raster src, Raster dstIn, 
                               WritableRaster dstOut) {
            int [] srcPix = null;
            int [] dstPix = null;

            int x=dstOut.getMinX();
            int w=dstOut.getWidth();

            int y0=dstOut.getMinY();
            int y1=y0 + dstOut.getHeight();

            final int norm = (1<<24)/255;
            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int srcM = (255-dstPix[sp+3])*norm;
                    final int dstM = (255-srcPix[sp+3])*norm;

                    dstPix[sp] = (srcPix[sp]*srcM + 
                                  dstPix[sp]*dstM)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + 
                                  dstPix[sp]*dstM)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + 
                                  dstPix[sp]*dstM)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + 
                                  dstPix[sp]*dstM)>>>24; ++sp;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }

        }
    }

    public static class ArithCompositeContext 
        extends AlphaPreCompositeContext {
        float k1, k2, k3, k4;
        ArithCompositeContext(ColorModel srcCM,
                              ColorModel dstCM,
                              float k1, float k2, float k3, float k4) {
            super(srcCM, dstCM);
            this.k1 = k1;
            this.k2 = k2;
            this.k3 = k3;
            this.k4 = k4;
        }

        public void precompose(Raster src, Raster dstIn, 
                               WritableRaster dstOut) {
            int [] srcPix = null;
            int [] dstPix = null;

            int x=dstOut.getMinX();
            int w=dstOut.getWidth();

            int y0=dstOut.getMinY();
            int y1=y0 + dstOut.getHeight();
            
            float kk1 = k1/255.0f;
            float kk4 = k4*255.0f+0.5f;

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                for (int i=0; i<srcPix.length; i++) {
                    dstPix[i] =(int)((kk1*srcPix[i]*dstPix[i]) +
                                     k2*srcPix[i] + k3*dstPix[i] + kk4);
                    if      (dstPix[i] < 0)   dstPix[i] = 0;
                    else if (dstPix[i] > 255) dstPix[i] = 255;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }
        }
    }

    /**
     * The following classes implement the various blend modes from SVG.
     */
    public static class MultiplyCompositeContext 
        extends AlphaPreCompositeContext {

        MultiplyCompositeContext(ColorModel srcCM, ColorModel dstCM) {
            super(srcCM, dstCM);
        }

        public void precompose(Raster src, Raster dstIn, 
                               WritableRaster dstOut) {
            int [] srcPix = null;
            int [] dstPix = null;

            int x=dstOut.getMinX();
            int w=dstOut.getWidth();

            int y0=dstOut.getMinY();
            int y1=y0 + dstOut.getHeight();

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int srcM = 255-dstPix[sp+3];
                    final int dstM = 255-srcPix[sp+3];

                    dstPix[sp] = (srcPix[sp]*srcM + 
                                  dstPix[sp]*dstM +
                                  srcPix[sp]*dstPix[sp])/255; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + 
                                  dstPix[sp]*dstM +
                                  srcPix[sp]*dstPix[sp])/255; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + 
                                  dstPix[sp]*dstM +
                                  srcPix[sp]*dstPix[sp])/255; ++sp;
                    dstPix[sp] = (srcPix[sp] + dstPix[sp] -
                                  ((dstPix[sp]*srcPix[sp])/255)); ++sp;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }
        }
    }

    public static class ScreenCompositeContext 
        extends AlphaPreCompositeContext {

        ScreenCompositeContext(ColorModel srcCM, ColorModel dstCM) {
            super(srcCM, dstCM);
        }

        public void precompose(Raster src, Raster dstIn, 
                               WritableRaster dstOut) {
            int [] srcPix = null;
            int [] dstPix = null;

            int x=dstOut.getMinX();
            int w=dstOut.getWidth();

            int y0=dstOut.getMinY();
            int y1=y0 + dstOut.getHeight();

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    dstPix[sp] = (srcPix[sp] + dstPix[sp] -
                                  ((srcPix[sp]*dstPix[sp])/255)); ++sp;
                    dstPix[sp] = (srcPix[sp] + dstPix[sp] -
                                  ((srcPix[sp]*dstPix[sp])/255)); ++sp;
                    dstPix[sp] = (srcPix[sp] + dstPix[sp] -
                                  ((srcPix[sp]*dstPix[sp])/255)); ++sp;
                    dstPix[sp] = (srcPix[sp] + dstPix[sp] -
                                  ((dstPix[sp]*srcPix[sp])/255)); ++sp;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }
        }
    }

    public static class DarkenCompositeContext 
        extends AlphaPreCompositeContext {

        DarkenCompositeContext(ColorModel srcCM, ColorModel dstCM) {
            super(srcCM, dstCM);
        }

        public void precompose(Raster src, Raster dstIn, 
                               WritableRaster dstOut) {
            int [] srcPix = null;
            int [] dstPix = null;

            int x=dstOut.getMinX();
            int w=dstOut.getWidth();

            int y0=dstOut.getMinY();
            int y1=y0 + dstOut.getHeight();

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int alp = sp +3;
                    final int srcM = 255-dstPix[alp];
                    final int dstM = 255-srcPix[alp];

                    while (sp < alp) {
                        int t1 = ((srcM*srcPix[sp])/255) + dstPix[sp];
                        int t2 = ((dstM*dstPix[sp])/255) + srcPix[sp];
                        if (t1 > t2) dstPix[sp] = t2;
                        else         dstPix[sp] = t1;
                        ++sp;
                    }
                    dstPix[sp] = (srcPix[sp] + dstPix[sp] -
                                  ((dstPix[sp]*srcPix[sp])/255)); ++sp;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }
        }
    }

    public static class LightenCompositeContext 
        extends AlphaPreCompositeContext {

        LightenCompositeContext(ColorModel srcCM, ColorModel dstCM) {
            super(srcCM, dstCM);
        }

        public void precompose(Raster src, Raster dstIn, 
                               WritableRaster dstOut) {
            int [] srcPix = null;
            int [] dstPix = null;

            int x=dstOut.getMinX();
            int w=dstOut.getWidth();

            int y0=dstOut.getMinY();
            int y1=y0 + dstOut.getHeight();

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int alp = sp +3;
                    final int srcM = 255-dstPix[alp];
                    final int dstM = 255-srcPix[alp];
                    while (sp < alp) {
                        int t1 = ((srcM*srcPix[sp])/255) + dstPix[sp];
                        int t2 = ((dstM*dstPix[sp])/255) + srcPix[sp];
                        if (t1 > t2) dstPix[sp] = t1;
                        else         dstPix[sp] = t2;
                        ++sp;
                    }
                    dstPix[sp] = (srcPix[sp] + dstPix[sp] -
                                  ((dstPix[sp]*srcPix[sp])/255)); ++sp;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }
        }
    }
}
