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
public class Os2Table implements Table {

    private int version;
    private short xAvgCharWidth;
    private int usWeightClass;
    private int usWidthClass;
    private short fsType;
    private short ySubscriptXSize;
    private short ySubscriptYSize;
    private short ySubscriptXOffset;
    private short ySubscriptYOffset;
    private short ySuperscriptXSize;
    private short ySuperscriptYSize;
    private short ySuperscriptXOffset;
    private short ySuperscriptYOffset;
    private short yStrikeoutSize;
    private short yStrikeoutPosition;
    private short sFamilyClass;
    private Panose panose;
    private int ulUnicodeRange1;
    private int ulUnicodeRange2;
    private int ulUnicodeRange3;
    private int ulUnicodeRange4;
    private int achVendorID;
    private short fsSelection;
    private int usFirstCharIndex;
    private int usLastCharIndex;
    private short sTypoAscender;
    private short sTypoDescender;
    private short sTypoLineGap;
    private int usWinAscent;
    private int usWinDescent;
    private int ulCodePageRange1;
    private int ulCodePageRange2;

    protected Os2Table(DirectoryEntry de,RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        version = raf.readUnsignedShort();
        xAvgCharWidth = raf.readShort();
        usWeightClass = raf.readUnsignedShort();
        usWidthClass = raf.readUnsignedShort();
        fsType = raf.readShort();
        ySubscriptXSize = raf.readShort();
        ySubscriptYSize = raf.readShort();
        ySubscriptXOffset = raf.readShort();
        ySubscriptYOffset = raf.readShort();
        ySuperscriptXSize = raf.readShort();
        ySuperscriptYSize = raf.readShort();
        ySuperscriptXOffset = raf.readShort();
        ySuperscriptYOffset = raf.readShort();
        yStrikeoutSize = raf.readShort();
        yStrikeoutPosition = raf.readShort();
        sFamilyClass = raf.readShort();
        byte[] buf = new byte[10];
        raf.read(buf);
        panose = new Panose(buf);
        ulUnicodeRange1 = raf.readInt();
        ulUnicodeRange2 = raf.readInt();
        ulUnicodeRange3 = raf.readInt();
        ulUnicodeRange4 = raf.readInt();
        achVendorID = raf.readInt();
        fsSelection = raf.readShort();
        usFirstCharIndex = raf.readUnsignedShort();
        usLastCharIndex = raf.readUnsignedShort();
        sTypoAscender = raf.readShort();
        sTypoDescender = raf.readShort();
        sTypoLineGap = raf.readShort();
        usWinAscent = raf.readUnsignedShort();
        usWinDescent = raf.readUnsignedShort();
        ulCodePageRange1 = raf.readInt();
        ulCodePageRange2 = raf.readInt();
    }

    public int getVersion() {
        return version;
    }

    public short getAvgCharWidth() {
        return xAvgCharWidth;
    }

    public int getWeightClass() {
        return usWeightClass;
    }

    public int getWidthClass() {
        return usWidthClass;
    }

    public short getLicenseType() {
        return fsType;
    }

    public short getSubscriptXSize() {
        return ySubscriptXSize;
    }

    public short getSubscriptYSize() {
        return ySubscriptYSize;
    }

    public short getSubscriptXOffset() {
        return ySubscriptXOffset;
    }

    public short getSubscriptYOffset() {
        return ySubscriptYOffset;
    }

    public short getSuperscriptXSize() {
        return ySuperscriptXSize;
    }

    public short getSuperscriptYSize() {
        return ySuperscriptYSize;
    }

    public short getSuperscriptXOffset() {
        return ySuperscriptXOffset;
    }

    public short getSuperscriptYOffset() {
        return ySuperscriptYOffset;
    }

    public short getStrikeoutSize() {
        return yStrikeoutSize;
    }

    public short getStrikeoutPosition() {
        return yStrikeoutPosition;
    }

    public short getFamilyClass() {
        return sFamilyClass;
    }

    public Panose getPanose() {
        return panose;
    }

    public int getUnicodeRange1() {
        return ulUnicodeRange1;
    }

    public int getUnicodeRange2() {
        return ulUnicodeRange2;
    }

    public int getUnicodeRange3() {
        return ulUnicodeRange3;
    }

    public int getUnicodeRange4() {
        return ulUnicodeRange4;
    }

    public int getVendorID() {
        return achVendorID;
    }

    public short getSelection() {
        return fsSelection;
    }

    public int getFirstCharIndex() {
        return usFirstCharIndex;
    }

    public int getLastCharIndex() {
        return usLastCharIndex;
    }

    public short getTypoAscender() {
        return sTypoAscender;
    }

    public short getTypoDescender() {
        return sTypoDescender;
    }

    public short getTypoLineGap() {
        return sTypoLineGap;
    }

    public int getWinAscent() {
        return usWinAscent;
    }

    public int getWinDescent() {
        return usWinDescent;
    }

    public int getCodePageRange1() {
        return ulCodePageRange1;
    }

    public int getCodePageRange2() {
        return ulCodePageRange2;
    }

    public int getType() {
        return OS_2;
    }
}
