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
public class NameTable implements Table {

    private short formatSelector;
    private short numberOfNameRecords;
    private short stringStorageOffset;
    private NameRecord[] records;

    protected NameTable(DirectoryEntry de,RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        formatSelector = raf.readShort();
        numberOfNameRecords = raf.readShort();
        stringStorageOffset = raf.readShort();
        records = new NameRecord[numberOfNameRecords];
        
        // Load the records, which contain the encoding information and string offsets
        for (int i = 0; i < numberOfNameRecords; i++) {
            records[i] = new NameRecord(raf);
        }
        
        // Now load the strings
        for (int i = 0; i < numberOfNameRecords; i++) {
            records[i].loadString(raf, de.getOffset() + stringStorageOffset);
        }
    }

    public String getRecord(short nameId) {

        // Search for the first instance of this name ID
        for (int i = 0; i < numberOfNameRecords; i++) {
            if (records[i].getNameId() == nameId) {
                return records[i].getRecordString();
            }
        }
        return "";
    }

    public int getType() {
        return name;
    }
}
