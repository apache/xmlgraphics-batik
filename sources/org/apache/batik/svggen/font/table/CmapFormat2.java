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
public class CmapFormat2 extends CmapFormat {

    private short[] subHeaderKeys = new short[256];
    private int[] subHeaders1;
    private int[] subHeaders2;
    private short[] glyphIndexArray;

    protected CmapFormat2(RandomAccessFile raf) throws IOException {
        super(raf);
        format = 2;
    }

    public int mapCharCode(int charCode) {
        return 0;
    }
}
