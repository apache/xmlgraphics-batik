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
    protected int column = 1;

    /**
     * The buffer.
     */
    protected char[] buffer = new char[4096];

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
     * The previous char.
     */
    protected int previous;

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
     * Initializes the parser.
     */
    protected void initialize(Reader r) {
        reader = r;
    }

    /**
     * Reads one character from the given reader and sets 'current' to this
     * value.
     */
    protected void read() {
        try {
            if (position == count) {
                position = 0;
                count = reader.read(buffer, 0, buffer.length);
                if (count == -1) {
                    count = 0;
                    current = -1;
                    return;
                }
            }
            int c = buffer[position++];

            switch (c) {
            case -1:
                current = previous = -1;
                return;

            case 10:
                if (previous == 13) {
                    previous = 10;
                    read();
                    return;
                }
                line++;
                column = 1;
                previous = c;
                break;

            case 13:
                previous = c;
                c = 10;
                line++;
                column = 1;
                break;

            default:
                previous = c;
                column++;
            }
            current = c;
        } catch (IOException e) {
            errorHandler.error
                (new ParseException
                    (createErrorMessage("io.exception", null),
                     e));
        }
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
        for (;;) {
	    switch (current) {
	    default:
		return;
	    case 0x20:
	    case 0x9:
	    case 0xD:
	    case 0xA:
	    }
	    read();
	}
    }

    /**
     * Skips the whitespaces and an optional comma.
     */
    protected void skipCommaSpaces() {
        wsp1: for (;;) {
	    switch (current) {
	    default:
		break wsp1;
	    case 0x20:
	    case 0x9:
	    case 0xD:
	    case 0xA:
	    }
	    read();
	}
	if (current == ',') {
            wsp2: for (;;) {
		read();
		switch (current) {
		default:
		    break wsp2;
		case 0x20:
		case 0x9:
		case 0xD:
		case 0xA:
		}
	    }
	}
    }
}
