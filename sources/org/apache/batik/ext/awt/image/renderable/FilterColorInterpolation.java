/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.color.ColorSpace;

/**
 * This is an extension of our Filter interface that adds support for
 * a color-interpolation specification which indicates what colorspace the
 * operation should take place in.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public interface FilterColorInterpolation extends Filter {

    /**
     * Returns true if this operation is to be performed in
     * the linear sRGB colorspace, returns false if the
     * operation is performed in gamma corrected sRGB.
     */
    public boolean isColorSpaceLinear();

    /**
     * Sets the colorspace the operation will be performed in.
     * @param csLinear if true this operation will be performed in the
     * linear sRGB colorspace, if false the operation will be performed in
     * gamma corrected sRGB.
     */
    public void setColorSpaceLinear(boolean csLinear);

    /**
     * Returns the ColorSpace that the object will perform
     * it's work in.
     */
    public ColorSpace getOperationColorSpace();
}
