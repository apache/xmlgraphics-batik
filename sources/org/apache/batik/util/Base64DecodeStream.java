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

package org.apache.batik.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class implements a Base64 Character decoder as specified in RFC1113.
 * Unlike some other encoding schemes there is nothing in this encoding that
 * tells the decoder where a buffer starts or stops, so to use it you will need
 * to isolate your encoded data into a single chunk and then feed them
 * this decoder. The simplest way to do that is to read all of the encoded
 * data into a string and then use:
 * <pre>
 *      byte    data[];
 *      InputStream is = new ByteArrayInputStream(data);
 *      is = new Base64DecodeStream(is);
 * </pre>
 *
 * On errors, this class throws a IOException with the following detail
 * strings:
 * <pre>
 *    "Base64DecodeStream: Bad Padding byte (2)."
 *    "Base64DecodeStream: Bad Padding byte (1)."
 * </pre>
 *
 * @author <a href="thomas.deweese@kodak.com">Thomas DeWeese</a>
 * @author <a href="vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author      Chuck McManis
 * @version $Id$
 */

public class Base64DecodeStream extends InputStream {

    InputStream src;

    public Base64DecodeStream(InputStream src) {
	this.src = src;
    }

    private final static byte pem_array[] = new byte[256];
    static {
        for (int i=0; i<pem_array.length; i++)
            pem_array[i] = -1;

        int idx = 0;
        for (char c='A'; c<='Z'; c++) {
            pem_array[c] = (byte)idx++;
        }
        for (char c='a'; c<='z'; c++) {
            pem_array[c] = (byte)idx++;
        }
	
        for (char c='0'; c<='9'; c++) {
            pem_array[c] = (byte)idx++;
        }

        pem_array['+'] = (byte)idx++;
        pem_array['/'] = (byte)idx++;
    }

    public boolean markSupported() { return false; }

    public void close() 
        throws IOException {
        EOF = true;
    }

    public int available() 
        throws IOException {
        return 3-out_offset;
    }

    byte decode_buffer[] = new byte[4];
    byte out_buffer[] = new byte[3];
    int  out_offset = 3;
    boolean EOF = false;

    public int read() throws IOException {

        if (out_offset == 3) {
            if (EOF || getNextAtom()) {
                EOF = true;
                return -1;
            }
        }

        return ((int)out_buffer[out_offset++])&0xFF;
    }

    public int read(byte []out, int offset, int len) 
        throws IOException {

        int idx = 0;
        while (idx < len) {
            if (out_offset == 3) {
                if (EOF || getNextAtom()) {
                    EOF = true;
                    if (idx == 0) return -1;
                    else          return idx;
                }
            }

            out[offset+idx] = out_buffer[out_offset++];

            idx++;
        }
        return idx;
    }

    final boolean getNextAtom() throws IOException {
        int count, a, b, c, d;

        int off = 0;
        while(off != 4) {
            count = src.read(decode_buffer, off, 4-off);
            if (count == -1)
                return true;

            int in=off, out=off;
            while(in < off+count) {
                if ((decode_buffer[in] != '\n') && 
                    (decode_buffer[in] != '\r') &&
                    (decode_buffer[in] != ' '))
                    decode_buffer[out++] = decode_buffer[in];
                in++;
            }

            off = out;
        }

        a = pem_array[((int)decode_buffer[0])&0xFF];
        b = pem_array[((int)decode_buffer[1])&0xFF];
        c = pem_array[((int)decode_buffer[2])&0xFF];
        d = pem_array[((int)decode_buffer[3])&0xFF];
	
        out_buffer[0] = (byte)((a<<2) | (b>>>4));
        out_buffer[1] = (byte)((b<<4) | (c>>>2));
        out_buffer[2] = (byte)((c<<6) |  d     );

        if (decode_buffer[3] != '=') {
            // All three bytes are good.
            out_offset=0;
        } else if (decode_buffer[2] == '=') {
            // Only one byte of output.
            out_buffer[2] = out_buffer[0];
            out_offset = 2;
            EOF=true;
        } else {
            // Only two bytes of output.
            out_buffer[2] = out_buffer[1];
            out_buffer[1] = out_buffer[0];
            out_offset = 1;
            EOF=true;
        }
            
        return false;
    }
}
