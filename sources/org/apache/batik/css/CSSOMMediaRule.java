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
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.MediaList;

/**
 * This class implements the {@link org.w3c.dom.css.CSSMediaRule} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMMediaRule
    extends AbstractCSSRule
    implements CSSMediaRule, CSSRuleListOwner {
    /**
     * The rule list.
     */
    protected CSSOMRuleList cssRules = new CSSOMRuleList();

    /**
     * The CSS parser.
     */
    protected Parser parser;

    /**
     * The value factory map.
     */
    protected ValueFactoryMap factories;

    /**
     * The media.
     */
    protected MediaList media;

    /**
     * The CSS document handler.
     */
    protected DocumentHandler ruleHandler = new MediaRuleHandler();

    /**
     * Creates a new media rule.
     */
    public CSSOMMediaRule(CSSStyleSheet   parent,
			  CSSRule         pr,
			  MediaList       media,
			  Parser          p,
			  ValueFactoryMap m) {
	super(parent, pr);
	this.media = media;
	parser = p;
	factories = m;
    }

    /**
     * Returns the parser used to read style sheets.
     */
    public Parser getParser() {
	return parser;
    }

    /**
     * Returns the map of value factories.
     */
    public ValueFactoryMap getValueFactoryMap() {
	return factories;
    }
    
    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSRule#getType()}.
     */
    public short getType() {
	return MEDIA_RULE;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSRule#getCssText()}.
     */
    public String getCssText() {
	String result = "@media " + media.getMediaText() + " {\n";
	for (int i = 0; i < cssRules.getLength(); i++) {
	    result += cssRules.item(i).getCssText() + "\n";
	}
	return result + "}";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSRule#setCssText(String)}.
     */
    public void setCssText(String cssText) throws DOMException {
	try {
	    parser.setSelectorFactory(SELECTOR_FACTORY);
	    parser.setConditionFactory(CONDITION_FACTORY);
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
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSMediaRule#getMedia()}.
     */
    public MediaList getMedia() {
	return media;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSMediaRule#getCssRules()}.
     */
    public CSSRuleList getCssRules() {
	return cssRules;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSMediaRule#insertRule(String,int)}.
     */
    public int insertRule(String rule, int index) throws DOMException {
	CSSOMStyleSheet ss = (CSSOMStyleSheet)getParentStyleSheet();
	CSSRule r = CSSDocumentHandler.parseRule(ss, rule);
	if (r != null) {
	    cssRules.insert(r, index);
	}
	return index;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSMediaRule#deleteRule(int)}.
     */
    public void deleteRule(int index) throws DOMException {
	cssRules.delete(index);
    }

    /**
     * Appends a rule.
     */
    public void appendRule(CSSRule r) {
	cssRules.append(r);
    }

    /**
     * To handle the parsing of a media rule.
     */
    protected class MediaRuleHandler extends DocumentHandlerAdapter {
	/**
	 * The current rule.
	 */
	protected CSSRule currentRule;

	/**
	 * <b>SAC</b>: Implements {@link
	 * org.w3c.css.sac.DocumentHandler#startMedia(SACMediaList)}.
	 */
	public void startMedia(SACMediaList media) throws CSSException {
	    DOMMediaList l = new DOMMediaList();
	    for (int i = 0; i < media.getLength(); i++) {
		l.appendMedium(media.item(i));
	    }
	    CSSOMMediaRule.this.media = l;
	    cssRules = new CSSOMRuleList();
	}

	/**
	 * <b>SAC</b>: Implements {@link
	 * org.w3c.css.sac.DocumentHandler#endMedia(SACMediaList)}.
	 */
	public void endMedia(SACMediaList media) throws CSSException {
	}

        /**
	 * <b>SAC</b>: Implements {@link
	 * org.w3c.css.sac.DocumentHandler#startSelector(SelectorList)}.
	 */
	public void startSelector(SelectorList selectors) throws CSSException {
	    currentRule = new CSSOMStyleRule(getParentStyleSheet(),
					     CSSOMMediaRule.this,
					     getParser(),
					     getValueFactoryMap());
	    appendRule(currentRule);
	    ((CSSOMStyleRule)currentRule).setSelectors(selectors);
	}

	/**
	 * <b>SAC</b>: Implements {@link
	 * org.w3c.css.sac.DocumentHandler#endSelector(SelectorList)}.
	 */
	public void endSelector(SelectorList selectors) throws CSSException {
	}

	/**
	 * <b>SAC</b>: Implements {@link
	 * DocumentHandler#property(String,LexicalUnit,boolean)}.
	 */
	public void property(String name, LexicalUnit value, boolean important)
	    throws CSSException {
	    CSSOMStyleDeclaration sd;
	    sd = (CSSOMStyleDeclaration)
                ((CSSOMStyleRule)currentRule).getStyle();
	    String imp = (important) ? "!important" : "";
	    ValueFactory f = getValueFactoryMap().get(name);
	    f.createCSSValue(value, sd, imp);
	}
    }
}
