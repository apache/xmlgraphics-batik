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
public class LigatureSubstFormat1 extends LigatureSubst {

    private int coverageOffset;
    private int ligSetCount;
    private int[] ligatureSetOffsets;
    private Coverage coverage;
    private LigatureSet[] ligatureSets;

    /** Creates new LigatureSubstFormat1 */
    protected LigatureSubstFormat1(RandomAccessFile raf,int offset) throws IOException {
        coverageOffset = raf.readUnsignedShort();
        ligSetCount = raf.readUnsignedShort();
        ligatureSetOffsets = new int[ligSetCount];
        ligatureSets = new LigatureSet[ligSetCount];
        for (int i = 0; i < ligSetCount; i++) {
            ligatureSetOffsets[i] = raf.readUnsignedShort();
        }
        raf.seek(offset + coverageOffset);
        coverage = Coverage.read(raf);
        for (int i = 0; i < ligSetCount; i++) {
            ligatureSets[i] = new LigatureSet(raf, offset + ligatureSetOffsets[i]);
        }
    }

    public int getFormat() {
        return 1;
    }

}
