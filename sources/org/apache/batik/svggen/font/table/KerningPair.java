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
public class KerningPair {

    private int left;
    private int right;
    private short value;

    /** Creates new KerningPair */
    protected KerningPair(RandomAccessFile raf) throws IOException {
        left = raf.readUnsignedShort();
        right = raf.readUnsignedShort();
        value = raf.readShort();
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public short getValue() {
        return value;
    }

}
