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
public class LigatureSet {

    private int ligatureCount;
    private int[] ligatureOffsets;
    private Ligature[] ligatures;

    /** Creates new LigatureSet */
    public LigatureSet(RandomAccessFile raf, int offset) throws IOException {
        raf.seek(offset);
        ligatureCount = raf.readUnsignedShort();
        ligatureOffsets = new int[ligatureCount];
        ligatures = new Ligature[ligatureCount];
        for (int i = 0; i < ligatureCount; i++) {
            ligatureOffsets[i] = raf.readUnsignedShort();
        }
        for (int i = 0; i < ligatureCount; i++) {
            raf.seek(offset + ligatureOffsets[i]);
            ligatures[i] = new Ligature(raf);
        }
    }

}

