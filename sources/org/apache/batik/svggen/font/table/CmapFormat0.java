/*

   Copyright 2001  The Apache Software Foundation 

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
 * Simple Macintosh cmap table, mapping only the ASCII character set to glyphs.
 *
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class CmapFormat0 extends CmapFormat {

    private int[] glyphIdArray = new int[256];

    protected CmapFormat0(RandomAccessFile raf) throws IOException {
        super(raf);
        format = 0;
        for (int i = 0; i < 256; i++) {
            glyphIdArray[i] = raf.readUnsignedByte();
        }
    }

    public int mapCharCode(int charCode) {
        if (0 <= charCode && charCode < 256) {
            return glyphIdArray[charCode];
        } else {
            return 0;
        }
    }
}
