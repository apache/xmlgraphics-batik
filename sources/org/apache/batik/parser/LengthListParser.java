/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * This class implements an event-based parser for the SVG length
 * list values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LengthListParser extends LengthParser {

    /**
     * Creates a new LengthListParser.
     */
    public LengthListParser() {
	lengthHandler = DefaultLengthListHandler.INSTANCE;
    }

    /**
     * Allows an application to register a length list handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform list handler.
     */
    public void setLengthListHandler(LengthListHandler handler) {
	lengthHandler = handler;
    }

    /**
     * Returns the length list handler in use.
     */
    public LengthListHandler getLengthListHandler() {
	return (LengthListHandler)lengthHandler;
    }

    /**
     * Parses the given reader.
     */
    protected void doParse() throws ParseException {
	((LengthListHandler)lengthHandler).startLengthList();

	read();
	skipSpaces();
	
	try {
	    for (;;) {
		lengthHandler.startLength();
		parseLength();
		lengthHandler.endLength();
		skipSpaces();
		if (current == -1) {
		    break;
		}
	    }
	} catch (NumberFormatException e) {
	    reportError("float.format", new Object[] { getBufferContent() });
	}
	((LengthListHandler)lengthHandler).endLengthList();
    }
}
