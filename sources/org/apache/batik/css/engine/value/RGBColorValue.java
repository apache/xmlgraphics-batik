/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class represents RGB colors.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class RGBColorValue extends AbstractValue {
    
    /**
     * The red component.
     */
    protected Value red;

    /**
     * The green component.
     */
    protected Value green;

    /**
     * The blue component.
     */
    protected Value blue;

    /**
     * Creates a new RGBColorValue.
     */
    public RGBColorValue(Value r, Value g, Value b) {
        red = r;
        green = g;
        blue = b;
    }

    /**
     * The type of the value.
     */
    public short getPrimitiveType() {
        return CSSPrimitiveValue.CSS_RGBCOLOR;
    }

    /**
     * A string representation of the current value. 
     */
    public String getCssText() {
        return "rgb(" +
            red.getCssText() + ", " +
            green.getCssText() + ", " +
            blue.getCssText() + ")";
    }

    /**
     * Implements {@link Value#getRed()}.
     */
    public Value getRed() throws DOMException {
        return red;
    }

    /**
     * Implements {@link Value#getGreen()}.
     */
    public Value getGreen() throws DOMException {
        return green;
    }

    /**
     * Implements {@link Value#getBlue()}.
     */
    public Value getBlue() throws DOMException {
        return blue;
    }

    /**
     * Returns a printable representation of the color.
     */
    public String toString() {
        return getCssText();
    }
}
