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
public abstract class Coverage {

    public abstract int getFormat();

    /**
     * @param glyphId The ID of the glyph to find.
     * @returns The index of the glyph within the coverage, or -1 if the glyph
     * can't be found.
     */
    public abstract int findGlyph(int glyphId);
    
    protected static Coverage read(RandomAccessFile raf) throws IOException {
        Coverage c = null;
        int format = raf.readUnsignedShort();
        if (format == 1) {
            c = new CoverageFormat1(raf);
        } else if (format == 2) {
            c = new CoverageFormat2(raf);
        }
        return c;
    }

}
