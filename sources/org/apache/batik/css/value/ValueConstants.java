/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.util.CSSConstants;

import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This interface defines the constants for CSS values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ValueConstants extends CSSConstants {
    /**
     * The 'inherit' value.
     */
    ImmutableValue INHERIT = ImmutableInherit.INSTANCE;

    /**
     * The 'auto' identifier.
     */
    ImmutableValue AUTO_VALUE =
        new ImmutableString(CSSPrimitiveValue.CSS_IDENT, AUTO);

    /**
     * The 'none' identifier value.
     */
    ImmutableValue NONE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, NONE);

    /**
     * The 'normal' identifier value.
     */
    ImmutableValue NORMAL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, NORMAL);

    
}
