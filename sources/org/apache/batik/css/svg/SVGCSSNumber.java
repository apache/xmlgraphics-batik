/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.w3c.dom.DOMException;

import org.w3c.dom.svg.SVGNumber;

/**
 * This class provides an implementation of SVGNumber.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGCSSNumber implements SVGNumber {
    
    /**
     * The value of this number.
     */
    protected float value;

    /**
     * Creates a new SVGCSSNumber.
     */
    public SVGCSSNumber(float f) {
        value = f;
    }

    /**
     * Returns the value of this float.
     */
    public float getValue() {
        return value;
    }

    /**
     * Sets the value of this number.
     */
    public void setValue(float f) {
        value = f;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SVGCSSNumber)) {
            return false;
        }
        SVGCSSNumber n = (SVGCSSNumber)obj;
        return value == n.value;
    }
    
    /**
     * Returns a printable representation of this object.
     */
    public String toString() {
        return "" + value;
    }
}
