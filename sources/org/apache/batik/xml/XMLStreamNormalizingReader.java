/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.xml;

import java.io.InputStream;
import java.io.IOException;
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
