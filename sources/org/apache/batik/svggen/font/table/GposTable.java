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
public class GposTable implements Table {

    protected GposTable(DirectoryEntry de,RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());

        // GPOS Header
        int version = raf.readInt();
        int scriptList = raf.readInt();
        int featureList = raf.readInt();
        int lookupList = raf.readInt();
/*        
        for (int i = 0; i < t_maxp.getNumGlyphs(); i++) {
            raf.seek(tde.getOffset() + t_loca.getOffset(i));
            int len = t_loca.getOffset((short)(i + 1)) - t_loca.getOffset(i);
            if (len > 0) {
                short numberOfContours = raf.readShort();
                if (numberOfContours < 0) {
                    //          descript[i] = new TableGlyfCompositeDescript(this, raf);
                } else {
                    descript[i] = new TableGlyfSimpleDescript(this, numberOfContours, raf);
                }
            } else {
                descript[i] = null;
            }
        }

        for (int i = 0; i < t_maxp.getNumGlyphs(); i++) {
            raf.seek(tde.getOffset() + t_loca.getOffset(i));
            int len = t_loca.getOffset((short)(i + 1)) - t_loca.getOffset(i);
            if (len > 0) {
                short numberOfContours = raf.readShort();
                if (numberOfContours < 0) {
                    descript[i] = new TableGlyfCompositeDescript(this, raf);
                }
            }
        }
*/
    }

    /** Get the table type, as a table directory value.
     * @return The table type
     */
    public int getType() {
        return GPOS;
    }
    
    public String toString() {
        return "GPOS";
    }

}
