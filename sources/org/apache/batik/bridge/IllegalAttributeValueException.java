/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * Thrown when an attribute has an illegal value.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class IllegalAttributeValueException extends RuntimeException {

    /**
     * Constructs a new <tt>IllegalAttributeValueException</tt>.
     * @param msg the exception message
     * @param e the element on which the error occured
     */
    public IllegalAttributeValueException(String msg) {
        super(msg);
    }
}
