/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser.style;

import org.w3c.dom.css.CSSValue;

/**
 * This class represents CSS 'inherit' values
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSInheritValue extends AbstractCSSValue {
    /**
     * The instance of this class.
     */
    public final static CSSInheritValue INSTANCE = new CSSInheritValue();

    /**
     * This class do not need to be instantiated.
     */
    protected CSSInheritValue() {
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValue#getCssValueType()}.
     */
    public short getCssValueType() {
        return CSSValue.CSS_INHERIT;
    }
    
    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValue#getCssText()}.
     */
    public String getCssText() {
	return "inherit";
    }
}
