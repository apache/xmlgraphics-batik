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
 * This class implements the {@link org.apache.batik.css.parser.ExtendedParser} 
 * interface by wrapping a standard {@link org.w3c.css.sac.Parser}.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public class ExtendedParserWrapper implements ExtendedParser {

    /**
     * This converts a standard @link org.w3c.css.sac.Parser into
     * an Extended Parser.  If it is already an ExtendedParser
     * it will simply cast it and return, otherwise it will wrap it
     * and return the result.
     * @param p Parser to wrap.
     * @return p as an ExtendedParser.
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
     * <b>SAC</b>: Implements {@link org.w3c.css.sac.Parser#getParserVersion()}.
     */
    public String getParserVersion() {
	return parser.getParserVersion();
    }
    
    /**
     * <b>SAC</b>: Implements {@link org.w3c.css.sac.Parser#setLocale(Locale)}.
     */
    public void setLocale(Locale locale) throws CSSException {
	parser.setLocale(locale);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#setDocumentHandler(DocumentHandler)}.
     */
    public void setDocumentHandler(DocumentHandler handler) {
	parser.setDocumentHandler(handler);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#setSelectorFactory(SelectorFactory)}.
     */
    public void setSelectorFactory(SelectorFactory selectorFactory) {
	parser.setSelectorFactory(selectorFactory);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#setConditionFactory(ConditionFactory)}.
     */
    public void setConditionFactory(ConditionFactory conditionFactory) {
	parser.setConditionFactory(conditionFactory);
    }
    
    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#setErrorHandler(ErrorHandler)}.
     */
    public void setErrorHandler(ErrorHandler handler) {
	parser.setErrorHandler(handler);
    }
    
    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#parseStyleSheet(InputSource)}.
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
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#parseStyleDeclaration(InputSource)}.
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
     * @exception IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */
    public void parseStyleDeclaration(String source) 
	throws CSSException, IOException {
	parser.parseStyleDeclaration
	    (new InputSource(new StringReader(source)));
    }


    /**
     * <b>SAC</b>: Implements {@link org.w3c.css.sac.Parser#parseRule(InputSource)}.
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
     * <b>SAC</b>: Implements {@link org.w3c.css.sac.Parser#parseSelectors(InputSource)}.
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
     * <b>SAC</b>: Implements
     * {@link org.w3c.css.sac.Parser#parsePropertyValue(InputSource)}.
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
     * <b>SAC</b>: Implements
     * {@link org.w3c.css.sac.Parser#parsePriority(InputSource)}.
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
