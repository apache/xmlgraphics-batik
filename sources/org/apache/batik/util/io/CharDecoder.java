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
 * This interface represents an object which decodes characters from a
 * stream of bytes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CharDecoder {
    
    /**
     * This constant represents the end of stream character.
     */
    int END_OF_STREAM = -1;

    /**
     * Reads the next character.
     * @return a character or END_OF_STREAM.
     */
    int readChar() throws IOException;

    /**
     * Disposes the associated resources.
     */
    void dispose() throws IOException;
}
