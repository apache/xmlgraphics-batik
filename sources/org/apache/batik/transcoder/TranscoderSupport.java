/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder;

import java.util.Map;

/**
 * This is a utility class that can be used by transcoders that
 * support transcoding hints and/or error handler.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class TranscoderSupport {

    static final ErrorHandler defaultErrorHandler = new DefaultErrorHandler();

    /** The transcoding hints. */
    protected TranscodingHints hints = new TranscodingHints();
    /** The error handler used to report warnings and errors. */
    protected ErrorHandler handler = defaultErrorHandler;

    /**
     * Constructs a new <tt>TranscoderSupport</tt>.
     */
    public TranscoderSupport() { }

    /**
     * Returns the transcoding hints of this transcoder.
     */
    public TranscodingHints getTranscodingHints() {
        return new TranscodingHints(hints);
    }

    /**
     * Sets the value of a single preference for the transcoding process.
     * @param key the key of the hint to be set
     * @param value the value indicating preferences for the specified
     * hint category.
     */
    public void addTranscodingHint(TranscodingHints.Key key, Object value) {
        hints.put(key, value);
    }

    /**
     * Replaces the values of all preferences for the transcoding algorithms
     * with the specified hints.
     * @param hints the rendering hints to be set
     */
    public void setTranscodingHints(Map hints) {
        this.hints.putAll(hints);
    }

    /**
     * Sets the error handler this transcoder may use to report
     * warnings and errors.
     * @param handler to ErrorHandler to use
     */
    public void setErrorHandler(ErrorHandler handler) {
        this.handler = handler;
    }

    /**
     * Returns the error handler this transcoder uses to report
     * warnings and errors, or null if any.
     */
    public ErrorHandler getErrorHandler() {
        return handler;
    }
}


