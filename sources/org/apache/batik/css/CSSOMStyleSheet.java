/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.apache.batik.css.event.CSSPropertyChangeEvent;
import org.apache.batik.css.event.CSSStyleDeclarationChangeEvent;
import org.apache.batik.css.event.CSSStyleRuleChangeEvent;
import org.apache.batik.css.event.CSSStyleRuleChangeListener;
import org.apache.batik.css.event.CSSStyleSheetChangeListener;
import org.apache.batik.css.event.CSSStyleSheetChangeSupport;
import org.apache.batik.css.event.SelectorListChangeEvent;
import org.apache.batik.css.value.ValueFactoryMap;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.MediaList;
import org.w3c.dom.stylesheets.StyleSheet;

/**
 * This class provides an implementation of the {@link
 * org.w3c.dom.css.CSSStyleSheet} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMStyleSheet
    extends    AbstractStyleSheet
    implements CSSStyleSheet,
	       CSSRuleListOwner,
               CSSStyleRuleChangeListener {
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
     * The owner rule.
     */
    protected CSSRule ownerRule;

    /**
     * The Style sheet change support.
     */
    protected CSSStyleSheetChangeSupport styleSheetChangeSupport;
    
    /**
     * Creates a new style sheet.
     * @param owner The owner node or null.
     * @param parent The parent StyleSheet or null.
     * @param href The StyleSheet URI or null.
     * @param title The title or null.
     * @param media The media list.
     * @param ownerRule The owner rule or null.
     * @param factories The values factories.
     * @param parser The CSS parser.
     */
    public CSSOMStyleSheet(Node            owner,
			   StyleSheet      parent,
			   String          href,
			   String          title,
			   MediaList       media,
			   CSSRule         ownerRule,
			   ValueFactoryMap factories,
			   Parser          parser) {
	super(owner, parent, href, title, media);
	this.ownerRule = ownerRule;
	this.factories = factories;
	this.parser = parser;
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
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSStyleSheet#getType()}.
     */
    public String getType() {
	return "text/css";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleSheet#getOwnerRule()}.
     */
    public CSSRule getOwnerRule() {
	return ownerRule;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleSheet#getCssRules()}.
     */
    public CSSRuleList getCssRules() {
	return cssRules;
    }

    /**
     * Appends a set of rule to the stylesheet.
     */
    public void appendRules(String rules) {
	CSSDocumentHandler.parseRules(this, rules);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleSheet#insertRule(String,int)}.
     */
    public int insertRule(String rule, int index) throws DOMException {
	CSSRule r = CSSDocumentHandler.parseRule(this, rule);
	if (r != null) {
	    cssRules.insert(r, index);
	    cssRuleAdded(r);
	}
	return index;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleSheet#deleteRule(int)}.
     */
    public void deleteRule(int index) throws DOMException {
	CSSRule r = cssRules.delete(index);
	switch (r.getType()) {
	case CSSRule.STYLE_RULE:
	    ((CSSOMStyleRule)r).removeCSSStyleRuleChangeListener(this);
	    break;
	}
	if (styleSheetChangeSupport != null) {
	    styleSheetChangeSupport.fireCSSRuleRemoved(r);
	}
    }

    /**
     * Appends a rule to the style sheet.
     */
    public void appendRule(CSSRule r) {
	cssRules.append(r);
	cssRuleAdded(r);
    }

    /**
     * Called when a CSSRule has been added to the style sheet.
     */
    protected void cssRuleAdded(CSSRule r) {
	switch (r.getType()) {
	case CSSRule.STYLE_RULE:
	    ((CSSOMStyleRule)r).addCSSStyleRuleChangeListener(this);
	    break;
	}
	if (styleSheetChangeSupport != null) {
	    styleSheetChangeSupport.fireCSSRuleAdded(r);
	}
    }

    // Events //////////////////////////////////////////////////////////////

    /**
     * Adds a CSSStyleSheetChangeListener to the listener list.
     * @param listener The CSSStyleSheetChangeListener to be added
     */
    public void addCSSStyleSheetChangeListener
	(CSSStyleSheetChangeListener listener) {
	if (styleSheetChangeSupport == null) {
	    styleSheetChangeSupport = new CSSStyleSheetChangeSupport(this);
	}
	styleSheetChangeSupport.addCSSStyleSheetChangeListener(listener);
    }
    
    /**
     * Removes a CSSStyleSheetChangeListener from the listener list.
     * @param listener The CSSStyleSheetChangeListener to be removed
     */
    public void removeCSSStyleSheetChangeListener
	(CSSStyleSheetChangeListener listener) {
	if (styleSheetChangeSupport == null) {
	    return;
	}
	styleSheetChangeSupport.removeCSSStyleSheetChangeListener(listener);
    }

    /**
     * Called before a CSS rule will be changed.
     */
    public void cssStyleRuleChangeStart(CSSStyleRuleChangeEvent evt) {
	if (styleSheetChangeSupport != null) {
	    styleSheetChangeSupport.fireCSSStyleRuleChangeStart(evt);
	}
    }

    /**
     * Called a CSS rule change has been cancelled.
     */
    public void cssStyleRuleChangeCancel(CSSStyleRuleChangeEvent evt) {
	if (styleSheetChangeSupport != null) {
	    styleSheetChangeSupport.fireCSSStyleRuleChangeCancel(evt);
	}
    }

    /**
     * Called after a CSS rule was changed.
     */
    public void cssStyleRuleChangeEnd(CSSStyleRuleChangeEvent evt) {
	if (styleSheetChangeSupport != null) {
	    styleSheetChangeSupport.fireCSSStyleRuleChangeEnd(evt);
	}
    }

    /**
     * Called when a selector list was changed.
     */
    public void selectorListChange(SelectorListChangeEvent evt) {
	if (styleSheetChangeSupport != null) {
	    styleSheetChangeSupport.fireSelectorListChange(evt);
	}
    }

    /**
     * Called before a CSS declaration will be changed.
     */
    public void cssStyleDeclarationChangeStart
        (CSSStyleDeclarationChangeEvent evt) {
	if (styleSheetChangeSupport != null) {
	    styleSheetChangeSupport.fireCSSStyleDeclarationChangeStart(evt);
	}
    }

    /**
     * Called a CSS declaration change has been cancelled.
     */
    public void cssStyleDeclarationChangeCancel
        (CSSStyleDeclarationChangeEvent evt) {
	if (styleSheetChangeSupport != null) {
	    styleSheetChangeSupport.fireCSSStyleDeclarationChangeCancel(evt);
	}
    }

    /**
     * Called after a CSS declaration was changed.
     */
    public void cssStyleDeclarationChangeEnd
        (CSSStyleDeclarationChangeEvent evt) {
	if (styleSheetChangeSupport != null) {
	    styleSheetChangeSupport.fireCSSStyleDeclarationChangeEnd(evt);
	}
    }

    /**
     * Called when a CSS property is changed.
     */
    public void cssPropertyChange(CSSPropertyChangeEvent evt) {
	if (styleSheetChangeSupport != null) {
	    styleSheetChangeSupport.fireCSSPropertyChange(evt);
	}
    }
}
