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
 * This filter primitive lights an image using the alpha channel as a bump map. 
 * The resulting image is an RGBA opaque image based on the light color
 * with alpha = 1.0 everywhere. The lighting calculation follows the standard diffuse
 * component of the Phong lighting model. The resulting image depends on the light color, 
 * light position and surface geometry of the input bump map.
 *
 * This filter follows the specification of the feDiffuseLighting filter in 
 * the SVG 1.0 specification.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public interface DiffuseLightingRable extends Filter {
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
     * @return diffuse constant, or kd.
     */
    public double getKd();

    /**
     * Sets the diffuse constant, or kd
     */
    public void setKd(double kd);

    /**
     * @return the litRegion for this filter
     */
    public Rectangle2D getLitRegion();

    /**
     * Sets the litRegion for this filter
     */
    public void setLitRegion(Rectangle2D litRegion);
}

