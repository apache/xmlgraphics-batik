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

package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents an object which decodes UTF-16 characters from
 * a stream of bytes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class UTF16Decoder extends AbstractCharDecoder {

    /**
     * Whether the stream's byte-order is big-endian.
     */
    protected boolean bigEndian;
    
    /**
     * Creates a new UTF16Decoder.
     * It is assumed that the byte-order mark is present.
     * @param is The stream to decode.
     */
    public UTF16Decoder(InputStream is) throws IOException {
        super(is);
        // Byte-order detection.
        int b1 = is.read();
        if (b1 == -1) {
            endOfStreamError("UTF-16");
        }
        int b2 = is.read();
        if (b2 == -1) {
            endOfStreamError("UTF-16");
        }
        int m = (((b1 & 0xff) << 8) | (b2 & 0xff));
        switch (m) {
        case 0xfeff:
            bigEndian = true;
            break;
        case 0xfffe:
            break;
        default:
            charError("UTF-16");
        }
    }

    /**
     * Creates a new UTF16Decoder.
     * @param is The stream to decode. 
     * @param be Whether or not the given stream's byte-order is
     * big-endian.
     */
    public UTF16Decoder(InputStream is, boolean be) {
        super(is);
        bigEndian = be;
    }

    /**
     * Reads the next character.
     * @return a character or END_OF_STREAM.
     */
    public int readChar() throws IOException {
        if (position == count) {
            fillBuffer();
        }
        if (count == -1) {
            return END_OF_STREAM;
        }
        byte b1 = buffer[position++];
        if (position == count) {
            fillBuffer();
        }
        if (count == -1) {
            endOfStreamError("UTF-16");
        }
        byte b2 = buffer[position++];
        int c = (bigEndian)
            ? (((b1 & 0xff) << 8) | (b2 & 0xff))
            : (((b2 & 0xff) << 8) | (b1 & 0xff));
        if (c == 0xfffe) {
            charError("UTF-16");
        }
        return c;
    }
}
