/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

import java.io.Reader;

/**
 * This class implements an event-based parser for the SVG length
 * values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LengthParser extends AbstractParser {

    /**
     * The length handler used to report parse events.
     */
    protected LengthHandler lengthHandler;

    /**
     * Creates a new LengthParser.
     */
    public LengthParser() {
	lengthHandler = DefaultLengthHandler.INSTANCE;
    }

    /**
     * Allows an application to register a length handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform list handler.
     */
    public void setLengthHandler(LengthHandler handler) {
	lengthHandler = handler;
    }

    /**
     * Returns the length handler in use.
     */
    public LengthHandler getLengthHandler() {
	return lengthHandler;
    }

    protected void doParse() throws ParseException {
        lengthHandler.startLength();

        read();
        skipSpaces();
	
        parseLength();

        skipSpaces();
        if (current != -1) {
            reportError("end.of.stream.expected",
                        new Object[] { new Integer(current) });
        }
        lengthHandler.endLength();
    }

    /**
     * Parses a length value.
     */
    protected void parseLength() throws ParseException {
        int     mant      = 0;
        int     mantDig   = 0;
        boolean mantPos   = true;
        boolean mantRead  = false;

        int     exp       = 0;
        int     expDig    = 0;
        int     expAdj    = 0;
        boolean expPos    = true;

        int     unitState = 0;

        switch (current) {
        case '-':
            mantPos = false;
        case '+':
            if (position == count && !fillBuffer()) {
                current = -1;
                break;
            }
            current = buffer[position++];
            column++;
        }

        m1: switch (current) {
        case 10:
            line++;
            column = 1;
        default:
            reportError("character.unexpected",
                        new Object[] { new Integer(current) });
            return;

        case '.':
            break;

        case '0':
            mantRead = true;
            l: for (;;) {
                if (position == count && !fillBuffer()) {
                    current = -1;
                } else {
                    current = buffer[position++];
                    column++;
                }
                switch (current) {
                case '1': case '2': case '3': case '4': 
                case '5': case '6': case '7': case '8': case '9': 
                    break l;
                case 10:
                    line++;
                    column = 1;
                default:
                    break m1;
                case '0':
                }
            }

        case '1': case '2': case '3': case '4': 
        case '5': case '6': case '7': case '8': case '9': 
            mantRead = true;
            l: for (;;) {
                if (mantDig < 9) {
                    mantDig++;
                    mant = mant * 10 + (current - '0');
                } else {
                    expAdj++;
                }
                if (position == count && !fillBuffer()) {
                    current = -1;
                } else {
                    current = buffer[position++];
                    column++;
                }
                switch (current) {
                case 10:
                    line++;
                    column = 1;
                default:
                    break l;
                case '0': case '1': case '2': case '3': case '4': 
                case '5': case '6': case '7': case '8': case '9': 
                }                
            }
        }
        
        if (current == '.') {
            if (position == count && !fillBuffer()) {
                current = -1;
            } else {
                current = buffer[position++];
                column++;
            }
            m2: switch (current) {
            case 10:
                line++;
                column = 1;
            default:
            case 'e': case 'E':
                if (!mantRead) {
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    return;
                }
                break;

            case '0':
                if (mantDig == 0) {
                    l: for (;;) {
                        if (position == count && !fillBuffer()) {
                            current = -1;
                        } else {
                            current = buffer[position++];
                            column++;
                        }
                        expAdj--;
                        switch (current) {
                        case '1': case '2': case '3': case '4': 
                        case '5': case '6': case '7': case '8': case '9': 
                            break l;
                        case 10:
                            line++;
                            column = 1;
                        default:
                            break m2;
                        case '0':
                        }
                    }
                }
            case '1': case '2': case '3': case '4': 
            case '5': case '6': case '7': case '8': case '9': 
                l: for (;;) {
                    if (mantDig < 9) {
                        mantDig++;
                        mant = mant * 10 + (current - '0');
                        expAdj--;
                    }
                    if (position == count && !fillBuffer()) {
                        current = -1;
                    } else {
                        current = buffer[position++];
                        column++;
                    }
                    switch (current) {
                    case 10:
                        line++;
                        column = 1;
                    default:
                        break l;
                    case '0': case '1': case '2': case '3': case '4': 
                    case '5': case '6': case '7': case '8': case '9': 
                    }
                }
            }
        }

        boolean le = false;
        es: switch (current) {
        case 'e':
            le = true;
        case 'E':
            if (position == count && !fillBuffer()) {
                current = -1;
            } else {
                current = buffer[position++];
                column++;
            }
            switch (current) {
            case 10:
                line++;
                column = 1;
            default:
                reportError("character.unexpected",
                            new Object[] { new Integer(current) });
                return;
            case 'm':
                if (!le) {
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    return;
                }
                unitState = 1;
                break es;
            case 'x':
                if (!le) {
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    return;
                }
                unitState = 2;
                break es;
            case '-':
                expPos = false;
            case '+':
                if (position == count && !fillBuffer()) {
                    current = -1;
                } else {
                    current = buffer[position++];
                    column++;
                }
                switch (current) {
                case 10:
                    line++;
                    column = 1;
                default:
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    return;
                case '0': case '1': case '2': case '3': case '4': 
                case '5': case '6': case '7': case '8': case '9': 
                }
            case '0': case '1': case '2': case '3': case '4': 
            case '5': case '6': case '7': case '8': case '9': 
            }
            
            en: switch (current) {
            case '0':
                l: for (;;) {
                    if (position == count && !fillBuffer()) {
                        current = -1;
                    } else {
                        current = buffer[position++];
                        column++;
                    }
                    switch (current) {
                    case '1': case '2': case '3': case '4': 
                    case '5': case '6': case '7': case '8': case '9': 
                        break l;
                    case 10:
                        line++;
                        column = 1;
                    default:
                        break en;
                    case '0':
                    }
                }

            case '1': case '2': case '3': case '4': 
            case '5': case '6': case '7': case '8': case '9': 
                l: for (;;) {
                    if (expDig < 3) {
                        expDig++;
                        exp = exp * 10 + (current - '0');
                    }
                    if (position == count && !fillBuffer()) {
                        current = -1;
                    } else {
                        current = buffer[position++];
                        column++;
                    }
                    switch (current) {
                    case 10:
                        line++;
                        column = 1;
                    default:
                        break l;
                    case '0': case '1': case '2': case '3': case '4': 
                    case '5': case '6': case '7': case '8': case '9': 
                    }
                }
            }
        default:
        }

        if (!expPos) {
            exp = -exp;
        }
        exp += expAdj;
        if (!mantPos) {
            mant = -mant;
        }

        lengthHandler.lengthValue(NumberParser.buildFloat(mant, exp));

        switch (unitState) {
        case 1:
            lengthHandler.em();
            read();
            return;
        case 2:
            lengthHandler.ex();
            read();
            return;
        }

        switch (current) {
        case -1: case 0xD: case 0xA: case 0x20: case 0x9:
            return;
        case 'e':
            read();
            switch (current) {
            case 'm':
                lengthHandler.em();
                read();
                break;
            case 'x':
                lengthHandler.ex();
                read();
                break;
            default:
                reportError("character.unexpected",
                            new Object[] { new Integer(current) });
            }
            break;

        case 'p':
            read();
            switch (current) {
            case 'c':
                lengthHandler.pc();
                read();
                break;
            case 't':
                lengthHandler.pt();
                read();
                break;
            case 'x':
                lengthHandler.px();
                read();
                break;
            default:
                reportError("character.unexpected",
                            new Object[] { new Integer(current) });
            }
            break;

        case 'i':
            read();
            if (current != 'n') {
                reportError("character.expected",
                            new Object[] { new Character('n'),
                                           new Integer(current) });
                break;
            }
            lengthHandler.in();
            read();
            break;
        case 'c':
            read();
            if (current != 'm') {
                reportError("character.expected",
                            new Object[] { new Character('m'),
                                           new Integer(current) });
                break;
            }
            lengthHandler.cm();
            read();
            break;
        case 'm':
            read();
            if (current != 'm') {
                reportError("character.expected",
                            new Object[] { new Character('m'),
                                           new Integer(current) });
                break;
            }
            lengthHandler.mm();
            read();
            break;
        case '%':
            lengthHandler.percentage();
            read();
            break;
        default:
            reportError("character.unexpected",
                        new Object[] { new Integer(current) });
        }
    }
}
