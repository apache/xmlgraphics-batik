/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.image.Kernel;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.ConvolveMatrixRable;
import org.apache.batik.gvt.filter.Filter;

/**
 * Convolves an image with a convolution matrix.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class ConcreteConvolveMatrixRable 
    extends    AbstractRable 
    implements ConvolveMatrixRable
{

    Kernel kernel;
    double bias;
    PadMode edgeMode;

    ConcreteConvolveMatrixRable(Filter source) {
        super(source);
    }

    public Filter getSource() {
        return (Filter)getSources().get(0);
    }

    public void setSource(Filter src) {
        init(src);
    }


    /**
     * Returns the Convolution Kernel in use
     */
    public Kernel getKernel() {
        return kernel;
    }

    /**
     * Sets the Convolution Kernel to use.
     * @param k Kernel to use for convolution.
     */
    public void setKernel(Kernel k) {
        touch();
        this.kernel = k;
    }

    /**
     * Returns the shift value to apply to the result of convolution
     */
    public double getBias() {
        return bias;
    }
    
    /**
     * Returns the shift value to apply to the result of convolution
     */
    public void setBias(double bias) {
        touch();
        this.bias = bias;
    }

    /**
     * Returns the current edge handling mode.
     */
    public PadMode getEdgeMode() {
        return edgeMode;
    }
    
    /**
     * Sets the current edge handling mode.
     */
    public void setEdgeMode(PadMode edgeMode) {
        touch();
        this.edgeMode = edgeMode;
    }

    public RenderedImage createRendering(RenderContext rc) {
        return null;
    }
    
}
