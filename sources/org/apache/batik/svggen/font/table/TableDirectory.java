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
public class TableDirectory {

    private int version = 0;
    private short numTables = 0;
    private short searchRange = 0;
    private short entrySelector = 0;
    private short rangeShift = 0;
    private DirectoryEntry[] entries;

    public TableDirectory(RandomAccessFile raf) throws IOException {
        version = raf.readInt();
        numTables = raf.readShort();
        searchRange = raf.readShort();
        entrySelector = raf.readShort();
        rangeShift = raf.readShort();
        entries = new DirectoryEntry[numTables];
        for (int i = 0; i < numTables; i++) {
            entries[i] = new DirectoryEntry(raf);
        }

        // Sort them into file order (simple bubble sort)
        boolean modified = true;
        while (modified) {
            modified = false;
            for (int i = 0; i < numTables - 1; i++) {
                if (entries[i].getOffset() > entries[i+1].getOffset()) {
                    DirectoryEntry temp = entries[i];
                    entries[i] = entries[i+1];
                    entries[i+1] = temp;
                    modified = true;
                }
            }
        }
    }

    public DirectoryEntry getEntry(int index) {
        return entries[index];
    }

    public DirectoryEntry getEntryByTag(int tag) {
        for (int i = 0; i < numTables; i++) {
            if (entries[i].getTag() == tag) {
                return entries[i];
            }
        }
        return null;
    }

    public short getEntrySelector() {
        return entrySelector;
    }

    public short getNumTables() {
        return numTables;
    }

    public short getRangeShift() {
        return rangeShift;
    }

    public short getSearchRange() {
        return searchRange;
    }

    public int getVersion() {
        return version;
    }
}
