/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.io;

import java.io.IOException;
import java.io.Reader;

/**
 * This class represents a reader which normalizes the line break: \n,
 * \r, \r\n are replaced by \n.  The methods of this reader are not
 * synchronized.  The input is buffered.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class NormalizingReader extends Reader {

    /**
     * Read characters into a portion of an array.
     * @param cbuf  Destination buffer
     * @param off   Offset at which to start writing characters
     * @param len   Maximum number of characters to read
     * @return The number of characters read, or -1 if the end of the
     * stream has been reached
     */
    public int read(char cbuf[], int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        int result = 0;
        do {
            cbuf[result + off] = (char)c;
            result++;
            c = read();
        } while (c != -1 && result < len);
        return result;
    }

    /**
     * Returns the current line in the stream.
     */
    public abstract int getLine();

    /**
     * Returns the current column in the stream.
     */
    public abstract int getColumn();

}
