/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder;

import java.io.OutputStream;
import java.util.Map;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Provides a way to transcode an input stream or a document.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface Transcoder {

    /**
     * Transcodes the specified input and write the result to the
     * specified output.
     * @param isource the input to transcode
     * @param ostream the ouput stream where to write the transcoded input
     * @exception TranscoderException if an error occured while transcoding
     */
    void transcodeToStream(InputSource isource, OutputStream ostream)
        throws TranscoderException;

    /**
     * Transcodes the specified document and write the result to the
     * specified output.
     * @param document the document to transcode
     * @param ostream the ouput stream where to write the transcoded input
     * @exception TranscoderException if an error occured while transcoding
     */
    void transcodeToStream(Document document, OutputStream ostream)
        throws TranscoderException;

    /**
     * Returns the transcoding hints of this transcoder.
     */
    TranscodingHints getTranscodingHints();

    /**
     * Sets the value of a single preference for the transcoding algorithms.
     * @param key the key of the hint to be set
     * @param value the value indicating preferences for the specified
     * hint category.
     */
    void addTranscodingHint(TranscodingHints.Key key, Object value);

    /**
     * Replaces the values of all preferences for the transcoding algorithms
     * with the specified hints.
     * @param hints the rendering hints to be set
     */
    void setTranscodingHints(Map hints);

    /**
     * Returns the mime type of the ouput format of this transcoder.
     */
    String getMimeType();
}
