/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.rasterizer;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import org.apache.batik.transcoder.Transcoder;

/**
 * Interface for controlling some aspectes of the 
 * <tt>SVGConverter</tt> operation.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface SVGConverterController {
    /**
     * Invoked when the rasterizer has computed the 
     * exact description of what it should do. The controller 
     * should return true if the transcoding process should 
     * proceed or false otherwise.
     *
     * @param transcoder Transcoder which will be used 
     * @param hints set of hints that were set on the transcoder
     * @param sources list of SVG sources it will convert.
     * @param dest list of destination file it will use
     */
    public boolean proceedWithComputedTask(Transcoder transcoder,
                                           Map hints,
                                           Vector sources,
                                           Vector dest);

    /**
     * Invoked when the rasterizer is about to start transcoding
     * of a given source.
     * The controller should return true if the source should be
     * transcoded and false otherwise.
     */
    public boolean proceedWithSourceTranscoding(SVGConverterSource source,
                                                File dest);
        
    /**
     * Invoked when the rasterizer got an error while
     * transcoding the input source. 
     * The controller should return true if the transcoding process
     * should continue on other sources and it should return false
     * if it should not.
     *
     * @param errorCode see the {@link SVGConverter} error code descriptions.
     */
    public boolean proceedOnSourceTranscodingFailure(SVGConverterSource source,
                                                     File dest,
                                                     String errorCode);

    /**
     * Invoked when the rasterizer successfully transcoded
     * the input source.
     */
    public void onSourceTranscodingSuccess(SVGConverterSource source,
                                           File dest);

}

