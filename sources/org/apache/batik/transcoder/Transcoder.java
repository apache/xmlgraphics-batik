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
 * This class defines an API for transcoding.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface Transcoder {

    /**
     * Transcodes the specified input in the specified output.
     * @param input the input to transcode
     * @param output the ouput where to transcode
     * @exception TranscoderException if an error occured while transcoding
     */
    void transcode(TranscoderInput input, TranscoderOutput output)
            throws TranscoderException;

    /**
     * Returns the transcoding hints of this transcoder.
     */
    TranscodingHints getTranscodingHints();

    /**
     * Sets the value of a single preference for the transcoding process.
     * @param key the key of the hint to be set
     * @param value the value indicating preferences for the specified
     * hint category.
     */
    void addTranscodingHint(TranscodingHints.Key key, Object value);

    /**
     * Removes the value of a single preference for the transcoding process.
     * @param key the key of the hint to remove
     */
    void removeTranscodingHint(TranscodingHints.Key key);

    /**
     * Replaces the values of all preferences for the transcoding algorithms
     * with the specified hints.
     * @param hints the rendering hints to be set
     */
    void setTranscodingHints(Map hints);

    /**
     * Sets the values of all preferences for the transcoding algorithms
     * with the specified hints.
     * @param hints the rendering hints to be set
     */
    void setTranscodingHints(TranscodingHints hints);

    /**
     * Sets the error handler this transcoder may use to report
     * warnings and errors.
     * @param handler to ErrorHandler to use
     */
    void setErrorHandler(ErrorHandler handler);

    /**
     * Returns the error handler this transcoder uses to report
     * warnings and errors, or null if any.
     */
    ErrorHandler getErrorHandler();
}
