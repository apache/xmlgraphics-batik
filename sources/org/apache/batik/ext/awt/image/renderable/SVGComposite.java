/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;

import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;

import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.PackedColorModel;
import java.awt.image.DataBufferInt;

import org.apache.batik.ext.awt.image.GraphicsUtil;

/**
 * This provides an implementation of all the composite rules in SVG.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class SVGComposite
    implements Composite {

    public static final SVGComposite OVER 
        = new SVGComposite(CompositeRule.OVER);
    
    public static final SVGComposite IN 
        = new SVGComposite(CompositeRule.IN);
    
    public static final SVGComposite OUT 
        = new SVGComposite(CompositeRule.OUT);
    
    public static final SVGComposite ATOP 
        = new SVGComposite(CompositeRule.ATOP);
    
    public static final SVGComposite XOR 
        = new SVGComposite(CompositeRule.XOR);
    
    public static final SVGComposite MULTIPLY 
        = new SVGComposite(CompositeRule.MULTIPLY);
    
    public static final SVGComposite SCREEN 
        = new SVGComposite(CompositeRule.SCREEN);
    
    public static final SVGComposite DARKEN 
        = new SVGComposite(CompositeRule.DARKEN);
    
    public static final SVGComposite LIGHTEN 
        = new SVGComposite(CompositeRule.LIGHTEN);
    

    CompositeRule rule;

    public CompositeRule getRule() { return rule; }

    public SVGComposite(CompositeRule rule) {
        this.rule = rule;
    }

    public boolean equals(Object o) {
        if (o instanceof SVGComposite) {
            SVGComposite svgc = (SVGComposite)o;
            return (svgc.getRule() == getRule());
        } else if (o instanceof AlphaComposite) {
            AlphaComposite ac = (AlphaComposite)o;
            switch (getRule().getRule()) {
            case CompositeRule.RULE_OVER:
                return (ac == AlphaComposite.SrcOver);
            case CompositeRule.RULE_IN:
                return (ac == AlphaComposite.SrcIn);
            case CompositeRule.RULE_OUT:
                return (ac == AlphaComposite.SrcOut);
            default:
                return false;
            }
        }
        return false;
    }

    public boolean is_INT_PACK(ColorModel cm) {
          // Check ColorModel is of type DirectColorModel
        if(!(cm instanceof PackedColorModel)) return false;

        PackedColorModel pcm = (PackedColorModel)cm;

        int [] masks = pcm.getMasks();

        // Check transfer type
        if(masks.length != 4) return false;

        if (masks[0] != 0x00ff0000) return false;
        if (masks[1] != 0x0000ff00) return false;
        if (masks[2] != 0x000000ff) return false;
        if (masks[3] != 0xff000000) return false;

        return true;
   }

    public CompositeContext createContext(ColorModel srcCM,
                                          ColorModel dstCM,
                                          RenderingHints hints) {
        if (false) {
            ColorSpace srcCS = srcCM.getColorSpace();
            ColorSpace dstCS = dstCM.getColorSpace();
            System.out.println("srcCS: " + srcCS);
            System.out.println("dstCS: " + dstCS);
            System.out.println
                ("lRGB: " + ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB));
            System.out.println
                ("sRGB: " + ColorSpace.getInstance(ColorSpace.CS_sRGB));
        }

        boolean use_int_pack = (is_INT_PACK(srcCM) && is_INT_PACK(dstCM));

        switch (rule.getRule()) {
        case CompositeRule.RULE_OVER:
            if (use_int_pack) 
                return new OverCompositeContext_INT_PACK(srcCM, dstCM);
            else
                return new OverCompositeContext(srcCM, dstCM);

        case CompositeRule.RULE_IN:
            return new InCompositeContext  (srcCM, dstCM);

        case CompositeRule.RULE_OUT:
            return new OutCompositeContext (srcCM, dstCM);

        case CompositeRule.RULE_ATOP:
            return new AtopCompositeContext(srcCM, dstCM);

        case CompositeRule.RULE_XOR:
            return new XorCompositeContext (srcCM, dstCM);

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

    public static class OverCompositeContext 
        extends AlphaPreCompositeContext {
        OverCompositeContext(ColorModel srcCM, ColorModel dstCM) {
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
            final int pt5  = (1<<23);

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int dstM = (255-srcPix[sp+3])*norm;
                    dstPix[sp] = srcPix[sp] + ((dstPix[sp]*dstM +pt5)>>>24);
                    ++sp;
                    dstPix[sp] = srcPix[sp] + ((dstPix[sp]*dstM +pt5)>>>24);
                    ++sp;
                    dstPix[sp] = srcPix[sp] + ((dstPix[sp]*dstM +pt5)>>>24);
                    ++sp;
                    dstPix[sp] = srcPix[sp] + ((dstPix[sp]*dstM +pt5)>>>24);
                    ++sp;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }

        }
    }

    public static class OverCompositeContext_INT_PACK 
        extends AlphaPreCompositeContext {
        OverCompositeContext_INT_PACK(ColorModel srcCM, ColorModel dstCM) {
            super(srcCM, dstCM);
        }

        public void precompose(Raster src, Raster dstIn, 
                               WritableRaster dstOut) {

            int x0=dstOut.getMinX();
            int w =dstOut.getWidth();

            int y0=dstOut.getMinY();
            int y1=y0 + dstOut.getHeight();

            SinglePixelPackedSampleModel srcSPPSM;
            srcSPPSM = (SinglePixelPackedSampleModel)src.getSampleModel();

            final int     srcScanStride = srcSPPSM.getScanlineStride();
            DataBufferInt srcDB         = (DataBufferInt)src.getDataBuffer();
            final int []  srcPixels     = srcDB.getBankData()[0];
            final int     srcBase =
                (srcDB.getOffset() +
                 srcSPPSM.getOffset(x0-src.getSampleModelTranslateX(),
                                    y0-src.getSampleModelTranslateY()));


            SinglePixelPackedSampleModel dstInSPPSM;
            dstInSPPSM = (SinglePixelPackedSampleModel)dstIn.getSampleModel();

            final int     dstInScanStride = dstInSPPSM.getScanlineStride();
            DataBufferInt dstInDB         = (DataBufferInt)dstIn.getDataBuffer();
            final int []  dstInPixels     = dstInDB.getBankData()[0];
            final int     dstInBase =
                (dstInDB.getOffset() +
                 dstInSPPSM.getOffset(x0-dstIn.getSampleModelTranslateX(),
                                      y0-dstIn.getSampleModelTranslateY()));

            SinglePixelPackedSampleModel dstOutSPPSM;
            dstOutSPPSM = (SinglePixelPackedSampleModel)dstOut.getSampleModel();

            final int     dstOutScanStride = dstOutSPPSM.getScanlineStride();
            DataBufferInt dstOutDB         = (DataBufferInt)dstOut.getDataBuffer();
            final int []  dstOutPixels     = dstOutDB.getBankData()[0];
            final int     dstOutBase =
                (dstOutDB.getOffset() +
                 dstOutSPPSM.getOffset(x0-dstOut.getSampleModelTranslateX(),
                                       y0-dstOut.getSampleModelTranslateY()));

            final int norm = (1<<24)/255;
            final int pt5  = (1<<23);

            final int   srcAdjust  =    srcScanStride - w;
            final int  dstInAdjust =  dstInScanStride - w;
            final int dstOutAdjust = dstOutScanStride - w;

            int srcSp    = srcBase;
            int dstInSp  = dstInBase;
            int dstOutSp = dstOutBase;

            int srcP, dstInP, dstM, a, r, g, b;

            for (int y = y0; y<y1; y++) {
                final int end = dstOutSp+w;
                while (dstOutSp<end) {
                    srcP   = srcPixels  [srcSp++];
                    dstInP = dstInPixels[dstInSp++];
                    
                    dstM = (255-(srcP>>>24))*norm;
                    a = ((     srcP & 0xFF000000) +
                         ((((dstInP>>>24)     )*dstM + pt5)     )) &0xFF000000;
                    r = ((     srcP & 0x00FF0000) +
                         ((((dstInP>> 16)&0xFF)*dstM + pt5)>>  8)) &0x00FF0000;
                    g = ((     srcP & 0x0000FF00) +
                         ((((dstInP>>  8)&0xFF)*dstM + pt5)>> 16)) &0x0000FF00;
                    b = ((     srcP & 0x000000FF) +
                         ((((dstInP     )&0xFF)*dstM + pt5)>>>24));

                    dstOutPixels[dstOutSp++] = (a|r|g|b);
                }
                srcSp    += srcAdjust;
                dstInSp  += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }

    public static class InCompositeContext 
        extends AlphaPreCompositeContext {
        InCompositeContext(ColorModel srcCM, ColorModel dstCM) {
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
            final int pt5  = (1<<23);

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int srcM = dstPix[sp+3]*norm;
                    dstPix[sp] = (srcPix[sp]*srcM + pt5)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + pt5)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + pt5)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + pt5)>>>24; ++sp;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
            }

        }
    }

    public static class OutCompositeContext 
        extends AlphaPreCompositeContext {
        OutCompositeContext(ColorModel srcCM, ColorModel dstCM) {
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
            final int pt5  = (1<<23);

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int srcM = (255-dstPix[sp+3])*norm;
                    dstPix[sp] = (srcPix[sp]*srcM + pt5)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + pt5)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + pt5)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + pt5)>>>24; ++sp;
                }
                dstOut.setPixels(x, y, w, 1, dstPix);
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
            final int pt5  = (1<<23);

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int srcM = (    dstPix[sp+3])*norm;
                    final int dstM = (255-srcPix[sp+3])*norm;
                    dstPix[sp] =(srcPix[sp]*srcM + dstPix[sp]*dstM +pt5)>>>24;
                    ++sp;
                    dstPix[sp] =(srcPix[sp]*srcM + dstPix[sp]*dstM +pt5)>>>24;
                    ++sp;
                    dstPix[sp] =(srcPix[sp]*srcM + dstPix[sp]*dstM +pt5)>>>24;
                    sp+=2;
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
            final int pt5  = (1<<23);

            for (int y = y0; y<y1; y++) {
                srcPix = src.getPixels  (x, y, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
                int sp  = 0;
                int end = w*4;
                while(sp<end) {
                    final int srcM = (255-dstPix[sp+3])*norm;
                    final int dstM = (255-srcPix[sp+3])*norm;

                    dstPix[sp] = (srcPix[sp]*srcM + 
                                  dstPix[sp]*dstM + pt5)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + 
                                  dstPix[sp]*dstM + pt5)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + 
                                  dstPix[sp]*dstM + pt5)>>>24; ++sp;
                    dstPix[sp] = (srcPix[sp]*srcM + 
                                  dstPix[sp]*dstM + pt5)>>>24; ++sp;
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
