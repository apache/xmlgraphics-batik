/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;

import org.w3c.dom.DOMException;

/**
 * This interface represents the values for properties like 'fill',
 * 'flood-color'...
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGValue extends Value {
    
    /**
     * Returns the paint type, if this object represents a SVGPaint.
     */
    short getPaintType() throws DOMException;

    /**
     * Returns the URI of the paint, if this object represents a SVGPaint.
     */
    String getUri() throws DOMException;

    /**
     * Returns the color type, if this object represents a SVGColor.
     */
    short getColorType() throws DOMException;

    /**
     * Returns the color profile, if this object represents a SVGColor.
     */
    String getColorProfile() throws DOMException;

    /**
     * Returns the number of colors, if this object represents a SVGColor.
     */
    int getNumberOfColors() throws DOMException;

    /**
     * Returns the color at the given index, if this object represents
     * a SVGColor.
     */
    float getColor(int i) throws DOMException;
}
