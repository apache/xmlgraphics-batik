/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.parser;

import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.ParseException;

/**
 * This class provides an adapter for LengthHandler
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultLengthHandler implements LengthHandler {
    /**
     * The only instance of this class.
     */
    public final static LengthHandler INSTANCE = new DefaultLengthHandler();

    /**
     * This class does not need to be instantiated.
     */
    protected DefaultLengthHandler() {
    }

    /**
     * Implements {@link LengthHandler#startLength()}.
     */
    public void startLength() throws ParseException {
    }

    /**
     * Implements {@link LengthHandler#lengthValue(float)}.
     */
    public void lengthValue(float v) throws ParseException {
    }

    /**
     * Implements {@link LengthHandler#em()}.
     */
    public void em() throws ParseException {
    }

    /**
     * Implements {@link LengthHandler#ex()}.
     */
    public void ex() throws ParseException {
    }

    /**
     * Implements {@link LengthHandler#in()}.
     */
    public void in() throws ParseException {
    }

    /**
     * Implements {@link LengthHandler#cm()}.
     */
    public void cm() throws ParseException {
    }

    /**
     * Implements {@link LengthHandler#mm()}.
     */
    public void mm() throws ParseException {
    }

    /**
     * Implements {@link LengthHandler#pc()}.
     */
    public void pc() throws ParseException {
    }

    /**
     * Implements {@link LengthHandler#pt()}.
     */
    public void pt() throws ParseException {
    }

    /**
     * Implements {@link LengthHandler#px()}.
     */
    public void px() throws ParseException {
    }

    /**
     * Implements {@link LengthHandler#percentage()}.
     */
    public void percentage() throws ParseException {
    }

    /**
     * Implements {@link LengthHandler#endLength()}.
     */
    public void endLength() throws ParseException {
    }
}
