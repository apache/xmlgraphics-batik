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

package org.apache.batik.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.Reader;

import org.apache.batik.util.io.StreamNormalizingReader;
import org.apache.batik.util.io.UTF16Decoder;

/**
 * This class represents a normalizing reader with encoding detection
 * management.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class XMLStreamNormalizingReader extends StreamNormalizingReader {
    
    /**
     * Creates a new XMLStreamNormalizingReader.
     * @param is The input stream to read.
     * @param encod The character encoding to use if the auto-detection fail.
     */
    public XMLStreamNormalizingReader(InputStream is, String encod)
        throws IOException {
        PushbackInputStream pbis = new PushbackInputStream(is, 128);
        byte[] buf = new byte[4];

        int len = pbis.read(buf);
        if (len > 0) {
            pbis.unread(buf, 0, len);
        }

        if (len == 4) {
            switch (buf[0] & 0x00FF) {
            case 0:
                if (buf[1] == 0x003c && buf[2] == 0x0000 && buf[3] == 0x003f) {
                    charDecoder = new UTF16Decoder(pbis, true);
                    return;
                }
                break;

            case '<':
                switch (buf[1] & 0x00FF) {
                case 0:
                    if (buf[2] == 0x003f && buf[3] == 0x0000) {
                        charDecoder = new UTF16Decoder(pbis, false);
                        return;
                    }
                    break;

                case '?':
                    if (buf[2] == 'x' && buf[3] == 'm') {
                        Reader r = XMLUtilities.createXMLDeclarationReader
                            (pbis, "UTF8");
                        String enc = XMLUtilities.getXMLDeclarationEncoding
                            (r, "UTF-8");
                        charDecoder = createCharDecoder(pbis, enc);
                        return;
                    }
                }
                break;

            case 0x004C:
                if (buf[1] == 0x006f &&
                    (buf[2] & 0x00FF) == 0x00a7 &&
                    (buf[3] & 0x00FF) == 0x0094) {
                    Reader r = XMLUtilities.createXMLDeclarationReader
                        (pbis, "CP037");
                    String enc = XMLUtilities.getXMLDeclarationEncoding
                        (r, "EBCDIC-CP-US");
                    charDecoder = createCharDecoder(pbis, enc);
                    return;
                }
                break;

            case 0x00FE:
                if ((buf[1] & 0x00FF) == 0x00FF) {
                    charDecoder = createCharDecoder(pbis, "UTF-16");
                    return;
                }
                break;

            case 0x00FF:
                if ((buf[1] & 0x00FF) == 0x00FE) {
                    charDecoder = createCharDecoder(pbis, "UTF-16");
                    return;
                }
            }
        }

        encod = (encod == null) ? "UTF-8" : encod;
        charDecoder = createCharDecoder(pbis, encod);
    }
}
