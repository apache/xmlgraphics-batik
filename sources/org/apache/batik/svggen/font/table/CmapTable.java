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
public class CmapTable implements Table {

    private int version;
    private int numTables;
    private CmapIndexEntry[] entries;
    private CmapFormat[] formats;

    protected CmapTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        long fp = raf.getFilePointer();
        version = raf.readUnsignedShort();
        numTables = raf.readUnsignedShort();
        entries = new CmapIndexEntry[numTables];
        formats = new CmapFormat[numTables];

        // Get each of the index entries
        for (int i = 0; i < numTables; i++) {
            entries[i] = new CmapIndexEntry(raf);
        }

        // Get each of the tables
        for (int i = 0; i < numTables; i++) {
            raf.seek(fp + entries[i].getOffset());
            int format = raf.readUnsignedShort();
            formats[i] = CmapFormat.create(format, raf);
        }
    }

    public CmapFormat getCmapFormat(short platformId, short encodingId) {

        // Find the requested format
        for (int i = 0; i < numTables; i++) {
            if (entries[i].getPlatformId() == platformId
                    && entries[i].getEncodingId() == encodingId) {
                return formats[i];
            }
        }
        return null;
    }

    public int getType() {
        return cmap;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer().append("cmap\n");

        // Get each of the index entries
        for (int i = 0; i < numTables; i++) {
            sb.append("\t").append(entries[i].toString()).append("\n");
        }

        // Get each of the tables
        for (int i = 0; i < numTables; i++) {
            sb.append("\t").append(formats[i].toString()).append("\n");
        }
        return sb.toString();
    }
}
