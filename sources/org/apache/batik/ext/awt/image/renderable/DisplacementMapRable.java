/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.util.List;

import org.apache.batik.ext.awt.image.ARGBChannel;

/**
 * Implements a DisplacementMap operation, which takes pixel values from
 * another image to spatially displace the input image
 *
 * @author <a href="mailto:sheng.pei@eng.sun.com>Sheng Pei</a>
 * @version $Id$
 */
public interface DisplacementMapRable extends FilterColorInterpolation {

    public static final int CHANNEL_R = 1;
    public static final int CHANNEL_G = 2;
    public static final int CHANNEL_B = 3;
    public static final int CHANNEL_A = 4;

    /**
     * The sources to be used in the displacement operation
     * The source at index 0 is displacement by the channels
     * in source at index 1 defined by the xChannelSelector
     * and the yChannelSelector. The displacement amount is
     * defined by the scale attribute.
     *
     * @param srcs The list of images used in the operation.
     */
    public void setSources(List srcs);

    /**
     * The displacement scale factor
     * @param scale can be any number.
     */
    public void setScale(double scale);

    /**
     * Returns the displacement scale factor
     */
    public double getScale();

    /**
     * Select which component values will be used
     * for displacement along the X axis
     * @param xChannelSelector value is among R,
     * G, B and A.
     */
    public void setXChannelSelector(ARGBChannel xChannelSelector);

    /**
     * Returns the xChannelSelector
     */
    public ARGBChannel getXChannelSelector();

    /**
     * Select which component values will be used
     * for displacement along the Y axis
     * @param yChannelSelector value is among R,
     * G, B and A.
     */
    public void setYChannelSelector(ARGBChannel yChannelSelector);

    /**
     * Returns the yChannelSelector
     */
    public ARGBChannel getYChannelSelector();

}
