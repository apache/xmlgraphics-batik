/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.Attr;

/**
 * This interface should be implemented by all the attribute values
 * objects that must be updated when the attribute node is modified.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface LiveAttributeValue {
    /**
     * Called when the string representation of the value as been modified.
     * @param oldValue The old Attr node.
     * @param newValue The new Attr node.
     */
    void valueChanged(Attr oldValue, Attr newValue);
}
