/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGElement;

/**
 * This interface represents objects that handle the modifications of a value.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ModificationHandler {

    /**
     * Called when the handled value has been modified.
     * @param object The modified object.
     * @param value The new value.
     */
    void valueChanged(Object object, String value);

    /**
     * Returns the object associated with the given key.
     * @param key The key to use to retreive the object.
     */
    Object getObject(Object key);
}
