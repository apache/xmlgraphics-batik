/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

/**
 * This class provides an adapter for PointsHandler.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultPointsHandler implements PointsHandler {
    /**
     * The only instance of this class.
     */
    public final static DefaultPointsHandler INSTANCE
        = new DefaultPointsHandler();

    /**
     * This class does not need to be instantiated.
     */
    protected DefaultPointsHandler() {
    }

    /**
     * Implements {@link PointsHandler#startPoints()}.
     */
    public void startPoints() throws ParseException {
    }

    /**
     * Implements {@link PointsHandler#point(float,float)}.
     */
    public void point(float x, float y) throws ParseException {
    }

    /**
     * Implements {@link PointsHandler#endPoints()}.
     */
    public void endPoints() throws ParseException {
    }
}
