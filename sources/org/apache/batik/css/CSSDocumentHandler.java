/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import java.io.StringReader;
import org.apache.batik.css.value.ValueFactory;
import org.apache.batik.css.value.ValueFactoryMap;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;

/**
 * This class provides conveniant methods for parsing style sheets and
 * implements the {@link org.w3c.css.sac.DocumentHandler} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSDocumentHandler implements DocumentHandler {
    /**
     * The parser class.
     */
    protected static Class parserClass = "org.apache.batik.css.parser.Parser";

    /**
     * The CSS parser.
     */
    protected Parser parser;

    /**
     * The value factory map.
     */
    protected ValueFactoryMap factories;

    /**
     * The current rule.
     */
    protected CSSRule currentRule;

    /**
     * The style sheet.
     */
    protected CSSOMStyleSheet styleSheet;

    /**
     * Whether the parsed rules must be added to the style sheet.
     */
    protected boolean append;

    /**
     * Sets the parser class name.
     */
    public static void setParserClassName(String name) {
	try {
	    parserClass = Class.forName(name);
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    /**
     * Creates a new parser.
     */
    public static Parser createParser() {
	try {
	    return (Parser)parserClass.newInstance();
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    /**
     * Parses a style sheet.
     */
    public static void parseStyleSheet(CSSOMStyleSheet ss, String uri)
        throws DOMException {
	try {
	    Parser parser = ss.getParser();
	    InputSource is = new InputSource(uri);
	    parser.setSelectorFactory(AbstractCSSRule.SELECTOR_FACTORY);
	    parser.setConditionFactory(AbstractCSSRule.CONDITION_FACTORY);
	    parser.setDocumentHandler(new CSSDocumentHandler(ss, true));
	    parser.parseStyleSheet(is);
	} catch (DOMException e) {
	    throw e;
	} catch (Exception e) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.SYNTAX_ERR,
		 "syntax.error.at",
		 new Object[] { uri });
	}
    }

    /**
     * Parses a set of rules from its CSS text.
     */
    public static void parseRules(CSSOMStyleSheet ss, String rules)
        throws DOMException {
	try {
	    Parser parser = ss.getParser();
	    parser.setSelectorFactory(AbstractCSSRule.SELECTOR_FACTORY);
	    parser.setConditionFactory(AbstractCSSRule.CONDITION_FACTORY);
	    parser.setDocumentHandler(new CSSDocumentHandler(ss, true));
	    parser.parseStyleSheet(new InputSource(new StringReader(rules)));
	} catch (DOMException e) {
	    throw e;
	} catch (Exception e) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.SYNTAX_ERR,
		 "rules.syntax.error",
		 new Object[] { rules });
	}
    }

    /**
     * Parses one rules from its CSS text.
     */
    public static CSSRule parseRule(CSSOMStyleSheet ss, String rule)
        throws DOMException {
	try {
	    CSSDocumentHandler ssh;
	    Parser parser = ss.getParser();
	    parser.setSelectorFactory(AbstractCSSRule.SELECTOR_FACTORY);
	    parser.setConditionFactory(AbstractCSSRule.CONDITION_FACTORY);
	    parser.setDocumentHandler(ssh = new CSSDocumentHandler(ss, false));
	    parser.parseRule(new InputSource(new StringReader(rule)));
	    return ssh.currentRule;
	} catch (DOMException e) {
	    throw e;
	} catch (Exception e) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.SYNTAX_ERR,
		 "rule.syntax.error",
		 new Object[] { rule });
	}
    }

    /**
     * Creates a new handler.
     */
    protected CSSDocumentHandler(CSSOMStyleSheet ss, boolean append) {
	this(ss, null, append);
    }

    /**
     * Creates a new handler.
     */
    protected CSSDocumentHandler(CSSOMStyleSheet ss, CSSRule cr,
                                 boolean append) {
	parser = ss.getParser();
	factories = ss.getValueFactoryMap();
	styleSheet = ss;
	currentRule = cr;
	this.append = append;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#startDocument(InputSource)}.
     */
    public void startDocument(InputSource source)
        throws CSSException {
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#endDocument(InputSource)}.
     */
    public void endDocument(InputSource source) throws CSSException {
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#comment(String)}.
     */
    public void comment(String text) throws CSSException {
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#ignorableAtRule(String)}.
     */
    public void ignorableAtRule(String atRule) throws CSSException {
	currentRule = new CSSOMUnknownRule(styleSheet, currentRule, atRule,
                                           parser);
	if (append) {
	    styleSheet.appendRule(currentRule);
	}
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#namespaceDeclaration(String,String)}.
     */
    public void namespaceDeclaration(String prefix, String uri) 
	throws CSSException {
    }

    /**
     * <b>SAC</b>: Implements {@link
     * DocumentHandler#importStyle(String,SACMediaList,String)}.
     */
    public void importStyle(String       uri,
			    SACMediaList media, 
			    String       defaultNamespaceURI)
	throws CSSException {
	DOMMediaList l = new DOMMediaList();
	for (int i = 0; i < media.getLength(); i++) {
	    l.appendMedium(media.item(i));
	}
	currentRule = new CSSOMImportRule(styleSheet, uri, l);
	if (append) {
	    styleSheet.appendRule(currentRule);
	}
	currentRule = null;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#startMedia(SACMediaList)}.
     */
    public void startMedia(SACMediaList media) throws CSSException {
	DOMMediaList l = new DOMMediaList();
	for (int i = 0; i < media.getLength(); i++) {
	    l.appendMedium(media.item(i));
	}
	CSSRuleListOwner rlo = (CSSRuleListOwner)currentRule;
	currentRule = new CSSOMMediaRule(styleSheet, currentRule, l, parser,
                                         factories);
	if (append && rlo == null) {
	    styleSheet.appendRule(currentRule);
	} else if (append && rlo != null) {
	    rlo.appendRule(currentRule);
	}
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#endMedia(SACMediaList)}.
     */
    public void endMedia(SACMediaList media) throws CSSException {
	currentRule = currentRule.getParentRule();
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#startPage(String,String)}.
     */    
    public void startPage(String name, String pseudo_page)
        throws CSSException {
	throw new CSSException("!!! TODO");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#endPage(String,String)}.
     */
    public void endPage(String name, String pseudo_page) throws CSSException {
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#startFontFace()}.
     */
    public void startFontFace() throws CSSException {
	throw new CSSException("!!! TODO");
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#endFontFace()}.
     */
    public void endFontFace() throws CSSException {
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#startSelector(SelectorList)}.
     */
    public void startSelector(SelectorList selectors) throws CSSException {
	CSSRuleListOwner rlo = (CSSRuleListOwner)currentRule;
	currentRule = new CSSOMStyleRule(styleSheet, currentRule, parser,
                                         factories);
	if (append && rlo == null) {
	    styleSheet.appendRule(currentRule);
	} else if (append && rlo != null) {
	    rlo.appendRule(currentRule);
	}
	((CSSOMStyleRule)currentRule).setSelectors(selectors);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#endSelector(SelectorList)}.
     */
    public void endSelector(SelectorList selectors) throws CSSException {
	currentRule = currentRule.getParentRule();
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.DocumentHandler#property(String,LexicalUnit,boolean)}.
     */
    public void property(String name, LexicalUnit value, boolean important)
        throws CSSException {
	CSSOMStyleDeclaration sd;
	sd = (CSSOMStyleDeclaration)((CSSOMStyleRule)currentRule).getStyle();
	ValueFactory f = factories.get(name);
	f.createCSSValue(value, sd, (important) ? "!important" : "");
    }
}
