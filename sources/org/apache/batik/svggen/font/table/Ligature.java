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
 *
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 * @version $Id$
 */
public class Ligature {

    private int ligGlyph;
    private int compCount;
    private int[] components;

    /** Creates new Ligature */
    public Ligature(RandomAccessFile raf) throws IOException {
        ligGlyph = raf.readUnsignedShort();
        compCount = raf.readUnsignedShort();
        components = new int[compCount - 1];
        for (int i = 0; i < compCount - 1; i++) {
            components[i] = raf.readUnsignedShort();
        }
    }
    
    public int getGlyphCount() {
        return compCount;
    }
    
    public int getGlyphId(int i) {
        return (i == 0) ? ligGlyph : components[i-1];
    }

}
