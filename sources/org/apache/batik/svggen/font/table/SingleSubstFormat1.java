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
public class SingleSubstFormat1 extends SingleSubst {

    private int coverageOffset;
    private short deltaGlyphID;
    private Coverage coverage;

    /** Creates new SingleSubstFormat1 */
    protected SingleSubstFormat1(RandomAccessFile raf, int offset) throws IOException {
        coverageOffset = raf.readUnsignedShort();
        deltaGlyphID = raf.readShort();
        raf.seek(offset + coverageOffset);
        coverage = Coverage.read(raf);
    }

    public int getFormat() {
        return 1;
    }

    public int substitute(int glyphId) {
        int i = coverage.findGlyph(glyphId);
        if (i > -1) {
            return glyphId + deltaGlyphID;
        }
        return glyphId;
    }

}

