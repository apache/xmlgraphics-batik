/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * Thrown when a required attribute is missing on a specific Element.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class MissingAttributeException extends RuntimeException {

    /**
     * Constructs a new <tt>MissingAttributeException</tt>.
     * @param msg the exception message
     * @param e the element on which the error occured
     */
    public MissingAttributeException(String msg) {
        super(msg);
    }
}
