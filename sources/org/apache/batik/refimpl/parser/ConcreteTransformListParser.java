/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.parser;

import java.io.Reader;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.TransformListHandler;
import org.apache.batik.parser.TransformListParser;

/**
 * This class implements an event-based parser for the SVG transform
 * attribute values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ConcreteTransformListParser
    extends    NumberParser
    implements TransformListParser {
    /**
     * The transform list handler used to report parse events.
     */
    protected TransformListHandler transformListHandler;

    /**
     * Creates a new TransformListParser.
     */
    public ConcreteTransformListParser() {
	transformListHandler = DefaultTransformListHandler.INSTANCE;
    }

    /**
     * Allows an application to register a transform list handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform handler.
     */
    public void setTransformListHandler(TransformListHandler handler) {
	transformListHandler = handler;
    }

    /**
     * Returns the transform list handler in use.
     */
    public TransformListHandler getTransformListHandler() {
	return transformListHandler;
    }

    /**
     * Parses the given reader.
     */
    public void parse(Reader r) throws ParseException {
	initialize(r);

	transformListHandler.startTransformList();

	loop: for (;;) {
	    read();
	    switch (current) {
	    case 0xD:
	    case 0xA:
	    case 0x20:
	    case 0x9:
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
		if (current == -1) {
		    break loop;
		}
		reportError("character.unexpected",
			    new Object[] { new Integer(current) });
		skipTransform();
	    }
	}
	skipSpaces();
	if (current != -1) {
	    reportError("end.of.stream.expected",
			new Object[] { new Integer(current) });
	}

	transformListHandler.endTransformList();
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

	    transformListHandler.matrix(a, b, c, d, e, f);
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
		transformListHandler.rotate(theta);
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

	    transformListHandler.rotate(theta, cx, cy);
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
		transformListHandler.translate(tx);
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

	    transformListHandler.translate(tx, ty);
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
		transformListHandler.scale(sx);
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

	    transformListHandler.scale(sx, sy);
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
		transformListHandler.skewX(sk);
	    } else {
		transformListHandler.skewY(sk);
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
     * Implements {@link NumberParser#readNumber()}.
     */
    protected void readNumber() throws ParseException {
	bufferSize = 0;
	bufferize();
        for (;;) {
	    read();
	    switch (current) {
	    case 0x20:
	    case 0x9:
	    case 0xD:
	    case 0xA:
	    case ',':
	    case ')':
		return;
	    default:
		if (current == -1) {
		    reportError("end.of.stream",  new Object[] {});
		    skipTransform();
		    return;
		}
		bufferize();
	    }
	}
    }
}
