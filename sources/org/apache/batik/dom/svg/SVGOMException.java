/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGException;

/**
 * An implementation of the SVGException class.
 *
 * @author <a href="mailto:tkormann@ilog.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGOMException extends SVGException {

    /**
     * Constructs a new <tt>SVGOMException</tt> with the specified parameters.
     *
     * @param code the exception code
     * @param message the error message
     */
    public SVGOMException(short code, String message) {
        super(code, message);
    }
}
