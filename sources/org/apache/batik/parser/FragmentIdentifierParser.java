/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

import java.io.Reader;

import org.apache.batik.util.XMLUtilities;

/**
 * This class represents an event-based parser for the SVG
 * fragment identifiers.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FragmentIdentifierParser extends PreserveAspectRatioParser {
    
    /**
     * The buffer used for numbers.
     */
    protected char[] buffer = new char[16];

    /**
     * The buffer size.
     */
    protected int bufferSize;

    /**
     * Creates a new FragmentIdentifier parser.
     */
    public FragmentIdentifierParser() {
        preserveAspectRatioHandler =
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
        preserveAspectRatioHandler = handler;
    }

    /**
     * Returns the points handler in use.
     */
    public FragmentIdentifierHandler getFragmentIdentfierHandler() {
        return (FragmentIdentifierHandler)preserveAspectRatioHandler;
    }

    /**
     * Returns the current handler.
     */
    protected FragmentIdentifierHandler getFragmentIdentifierHandler() {
        return (FragmentIdentifierHandler)preserveAspectRatioHandler;
    }

    /**
     * Parses the given reader.
     */
    public void parse(Reader r) throws ParseException {
	initialize(r);

	read();

        getFragmentIdentfierHandler().startFragmentIdentifier();

        ident: {
            String id = null;

            switch (current) {
            case 'x':
                inputBuffer.setMark();

                read();
                if (current != 'p') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 'o') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 'i') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 'n') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 'o') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 't') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 'e') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 'r') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != '(') {
                    parseIdentifier();
                    break;
                }
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
                inputBuffer.resetMark();
                parseIdentifier();

                char[] c = new char[inputBuffer.contentSize()];
                inputBuffer.readContent(c);

                id = new String(c);

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
                    break ident;
                }
                break;
            case 's':
                inputBuffer.setMark();

                read();
                if (current != 'v') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 'g') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 'V') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 'i') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 'e') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != 'w') {
                    parseIdentifier();
                    break;
                }
                read();
                if (current != '(') {
                    reportError("character.expected",
                                new Object[] { new Character('('),
                                               new Integer(current) });
                    break ident;
                }
                inputBuffer.unsetMark();
                read();

                parseViewAttributes();

                if (current != ')') {
                    reportError("character.expected",
                                new Object[] { new Character('('),
                                               new Integer(current) });
                }
                break ident;
            default:
                if (current == -1 ||
                    !XMLUtilities.isXMLNameFirstCharacter((char)current)) {
                    break ident;
                }
                inputBuffer.setMark();
                read();
                parseIdentifier();
                c = new char[inputBuffer.contentSize()];
                inputBuffer.readContent(c);

                id = new String(c);
            }
            getFragmentIdentfierHandler().idReference(id);
        }

        getFragmentIdentfierHandler().endFragmentIdentifier();
    }

    /**
     * Parses the svgView attributes.
     */
    protected void parseViewAttributes() throws ParseException {
        loop: for (;;) {
            switch (current) {
            case -1:
            case ')':
            default:
                break loop;
            case 'v':
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
                if (current != 'B') {
                    reportError("character.expected",
                                new Object[] { new Character('B'),
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

                float x = parseFloat();
                if (current != ',') {
                    reportError("character.expected",
                                new Object[] { new Character(','),
                                               new Integer(current) });
                    break loop;
                }

                float y = parseFloat();
                if (current != ',') {
                    reportError("character.expected",
                                new Object[] { new Character(','),
                                               new Integer(current) });
                    break loop;
                }

                float w = parseFloat();
                if (current != ',') {
                    reportError("character.expected",
                                new Object[] { new Character(','),
                                               new Integer(current) });
                    break loop;
                }

                float h = parseFloat();
                if (current != ')') {
                    reportError("character.expected",
                                new Object[] { new Character(')'),
                                               new Integer(current) });
                    break loop;
                }
                read();
                getFragmentIdentfierHandler().viewBox(x, y, w, h);
            }
        }
    }

    /**
     * Parses an identifier.
     */
    protected void parseIdentifier() throws ParseException {
        loop: for (;;) {
            if (current == -1 || !XMLUtilities.isXMLNameCharacter((char)current)) {
                break;
            }
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
	    read();
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
	}
    }
}
