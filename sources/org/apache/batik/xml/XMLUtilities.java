/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.Reader;

import java.util.HashMap;
import java.util.Map;

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
        String result = (String)ENCODINGS.get(e.toUpperCase());
        return (result == null) ? de : result;
    }

    /**
     * The table used to convert the encoding names.
     */
    protected static Map ENCODINGS = new HashMap();
    static {
        ENCODINGS.put("UTF-8",           "UTF8");
        ENCODINGS.put("UTF-16",          "Unicode");
        ENCODINGS.put("US-ASCII",        "ASCII");

        ENCODINGS.put("ISO-8859-1",      "8859_1");
        ENCODINGS.put("ISO-8859-2",      "8859_2");
        ENCODINGS.put("ISO-8859-3",      "8859_3");
        ENCODINGS.put("ISO-8859-4",      "8859_4");
        ENCODINGS.put("ISO-8859-5",      "8859_5");
        ENCODINGS.put("ISO-8859-6",      "8859_6");
        ENCODINGS.put("ISO-8859-7",      "8859_7");
        ENCODINGS.put("ISO-8859-8",      "8859_8");
        ENCODINGS.put("ISO-8859-9",      "8859_9");
        ENCODINGS.put("ISO-2022-JP",     "JIS");

        ENCODINGS.put("WINDOWS-31J",     "MS932");
        ENCODINGS.put("EUC-JP",          "EUCJIS");
        ENCODINGS.put("GB2312",          "GB2312");
        ENCODINGS.put("BIG5",            "Big5");
        ENCODINGS.put("EUC-KR",          "KSC5601");
        ENCODINGS.put("ISO-2022-KR",     "ISO2022KR");
        ENCODINGS.put("KOI8-R",          "KOI8_R");

        ENCODINGS.put("EBCDIC-CP-US",    "CP037");
        ENCODINGS.put("EBCDIC-CP-CA",    "CP037");
        ENCODINGS.put("EBCDIC-CP-NL",    "CP037");
	ENCODINGS.put("EBCDIC-CP-WT",    "CP037");
        ENCODINGS.put("EBCDIC-CP-DK",    "CP277");
        ENCODINGS.put("EBCDIC-CP-NO",    "CP277");
        ENCODINGS.put("EBCDIC-CP-FI",    "CP278");
        ENCODINGS.put("EBCDIC-CP-SE",    "CP278");
        ENCODINGS.put("EBCDIC-CP-IT",    "CP280");
        ENCODINGS.put("EBCDIC-CP-ES",    "CP284");
        ENCODINGS.put("EBCDIC-CP-GB",    "CP285");
        ENCODINGS.put("EBCDIC-CP-FR",    "CP297");
        ENCODINGS.put("EBCDIC-CP-AR1",   "CP420");
        ENCODINGS.put("EBCDIC-CP-HE",    "CP424");
        ENCODINGS.put("EBCDIC-CP-BE",    "CP500");
        ENCODINGS.put("EBCDIC-CP-CH",    "CP500");
        ENCODINGS.put("EBCDIC-CP-ROECE", "CP870");
        ENCODINGS.put("EBCDIC-CP-YU",    "CP870");
        ENCODINGS.put("EBCDIC-CP-IS",    "CP871");
        ENCODINGS.put("EBCDIC-CP-AR2",   "CP918");

        ENCODINGS.put("CP1252",          "CP1252");
    }
}
