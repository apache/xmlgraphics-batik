/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSOMReadOnlyValue;

/**
 * This interface represents the context the application must provides
 * to the CSS engine in order to resolve the relative CSS values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CommonCSSContext {
    /**
     * The default color.
     */
    CSSOMReadOnlyValue getDefaultColorValue();

    /**
     * The font-family value.
     */
    CSSOMReadOnlyValue getDefaultFontFamilyValue();
}
