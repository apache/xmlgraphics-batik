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

package org.apache.batik.parser;

import java.io.IOException;

import org.apache.batik.xml.XMLUtilities;

/**
 * This class represents an event-based parser for the SVG
 * fragment identifiers.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FragmentIdentifierParser extends NumberParser {
    
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
    public void
        setFragmentIdentifierHandler(FragmentIdentifierHandler handler) {
        fragmentIdentifierHandler = handler;
    }

    /**
     * Returns the points handler in use.
     */
    public FragmentIdentifierHandler getFragmentIdentifierHandler() {
        return fragmentIdentifierHandler;
    }

    /**
     * Parses the current reader.
     */
    protected void doParse() throws ParseException, IOException {
        bufferSize = 0;
                
        current = reader.read();

        fragmentIdentifierHandler.startFragmentIdentifier();

        ident: {
            String id = null;

            switch (current) {
            case 'x':
                bufferize();
                current = reader.read();
                if (current != 'p') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != 'o') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != 'i') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != 'n') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != 't') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != 'e') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != 'r') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != '(') {
                    parseIdentifier();
                    break;
                }
                bufferSize = 0;
                current = reader.read();
                if (current != 'i') {
                    reportError("character.expected",
                                new Object[] { new Character('i'),
	         	                       new Integer(current) });
                    break ident;
                }
                current = reader.read();
                if (current != 'd') {
                    reportError("character.expected",
                                new Object[] { new Character('d'),
                                               new Integer(current) });
                    break ident;
                }
                current = reader.read();
                if (current != '(') {
                    reportError("character.expected",
                                new Object[] { new Character('('),
                                               new Integer(current) });
                    break ident;
                }
                current = reader.read();
                if (current != '"' && current != '\'') {
                    reportError("character.expected",
                                new Object[] { new Character('\''),
                                               new Integer(current) });
                    break ident;
                }
                char q = (char)current;
                current = reader.read();
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
                current = reader.read();
                if (current != ')') {
                    reportError("character.expected",
                                new Object[] { new Character(')'),
                                               new Integer(current) });
                    break ident;
                }
                current = reader.read();
                if (current != ')') {
                    reportError("character.expected",
                                new Object[] { new Character(')'),
                                               new Integer(current) });
                }
                break ident;

            case 's':
                bufferize();
                current = reader.read();
                if (current != 'v') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != 'g') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != 'V') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != 'i') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != 'e') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != 'w') {
                    parseIdentifier();
                    break;
                }
                bufferize();
                current = reader.read();
                if (current != '(') {
                    parseIdentifier();
                    break;
                }
                bufferSize = 0;
                current = reader.read();
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
                current = reader.read();
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
    protected void parseViewAttributes() throws ParseException, IOException {
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
                current = reader.read();
                break;
            case 'v':
                first = false;
                current = reader.read();
                if (current != 'i') {
                    reportError("character.expected",
                                new Object[] { new Character('i'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'e') {
                    reportError("character.expected",
                                new Object[] { new Character('e'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'w') {
                    reportError("character.expected",
                                new Object[] { new Character('w'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();

                switch (current) {
                case 'B':
                    current = reader.read();
                    if (current != 'o') {
                        reportError("character.expected",
                                    new Object[] { new Character('o'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'x') {
                        reportError("character.expected",
                                    new Object[] { new Character('x'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != '(') {
                        reportError("character.expected",
                                    new Object[] { new Character('('),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();

                    float x = parseFloat();
                    if (current != ',') {
                        reportError("character.expected",
                                    new Object[] { new Character(','),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    
                    float y = parseFloat();
                    if (current != ',') {
                        reportError("character.expected",
                                    new Object[] { new Character(','),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    
                    float w = parseFloat();
                    if (current != ',') {
                        reportError("character.expected",
                                    new Object[] { new Character(','),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    
                    float h = parseFloat();
                    if (current != ')') {
                        reportError("character.expected",
                                    new Object[] { new Character(')'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    fragmentIdentifierHandler.viewBox(x, y, w, h);
                    if (current != ')' && current != ';') {
                        reportError("character.expected",
                                    new Object[] { new Character(')'),
                                                   new Integer(current) });
                        break loop;
                    }
                    break;

                case 'T':
                    current = reader.read();
                    if (current != 'a') {
                        reportError("character.expected",
                                    new Object[] { new Character('a'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'r') {
                        reportError("character.expected",
                                    new Object[] { new Character('r'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'g') {
                        reportError("character.expected",
                                    new Object[] { new Character('g'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'e') {
                        reportError("character.expected",
                                    new Object[] { new Character('e'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 't') {
                        reportError("character.expected",
                                    new Object[] { new Character('t'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != '(') {
                        reportError("character.expected",
                                    new Object[] { new Character('('),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();

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
                        current = reader.read();
                        parseIdentifier();
                        String s = getBufferContent();

                        fragmentIdentifierHandler.viewTarget(s);

                        bufferSize = 0;
                        switch (current) {
                        case ')':
                            current = reader.read();
                            break id;
                        case ',':
                        case ';':
                            current = reader.read();
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
                current = reader.read();
                if (current != 'r') {
                    reportError("character.expected",
                                new Object[] { new Character('r'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'e') {
                    reportError("character.expected",
                                new Object[] { new Character('e'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 's') {
                    reportError("character.expected",
                                new Object[] { new Character('s'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'e') {
                    reportError("character.expected",
                                new Object[] { new Character('e'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'r') {
                    reportError("character.expected",
                                new Object[] { new Character('r'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'v') {
                    reportError("character.expected",
                                new Object[] { new Character('v'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'e') {
                    reportError("character.expected",
                                new Object[] { new Character('e'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'A') {
                    reportError("character.expected",
                                new Object[] { new Character('A'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 's') {
                    reportError("character.expected",
                                new Object[] { new Character('s'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'p') {
                    reportError("character.expected",
                                new Object[] { new Character('p'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'e') {
                    reportError("character.expected",
                                new Object[] { new Character('e'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'c') {
                    reportError("character.expected",
                                new Object[] { new Character('c'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 't') {
                    reportError("character.expected",
                                new Object[] { new Character('t'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'R') {
                    reportError("character.expected",
                                new Object[] { new Character('R'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'a') {
                    reportError("character.expected",
                                new Object[] { new Character('a'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 't') {
                    reportError("character.expected",
                                new Object[] { new Character('t'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'i') {
                    reportError("character.expected",
                                new Object[] { new Character('i'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'o') {
                    reportError("character.expected",
                                new Object[] { new Character('o'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != '(') {
                    reportError("character.expected",
                                new Object[] { new Character('('),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();

                parsePreserveAspectRatio();

                if (current != ')') {
                    reportError("character.expected",
                                new Object[] { new Character(')'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                break;

            case 't':
                first = false;
                current = reader.read();
                if (current != 'r') {
                    reportError("character.expected",
                                new Object[] { new Character('r'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'a') {
                    reportError("character.expected",
                                new Object[] { new Character('a'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'n') {
                    reportError("character.expected",
                                new Object[] { new Character('n'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 's') {
                    reportError("character.expected",
                                new Object[] { new Character('s'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'f') {
                    reportError("character.expected",
                                new Object[] { new Character('f'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'o') {
                    reportError("character.expected",
                                new Object[] { new Character('o'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'r') {
                    reportError("character.expected",
                                new Object[] { new Character('r'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'm') {
                    reportError("character.expected",
                                new Object[] { new Character('m'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != '(') {
                    reportError("character.expected",
                                new Object[] { new Character('('),
                                               new Integer(current) });
                    break loop;
                }

                fragmentIdentifierHandler.startTransformList();

                tloop: for (;;) {
                    try {
                        current = reader.read();
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
                            current = reader.read();
                            switch (current) {
                            case 'c':
                                parseScale();
                                break;
                            case 'k':
                                parseSkew();
                                break;
                            default:
                                reportError("character.unexpected",
                                            new Object[] {
                                                new Integer(current) });
                                skipTransform();
                            }
                            break;
                        default:
                            break tloop;
                        }
                    } catch (ParseException e) {
                        errorHandler.error(e);
                        skipTransform();
                    }
                }

                fragmentIdentifierHandler.endTransformList();
                break;

            case 'z':
                first = false;
                current = reader.read();
                if (current != 'o') {
                    reportError("character.expected",
                                new Object[] { new Character('o'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'o') {
                    reportError("character.expected",
                                new Object[] { new Character('o'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'm') {
                    reportError("character.expected",
                                new Object[] { new Character('m'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'A') {
                    reportError("character.expected",
                                new Object[] { new Character('A'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'n') {
                    reportError("character.expected",
                                new Object[] { new Character('n'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'd') {
                    reportError("character.expected",
                                new Object[] { new Character('d'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'P') {
                    reportError("character.expected",
                                new Object[] { new Character('P'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'a') {
                    reportError("character.expected",
                                new Object[] { new Character('a'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != 'n') {
                    reportError("character.expected",
                                new Object[] { new Character('n'),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();
                if (current != '(') {
                    reportError("character.expected",
                                new Object[] { new Character('('),
                                               new Integer(current) });
                    break loop;
                }
                current = reader.read();

                switch (current) {
                case 'm':
                    current = reader.read();
                    if (current != 'a') {
                        reportError("character.expected",
                                    new Object[] { new Character('a'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'g') {
                        reportError("character.expected",
                                    new Object[] { new Character('g'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'n') {
                        reportError("character.expected",
                                    new Object[] { new Character('n'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'i') {
                        reportError("character.expected",
                                    new Object[] { new Character('i'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'f') {
                        reportError("character.expected",
                                    new Object[] { new Character('f'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'y') {
                        reportError("character.expected",
                                    new Object[] { new Character('y'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    fragmentIdentifierHandler.zoomAndPan(true);
                    break;

                case 'd':
                    current = reader.read();
                    if (current != 'i') {
                        reportError("character.expected",
                                    new Object[] { new Character('i'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 's') {
                        reportError("character.expected",
                                    new Object[] { new Character('s'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'a') {
                        reportError("character.expected",
                                    new Object[] { new Character('a'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'b') {
                        reportError("character.expected",
                                    new Object[] { new Character('b'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'l') {
                        reportError("character.expected",
                                    new Object[] { new Character('l'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
                    if (current != 'e') {
                        reportError("character.expected",
                                    new Object[] { new Character('e'),
                                                   new Integer(current) });
                        break loop;
                    }
                    current = reader.read();
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
                current = reader.read();
            }
        }
    }

    /**
     * Parses an identifier.
     */
    protected void parseIdentifier() throws ParseException, IOException {
        for (;;) {
            if (current == -1 ||
                !XMLUtilities.isXMLNameCharacter((char)current)) {
                break;
            }
            bufferize();
            current = reader.read();
        }
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
     * Skips the whitespaces in the current reader.
     */
    protected void skipSpaces() throws IOException {
	if (current == ',') {
            current = reader.read();
	}
    }

    /**
     * Skips the whitespaces and an optional comma.
     */
    protected void skipCommaSpaces() throws IOException {
	if (current == ',') {
            current = reader.read();
	}
    }

    /**
     * Parses a matrix transform. 'm' is assumed to be the current character.
     */
    protected void parseMatrix() throws ParseException, IOException {
	current = reader.read();

	// Parse 'atrix wsp? ( wsp?'
	if (current != 'a') {
	    reportError("character.expected",
			new Object[] { new Character('a'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 't') {
	    reportError("character.expected",
			new Object[] { new Character('t'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'r') {
	    reportError("character.expected",
			new Object[] { new Character('r'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'i') {
	    reportError("character.expected",
			new Object[] { new Character('i'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'x') {
	    reportError("character.expected",
			new Object[] { new Character('x'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	skipSpaces();
	if (current != '(') {
	    reportError("character.expected",
			new Object[] { new Character('('),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	skipSpaces();

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
	
        skipSpaces();
        if (current != ')') {
            reportError("character.expected",
                        new Object[] { new Character(')'),
                                       new Integer(current) });
            skipTransform();
            return;
        }

        fragmentIdentifierHandler.matrix(a, b, c, d, e, f);
    }

    /**
     * Parses a rotate transform. 'r' is assumed to be the current character.
     * @return the current character.
     */
    protected void parseRotate() throws ParseException, IOException {
	current = reader.read();

	// Parse 'otate wsp? ( wsp?'
	if (current != 'o') {
	    reportError("character.expected",
			new Object[] { new Character('o'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 't') {
	    reportError("character.expected",
			new Object[] { new Character('t'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'a') {
	    reportError("character.expected",
			new Object[] { new Character('a'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 't') {
	    reportError("character.expected",
			new Object[] { new Character('t'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'e') {
	    reportError("character.expected",
			new Object[] { new Character('e'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	skipSpaces();

	if (current != '(') {
	    reportError("character.expected",
			new Object[] { new Character('('),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	skipSpaces();

        float theta = parseFloat();
        skipSpaces();

        switch (current) {
        case ')':
            fragmentIdentifierHandler.rotate(theta);
            return;
        case ',':
            current = reader.read();
            skipSpaces();
        }
        
        float cx = parseFloat();
        skipCommaSpaces();
        float cy = parseFloat();

        skipSpaces();
        if (current != ')') {
            reportError("character.expected",
                        new Object[] { new Character(')'),
                                       new Integer(current) });
            skipTransform();
            return;
        }

        fragmentIdentifierHandler.rotate(theta, cx, cy);
    }

    /**
     * Parses a translate transform. 't' is assumed to be
     * the current character.
     * @return the current character.
     */
    protected void parseTranslate() throws ParseException, IOException {
	current = reader.read();

	// Parse 'ranslate wsp? ( wsp?'
	if (current != 'r') {
	    reportError("character.expected",
			new Object[] { new Character('r'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'a') {
	    reportError("character.expected",
			new Object[] { new Character('a'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'n') {
	    reportError("character.expected",
			new Object[] { new Character('n'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 's') {
	    reportError("character.expected",
			new Object[] { new Character('s'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'l') {
	    reportError("character.expected",
			new Object[] { new Character('l'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'a') {
	    reportError("character.expected",
			new Object[] { new Character('a'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 't') {
	    reportError("character.expected",
			new Object[] { new Character('t'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'e') {
	    reportError("character.expected",
			new Object[] { new Character('e'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	skipSpaces();
	if (current != '(') {
	    reportError("character.expected",
			new Object[] { new Character('('),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	skipSpaces();

        float tx = parseFloat();
        skipSpaces();

        switch (current) {
        case ')':
            fragmentIdentifierHandler.translate(tx);
            return;
        case ',':
            current = reader.read();
            skipSpaces();
        }

        float ty = parseFloat();

        skipSpaces();
        if (current != ')') {
            reportError("character.expected",
                        new Object[] { new Character(')'),
                                       new Integer(current) });
            skipTransform();
            return;
        }

        fragmentIdentifierHandler.translate(tx, ty);
    }

    /**
     * Parses a scale transform. 'c' is assumed to be the current character.
     * @return the current character.
     */
    protected void parseScale() throws ParseException, IOException {
	current = reader.read();

	// Parse 'ale wsp? ( wsp?'
	if (current != 'a') {
	    reportError("character.expected",
			new Object[] { new Character('a'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'l') {
	    reportError("character.expected",
			new Object[] { new Character('l'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'e') {
	    reportError("character.expected",
			new Object[] { new Character('e'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	skipSpaces();
	if (current != '(') {
	    reportError("character.expected",
			new Object[] { new Character('('),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	skipSpaces();

        float sx = parseFloat();
        skipSpaces();

        switch (current) {
        case ')':
            fragmentIdentifierHandler.scale(sx);
            return;
        case ',':
            current = reader.read();
            skipSpaces();
        }

        float sy = parseFloat();

        skipSpaces();
        if (current != ')') {
            reportError("character.expected",
                        new Object[] { new Character(')'),
                                       new Integer(current) });
            skipTransform();
            return;
        }

        fragmentIdentifierHandler.scale(sx, sy);
    }

    /**
     * Parses a skew transform. 'e' is assumed to be the current character.
     * @return the current character.
     */
    protected void parseSkew() throws ParseException, IOException {
	current = reader.read();

	// Parse 'ew[XY] wsp? ( wsp?'
	if (current != 'e') {
	    reportError("character.expected",
			new Object[] { new Character('e'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	if (current != 'w') {
	    reportError("character.expected",
			new Object[] { new Character('w'),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();

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
	current = reader.read();
	skipSpaces();
	if (current != '(') {
	    reportError("character.expected",
			new Object[] { new Character('('),
				       new Integer(current) });
	    skipTransform();
	    return;
	}
	current = reader.read();
	skipSpaces();

        float sk = parseFloat();

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
    }

    /**
     * Skips characters in the given reader until a ')' is encountered.
     * @return the first character after the ')'.
     */
    protected void skipTransform() throws IOException {
	loop: for (;;) {
	    current = reader.read();
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
    protected void parsePreserveAspectRatio()
        throws ParseException, IOException {
	fragmentIdentifierHandler.startPreserveAspectRatio();

        align: switch (current) {
        case 'n':
	    current = reader.read();
	    if (current != 'o') {
		reportError("character.expected",
			    new Object[] { new Character('o'),
                                           new Integer(current) });
		skipIdentifier();
		break align;
	    }
	    current = reader.read();
	    if (current != 'n') {
		reportError("character.expected",
			    new Object[] { new Character('n'),
					   new Integer(current) });
		skipIdentifier();
		break align;
	    }
	    current = reader.read();
	    if (current != 'e') {
		reportError("character.expected",
			    new Object[] { new Character('e'),
					   new Integer(current) });
		skipIdentifier();
		break align;
	    }
	    current = reader.read();
	    skipSpaces();
	    fragmentIdentifierHandler.none();
            break;
                
        case 'x':
            current = reader.read();
            if (current != 'M') {
                reportError("character.expected",
                            new Object[] { new Character('M'),
					   new Integer(current) });
                skipIdentifier();
                break;
            }
            current = reader.read();
            switch (current) {
            case 'a':
                current = reader.read();
                if (current != 'x') {
                    reportError("character.expected",
                                new Object[] { new Character('x'),
			          	       new Integer(current) });
                    skipIdentifier();
                    break align;
                }
                current = reader.read();
                if (current != 'Y') {
                    reportError("character.expected",
                                new Object[] { new Character('Y'),
					       new Integer(current) });
                    skipIdentifier();
                    break align;
                }
                current = reader.read();
                if (current != 'M') {
                    reportError("character.expected",
                                new Object[] { new Character('M'),
					       new Integer(current) });
                    skipIdentifier();
                    break align;
                }
                current = reader.read();
                switch (current) {
                case 'a':
                    current = reader.read();
                    if (current != 'x') {
                        reportError("character.expected",
                                    new Object[] { new Character('x'),
						   new Integer(current) });
                        skipIdentifier();
                        break align;
                    }
                    fragmentIdentifierHandler.xMaxYMax();
                    current = reader.read();
                    break;
                case 'i':
                    current = reader.read();
                    switch (current) {
                    case 'd':
                        fragmentIdentifierHandler.xMaxYMid();
                        current = reader.read();
                        break;
                    case 'n':
                        fragmentIdentifierHandler.xMaxYMin();
                        current = reader.read();
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
                current = reader.read();
                switch (current) {
                case 'd':
                    current = reader.read();
                    if (current != 'Y') {
                        reportError("character.expected",
                                    new Object[] { new Character('Y'),
						   new Integer(current) });
                        skipIdentifier();
                        break align;
                    }
                    current = reader.read();
                    if (current != 'M') {
                        reportError("character.expected",
                                    new Object[] { new Character('M'),
						   new Integer(current) });
                        skipIdentifier();
                        break align;
                    }
                    current = reader.read();
                    switch (current) {
                    case 'a':
                        current = reader.read();
                        if (current != 'x') {
                            reportError
                                ("character.expected",
                                 new Object[] { new Character('x'),
                                                    new Integer(current) });
			    skipIdentifier();
			    break align;
                        }
                        fragmentIdentifierHandler.xMidYMax();
                        current = reader.read();
                        break;
                    case 'i':
                        current = reader.read();
                        switch (current) {
                        case 'd':
			    fragmentIdentifierHandler.xMidYMid();
			    current = reader.read();
			    break;
                        case 'n':
                            fragmentIdentifierHandler.xMidYMin();
                            current = reader.read();
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
                    current = reader.read();
                    if (current != 'Y') {
                        reportError("character.expected",
                                    new Object[] { new Character('Y'),
					           new Integer(current) });
                        skipIdentifier();
                        break align;
                    }
                    current = reader.read();
                    if (current != 'M') {
                        reportError("character.expected",
                                    new Object[] { new Character('M'),
						   new Integer(current) });
                        skipIdentifier();
                        break align;
                    }
                    current = reader.read();
                    switch (current) {
                    case 'a':
                        current = reader.read();
                        if (current != 'x') {
                            reportError
                                ("character.expected",
                                 new Object[] { new Character('x'),
                                                new Integer(current) });
                            skipIdentifier();
                            break align;
                        }
                        fragmentIdentifierHandler.xMinYMax();
                        current = reader.read();
                        break;
                    case 'i':
                        current = reader.read();
                        switch (current) {
                        case 'd':
                            fragmentIdentifierHandler.xMinYMid();
                            current = reader.read();
                            break;
                        case 'n':
                            fragmentIdentifierHandler.xMinYMin();
                            current = reader.read();
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
            current = reader.read();
            if (current != 'e') {
                reportError("character.expected",
                            new Object[] { new Character('e'),
				           new Integer(current) });
                skipIdentifier();
                break;
            }
            current = reader.read();
            if (current != 'e') {
                reportError("character.expected",
                            new Object[] { new Character('e'),
			         	   new Integer(current) });
                skipIdentifier();
                break;
            }
            current = reader.read();
            if (current != 't') {
                reportError("character.expected",
                            new Object[] { new Character('t'),
	        			   new Integer(current) });
                skipIdentifier();
                break;
            }
            fragmentIdentifierHandler.meet();
            current = reader.read();
            break;
        case 's':
            current = reader.read();
            if (current != 'l') {
                reportError("character.expected",
                            new Object[] { new Character('l'),
				           new Integer(current) });
                skipIdentifier();
                break;
            }
            current = reader.read();
            if (current != 'i') {
                reportError("character.expected",
                            new Object[] { new Character('i'),
					   new Integer(current) });
                skipIdentifier();
                break;
            }
            current = reader.read();
            if (current != 'c') {
                reportError("character.expected",
                            new Object[] { new Character('c'),
			        	   new Integer(current) });
                skipIdentifier();
                break;
            }
            current = reader.read();
            if (current != 'e') {
                reportError("character.expected",
                            new Object[] { new Character('e'),
					   new Integer(current) });
                skipIdentifier();
                break;
            }
            fragmentIdentifierHandler.slice();
            current = reader.read();
        }

	fragmentIdentifierHandler.endPreserveAspectRatio();
    }

    /**
     * Skips characters in the given reader until a white space is encountered.
     * @return the first character after the space.
     */
    protected void skipIdentifier() throws IOException {
	loop: for (;;) {
	    current = reader.read();
	    switch(current) {
	    case 0xD: case 0xA: case 0x20: case 0x9:
		current = reader.read();
            case -1:
                break loop;
	    }
	}
    }
}
