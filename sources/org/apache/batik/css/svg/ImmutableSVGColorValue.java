/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.apache.batik.css.CSSOMValue;

import org.apache.batik.css.value.AbstractImmutableValue;
import org.apache.batik.css.value.ImmutableValue;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.RGBColor;

import org.w3c.dom.DOMException;

import org.w3c.dom.svg.SVGColor;
import org.w3c.dom.svg.SVGICCColor;
import org.w3c.dom.svg.SVGNumberList;

/**
 * This class represents the immutable value used to implement a SVGColor.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ImmutableSVGColorValue
    extends AbstractImmutableValue
    implements SVGImmutableValue,
               RGBColor,
               SVGICCColor {

    /**
     * The SVG color type.
     */
    protected short colorType;

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
     * The color profile.
     */
    protected String colorProfile;

    /**
     * The colors.
     */
    protected SVGCSSNumberList colors;

    /**
     * Creates a new ImmutableSVGColorValue.
     */
    public ImmutableSVGColorValue(short ctype,
                                  CSSPrimitiveValue r,
                                  CSSPrimitiveValue g,
                                  CSSPrimitiveValue b,
                                  String cprofile,
                                  SVGCSSNumberList l) {
        colorType = ctype;
        red = r;
        green = g;
        blue = b;
        colorProfile = cprofile;
        colors = l;
    }
    
    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj instanceof ImmutableSVGColorValue)) {
	    return false;
	}
	ImmutableSVGColorValue v = (ImmutableSVGColorValue)obj;
	if (colorType != v.colorType) {
            return false;
        }
        if (red != null) {
            if (!red.equals(v.red) ||
                !green.equals(v.green) ||
                !blue.equals(v.blue)) {
                return false;
            }
        } else if (v.red != null) {
            return false;
        }
        if (colorProfile != null) {
            if (colorProfile.equals(v.colorProfile)) {
                return false;
            }
        } else if (v.colorProfile != null) {
            return false;
        }
        if (colors != null) {
            return colors.equals(v.colors);
        } else if (v.colors != null) {
            return false;
        }
        return true;
    }

    /**
     * Returns a deep read-only copy of this object.
     */
    public ImmutableValue createReadOnlyCopy() {
	return new ImmutableSVGColorValue
            (colorType,
             (red == null) ? null : ((CSSOMValue)red).createReadOnlyCopy(),
             (green == null) ? null : ((CSSOMValue)green).createReadOnlyCopy(),
             (blue == null) ? null : ((CSSOMValue)blue).createReadOnlyCopy(),
             colorProfile,
             (colors == null) ? null : colors.createReadOnlyCopy());
    }

    /**
     *  A string representation of the current value. 
     */
    public String getCssText() {
        switch (colorType) {
        case SVGColor.SVG_COLORTYPE_RGBCOLOR:
            return "rgb("
                + red.getCssText() + ", "
                + green.getCssText() + ", "
                + blue.getCssText() + ")";
        case SVGColor.SVG_COLORTYPE_RGBCOLOR_ICCCOLOR:
            StringBuffer res = new StringBuffer();
            res .append("rgb(")
                .append(red.getCssText()).append(", ")
                .append(green.getCssText()).append(", ")
                .append(blue.getCssText()).append(") icc-color(")
                .append(colorProfile);
            if (colors.getNumberOfItems() != 0) {
                res.append(", ").append(colors.toString());
            }
            res.append(")");
            return res.toString();
        }
        throw CSSDOMExceptionFactory.createDOMException
            (DOMException.INVALID_ACCESS_ERR,
             "invalid.svgcolor.unit",
             new Object[] { new Integer(colorType) });
    }

    // SVGPaint /////////////////////////////////////////////////////////////

    /**
     * Returns the paint type, if this object represents a SVGPaint.
     */
    public short getPaintType() {
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR, "invalid.value", null);
    }

    /**
     * Returns the URI of the paint, if this object represents a SVGPaint.
     */
    public String getUri() {
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR, "invalid.value", null);
    }

    /**
     * Returns the color type, if this object represents a SVGColor.
     */
    public short getColorType() {
        return colorType;
    }

    /**
     * Returns the RGBColor, if this object represents a SVGColor.
     */
    public RGBColor getRGBColor() {
        return this;
    }

    /**
     * Returns the RGBColor, if this object represents a SVGColor.
     */
    public SVGICCColor getICCColor() {
        return this;
    }

    // RGBColor //////////////////////////////////////////////////////////////

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

    // SVGICCColor //////////////////////////////////////////////////////////

    /**
     * Returns the color profile of this ICC color.
     */
    public String getColorProfile() {
        return colorProfile;
    }

    /**
     * Sets the color profile of this ICC color.
     */
    public void setColorProfile(String colorProfile) throws DOMException {
    }

    /**
     * Returns the colors in this ICC color.
     */
    public SVGNumberList getColors() {
        return colors;
    }
}
