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
public class NameRecord {

    private short platformId;
    private short encodingId;
    private short languageId;
    private short nameId;
    private short stringLength;
    private short stringOffset;
    private String record;

    protected NameRecord(RandomAccessFile raf) throws IOException {
        platformId = raf.readShort();
        encodingId = raf.readShort();
        languageId = raf.readShort();
        nameId = raf.readShort();
        stringLength = raf.readShort();
        stringOffset = raf.readShort();
    }
    
    public short getEncodingId() {
        return encodingId;
    }
    
    public short getLanguageId() {
        return languageId;
    }
    
    public short getNameId() {
        return nameId;
    }
    
    public short getPlatformId() {
        return platformId;
    }

    public String getRecordString() {
        return record;
    }

    protected void loadString(RandomAccessFile raf, int stringStorageOffset) throws IOException {
        StringBuffer sb = new StringBuffer();
        raf.seek(stringStorageOffset + stringOffset);
        if (platformId == Table.platformAppleUnicode) {
            
            // Unicode (big-endian)
            for (int i = 0; i < stringLength/2; i++) {
                sb.append(raf.readChar());
            }
        } else if (platformId == Table.platformMacintosh) {

            // Macintosh encoding, ASCII
            for (int i = 0; i < stringLength; i++) {
                sb.append((char) raf.readByte());
            }
        } else if (platformId == Table.platformISO) {
            
            // ISO encoding, ASCII
            for (int i = 0; i < stringLength; i++) {
                sb.append((char) raf.readByte());
            }
        } else if (platformId == Table.platformMicrosoft) {
            
            // Microsoft encoding, Unicode
            char c;
            for (int i = 0; i < stringLength/2; i++) {
                c = raf.readChar();
                sb.append(c);
            }
        }
        record = sb.toString();
    }
}
