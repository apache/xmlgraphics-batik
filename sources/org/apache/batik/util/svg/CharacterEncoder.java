/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
package org.apache.batik.util.svg;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;

/**
 * This class defines the encoding half of character encoders.
 * A character encoder is an algorithim for transforming 8 bit binary
 * data into text (generally 7 bit ASCII or 8 bit ISO-Latin-1 text)
 * for transmition over text channels such as e-mail and network news.
 *
 * The character encoders have been structured around a central theme
 * that, in general, the encoded text has the form:
 *
 * <pre>
 *      [Buffer Prefix]
 *      [Line Prefix][encoded data atoms][Line Suffix]
 *      [Buffer Suffix]
 * </pre>
 *
 * In the CharacterEncoder and CharacterDecoder classes, one complete
 * chunk of data is referred to as a <i>buffer</i>. Encoded buffers
 * are all text, and decoded buffers (sometimes just referred to as
 * buffers) are binary octets.
 *
 * To create a custom encoder, you must, at a minimum,  overide three
 * abstract methods in this class.
 * <DL>
 * <DD>bytesPerAtom which tells the encoder how many bytes to
 * send to encodeAtom
 * <DD>encodeAtom which encodes the bytes sent to it as text.
 * <DD>bytesPerLine which tells the encoder the maximum number of
 * bytes per line.
 * </DL>
 *
 * Several useful encoders have already been written and are
 * referenced in the See Also list below.
 *
 * @author <a href="vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author      Chuck McManis
 * @version $Id$
 *
 * @see         CharacterDecoder
 * @see         Base64Encoder
 */

public abstract class CharacterEncoder {

    /** Stream that understands "printing" */
    protected PrintStream pStream;

    /** Return the number of bytes per atom of encoding */
    abstract int bytesPerAtom();

    /** Return the number of bytes that can be encoded per line */
    abstract int bytesPerLine();

    /**
     * Encode the prefix for the entire buffer. By default is simply
     * opens the PrintStream for use by the other functions.
     */
    void encodeBufferPrefix(OutputStream aStream) {
        pStream = new PrintStream(aStream);
    }

    /**
     * Encode the suffix for the entire buffer.
     */
    void encodeBufferSuffix(OutputStream aStream) { }

    /**
     * Encode the prefix that starts every output line.
     */
    void encodeLinePrefix(OutputStream aStream, int aLength) { }

    /**
     * Encode the suffix that ends every output line. By default
     * this method just prints a <newline> into the output stream.
     */
    void encodeLineSuffix(OutputStream aStream) {
        pStream.println();
    }

    /** Encode one "atom" of information into characters. */
    abstract void encodeAtom(OutputStream aStream, byte someBytes[],
                             int anOffset, int aLength) throws IOException;

    /**
     * Encode bytes from the input stream, and write them as text characters
     * to the output stream. This method will run until it exhausts the
     * input stream.
     */
    public void encodeBuffer(InputStream inStream, OutputStream outStream) throws IOException{
        int     j;
        int     numBytes;
        byte    tmpbuffer[] = new byte[bytesPerLine()];

        encodeBufferPrefix(outStream);

        while (true) {
            numBytes = inStream.read(tmpbuffer);
            if (numBytes == -1) {
                break;
            }
            encodeLinePrefix(outStream, numBytes);
            for (j = 0; j < numBytes; j += bytesPerAtom()) {
                if ((j + bytesPerAtom()) <= numBytes) {
                    encodeAtom(outStream, tmpbuffer, j, bytesPerAtom());
                } else {
                    encodeAtom(outStream, tmpbuffer, j, tmpbuffer.length - j);
                }
            }
            encodeLineSuffix(outStream);
            if (numBytes < bytesPerLine()) {
                break;
            }
        }
        encodeBufferSuffix(outStream);
    }

    /**
     * Encode the buffer in <i>aBuffer</i> and write the encoded
     * result to the OutputStream <i>aStream</i>.
     */
    public void encodeBuffer(byte aBuffer[], OutputStream aStream) throws IOException{
        ByteArrayInputStream inStream = new ByteArrayInputStream(aBuffer);
        encodeBuffer(inStream, aStream);
    }

    /**
     * A 'streamless' version of encode that simply takes a buffer of
     * bytes and returns a string containing the encoded buffer.
     */
    public String encodeBuffer(byte aBuffer[]) throws IOException{
        ByteArrayOutputStream   outStream = new ByteArrayOutputStream();
        ByteArrayInputStream    inStream = new ByteArrayInputStream(aBuffer);
        encodeBuffer(inStream, outStream);
        return (outStream.toString());
    }

}
