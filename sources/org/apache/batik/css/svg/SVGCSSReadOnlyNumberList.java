/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.w3c.dom.DOMException;

import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGNumber;

import org.w3c.dom.DOMException;

/**
 * This class provides a read-only SVG number list.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGCSSReadOnlyNumberList extends SVGCSSNumberList {
    /**
     * Clears this list.
     */
    public void clear() throws DOMException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
    }

    /**
     * Initializes this list with the given item.
     */
    public SVGNumber initialize(SVGNumber item)
        throws DOMException, SVGException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
    }

    /**
     * Inserts the given item at the given index.
     */
    public SVGNumber insertItemBefore(SVGNumber item, int index)
        throws DOMException, SVGException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
    }

    /**
     * Inserts the item at the given index.
     */
    public SVGNumber replaceItem(SVGNumber item, int index)
        throws DOMException, SVGException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
    }

    /**
     * Removes the item at the given index.
     */
    public SVGNumber removeItem(int index) throws DOMException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
    }
    
    /**
     * Appends the item at the given index.
     */
    public SVGNumber appendItem(SVGNumber item)
        throws DOMException, SVGException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
    }
}
