/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

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
	int idx = 0;
	for (int i=0; i<pem_array.length; i++)
	    pem_array[i] = -1;

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
    int  line_offset = 0;
    boolean EOF = false;

    public int read() throws IOException {

	if (EOF) return -1;

	if (out_offset == 3) {
	    if (getNextAtom()) {
		EOF = true;
		return -1;
	    }
	}

	return out_buffer[out_offset++];
    }

    public int read(byte []out, int offset, int len) 
	throws IOException {

	if (EOF) return -1;

	int idx = 0;
	while (idx < len) {
	    if (out_offset == 3) {
		if (getNextAtom()) {
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
	if (line_offset == 64) {
	    // End of current line so setup next one.
	    if (decodeLineSuffix()) 
		return true;
	    line_offset = 0;
	}

	decode_buffer[0] = (byte) src.read();
	if (decode_buffer[0] == -1)
	    return true;

	// check to see if we caught the trailing end of a <CR><LF>
	if (decode_buffer[0] == '\n') 
	    count = src.read(decode_buffer, 0, 4);
	else
	    count = src.read(decode_buffer, 1, 3);
	
	if (count == -1)
	    return true;
	line_offset+=4;

	a = pem_array[decode_buffer[0]];
	b = pem_array[decode_buffer[1]];
	c = pem_array[decode_buffer[2]];
	d = pem_array[decode_buffer[3]];
	
	out_buffer[0] = (byte)((a<<2) | (b>>>4));
	out_buffer[1] = (byte)((b<<4) | (c>>>2));
	out_buffer[2] = (byte)((c<<6) |  d     );
	out_offset = 0;
	return false;
    }

    /**
     * decodeLineSuffix in this decoder simply finds the [newLine] and
     * positions us past it.
     */
    boolean decodeLineSuffix() throws IOException{
        int c;

        while (true) {
            c = src.read();
            if (c == -1) return true;
            if ((c == '\n') || (c == '\r') || (c == ' ')) {
                return false;
            }
        }
    }
}
