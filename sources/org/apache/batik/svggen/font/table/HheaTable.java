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
public class HheaTable implements Table {

    private int version;
    private short ascender;
    private short descender;
    private short lineGap;
    private short advanceWidthMax;
    private short minLeftSideBearing;
    private short minRightSideBearing;
    private short xMaxExtent;
    private short caretSlopeRise;
    private short caretSlopeRun;
    private short metricDataFormat;
    private short numberOfHMetrics;

    protected HheaTable(DirectoryEntry de,RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        version = raf.readInt();
        ascender = raf.readShort();
        descender = raf.readShort();
        lineGap = raf.readShort();
        advanceWidthMax = raf.readShort();
        minLeftSideBearing = raf.readShort();
        minRightSideBearing = raf.readShort();
        xMaxExtent = raf.readShort();
        caretSlopeRise = raf.readShort();
        caretSlopeRun = raf.readShort();
        for (int i = 0; i < 5; i++) {
            raf.readShort();
        }
        metricDataFormat = raf.readShort();
        numberOfHMetrics = raf.readShort();
    }

    public short getAdvanceWidthMax() {
        return advanceWidthMax;
    }

    public short getAscender() {
        return ascender;
    }

    public short getCaretSlopeRise() {
        return caretSlopeRise;
    }

    public short getCaretSlopeRun() {
        return caretSlopeRun;
    }

    public short getDescender() {
        return descender;
    }

    public short getLineGap() {
        return lineGap;
    }

    public short getMetricDataFormat() {
        return metricDataFormat;
    }

    public short getMinLeftSideBearing() {
        return minLeftSideBearing;
    }

    public short getMinRightSideBearing() {
        return minRightSideBearing;
    }

    public short getNumberOfHMetrics() {
        return numberOfHMetrics;
    }

    public int getType() {
        return hhea;
    }

    public short getXMaxExtent() {
        return xMaxExtent;
    }
}
