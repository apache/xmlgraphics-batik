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
 * Coverage Index (GlyphID) = StartCoverageIndex + GlyphID - Start GlyphID
 *
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 * @version $Id$
 */
public class RangeRecord {

    private int start;
    private int end;
    private int startCoverageIndex;

    /** Creates new RangeRecord */
    public RangeRecord(RandomAccessFile raf) throws IOException {
        start = raf.readUnsignedShort();
        end = raf.readUnsignedShort();
        startCoverageIndex = raf.readUnsignedShort();
    }

    public boolean isInRange(int glyphId) {
        return (start <= glyphId && glyphId <= end);
    }
    
    public int getCoverageIndex(int glyphId) {
        if (isInRange(glyphId)) {
            return startCoverageIndex + glyphId - start;
        }
        return -1;
    }

}

