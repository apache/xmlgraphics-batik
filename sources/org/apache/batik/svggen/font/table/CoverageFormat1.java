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
 *
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 * @version $Id$
 */
public class CoverageFormat1 extends Coverage {

    private int glyphCount;
    private int[] glyphIds;

    /** Creates new CoverageFormat1 */
    protected CoverageFormat1(RandomAccessFile raf) throws IOException {
        glyphCount = raf.readUnsignedShort();
        glyphIds = new int[glyphCount];
        for (int i = 0; i < glyphCount; i++) {
            glyphIds[i] = raf.readUnsignedShort();
        }
    }

    public int getFormat() {
        return 1;
    }

    public int findGlyph(int glyphId) {
        for (int i = 0; i < glyphCount; i++) {
            if (glyphIds[i] == glyphId) {
                return i;
            }
        }
        return -1;
    }

}
