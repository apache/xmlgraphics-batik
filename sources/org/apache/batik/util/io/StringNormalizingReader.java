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
 * This class represents a NormalizingReader which handles Strings.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StringNormalizingReader extends NormalizingReader {

    /**
     * The characters.
     */
    protected String string;

    /**
     * The length of the string.
     */
    protected int length;
    
    /**
     * The index of the next character.
     */
    protected int next;

    /**
     * The current line in the stream.
     */
    protected int line = 1;

    /**
     * The current column in the stream.
     */
    protected int column;

    /**
     * Creates a new StringNormalizingReader.
     * @param s The string to read.
     */
    public StringNormalizingReader(String s) {
        string = s;
        length = s.length();
    }

    /**
     * Read a single character.  This method will block until a
     * character is available, an I/O error occurs, or the end of the
     * stream is reached.
     */
    public int read() throws IOException {
        int result = (length == next) ? -1 : string.charAt(next++);
        if (result <= 13) {
            switch (result) {
            case 13:
                column = 0;
                line++;
                int c = (length == next) ? -1 : string.charAt(next);
                if (c == 10) {
                    next++;
                }
                return 10;
                
            case 10:
                column = 0;
                line++;
            }
        }
        return result;
    }

    /**
     * Returns the current line in the stream.
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the current column in the stream.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Close the stream.
     */
    public void close() throws IOException {
        string = null;
    }
}
