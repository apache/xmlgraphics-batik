/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.xml.scanner;

/**
 * This interface defines the constants that represent XML lexical units.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface LexicalUnits {
    
    /**
     * Represents the EOF lexical unit.
     */
    int EOF = 0;

    /**
     * Represents the S (space) lexical unit.
     */
    int S = 1;

    /**
     * Represents a start tag lexical unit, ie. '<Name'.
     */
    int START_TAG = 2;

    /**
     * Represents a PI start lexical unit, ie. '<?Name'.
     */
    int PI_START = 3;

    /**
     * Represents an XML declaration start lexical unit, ie. '<?xml'.
     */
    int XML_DECL_START = 4;

    /**
     * Represents a PI data lexical unit.
     */
    int PI_DATA = 5;

    /**
     * Represents a PI end lexical unit, ie. '?>'.
     */
    int PI_END = 6;

    /**
     * Represents a comment lexical unit.
     */
    int COMMENT = 7;

    /**
     * Represents a doctype start lexical unit, ie. <!DOCTYPE.
     */
    int DOCTYPE_START = 8;

    /**
     * Represents an empty element end lexical unit, ie. '/>'.
     */
    int EMPTY_ELEMENT_END = 9;

    /**
     * Represents a end character lexical unit, ie. '>'.
     */
    int END_CHAR = 10;

    /**
     * Represents a name lexical unit.
     */
    int NAME = 11;

    /**
     * Represents '=' lexical unit.
     */
    int EQ = 12;

    /**
     * Represents a string without entities lexical unit.
     */
    int STRING_FRAGMENT = 13;

    /**
     * Represents an entity reference lexical unit.
     */
    int ENTITY_REFERENCE = 14;

    /**
     * Represents a character reference lexical unit.
     */
    int CHARACTER_REFERENCE = 15;

    /**
     * Represents a character data lexical unit, ie. the content of an element.
     */
    int CHARACTER_DATA = 16;

    /**
     * Represents an end tag lexical unit, ie. '</Name'.
     */
    int END_TAG = 17;

    /**
     * Represents a CDATA section start lexical unit, ie. '<![CDATA['.
     */
    int CDATA_START = 18;

    /**
     * Represents a section end lexical unit, ie. ']]>'.
     */
    int SECTION_END = 19;

    /**
     * Represents a 'version' lexical unit.
     */
    int VERSION_IDENTIFIER = 20;

    /**
     * Represents a 'encoding' lexical unit.
     */
    int ENCODING_IDENTIFIER = 21;

    /**
     * Represents a 'standalone' lexical unit.
     */
    int STANDALONE_IDENTIFIER = 22;

    /**
     * Represents a string lexical unit.
     */
    int STRING = 23;

    /**
     * Represents a 'SYSTEM' lexical unit.
     */
    int SYSTEM_IDENTIFIER = 24;

    /**
     * Represents a 'PUBLIC' lexical unit.
     */
    int PUBLIC_IDENTIFIER = 25;

    /**
     * Represents a '[' lexical unit.
     */
    int LSQUARE_BRACKET = 26;

    /**
     * Represents a ']' lexical unit.
     */
    int RSQUARE_BRACKET = 27;

    /**
     * Represents a parameter entity reference lexical unit, ie. '%Name;'.
     */
    int PARAMETER_ENTITY_REFERENCE = 28;

    /**
     * Represents a element declaration start lexical unit, ie. '<!ELEMENT'.
     */
    int ELEMENT_DECLARATION_START = 29;

    /**
     * Represents an ATTLIST declaration start lexical unit, ie. '<!ATTLIST'.
     */
    int ATTLIST_START = 30;

    /**
     * Represents an entity start lexical unit, ie. '<!ENTITY'.
     */
    int ENTITY_START = 31;

    /**
     * Represents a notation start lexical unit, ie. '<!NOTATION'.
     */
    int NOTATION_START = 32;

    /**
     * Represents a '%' lexical unit.
     */
    int PERCENT = 33;

    /**
     * Represents a 'NDATA' lexical unit.
     */
    int NDATA_IDENTIFIER = 34;

    /**
     * Represents a 'EMPTY' lexical unit.
     */
    int EMPTY_IDENTIFIER = 35;

    /**
     * Represents a 'ANY' lexical unit.
     */
    int ANY_IDENTIFIER = 36;

    /**
     * Represents a '?' lexical unit.
     */
    int QUESTION = 37;

    /**
     * Represents a '+' lexical unit.
     */
    int PLUS = 38;

    /**
     * Represents a '*' lexical unit.
     */
    int STAR = 39;

    /**
     * Represents a '(' lexical unit.
     */
    int LEFT_BRACE = 40;

    /**
     * Represents a ')' lexical unit.
     */
    int RIGHT_BRACE = 41;

    /**
     * Represents a '|' lexical unit.
     */
    int PIPE = 42;

    /**
     * Represents a ',' lexical unit.
     */
    int COMMA = 43;

    /**
     * Represents a '#PCDATA' lexical unit.
     */
    int PCDATA_IDENTIFIER = 44;

    /**
     * Represents a 'CDATA' lexical unit.
     */
    int CDATA_IDENTIFIER = 45;

    /**
     * Represents a 'ID' lexical unit.
     */
    int ID_IDENTIFIER = 46;

    /**
     * Represents a 'IDREF' lexical unit.
     */
    int IDREF_IDENTIFIER = 47;

    /**
     * Represents a 'IDREFS' lexical unit.
     */
    int IDREFS_IDENTIFIER = 48;

    /**
     * Represents a 'NMTOKEN' lexical unit.
     */
    int NMTOKEN_IDENTIFIER = 49;

    /**
     * Represents a 'NMTOKENS' lexical unit.
     */
    int NMTOKENS_IDENTIFIER = 50;

    /**
     * Represents a 'ENTITY' lexical unit.
     */
    int ENTITY_IDENTIFIER = 51;

    /**
     * Represents a 'ENTITIES' lexical unit.
     */
    int ENTITIES_IDENTIFIER = 52;

    /**
     * Represents a '#REQUIRED' lexical unit.
     */
    int REQUIRED_IDENTIFIER = 53;

    /**
     * Represents a '#IMPLIED' lexical unit.
     */
    int IMPLIED_IDENTIFIER = 54;

    /**
     * Represents a '#FIXED' lexical unit.
     */
    int FIXED_IDENTIFIER = 55;

    /**
     * Represents a Nmtoken lexical unit.
     */
    int NMTOKEN = 56;

    /**
     * Represents a 'NOTATION' lexical unit.
     */
    int NOTATION_IDENTIFIER = 57;

    /**
     * The lexical units values.
     */
    char[][] VALUES = {
	"".toCharArray(),
	null,
	null,
	null,
	"<?xml".toCharArray(),
	null,
	"?>".toCharArray(),
	null,
	"<!DOCTYPE".toCharArray(),
	"/>".toCharArray(),
	">".toCharArray(),
	null,
	"=".toCharArray(),
	null,
	null,
	null,
	null,
	null,
	"<![CDATA[".toCharArray(),
	"]]>".toCharArray(),
	"version".toCharArray(),
	"encoding".toCharArray(),
	"standalone".toCharArray(),
	null,
	"SYSTEM".toCharArray(),
	"PUBLIC".toCharArray(),
	"[".toCharArray(),
	"]".toCharArray(),
	null,
	"<!ELEMENT".toCharArray(),
	"<!ATTLIST".toCharArray(),
	"<!ENTITY".toCharArray(),
	"<!NOTATION".toCharArray(),
	"%".toCharArray(),
	"NDATA".toCharArray(),
	"EMPTY".toCharArray(),
	"ANY".toCharArray(),
	"?".toCharArray(),
	"+".toCharArray(),
	"*".toCharArray(),
	"(".toCharArray(),
	")".toCharArray(),
	"|".toCharArray(),
	",".toCharArray(),
	"#PCDATA".toCharArray(),
	"CDATA".toCharArray(),
	"ID".toCharArray(),
	"IDREF".toCharArray(),
	"IDREFS".toCharArray(),
	"NMTOKEN".toCharArray(),
	"NMTOKENS".toCharArray(),
	"ENTITY".toCharArray(),
	"ENTITIES".toCharArray(),
	"#REQUIRED".toCharArray(),
	"#IMPLIED".toCharArray(),
	"#FIXED".toCharArray(),
        null,
	"NOTATION".toCharArray(),
    };
}
