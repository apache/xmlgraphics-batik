/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.svggen.font.table;

import java.io.*;

/**
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class CvtTable implements Table {

    private short[] values;

    protected CvtTable(DirectoryEntry de,RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        int len = de.getLength() / 2;
        values = new short[len];
        for (int i = 0; i < len; i++) {
            values[i] = raf.readShort();
        }
    }

    public int getType() {
        return cvt;
    }

    public short[] getValues() {
        return values;
    }
}
