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
public class KernSubtableFormat2 extends KernSubtable {

    private int rowWidth;
    private int leftClassTable;
    private int rightClassTable;
    private int array;

    /** Creates new KernSubtableFormat2 */
    protected KernSubtableFormat2(RandomAccessFile raf) throws IOException {
        rowWidth = raf.readUnsignedShort();
        leftClassTable = raf.readUnsignedShort();
        rightClassTable = raf.readUnsignedShort();
        array = raf.readUnsignedShort();
    }

    public int getKerningPairCount() {
        return 0;
    }

    public KerningPair getKerningPair(int i) {
        return null;
    }

}
