/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.parser;

import org.apache.batik.parser.ErrorHandler;
import org.apache.batik.parser.ParseException;

/**
 * This class provides a default implementation of ErrorHandler.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class DefaultErrorHandler implements ErrorHandler {
    /**
     * Implements {@link ErrorHandler#error(ParseException)}.
     * Throws the given exception.
     */
    public void error(ParseException e) throws ParseException {
	throw e;
    }
}
