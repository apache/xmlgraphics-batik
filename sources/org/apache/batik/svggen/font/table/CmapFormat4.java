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
public class CmapFormat4 extends CmapFormat {

    private int segCountX2;
    private int searchRange;
    private int entrySelector;
    private int rangeShift;
    private int[] endCode;
    private int[] startCode;
    private int[] idDelta;
    private int[] idRangeOffset;
    private int[] glyphIdArray;
    private int segCount;

    protected CmapFormat4(RandomAccessFile raf) throws IOException {
        super(raf);
        format = 4;
        segCountX2 = raf.readUnsignedShort();
        segCount = segCountX2 / 2;
        endCode = new int[segCount];
        startCode = new int[segCount];
        idDelta = new int[segCount];
        idRangeOffset = new int[segCount];
        searchRange = raf.readUnsignedShort();
        entrySelector = raf.readUnsignedShort();
        rangeShift = raf.readUnsignedShort();
        for (int i = 0; i < segCount; i++) {
            endCode[i] = raf.readUnsignedShort();
        }
        raf.readUnsignedShort(); // reservePad
        for (int i = 0; i < segCount; i++) {
            startCode[i] = raf.readUnsignedShort();
        }
        for (int i = 0; i < segCount; i++) {
            idDelta[i] = raf.readUnsignedShort();
        }
        for (int i = 0; i < segCount; i++) {
            idRangeOffset[i] = raf.readUnsignedShort();
        }

        // Whatever remains of this header belongs in glyphIdArray
        int count = (length - 24) / 2;
        glyphIdArray = new int[count];
        for (int i = 0; i < count; i++) {
            glyphIdArray[i] = raf.readUnsignedShort();
        }
    }

    public int mapCharCode(int charCode) {
        try {
            for (int i = 0; i < segCount; i++) {
                if (endCode[i] >= charCode) {
                    if (startCode[i] <= charCode) {
                        if (idRangeOffset[i] > 0) {
                            return glyphIdArray[idRangeOffset[i]/2 + (charCode - startCode[i]) - (segCount - i)];
                        } else {
                            return (idDelta[i] + charCode) % 65536;
                        }
                    } else {
                        break;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("error: Array out of bounds - " + e.getMessage());
        }
        return 0;
    }

    public String toString() {
        return new StringBuffer()
        .append(super.toString())
        .append(", segCountX2: ")
        .append(segCountX2)
        .append(", searchRange: ")
        .append(searchRange)
        .append(", entrySelector: ")
        .append(entrySelector)
        .append(", rangeShift: ")
        .append(rangeShift)
        .append(", endCode: ")
        .append(endCode)
        .append(", startCode: ")
        .append(endCode)
        .append(", idDelta: ")
        .append(idDelta)
        .append(", idRangeOffset: ")
        .append(idRangeOffset).toString();
    }
}
