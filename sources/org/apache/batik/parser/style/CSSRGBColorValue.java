/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser.style;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;

/**
 * This class represents CSS color values
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSRGBColorValue
    extends    AbstractCSSValue
    implements RGBColor {
    /**
     * The red value
     */
    protected CSSPrimitiveValue red;

    /**
     * The green value
     */
    protected CSSPrimitiveValue green;

    /**
     * The blue value
     */
    protected CSSPrimitiveValue blue;

    /**
     * Creates a new color value.
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     */
    public CSSRGBColorValue(CSSPrimitiveValue r,
                            CSSPrimitiveValue g,
                            CSSPrimitiveValue b) {
        red   = r;
        green = g;
        blue  = b;
    }


    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValue#getCssValueType()}.
     */
    public short getCssValueType() {
        return CSSValue.CSS_PRIMITIVE_VALUE;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getPrimitiveType()}.
     */
    public short getPrimitiveType() {
	return CSSPrimitiveValue.CSS_RGBCOLOR;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValue#getCssText()}.
     */
    public String getCssText() {
        return "rgb("
            + red.getCssText() + ", "
            + green.getCssText() + ", "
            + blue.getCssText() + ")";
    }
    
    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.RGBColor#getRed()}.
     */
    public CSSPrimitiveValue getRed() {
        return red;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.RGBColor#getGreen()}.
     */
    public CSSPrimitiveValue getGreen() {
        return green;
    }
    
    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.RGBColor#getBlue()}.
     */
    public CSSPrimitiveValue getBlue() {
        return blue;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getRGBColorValue()}.
     */
    public RGBColor getRGBColorValue() throws DOMException {
        return this;
    }
}
