/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder;

/**
 * This interface provides a way to catch errors and warnings from a
 * Transcoder.
 *
 * If an application needs to implement customized error handling, it
 * must implement this interface and then register an instance with
 * the Transcoder using the transcoder's setErrorHandler method. The
 * transcoder will then report all errors and warnings through this
 * interface.
 *
 * A transcoder shall use this interface instead of throwing an
 * exception: it is up to the application whether to throw an
 * exception for different types of errors and warnings. Note,
 * however, that there is no requirement that the transcoder continue
 * to provide useful information after a call to fatalError (in other
 * words, a transcoder class could catch an exception and report a
 * fatalError).
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface ErrorHandler {

    /**
     * Invoked when an error occured while transcoding.
     * @param ex the error informations encapsulated in a TranscoderException */
    void error(TranscoderException ex);

    /**
     * Invoked when an fatal error occured while transcoding.
     * @param ex the fatal error informations encapsulated in a
     * TranscoderException
     */
    void fatalError(TranscoderException ex);

    /**
     * Invoked when a warning occured while transcoding.
     * @param ex the warning informations encapsulated in a TranscoderException
     */
    void warning(TranscoderException ex);

}
