/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.w3c.dom.DOMException;

/**
 * This class provides a read-only implementation of SVGNumber.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGCSSReadOnlyNumber extends SVGCSSNumber {
    
    /**
     * Creates a new SVGCSSReadOnlyNumber.
     */
    public SVGCSSReadOnlyNumber(float f) {
        super(f);
    }

    /**
     * Sets the value of this number.
     */
    public void setValue(float f) throws DOMException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
    }
}
