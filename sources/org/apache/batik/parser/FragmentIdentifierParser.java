/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

import java.io.Reader;

import org.apache.batik.xml.XMLUtilities;

/**
 * This class represents an event-based parser for the SVG
 * fragment identifiers.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FragmentIdentifierParser extends AbstractParser {
    
    /**
     * The buffer used for numbers.
     */
    protected char[] buffer = new char[16];

    /**
     * The buffer size.
     */
    protected int bufferSize;

    /**
     * The FragmentIdentifierHandler.
     */
    protected FragmentIdentifierHandler fragmentIdentifierHandler;

    /**
     * Creates a new FragmentIdentifier parser.
     */
    public FragmentIdentifierParser() {
        fragmentIdentifierHandler =
            DefaultFragmentIdentifierHandler.INSTANCE;
    }

    /**
     * Allows an application to register a fragment identifier handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform list handler.
     */
    public void setFragmentIdentifierHandler(FragmentIdentifierHandler handler) {
        fragmentIdentifierHandler = handler;
    }

    /**
     * Returns the points handler in use.
     */
    public FragmentIdentifierHandler getFragmentIdentifierHandler() {
        return fragmentIdentifierHandler;
    }

    /**
     * Parses the given reader.
     */
    protected void doParse() throws ParseException {
        bufferSize = 0;
                
        read();

        fragmentIdentifierHandler.startFragmentIdentifier();

        ident: {
            String id = null;

            switch (current) {
            case 'x':
                bufferize();
                read();
                if (current != 'p') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != 'o') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != 'i') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != 'n') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != 't') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != 'e') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != 'r') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != '(') {
                    parseIdentifier();
                    break;
                }
                bufferSize = 0;
                read();
                if (current != 'i') {
                    reportError("character.expected",
                                new Object[] { new Character('i'),
	         	                       new Integer(current) });
                    break ident;
                }
                read();
                if (current != 'd') {
                    reportError("character.expected",
                                new Object[] { new Character('d'),
                                               new Integer(current) });
                    break ident;
                }
                read();
                if (current != '(') {
                    reportError("character.expected",
                                new Object[] { new Character('('),
                                               new Integer(current) });
                    break ident;
                }
                read();
                if (current != '"' && current != '\'') {
                    reportError("character.expected",
                                new Object[] { new Character('\''),
                                               new Integer(current) });
                    break ident;
                }
                char q = (char)current;
                read();
                parseIdentifier();

                id = getBufferContent();
                bufferSize = 0;
                fragmentIdentifierHandler.idReference(id);

                if (current != q) {
                    reportError("character.expected",
                                new Object[] { new Character(q),
                                               new Integer(current) });
                    break ident;
                }
                read();
                if (current != ')') {
                    reportError("character.expected",
                                new Object[] { new Character(')'),
                                               new Integer(current) });
                    break ident;
                }
                read();
                if (current != ')') {
                    reportError("character.expected",
                                new Object[] { new Character(')'),
                                               new Integer(current) });
                }
                break ident;

            case 's':
                bufferize();
                read();
                if (current != 'v') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != 'g') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != 'V') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != 'i') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != 'e') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != 'w') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                read();
                if (current != '(') {
                    parseIdentifier();
                    break;
                }
                bufferSize = 0;
                read();
                parseViewAttributes();

                if (current != ')') {
                    reportError("character.expected",
                                new Object[] { new Character(')'),
                                               new Integer(current) });
                }
                break ident;

            default:
                if (current == -1 ||
                    !XMLUtilities.isXMLNameFirstCharacter((char)current)) {
                    break ident;
                }
                bufferize();
                read();
                parseIdentifier();
            }
            id = getBufferContent();
            fragmentIdentifierHandler.idReference(id);
        }

        fragmentIdentifierHandler.endFragmentIdentifier();
    }

    /**
     * Parses the svgView attributes.
     */
    protected void parseViewAttributes() throws ParseException {
        boolean first = true;
        loop: for (;;) {
            switch (current) {
            case -1:
            case ')':
                if (first) {
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    break loop;
                }
            default:
                break loop;
            case ';':
                if (first) {
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    break loop;
                }
                read();
                break;
            case 'v':
                first = false;
                read();
                if (current != 'i') {
                    reportError("character.expected",
                                new Object[] { new Character('i'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'e') {
                    reportError("character.expected",
                                new Object[] { new Character('e'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'w') {
                    reportError("character.expected",
                                new Object[] { new Character('w'),
                                               new Integer(current) });
                    break loop;
                }
                read();

                switch (current) {
                case 'B':
                    read();
                    if (current != 'o') {
                        reportError("character.expected",
                                    new Object[] { new Character('o'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'x') {
                        reportError("character.expected",
                                    new Object[] { new Character('x'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != '(') {
                        reportError("character.expected",
                                    new Object[] { new Character('('),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();

                    float x = parseFloat();
                    if (current != ',') {
                        reportError("character.expected",
                                    new Object[] { new Character(','),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    
                    float y = parseFloat();
                    if (current != ',') {
                        reportError("character.expected",
                                    new Object[] { new Character(','),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    
                    float w = parseFloat();
                    if (current != ',') {
                        reportError("character.expected",
                                    new Object[] { new Character(','),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    
                    float h = parseFloat();
                    if (current != ')') {
                        reportError("character.expected",
                                    new Object[] { new Character(')'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    fragmentIdentifierHandler.viewBox(x, y, w, h);
                    if (current != ')' && current != ';') {
                        reportError("character.expected",
                                    new Object[] { new Character(')'),
                                                   new Integer(current) });
                        break loop;
                    }
                    break;

                case 'T':
                    read();
                    if (current != 'a') {
                        reportError("character.expected",
                                    new Object[] { new Character('a'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'r') {
                        reportError("character.expected",
                                    new Object[] { new Character('r'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'g') {
                        reportError("character.expected",
                                    new Object[] { new Character('g'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'e') {
                        reportError("character.expected",
                                    new Object[] { new Character('e'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 't') {
                        reportError("character.expected",
                                    new Object[] { new Character('t'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != '(') {
                        reportError("character.expected",
                                    new Object[] { new Character('('),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();

                    fragmentIdentifierHandler.startViewTarget();

                    id: for (;;) {
                        bufferSize = 0;
                        if (current == -1 ||
                            !XMLUtilities.isXMLNameFirstCharacter((char)current)) {
                            reportError("character.unexpected",
                                        new Object[] { new Integer(current) });
                            break loop;
                        }
                        bufferize();
                        read();
                        parseIdentifier();
                        String s = getBufferContent();

                        fragmentIdentifierHandler.viewTarget(s);

                        bufferSize = 0;
                        switch (current) {
                        case ')':
                            read();
                            break id;
                        case ',':
                        case ';':
                            read();
                            break;
                        default:
                            reportError("character.unexpected",
                                        new Object[] { new Integer(current) });
                            break loop;
                        }
                    }

                    fragmentIdentifierHandler.endViewTarget();
                    break;

                default:
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    break loop;
                }
                break;
            case 'p':
                first = false;
                read();
                if (current != 'r') {
                    reportError("character.expected",
                                new Object[] { new Character('r'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'e') {
                    reportError("character.expected",
                                new Object[] { new Character('e'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 's') {
                    reportError("character.expected",
                                new Object[] { new Character('s'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'e') {
                    reportError("character.expected",
                                new Object[] { new Character('e'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'r') {
                    reportError("character.expected",
                                new Object[] { new Character('r'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'v') {
                    reportError("character.expected",
                                new Object[] { new Character('v'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'e') {
                    reportError("character.expected",
                                new Object[] { new Character('e'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'A') {
                    reportError("character.expected",
                                new Object[] { new Character('A'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 's') {
                    reportError("character.expected",
                                new Object[] { new Character('s'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'p') {
                    reportError("character.expected",
                                new Object[] { new Character('p'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'e') {
                    reportError("character.expected",
                                new Object[] { new Character('e'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'c') {
                    reportError("character.expected",
                                new Object[] { new Character('c'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 't') {
                    reportError("character.expected",
                                new Object[] { new Character('t'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'R') {
                    reportError("character.expected",
                                new Object[] { new Character('R'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'a') {
                    reportError("character.expected",
                                new Object[] { new Character('a'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 't') {
                    reportError("character.expected",
                                new Object[] { new Character('t'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'i') {
                    reportError("character.expected",
                                new Object[] { new Character('i'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'o') {
                    reportError("character.expected",
                                new Object[] { new Character('o'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != '(') {
                    reportError("character.expected",
                                new Object[] { new Character('('),
                                               new Integer(current) });
                    break loop;
                }
                read();

                parsePreserveAspectRatio();

                if (current != ')') {
                    reportError("character.expected",
                                new Object[] { new Character(')'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                break;

            case 't':
                first = false;
                read();
                if (current != 'r') {
                    reportError("character.expected",
                                new Object[] { new Character('r'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'a') {
                    reportError("character.expected",
                                new Object[] { new Character('a'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'n') {
                    reportError("character.expected",
                                new Object[] { new Character('n'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 's') {
                    reportError("character.expected",
                                new Object[] { new Character('s'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'f') {
                    reportError("character.expected",
                                new Object[] { new Character('f'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'o') {
                    reportError("character.expected",
                                new Object[] { new Character('o'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'r') {
                    reportError("character.expected",
                                new Object[] { new Character('r'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'm') {
                    reportError("character.expected",
                                new Object[] { new Character('m'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != '(') {
                    reportError("character.expected",
                                new Object[] { new Character('('),
                                               new Integer(current) });
                    break loop;
                }

                fragmentIdentifierHandler.startTransformList();

                tloop: for (;;) {
                    read();
                    switch (current) {
                    case ',':
                        break;
                    case 'm':
                        parseMatrix();
                        break;
                    case 'r':
                        parseRotate();
                        break;
                    case 't':
                        parseTranslate();
                        break;
                    case 's':
                        read();
                        switch (current) {
                        case 'c':
                            parseScale();
                            break;
                        case 'k':
                            parseSkew();
                            break;
                        default:
                            reportError("character.unexpected",
                                        new Object[] { new Integer(current) });
                            skipTransform();
                        }
                        break;
                    default:
                        break tloop;
                    }
                }

                fragmentIdentifierHandler.endTransformList();
                break;

            case 'z':
                first = false;
                read();
                if (current != 'o') {
                    reportError("character.expected",
                                new Object[] { new Character('o'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'o') {
                    reportError("character.expected",
                                new Object[] { new Character('o'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'm') {
                    reportError("character.expected",
                                new Object[] { new Character('m'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'A') {
                    reportError("character.expected",
                                new Object[] { new Character('A'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'n') {
                    reportError("character.expected",
                                new Object[] { new Character('n'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'd') {
                    reportError("character.expected",
                                new Object[] { new Character('d'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'P') {
                    reportError("character.expected",
                                new Object[] { new Character('P'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'a') {
                    reportError("character.expected",
                                new Object[] { new Character('a'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != 'n') {
                    reportError("character.expected",
                                new Object[] { new Character('n'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                if (current != '(') {
                    reportError("character.expected",
                                new Object[] { new Character('('),
                                               new Integer(current) });
                    break loop;
                }
                read();

                switch (current) {
                case 'm':
                    read();
                    if (current != 'a') {
                        reportError("character.expected",
                                    new Object[] { new Character('a'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'g') {
                        reportError("character.expected",
                                    new Object[] { new Character('g'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'n') {
                        reportError("character.expected",
                                    new Object[] { new Character('n'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'i') {
                        reportError("character.expected",
                                    new Object[] { new Character('i'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'f') {
                        reportError("character.expected",
                                    new Object[] { new Character('f'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'y') {
                        reportError("character.expected",
                                    new Object[] { new Character('y'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    fragmentIdentifierHandler.zoomAndPan(true);
                    break;

                case 'd':
                    read();
                    if (current != 'i') {
                        reportError("character.expected",
                                    new Object[] { new Character('i'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 's') {
                        reportError("character.expected",
                                    new Object[] { new Character('s'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'a') {
                        reportError("character.expected",
                                    new Object[] { new Character('a'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'b') {
                        reportError("character.expected",
                                    new Object[] { new Character('b'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'l') {
                        reportError("character.expected",
                                    new Object[] { new Character('l'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    if (current != 'e') {
                        reportError("character.expected",
                                    new Object[] { new Character('e'),
                                                   new Integer(current) });
                        break loop;
                    }
                    read();
                    fragmentIdentifierHandler.zoomAndPan(false);
                    break;

                default:
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    break loop;
                }

                if (current != ')') {
                    reportError("character.expected",
                                new Object[] { new Character(')'),
                                               new Integer(current) });
                    break loop;
                }
                read();
            }
        }
    }

    /**
     * Parses an identifier.
     */
    protected void parseIdentifier() throws ParseException {
        for (;;) {
            if (current == -1 || !XMLUtilities.isXMLNameCharacter((char)current)) {
                break;
            }
            bufferize();
            read();
        }
    }

    /**
     * Parses the content of the buffer and converts it to a float.
     */
    protected float parseFloat()
	throws NumberFormatException,
	       ParseException {
	readNumber();
	return Float.parseFloat(getBufferContent());
    }

    /**
     * Returns the content of the buffer.
     */
    protected String getBufferContent() {
	return new String(buffer, 0, bufferSize);
    }

    /**
     * Adds the current character to the buffer.
     */
    protected void bufferize() {
	if (bufferSize >= buffer.length) {
	    char[] t = new char[buffer.length * 2];
	    for (int i = 0; i < bufferSize; i++) {
		t[i] = buffer[i];
	    }
	    buffer = t;
	}
	buffer[bufferSize++] = (char)current;
    }

    /**
     * Reads a number.
     */
    protected void readNumber() throws ParseException {
	bufferSize = 0;
        for (;;) {
	    switch (current) {
	    case ',':
	    case ')':
                return;
	    default:
		if (current == -1) {
		    return;
		}
		bufferize();
	    }
	    read();
	}
    }

    /**
     * Skips the whitespaces in the current reader.
     */
    protected void skipSpaces() {
	if (current == ',') {
            read();
	}
    }

    /**
     * Skips the whitespaces and an optional comma.
     */
    protected void skipCommaSpaces() {
	if (current == ',') {
            read();
	}
    }

    /**
     * Parses a matrix transform. 'm' is assumed to be the current character.
     */
    protected void parseMatrix() throws ParseException {
	read();

	// Parse 'atrix wsp? ( wsp?'
	if (current != 'a') {
	    reportError("character.expected",
			new Object[] { new Character('a'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 't') {
	    reportError("character.expected",
			new Object[] { new Character('t'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'r') {
	    reportError("character.expected",
			new Object[] { new Character('r'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'i') {
	    reportError("character.expected",
			new Object[] { new Character('i'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'x') {
	    reportError("character.expected",
			new Object[] { new Character('x'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	skipSpaces();
	if (current != '(') {
	    reportError("character.expected",
			new Object[] { new Character('('),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	skipSpaces();

	try {
	    float a = parseFloat();
	    skipCommaSpaces();
	    float b = parseFloat();
	    skipCommaSpaces();
	    float c = parseFloat();
	    skipCommaSpaces();
	    float d = parseFloat();
	    skipCommaSpaces();
	    float e = parseFloat();
	    skipCommaSpaces();
	    float f = parseFloat();
	
	    // Parse 'wsp? )'
	    skipSpaces();
	    if (current != ')') {
		reportError("character.expected",
			    new Object[] { new Character(')'),
					   new Integer(current) });
		skipTransform();
		return;
	    }

	    fragmentIdentifierHandler.matrix(a, b, c, d, e, f);
	} catch (NumberFormatException ex) {
	    reportError("float.format", new Object[] { getBufferContent() });
	    skipTransform();
	}
    }

    /**
     * Parses a rotate transform. 'r' is assumed to be the current character.
     * @return the current character.
     */
    protected void parseRotate() throws ParseException {
	read();

	// Parse 'otate wsp? ( wsp?'
	if (current != 'o') {
	    reportError("character.expected",
			new Object[] { new Character('o'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 't') {
	    reportError("character.expected",
			new Object[] { new Character('t'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'a') {
	    reportError("character.expected",
			new Object[] { new Character('a'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 't') {
	    reportError("character.expected",
			new Object[] { new Character('t'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'e') {
	    reportError("character.expected",
			new Object[] { new Character('e'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	skipSpaces();

	if (current != '(') {
	    reportError("character.expected",
			new Object[] { new Character('('),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	skipSpaces();

	try {
	    float theta = parseFloat();
	    skipSpaces();

	    switch (current) {
	    case ')':
		fragmentIdentifierHandler.rotate(theta);
		return;
	    case ',':
		read();
		skipSpaces();
	    }

	    float cx = parseFloat();
	    skipCommaSpaces();
	    float cy = parseFloat();

	    // Parse 'wsp? )'
	    skipSpaces();
	    if (current != ')') {
		reportError("character.expected",
			    new Object[] { new Character(')'),
					   new Integer(current) });
		skipTransform();
		return;
	    }

	    fragmentIdentifierHandler.rotate(theta, cx, cy);
	} catch (NumberFormatException e) {
	    reportError("float.format", new Object[] { getBufferContent() });
	    skipTransform();
	}
    }

    /**
     * Parses a translate transform. 't' is assumed to be
     * the current character.
     * @return the current character.
     */
    protected void parseTranslate() throws ParseException {
	read();

	// Parse 'ranslate wsp? ( wsp?'
	if (current != 'r') {
	    reportError("character.expected",
			new Object[] { new Character('r'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'a') {
	    reportError("character.expected",
			new Object[] { new Character('a'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'n') {
	    reportError("character.expected",
			new Object[] { new Character('n'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 's') {
	    reportError("character.expected",
			new Object[] { new Character('s'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'l') {
	    reportError("character.expected",
			new Object[] { new Character('l'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'a') {
	    reportError("character.expected",
			new Object[] { new Character('a'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 't') {
	    reportError("character.expected",
			new Object[] { new Character('t'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'e') {
	    reportError("character.expected",
			new Object[] { new Character('e'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	skipSpaces();
	if (current != '(') {
	    reportError("character.expected",
			new Object[] { new Character('('),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	skipSpaces();

	try {
	    float tx = parseFloat();
	    skipSpaces();

	    switch (current) {
	    case ')':
		fragmentIdentifierHandler.translate(tx);
		return;
	    case ',':
		read();
		skipSpaces();
	    }

	    float ty = parseFloat();

	    // Parse 'wsp? )'
	    skipSpaces();
	    if (current != ')') {
		reportError("character.expected",
			    new Object[] { new Character(')'),
					   new Integer(current) });
		skipTransform();
		return;
	    }

	    fragmentIdentifierHandler.translate(tx, ty);
	} catch (NumberFormatException e) {
	    reportError("float.format", new Object[] { getBufferContent() });
	    skipTransform();
	}
    }

    /**
     * Parses a scale transform. 'c' is assumed to be the current character.
     * @return the current character.
     */
    protected void parseScale() throws ParseException {
	read();

	// Parse 'ale wsp? ( wsp?'
	if (current != 'a') {
	    reportError("character.expected",
			new Object[] { new Character('a'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'l') {
	    reportError("character.expected",
			new Object[] { new Character('l'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'e') {
	    reportError("character.expected",
			new Object[] { new Character('e'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	skipSpaces();
	if (current != '(') {
	    reportError("character.expected",
			new Object[] { new Character('('),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	skipSpaces();

	try {
	    float sx = parseFloat();
	    skipSpaces();

	    switch (current) {
	    case ')':
		fragmentIdentifierHandler.scale(sx);
		return;
	    case ',':
		read();
		skipSpaces();
	    }

	    float sy = parseFloat();

	    // Parse 'wsp? )'
	    skipSpaces();
	    if (current != ')') {
		reportError("character.expected",
			    new Object[] { new Character(')'),
					   new Integer(current) });
		skipTransform();
		return;
	    }

	    fragmentIdentifierHandler.scale(sx, sy);
	} catch (NumberFormatException e) {
	    reportError("float.format", new Object[] { getBufferContent() });
	    skipTransform();
	}
    }

    /**
     * Parses a skew transform. 'e' is assumed to be the current character.
     * @return the current character.
     */
    protected void parseSkew() throws ParseException {
	read();

	// Parse 'ew[XY] wsp? ( wsp?'
	if (current != 'e') {
	    reportError("character.expected",
			new Object[] { new Character('e'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	if (current != 'w') {
	    reportError("character.expected",
			new Object[] { new Character('w'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();

	boolean skewX = false;
	switch (current) {
	case 'X':
	    skewX = true;
	case 'Y':
	    break;
	default:
	    reportError("character.expected",
			new Object[] { new Character('X'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	skipSpaces();
	if (current != '(') {
	    reportError("character.expected",
			new Object[] { new Character('('),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	read();
	skipSpaces();

	try {
	    float sk = parseFloat();

	    // Parse 'wsp? )'
	    skipSpaces();
	    if (current != ')') {
		reportError("character.expected",
			    new Object[] { new Character(')'),
					   new Integer(current) });
		skipTransform();
		return;
	    }

	    if (skewX) {
		fragmentIdentifierHandler.skewX(sk);
	    } else {
		fragmentIdentifierHandler.skewY(sk);
	    }
	} catch (NumberFormatException e) {
	    reportError("float.format", new Object[] { getBufferContent() });
	    skipTransform();
	}
    }

    /**
     * Skips characters in the given reader until a ')' is encountered.
     * @return the first character after the ')'.
     */
    protected void skipTransform() {
	loop: for (;;) {
	    read();
	    switch (current) {
	    case ')':
		break loop;
	    default:
		if (current == -1) {
		    break loop;
		}
	    }
	}
    }

    /**
     * Parses a PreserveAspectRatio attribute.
     */
    protected void parsePreserveAspectRatio() throws ParseException {
	fragmentIdentifierHandler.startPreserveAspectRatio();

        align: switch (current) {
        case 'n':
	    read();
	    if (current != 'o') {
		reportError("character.expected",
			    new Object[] { new Character('o'),
                                           new Integer(current) });
		skipIdentifier();
		break align;
	    }
	    read();
	    if (current != 'n') {
		reportError("character.expected",
			    new Object[] { new Character('n'),
					   new Integer(current) });
		skipIdentifier();
		break align;
	    }
	    read();
	    if (current != 'e') {
		reportError("character.expected",
			    new Object[] { new Character('e'),
					   new Integer(current) });
		skipIdentifier();
		break align;
	    }
	    read();
	    skipSpaces();
	    fragmentIdentifierHandler.none();
            break;
                
        case 'x':
            read();
            if (current != 'M') {
                reportError("character.expected",
                            new Object[] { new Character('M'),
					   new Integer(current) });
                skipIdentifier();
                break;
            }
            read();
            switch (current) {
            case 'a':
                read();
                if (current != 'x') {
                    reportError("character.expected",
                                new Object[] { new Character('x'),
			          	       new Integer(current) });
                    skipIdentifier();
                    break align;
                }
                read();
                if (current != 'Y') {
                    reportError("character.expected",
                                new Object[] { new Character('Y'),
					       new Integer(current) });
                    skipIdentifier();
                    break align;
                }
                read();
                if (current != 'M') {
                    reportError("character.expected",
                                new Object[] { new Character('M'),
					       new Integer(current) });
                    skipIdentifier();
                    break align;
                }
                read();
                switch (current) {
                case 'a':
                    read();
                    if (current != 'x') {
                        reportError("character.expected",
                                    new Object[] { new Character('x'),
						   new Integer(current) });
                        skipIdentifier();
                        break align;
                    }
                    fragmentIdentifierHandler.xMaxYMax();
                    read();
                    break;
                case 'i':
                    read();
                    switch (current) {
                    case 'd':
                        fragmentIdentifierHandler.xMaxYMid();
                        read();
                        break;
                    case 'n':
                        fragmentIdentifierHandler.xMaxYMin();
                        read();
                        break;
                    default:
                        reportError("character.unexpected",
                                    new Object[] { new Integer(current) });
                        skipIdentifier();
                        break align;
                    }
                }
                break;
            case 'i':
                read();
                switch (current) {
                case 'd':
                    read();
                    if (current != 'Y') {
                        reportError("character.expected",
                                    new Object[] { new Character('Y'),
						   new Integer(current) });
                        skipIdentifier();
                        break align;
                    }
                    read();
                    if (current != 'M') {
                        reportError("character.expected",
                                    new Object[] { new Character('M'),
						   new Integer(current) });
                        skipIdentifier();
                        break align;
                    }
                    read();
                    switch (current) {
                    case 'a':
                        read();
                        if (current != 'x') {
                            reportError
                                ("character.expected",
                                 new Object[] { new Character('x'),
                                                    new Integer(current) });
			    skipIdentifier();
			    break align;
                        }
                        fragmentIdentifierHandler.xMidYMax();
                        read();
                        break;
                    case 'i':
                        read();
                        switch (current) {
                        case 'd':
			    fragmentIdentifierHandler.xMidYMid();
			    read();
			    break;
                        case 'n':
                            fragmentIdentifierHandler.xMidYMin();
                            read();
                            break;
			default:
			    reportError("character.unexpected",
					new Object[] { new Integer(current) });
			    skipIdentifier();
			    break align;
                        }
                    }
                    break;
                case 'n':
                    read();
                    if (current != 'Y') {
                        reportError("character.expected",
                                    new Object[] { new Character('Y'),
					           new Integer(current) });
                        skipIdentifier();
                        break align;
                    }
                    read();
                    if (current != 'M') {
                        reportError("character.expected",
                                    new Object[] { new Character('M'),
						   new Integer(current) });
                        skipIdentifier();
                        break align;
                    }
                    read();
                    switch (current) {
                    case 'a':
                        read();
                        if (current != 'x') {
                            reportError
                                ("character.expected",
                                 new Object[] { new Character('x'),
                                                new Integer(current) });
                            skipIdentifier();
                            break align;
                        }
                        fragmentIdentifierHandler.xMinYMax();
                        read();
                        break;
                    case 'i':
                        read();
                        switch (current) {
                        case 'd':
                            fragmentIdentifierHandler.xMinYMid();
                            read();
                            break;
                        case 'n':
                            fragmentIdentifierHandler.xMinYMin();
                            read();
                            break;
                        default:
                            reportError
                                ("character.unexpected",
                                 new Object[] { new Integer(current) });
                            skipIdentifier();
                            break align;
                        }
                    }
                    break;
                default:
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    skipIdentifier();
                    break align;
                }
                break;
            default:
                reportError("character.unexpected",
                            new Object[] { new Integer(current) });
                skipIdentifier();
            }
            break;
        default:
            if (current != -1) {
                reportError("character.unexpected",
                            new Object[] { new Integer(current) });
                skipIdentifier();
            }
        }

        skipCommaSpaces();

        switch (current) {
        case 'm':
            read();
            if (current != 'e') {
                reportError("character.expected",
                            new Object[] { new Character('e'),
				           new Integer(current) });
                skipIdentifier();
                break;
            }
            read();
            if (current != 'e') {
                reportError("character.expected",
                            new Object[] { new Character('e'),
			         	   new Integer(current) });
                skipIdentifier();
                break;
            }
            read();
            if (current != 't') {
                reportError("character.expected",
                            new Object[] { new Character('t'),
	        			   new Integer(current) });
                skipIdentifier();
                break;
            }
            fragmentIdentifierHandler.meet();
            read();
            break;
        case 's':
            read();
            if (current != 'l') {
                reportError("character.expected",
                            new Object[] { new Character('l'),
				           new Integer(current) });
                skipIdentifier();
                break;
            }
            read();
            if (current != 'i') {
                reportError("character.expected",
                            new Object[] { new Character('i'),
					   new Integer(current) });
                skipIdentifier();
                break;
            }
            read();
            if (current != 'c') {
                reportError("character.expected",
                            new Object[] { new Character('c'),
			        	   new Integer(current) });
                skipIdentifier();
                break;
            }
            read();
            if (current != 'e') {
                reportError("character.expected",
                            new Object[] { new Character('e'),
					   new Integer(current) });
                skipIdentifier();
                break;
            }
            fragmentIdentifierHandler.slice();
            read();
        }

	fragmentIdentifierHandler.endPreserveAspectRatio();
    }

    /**
     * Skips characters in the given reader until a white space is encountered.
     * @return the first character after the space.
     */
    protected void skipIdentifier() {
	loop: for (;;) {
	    read();
	    switch(current) {
	    case 0xD: case 0xA: case 0x20: case 0x9:
		read();
		break loop;
	    default:
		if (current == -1) {
		    break loop;
		}
	    }
	}
    }
}
