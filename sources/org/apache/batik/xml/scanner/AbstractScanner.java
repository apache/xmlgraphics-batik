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

import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;

import org.apache.batik.util.InputBuffer;
import org.apache.batik.util.XMLUtilities;

/**
 * This class contains the basic methods needed by an XML scanners.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AbstractScanner implements Localizable {

    /**
     * The default resource bundle base name.
     */
    public final static String BUNDLE_CLASSNAME =
	"org.apache.batik.xml.scanner.resources.Messages";

    /**
     * The localizable support.
     */
    protected LocalizableSupport localizableSupport =
        new LocalizableSupport(BUNDLE_CLASSNAME);

    /**
     * The input buffer.
     */
    protected InputBuffer inputBuffer;

    /**
     * The buffer used to store the value of the current lexical unit.
     */
    protected char[] buffer = new char[4096];

    /**
     * The value of the current lexical unit.
     */
    protected char[] value;

    /**
     * The type of the current lexical unit.
     */
    protected int type;

    /**
     * Must be set to true when the current lexical unit is the last
     * string fragment of an attribute value or an entity value.
     */
    protected boolean lastFragment;

    /**
     * Creates a new AbstractScanner object.
     * @param r The reader to scan.
     */
    protected AbstractScanner(Reader r) throws LexicalException {
	try {
	    inputBuffer = new InputBuffer(r);
	    inputBuffer.setMark();
	} catch (IOException e) {
	    throw createException(e.getLocalizedMessage());
	}
    }

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#setLocale(Locale)}.
     */
    public  void setLocale(Locale l) {
	localizableSupport.setLocale(l);
    }

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#getLocale()}.
     */
    public Locale getLocale() {
        return localizableSupport.getLocale();
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.Localizable#formatMessage(String,Object[])}.
     */
    public String formatMessage(String key, Object[] args)
        throws MissingResourceException {
        return localizableSupport.formatMessage(key, args);
    }

    /**
     * Returns the input buffer.
     */
    public InputBuffer getInputBuffer() {
	return inputBuffer;
    }

    /**
     * The current lexical unit type like defined in LexicalUnits.
     */
    public int currentType() {
	return type;
    }

    /**
     * Reads the given identifier.
     * @param s The portion of the identifier to read.
     * @param type The lexical unit type of the identifier.
     * @param ntype The lexical unit type to set if the identifier do not
     * match or -1 if an error must be signaled.
     */
    protected int readIdentifier(String s, int type, int ntype)
	throws IOException, LexicalException {
	int len = s.length();
	for (int i = 0; i < len; i++) {
	    int c = inputBuffer.next();
	    if (c != s.charAt(i)) {
		if (ntype == -1) {
		    throw createException("character");
		} else {
		    while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c)) {
			c = inputBuffer.next();
		    }
		    return this.type = ntype;
		}
	    }
	}
	inputBuffer.next();
	return this.type = type;
    }

    /**
     * Reads a name. The current character must be the first character.
     * @param type The lexical unit type to set.
     * @return type.
     */
    protected int readName(int type) throws IOException, LexicalException {
        inputBuffer.resetMark();
	int c = inputBuffer.current();
	if (c == -1) {
	    throw createException("eof");
	}
	if (!XMLUtilities.isXMLNameFirstCharacter((char)c)) {
	    throw createException("name");
	}
	do {
	    c = inputBuffer.next();
	} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
	return this.type = type;
    }

    /**
     * Reads a Nmtoken. The current character must be the first character.
     * @return LexicalUnits.NMTOKEN.
     */
    protected int readNmtoken() throws IOException, LexicalException {
        inputBuffer.resetMark();
	int c = inputBuffer.current();
	if (c == -1) {
	    throw createException("eof");
	}
	while (XMLUtilities.isXMLNameCharacter((char)c)) {
	    c = inputBuffer.next();
	}
	return type = LexicalUnits.NMTOKEN;
    }

    /**
     * Reads a comment. '&lt;!-' must have been read.
     * @return type.
     */
    protected int readComment() throws IOException, LexicalException {
	int c = inputBuffer.next();
	if (c != '-') {
	    throw createException("comment");
	}
	c = inputBuffer.next();
	inputBuffer.unsetMark();
	inputBuffer.setMark();
	while (c != -1) {
	    while (c != -1 && c != '-') {
		c = inputBuffer.next();
	    }
	    c = inputBuffer.next();
	    if (c == '-') {
		break;
	    }
	}
	if (c == -1) {
	    throw createException("eof");
	}
	c = inputBuffer.next();
	if (c != '>') {
	    throw createException("comment");
	}
	c = inputBuffer.next();
	return type = LexicalUnits.COMMENT;
    }

    /**
     * Reads a simple string, like the ones used for version, encoding,
     * public/system identifiers...
     * The current character must be the string delimiter.
     * @return type.
     */
    protected int readString() throws IOException, LexicalException {
	int sd = inputBuffer.current();
	int c = inputBuffer.next();
	inputBuffer.unsetMark();
	inputBuffer.setMark();
	while (c != -1 && c != sd) {
	    c = inputBuffer.next();
	}
	if (c == -1) {
	    throw createException("eof");
	}
	inputBuffer.next();
	return type = LexicalUnits.STRING;
    }

    /**
     * Reads an entity or character reference. The current character
     * must be '&'.
     * @return type.
     */
    protected int readReference() throws IOException, LexicalException {
	int c = inputBuffer.next();
	if (c == '#') {
	    c = inputBuffer.next();
	    inputBuffer.unsetMark();
	    inputBuffer.setMark();
	    int i = 0;
	    switch (c) {
	    case 'x':
		do {
		    i++;
		    c = inputBuffer.next();
		} while ((c >= '0' && c <= '9') ||
			 (c >= 'a' && c <= 'f') ||
			 (c >= 'A' && c <= 'F'));
		break;
	    default:
		do {
		    i++;
		    c = inputBuffer.next();
		} while (c >= '0' && c <= '9');
		break;
	    case -1:
		throw createException("eof");
	    }
	    if (i == 1 || c != ';') {
		throw createException("character.reference");
	    }
	    inputBuffer.next();
	    return type = LexicalUnits.CHARACTER_REFERENCE;
	} else {
	    readName(LexicalUnits.ENTITY_REFERENCE);
	    c = inputBuffer.current();
	    if (c != ';') {
		throw createException("character.reference");
	    }
	    inputBuffer.next();
	    return type;
	}
    }

    /**
     * Reads a parameter entity reference. The current character must be '%'.
     * @return type.
     */
    protected int readPEReference() throws IOException, LexicalException {
	int c = inputBuffer.next();
	inputBuffer.unsetMark();
	inputBuffer.setMark();
	if (c == -1) {
	    throw createException("eof");
	}
	if (!XMLUtilities.isXMLNameFirstCharacter((char)c)) {
	    throw createException("parameter.entity");
	}
	do {
	    c = inputBuffer.next();
	} while (c != -1 && XMLUtilities.isXMLNameCharacter((char)c));
	if (c != ';') {
	    throw createException("parameter.entity");
	}
	inputBuffer.next();
	return type = LexicalUnits.PARAMETER_ENTITY_REFERENCE;
    }

    /**
     * Reads a processing instruction start.
     * @return type.
     */
    protected int readPIStart() throws IOException, LexicalException {
	int c = inputBuffer.next();
	inputBuffer.unsetMark();
	inputBuffer.setMark();
	if (c == -1) {
	    throw createException("eof");
	}
	if (!XMLUtilities.isXMLNameFirstCharacter((char)c)) {
	    throw createException("pi.target");
	}
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
	if ((c  == 'x' || c  == 'X') &&
	    (c2 == 'm' || c2 == 'M') &&
	    (c3 == 'l' || c3 == 'L')) {
	    throw createException("xml.reserved");
	}
	return type = LexicalUnits.PI_START;
    }

    /**
     * Returns a LexicalException initialized with the given message.
     */
    protected LexicalException createException(String message) {
	return new LexicalException(formatMessage(message, null),
                                    inputBuffer.getLine(),
                                    inputBuffer.getColumn());
    }
}
