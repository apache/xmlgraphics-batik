/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.io.IOException;
import java.io.Reader;

/**
 * This class represents a resizable input buffer.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class InputBuffer {

    /**
     * The buffer size.
     */
    protected final static int BUFFER_SIZE = 8192;

    /**
     * The reader.
     */
    protected Reader reader;

    /**
     * The current buffer index.
     */
    protected int bufferIndex;

    /**
     * The current buffer.
     */
    protected char[] buffer;

    /**
     * The buffers.
     */
    protected char[][] buffers = new char[][] {
	buffer = new char[BUFFER_SIZE], null, null, null, null
    };

    /**
     * The number of bytes in the current buffer.
     */
    protected int count;

    /**
     * The current position.
     */
    protected int position;

    /**
     * The current mark
     */
    protected int mark;

    /**
     * Whether a mark is set.
     */
    protected boolean markSet;

    /**
     * The current character.
     */
    protected int current;

    /**
     * The line number.
     */
    protected int line;

    /**
     * The column number.
     */
    protected int column;

    /**
     * Creates a new InputBuffer object.
     * @param r The reader used to read the document.
     */
    public InputBuffer(Reader r) throws IOException {
	reader = r;
	line = 1;
	column = 1;
       	count = reader.read(buffer, 0, BUFFER_SIZE);
	current = (count == -1) ? -1 : 0;
        position = -1;
        next();
    }

    /**
     * Returns the current character or -1.
     */
    public int current() {
	return current;
    }

    /**
     * Returns the line of the current character.
     */
    public int getLine() {
	return line;
    }

    /**
     * Returns the column of the current character.
     */
    public int getColumn() {
	return column;
    }

    /**
     * Go to the next character. A newline is reported with a single '\0xA'.
     * @return the new current character.
     */
    public int next() throws IOException {
	next(markSet);

	switch (current) {
	case 10:
	    break;
	case 13:
	    next(true);
	    switch (current) {
	    case 10:
                break;
	    default:
		if (position == 0) {
		    position = BUFFER_SIZE - 1;
                    bufferIndex--;
		} else {
		    position--;
		}
                current = 10;
		break;
	    case -1:
                line++;
                column = 1;
		return 10;
	    }
	    break;
	default:
	    column++;
	    return current;
	}
	line++;
	column = 1;

	return current;
    }

    /**
     * Go to the next character.
     * @param preserve true if the buffer must be preserved when a new
     *                 portion of the stream must be read.
     * @return the new current character.
     */
    protected void next(boolean preserve) throws IOException {
	if (current == -1) {
	    return;
	}
	position++;
	if (position < count) {
	    current = buffer[position];
	} else {
	    if (buffers[bufferIndex + 1] == buffer) {
		bufferIndex++;
		position = 0;
                current = buffer[0];
		return;
	    }
	    if (count < BUFFER_SIZE) {
		current = -1;
		return;
	    }
	    if (preserve) {
		bufferIndex++;
		if (bufferIndex == buffers.length) {
		    char[][] old = buffers;
		    buffers = new char[buffers.length*2][];
		    for (int i = 0; i < old.length; i++) {
			buffers[i] = old[i];
		    }
		}
		buffer = new char[BUFFER_SIZE];
		buffers[bufferIndex] = buffer;
		count = reader.read(buffer, 0, BUFFER_SIZE);
		position = 0;
	    } else {
		count = reader.read(buffer, 0, BUFFER_SIZE);
		position = 0;
	    }
	    current = (count == 0) ? -1 : buffer[0];
	}
    }

    /**
     * Sets a mark at the current position in the buffer.
     * @exception IllegalStateException if the mark was already set.
     */
    public void setMark() {
	if (markSet) {
	    throw new IllegalStateException("Mark already set");
	}
	markSet = true;
	mark = position;
    }

    /**
     * Resets a mark at the current position in the buffer.
     */
    public void resetMark() {
	if (bufferIndex != 0) {
	    buffer = buffers[bufferIndex];
	    buffers[0] = buffer;
	    for (int i = 1; i <= bufferIndex; i++) {
		buffers[i] = null;
	    }
	    bufferIndex = 0;
	}
	markSet = true;
	mark = position;
    }

    /**
     * Unsets the current mark.
     * @exception IllegalStateException if the mark was not previously set.
     */
    public void unsetMark() {
	if (!markSet) {
	    throw new IllegalStateException("Mark not set");
	}
	markSet = false;
	if (bufferIndex != 0) {
	    buffer = buffers[bufferIndex];
	    buffers[0] = buffer;
	    for (int i = 1; i <= bufferIndex; i++) {
		buffers[i] = null;
	    }
	    bufferIndex = 0;
	}
    }

    /**
     * The size of the content of the buffer, ie. from the mark to the
     * current position.
     * @exception IllegalStateException if the mark was not previously set.
     */
    public int contentSize() {
	if (!markSet) {
	    throw new IllegalStateException("Mark not set");
	}
	return (bufferIndex * BUFFER_SIZE) + position - mark + ((current != -1) ? 1 : 0);
    }

    /**
     * Returns the content of the buffer from the mark to the current
     * position.
     * @param a The array to fill with the content of the buffer.
     * @exception IllegalStateException if the mark was not previously set.
     * @exception IllegalArgumentException if the given array is too small.
     */
    public void readContent(char[] a) {
	if (!markSet) {
	    throw new IllegalStateException("Mark not set");
	}
	int size = contentSize();
	if (a.length < size) {
	    throw new IllegalArgumentException("a.length < contentSize()");
	}
	if (bufferIndex == 0) {
	    //System.arraycopy(buffers[0], mark, a, 0, size);
            char[] buffer = buffers[0];
            for (int i = size - 1; i >= 0; i--) {
                a[i] = buffer[i + mark];
            }
	} else {
	    int offset = BUFFER_SIZE - mark;
	    //System.arraycopy(buffers[0], mark, a, 0, offset);
            char[] buffer = buffers[0];
            for (int i = offset - 1; i >= 0; i--) {
                a[i] = buffer[i + mark];
            }
	    for (int i = 1; i < bufferIndex; i++) {
		//System.arraycopy(buffers[i], 0, a, offset, BUFFER_SIZE);
                buffer = buffers[i];
                for (int j = BUFFER_SIZE - 1; j >= 0; j--) {
                    a[j + offset] = buffer[j];
                }
		offset += BUFFER_SIZE;
	    }
	    size = position + ((current != -1) ? 1 : 0);
	    //System.arraycopy(buffers[bufferIndex], 0, a, offset, size);
            buffer = buffers[bufferIndex];
            for (int i = size - 1; i >= 0; i--) {
                a[i + offset] = buffer[i];
            }
	}
    }
}
