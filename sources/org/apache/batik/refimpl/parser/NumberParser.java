/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.parser;

import java.io.IOException;
import org.apache.batik.parser.ParseException;

/**
 * This class represents a parser with support for numbers.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class NumberParser extends AbstractParser {
    /**
     * The buffer used for numbers.
     */
    protected char[] buffer = new char[16];

    /**
     * The buffer size.
     */
    protected int bufferSize;

    /**
     * Reads a number from the current stream and put it in the buffer.
     */
    protected abstract void readNumber() throws ParseException;

    /**
     * Parses the content of the buffer and converts it to a float.
     */
    protected float parseFloat()
	throws NumberFormatException,
	       ParseException {
	readNumber();
	return Float.parseFloat(getBufferContent());
    }

    /**
     * Returns the content of the buffer.
     */
    protected String getBufferContent() {
	return new String(buffer, 0, bufferSize);
    }

    /**
     * Adds the current character to the buffer.
     */
    protected void bufferize() {
	if (bufferSize >= buffer.length) {
	    char[] t = new char[buffer.length * 2];
	    for (int i = 0; i < bufferSize; i++) {
		t[i] = buffer[i];
	    }
	    buffer = t;
	}
	buffer[bufferSize++] = (char)current;
    }
}
