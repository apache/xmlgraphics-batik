/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import java.awt.geom.Rectangle2D;

/**
 * Creates a sourceless image from a turbulence function.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public interface TurbulenceRable extends Filter {

    /**
     * Sets the turbulence region
     * @param TurbulenceRable region to fill with turbulence function.
     */
    public void setTurbulenceRegion(Rectangle2D turbulenceRegion);

    /**
     * Get the turbulence region
     */
     public Rectangle2D getTurbulenceRegion();

    /**
     * Get the current seed value for the pseudo random number generator.
     * @return The current seed value for the pseudo random number generator.
     */
    public int getSeed();

    /**
     * Get the current base fequency in x direction.
     * @return The current base fequency in x direction.
     */
    public double getBaseFrequencyX();

    /**
     * Get the current base fequency in y direction.
     * @return The current base fequency in y direction.
     */
    public double getBaseFrequencyY();

    /**
     * Get the current number of octaves for the noise function .
     * @return The current number of octaves for the noise function .
     */
    public int getNumOctaves();

    /**
     * Returns true if the turbulence function is currently stitching tiles.
     * @return true if the turbulence function is currently stitching tiles.
     */
    public boolean isStitched();

    /**
     * Returns true if the turbulence function is using fractile noise,
     * instead of turbulence noise.
     * @return true if the turbulence function is using fractile noise,
     * instead of turbulence noise.
     */
    public boolean isFractileNoise();

    /**
     * Set the seed value for the pseudo random number generator.
     * @param seed The new seed value for the pseudo random number generator.
     */
    public void setSeed(int seed);

    /**
     * Set the base fequency in x direction.
     * @param xfreq The new base fequency in x direction.
     */
    public void setBaseFrequencyX(double xfreq);

    /**
     * Set the base fequency in y direction.
     * @param yfreq The new base fequency in y direction.
     */
    public void setBaseFrequencyY(double yfreq);

    /**
     * Set the number of octaves for the noise function .
     * @param numOctaves The new number of octaves for the noise function .
     */
    public void setNumOctaves(int numOctaves);

    /**
     * Set stitching state for tiles.
     * @param stitched true if the turbulence operator should stitch tiles.
     */
    public void setStitched(boolean stitched);

    /**
     * Turns on/off fractile noise.
     * @param fractileNoise true if fractile noise should be used.
     */
    public void setFractileNoise(boolean fractileNoise);
}


