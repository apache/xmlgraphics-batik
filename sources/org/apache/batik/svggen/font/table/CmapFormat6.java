/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class CmapFormat6 extends CmapFormat {

    private short format;
    private short length;
    private short version;
    private short firstCode;
    private short entryCount;
    private short[] glyphIdArray;

    protected CmapFormat6(RandomAccessFile raf) throws IOException {
        super(raf);
        format = 6;
    }

    public int mapCharCode(int charCode) {
        return 0;
    }
}
