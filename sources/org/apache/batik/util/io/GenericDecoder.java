/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * This class delegates to a reader the decoding of an input stream.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GenericDecoder implements CharDecoder {

    /**
     * The reader used to decode the stream.
     */
    protected Reader reader;

    /**
     * Creates a new GenericDecoder.
     * @param is The input stream to decode.
     * @param enc The Java encoding name.
     */
    public GenericDecoder(InputStream is, String enc) throws IOException {
        reader = new InputStreamReader(is, enc);
        reader = new BufferedReader(reader);
    }

    /**
     * Creates a new GenericDecoder.
     * @param r The reader to use.
     */
    public GenericDecoder(Reader r) {
        reader = r;
        if (!(r instanceof BufferedReader)) {
            reader = new BufferedReader(reader);
        }
    }

    /**
     * Reads the next character.
     * @return a character or END_OF_STREAM.
     */
    public int readChar() throws IOException {
        return reader.read();
    }

    /**
     * Disposes the associated resources.
     */
    public void dispose() throws IOException {
        reader.close();
        reader = null;
    }
}
