/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.parser;

import java.io.Reader;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsHandler;
import org.apache.batik.parser.PointsParser;

/**
 * This class implements an event-based parser for the SVG points
 * attribute values (used with polyline and polygon elements).
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ConcretePointsParser
    extends    NumberParser
    implements PointsParser {
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
    public ConcretePointsParser() {
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
     * Parses the given reader.
     */
    public void parse(Reader r) throws ParseException {
	initialize(r);

	pointsHandler.startPoints();

	read();
	skipSpaces();

	loop: for (;;) {
	    if (current == -1) {
		break loop;
	    }
	    try {
		float x = parseFloat();
		skipCommaSpaces();
		float y = parseFloat();
		    
		pointsHandler.point(x, y);
	    } catch (NumberFormatException e) {
		reportError("float.format",
			    new Object[] { getBufferContent() });
	    }
	    skipCommaSpaces();
	}

	pointsHandler.endPoints();
    }

    /**
     * Implements {@link NumberParser#readNumber()}.
     */
    protected void readNumber() throws ParseException {
	bufferSize = 0;
	bufferize();
	eRead = false;
        for (;;) {
	    read();
	    switch (current) {
	    case 0x20:
	    case 0x9:
	    case 0xD:
	    case 0xA:
	    case ',':
		eRead = false;
		return;
	    case 'e': case 'E':
		eRead = true;
		bufferize();
		break;
	    case '+':
	    case '-':
		if (!eRead) {
		    return;
		}
		eRead = false;
		bufferize();
		break;
	    default:
		if (current == -1) {
		    return;
		}
		eRead = false;
		bufferize();
	    }
	}
    }
}
