/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.css.CSSOMValue;

import org.apache.batik.css.value.ImmutableValue;
import org.apache.batik.css.value.ValueFactory;

import org.w3c.dom.DOMException;

import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;

import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGICCColor;
import org.w3c.dom.svg.SVGPaint;

/**
 * This class represents a SVG CSS value.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGCSSValue
    extends    CSSOMValue
    implements SVGPaint {
    
    /**
     * Creates a new CSS value.
     */
    public SVGCSSValue(ValueFactory vf, ImmutableValue v) {
        super(vf, v);
    }

    /**
     * Returns a read-only copy of this value.
     */
    public CSSOMReadOnlyValue createReadOnlyCopy() {
	return new SVGCSSReadOnlyValue(value.createReadOnlyCopy());
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGPaint#getPaintType()}.
     */
    public short getPaintType() {
        return ((SVGImmutableValue)value).getPaintType();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGPaint#getUri()}.
     */
    public String getUri() {
        return ((SVGImmutableValue)value).getUri();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGPaint#setUri(String)}.
     */
    public void setUri(String uri) {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGPaint#setPaint(short,String,String,String)}.
     */
    public void setPaint (short paintType, String uri, String rgbColor,
                          String iccColor) throws SVGException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGColor#getColorType()}.
     */
    public short getColorType() {
        return ((SVGImmutableValue)value).getColorType();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGColor#getRGBColor()}.
     */
    public RGBColor getRGBColor() {
        return ((SVGImmutableValue)value).getRGBColor();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGColor#getICCColor()}.
     */
    public SVGICCColor getICCColor() {
        return ((SVGImmutableValue)value).getICCColor();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGColor#setRGBColor(String)}.
     */
    public void setRGBColor(String rgbColor) throws SVGException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGColor#setRGBColorICCColor(String,String)}.
     */
    public void setRGBColorICCColor(String rgbColor, String iccColor)
        throws SVGException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }
}
