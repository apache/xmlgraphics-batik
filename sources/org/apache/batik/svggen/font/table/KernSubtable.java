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
public abstract class KernSubtable {

    /** Creates new KernSubtable */
    protected KernSubtable() {
    }
    
    public abstract int getKerningPairCount();

    public abstract KerningPair getKerningPair(int i);

    public static KernSubtable read(RandomAccessFile raf) throws IOException {
        KernSubtable table = null;
        int version = raf.readUnsignedShort();
        int length = raf.readUnsignedShort();
        int coverage = raf.readUnsignedShort();
        int format = coverage >> 8;
        
        switch (format) {
        case 0:
            table = new KernSubtableFormat0(raf);
            break;
        case 2:
            table = new KernSubtableFormat2(raf);
            break;
        default:
            break;
        }
        return table;
    }

}
