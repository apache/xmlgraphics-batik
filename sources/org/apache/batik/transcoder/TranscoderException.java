/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder;

/**
 * Thrown when a transcoder is not able to transcode its input.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class TranscoderException extends Exception {

    /** The enclosed exception. */
    protected Exception ex;

    /**
     * Constructs a new transcoder exception with the specified detail message.
     * @param s the detail message of this exception
     */
    public TranscoderException(String s) {
        this(s, null);
    }

    /**
     * Constructs a new transcoder exception with the specified detail message.
     * @param ex the enclosed exception
     */
    public TranscoderException(Exception ex) {
        this(null, ex);
    }

    /**
     * Constructs a new transcoder exception with the specified detail message.
     * @param s the detail message of this exception
     * @param ex the original exception
     */
    public TranscoderException(String s, Exception ex) {
        super(s);
        this.ex = ex;
    }

    /**
     * Returns the original enclosed exception or null if any.
     */
    public Exception getException() {
        return ex;
    }
}
