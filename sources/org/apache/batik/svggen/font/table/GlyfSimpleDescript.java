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

import java.io.ByteArrayInputStream;

/**
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class GlyfSimpleDescript extends GlyfDescript {

    private int[] endPtsOfContours;
    private byte[] flags;
    private short[] xCoordinates;
    private short[] yCoordinates;
    private int count;

    public GlyfSimpleDescript(GlyfTable parentTable, short numberOfContours, ByteArrayInputStream bais) {

        super(parentTable, numberOfContours, bais);
        
        // Simple glyph description
        endPtsOfContours = new int[numberOfContours];
        for (int i = 0; i < numberOfContours; i++) {
            endPtsOfContours[i] = (bais.read()<<8 | bais.read());
        }

        // The last end point index reveals the total number of points
        count = endPtsOfContours[numberOfContours-1] + 1;
        flags = new byte[count];
        xCoordinates = new short[count];
        yCoordinates = new short[count];

        int instructionCount = (bais.read()<<8 | bais.read());
        readInstructions(bais, instructionCount);
        readFlags(count, bais);
        readCoords(count, bais);
    }

    public int getEndPtOfContours(int i) {
        return endPtsOfContours[i];
    }

    public byte getFlags(int i) {
        return flags[i];
    }

    public short getXCoordinate(int i) {
        return xCoordinates[i];
    }

    public short getYCoordinate(int i) {
        return yCoordinates[i];
    }

    public boolean isComposite() {
        return false;
    }

    public int getPointCount() {
        return count;
    }

    public int getContourCount() {
        return getNumberOfContours();
    }
    /*
    public int getComponentIndex(int c) {
    return 0;
    }

    public int getComponentCount() {
    return 1;
    }
     */
    /**
     * The table is stored as relative values, but we'll store them as absolutes
     */
    private void readCoords(int count, ByteArrayInputStream bais) {
        short x = 0;
        short y = 0;
        for (int i = 0; i < count; i++) {
            if ((flags[i] & xDual) != 0) {
                if ((flags[i] & xShortVector) != 0) {
                    x += (short) bais.read();
                }
            } else {
                if ((flags[i] & xShortVector) != 0) {
                    x += (short) -((short) bais.read());
                } else {
                    x += (short)(bais.read()<<8 | bais.read());
                }
            }
            xCoordinates[i] = x;
        }

        for (int i = 0; i < count; i++) {
            if ((flags[i] & yDual) != 0) {
                if ((flags[i] & yShortVector) != 0) {
                    y += (short) bais.read();
                }
            } else {
                if ((flags[i] & yShortVector) != 0) {
                    y += (short) -((short) bais.read());
                } else {
                    y += (short)(bais.read()<<8 | bais.read());
                }
            }
            yCoordinates[i] = y;
        }
    }

    /**
     * The flags are run-length encoded
     */
    private void readFlags(int flagCount, ByteArrayInputStream bais) {
        try {
            for (int index = 0; index < flagCount; index++) {
                flags[index] = (byte) bais.read();
                if ((flags[index] & repeat) != 0) {
                    int repeats = bais.read();
                    for (int i = 1; i <= repeats; i++) {
                        flags[index + i] = flags[index];
                    }
                    index += repeats;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("error: array index out of bounds");
        }
    }
}
