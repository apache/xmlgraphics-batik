/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import java.io.StringReader;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSUnknownRule;

/**
 * This class provides an implementation of the {@link
 * org.w3c.dom.css.CSSUnknownRule} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMUnknownRule
    extends    AbstractCSSRule
    implements CSSUnknownRule {
    /**
     * The text of the rule.
     */
    protected String cssText;
    
    /**
     * The CSS parser.
     */
    protected Parser parser;

    /**
     * The CSS document handler.
     */
    protected DocumentHandler ruleHandler = new UnknownRuleHandler();

    /**
     * Creates a new CSSOMUnknownRule.
     */
    public CSSOMUnknownRule(CSSStyleSheet parent, CSSRule pr, String text,
                            Parser p) {
	super(parent, pr);
	cssText = text;
	parser  = p;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSRule#getType()}.
     */
    public short getType() {
	return UNKNOWN_RULE;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSRule#getCssText()}.
     */
    public String getCssText() {
	return cssText;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSRule#setCssText(String)}.
     */
    public void setCssText(String cssText) throws DOMException {
	try {
	    parser.setDocumentHandler(ruleHandler);
	    parser.parseRule(new InputSource(new StringReader(cssText)));
	} catch (DOMException e) {
	    throw e;
	} catch (Exception e) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.SYNTAX_ERR,
		 "rule.syntax.error",
		 new Object[] { cssText });
	}
    }    

    /**
     * To handle the parsing of an unknown rule.
     */
    protected class UnknownRuleHandler extends DocumentHandlerAdapter {
	/**
	 * <b>SAC</b>: Implements {@link
	 * org.w3c.css.sac.DocumentHandler#ignorableAtRule(String)}.
	 */
	public void ignorableAtRule(String atRule) throws CSSException {
	    cssText = atRule;
	}
    }
}
