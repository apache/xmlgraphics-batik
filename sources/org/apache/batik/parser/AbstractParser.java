/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

import java.io.IOException;
import java.io.Reader;

import java.text.MessageFormat;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.batik.i18n.LocalizableSupport;

/**
 * This class is the superclass of all parsers. It provides localization
 * and error handling methods.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractParser implements Parser {

    /**
     * The default resource bundle base name.
     */
    public final static String BUNDLE_CLASSNAME =
	"org.apache.batik.parser.resources.Messages";

    /**
     * The error handler.
     */
    protected ErrorHandler errorHandler = new DefaultErrorHandler();

    /**
     * The localizable support.
     */
    protected LocalizableSupport localizableSupport =
        new LocalizableSupport(BUNDLE_CLASSNAME);

    /**
     * The reader.
     */
    protected Reader reader;

    /**
     * The current line.
     */
    protected int line = 1;

    /**
     * The current column.
     */
    protected int column;

    /**
     * The buffer.
     */
    protected char[] buffer;

    /**
     * The current position in the buffer.
     */
    protected int position;

    /**
     * The current count of characters in the buffer.
     */
    protected int count;

    /**
     * The current character.
     */
    protected int current;

    /**
     * Returns the current character value.
     */
    public int getCurrent() {
	return current;
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
     * Allow an application to register an error event handler.
     *
     * <p>If the application does not register an error event handler,
     * all error events reported by the parser will cause an exception
     * to be thrown.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The error handler.
     */
    public void setErrorHandler(ErrorHandler handler) {
	errorHandler = handler;
    }

    /**
     * Parses the given reader
     */
    public void parse(Reader r)  throws ParseException {
        reader = r;
        buffer = new char[4096];

        doParse();
    }

    /**
     * Parses the given string.
     */
    public void parse(String s)  throws ParseException {
        reader = null;
        buffer = s.toCharArray();
        count = buffer.length;
        collapseCRNL(0, count);

        doParse();
    }

    /**
     * Method resposible for actually parsing data after AbstractParser
     * has initialized it's self.
     */
    protected abstract void doParse() throws ParseException ;

    protected final void collapseCRNL(int src, int end) {
        // Now collapse cr/nl...
        while (src < end) {
            if (buffer[src] != 13) {
                src++;
            } else {
                buffer[src] = 10;
                src++;
                if (src >= end) {
                    break;
                }
                if (buffer[src] == 10) {
                    // We now need to collapse some of the chars to
                    // eliminate cr/nl pairs.  This is where we do it...
                    int dst = src; // start writing where this 10 is
                    src++; // skip reading this 10.
                    while (src < end) {
                        if (buffer[src] == 13) {
                            buffer[dst++] = 10;
                            src++;
                            if (src >= end) {
                                break;
                            }
                            if (buffer[src] == 10) {
                                src++;
                            }
                            continue;
                        }
                        buffer[dst++] = buffer[src++];
                    }
                    end = dst;
                    break;
                }
            }
        }
    }

    protected final boolean fillBuffer() {
        try {
            if (count != 0) {
                if (position == count) {
                    buffer[0] = buffer[count - 1];
                    count = 1;
                    position = 1;
                } else {
                    // we keep the last char in our buffer.
                    System.arraycopy(buffer, position - 1, buffer, 0, 
                                     count - position + 1);
                    count = (count - position) + 1;
                    position = 1;
                }
            }

            if (reader == null) {
                return (count != position);
            }
            
            // remember where the fill starts...
            int src = count - 1;
            if (src < 0) {
                src = 0;
            }

            // Refill the buffer...
            int read = reader.read(buffer, count, buffer.length-count);
            if (read == -1) {
                return (count != position);
            }

            count += read; // add in chars read.
            collapseCRNL(src, count);
        } catch (IOException e) {
            errorHandler.error
                (new ParseException
                    (createErrorMessage("io.exception", null), e));
        }
        return (count != position);
    }

    /**
     * Reads one character from the given reader and sets 'current' to this
     * value.
     */
    protected void read() {
        if (position == count && !fillBuffer()) {
            current = -1;
            return;
        }

        if (current == 10) {
            line++;
            column = 1;
        } else {
            column++;
        }

        current = buffer[position++];
    }

    /**
     * Signals an error to the error handler.
     * @param key The message key in the resource bundle.
     * @param args The message arguments.
     */
    protected void reportError(String key, Object[] args)
        throws ParseException {
        errorHandler.error(new ParseException(createErrorMessage(key, args),
                                              line,
                                              column));
    }

    /**
     * Returns a localized error message.
     * @param key The message key in the resource bundle.
     * @param args The message arguments.
     */
    protected String createErrorMessage(String key, Object[] args) {
        try {
            return formatMessage(key, args);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the resource bundle base name.
     * @return BUNDLE_CLASSNAME.
     */
    protected String getBundleClassName() {
        return BUNDLE_CLASSNAME;
    }

    /**
     * Skips the whitespaces in the current reader.
     */
    protected void skipSpaces() {
        switch (current) {
        default:
            return;
        case 0x20:
        case 0x9:
        case 0xD:
        case 0xA:
        }
        for (;;) {
            if (position == count && !fillBuffer()) {
                current = -1;
                return;
            }

            if (current == 10) {
                line++;
                column = 1;
            } else {
                column++;
            }

            current = buffer[position++];

            switch (current) {
            default:
                return;
            case 0x20: case 0x09: case 0x0D: case 0x0A:
            }
        }
    }

    /**
     * Skips the whitespaces and an optional comma.
     */
    protected void skipCommaSpaces() {
        switch (current) {
        default:
            return;
        case 0x20: case 0x09: case 0x0D: case 0x0A:
            break;
        case ',': 
            for(;;) {
                if (position == count && !fillBuffer()) {
                    current = -1;
                    return;
                }

                if (current == 10) {
                    line++;
                    column = 1;
                } else {
                    column++;
                }

                current = buffer[position++];

                switch (current) {
                default:
                    return;
                case 0x20: case 0x09: case 0x0D: case 0x0A:
                }
            }
        }

        for(;;) {
            if (position == count && !fillBuffer()) {
                current = -1;
                return;
            }

            if (current == 10) {
                line++;
                column = 1;
            } else {
                column++;
            }

            current = buffer[position++];

            switch (current) {
            default:
                return;
            case 0x20: case 0x09: case 0x0D: case 0x0A:
                break;
            case ',':
                for(;;) {
                    if (position == count && !fillBuffer()) {
                        current = -1;
                        return;
                    }

                    if (current == 10) {
                        line++;
                        column = 1;
                    } else {
                        column++;
                    }
                    current = buffer[position++];
                    switch (current) {
                    default:
                        return; 
                    case 0x20: case 0x09: case 0x0D: case 0x0A:
                    }
                }
            }
        }
    }
}
