/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;

/**
 * Thrown when an attribute has an illegal value.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class IllegalAttributeValueException extends BridgeException {

    /**
     * Constructs a new <tt>IllegalAttributeValueException</tt>.
     * @param msg the exception message
     */
    public IllegalAttributeValueException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new <tt>IllegalAttributeValueException</tt>.
     * @param msg the exception message
     * @param node the graphics node on which the error occured
     */
    public IllegalAttributeValueException(String msg, GraphicsNode node) {
        super(msg, node);
    }
}
