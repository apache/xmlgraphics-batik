/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.parser;

import org.apache.batik.parser.LengthListHandler;
import org.apache.batik.parser.ParseException;

/**
 * This class provides an adapter for LengthListHandler
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultLengthListHandler
    extends    DefaultLengthHandler
    implements LengthListHandler {
    /**
     * The only instance of this class.
     */
    public final static LengthListHandler INSTANCE
        = new DefaultLengthListHandler();

    /**
     * This class does not need to be instantiated.
     */
    protected DefaultLengthListHandler() {
    }

    /**
     * Implements {@link LengthListHandler#startLengthList()}.
     */
    public void startLengthList() throws ParseException {
    }

    /**
     * Implements {@link LengthListHandler#endLengthList()}.
     */
    public void endLengthList() throws ParseException {
    }
}
