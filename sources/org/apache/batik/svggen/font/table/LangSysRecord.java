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
public class LangSysRecord {

    private int tag;
    private int offset;
    
    /** Creates new LangSysRecord */
    public LangSysRecord(RandomAccessFile raf) throws IOException {
        tag = raf.readInt();
        offset = raf.readUnsignedShort();
    }

    public int getTag() {
        return tag;
    }

    public int getOffset() {
        return offset;
    }

}
