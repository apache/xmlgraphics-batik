/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder;

/**
 * This class can be the base class of a transcoder which may support
 * transcoding hints and/or error handler.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class AbstractTranscoder extends TranscoderSupport
        implements Transcoder {

    /**
     * Constructs a new <tt>AbstractTranscoder</tt>.
     */
    protected AbstractTranscoder() {}

}
