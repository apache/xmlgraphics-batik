/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;

import org.apache.batik.ext.awt.image.Light;

/**
 * This filter follows the specification of the feSpecularLighting filter in 
 * the SVG 1.0 specification.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public interface SpecularLightingRable extends Filter {
    /**
     * Returns the source to be filtered
     */
    public Filter getSource();

    /**
     * Sets the source to be filtered
     */
    public void setSource(Filter src);

    /**
     * @return Light object used for the diffuse lighting
     */
    public Light getLight();

    /**
     * @param New Light object
     */
    public void setLight(Light light);

    /**
     * @return surfaceScale
     */
    public double getSurfaceScale();

    /**
     * Sets the surface scale
     */
    public void setSurfaceScale(double surfaceScale);

    /**
     * @return specular constant, or ks.
     */
    public double getKs();

    /**
     * Sets the specular constant, or ks
     */
    public void setKs(double ks);

    /**
     * @return specular exponent, or kd
     */
    public double getSpecularExponent();

    /**
     * Sets the specular exponent
     */
    public void setSpecularExponent(double specularExponent);

    /**
     * @return the litRegion for this filter
     */
    public Rectangle2D getLitRegion();

    /**
     * Sets the litRegion for this filter
     */
    public void setLitRegion(Rectangle2D litRegion);
}

