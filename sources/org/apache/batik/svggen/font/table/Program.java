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
import java.io.ByteArrayInputStream;

/**
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public abstract class Program {

    private short[] instructions;

    public short[] getInstructions() {
        return instructions;
    }

    protected void readInstructions(RandomAccessFile raf, int count) throws IOException {
        instructions = new short[count];
        for (int i = 0; i < count; i++) {
            instructions[i] = (short) raf.readUnsignedByte();
        }
    }

    protected void readInstructions(ByteArrayInputStream bais, int count) {
        instructions = new short[count];
        for (int i = 0; i < count; i++) {
            instructions[i] = (short) bais.read();
        }
    }
}
