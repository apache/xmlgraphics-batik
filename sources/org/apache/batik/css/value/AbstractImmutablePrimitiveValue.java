/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

/**
 * This class represents immutable primitive values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public abstract class AbstractImmutablePrimitiveValue
    extends AbstractImmutableValue {
    /**
     * A code defining the type of the value. 
     */
    public short getCssValueType() {
	return CSSValue.CSS_PRIMITIVE_VALUE;
    }

    /**
     * The type of the value as defined by the constants specified in
     * CSSPrimitiveValue.
     */
    public abstract short getPrimitiveType();
}
