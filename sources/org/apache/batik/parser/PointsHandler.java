/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

/**
 * This interface must be implemented and then registred as the
 * handler of a <code>PointsParser</code> instance in order to be
 * notified of parsing events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface PointsHandler {
    /**
     * Invoked when the points attribute starts.
     * @exception ParseException if an error occured while processing the
     *                           points
     */
    void startPoints() throws ParseException;

    /**
     * Invoked when a point has been parsed.
     * @param x,&nbsp;y the coordinates of the point
     * @exception ParseException if an error occured while processing the
     *                           points
     */
    void point(float x, float y) throws ParseException;

    /**
     * Invoked when the points attribute ends.
     * @exception ParseException if an error occured while processing the
     *                           points
     */
    void endPoints() throws ParseException;
}
