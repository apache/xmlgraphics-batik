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

package org.apache.batik.css.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.StringTokenizer;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SelectorList;

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
     * Implements {@link ExtendedParser#parseMedia(String)}.
     */
    public SACMediaList parseMedia(String mediaText)
        throws CSSException, IOException {
        CSSSACMediaList result = new CSSSACMediaList();
        if (!"all".equalsIgnoreCase(mediaText)) {
            StringTokenizer st = new StringTokenizer(mediaText, " ,");
            while (st.hasMoreTokens()) {
                result.append(st.nextToken());
            }
        }
        return result;
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
