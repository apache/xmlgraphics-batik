/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

/**
 * Defines the interface expected from a color matrix
 * operation
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface ColorMatrixRable extends FilterColorInterp {
    /**
     * Identifier used to refer to predefined matrices
     */
    public static final int TYPE_MATRIX             = 0;
    public static final int TYPE_SATURATE           = 1;
    public static final int TYPE_HUE_ROTATE         = 2;
    public static final int TYPE_LUMINANCE_TO_ALPHA = 3;

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
     * Returns the type of this color matrix. 
     * @return one of TYPE_MATRIX, TYPE_SATURATE, TYPE_HUE_ROTATE,
     *         TYPE_LUMINANCE_TO_ALPHA
     */
    public int getType();

    /**
     * Returns the rows of the color matrix. This uses
     * the same convention as BandCombineOp.
     */
    public float[][] getMatrix();
    
}
