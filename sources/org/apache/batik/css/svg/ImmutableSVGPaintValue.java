/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.CSSOMValue;

import org.apache.batik.css.value.ImmutableValue;

import org.w3c.dom.css.CSSPrimitiveValue;

import org.w3c.dom.svg.SVGPaint;

/**
 * This class represents the immutable value used to implement a SVGPaint.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ImmutableSVGPaintValue extends ImmutableSVGColorValue {

    /**
     * The URI of this paint.
     */
    protected String uri;

    /**
     * Creates a new ImmutableSVGPaintValue.
     */
    public ImmutableSVGPaintValue(short ctype,
                                  CSSPrimitiveValue r,
                                  CSSPrimitiveValue g,
                                  CSSPrimitiveValue b,
                                  String cprofile,
                                  SVGCSSNumberList l,
                                  String url) {
        super(ctype, r, g, b, cprofile, l);
        uri = url;
    }
    
    /**
     * Returns the paint type, if this object represents a SVGPaint.
     */
    public short getPaintType() {
        return getColorType();
    }

    /**
     * Returns the URI of the paint, if this object represents a SVGPaint.
     */
    public String getUri() {
        return uri;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj instanceof ImmutableSVGPaintValue)) {
	    return false;
	}
	ImmutableSVGPaintValue v = (ImmutableSVGPaintValue)obj;
        if (!super.equals(obj)) {
            return false;
        }
        if (uri != null) {
            if (!uri.equals(v.uri)) {
                return false;
            }
        } else if (v.uri != null) {
            return false;
        }
        return true;
    }

    /**
     * Returns a deep read-only copy of this object.
     */
    public ImmutableValue createReadOnlyCopy() {
	return new ImmutableSVGPaintValue
            (colorType,
             (red == null) ? null : ((CSSOMValue)red).createReadOnlyCopy(),
             (green == null) ? null : ((CSSOMValue)green).createReadOnlyCopy(),
             (blue == null) ? null : ((CSSOMValue)blue).createReadOnlyCopy(),
             colorProfile,
             (colors == null) ? null : colors.createReadOnlyCopy(),
             uri);
    }

    /**
     *  A string representation of the current value. 
     */
    public String getCssText() {
        switch (colorType) {
        case SVGPaint.SVG_PAINTTYPE_URI_NONE:
            return "url('" + uri + "') none";
        case SVGPaint.SVG_PAINTTYPE_URI_CURRENTCOLOR:
            return "url('" + uri + "') currentColor";
        case SVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR:
            StringBuffer res = new StringBuffer();
            res .append("url('").append(uri)
                .append("') rgb(")
                .append(red.getCssText()).append(", ")
                .append(green.getCssText()).append(", ")
                .append(blue.getCssText()).append(")");
            return res.toString();
        case SVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR_ICCCOLOR:
            res = new StringBuffer();
            res .append("url('").append(uri)
                .append("') rgb(")
                .append(red.getCssText()).append(", ")
                .append(green.getCssText()).append(", ")
                .append(blue.getCssText()).append(") icc-color(")
                .append(colorProfile);
            if (colors.getNumberOfItems() != 0) {
                res.append(", ").append(colors.toString());
            }
            res.append(")");
            return res.toString();
        default:
            return super.getCssText();
        }
    }

}
