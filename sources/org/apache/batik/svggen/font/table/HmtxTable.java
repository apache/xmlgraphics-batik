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
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class HmtxTable implements Table {

    private byte[] buf = null;
    private int[] hMetrics = null;
    private short[] leftSideBearing = null;

    protected HmtxTable(DirectoryEntry de,RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        buf = new byte[de.getLength()];
        raf.read(buf);
/*
        TableMaxp t_maxp = (TableMaxp) td.getEntryByTag(maxp).getTable();
        TableHhea t_hhea = (TableHhea) td.getEntryByTag(hhea).getTable();
        int lsbCount = t_maxp.getNumGlyphs() - t_hhea.getNumberOfHMetrics();
        hMetrics = new int[t_hhea.getNumberOfHMetrics()];
        for (int i = 0; i < t_hhea.getNumberOfHMetrics(); i++) {
            hMetrics[i] = raf.readInt();
        }
        if (lsbCount > 0) {
            leftSideBearing = new short[lsbCount];
            for (int i = 0; i < lsbCount; i++) {
                leftSideBearing[i] = raf.readShort();
            }
        }
*/
    }

    public void init(int numberOfHMetrics, int lsbCount) {
        if (buf == null) {
            return;
        }
        hMetrics = new int[numberOfHMetrics];
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        for (int i = 0; i < numberOfHMetrics; i++) {
            hMetrics[i] = (bais.read()<<24 | bais.read()<<16 | 
                           bais.read()<< 8 | bais.read());
        }
        if (lsbCount > 0) {
            leftSideBearing = new short[lsbCount];
            for (int i = 0; i < lsbCount; i++) {
                leftSideBearing[i] = (short)(bais.read()<<8 | bais.read());
            }
        }
        buf = null;
    }

    public int getAdvanceWidth(int i) {
        if (hMetrics == null) {
            return 0;
        }
        if (i < hMetrics.length) {
            return hMetrics[i] >> 16;
        } else {
            return hMetrics[hMetrics.length - 1] >> 16;
        }
    }

    public short getLeftSideBearing(int i) {
        if (hMetrics == null) {
            return 0;
        }
        if (i < hMetrics.length) {
            return (short)(hMetrics[i] & 0xffff);
        } else {
            return leftSideBearing[i - hMetrics.length];
        }
    }

    public int getType() {
        return hmtx;
    }
}
