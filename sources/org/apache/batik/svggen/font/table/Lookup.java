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
public class Lookup {

    // LookupFlag bit enumeration
    public static final int IGNORE_BASE_GLYPHS = 0x0002;
    public static final int IGNORE_BASE_LIGATURES = 0x0004;
    public static final int IGNORE_BASE_MARKS = 0x0008;
    public static final int MARK_ATTACHMENT_TYPE = 0xFF00;

    private int type;
    private int flag;
    private int subTableCount;
    private int[] subTableOffsets;
    private LookupSubtable[] subTables;

    /** Creates new Lookup */
    public Lookup(LookupSubtableFactory factory, RandomAccessFile raf, int offset)
    throws IOException {
        raf.seek(offset);
        type = raf.readUnsignedShort();
        flag = raf.readUnsignedShort();
        subTableCount = raf.readUnsignedShort();
        subTableOffsets = new int[subTableCount];
        subTables = new LookupSubtable[subTableCount];
        for (int i = 0; i < subTableCount; i++) {
            subTableOffsets[i] = raf.readUnsignedShort();
        }
        for (int i = 0; i < subTableCount; i++) {
            subTables[i] = factory.read(type, raf, offset + subTableOffsets[i]);
        }
    }

    public int getType() {
        return type;
    }

    public int getSubtableCount() {
        return subTableCount;
    }

    public LookupSubtable getSubtable(int i) {
        return subTables[i];
    }

}

