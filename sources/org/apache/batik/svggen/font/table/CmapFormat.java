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
public abstract class CmapFormat {

    protected int format;
    protected int length;
    protected int version;

    protected CmapFormat(RandomAccessFile raf) throws IOException {
        length = raf.readUnsignedShort();
        version = raf.readUnsignedShort();
    }

    protected static CmapFormat create(int format, RandomAccessFile raf)
    throws IOException {
        switch(format) {
            case 0:
            return new CmapFormat0(raf);
            case 2:
            return new CmapFormat2(raf);
            case 4:
            return new CmapFormat4(raf);
            case 6:
            return new CmapFormat6(raf);
        }
        return null;
    }

    public int getFormat() {
        return format;
    }

    public int getLength() {
        return length;
    }

    public int getVersion() {
        return version;
    }

    public abstract int mapCharCode(int charCode);

    public String toString() {
        return new StringBuffer()
        .append("format: ")
        .append(format)
        .append(", length: ")
        .append(length)
        .append(", version: ")
        .append(version).toString();
    }
}
