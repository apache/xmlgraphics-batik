/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser.style;

import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

/**
 * This class is the abstract superclass of all factories.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractCSSValueFactory implements CSSValueFactory {
    /**
     * The 'inherit' value.
     */
    public final static CSSValue INHERIT = CSSInheritValue.INSTANCE;

    /**
     * The 'auto' identifier string representation.
     */
    public final static String AUTO = "auto";

    /**
     * The 'auto' identifier.
     */
    public final static CSSValue AUTO_VALUE =
        new CSSStringValue(CSSPrimitiveValue.CSS_IDENT, AUTO);

    /**
     * Creates a new AbstractCSSValueFactory object.
     */
    protected AbstractCSSValueFactory() {
    }
}
