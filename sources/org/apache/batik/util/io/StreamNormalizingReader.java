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
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.util.EncodingUtilities;

/**
 * This class represents a NormalizingReader which handles streams of
 * bytes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StreamNormalizingReader extends NormalizingReader {

    /**
     * The char decoder.
     */
    protected CharDecoder charDecoder;

    /**
     * The next char.
     */
    protected int nextChar = -1;

    /**
     * The current line in the stream.
     */
    protected int line = 1;

    /**
     * The current column in the stream.
     */
    protected int column;

    /**
     * Creates a new NormalizingReader. The encoding is assumed to be
     * ISO-8859-1.
     * @param is The input stream to decode.
     */
    public StreamNormalizingReader(InputStream is) throws IOException {
        this(is, null);
    }

    /**
     * Creates a new NormalizingReader.
     * @param is The input stream to decode.
     * @param enc The standard encoding name. A null encoding means
     * ISO-8859-1.
     */
    public StreamNormalizingReader(InputStream is, String enc)
        throws IOException {
        if (enc == null) {
            enc = "ISO-8859-1";
        }
        charDecoder = createCharDecoder(is, enc);
    }

    /**
     * Creates a new NormalizingReader.
     * @param r The reader to wrap.
     */
    public StreamNormalizingReader(Reader r) throws IOException {
        charDecoder = new GenericDecoder(r);
    }

    /**
     * This constructor is intended for use by subclasses.
     */
    protected StreamNormalizingReader() {
    }

    /**
     * Read a single character.  This method will block until a
     * character is available, an I/O error occurs, or the end of the
     * stream is reached.
     */
    public int read() throws IOException {
        int result = nextChar;
        if (result != -1) {
            nextChar = -1;
            if (result == 13) {
                column = 0;
                line++;
            } else {
                column++;
            }
            return result;
        }
        result = charDecoder.readChar();
        switch (result) {
        case 13:
            column = 0;
            line++;
            int c = charDecoder.readChar();
            if (c == 10) {
                return 10;
            }
            nextChar = c;
            return 10;
                
        case 10:
            column = 0;
            line++;
        }
        return result;
    }

    /**
     * Returns the current line in the stream.
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the current column in the stream.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Close the stream.
     */
    public void close() throws IOException {
        charDecoder.dispose();
        charDecoder = null;
    }

    /**
     * Creates the CharDecoder mapped with the given encoding name.
     */
    protected CharDecoder createCharDecoder(InputStream is, String enc)
        throws IOException {
        CharDecoderFactory cdf =
            (CharDecoderFactory)charDecoderFactories.get(enc.toUpperCase());
        if (cdf != null) {
            return cdf.createCharDecoder(is);
        }
        String e = EncodingUtilities.javaEncoding(enc);
        if (e == null) {
            e = enc;
        }
        return new GenericDecoder(is, e);
    }

    /**
     * The CharDecoder factories map.
     */
    protected final static Map charDecoderFactories = new HashMap(11);
    static {
        CharDecoderFactory cdf = new ASCIIDecoderFactory();
        charDecoderFactories.put("ASCII", cdf);
        charDecoderFactories.put("US-ASCII", cdf);
        charDecoderFactories.put("ISO-8859-1", new ISO_8859_1DecoderFactory());
        charDecoderFactories.put("UTF-8", new UTF8DecoderFactory());
        charDecoderFactories.put("UTF-16", new UTF16DecoderFactory());
    }

    /**
     * Represents a CharDecoder factory.
     */
    protected interface CharDecoderFactory {
        CharDecoder createCharDecoder(InputStream is) throws IOException;
    }

    /**
     * To create an ASCIIDecoder.
     */
    protected static class ASCIIDecoderFactory
        implements CharDecoderFactory {
        public CharDecoder createCharDecoder(InputStream is)
            throws IOException {
            return new ASCIIDecoder(is);
        }
    }

    /**
     * To create an ISO_8859_1Decoder.
     */
    protected static class ISO_8859_1DecoderFactory
        implements CharDecoderFactory {
        public CharDecoder createCharDecoder(InputStream is)
            throws IOException {
            return new ISO_8859_1Decoder(is);
        }
    }

    /**
     * To create a UTF8Decoder.
     */
    protected static class UTF8DecoderFactory
        implements CharDecoderFactory {
        public CharDecoder createCharDecoder(InputStream is)
            throws IOException {
            return new UTF8Decoder(is);
        }
    }

    /**
     * To create a UTF16Decoder.
     */
    protected static class UTF16DecoderFactory
        implements CharDecoderFactory {
        public CharDecoder createCharDecoder(InputStream is)
            throws IOException {
            return new UTF16Decoder(is);
        }
    }
}
