/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.parser;

import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

/**
 * This class provides a default implementation of the
 * {@link ErrorHandler} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultErrorHandler implements ErrorHandler {

    /**
     * The instance of this class.
     */
    public final static ErrorHandler INSTANCE = new DefaultErrorHandler();

    /**
     * This class does not need to be instantiated.
     */
    protected DefaultErrorHandler() {
    }

    /**
     * <b>SAC</b>: Implements {ErrorHandler#warning(CSSParseException)}.
     */
    public void warning(CSSParseException e) {
        // Do nothing
    }

    /**
     * <b>SAC</b>: Implements {ErrorHandler#error(CSSParseException)}.
     */
    public void error(CSSParseException e) {
        // Do nothing
    }

    /**
     * <b>SAC</b>: Implements {ErrorHandler#fatalError(CSSParseException)}.
     */
    public void fatalError(CSSParseException e) {
        throw e;
    }
}
