/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSValue;

/**
 * This singleton class represents the 'inherit' value.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class InheritValue extends AbstractValue {
    /**
     * The only instance of this class.
     */
    public final static InheritValue INSTANCE = new InheritValue();
    
    /**
     * Creates a new InheritValue object.
     */
    protected InheritValue() {
    }

    /**
     *  A string representation of the current value. 
     */
    public String getCssText() {
	return "inherit";
    }

    /**
     * A code defining the type of the value. 
     */
    public short getCssValueType() {
	return CSSValue.CSS_INHERIT;
    }

    /**
     * Returns a printable representation of this object.
     */
    public String toString() {
        return getCssText();
    }
}
