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
	
        try {
            parseLength();

            skipSpaces();
            if (current != -1) {
                reportError("end.of.stream.expected",
                            new Object[] { new Integer(current) });
            }
        } catch (NumberFormatException e) {
            reportError("character.unexpected", 
                        new Object[] { new Integer(current) });
        }
        lengthHandler.endLength();
    }

    /**
     * Parses a length value.
     */
    protected void parseLength()
	throws ParseException,
	       NumberFormatException {
        // readNumber();
        // float ret = Float.parseFloat(getBufferContent());

        int mant       =0;
        int mantDig    =0;
        boolean mantPos=true;
        boolean mantRead=false;
        int exp        =0;
        int expDig     =0;
        int expAdj     =0;
        boolean expPos =true;

        int     eRead     = 0;
        boolean eJustRead = false;
        boolean first     = true;
        boolean done      = false;
        boolean unitDone  = false;
        boolean dotRead   = false;

        while (!done) {
            switch (current) {
            case '0': case '1': case '2': case '3': case '4': 
            case '5': case '6': case '7': case '8': case '9': 
                if (eRead==0) {
                    // Still creating mantisa
                    mantRead = true;
                    // Only keep first 9 digits rest won't count anyway...
                    if (mantDig >= 9) {
                        if (!dotRead)
                            expAdj++;
                        break;
                    }
                    if (dotRead) expAdj--;
                    if ((mantDig != 0) || (current != '0')) {
                                // Ignore leading zeros.
                        mantDig++;
                        mant = mant*10+(current-'0');
                    }
                } else {
                    // Working on exp.
                    if (expDig >= 3) break;
                    if ((expDig != 0) || (current != '0')) {
                                // Ignore leading zeros.
                        expDig++;
                        expDig = expDig*10+(current-'0');
                    }
                }
                eJustRead = false;
                break;
            case 'e': case 'E':
                if (eRead > 1) {
                    done = true;
                    break;
                }
                eJustRead = true;
                eRead++;
                break;
            case 'm': // Confusing case between 10e5 and 10em
            case 'x':
                if (!eJustRead) {
                    done = true;
                    break;
                }
                if (current == 'm')
                    lengthHandler.em();
                else 
                    lengthHandler.ex();
                done     = true;
                unitDone = true;
                read(); // eat the 'm' or 'x' char.
                break;
            case '.':
                if ((eRead!=0) || dotRead) {
                    done=true;
                    break;
                }
                dotRead = true;
                break;
            case '+': 
                if ((!first) && (!eJustRead))
                    done=true;
                eJustRead = false;
                break;
            case '-':
                if      (first)     mantPos = false;
                else if (eJustRead) expPos  = false;
                else                done    = true;
                eJustRead = false;
                break;

            case 10:
                line++;
                column =1;
                done = true;
                break;
                
            default:
                done=true;
                break;
            }
            if (!done) {
                first = false;
                if ((position == count) && (!fillBuffer())) {
                    current = -1;
                    break;
                }
                current = buffer[position++];
                column++;
            }
        }

        if (!mantRead)
            throw new NumberFormatException
                ("No digits where number expected '" + ((char)current) + "'");

        if (!expPos) exp = -exp;
        exp += expAdj; // account for digits after 'dot'.
        if (!mantPos) mant = -mant;

        lengthHandler.lengthValue(NumberParser.buildFloat(mant, exp));
	
        if(unitDone)
            return;

        switch (current) {
	    case -1: case 0xD: case 0xA: case 0x20: case 0x9:
            return;
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
