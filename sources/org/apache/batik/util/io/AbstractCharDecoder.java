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
 * This class is the superclass of all the char decoders.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractCharDecoder implements CharDecoder {

    /**
     * The buffer size.
     */
    protected final static int BUFFER_SIZE = 8192;

    /**
     * The input stream to read.
     */
    protected InputStream inputStream;
    
    /**
     * The input buffer.
     */
    protected byte[] buffer = new byte[BUFFER_SIZE];

    /**
     * The current position in the buffer.
     */
    protected int position;

    /**
     * The byte count in the buffer.
     */
    protected int count;

    /**
     * Creates a new CharDecoder object.
     * @param is The stream to read.
     */
    protected AbstractCharDecoder(InputStream is) {
        inputStream = is;
    }

    /**
     * Disposes the associated resources.
     */
    public void dispose() throws IOException {
        inputStream.close();
        inputStream = null;
    }

    /**
     * Fills the input buffer.
     */
    protected void fillBuffer() throws IOException {
        count = inputStream.read(buffer, 0, BUFFER_SIZE);
        position = 0;
    }

    /**
     * To throws an exception when the input stream contains an
     * invalid character.
     * @param encoding The encoding name.
     */
    protected void charError(String encoding) throws IOException {
        throw new IOException
            (Messages.formatMessage("invalid.char",
                                    new Object[] { encoding }));
    }

    /**
     * To throws an exception when the end of stream was unexpected.
     * @param encoding The encoding name.
     */
    protected void endOfStreamError(String encoding) throws IOException {
        throw new IOException
            (Messages.formatMessage("end.of.stream",
                                    new Object[] { encoding }));
    }
}
