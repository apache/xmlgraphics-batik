/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.spi;

import java.io.InputStream;
import java.io.StreamCorruptedException;

import org.apache.batik.ext.awt.image.renderable.Filter;


/**
 * This type of Image tag registy entry is used for most normal image
 * file formats.  You are given a markable stream and an opportunity
 * to check if it is "compatible" if you return true then you will
 * likely be asked to provide the decoded image next.
 * @see MagicNumberRegistryEntry
 */
public interface StreamRegistryEntry extends RegistryEntry {

    /**
     * returns the number of bytes that need to be
     * supported by mark on the InputStream for this
     * to check the stream for compatibility.
     */
    public int getReadlimit();

    /**
     * Check if the Stream references an image that can be handled by
     * this format handler.  The input stream passed in should be
     * assumed to support mark and reset.
     *
     * If this method throws a StreamCorruptedException then the
     * InputStream will be closed and a new one opened (if possible).
     *
     * This method should only throw a StreamCorruptedException if it
     * is unable to restore the state of the InputStream
     * (i.e. mark/reset fails basically).  
     */
    public boolean isCompatibleStream(InputStream is) 
        throws StreamCorruptedException;

    /**
     * Decode the Stream into a RenderableImage
     *
     * @param is The input stream that contains the image.
     * @param needRawData If true the image returned should not have
     *                    any default color correction the file may 
     *                    specify applied.  
     */
    public Filter handleStream(InputStream is, boolean needRawData);
}

