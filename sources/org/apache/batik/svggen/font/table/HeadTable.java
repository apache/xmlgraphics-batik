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
public class HeadTable implements Table {

    private int versionNumber;
    private int fontRevision;
    private int checkSumAdjustment;
    private int magicNumber;
    private short flags;
    private short unitsPerEm;
    private long created;
    private long modified;
    private short xMin;
    private short yMin;
    private short xMax;
    private short yMax;
    private short macStyle;
    private short lowestRecPPEM;
    private short fontDirectionHint;
    private short indexToLocFormat;
    private short glyphDataFormat;

    protected HeadTable(DirectoryEntry de,RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        versionNumber = raf.readInt();
        fontRevision = raf.readInt();
        checkSumAdjustment = raf.readInt();
        magicNumber = raf.readInt();
        flags = raf.readShort();
        unitsPerEm = raf.readShort();
        created = raf.readLong();
        modified = raf.readLong();
        xMin = raf.readShort();
        yMin = raf.readShort();
        xMax = raf.readShort();
        yMax = raf.readShort();
        macStyle = raf.readShort();
        lowestRecPPEM = raf.readShort();
        fontDirectionHint = raf.readShort();
        indexToLocFormat = raf.readShort();
        glyphDataFormat = raf.readShort();
    }

    public int getCheckSumAdjustment() {
        return checkSumAdjustment;
    }

    public long getCreated() {
        return created;
    }

    public short getFlags() {
        return flags;
    }

    public short getFontDirectionHint() {
        return fontDirectionHint;
    }

    public int getFontRevision(){
        return fontRevision;
    }

    public short getGlyphDataFormat() {
        return glyphDataFormat;
    }

    public short getIndexToLocFormat() {
        return indexToLocFormat;
    }

    public short getLowestRecPPEM() {
        return lowestRecPPEM;
    }

    public short getMacStyle() {
        return macStyle;
    }

    public long getModified() {
        return modified;
    }

    public int getType() {
        return head;
    }

    public short getUnitsPerEm() {
        return unitsPerEm;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public short getXMax() {
        return xMax;
    }

    public short getXMin() {
        return xMin;
    }

    public short getYMax() {
        return yMax;
    }

    public short getYMin() {
        return yMin;
    }

    public String toString() {
        return new StringBuffer()
            .append("head\n\tversionNumber: ").append(versionNumber)
            .append("\n\tfontRevision: ").append(fontRevision)
            .append("\n\tcheckSumAdjustment: ").append(checkSumAdjustment)
            .append("\n\tmagicNumber: ").append(magicNumber)
            .append("\n\tflags: ").append(flags)
            .append("\n\tunitsPerEm: ").append(unitsPerEm)
            .append("\n\tcreated: ").append(created)
            .append("\n\tmodified: ").append(modified)
            .append("\n\txMin: ").append(xMin)
            .append(", yMin: ").append(yMin)
            .append("\n\txMax: ").append(xMax)
            .append(", yMax: ").append(yMax)
            .append("\n\tmacStyle: ").append(macStyle)
            .append("\n\tlowestRecPPEM: ").append(lowestRecPPEM)
            .append("\n\tfontDirectionHint: ").append(fontDirectionHint)
            .append("\n\tindexToLocFormat: ").append(indexToLocFormat)
            .append("\n\tglyphDataFormat: ").append(glyphDataFormat)
            .toString();
    }
}
