/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.rendered.TurbulencePatternRed;

/**
 * Creates a sourceless image from a turbulence function.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class TurbulenceRable8Bit
    extends    AbstractColorInterpolationRable
    implements TurbulenceRable {

    int     seed          = 0;     // Seed value to pseudo rand num gen.
    int     numOctaves    = 1;     // number of octaves in turbulence function
    double  baseFreqX     = 0;     // Frequency in X/Y directions
    double  baseFreqY     = 0;
    boolean stitched       = false; // True if tiles are stitched
    boolean fractalNoise = false; // True if fractal noise should be used.

    Rectangle2D region;

    public TurbulenceRable8Bit(Rectangle2D region) {
        super();
        this.region = region;
    }

    public TurbulenceRable8Bit(Rectangle2D region,
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

        Rectangle2D aoiRect;
        Shape aoi = rc.getAreaOfInterest();
        if(aoi == null){
            aoiRect = getBounds2D();
        } else {
            Rectangle2D rect = getBounds2D();
            aoiRect          = aoi.getBounds2D();
            if (aoiRect.intersects(rect) == false)
                return null;
            Rectangle2D.intersect(aoiRect, rect, aoiRect);
        }

        AffineTransform usr2dev = rc.getTransform();

        // Compute size of raster image in device space.
        // System.out.println("Turbulence aoi : " + aoi);
        // System.out.println("Scale X : " + usr2dev.getScaleX() + " scaleY : " + usr2dev.getScaleY());
        // System.out.println("Turbulence aoi dev : " + usr2dev.createTransformedShape(aoi).getBounds());
        final Rectangle devRect
            = usr2dev.createTransformedShape(aoiRect).getBounds();

        if ((devRect.width <= 0) ||
            (devRect.height <= 0))
            return null;

        ColorSpace cs = getOperationColorSpace();
        
        Rectangle2D tile = null;
        if (stitched)
            tile = (Rectangle2D)region.clone();

        AffineTransform patternTxf = new AffineTransform();
        try{
            patternTxf = usr2dev.createInverse();
        }catch(NoninvertibleTransformException e){
        }

        return new TurbulencePatternRed
            (baseFreqX, baseFreqY, numOctaves, seed, fractalNoise, 
             tile, patternTxf, devRect, cs, true);
    }
}
