/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.rasterizer;

import java.io.InputStream;
import java.io.IOException;

/**
 * Interface used to handle both Files and URLs in the 
 * <tt>SVGConverter</tt>
 * 
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface SVGConverterSource {
    /**
     * Returns the name of the source. That would be the 
     * name for a File or URL
     */
    public String getName();
    
    /**
     * Gets a <tt>TranscoderInput</tt> for that source
     */
    public InputStream openStream() throws IOException;
    
    /**
     * Checks if same as source described by srcStr
     */
    public boolean isSameAs(String srcStr);
    
    /**
     * Checks if source can be read
     */
    public boolean isReadable();
}

