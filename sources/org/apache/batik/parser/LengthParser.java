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
 * This class implements an event-based parser for the SVG length
 * values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LengthParser extends NumberParser {

    /**
     * The length handler used to report parse events.
     */
    protected LengthHandler lengthHandler;

    /**
     * Whether the last character was a 'e' or 'E'.
     */
    protected boolean eRead;

    /**
     * Creates a new LengthParser.
     */
    public LengthParser() {
	lengthHandler = DefaultLengthHandler.INSTANCE;
    }

    /**
     * Allows an application to register a length handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform list handler.
     */
    public void setLengthHandler(LengthHandler handler) {
	lengthHandler = handler;
    }

    /**
     * Returns the length handler in use.
     */
    public LengthHandler getLengthHandler() {
	return lengthHandler;
    }

    /**
     * Parses the given reader.
     */
    public void parse(Reader r) throws ParseException {
	initialize(r);

	lengthHandler.startLength();

	read();
	skipSpaces();
	
	try {
	    parseLength();

	    skipSpaces();
	    if (current != -1) {
		reportError("end.of.stream.expected",
			    new Object[] { new Integer(current) });
	    }
	} catch (NumberFormatException e) {
	    reportError("float.format", new Object[] { getBufferContent() });
	}
	lengthHandler.endLength();
    }

    /**
     * Parses a length value.
     */
    protected void parseLength()
	throws ParseException,
	       NumberFormatException {
	float f = parseFloat();

	lengthHandler.lengthValue(f);
	
	s: if (eRead || current != -1) {
	    switch (current) {
	    case 0xD: case 0xA: case 0x20: case 0x9:
		break s;
	    }
	    
	    if (eRead) {
		switch (current) {
		case 'm':
		    lengthHandler.em();
		    read();
		    break;
		case 'x':
		    lengthHandler.ex();
		    read();
		    break;
		default:
		    reportError("character.unexpected",
				new Object[] { new Integer(current) });
		}
	    } else {
		switch (current) {
		case 'p':
		    read();
		    switch (current) {
		    case 'c':
			lengthHandler.pc();
			read();
			break;
		    case 't':
			lengthHandler.pt();
			read();
			break;
		    case 'x':
			lengthHandler.px();
			read();
			break;
		    default:
			reportError("character.unexpected",
				    new Object[] { new Integer(current) });
		    }
		    break;
		case 'i':
		    read();
		    if (current != 'n') {
			reportError("character.expected",
				    new Object[] { new Character('n'),
						   new Integer(current) });
			break;
		    }
		    lengthHandler.in();
		    read();
		    break;
		case 'c':
		    read();
		    if (current != 'm') {
			reportError("character.expected",
				    new Object[] { new Character('m'),
						   new Integer(current) });
			break;
		    }
		    lengthHandler.cm();
		    read();
		    break;
		case 'm':
		    read();
		    if (current != 'm') {
			reportError("character.expected",
				    new Object[] { new Character('m'),
						   new Integer(current) });
			break;
		    }
		    lengthHandler.mm();
		    read();
		    break;
		case '%':
		    lengthHandler.percentage();
		    read();
		    break;
		default:
		    reportError("character.unexpected",
				new Object[] { new Integer(current) });
		}
	    }
	}
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
	    case 'c':
	    case 'i':
	    case 'p':
	    case '%':
		return;
	    case 'e': case 'E':
		eRead = true;
		bufferize();
		break;
	    case 'm':
		if (!eRead) {
		    return;
		}
	    case 'x':
		bufferSize--;
		return;
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
