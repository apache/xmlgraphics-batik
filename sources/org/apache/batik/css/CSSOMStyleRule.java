/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import java.io.StringReader;
import java.net.URL;

import org.apache.batik.css.value.ValueFactory;
import org.apache.batik.css.event.CSSStyleDeclarationChangeEvent;
import org.apache.batik.css.event.CSSStyleDeclarationChangeListener;
import org.apache.batik.css.event.CSSStyleRuleChangeListener;
import org.apache.batik.css.event.CSSStyleRuleChangeSupport;
import org.apache.batik.css.event.CSSPropertyChangeEvent;
import org.apache.batik.css.value.ValueFactoryMap;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

/**
 * This class implements the {@link org.w3c.dom.css.CSSStyleRule} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMStyleRule
    extends    AbstractCSSRule
    implements CSSStyleRule,
	       CSSStyleDeclarationChangeListener {
    /**
     * The selectors.
     */
    protected SelectorList selectors;

    /**
     * The declaration-block of this rule set.
     */
    protected CSSOMStyleDeclaration style;

    /**
     * The CSS parser.
     */
    protected Parser parser;
    
    /**
     * The value factory map.
     */
    protected ValueFactoryMap factories;

    /**
     * The CSS document handler.
     */
    protected DocumentHandler ruleHandler = new StyleRuleHandler();

    /**
     * The Style rule change support.
     */
    protected CSSStyleRuleChangeSupport styleRuleChangeSupport;

    /**
     * The base URI.
     */
    protected URL baseURI;
    
    /**
     * Creates a new rule set.
     */
    public CSSOMStyleRule(CSSStyleSheet ss,
			  CSSRule pr,
			  Parser p,
			  ValueFactoryMap m) {
	super(ss, pr);
	parser = p;
	factories = m;
	style = new CSSOMStyleDeclaration(this, p);
	style.setValueFactoryMap(m);
	style.addCSSStyleDeclarationChangeListener(this);
    }

    /**
     * Sets the base URI.
     */
    public void setBaseURI(URL url) {
        baseURI = url;
    }

    /**
     * Returns the base URI.
     */
    public URL getBaseURI() {
        return baseURI;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSRule#getType()}.
     * @return {@link org.w3c.dom.css.CSSRule#STYLE_RULE}.
     */
    public short getType() {
	return STYLE_RULE;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSRule#getCssText()}.
     */
    public String getCssText() {
	if (selectors == null) {
	    return "";
	}
	return getSelectorText() + " {\n" + style.getCssText() + "}";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSRule#setCssText(String)}.
     */
    public void setCssText(String cssText) throws DOMException {
	SelectorList s = selectors;
	CSSOMStyleDeclaration sd = style;
	try {
	    fireCSSStyleRuleChangeStart();
	    style = new CSSOMStyleDeclaration(this, parser);
	    style.addCSSStyleDeclarationChangeListener(this);
	    style.fireCSSStyleDeclarationChangeStart();

	    InputSource is = new InputSource(new StringReader(cssText));
	    parser.setSelectorFactory(SELECTOR_FACTORY);
	    parser.setConditionFactory(CONDITION_FACTORY);
	    parser.setDocumentHandler(ruleHandler);
	    parser.parseRule(is);
	} catch (DOMException e) {
	    style.fireCSSStyleDeclarationChangeCancel();
	    fireCSSStyleRuleChangeCancel();
	    selectors = s;
	    style = sd;
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "style.rule.value",
		 new Object[] { cssText + "\n" + e.getMessage() });
	} catch (Exception e) {
	    style.fireCSSStyleDeclarationChangeCancel();
	    fireCSSStyleRuleChangeCancel();
	    selectors = s;
	    style = sd;
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "style.rule.value",
		 new Object[] { cssText });
	} 
	style.fireCSSStyleDeclarationChangeEnd();
	sd.setCssText("");
	sd.removeCSSStyleDeclarationChangeListener(this);
	fireCSSStyleRuleChangeEnd();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleRule#getSelectorText()}.
     */
    public String getSelectorText() {
	if (selectors == null) {
	    return "";
	}
	String result = selectors.item(0).toString();
	for (int i = 1; i < selectors.getLength(); i++) {
	    result += ", " + selectors.item(i);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements
     * {@link org.w3c.dom.css.CSSStyleRule#setSelectorText(String)}.
     */
    public void setSelectorText(String selectorText) throws DOMException {
	try {
	    InputSource is = new InputSource(new StringReader(selectorText));
	    parser.setSelectorFactory(SELECTOR_FACTORY);
	    parser.setConditionFactory(CONDITION_FACTORY);
	    SelectorList sl = selectors;
	    selectors = parser.parseSelectors(is);
	    if (styleRuleChangeSupport != null) {
		styleRuleChangeSupport.fireSelectorListChange(sl, selectors);
	    }
	} catch (Exception e) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "selector.value",
		 new Object[] { selectorText });
	}
    }

    /**
     * Sets the selectors.
     */
    public void setSelectors(SelectorList s) {
	selectors = s;
    }

    /**
     * Returns the selectors.
     */
    public SelectorList getSelectors() {
	return selectors;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSStyleRule#getStyle()}.
     */
    public CSSStyleDeclaration getStyle() {
	return style;
    }

    // Events ////////////////////////////////////////////////////////////

    /**
     * Adds a CSSStyleRuleChangeListener to the listener list.
     * @param listener The CSSStyleRuleChangeListener to be added
     */
    public void addCSSStyleRuleChangeListener
	(CSSStyleRuleChangeListener listener) {
	if (styleRuleChangeSupport == null) {
	    styleRuleChangeSupport = new CSSStyleRuleChangeSupport(this);
	}
	styleRuleChangeSupport.addCSSStyleRuleChangeListener(listener);
    }
    
    /**
     * Removes a CSSStyleRuleChangeListener from the listener list.
     * @param listener The CSSStyleRuleChangeListener to be removed
     */
    public void removeCSSStyleRuleChangeListener
	(CSSStyleRuleChangeListener listener) {
	if (styleRuleChangeSupport == null) {
	    return;
	}
	styleRuleChangeSupport.removeCSSStyleRuleChangeListener(listener);
    }

    /**
     * Called before a CSS declaration will be changed.
     */
    public void cssStyleDeclarationChangeStart
        (CSSStyleDeclarationChangeEvent evt) {
	if (styleRuleChangeSupport != null) {
	    styleRuleChangeSupport.fireCSSStyleDeclarationChangeStart();
	}
    }

    /**
     * Called a CSS declaration change has been cancelled.
     */
    public void cssStyleDeclarationChangeCancel
        (CSSStyleDeclarationChangeEvent evt) {
	if (styleRuleChangeSupport != null) {
	    styleRuleChangeSupport.fireCSSStyleDeclarationChangeCancel();
	}
    }

    /**
     * Called after a CSS declaration was changed.
     */
    public void cssStyleDeclarationChangeEnd
        (CSSStyleDeclarationChangeEvent evt) {
	if (styleRuleChangeSupport != null) {
	    styleRuleChangeSupport.fireCSSStyleDeclarationChangeEnd();
	}
    }

    /**
     * Called when a CSS property is changed.
     */
    public void cssPropertyChange(CSSPropertyChangeEvent evt) {
	if (styleRuleChangeSupport != null) {
	    styleRuleChangeSupport.fireCSSPropertyChange(evt.getPropertyName(),
							 evt.getOldValue(),
							 evt.getNewValue());
	}
    }

    /**
     * Reports the start of a CSSStyleRule update to any registered listeners.
     */
    public void fireCSSStyleRuleChangeStart() {
	if (styleRuleChangeSupport == null) {
	    return;
	}
	styleRuleChangeSupport.fireCSSStyleRuleChangeStart();
    }

    /**
     * Reports the cancellation of a CSSStyleRule update to any
     * registered listeners.
     */
    public void fireCSSStyleRuleChangeCancel() {
	if (styleRuleChangeSupport == null) {
	    return;
	}
	styleRuleChangeSupport.fireCSSStyleRuleChangeCancel();
    }

    /**
     * Reports the end of a CSSStyleRule update to any registered listeners.
     */
    public void fireCSSStyleRuleChangeEnd() {
	if (styleRuleChangeSupport == null) {
	    return;
	}
	styleRuleChangeSupport.fireCSSStyleRuleChangeEnd();
    }

    /**
     * To handle the parsing of a style rule.
     */
    protected class StyleRuleHandler extends DocumentHandlerAdapter {
	/**
	 * Receives notification of the beginning of a rule statement.
	 */
	public void startSelector(SelectorList selectors) throws CSSException {
	    SelectorList sl = CSSOMStyleRule.this.selectors;
	    CSSOMStyleRule.this.selectors = selectors;
	    if (styleRuleChangeSupport != null) {
		styleRuleChangeSupport.fireSelectorListChange(sl, selectors);
	    }
	}

	/**
	 * Receives notification of the end of a rule statement.
	 */
	public void endSelector(SelectorList selectors) throws CSSException {
        }

        /**
	 * Receives notification of a declaration.
	 */
        public void property(String name, LexicalUnit value, boolean important)
	    throws CSSException {
	    String imp = (important) ? "!important" : "";
	    ValueFactory f = factories.get(name);
	    f.createCSSValue(value, style, imp);
	}
    }
}
