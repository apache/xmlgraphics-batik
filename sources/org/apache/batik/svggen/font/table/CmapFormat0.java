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
 * Simple Macintosh cmap table, mapping only the ASCII character set to glyphs.
 *
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class CmapFormat0 extends CmapFormat {

    private int[] glyphIdArray = new int[256];

    protected CmapFormat0(RandomAccessFile raf) throws IOException {
        super(raf);
        format = 0;
        for (int i = 0; i < 256; i++) {
            glyphIdArray[i] = raf.readUnsignedByte();
        }
    }

    public int mapCharCode(int charCode) {
        if (0 <= charCode && charCode < 256) {
            return glyphIdArray[charCode];
        } else {
            return 0;
        }
    }
}
