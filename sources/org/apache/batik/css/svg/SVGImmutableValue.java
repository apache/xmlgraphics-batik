/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.value.ImmutableValue;

import org.w3c.dom.css.RGBColor;

import org.w3c.dom.svg.SVGICCColor;

/**
 * This interface represents the immutable values used internally to
 * represents SVG CSS values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGImmutableValue extends ImmutableValue {

    /**
     * Returns the paint type, if this object represents a SVGPaint.
     */
    short getPaintType();

    /**
     * Returns the URI of the paint, if this object represents a SVGPaint.
     */
    String getUri();

    /**
     * Returns the color type, if this object represents a SVGColor.
     */
    short getColorType();

    /**
     * Returns the RGBColor, if this object represents a SVGColor.
     */
    RGBColor getRGBColor();

    /**
     * Returns the RGBColor, if this object represents a SVGColor.
     */
    SVGICCColor getICCColor();

}
