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
public class DirectoryEntry {

    private int tag;
    private int checksum;
    private int offset;
    private int length;
    private Table table = null;

    protected DirectoryEntry(RandomAccessFile raf) throws IOException {
        tag = raf.readInt();
        checksum = raf.readInt();
        offset = raf.readInt();
        length = raf.readInt();
    }

    public int getChecksum() {
        return checksum;
    }

    public int getLength() {
        return length;
    }

    public int getOffset() {
        return offset;
    }

    public int getTag() {
        return tag;
    }

    public String toString() {
        return new StringBuffer()
            .append((char)((tag>>24)&0xff))
            .append((char)((tag>>16)&0xff))
            .append((char)((tag>>8)&0xff))
            .append((char)((tag)&0xff))
            .append(", offset: ")
            .append(offset)
            .append(", length: ")
            .append(length)
            .append(", checksum: 0x")
            .append(Integer.toHexString(checksum))
            .toString();
    }
}
