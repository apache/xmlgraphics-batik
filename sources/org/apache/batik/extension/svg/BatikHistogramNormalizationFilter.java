/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.renderable.FilterColorInterpolation;
import org.apache.batik.ext.awt.image.renderable.Filter;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.ColorMatrixRed;

public interface BatikHistogramNormalizationFilter 
    extends FilterColorInterpolation {

    /**
     * Returns the source to be offset.
     */
    public Filter getSource();

    /**
     * Sets the source to be offset.
     * @param src image to offset.
     */
    public void setSource(Filter src);

    /**
     * Returns the trim percent for this normalization.
     */
    public float getTrim();


    /**
     * Sets the trim percent for this normalization.
     */
    public void setTrim(float trim);
}
