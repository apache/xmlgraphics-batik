/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents an object which decodes ISO-8859-1 characters from
 * a stream of bytes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ISO_8859_1Decoder extends AbstractCharDecoder {
    
    /**
     * Creates a new ISO_8859_1Decoder.
     */
    public ISO_8859_1Decoder(InputStream is) {
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
            return -1;
        }
        return buffer[position++] & 0xff;
    }
}
