/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import java.awt.image.Kernel;

/**
 * Convolves an image with a convolution matrix.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public interface ConvolveMatrixRable extends Filter {

    /**
     * Returns the source to be Convolved
     */
    public Filter getSource();
    
    /**
     * Sets the source to be Convolved
     * @param src image to Convolved.
     */
    public void setSource(Filter src);


    /**
     * Returns the Convolution Kernel in use
     */
    public Kernel getKernel();

    /**
     * Sets the Convolution Kernel to use.
     * @param k Kernel to use for convolution.
     */
    public void setKernel(Kernel k);

    /**
     * Returns the shift value to apply to the result of convolution
     */
    public double getBias();
    
    /**
     * Returns the shift value to apply to the result of convolution
     */
    public void setBias(double bias);

    /**
     * Returns the current edge handling mode.
     */
    public PadMode getEdgeMode();
    
    /**
     * Sets the current edge handling mode.
     */
    public void setEdgeMode(PadMode edgeMode);
}
