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
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.util.io.NormalizingReader;
import org.apache.batik.util.io.StreamNormalizingReader;
import org.apache.batik.util.io.StringNormalizingReader;

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
        new LocalizableSupport(BUNDLE_CLASSNAME, 
                               AbstractParser.class.getClassLoader());

    /**
     * The normalizing reader.
     */
    protected NormalizingReader reader;

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
`     *
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
    public void parse(Reader r) throws ParseException {
        try {
            reader = new StreamNormalizingReader(r);
            doParse();
        } catch (IOException e) {
            errorHandler.error
                (new ParseException
                 (createErrorMessage("io.exception", null), e));
        }
    }

    /**
     * Parses the given input stream. If the encoding is null,
     * ISO-8859-1 is used.
     */
    public void parse(InputStream is, String enc) throws ParseException {
        try {
            reader = new StreamNormalizingReader(is, enc);
            doParse();
        } catch (IOException e) {
            errorHandler.error
                (new ParseException
                 (createErrorMessage("io.exception", null), e));
        }
    }

    /**
     * Parses the given string.
     */
    public void parse(String s) throws ParseException {
        try {
            reader = new StringNormalizingReader(s);
            doParse();
        } catch (IOException e) {
            errorHandler.error
                (new ParseException
                 (createErrorMessage("io.exception", null), e));
        }
    }

    /**
     * Method responsible for actually parsing data after AbstractParser
     * has initialized itself.
     */
    protected abstract void doParse()
        throws ParseException, IOException;

    /**
     * Signals an error to the error handler.
     * @param key The message key in the resource bundle.
     * @param args The message arguments.
     */
    protected void reportError(String key, Object[] args)
        throws ParseException {
        errorHandler.error(new ParseException(createErrorMessage(key, args),
                                              reader.getLine(),
                                              reader.getColumn()));
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
    protected void skipSpaces() throws IOException {
        for (;;) {
            switch (current) {
            default:
                return;
            case 0x20:
            case 0x09:
            case 0x0D:
            case 0x0A:
            }
            current = reader.read();
        }
    }

    /**
     * Skips the whitespaces and an optional comma.
     */
    protected void skipCommaSpaces() throws IOException {
        wsp1: for (;;) {
	    switch (current) {
	    default:
		break wsp1;
	    case 0x20:
	    case 0x9:
	    case 0xD:
	    case 0xA:
	    }
	    current = reader.read();
	}
	if (current == ',') {
            wsp2: for (;;) {
		switch (current = reader.read()) {
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
