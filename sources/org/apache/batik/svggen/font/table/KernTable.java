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
public class KernTable implements Table {
    
    private int version;
    private int nTables;
    private KernSubtable[] tables;

    /** Creates new KernTable */
    protected KernTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        version = raf.readUnsignedShort();
        nTables = raf.readUnsignedShort();
        tables = new KernSubtable[nTables];
        for (int i = 0; i < nTables; i++) {
            tables[i] = KernSubtable.read(raf);
        }
    }

    public int getSubtableCount() {
        return nTables;
    }
    
    public KernSubtable getSubtable(int i) {
        return tables[i];
    }

    /** Get the table type, as a table directory value.
     * @return The table type
     */
    public int getType() {
        return kern;
    }

}
