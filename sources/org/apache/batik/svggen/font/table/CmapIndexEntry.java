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
public class CmapIndexEntry {

    private int platformId;
    private int encodingId;
    private int offset;

    protected CmapIndexEntry(RandomAccessFile raf) throws IOException {
        platformId = raf.readUnsignedShort();
        encodingId = raf.readUnsignedShort();
        offset = raf.readInt();
    }

    public int getEncodingId() {
        return encodingId;
    }

    public int getOffset() {
        return offset;
    }

    public int getPlatformId() {
        return platformId;
    }

    public String toString() {
        String platform;
        String encoding = "";

        switch (platformId) {
            case 1: platform = " (Macintosh)"; break;
            case 3: platform = " (Windows)"; break;
            default: platform = "";
        }
        if (platformId == 3) {
            // Windows specific encodings
            switch (encodingId) {
                case 0: encoding = " (Symbol)"; break;
                case 1: encoding = " (Unicode)"; break;
                case 2: encoding = " (ShiftJIS)"; break;
                case 3: encoding = " (Big5)"; break;
                case 4: encoding = " (PRC)"; break;
                case 5: encoding = " (Wansung)"; break;
                case 6: encoding = " (Johab)"; break;
                default: encoding = "";
            }
        }
        return new StringBuffer()
        .append( "platform id: " )
        .append( platformId )
        .append( platform )
        .append( ", encoding id: " )
        .append( encodingId )
        .append( encoding )
        .append( ", offset: " )
        .append( offset ).toString();
    }
}
