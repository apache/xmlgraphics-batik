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
 * This class implements an event-based parser for the SVG angle
 * values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class AngleParser extends NumberParser {

    /**
     * The angle handler used to report parse events.
     */
    protected AngleHandler angleHandler = DefaultAngleHandler.INSTANCE;

    /**
     * Allows an application to register an angle handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform list handler.
     */
    public void setAngleHandler(AngleHandler handler) {
	angleHandler = handler;
    }

    /**
     * Returns the angle handler in use.
     */
    public AngleHandler getAngleHandler() {
	return angleHandler;
    }

    /**
     * Parses the given reader representing an angle.
     */
    public void parse(Reader r) throws ParseException {
	initialize(r);

	angleHandler.startAngle();

	read();
	skipSpaces();
	
	try {
	    float f = parseFloat();

	    angleHandler.angleValue(f);

	    s: if (current != -1) {
		switch (current) {
		case 0xD: case 0xA: case 0x20: case 0x9:
		    break s;
		}
		
		switch (current) {
		case 'd':
		    read();
		    if (current != 'e') {
			reportError("character.expected",
				    new Object[] { new Character('e'),
						   new Integer(current) });
			break;
		    }
		    read();
		    if (current != 'g') {
			reportError("character.expected",
				    new Object[] { new Character('g'),
						   new Integer(current) });
			break;
		    }
		    angleHandler.deg();
		    read();
		    break;
		case 'g':
		    read();
		    if (current != 'r') {
			reportError("character.expected",
				    new Object[] { new Character('r'),
						   new Integer(current) });
			break;
		    }
		    read();
		    if (current != 'a') {
			reportError("character.expected",
				    new Object[] { new Character('a'),
						   new Integer(current) });
			break;
		    }
		    read();
		    if (current != 'd') {
			reportError("character.expected",
				    new Object[] { new Character('d'),
						   new Integer(current) });
			break;
		    }
		    angleHandler.grad();
		    read();
		    break;
		case 'r':
		    read();
		    if (current != 'a') {
			reportError("character.expected",
				    new Object[] { new Character('a'),
						   new Integer(current) });
			break;
		    }
		    read();
		    if (current != 'd') {
			reportError("character.expected",
				    new Object[] { new Character('d'),
						   new Integer(current) });
			break;
		    }
		    angleHandler.rad();
		    read();
		    break;
		default:
		    reportError("character.unexpected",
				new Object[] { new Integer(current) });
		}
	    }

	    skipSpaces();
	    if (current != -1) {
		reportError("end.of.stream.expected",
			    new Object[] { new Integer(current) });
	    }
	} catch (NumberFormatException e) {
	    reportError("float.format", new Object[] { getBufferContent() });
	}
	angleHandler.endAngle();
    }

    /**
     * Implements {@link NumberParser#readNumber()}.
     */
    protected void readNumber() throws ParseException {
	bufferSize = 0;
	bufferize();
        for (;;) {
	    read();
	    switch (current) {
	    case 0x20:
	    case 0x9:
	    case 0xD:
	    case 0xA:
	    case 'd':
	    case 'g':
	    case 'r':
		return;
	    default:
		if (current == -1) {
		    return;
		}
		bufferize();
	    }
	}
    }
}
