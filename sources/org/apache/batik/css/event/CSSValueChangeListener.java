/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.event;

import org.w3c.dom.css.CSSValue;

/**
 * This interface must be implemented by the objects that must be
 * notified of CSS values changes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CSSValueChangeListener {
    /**
     * Called when a CSS value is changed.
     * @param property The name of the CSS property the value represents.
     * @param before The value before it changes.
     * @param after The value after it changes.
     */
    void cssValueChange(String property, CSSValue before, CSSValue after);
}
