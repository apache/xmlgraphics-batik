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
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This class implements a Base64 Character encoder as specified in RFC1113.
 * Unlike some other encoding schemes there is nothing in this encoding
 * that indicates where a buffer starts or ends.
 *
 * This means that the encoded text will simply start with the first line
 * of encoded text and end with the last line of encoded text.
 *
 * @author <a href="deweese@apache.org">Thomas DeWeese</a>
 * @author <a href="vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author      Chuck McManis
 * @version $Id$
 */
public class Base64EncoderStream extends OutputStream {

    /** This array maps the 6 bit values to their characters */
    private final static byte pem_array[] = {
    //   0   1   2   3   4   5   6   7
        'A','B','C','D','E','F','G','H', // 0
        'I','J','K','L','M','N','O','P', // 1
        'Q','R','S','T','U','V','W','X', // 2
        'Y','Z','a','b','c','d','e','f', // 3
        'g','h','i','j','k','l','m','n', // 4
        'o','p','q','r','s','t','u','v', // 5
        'w','x','y','z','0','1','2','3', // 6
        '4','5','6','7','8','9','+','/'  // 7
    };

    byte [] atom = new byte[3];
    int     atomLen = 0;
    byte [] encodeBuf = new byte[4];
    int     lineLen = 0;

    PrintStream  out;
    boolean closeOutOnClose;

    public Base64EncoderStream(OutputStream out) {
        this.out = new PrintStream(out);
        closeOutOnClose = true;
    }

    public Base64EncoderStream(OutputStream out, boolean closeOutOnClose) {
        this.out = new PrintStream(out);
        this.closeOutOnClose = closeOutOnClose;
    }

    public void close () throws IOException {
        if (out != null) {
            encodeAtom();
            out.flush();        
            if (closeOutOnClose)
                out.close();
            out=null;
        }
    }

    /**
     * This can't really flush out output since that may generate
     * '=' chars which would indicate the end of the stream.
     * Instead we flush out.  You can only be sure all output is 
     * writen by closing this stream.
     */
    public void flush() throws IOException {
        out.flush();
    }

    public void write(int b) throws IOException {
        atom[atomLen++] = (byte)b;
        if (atomLen == 3)
            encodeAtom();
    }

    public void write(byte []data) throws IOException {
        encodeFromArray(data, 0, data.length);
    }

    public void write(byte [] data, int off, int len) throws IOException {
        encodeFromArray(data, off, len);
    }

    /**
     * enocodeAtom - Take three bytes of input and encode it as 4
     * printable characters. Note that if the length in len is less
     * than three is encodes either one or two '=' signs to indicate
     * padding characters.
     */
    void encodeAtom() throws IOException {
        byte a, b, c;

        switch (atomLen) {
        case 0: return;
        case 1:
            a = atom[0];
            encodeBuf[0] = pem_array[((a >>> 2) & 0x3F)];
            encodeBuf[1] = pem_array[((a <<  4) & 0x30)];
            encodeBuf[2] = encodeBuf[3] = '=';
            break;
        case 2:
            a = atom[0];
            b = atom[1];
            encodeBuf[0] = pem_array[((a >>> 2) & 0x3F)];
            encodeBuf[1] = pem_array[(((a << 4) & 0x30) | ((b >>> 4) & 0x0F))];
            encodeBuf[2] = pem_array[((b  << 2) & 0x3C)];
            encodeBuf[3] = '=';
            break;
        default:
            a = atom[0];
            b = atom[1];
            c = atom[2];
            encodeBuf[0] = pem_array[((a >>> 2) & 0x3F)];
            encodeBuf[1] = pem_array[(((a << 4) & 0x30) | ((b >>> 4) & 0x0F))];
            encodeBuf[2] = pem_array[(((b << 2) & 0x3C) | ((c >>> 6) & 0x03))];
            encodeBuf[3] = pem_array[c & 0x3F];
        }
        if (lineLen == 64) {
            out.println();
            lineLen = 0;
        }
        out.write(encodeBuf);

        lineLen += 4;
        atomLen = 0;
    }

    /**
     * enocodeAtom - Take three bytes of input and encode it as 4
     * printable characters. Note that if the length in len is less
     * than three is encodes either one or two '=' signs to indicate
     * padding characters.
     */
    void encodeFromArray(byte[] data, int offset, int len) 
        throws IOException{
        byte a, b, c;
        if (len == 0)
            return;

        // System.out.println("atomLen: " + atomLen + 
        //                    " len: " + len + 
        //                    " offset:  " + offset);

        if (atomLen != 0) {
            switch(atomLen) {
            case 1:
                atom[1] = data[offset++]; len--; atomLen++;
                if (len == 0) return;
                atom[2] = data[offset++]; len--; atomLen++;
                break;
            case 2:
                atom[2] = data[offset++]; len--; atomLen++;
                break;
            default:
            }
            encodeAtom();
        }

        while (len >=3) {
            a = data[offset++];
            b = data[offset++];
            c = data[offset++];
            
            encodeBuf[0] = pem_array[((a >>> 2) & 0x3F)];
            encodeBuf[1] = pem_array[(((a << 4) & 0x30) | ((b >>> 4) & 0x0F))];
            encodeBuf[2] = pem_array[(((b << 2) & 0x3C) | ((c >>> 6) & 0x03))];
            encodeBuf[3] = pem_array[c & 0x3F];
            out.write(encodeBuf);

            lineLen += 4;
            if (lineLen == 64) {
                out.println();
                lineLen = 0;
            }

            len -=3;
        }

        switch (len) {
        case 1:
            atom[0] = data[offset];
            break;
        case 2:
            atom[0] = data[offset];
            atom[1] = data[offset+1];
            break;
        default:
        }
        atomLen = len;
    }

    
    
}
