/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.xml.scanner;

import java.io.IOException;
import java.io.Reader;

import org.apache.batik.util.XMLUtilities;

/**
 * This class represents a low-level lexical scanner for XML documents.
 * It scans the input and returns raw tokens, without entity or
 * character references management.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DocumentScanner extends AbstractScanner {

    /**
     * The DTD declarations context.
     */
    public final static int DTD_DECLARATIONS_CONTEXT = 1;

    /**
     * The double quoted attribute context.
     */
    public final static int DQUOTED_ATTRIBUTE_CONTEXT = 2;

    /**
     * The single quoted attribute context.
     */
    public final static int SQUOTED_ATTRIBUTE_CONTEXT = 3;

    /**
     * The double quoted entity value context.
     */
    public final static int DQUOTED_ENTITY_VALUE_CONTEXT = 4;

    /**
     * The single quoted entity value context.
     */
    public final static int SQUOTED_ENTITY_VALUE_CONTEXT = 5;

    /**
     * The element declaration context.
     */
    public final static int ELEMENT_DECLARATION_CONTEXT = 6;

    /**
     * The entity context.
     */
    public final static int ENTITY_CONTEXT = 7;

    /**
     * The notation context.
     */
    public final static int NOTATION_CONTEXT = 8;

    /**
     * The ATTLIST context.
     */
    public final static int ATTLIST_CONTEXT = 9;

    /**
     * The document start context.
     */
    public final static int DOCUMENT_START_CONTEXT = 10;

    /**
     * The top level context.
     */
    public final static int TOP_LEVEL_CONTEXT = 11;

    /**
     * The processing instruction context.
     */
    public final static int PI_CONTEXT = 12;

    /**
     * The start tag context.
     */
    public final static int START_TAG_CONTEXT = 13;

    /**
     * The doctype context.
     */
    public final static int DOCTYPE_CONTEXT = 14;

    /**
     * The XML declaration context.
     */
    public final static int XML_DECL_CONTEXT = 15;

    /**
     * The content context.
     */
    public final static int CONTENT_CONTEXT = 16;

    /**
     * The CDATA section context.
     */
    public final static int CDATA_SECTION_CONTEXT = 17;

    /**
     * The end tag context.
     */
    public final static int END_TAG_CONTEXT = 18;

    /**
     * The notation type context.
     */
    public final static int NOTATION_TYPE_CONTEXT = 19;

    /**
     * The enumeration context.
     */
    public final static int ENUMERATION_CONTEXT = 20;

    /**
     * The current scanning context.
     */
    protected int context;

    /**
     * The depth in the xml tree.
     */
    protected int depth;

    /**
     * A PI end has been previously read.
     */
    protected boolean piEndRead;

    /**
     * A CDATA section end is the next token
     */
    protected boolean cdataEndRead;

    /**
     * The scanner is in the internal DTD.
     */
    protected boolean inDTD;

    /**
     * The last attribute delimiter encountered.
     */
    protected char stringDelimiter;

    /**
     * Creates a new DocumentScanner object.
     * @param r The reader to scan.
     */
    public DocumentScanner(Reader r) throws LexicalException {
	super(r);
	context = DOCUMENT_START_CONTEXT;
    }

    /**
     * Sets the current depth in the XML tree.
     */
    public void setDepth(int i) {
	depth = i;
    }

    /**
     * Returns the current depth in the XML tree.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets the current context.
     */
    public void setContext(int c) {
	context = c;
    }

    /**
     * Returns the current context.
     */
    public int getContext() {
        return context;
    }

    /**
     * Returns the last attribute delimiter encountered.
     */
    public char getStringDelimiter() {
	return stringDelimiter;
    }

    /**
     * Returns the current lexical unit value.
     * <p>
     * LexicalUnits.START_TAG token value is returned without the leading '<'.
     * LexicalUnits.COMMENT token value is returned without the leading '<!--' and
     * the final '-->'.
     * LexicalUnits.STRING value is returned without quotes.
     * LexicalUnits.STRING_FRAGMENT value is returned without quotes.
     */
    public char[] currentValue() {
	if (value == null) {
	    value = LexicalUnits.VALUES[type];
	    if (value == null) {
		int size = inputBuffer.contentSize();
		if (buffer.length < size) {
		    buffer = new char[size];
		}
		inputBuffer.readContent(buffer);
		int c = inputBuffer.current();
		int ds = (c == -1) ? 0 : 1;
		switch (type) {
		case LexicalUnits.STRING:
		case LexicalUnits.ENTITY_REFERENCE:
		case LexicalUnits.CHARACTER_REFERENCE:
		case LexicalUnits.PARAMETER_ENTITY_REFERENCE:
		    ds += 1;
		    break;
		case LexicalUnits.PI_DATA:
		    ds += 2;
		    break;
		case LexicalUnits.COMMENT:
		    ds += 3;
		    break;
		case LexicalUnits.STRING_FRAGMENT:
		    if (lastFragment) {
			ds += 1;
		    }
                    break;
                case LexicalUnits.CHARACTER_DATA:
                    if (cdataEndRead) {
                        ds += 3;
                    }
		}
		value = new char[size - ds];
		for (int i = 0; i < value.length; i++) {
		    value[i] = buffer[i];
		}
	    }
	}
	return value;
    }

    /**
     * Advances to the next lexical unit.
     * @return The type of the lexical unit like defined in LexicalUnits.
     */
    public int next() throws LexicalException {
	return next(context);
    }

    /**
     * Advances to the next lexical unit.
     * @param context The context to use for scanning.
     * @return The type of the lexical unit like defined in LexicalUnits.
     */
    public int next(int context) throws LexicalException {
	lastFragment = false;
	try {
	    inputBuffer.unsetMark();
	    inputBuffer.setMark();
	    value = null;

	    switch (context) {
	    case DOCUMENT_START_CONTEXT:
		return nextInDocumentStart();
	    case TOP_LEVEL_CONTEXT:
		return nextInTopLevel();
	    case PI_CONTEXT:
		return nextInPI();
	    case START_TAG_CONTEXT:
		return nextInStartTag();
	    case DQUOTED_ATTRIBUTE_CONTEXT:
		return nextInAttributeValue('"');
	    case SQUOTED_ATTRIBUTE_CONTEXT:
		return nextInAttributeValue('\'');
	    case CONTENT_CONTEXT:
		return nextInContent();
	    case END_TAG_CONTEXT:
		return nextInEndTag();
	    case CDATA_SECTION_CONTEXT:
		return nextInCDATASection();
	    case XML_DECL_CONTEXT:
		return nextInXMLDecl();
	    case DOCTYPE_CONTEXT:
		return nextInDoctype();
	    case DTD_DECLARATIONS_CONTEXT:
		return nextInDTDDeclarations();
	    case ELEMENT_DECLARATION_CONTEXT:
		return nextInElementDeclaration();
	    case ATTLIST_CONTEXT:
		return nextInAttList();
	    case NOTATION_CONTEXT:
		return nextInNotation();
	    case ENTITY_CONTEXT:
		return nextInEntity();
	    case DQUOTED_ENTITY_VALUE_CONTEXT:
		return nextInEntityValue('"');
	    case SQUOTED_ENTITY_VALUE_CONTEXT:
		return nextInEntityValue('\'');
            case NOTATION_TYPE_CONTEXT:
                return nextInNotationType();
            case ENUMERATION_CONTEXT:
                return nextInEnumeration();
	    default:
		throw new RuntimeException("Internal error: Invalid Context");
	    }
	} catch (IOException e) {
	    throw createException(e.getLocalizedMessage());
	}
    }

    /**
     * Returns the next lexical unit in the context of a element declaration.
     */
    protected int nextInElementDeclaration() throws IOException, LexicalException {
	int c = inputBuffer.current();

	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case '>':
	    inputBuffer.next();
	    context = DTD_DECLARATIONS_CONTEXT;
	    return type = LexicalUnits.END_CHAR;
	case '%':
            inputBuffer.next();
	    readName(LexicalUnits.PARAMETER_ENTITY_REFERENCE);
	    if (inputBuffer.current() != ';') {
		throw createException("parameter.entity");
	    }
	    inputBuffer.next();
	    return type;
	case 'E':
	    return readIdentifier("MPTY",
				  LexicalUnits.EMPTY_IDENTIFIER,
				  LexicalUnits.NAME);
	case 'A':
	    return readIdentifier("NY",
				  LexicalUnits.ANY_IDENTIFIER,
				  LexicalUnits.NAME);
	case '?':
	    inputBuffer.next();
	    return type = LexicalUnits.QUESTION;
	case '+':
	    inputBuffer.next();
	    return type = LexicalUnits.PLUS;
	case '*':
	    inputBuffer.next();
	    return type = LexicalUnits.STAR;
	case '(':
	    inputBuffer.next();
	    return type = LexicalUnits.LEFT_BRACE;
	case ')':
	    inputBuffer.next();
	    return type = LexicalUnits.RIGHT_BRACE;
	case '|':
	    inputBuffer.next();
	    return type = LexicalUnits.PIPE;
	case ',':
	    inputBuffer.next();
	    return type = LexicalUnits.COMMA;
	case '#':
	    return readIdentifier("PCDATA", LexicalUnits.PCDATA_IDENTIFIER, -1);
	default:
	    return readName(LexicalUnits.NAME);
	}
    }

    /**
     * Returns the next lexical unit in the context of an attribute list.
     */
    protected int nextInAttList() throws IOException, LexicalException {
	int c = inputBuffer.current();

	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case '>':
	    inputBuffer.next();
	    context = DTD_DECLARATIONS_CONTEXT;
	    return type = LexicalUnits.END_CHAR;
	    
	case '%':
	    readName(LexicalUnits.PARAMETER_ENTITY_REFERENCE);
	    if (c != ';') {
		throw createException("parameter.entity");
	    }
	    inputBuffer.next();
	    return type;
	case 'C':
	    return readIdentifier("DATA",
				  LexicalUnits.CDATA_IDENTIFIER,
				  LexicalUnits.NAME);
	case 'I':
	    c = inputBuffer.next();
	    if (c != 'D') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
	    }
	    c = inputBuffer.next();
	    if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		return LexicalUnits.ID_IDENTIFIER;
	    }
	    if (c != 'R') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
	    }
	    c = inputBuffer.next();
	    if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		return LexicalUnits.NAME;
	    }
	    if (c != 'E') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
	    }
	    c = inputBuffer.next();
	    if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		return LexicalUnits.NAME;
	    }
	    if (c != 'F') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
	    }
	    c = inputBuffer.next();
	    if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		return LexicalUnits.IDREF_IDENTIFIER;
	    }
	    if (c != 'S') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
	    }
	    c = inputBuffer.next();
	    if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		return LexicalUnits.IDREFS_IDENTIFIER;
	    }
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
	    return type = LexicalUnits.NAME; 
	case 'N':
	    c = inputBuffer.next();
            switch (c) {
            default:
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
            case 'O':
                context = NOTATION_TYPE_CONTEXT;
                return readIdentifier("TATION",
                                      LexicalUnits.NOTATION_IDENTIFIER,
                                      LexicalUnits.NAME);
            case 'M':
                c = inputBuffer.next();
                if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
                    return LexicalUnits.NAME;
                }
                if (c != 'T') {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
                    return type = LexicalUnits.NAME; 
                }
                c = inputBuffer.next();
                if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
                    return LexicalUnits.NAME;
                }
                if (c != 'O') {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
                    return type = LexicalUnits.NAME; 
                }
                c = inputBuffer.next();
                if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
                    return LexicalUnits.NAME;
                }
                if (c != 'K') {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
                    return type = LexicalUnits.NAME; 
                }
                c = inputBuffer.next();
                if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
                    return LexicalUnits.NAME;
                }
                if (c != 'E') {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
                    return type = LexicalUnits.NAME; 
                }
                c = inputBuffer.next();
                if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
                    return LexicalUnits.NAME;
                }
                if (c != 'N') {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
                    return type = LexicalUnits.NAME; 
                }
                c = inputBuffer.next();
                if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
                    return LexicalUnits.NMTOKEN_IDENTIFIER;
                }
                if (c != 'S') {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
                    return type = LexicalUnits.NAME; 
                }
                c = inputBuffer.next();
                if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
                    return LexicalUnits.NMTOKENS_IDENTIFIER;
                }
                do {
                    c = inputBuffer.next();
                } while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
                return type = LexicalUnits.NAME;
            }
	case 'E':
	    c = inputBuffer.next();
	    if (c != 'N') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
	    }
	    c = inputBuffer.next();
	    if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		return LexicalUnits.NAME;
	    }
	    if (c != 'T') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
	    }
	    c = inputBuffer.next();
	    if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		return LexicalUnits.NAME;
	    }
	    if (c != 'I') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
	    }
	    c = inputBuffer.next();
	    if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		return LexicalUnits.NAME;
	    }
	    if (c != 'T') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
	    }
	    c = inputBuffer.next();
	    if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		return LexicalUnits.NAME;
	    }
	    switch (c) {
	    case 'Y':
		c = inputBuffer.next();
		if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		    return LexicalUnits.ENTITY_IDENTIFIER;
		}
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
	    case 'I':
		c = inputBuffer.next();
		if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		    return LexicalUnits.NAME;
		}
		if (c != 'E') {
		    do {
			c = inputBuffer.next();
		    } while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		    return type = LexicalUnits.NAME; 
		}
		c = inputBuffer.next();
		if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		    return LexicalUnits.NAME;
		}
		if (c != 'S') {
		    do {
			c = inputBuffer.next();
		    } while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		    return type = LexicalUnits.NAME; 
		}
		return LexicalUnits.ENTITIES_IDENTIFIER;
	    default:
		if (c == -1 || !XMLUtilities.isXMLNameCharacter((char)c)) {
		    return LexicalUnits.NAME;
		}
		do {
		    c = inputBuffer.next();
		} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		return type = LexicalUnits.NAME; 
	    }
	case '"':
            stringDelimiter = '"';
	    c = inputBuffer.next();
	    if (c == -1) {
		throw createException("eof");
	    }
	    inputBuffer.unsetMark();
	    inputBuffer.setMark();

	    if (c != '"' && c != '&') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && c != '"' && c != '&');
	    }
	    switch (c) {
	    case '&':
		context = DQUOTED_ATTRIBUTE_CONTEXT;
		break;
	    case '"':
		lastFragment = true;
		inputBuffer.next();
		break;
	    default:
		throw createException("character");
	    }
	    return type = LexicalUnits.STRING_FRAGMENT;
	case '\'':
            stringDelimiter = '\'';
	    c = inputBuffer.next();
	    if (c == -1) {
		throw createException("eof");
	    }
	    inputBuffer.unsetMark();
	    inputBuffer.setMark();

	    if (c != '\'' && c != '&') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && c != '\'' && c != '&');
	    }
	    switch (c) {
	    case '&':
		context = SQUOTED_ATTRIBUTE_CONTEXT;
		break;
	    case '\'':
		lastFragment = true;
		inputBuffer.next();
		break;
	    default:
		throw createException("character");
	    }
	    return type = LexicalUnits.STRING_FRAGMENT;
	case '#':
	    c = inputBuffer.next();
	    switch (c) {
	    case 'R':
		return readIdentifier("EQUIRED", LexicalUnits.REQUIRED_IDENTIFIER, -1);
	    case 'I':
		return readIdentifier("MPLIED", LexicalUnits.IMPLIED_IDENTIFIER, -1);
	    case 'F':
		return readIdentifier("IXED", LexicalUnits.FIXED_IDENTIFIER, -1);
	    default:
		throw createException("character");
	    }
	case '(':
	    inputBuffer.next();
            context = ENUMERATION_CONTEXT;
	    return type = LexicalUnits.LEFT_BRACE;
	default:
	    return readName(LexicalUnits.NAME);
	}
    }

    /**
     * Returns the next lexical unit in the context of a notation type.
     */
    protected int nextInNotationType() throws IOException, LexicalException {
	int c = inputBuffer.current();

        switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case '|':
	    inputBuffer.next();
	    return type = LexicalUnits.PIPE;
	case '(':
	    inputBuffer.next();
	    return type = LexicalUnits.LEFT_BRACE;
	case ')':
	    inputBuffer.next();
            context = ATTLIST_CONTEXT;
	    return type = LexicalUnits.RIGHT_BRACE;
        default:
            return readName(LexicalUnits.NAME);
        }
    }

    /**
     * Returns the next lexical unit in the context of an enumeration.
     */
    protected int nextInEnumeration() throws IOException, LexicalException {
	int c = inputBuffer.current();

        switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case '|':
	    inputBuffer.next();
	    return type = LexicalUnits.PIPE;
	case ')':
	    inputBuffer.next();
            context = ATTLIST_CONTEXT;
	    return type = LexicalUnits.RIGHT_BRACE;
        default:
            return readNmtoken();
        }
    }

    /**
     * Returns the next lexical unit in the context of a notation.
     */
    protected int nextInNotation() throws IOException, LexicalException {
	int c = inputBuffer.current();

	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case '>':
	    inputBuffer.next();
	    context = DTD_DECLARATIONS_CONTEXT;
	    return type = LexicalUnits.END_CHAR;
	case '%':
	    readName(LexicalUnits.PARAMETER_ENTITY_REFERENCE);
	    if (c != ';') {
		throw createException("parameter.entity");
	    }
	    inputBuffer.next();
	    return type;
	case 'S':
	    return readIdentifier("YSTEM",
				  LexicalUnits.SYSTEM_IDENTIFIER,
				  LexicalUnits.NAME);
	case 'P':
	    return readIdentifier("UBLIC",
				  LexicalUnits.PUBLIC_IDENTIFIER,
				  LexicalUnits.NAME);
	case '"':
            stringDelimiter = '"';
	    return readString();
	case '\'':
            stringDelimiter = '\'';
	    return readString();
	default:
	    return readName(LexicalUnits.NAME);
	}
    }

    /**
     * Returns the next lexical unit in the context of an entity.
     */
    protected int nextInEntity() throws IOException, LexicalException {
	int c = inputBuffer.current();

	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case '>':
	    inputBuffer.next();
	    context = DTD_DECLARATIONS_CONTEXT;
	    return type = LexicalUnits.END_CHAR;
	case '%':
	    inputBuffer.next();
	    return type = LexicalUnits.PERCENT;
	case 'S':
	    return readIdentifier("YSTEM",
				  LexicalUnits.SYSTEM_IDENTIFIER,
				  LexicalUnits.NAME);
	case 'P':
	    return readIdentifier("UBLIC",
				  LexicalUnits.PUBLIC_IDENTIFIER,
				  LexicalUnits.NAME);
	case 'N':
	    return readIdentifier("DATA",
				  LexicalUnits.NDATA_IDENTIFIER,
				  LexicalUnits.NAME);
	case '"':
            stringDelimiter = '"';
	    c = inputBuffer.next();
	    if (c == -1) {
		throw createException("eof");
	    }
	    inputBuffer.unsetMark();
	    inputBuffer.setMark();

	    if (c != '"' && c != '&' && c != '%') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && c != '"' && c != '&' && c != '%');
	    }
	    switch (c) {
	    default:
		throw createException("character");
	    case '&':
	    case '%':
		context = DQUOTED_ENTITY_VALUE_CONTEXT;
		break;
	    case '"':
		inputBuffer.next();
		lastFragment = true;
	    }
	    return type = LexicalUnits.STRING_FRAGMENT;
	case '\'':
            stringDelimiter = '\'';
	    c = inputBuffer.next();
	    if (c == -1) {
		throw createException("eof");
	    }
	    inputBuffer.unsetMark();
	    inputBuffer.setMark();

	    if (c != '\'' && c != '&' && c != '%') {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && c != '\'' && c != '&' && c != '%');
	    }
	    switch (c) {
	    default:
		throw createException("character");
	    case '&':
	    case '%':
		context = SQUOTED_ENTITY_VALUE_CONTEXT;
		break;
	    case '\'':
		lastFragment = true;
		inputBuffer.next();
	    }
	    return type = LexicalUnits.STRING_FRAGMENT;
	default:
	    return readName(LexicalUnits.NAME);
	}
    }

    /**
     * Returns the next lexical unit in the context of an entity value.
     * @param sd The current string delimiter.
     */
    protected int nextInEntityValue(char sd) throws IOException, LexicalException {
	int c = inputBuffer.current();

	switch (c) {
	case '&':
	    return readReference();
	case '%':
	    inputBuffer.next();
	    inputBuffer.unsetMark();
	    inputBuffer.setMark();
	    
	    readName(LexicalUnits.PARAMETER_ENTITY_REFERENCE);
	    if (inputBuffer.current() != ';') {
		throw createException("parameter.entity");
	    }
	    inputBuffer.next();
	    return type;
	default:
	    while (c != -1 && c != sd && c != '&' && c != '%') {
		c = inputBuffer.next();
	    }
	    switch (c) {
	    case -1:
		throw createException("eof");
	    case '\'':
	    case '"':
		lastFragment = true;
		inputBuffer.next();
		context = ENTITY_CONTEXT;
	    }
	    return type = LexicalUnits.STRING_FRAGMENT;
	}
    }
    
    /**
     * Reads the first token in the stream.
     */
    protected int nextInDocumentStart() throws IOException, LexicalException {
	int c = inputBuffer.current();

	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    context = TOP_LEVEL_CONTEXT;
	    return type = LexicalUnits.S;
	case '<':
	    c = inputBuffer.next();
	    switch (c) {
	    case '?':
		c = inputBuffer.next();
		if (c == -1 || !XMLUtilities.isXMLNameFirstCharacter((char)c)) {
		    throw createException("pi.target");
		}
		inputBuffer.unsetMark();
		inputBuffer.setMark();
		context = PI_CONTEXT;
		int c2 = inputBuffer.next();
		if (c2 == -1 || !XMLUtilities.isXMLNameCharacter((char)c2)) {
		    return type = LexicalUnits.PI_START;
		}
		int c3 = inputBuffer.next();
		if (c3 == -1 || !XMLUtilities.isXMLNameCharacter((char)c3)) {
		    return type = LexicalUnits.PI_START;
		}
		int c4 = inputBuffer.next();
		if (c4 != -1 && XMLUtilities.isXMLNameCharacter((char)c4)) {
		    do {
			c = inputBuffer.next();
		    } while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
		    return type = LexicalUnits.PI_START;
		}
		if (c == 'x' && c2 == 'm' && c3 == 'l') {
		    context = XML_DECL_CONTEXT;
		    return type = LexicalUnits.XML_DECL_START;
		}
		if ((c  == 'x' || c  == 'X') &&
		    (c2 == 'm' || c2 == 'M') &&
		    (c3 == 'l' || c3 == 'L')) {
		    throw createException("xml.reserved");
		}
		return type = LexicalUnits.PI_START;
	    case '!':
		c = inputBuffer.next();
		switch (c) {
		case '-':
		    return readComment();
		case 'D':
		    context = DOCTYPE_CONTEXT;
		    return readIdentifier("OCTYPE", LexicalUnits.DOCTYPE_START, -1);
		default:
		    throw createException("comment.or.doctype");
		}
	    default:
		inputBuffer.unsetMark();
		inputBuffer.setMark();
		context = START_TAG_CONTEXT;
		depth++;
		return readName(LexicalUnits.START_TAG);
	    }
	case -1:
	    throw createException("eof");
	default:
	    throw createException("character");
	}
    }

    /**
     * Advances to the next lexical unit in the top level context.
     * @return The type of the lexical unit like defined in LexicalUnits.
     */
    protected int nextInTopLevel() throws IOException, LexicalException {
	int c = inputBuffer.current();

	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case '<':
	    c = inputBuffer.next();
	    switch (c) {
	    case '?':
		context = PI_CONTEXT;
		return readPIStart();
	    case '!':
		c = inputBuffer.next();
		switch (c) {
		case '-':
		    return readComment();
		case 'D':
		    context = DOCTYPE_CONTEXT;
		    return readIdentifier("OCTYPE", LexicalUnits.DOCTYPE_START, -1);
		default:
		    throw createException("character");
		}
	    default:
		inputBuffer.unsetMark();
		inputBuffer.setMark();
		context = START_TAG_CONTEXT;
		depth++;
		return readName(LexicalUnits.START_TAG);
	    }
	case -1:
	    return type = LexicalUnits.EOF;
	default:
	    throw createException("character");
	}
    }

    /**
     * Returns the next lexical unit in the context of a processing instruction.
     */
    protected int nextInPI() throws IOException, LexicalException {
	if (piEndRead) {
	    piEndRead = false;
	    context = (depth == 0) ? TOP_LEVEL_CONTEXT : CONTENT_CONTEXT;
	    return type = LexicalUnits.PI_END;
	}
	int c = inputBuffer.current();

	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case '?':
	    c = inputBuffer.next();
	    if (c != '>') {
		throw createException("pi.end");
	    }
	    c = inputBuffer.next();
	    if (inDTD) {
		context = DTD_DECLARATIONS_CONTEXT;
	    } else if (depth == 0) {
		context = TOP_LEVEL_CONTEXT;
	    } else {
		context = CONTENT_CONTEXT;
	    }
	    return type = LexicalUnits.PI_END;
	default:
	    do {
		do {
		    c = inputBuffer.next();
		} while (c != -1 && c != '?');
		c = inputBuffer.next();
	    } while (c != -1 && c != '>');
	    c = inputBuffer.next();
	    piEndRead = true;
	    return type = LexicalUnits.PI_DATA;
	}
    }

    /**
     * Returns the next lexical unit in the context of a start tag.
     */
    protected int nextInStartTag() throws IOException, LexicalException {
	int c = inputBuffer.current();

	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case '/':
	    c = inputBuffer.next();
	    if (c != '>') {
		throw createException("tag.end");
	    }
	    c = inputBuffer.next();
	    context = (--depth == 0) ? TOP_LEVEL_CONTEXT : CONTENT_CONTEXT;
	    return type = LexicalUnits.EMPTY_ELEMENT_END;
	case '>':
	    c = inputBuffer.next();
	    context = CONTENT_CONTEXT;
	    return type = LexicalUnits.END_CHAR;
	case '=':
	    c = inputBuffer.next();
	    return type = LexicalUnits.EQ;
	case '"':
	    stringDelimiter = '"';
	    c = inputBuffer.next();
	    inputBuffer.unsetMark();
	    inputBuffer.setMark();

	    dQuoteLoop: for (;;) {
		switch (c) {
		case '"':
		case '&':
		case '<':
		case -1:
		    break dQuoteLoop;
		}
		c = inputBuffer.next();
	    }

	    switch (c) {
	    case '&':
		context = DQUOTED_ATTRIBUTE_CONTEXT;
		break;
	    case '"':
		lastFragment = true;
		inputBuffer.next();
		break;
	    case '<':
		throw createException("character");
	    case -1:
		throw createException("eof");
	    }
	    return type = LexicalUnits.STRING_FRAGMENT;
	case '\'':
	    stringDelimiter = '\'';
	    c = inputBuffer.next();
	    inputBuffer.unsetMark();
	    inputBuffer.setMark();

	    sQuoteLoop: for (;;) {
		switch (c) {
		case '\'':
		case '&':
		case '<':
		case -1:
		    break sQuoteLoop;
		}
		c = inputBuffer.next();
	    }

	    switch (c) {
	    case '&':
		context = SQUOTED_ATTRIBUTE_CONTEXT;
		break;
	    case '\'':
		lastFragment = true;
		inputBuffer.next();
		break;
	    case '<':
		throw createException("character");
	    case -1:
		throw createException("eof");
	    }
	    return type = LexicalUnits.STRING_FRAGMENT;
	default:
	    return readName(LexicalUnits.NAME);
	}
    }

    /**
     * Returns the next lexical unit in the context of an attribute value.
     * @param sd The current string delimiter.
     */
    protected int nextInAttributeValue(char sd) throws IOException, LexicalException {
	int c = inputBuffer.current();
	if (c == -1) {
	    return LexicalUnits.EOF;
	}

	if (c == '&') {
	    return readReference();
	} else {
	    loop: for (;;) {
		switch (c) {
		case '&':
		case '<':
		case -1:
		    break loop;
		case '"':
		case '\'':
		    if (c == sd) {
			break loop;
		    }
		}
		c = inputBuffer.next();
	    }

	    switch (c) {
	    case -1:
		break;
	    case '<':
		throw createException("character");
	    case '\'':
	    case '"':
		lastFragment = true;
		inputBuffer.next();
		if (inDTD) {
		    context = ATTLIST_CONTEXT;
		} else {
		    context = START_TAG_CONTEXT;
		}
	    }
	    return type = LexicalUnits.STRING_FRAGMENT;
	}
    }

    /**
     * Returns the next lexical unit in the context of an element content.
     */
    protected int nextInContent() throws IOException, LexicalException {
	int c = inputBuffer.current();

	switch (c) {
	case -1:
	    return type = LexicalUnits.EOF;
	case '&':
	    return readReference();
	case '<':
	    c = inputBuffer.next();
	    switch (c) {
	    case '?':
		context = PI_CONTEXT;
		return readPIStart();
	    case '!':
		c = inputBuffer.next();
		switch (c) {
		case '-':
		    return readComment();
		case '[':
		    context = CDATA_SECTION_CONTEXT;
		    return readIdentifier("CDATA[", LexicalUnits.CDATA_START, -1);
		default:
		    throw createException("character");
		}
	    case '/':
		c = inputBuffer.next();
		inputBuffer.unsetMark();
		inputBuffer.setMark();
		context = END_TAG_CONTEXT;
		return readName(LexicalUnits.END_TAG);
	    default:
		depth++;
		inputBuffer.unsetMark();
		inputBuffer.setMark();
		context = START_TAG_CONTEXT;
		return readName(LexicalUnits.START_TAG);
	    }
	default:
	    while (c != -1 && c != '&' && c != '<') {
		c = inputBuffer.next();
	    }
	    return type = LexicalUnits.CHARACTER_DATA;
	}
    }

    /**
     * Returns the next lexical unit in the context of a end tag.
     */
    protected int nextInEndTag() throws IOException, LexicalException {
	int c = inputBuffer.current();
	
	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case '>':
	    if (--depth < 0) {
		throw createException("end.tag");
	    } else if (depth == 0) {
		context = TOP_LEVEL_CONTEXT;
	    } else {
		context = CONTENT_CONTEXT;
	    }
	    inputBuffer.next();
	    return type = LexicalUnits.END_CHAR;
	default:
	    throw createException("character");
	}
    }

    /**
     * Returns the next lexical unit in the context of a CDATA section.
     */
    protected int nextInCDATASection() throws IOException, LexicalException {
	if (cdataEndRead) {
	    cdataEndRead = false;
	    context = CONTENT_CONTEXT;
	    return type = LexicalUnits.SECTION_END;
	}
	int c = inputBuffer.current();
	
	while (c != -1) {
	    while (c != ']') {
		c = inputBuffer.next();
	    }
	    if (c != -1) {
		c = inputBuffer.next();
		if (c == ']') {
		    c = inputBuffer.next();
		    if (c == '>') {
			break;
		    }
		}
	    }
	}
	if (c == -1) {
	    throw createException("eof");
	}
	inputBuffer.next();
	cdataEndRead = true;
	return type = LexicalUnits.CHARACTER_DATA;
    }

    /**
     * Returns the next lexical unit in the context of an XML declaration.
     */
    protected int nextInXMLDecl() throws IOException, LexicalException {
	int c = inputBuffer.current();
	
	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case 'v':
	    return readIdentifier("ersion", LexicalUnits.VERSION_IDENTIFIER, -1);
	case 'e':
	    return readIdentifier("ncoding", LexicalUnits.ENCODING_IDENTIFIER, -1);
	case 's':
	    return readIdentifier("tandalone", LexicalUnits.STANDALONE_IDENTIFIER, -1);
	case '=':
	    inputBuffer.next();
	    return type = LexicalUnits.EQ;
	case '?':
	    c = inputBuffer.next();
	    if (c != '>') {
		throw createException("pi.end");
	    }
	    c = inputBuffer.next();
	    context = TOP_LEVEL_CONTEXT;
	    return type = LexicalUnits.PI_END;
	case '"':
            stringDelimiter = '"';
	    return readString();
	case '\'':
            stringDelimiter = '\'';
	    return readString();
	default:
	    throw createException("character");
	}
    }

    /**
     * Returns the next lexical unit in the context of a doctype.
     */
    protected int nextInDoctype() throws IOException, LexicalException {
	int c = inputBuffer.current();
	
	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case '>':
	    c = inputBuffer.next();
	    context = TOP_LEVEL_CONTEXT;
	    return type = LexicalUnits.END_CHAR;
	case 'S':
	    return readIdentifier("YSTEM",
				  LexicalUnits.SYSTEM_IDENTIFIER,
				  LexicalUnits.NAME);
	case 'P':
	    return readIdentifier("UBLIC",
				  LexicalUnits.PUBLIC_IDENTIFIER,
				  LexicalUnits.NAME);
	case '"':
            stringDelimiter = '"';
	    return readString();
	case '\'':
            stringDelimiter = '\'';
	    return readString();
	case '[':
	    inputBuffer.next();
	    context = DTD_DECLARATIONS_CONTEXT;
	    inDTD = true;
	    return type = LexicalUnits.LSQUARE_BRACKET;
	default:
	    return readName(LexicalUnits.NAME);
	}
    }

    /**
     * Returns the next lexical unit in the context dtd declarations.
     */
    protected int nextInDTDDeclarations() throws IOException, LexicalException {
	int c = inputBuffer.current();
	
	switch (c) {
	case 0x9:
	case 0xA:
	case 0xD:
	case 0x20:
	    do {
		c = inputBuffer.next();
	    } while (c != -1 && XMLUtilities.isXMLSpace((char)c));
	    return type = LexicalUnits.S;
	case ']':
	    inputBuffer.next();
	    context = DOCTYPE_CONTEXT;
	    inDTD = false;
	    return type = LexicalUnits.RSQUARE_BRACKET;
	case '%':
	    return readPEReference();
	case '<':
	    c = inputBuffer.next();
	    switch (c) {
	    case '?':
		context = PI_CONTEXT;
		return readPIStart();
	    case '!':
		c = inputBuffer.next();
		switch (c) {
		case '-':
		    return readComment();
		case 'E':
		    c = inputBuffer.next();
		    switch (c) {
		    case 'L':
			context = ELEMENT_DECLARATION_CONTEXT;
			return readIdentifier("EMENT",
					      LexicalUnits.ELEMENT_DECLARATION_START,
					      -1);
		    case 'N':
			context = ENTITY_CONTEXT;
			return readIdentifier("TITY",
					      LexicalUnits.ENTITY_START,
					      -1);
		    default:
			throw createException("character");
		    }
		case 'A':
		    context = ATTLIST_CONTEXT;
		    return readIdentifier("TTLIST",
					  LexicalUnits.ATTLIST_START,
					  -1);
		case 'N':
		    context = NOTATION_CONTEXT;
		    return readIdentifier("OTATION",
					  LexicalUnits.NOTATION_START,
					  -1);
		default:
		    throw createException("character");
		}
	    default:
		throw createException("character");
	    }
	default:
	    throw createException("character");
	}
    }
}
