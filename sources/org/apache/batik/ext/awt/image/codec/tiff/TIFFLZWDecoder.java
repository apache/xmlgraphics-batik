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

package org.apache.batik.ext.awt.image.codec.tiff;

/**
 * A class for performing LZW decoding.
 *
 *
 */
public class TIFFLZWDecoder {

    byte stringTable[][];
    byte data[] = null, uncompData[];
    int tableIndex, bitsToGet = 9;
    int bytePointer, bitPointer;
    int dstIndex;
    int w, h;
    int predictor, samplesPerPixel;
    int nextData = 0;
    int nextBits = 0;

    int andTable[] = {
	511, 
	1023,
	2047,
	4095
    };
    
    public TIFFLZWDecoder(int w, int predictor, int samplesPerPixel) {
	this.w = w;
	this.predictor = predictor;
	this.samplesPerPixel = samplesPerPixel;
    }

    /**
     * Method to decode LZW compressed data.
     *
     * @param data            The compressed data.
     * @param uncompData      Array to return the uncompressed data in.
     * @param h               The number of rows the compressed data contains.
     */
    public byte[] decode(byte data[], byte uncompData[], int h) {

        if(data[0] == (byte)0x00 && data[1] == (byte)0x01) {
            throw new UnsupportedOperationException("TIFFLZWDecoder0");
        }

	initializeStringTable();

	this.data = data;	
	this.h = h;
	this.uncompData = uncompData;
	
	// Initialize pointers
	bytePointer = 0;
	bitPointer = 0;
	dstIndex = 0;


	nextData = 0;
	nextBits = 0;

	int code, oldCode = 0;
	byte string[];
 
	while ( ((code = getNextCode()) != 257) && 
		dstIndex != uncompData.length) {

	    if (code == 256) {

		initializeStringTable();
		code = getNextCode();

		if (code == 257) {
		    break;
		}

		writeString(stringTable[code]);
		oldCode = code;

	    } else {

		if (code < tableIndex) {

		    string = stringTable[code];

		    writeString(string);
		    addStringToTable(stringTable[oldCode], string[0]); 
		    oldCode = code;

		} else {

		    string = stringTable[oldCode];
		    string = composeString(string, string[0]);
		    writeString(string);
		    addStringToTable(string);
		    oldCode = code;
		}

	    }

	}

	// Horizontal Differencing Predictor
	if (predictor == 2) {

	    int count;
	    for (int j = 0; j < h; j++) {
		
		count = samplesPerPixel * (j * w + 1);
		
		for (int i = samplesPerPixel; i < w * samplesPerPixel; i++) {
		    
		    uncompData[count] += uncompData[count - samplesPerPixel];
		    count++;
		}
	    }
	}

	return uncompData;
    }


    /**
     * Initialize the string table.
     */
    public void initializeStringTable() {

	stringTable = new byte[4096][];
	
	for (int i=0; i<256; i++) {
	    stringTable[i] = new byte[1];
	    stringTable[i][0] = (byte)i;
	}
	
	tableIndex = 258;
	bitsToGet = 9;
    }

    /**
     * Write out the string just uncompressed.
     */
    public void writeString(byte string[]) {
	
	for (int i=0; i<string.length; i++) {
	    uncompData[dstIndex++] = string[i];
	}
    }
    
    /**
     * Add a new string to the string table.
     */
    public void addStringToTable(byte oldString[], byte newString) {
	int length = oldString.length;
	byte string[] = new byte[length + 1];
	System.arraycopy(oldString, 0, string, 0, length);
	string[length] = newString;
	
	// Add this new String to the table
	stringTable[tableIndex++] = string;
	
	if (tableIndex == 511) {
	    bitsToGet = 10;
	} else if (tableIndex == 1023) {
	    bitsToGet = 11;
	} else if (tableIndex == 2047) {
	    bitsToGet = 12;
	} 
    }

    /**
     * Add a new string to the string table.
     */
    public void addStringToTable(byte string[]) {
	
	// Add this new String to the table
	stringTable[tableIndex++] = string;
	
	if (tableIndex == 511) {
	    bitsToGet = 10;
	} else if (tableIndex == 1023) {
	    bitsToGet = 11;
	} else if (tableIndex == 2047) {
	    bitsToGet = 12;
	} 
    }

    /**
     * Append <code>newString</code> to the end of <code>oldString</code>.
     */
    public byte[] composeString(byte oldString[], byte newString) {
	int length = oldString.length;
	byte string[] = new byte[length + 1];
	System.arraycopy(oldString, 0, string, 0, length);
	string[length] = newString;

	return string;
    }

    // Returns the next 9, 10, 11 or 12 bits
    public int getNextCode() {
        // Attempt to get the next code. The exception is caught to make
        // this robust to cases wherein the EndOfInformation code has been
        // omitted from a strip. Examples of such cases have been observed
        // in practice.
        try {
            nextData = (nextData << 8) | (data[bytePointer++] & 0xff);
            nextBits += 8;

            if (nextBits < bitsToGet) {
                nextData = (nextData << 8) | (data[bytePointer++] & 0xff);
                nextBits += 8;
            }

            int code =
                (nextData >> (nextBits - bitsToGet)) & andTable[bitsToGet-9];
            nextBits -= bitsToGet;

            return code;
        } catch(ArrayIndexOutOfBoundsException e) {
            // Strip not terminated as expected: return EndOfInformation code.
            return 257;
        }
    }
}
