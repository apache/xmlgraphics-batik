/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSOMValue;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.RGBColor;

/**
 * This class represents immutable CSS colors.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ImmutableRGBColor extends AbstractImmutablePrimitiveValue
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
     * Creates a new color.
     */
    public ImmutableRGBColor(CSSPrimitiveValue r,
			     CSSPrimitiveValue g,
			     CSSPrimitiveValue b) {
	red   = r;
	green = g;
	blue  = b;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj instanceof ImmutableRGBColor)) {
	    return false;
	}
	ImmutableRGBColor v = (ImmutableRGBColor)obj;
	return
	    red.equals(v.red) &&
	    green.equals(v.green) &&
	    blue.equals(v.blue);
    }

    /**
     * Returns a deep read-only copy of this object.
     */
    public ImmutableValue createReadOnlyCopy() {
	return new ImmutableRGBColor(((CSSOMValue)red).createReadOnlyCopy(),
				     ((CSSOMValue)green).createReadOnlyCopy(),
				     ((CSSOMValue)blue).createReadOnlyCopy());
    }

    /**
     *  A string representation of the current value. 
     */
    public String getCssText() {
	return "rgb("
	    + red.getCssText() + ", "
	    + green.getCssText() + ", "
	    + blue.getCssText() + ")";
    }

    /**
     * The type of the value as defined by the constants specified in
     * CSSPrimitiveValue.
     */
    public short getPrimitiveType() {
	return CSSPrimitiveValue.CSS_RGBCOLOR;
    }

    /**
     * This attribute is used for the red value of the RGB color. 
     */
    public CSSPrimitiveValue getRed() {
	return red;
    }

    /**
     * This attribute is used for the green value of the RGB color. 
     */
    public CSSPrimitiveValue getGreen() {
	return green;
    }
    
    /**
     * This attribute is used for the blue value of the RGB color. 
     */
    public CSSPrimitiveValue getBlue() {
	return blue;
    }

    /**
     * Returns this value.
     */
    public RGBColor getRGBColorValue() throws DOMException {
	return this;
    }
}
