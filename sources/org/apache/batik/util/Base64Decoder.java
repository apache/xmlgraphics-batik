/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class implements a Base64 Character decoder as specified in RFC1113.
 * Unlike some other encoding schemes there is nothing in this encoding that
 * tells the decoder where a buffer starts or stops, so to use it you will need
 * to isolate your encoded data into a single chunk and then feed them
 * this decoder. The simplest way to do that is to read all of the encoded
 * data into a string and then use:
 * <pre>
 *      byte    mydata[];
 *      Base64Decoder base64 = new Base64Decoder();
 *
 *      mydata = base64.decodeBuffer(bufferString);
 * </pre>
 * This will decode the String in <i>bufferString</i> and give you an array
 * of bytes in the array <i>myData</i>.
 *
 * On errors, this class throws a IOException with the following detail
 * strings:
 * <pre>
 *    "Base64Decoder: Bad Padding byte (2)."
 *    "Base64Decoder: Bad Padding byte (1)."
 * </pre>
 *
 * @author <a href="vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author      Chuck McManis
 * @version $Id$
 *
 * @see         CharacterEncoder
 * @see         Base64Decoder
 */

public class Base64Decoder extends CharacterDecoder {

    /** This class has 3 bytes per atom */
    int bytesPerAtom() {
        return (3);
    }

    /** This class has 48 bytes per encoded line */
    int bytesPerLine() {
        return (48);
    }

    /**
     * This character array provides the character to value map
     * based on RFC1113.
     */
    private final static char pem_array[] = {
        //       0   1   2   3   4   5   6   7
        'A','B','C','D','E','F','G','H', // 0
        'I','J','K','L','M','N','O','P', // 1
        'Q','R','S','T','U','V','W','X', // 2
        'Y','Z','a','b','c','d','e','f', // 3
        'g','h','i','j','k','l','m','n', // 4
        'o','p','q','r','s','t','u','v', // 5
        'w','x','y','z','0','1','2','3', // 6
        '4','5','6','7','8','9','+','/'  // 7
    };

    byte decode_buffer[] = new byte[4];

    /**
     * Decode one Base64 atom into 1, 2, or 3 bytes of data.
     */
    void decodeAtom(InputStream inStream, OutputStream outStream, int l) throws EOFException, IOException{
        int     i;
        byte    a = -1, b = -1, c = -1, d = -1;
        StringBuffer s = new StringBuffer(4);

        decode_buffer[0] = (byte) inStream.read();
        if (decode_buffer[0] == -1) {
            throw new EOFException();
        }

        // check to see if we caught the trailing end of a <CR><LF>
        if (decode_buffer[0] == '\n') {
            i = inStream.read(decode_buffer, 0, 4);
        } else {
            i = inStream.read(decode_buffer, 1, 3);
        }
        if (i == -1) {
            throw new EOFException();
        }

        for (i = 0; i < 64; i++) {
            if (decode_buffer[0] == pem_array[i]) {
                a = (byte) i;
            }
            if (decode_buffer[1] == pem_array[i]) {
                b = (byte) i;
            }
            if (decode_buffer[2] == pem_array[i]) {
                c = (byte) i;
            }
            if (decode_buffer[3] == pem_array[i]) {
                d = (byte) i;
            }
        }
        if ((l == 2) && (decode_buffer[3] != '=')) {
            throw new IOException("Base64Decoder: Bad Padding byte (2).");
        }
        if ((l == 1) &&
            ((decode_buffer[2] != '=') || (decode_buffer[3] != '='))) {
            throw new IOException("Base64Decoder: Bad Padding byte (1).");
        }

        for (i = 0; i < 4; i++) s.append((char) decode_buffer[i]);
        switch (l) {
        case 1:
            outStream.write( (byte)(((a << 2) & 0xfc) | ((b >>> 4) & 3)) );
            break;
        case 2:
            outStream.write( (byte) (((a << 2) & 0xfc) | ((b >>> 4) & 3)) );
            outStream.write( (byte) (((b << 4) & 0xf0) | ((c >>> 2) & 0xf)) );
            break;
        case 3:
            outStream.write( (byte) (((a << 2) & 0xfc) | ((b >>> 4) & 3)) );
            outStream.write( (byte) (((b << 4) & 0xf0) | ((c >>> 2) & 0xf)) );
            outStream.write( (byte) (((c << 6) & 0xc0) | (d  & 0x3f)) );
            break;
        }
        return;
    }

    /**
     * decodeLineSuffix in this decoder simply finds the [newLine] and
     * positions us past it.
     */
    void decodeLineSuffix(InputStream inStream, OutputStream outStream) throws EOFException, IOException{
        int c;

        while (true) {
            c = inStream.read();
            if (c == -1) {
                throw new EOFException();
            }
            if ((c == '\n') || (c == '\r') || (c == ' ')) {
                break;
            }
        }
    }
}
