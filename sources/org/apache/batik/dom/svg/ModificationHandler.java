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
     */
    void valueChanged(String value);

    /**
     * Returns the associated element.
     */
    SVGElement getSVGElement();

    /**
     * Creates a DOM exception with a localized message.
     * @param type The DOM exception type.
     * @param key The key of the message in the resource bundle.
     * @param args The message arguments.
     */
    DOMException createDOMException(short type, String key, Object[] args);
}
