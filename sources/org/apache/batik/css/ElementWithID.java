/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

/**
 * This interface represents elements with an ID attribute.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ElementWithID {
    /**
     * Sets the element ID attribute name.
     * @param uri The ID attribute namespace URI.
     * @param s   The ID attribute local name.
     */
    void setIDName(String uri, String s);

    /**
     * Returns the ID of this element or the empty string.
     */
    String getID();
}
