/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
