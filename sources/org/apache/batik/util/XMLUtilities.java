/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

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

}
