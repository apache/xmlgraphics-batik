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
 * This class implements an event-based parser for the SVG preserveAspectRatio
 * attribute values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PreserveAspectRatioParser extends AbstractParser {

    /**
     * The PreserveAspectRatio handler used to report parse events.
     */
    protected PreserveAspectRatioHandler preserveAspectRatioHandler;

    /**
     * Creates a new PreserveAspectRatioParser.
     */
    public PreserveAspectRatioParser() {
	preserveAspectRatioHandler =
            DefaultPreserveAspectRatioHandler.INSTANCE;
    }

    /**
     * Allows an application to register a PreserveAspectRatioParser handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform list handler.
     */
    public void setPreserveAspectRatioHandler(PreserveAspectRatioHandler handler) {
	preserveAspectRatioHandler = handler;
    }

    /**
     * Returns the length handler in use.
     */
    public PreserveAspectRatioHandler getPreserveAspectRatioHandler() {
        return preserveAspectRatioHandler;
    }

    /**
     * Parses the given reader.
     */
    protected void doParse() throws ParseException {
	read();
	skipSpaces();

        parsePreserveAspectRatio();
    }

    /**
     * Parses a PreserveAspectRatio attribute.
     */
    protected void parsePreserveAspectRatio() throws ParseException {
	preserveAspectRatioHandler.startPreserveAspectRatio();

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
	    preserveAspectRatioHandler.none();
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
                    preserveAspectRatioHandler.xMaxYMax();
                    read();
                    break;
                case 'i':
                    read();
                    switch (current) {
                    case 'd':
                        preserveAspectRatioHandler.xMaxYMid();
                        read();
                        break;
                    case 'n':
                        preserveAspectRatioHandler.xMaxYMin();
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
                        preserveAspectRatioHandler.xMidYMax();
                        read();
                        break;
                    case 'i':
                        read();
                        switch (current) {
                        case 'd':
			    preserveAspectRatioHandler.xMidYMid();
			    read();
			    break;
                        case 'n':
                            preserveAspectRatioHandler.xMidYMin();
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
                        preserveAspectRatioHandler.xMinYMax();
                        read();
                        break;
                    case 'i':
                        read();
                        switch (current) {
                        case 'd':
                            preserveAspectRatioHandler.xMinYMid();
                            read();
                            break;
                        case 'n':
                            preserveAspectRatioHandler.xMinYMin();
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
            preserveAspectRatioHandler.meet();
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
            preserveAspectRatioHandler.slice();
            read();
            break;
        default:
            if (current != -1) {
                reportError("character.unexpected",
                            new Object[] { new Integer(current) });
                skipIdentifier();
            }
        }

	skipSpaces();
	if (current != -1) {
	    reportError("end.of.stream.expected",
			new Object[] { new Integer(current) });
	}

	preserveAspectRatioHandler.endPreserveAspectRatio();
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
