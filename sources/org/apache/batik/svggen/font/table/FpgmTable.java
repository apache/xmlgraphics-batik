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
public class FpgmTable extends Program implements Table {

    protected FpgmTable(DirectoryEntry de,RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        readInstructions(raf, de.getLength());
    }

    public int getType() {
        return fpgm;
    }
}
