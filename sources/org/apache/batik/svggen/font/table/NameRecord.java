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
public class NameRecord {

    private short platformId;
    private short encodingId;
    private short languageId;
    private short nameId;
    private short stringLength;
    private short stringOffset;
    private String record;

    protected NameRecord(RandomAccessFile raf) throws IOException {
        platformId = raf.readShort();
        encodingId = raf.readShort();
        languageId = raf.readShort();
        nameId = raf.readShort();
        stringLength = raf.readShort();
        stringOffset = raf.readShort();
    }
    
    public short getEncodingId() {
        return encodingId;
    }
    
    public short getLanguageId() {
        return languageId;
    }
    
    public short getNameId() {
        return nameId;
    }
    
    public short getPlatformId() {
        return platformId;
    }

    public String getRecordString() {
        return record;
    }

    protected void loadString(RandomAccessFile raf, int stringStorageOffset) throws IOException {
        StringBuffer sb = new StringBuffer();
        raf.seek(stringStorageOffset + stringOffset);
        if (platformId == Table.platformAppleUnicode) {
            
            // Unicode (big-endian)
            for (int i = 0; i < stringLength/2; i++) {
                sb.append(raf.readChar());
            }
        } else if (platformId == Table.platformMacintosh) {

            // Macintosh encoding, ASCII
            for (int i = 0; i < stringLength; i++) {
                sb.append((char) raf.readByte());
            }
        } else if (platformId == Table.platformISO) {
            
            // ISO encoding, ASCII
            for (int i = 0; i < stringLength; i++) {
                sb.append((char) raf.readByte());
            }
        } else if (platformId == Table.platformMicrosoft) {
            
            // Microsoft encoding, Unicode
            char c;
            for (int i = 0; i < stringLength/2; i++) {
                c = raf.readChar();
                sb.append(c);
            }
        }
        record = sb.toString();
    }
}
