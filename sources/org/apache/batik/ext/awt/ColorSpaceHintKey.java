/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt;

import java.awt.RenderingHints;

/**
 * TranscodingHint as to what the destination of the drawing is.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public final class ColorSpaceHintKey extends RenderingHints.Key {

    /**
     * Notice to source that we prefer an Alpha RGB Image.
     */
    public static Object VALUE_COLORSPACE_ARGB  = new Object();

    /**
     * Notice to source that we will not use Alpha Channel but
     * we still want RGB data.
     */
    public static Object VALUE_COLORSPACE_RGB   = new Object();

    /**
     * Notice to source that we only want Greyscale data (no Alpha).
     */
    public static Object VALUE_COLORSPACE_GREY  = new Object();

    /**
     * Notice to source that we only want Greyscale data with
     * an alpha channel.
     */
    public static Object VALUE_COLORSPACE_AGREY = new Object();

    /**
     * Notice to source that we only want an alpha channel.
     * The source should simply render alpha (no conversion)
     */
    public static Object VALUE_COLORSPACE_ALPHA = new Object();

    /**
     * Notice to source that we only want an alpha channel.
     * The source should follow the SVG spec for how to
     * convert ARGB, RGB, Grey and AGrey to just an Alpha channel.
     */
    public static Object VALUE_COLORSPACE_ALPHA_CONVERT = new Object();

    public static final String PROPERTY_COLORSPACE =
        "org.apache.batik.gvt.filter.Colorspace";

    /** 
     * Note that this is package private.
     */
    ColorSpaceHintKey(int number) { super(number); }

    public boolean isCompatibleValue(Object val) {
        if (val == VALUE_COLORSPACE_ARGB)          return true;
        if (val == VALUE_COLORSPACE_RGB)           return true;
        if (val == VALUE_COLORSPACE_GREY)          return true;
        if (val == VALUE_COLORSPACE_AGREY)         return true;
        if (val == VALUE_COLORSPACE_ALPHA)         return true;
        if (val == VALUE_COLORSPACE_ALPHA_CONVERT) return true;
        return false;
    }
}

