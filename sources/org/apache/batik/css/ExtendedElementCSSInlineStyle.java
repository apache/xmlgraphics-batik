/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.w3c.dom.css.ElementCSSInlineStyle;

/**
 * This interface extends the {@link org.w3c.dom.css.ElementCSSInlineStyle}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ExtendedElementCSSInlineStyle extends ElementCSSInlineStyle {
    /**
     * Whether the element that implements this interface has a specified
     * style attribute.
     */
    boolean hasStyle();
}
