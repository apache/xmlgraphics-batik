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
public class KernSubtableFormat0 extends KernSubtable {
    
    private int nPairs;
    private int searchRange;
    private int entrySelector;
    private int rangeShift;
    private KerningPair[] kerningPairs;

    /** Creates new KernSubtableFormat0 */
    protected KernSubtableFormat0(RandomAccessFile raf) throws IOException {
        nPairs = raf.readUnsignedShort();
        searchRange = raf.readUnsignedShort();
        entrySelector = raf.readUnsignedShort();
        rangeShift = raf.readUnsignedShort();
        kerningPairs = new KerningPair[nPairs];
        for (int i = 0; i < nPairs; i++) {
            kerningPairs[i] = new KerningPair(raf);
        }
    }

    public int getKerningPairCount() {
        return nPairs;
    }

    public KerningPair getKerningPair(int i) {
        return kerningPairs[i];
    }

}
