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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;

import org.apache.batik.util.EncodingUtilities;

/**
 * A collection of utility functions for XML.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class XMLUtilities extends XMLCharacters {

    /**
     * This class does not need to be instantiated.
     */
    protected XMLUtilities() {
    }

    /**
     * Tests whether the given character is a valid space.
     */
    public static boolean isXMLSpace(char c) {
      return (c <= 0x0020) &&
             (((((1L << 0x0009) |
                 (1L << 0x000A) |
                 (1L << 0x000D) |
                 (1L << 0x0020)) >> c) & 1L) != 0);
    }

    /**
     * Tests whether the given character is usable as the
     * first character of an XML name.
     */
    public static boolean isXMLNameFirstCharacter(char c) {
	return (NAME_FIRST_CHARACTER[c / 32] & (1 << (c % 32))) != 0;
    }

    /**
     * Tests whether the given character is a valid XML name character.
     */
    public static boolean isXMLNameCharacter(char c) {
	return (NAME_CHARACTER[c / 32] & (1 << (c % 32))) != 0;
    }

    /**
     * Tests whether the given 32 bits character is valid in XML documents.
     */
    public static boolean isXMLCharacter(int c) {
	return (c >= 0x10000 && c <= 0x10ffff) ||
	    (XML_CHARACTER[c / 32] & (1 << (c % 32))) != 0;
    }

    /**
     * Tests whether the given character is a valid XML public ID character.
     */
    public static boolean isXMLPublicIdCharacter(char c) {
	return (c < 128) &&
            (PUBLIC_ID_CHARACTER[c / 32] & (1 << (c % 32))) != 0;
    }

    /**
     * Tests whether the given character is a valid XML version character.
     */
    public static boolean isXMLVersionCharacter(char c) {
	return (c < 128) &&
            (VERSION_CHARACTER[c / 32] & (1 << (c % 32))) != 0;
    }

    /**
     * Tests whether the given character is a valid aphabetic character.
     */
    public static boolean isXMLAlphabeticCharacter(char c) {
	return (c < 128) &&
            (ALPHABETIC_CHARACTER[c / 32] & (1 << (c % 32))) != 0;
    }

    /**
     * Creates a Reader initialized to scan the characters in the given
     * XML document's InputStream.
     * @param is The input stream positionned at the beginning of an
     *        XML document.
     * @return a Reader positionned at the beginning of the XML document
     *         It is created from an encoding figured out from the first
     *         few bytes of the document. As a consequence the given
     *         input stream is not positionned anymore at the beginning
     *         of the document when this method returns.
     */
    public static Reader createXMLDocumentReader(InputStream is)
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
                    return new InputStreamReader(pbis, "UnicodeBig");
                }
                break;

            case '<':
                switch (buf[1] & 0x00FF) {
                case 0:
                    if (buf[2] == 0x003f && buf[3] == 0x0000) {
                        return new InputStreamReader(pbis, "UnicodeLittle");
                    }
                    break;

                case '?':
                    if (buf[2] == 'x' && buf[3] == 'm') {
                        Reader r = createXMLDeclarationReader(pbis, "UTF8");
                        String enc = getXMLDeclarationEncoding(r, "UTF8");
                        return new InputStreamReader(pbis, enc);
                    }
                }
                break;

            case 0x004C:
                if (buf[1] == 0x006f &&
                    (buf[2] & 0x00FF) == 0x00a7 &&
                    (buf[3] & 0x00FF) == 0x0094) {
                    Reader r = createXMLDeclarationReader(pbis, "CP037");
                    String enc = getXMLDeclarationEncoding(r, "CP037");
                    return new InputStreamReader(pbis, enc);
                }
                break;

            case 0x00FE:
                if ((buf[1] & 0x00FF) == 0x00FF) {
                    return new InputStreamReader(pbis, "Unicode");
                }
                break;

            case 0x00FF:
                if ((buf[1] & 0x00FF) == 0x00FE) {
                    return new InputStreamReader(pbis, "Unicode");
                }
            }
        }

        return new InputStreamReader(pbis, "UTF8");
    }

    /**
     * Creates a reader from the given input stream and encoding.
     * This method assumes the input stream working buffer is at least
     * 128 byte long. The input stream is restored before this method
     * returns. The 4 first bytes are skipped before creating the reader.
     */
    protected static Reader createXMLDeclarationReader(PushbackInputStream pbis,
                                                       String enc)
        throws IOException {
        byte[] buf = new byte[128];
        int len = pbis.read(buf);

        if (len > 0) {
            pbis.unread(buf, 0, len);
        }

        return new InputStreamReader(new ByteArrayInputStream(buf, 4, len), enc);
    }

    /**
     * Reads an XML declaration to get the encoding declaration value.
     * @param r a reader positionned just after '<?xm'.
     * @param e the encoding to return by default or on error.
     */
    protected static String getXMLDeclarationEncoding(Reader r, String e)
        throws IOException {
        int c;

        if ((c = r.read()) != 'l') {
            return e;
        }

        if (!isXMLSpace((char)(c = r.read()))) {
            return e;
        }

        while (isXMLSpace((char)(c = r.read())));
            
        if (c != 'v') {
            return e;
        }
        if ((c = r.read()) != 'e') {
            return e;
        }
        if ((c = r.read()) != 'r') {
            return e;
        }
        if ((c = r.read()) != 's') {
            return e;
        }
        if ((c = r.read()) != 'i') {
            return e;
        }
        if ((c = r.read()) != 'o') {
            return e;
        }
        if ((c = r.read()) != 'n') {
            return e;
        }
             
        c = r.read();
        while (isXMLSpace((char)c)) {
            c = r.read();
        }

        if (c != '=') {
            return e;
        }

        while (isXMLSpace((char)(c = r.read())));
            
        if (c != '"' && c != '\'') {
            return e;
        }
        char sc = (char)c;

        for (;;) {
            c = r.read();
            if (c == sc) {
                break;
            }
            if (!isXMLVersionCharacter((char)c)) {
                return e;
            }
        }

        if (!isXMLSpace((char)(c = r.read()))) {
            return e;
        }
        while (isXMLSpace((char)(c = r.read())));

        if (c != 'e') {
            return e;
        }
        if ((c = r.read()) != 'n') {
            return e;
        }
        if ((c = r.read()) != 'c') {
            return e;
        }
        if ((c = r.read()) != 'o') {
            return e;
        }
        if ((c = r.read()) != 'd') {
            return e;
        }
        if ((c = r.read()) != 'i') {
            return e;
        }
        if ((c = r.read()) != 'n') {
            return e;
        }
        if ((c = r.read()) != 'g') {
            return e;
        }

        c = r.read();
        while (isXMLSpace((char)c)) {
            c = r.read();
        }

        if (c != '=') {
            return e;
        }

        while (isXMLSpace((char)(c = r.read())));
            
        if (c != '"' && c != '\'') {
            return e;
        }
        sc = (char)c;

        StringBuffer enc = new StringBuffer();
        for (;;) {
            c = r.read();
            if (c == -1) {
                return e;
            }
            if (c == sc) {
                return encodingToJavaEncoding(enc.toString(), e);
            }
            enc.append((char)c);
        }
    }

    /**
     * Converts the given standard encoding representation to the
     * corresponding Java encoding string.
     * @param e the encoding string to convert.
     * @param de the encoding string if no corresponding encoding was found.
     */
    public static String encodingToJavaEncoding(String e, String de) {
        String result = EncodingUtilities.javaEncoding(e);
        return (result == null) ? de : result;
    }
}
