/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class LocaTable implements Table {

    private byte[] buf = null;
    private int[] offsets = null;
    private short factor = 0;

    protected LocaTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        buf = new byte[de.getLength()];
        raf.read(buf);
    }

    public void init(int numGlyphs, boolean shortEntries) {
        if (buf == null) {
            return;
        }
        offsets = new int[numGlyphs + 1];
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        if (shortEntries) {
            factor = 2;
            for (int i = 0; i <= numGlyphs; i++) {
                offsets[i] = (int)(bais.read()<<8 | bais.read());
            }
        } else {
            factor = 1;
            for (int i = 0; i <= numGlyphs; i++) {
                offsets[i] = (int)(bais.read()<<24 | bais.read()<<16 | bais.read()<<8 | bais.read());
            }
        }
        buf = null;
    }
    
    public int getOffset(int i) {
        if (offsets == null) {
            return 0;
        }
        return offsets[i] * factor;
    }

    public int getType() {
        return loca;
    }
}
