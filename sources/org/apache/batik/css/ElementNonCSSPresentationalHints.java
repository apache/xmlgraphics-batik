/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This interface must be implemented by an element that contains
 * non-CSS presentational hints, like the 'align' attribute in HTML.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ElementNonCSSPresentationalHints {
    /**
     * Returns the translation of the non-CSS hints to the corresponding
     * CSS rules. The result can be null.
     */
    CSSStyleDeclaration getNonCSSPresentationalHints();
}
