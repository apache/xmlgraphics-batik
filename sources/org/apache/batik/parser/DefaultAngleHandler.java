/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

/**
 * This class provides an adapter for AngleHandler
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultAngleHandler implements AngleHandler {
    /**
     * The only instance of this class.
     */
    public final static AngleHandler INSTANCE
        = new DefaultAngleHandler();

    /**
     * This class does not need to be instantiated.
     */
    protected DefaultAngleHandler() {
    }

    /**
     * Implements {@link AngleHandler#startAngle()}.
     */
    public void startAngle() throws ParseException {
    }

    /**
     * Implements {@link AngleHandler#angleValue(float)}.
     */
    public void angleValue(float v) throws ParseException {
    }

    /**
     * Implements {@link AngleHandler#deg()}.
     */
    public void deg() throws ParseException {
    }

    /**
     * Implements {@link AngleHandler#grad()}.
     */
    public void grad() throws ParseException {
    }

    /**
     * Implements {@link AngleHandler#rad()}.
     */
    public void rad() throws ParseException {
    }

    /**
     * Implements {@link AngleHandler#endAngle()}.
     */
    public void endAngle() throws ParseException {
    }
}
