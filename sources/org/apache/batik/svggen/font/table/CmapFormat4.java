/*

   Copyright 2001,2003  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class CmapFormat4 extends CmapFormat {

    public  int language;
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
        int count = (length - 16 - (segCount*8)) / 2;
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
