/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;

import org.w3c.css.sac.Parser;

/**
 * This class implements the {@link org.apache.batik.css.ExtendedParser} 
 * interface by wrapping a standard {@link org.w3c.css.sac.Parser}.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public class ExtendedParserWrapper implements ExtendedParser {

    /**
     * This converts a standard @link org.w3c.css.sac.Parser into
     * an Exteneded Parser.  If it is already an ExtendedParser
     * it will simply cast it and return, otherwise it will wrap it
     * and return the result.
     * @param p Parser to wrap.
     * @return p as an ExtenedParser.
     */
    public static ExtendedParser wrap(Parser p) {
	if (p instanceof ExtendedParser)
	    return (ExtendedParser)p;

	return new ExtendedParserWrapper(p);
    }


    public Parser parser;

    public ExtendedParserWrapper(Parser parser) {
	this.parser = parser;
    }
    
    /**
     * Returns a string about which CSS language is supported by this
     * parser. For CSS Level 1, it returns "CSS1", for CSS Level 2, it returns
     * "CSS2". For CSS Level 3, it returns "CSS3", etc. Note that a "CSSx"
     * parser can return lexical unit other than those allowed by CSS Level x
     * but this usage is not recommended.
     */
    public String getParserVersion() {
	return parser.getParserVersion();
    }
    
    /**
     * Allow an application to request a locale for errors and warnings.
     *
     * <p>CSS parsers are not required to provide localisation for errors
     * and warnings; if they cannot support the requested locale,
     * however, they must throw a CSS exception.  Applications may
     * not request a locale change in the middle of a parse.</p>
     *
     * @param locale A Java Locale object.
     * @exception CSSException Throws an exception
     *            (using the previous or default locale) if the 
     *            requested locale is not supported.
     * @see CSSException
     * @see CSSParseException
     */
    public void setLocale(Locale locale) throws CSSException {
	parser.setLocale(locale);
    }

    /**
     * Allow an application to register a document event handler.
     *
     * <p>If the application does not register a document handler, all
     * document events reported by the CSS parser will be silently
     * ignored (this is the default behaviour implemented by
     * HandlerBase).</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the CSS parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The document handler.
     * @see DocumentHandler
     */
    public void setDocumentHandler(DocumentHandler handler) {
	parser.setDocumentHandler(handler);
    }

    public void setSelectorFactory(SelectorFactory selectorFactory) {
	parser.setSelectorFactory(selectorFactory);
    }
    public void setConditionFactory(ConditionFactory conditionFactory) {
	parser.setConditionFactory(conditionFactory);
    }
    
    /**
     * Allow an application to register an error event handler.
     *
     * <p>If the application does not register an error event handler,
     * all error events reported by the CSS parser will be silently
     * ignored, except for fatalError, which will throw a CSSException
     * (this is the default behaviour implemented by HandlerBase).</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the CSS parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The error handler.
     * @see ErrorHandler
     * @see CSSException
     */
    public void setErrorHandler(ErrorHandler handler) {
	parser.setErrorHandler(handler);
    }
    
    /**
     * Parse a CSS document.
     *
     * <p>The application can use this method to instruct the CSS parser
     * to begin parsing an CSS document from any valid input
     * source (a character stream, a byte stream, or a URI).</p>
     *
     * <p>Applications may not invoke this method while a parse is in
     * progress (they should create a new Parser instead for each
     * additional CSS document).  Once a parse is complete, an
     * application may reuse the same Parser object, possibly with a
     * different input source.</p>
     *
     * @param source The input source for the top-level of the
     *        CSS document.
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see InputSource
     * @see #parseStyleSheet(java.lang.String)
     * @see #setDocumentHandler
     * @see #setErrorHandler
     */
    public void parseStyleSheet(InputSource source) 
	throws CSSException, IOException {
	parser.parseStyleSheet(source);
    }
    
    /**
     * Parse a CSS document from a URI.
     *
     * <p>This method is a shortcut for the common case of reading a document
     * from a URI.  It is the exact equivalent of the following:</p>
     *
     * <pre>
     * parse(new InputSource(uri));
     * </pre>
     *
     * <p>The URI must be fully resolved by the application before it is passed
     * to the parser.</p>
     *
     * @param uri The URI.
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see #parseStyleSheet(InputSource) 
     */
    public void parseStyleSheet(String uri) throws CSSException, IOException {
	parser.parseStyleSheet(uri);
    }

    /**
     * Parse a CSS style declaration (without '{' and '}').
     *
     * @param styleValue The declaration.
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */
    public void parseStyleDeclaration(InputSource source) 
	throws CSSException, IOException {
	parser.parseStyleDeclaration(source);
    }

    /**
     * Parse a CSS style declaration (without '{' and '}').
     *
     * @param styleValue The declaration.
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */
    public void parseStyleDeclaration(String source) 
	throws CSSException, IOException {
	parser.parseStyleDeclaration
	    (new InputSource(new StringReader(source)));
    }


    /**
     * Parse a CSS rule.
     *
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */
    public void parseRule(InputSource source) 
	throws CSSException, IOException {
	parser.parseRule(source);
    }

    /**
     * Parse a CSS rule.
     *
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */
    public void parseRule(String source) throws CSSException, IOException {
	parser.parseRule(new InputSource(new StringReader(source)));
    }
    
    /**
     * Parse a comma separated list of selectors.
     * 
     * 
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */    
    public SelectorList parseSelectors(InputSource source)
        throws CSSException, IOException {
	return parser.parseSelectors(source);
    }

    /**
     * Parse a comma separated list of selectors.
     * 
     * 
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */    
    public SelectorList parseSelectors(String source)
        throws CSSException, IOException {
	return parser.parseSelectors
	    (new InputSource(new StringReader(source)));
    }


    /**
     * Parse a CSS property value.
     * 
     * 
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */    
    public LexicalUnit parsePropertyValue(InputSource source)
        throws CSSException, IOException {
	return parser.parsePropertyValue(source);
    }

    /**
     * Parse a CSS property value.
     * 
     * 
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */    
    public LexicalUnit parsePropertyValue(String source)
        throws CSSException, IOException {
	return parser.parsePropertyValue
	    (new InputSource(new StringReader(source)));
    }

    
    /**
     * Parse a CSS priority value (e.g. "!important").
     * 
     * 
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */    
    public boolean parsePriority(InputSource source)
        throws CSSException, IOException {
	return parser.parsePriority(source);
    }

    /**
     * Parse a CSS priority value (e.g. "!important").
     * 
     * 
     * @exception CSSException Any CSS exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */    
    public boolean parsePriority(String source)
        throws CSSException, IOException {
	return parser.parsePriority(new InputSource(new StringReader(source)));
    }
}
