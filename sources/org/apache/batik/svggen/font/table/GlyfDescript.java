/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;

/**
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public abstract class GlyfDescript extends Program implements GlyphDescription {

    // flags
    public static final byte onCurve = 0x01;
    public static final byte xShortVector = 0x02;
    public static final byte yShortVector = 0x04;
    public static final byte repeat = 0x08;
    public static final byte xDual = 0x10;
    public static final byte yDual = 0x20;

    protected GlyfTable parentTable;
    private int numberOfContours;
    private short xMin;
    private short yMin;
    private short xMax;
    private short yMax;

    protected GlyfDescript(GlyfTable parentTable, short numberOfContours, ByteArrayInputStream bais) {
        this.parentTable = parentTable;
        this.numberOfContours = numberOfContours;
        xMin = (short)(bais.read()<<8 | bais.read());
        yMin = (short)(bais.read()<<8 | bais.read());
        xMax = (short)(bais.read()<<8 | bais.read());
        yMax = (short)(bais.read()<<8 | bais.read());
    }

    public int getNumberOfContours() {
        return numberOfContours;
    }

    public short getXMaximum() {
        return xMax;
    }

    public short getXMinimum() {
        return xMin;
    }

    public short getYMaximum() {
        return yMax;
    }

    public short getYMinimum() {
        return yMin;
    }
}
