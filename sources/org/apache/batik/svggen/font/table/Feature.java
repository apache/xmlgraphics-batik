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
 *
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 * @version $Id$
 */
public class Feature {

    private int featureParams;
    private int lookupCount;
    private int[] lookupListIndex;

    /** Creates new Feature */
    protected Feature(RandomAccessFile raf, int offset) throws IOException {
        raf.seek(offset);
        featureParams = raf.readUnsignedShort();
        lookupCount = raf.readUnsignedShort();
        lookupListIndex = new int[lookupCount];
        for (int i = 0; i < lookupCount; i++) {
            lookupListIndex[i] = raf.readUnsignedShort();
        }
    }

    public int getLookupCount() {
        return lookupCount;
    }

    public int getLookupListIndex(int i) {
        return lookupListIndex[i];
    }

}
