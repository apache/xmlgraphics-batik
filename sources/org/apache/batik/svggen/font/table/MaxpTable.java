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
public class MaxpTable implements Table {

    private int versionNumber;
    private int numGlyphs;
    private int maxPoints;
    private int maxContours;
    private int maxCompositePoints;
    private int maxCompositeContours;
    private int maxZones;
    private int maxTwilightPoints;
    private int maxStorage;
    private int maxFunctionDefs;
    private int maxInstructionDefs;
    private int maxStackElements;
    private int maxSizeOfInstructions;
    private int maxComponentElements;
    private int maxComponentDepth;

    protected MaxpTable(DirectoryEntry de,RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        versionNumber = raf.readInt();
        numGlyphs = raf.readUnsignedShort();
        maxPoints = raf.readUnsignedShort();
        maxContours = raf.readUnsignedShort();
        maxCompositePoints = raf.readUnsignedShort();
        maxCompositeContours = raf.readUnsignedShort();
        maxZones = raf.readUnsignedShort();
        maxTwilightPoints = raf.readUnsignedShort();
        maxStorage = raf.readUnsignedShort();
        maxFunctionDefs = raf.readUnsignedShort();
        maxInstructionDefs = raf.readUnsignedShort();
        maxStackElements = raf.readUnsignedShort();
        maxSizeOfInstructions = raf.readUnsignedShort();
        maxComponentElements = raf.readUnsignedShort();
        maxComponentDepth = raf.readUnsignedShort();
    }

    public int getMaxComponentDepth() {
        return maxComponentDepth;
    }

    public int getMaxComponentElements() {
        return maxComponentElements;
    }

    public int getMaxCompositeContours() {
        return maxCompositeContours;
    }

    public int getMaxCompositePoints() {
        return maxCompositePoints;
    }

    public int getMaxContours() {
        return maxContours;
    }

    public int getMaxFunctionDefs() {
        return maxFunctionDefs;
    }

    public int getMaxInstructionDefs() {
        return maxInstructionDefs;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public int getMaxSizeOfInstructions() {
        return maxSizeOfInstructions;
    }

    public int getMaxStackElements() {
        return maxStackElements;
    }

    public int getMaxStorage() {
        return maxStorage;
    }

    public int getMaxTwilightPoints() {
        return maxTwilightPoints;
    }

    public int getMaxZones() {
        return maxZones;
    }

    public int getNumGlyphs() {
        return numGlyphs;
    }

    public int getType() {
        return maxp;
    }
}
