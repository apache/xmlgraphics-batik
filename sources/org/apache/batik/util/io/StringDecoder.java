/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.io;

import java.io.IOException;

/**
 * This class reads a string.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StringDecoder implements CharDecoder {

    /**
     * The string which contains the decoded characters.
     */
    protected String string;

    /**
     * The number of chars in the string.
     */
    protected int length;

    /**
     * The next char index.
     */
    protected int next;

    /**
     * Creates a new StringDecoder.
     */
    public StringDecoder(String s) {
        string = s;
        length = s.length();
    }

    /**
     * Reads the next character.
     * @return a character or END_OF_STREAM.
     */
    public int readChar() throws IOException {
        if (next == length) {
            return END_OF_STREAM;
        }
        return string.charAt(next++);
    }

    /**
     * Disposes the associated resources.
     */
    public void dispose() throws IOException {
        string = null;
    }
}
