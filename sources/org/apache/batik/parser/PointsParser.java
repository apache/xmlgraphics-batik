/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

import java.io.Reader;

/**
 * This class implements an event-based parser for the SVG points
 * attribute values (used with polyline and polygon elements).
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PointsParser extends NumberParser {

    /**
     * The points handler used to report parse events.
     */
    protected PointsHandler pointsHandler;

    /**
     * Whether the last character was a 'e' or 'E'.
     */
    protected boolean eRead;

    /**
     * Creates a new PointsParser.
     */
    public PointsParser() {
	pointsHandler = DefaultPointsHandler.INSTANCE;
    }

    /**
     * Allows an application to register a points handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform list handler.
     */
    public void setPointsHandler(PointsHandler handler) {
	pointsHandler = handler;
    }

    /**
     * Returns the points handler in use.
     */
    public PointsHandler getPointsHandler() {
	return pointsHandler;
    }

    /**
     * Parses the given current stream.
     */
    protected void doParse() throws ParseException {
        pointsHandler.startPoints();

        read();
        skipSpaces();

        loop: for (;;) {
            if (current == -1) {
                break loop;
            }
            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();
		    
            pointsHandler.point(x, y);
            skipCommaSpaces();
        }

        pointsHandler.endPoints();
    }
}
