/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.TurbulenceRable;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;

/**
 * Creates a sourceless image from a turbulence function.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class ConcreteTurbulenceRable
    extends    AbstractRable
    implements TurbulenceRable {
    
    /**
     * Paint used to clear the area outside the area of interest
     */
    private static final Paint CLEAR_PAINT = new Color(0, 0, 0, 0);

    int     seed          = 0;     // Seed value to pseudo rand num gen.
    int     numOctaves    = 1;     // number of octaves in turbulence function
    double  baseFreqX     = 0;     // Frequency in X/Y directions
    double  baseFreqY     = 0;
    boolean stitched       = false; // True if tiles are stitched
    boolean fractalNoise = false; // True if fractal noise should be used.

    Rectangle2D region;

    public ConcreteTurbulenceRable(Rectangle2D region) {
        super();
        this.region = region;
    }

    public ConcreteTurbulenceRable(Rectangle2D region,
                                   int         seed,
                                   int         numOctaves,
                                   double      baseFreqX,
                                   double      baseFreqY,
                                   boolean     stitched,
                                   boolean     fractalNoise) {
        super();
        this.seed          = seed;
        this.numOctaves    = numOctaves;
        this.baseFreqX     = baseFreqX;
        this.baseFreqY     = baseFreqY;
        this.stitched      = stitched;
        this.fractalNoise  = fractalNoise;
        this.region        = region;
    }

    /**
     * Get the turbulence region
     */
    public Rectangle2D getTurbulenceRegion() {
        return (Rectangle2D)region.clone();
    }

    /**
     * Get the turbulence region
     */
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)region.clone();
    }

    /**
     * Get the current seed value for the pseudo random number generator.
     * @return The current seed value for the pseudo random number generator.
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Get the current number of octaves for the noise function .
     * @return The current number of octaves for the noise function .
     */
    public int getNumOctaves() {
        return numOctaves;
    }

    /**
     * Get the current base fequency in x direction.
     * @return The current base fequency in x direction.
     */
    public double getBaseFrequencyX() {
        return baseFreqX;
    }

    /**
     * Get the current base fequency in y direction.
     * @return The current base fequency in y direction.
     */
    public double getBaseFrequencyY() {
        return baseFreqY;
    }

    /**
     * Returns true if the turbulence function is currently stitching tiles.
     * @return true if the turbulence function is currently stitching tiles.
     */
    public boolean isStitched() {
        return stitched;
    }

    /**
     * Returns true if the turbulence function is using fractal noise,
     * instead of turbulence noise.
     * @return true if the turbulence function is using fractal noise,
     * instead of turbulence noise.
     */
    public boolean isFractalNoise() {
        return fractalNoise;
    }

    /**
     * Sets the turbulence region
     * @param TurbulenceRegion region to fill with turbulence function.
     */
    public void setTurbulenceRegion(Rectangle2D turbulenceRegion) {
        touch();
        this.region = region;
    }

    /**
     * Set the seed value for the pseudo random number generator.
     * @param seed The new seed value for the pseudo random number generator.
     */
    public void setSeed(int seed) {
        touch();
        this.seed = seed;
    }

    /**
     * Set the number of octaves for the noise function .
     * @param numOctaves The new number of octaves for the noise function .
     */
    public void setNumOctaves(int numOctaves) {
        touch();
        this.numOctaves = numOctaves;
    }

    /**
     * Set the base fequency in x direction.
     * @param baseFreqX The new base fequency in x direction.
     */
    public void setBaseFrequencyX(double baseFreqX) {
        touch();
        this.baseFreqX = baseFreqX;
    }

    /**
     * Set the base fequency in y direction.
     * @param baseFreqY The new base fequency in y direction.
     */
    public void setBaseFrequencyY(double baseFreqY) {
        touch();
        this.baseFreqY = baseFreqY;
    }

    /**
     * Set stitching state for tiles.
     * @param stitched true if the turbulence operator should stitch tiles.
     */
    public void setStitched(boolean stitched) {
        touch();
        this.stitched = stitched;
    }

    /**
     * Turns on/off fractal noise.
     * @param fractalNoise true if fractal noise should be used.
     */
    public void setFractalNoise(boolean fractalNoise) {
        touch();
        this.fractalNoise = fractalNoise;
    }

    public RenderedImage createRendering(RenderContext rc){
        Shape aoi = rc.getAreaOfInterest();
        if(aoi == null){
            aoi = getBounds2D();
        }

        AffineTransform usr2dev = rc.getTransform();

        // Compute size of raster image in device space.
        // System.out.println("Turbulence aoi : " + aoi);
        // System.out.println("Scale X : " + usr2dev.getScaleX() + " scaleY : " + usr2dev.getScaleY());
        // System.out.println("Turbulence aoi dev : " + usr2dev.createTransformedShape(aoi).getBounds());
        final Rectangle rasterRect 
            = usr2dev.createTransformedShape(aoi).getBounds();

        ColorModel cm = ColorModel.getRGBdefault();

        // Create a raster for the turbulence pattern
        WritableRaster wr = cm.createCompatibleWritableRaster(rasterRect.width, rasterRect.height);
        WritableRaster twr = wr.createWritableTranslatedChild(rasterRect.x, rasterRect.y);

        // Create a TurbulencePatternGenerator that will do the job
        // <!> FIX ME. The tile is not propagated properly to the turbulence op
        // <!> FIX ME. SHOULD OPTIMIZE THE CHANNELS REQUIRED FROM THE FILTER. THIS COULD
        //     BE ADDED TO THE RENDER CONTEXT.
        TurbulencePatternGenerator turbGenerator 
            = new TurbulencePatternGenerator(baseFreqX, baseFreqY, numOctaves,
                                             seed, stitched, fractalNoise, true,
                                             (Rectangle2D)region.clone(), new boolean[]{true, true, true, true});

        AffineTransform patternTxf = new AffineTransform();
        try{
            patternTxf = usr2dev.createInverse();
        }catch(NoninvertibleTransformException e){
        }

        turbGenerator.generatePattern(twr, patternTxf);

        // Wrap raster in buffered image
        BufferedImage bi = new BufferedImage(cm, wr, 
                                             cm.isAlphaPremultiplied(),
                                             null);

        // Clear area outside area of interest
        /*if(usr2dev.getShearX() != 0 || usr2dev.getShearY() != 0){
            Graphics2D g = bi.createGraphics();
            RenderingHints hints = rc.getRenderingHints();
            if(hints == null){
                hints = new RenderingHints(null);
            }
            g.setRenderingHints(hints);
            g.setComposite(AlphaComposite.Src);
        
            Area nonAoi = new Area(rasterRect);
            nonAoi.subtract(new Area(usr2dev.createTransformedShape(aoi)));
            g.setPaint(CLEAR_PAINT);
            // g.setPaint(java.awt.Color.red);
            g.translate(-rasterRect.x, -rasterRect.y);
            g.fill(nonAoi);
            g.dispose();
            }*/

        return new ConcreteBufferedImageCachableRed(bi, rasterRect.x,
                                                    rasterRect.y);
    }

    public RenderedImage createRenderingThomas(RenderContext rc) {
        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        // Map the area of interest to our input...
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null)
            aoi = getBounds2D();

        Rectangle2D r = getBounds2D().createIntersection(aoi.getBounds2D());

        // update the current affine transform
        AffineTransform at = rc.getTransform();
        if (at == null) at = new AffineTransform();

        // This splits out the scale and translate and applies them
        // prior to the Gaussian.  Then after appying the gaussian
        // it applies the shear component.
        // I derived the Matrix as such:
        // +-          -+   +-           -+    +-           -+
        // |  a   b   0 |   |  sx  0   0  |    |  sx shy  0  |
        // |  c   d   0 | * |  0   sy  0  | =  | shx  sy  0  |
        // |  e   f   1 |   |  tx  ty  1  |    |  tx  ty  1  |
        // +-          -+   +-           -+    +-           -+
        //
        // This gives the following sequence of equasions:
        //
        // a* sx       =  sx -> a = 1
        // b* sy       = shy -> b = shy/ sy
        // c* sx       = shx -> c = shx/ sx
        // d* sy       =  sy -> d = 1
        // e* sx +  tx =  tx -> e = 0
        // f* sy +  ty =  ty -> f = 0

        double sx = at.getScaleX();
        double sy = at.getScaleY();

        double tx = at.getTranslateX();
        double ty = at.getTranslateY();

        AffineTransform srcAt = AffineTransform.getTranslateInstance(tx, ty);
        srcAt.concatenate(AffineTransform.getScaleInstance(sx, sy));

        double shx = at.getShearX();
        double shy = at.getShearY();

        AffineTransform resAt;
        resAt = AffineTransform.getShearInstance(shx/sx, shy/sy);

        return null;
    }



}
