/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.io;

import java.io.InputStream;
import java.io.IOException;

/**
 * This class represents an object which decodes ASCII characters from
 * a stream of bytes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ASCIIDecoder extends AbstractCharDecoder {
    
    /**
     * Creates a new ASCIIDecoder.
     */
    public ASCIIDecoder(InputStream is) {
        super(is);
    }

    /**
     * Reads the next character.
     * @return a character or END_OF_STREAM.
     */
    public int readChar() throws IOException {
        if (position == count) {
            fillBuffer();
        }
        if (count == -1) {
            return END_OF_STREAM;
        }
        int result = buffer[position++];
        if (result < 0) {
            charError("ASCII");
        }
        return result;
    }
}
