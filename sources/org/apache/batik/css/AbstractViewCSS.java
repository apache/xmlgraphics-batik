/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.batik.css.sac.ExtendedSelector;
import org.apache.batik.css.value.RelativeValueResolver;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.css.ElementCSSInlineStyle;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.stylesheets.DocumentStyle;
import org.w3c.dom.stylesheets.MediaList;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.views.DocumentView;

/**
 * This class provides an abstract implementation of the
 * {@link org.w3c.dom.css.ViewCSS} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractViewCSS implements ViewCSS {
    /**
     * The document of which this object is a view.
     */
    protected DocumentView document;

    /**
     * The computed styles.
     */
    protected Map styles = new HashMap(11);

    /**
     * The media to use for cascading.
     */
    protected MediaList media;

    /**
     * The user-agent style sheet.
     */
    protected CSSStyleSheet userAgentStyleSheet;

    /**
     * The user style sheet.
     */
    protected CSSStyleSheet userStyleSheet;

    /**
     * The relative value resolvers.
     */
    protected List relativeValueResolvers = new LinkedList();

    /**
     * creates a new ViewCSS object.
     * @param doc The document view associated with this abstract view.
     */
    protected AbstractViewCSS(DocumentView doc) {
	document = doc;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.views.AbstractView#getDocument()}.
     */
    public DocumentView getDocument() {
        return document;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.ViewCSS#getComputedStyle(Element,String)}.
     */
    public CSSStyleDeclaration getComputedStyle(Element elt,
                                                String pseudoElt) {
	Map m = (Map)styles.get(elt);
	if (m == null) {
	    styles.put(elt, m = new HashMap(11));
	}
        pseudoElt = (pseudoElt == null) ? "" : pseudoElt;
        CSSStyleDeclaration result = null;
	
        WeakReference ref = (WeakReference)m.get(pseudoElt);
        if (ref != null) {
            result = (CSSStyleDeclaration)ref.get();
        }
        if (result == null) {
            result = computeFullStyle(elt, pseudoElt);
            m.put(pseudoElt, new WeakReference(result));
        }
	return result;
    }
 
    /**
     * Sets the media to use to compute the styles.
     * @param mediaText The text representation of the media.
     */
    public void setMedia(String mediaText) {
	media = new DOMMediaList(mediaText);
    }

    /**
     * Sets the user-agent style sheet to use for cascading.
     */
    public void setUserAgentStyleSheet(CSSStyleSheet ss) {
	userAgentStyleSheet = ss;
    }

    /**
     * Sets the user style sheet to use for cascading.
     */
    public void setUserStyleSheet(CSSStyleSheet ss) {
	userStyleSheet = ss;
    }

    /**
     * Adds a resolver to the resolver list.
     */
    protected void addRelativeValueResolver(RelativeValueResolver rvr) {
	relativeValueResolvers.add(rvr);
    }

    /**
     * Computes the cascaded style for the given element and pseudo element.
     */
    protected CSSStyleDeclaration computeFullStyle(Element elt,
                                                   String pseudoElt) {
        CSSOMReadOnlyStyleDeclaration result;
        result = new CSSOMReadOnlyStyleDeclaration();

	addUserAgentProperties(elt, pseudoElt, result);
	addUserProperties(elt, pseudoElt, result);
	addNonCSSPresentationalHints(elt, pseudoElt, result);
	addAuthorStyleSheetProperties(elt, pseudoElt, result);
	addInlineStyleProperties(elt, pseudoElt, result);
	addOverrideStyleProperties(elt, pseudoElt, result);

	computeRelativeValues(elt, pseudoElt, result);

	return result;
    }

    /**
     * Computes the relative values in the given style declaration for the
     * given element and pseudo-element.
     * @param e The element to match.
     * @param pe The pseudo-element to match.
     * @param rd The result style declaration.
     */
    protected void computeRelativeValues(Element e,
					 String pe,
					 CSSOMReadOnlyStyleDeclaration rd) {
	CSSOMReadOnlyStyleDeclaration sd;
	CSSOMReadOnlyValue val;
	int orig;
	String prio;
	String prop;
	Iterator it = relativeValueResolvers.iterator();
	while (it.hasNext()) {
	    RelativeValueResolver rvr = (RelativeValueResolver)it.next();
	    prop = rvr.getPropertyName();
	    val = (CSSOMReadOnlyValue)rd.getPropertyCSSValue(prop);
	    prio = rd.getPropertyPriority(prop);
	    orig = rd.getPropertyOrigin(prop);
	    if ((rvr.isInheritedProperty() &&
		 (val == null || val.getCssValueType() == CSSValue.CSS_INHERIT))
		||
		(!rvr.isInheritedProperty() &&
		 (val != null && val.getCssValueType() ==
                  CSSValue.CSS_INHERIT))) {
		CSSValue result;
		Element elt = getParentElement(e);
		if (elt != null) {
		    sd = (CSSOMReadOnlyStyleDeclaration)getComputedStyle(elt,
                                                                         null);
		    rd.setPropertyCSSValue(prop,
					   sd.getPropertyCSSValue(prop),
					   sd.getPropertyPriority(prop),
					   sd.getPropertyOrigin(prop));
		    continue;
		}
	    }
	    if (val == null) {
		val = rvr.getDefaultValue();
		rd.setPropertyCSSValue(prop, val, prio, orig);
	    }
	    rvr.resolveValue(e, pe, this, rd, val, prio, orig);
	}
    }

    /**
     * Returns the parent element of the given one, or null.
     */
    protected Element getParentElement(Element e) {
	for (Node n = e.getParentNode(); n != null; n = n.getParentNode()) {
	    if (n.getNodeType() == Node.ELEMENT_NODE) {
		return (Element)n;
	    }
	}
	return null;
    }

    /**
     * Adds the user-agent style sheets properties matching the given element
     * and pseudo-element to the given style declaration.
     * @param e The element to match.
     * @param pe The pseudo-element to match.
     * @param rd The result style declaration.
     */
    protected void addUserAgentProperties(Element e,
					  String pe,
					  CSSOMReadOnlyStyleDeclaration rd) {
	CSSOMRuleList uaRules = new CSSOMRuleList();
	
	if (userAgentStyleSheet != null) {
	    addMatchingRules(userAgentStyleSheet.getCssRules(), e, pe,
                             uaRules);
	    uaRules = sortRules(uaRules, e, pe);
	    for (int i = 0; i < uaRules.getLength(); i++) {
		CSSStyleRule rule = (CSSStyleRule)uaRules.item(i);
		CSSStyleDeclaration decl = rule.getStyle();
		int len = decl.getLength();
		for (int j = 0; j < len; j++) {
		    setUserAgentProperty(decl.item(j), decl, rd);
		}
	    }
	}
    }

    /**
     * Sets a user-agent value to a computed style declaration.
     * @param name The property name.
     * @param decl The style declaration.
     * @param dest The result style declaration.
     */
    protected void setUserAgentProperty(String name,
					CSSStyleDeclaration decl,
					CSSOMReadOnlyStyleDeclaration dest) {
	CSSOMValue         val   = (CSSOMValue)decl.getPropertyCSSValue(name);
	String             prio  = decl.getPropertyPriority(name);
	CSSOMReadOnlyValue value = val.createReadOnlyCopy();
	dest.setPropertyCSSValue(name,
				 value,
				 prio,
			      CSSOMReadOnlyStyleDeclaration.USER_AGENT_ORIGIN);
    }
	
    /**
     * Adds the user style sheets properties matching the given element
     * and pseudo-element to the given style declaration.
     * @param e The element to match.
     * @param pe The pseudo-element to match.
     * @param rd The result style declaration.
     */
    protected void addUserProperties(Element e,
				     String pe,
				     CSSOMReadOnlyStyleDeclaration rd) {
	CSSOMRuleList uaRules = new CSSOMRuleList();
	
	if (userStyleSheet != null) {
	    addMatchingRules(userStyleSheet.getCssRules(), e, pe, uaRules);
	    uaRules = sortRules(uaRules, e, pe);
	    for (int i = 0; i < uaRules.getLength(); i++) {
		CSSStyleRule rule = (CSSStyleRule)uaRules.item(i);
		CSSStyleDeclaration decl = rule.getStyle();
		int len = decl.getLength();
		for (int j = 0; j < len; j++) {
		    setUserProperty(decl.item(j), decl, rd);
		}
	    }
	}
    }

    /**
     * Sets a user value to a computed style declaration.
     * @param name The property name.
     * @param decl The style declaration.
     * @param dest The result style declaration.
     */
    protected void setUserProperty(String name,
				   CSSStyleDeclaration decl,
				   CSSOMReadOnlyStyleDeclaration dest) {
	CSSOMValue         val   = (CSSOMValue)decl.getPropertyCSSValue(name);
	String             prio  = decl.getPropertyPriority(name);
	CSSOMReadOnlyValue value = val.createReadOnlyCopy();
	dest.setPropertyCSSValue(name,
				 value,
				 prio,
				 CSSOMReadOnlyStyleDeclaration.USER_ORIGIN);
    }
    
    /**
     * Adds the non-CSS presentational hints to the given style declaration.
     * @param e The element to match.
     * @param pe The pseudo-element to match.
     * @param rd The result style declaration.
     */
    protected void addNonCSSPresentationalHints
        (Element e, String pe, CSSOMReadOnlyStyleDeclaration rd) {
	if ((pe == null || pe.equals("")) &&
	    e instanceof ElementNonCSSPresentationalHints) {
	    ElementNonCSSPresentationalHints elt;
	    elt = (ElementNonCSSPresentationalHints)e;
	    CSSStyleDeclaration nonCSSDecl;
            nonCSSDecl = elt.getNonCSSPresentationalHints();
	    if (nonCSSDecl != null) {
		int len = nonCSSDecl.getLength();
		for (int i = 0; i < len; i++) {
		    setAuthorProperty(nonCSSDecl.item(i), nonCSSDecl, rd);
		}
	    }
	}
    }

    /**
     * Adds the author style sheets properties matching the given element
     * and pseudo-element to the given style declaration.
     * @param e The element to match.
     * @param pe The pseudo-element to match.
     * @param rd The result style declaration.
     */
    protected void addAuthorStyleSheetProperties
        (Element e, String pe, CSSOMReadOnlyStyleDeclaration rd) {
	CSSOMRuleList authorRules = new CSSOMRuleList();
	StyleSheetList l = ((DocumentStyle)document).getStyleSheets();
	for (int i = 0; i < l.getLength(); i++) {
	    addMatchingRules(((CSSStyleSheet)l.item(i)).getCssRules(),
			     e,
			     pe,
			     authorRules);
	}
	authorRules = sortRules(authorRules, e, pe);
	for (int i = 0; i < authorRules.getLength(); i++) {
	    CSSStyleRule rule = (CSSStyleRule)authorRules.item(i);
	    CSSStyleDeclaration decl = rule.getStyle();
	    int len = decl.getLength();
	    for (int j = 0; j < len; j++) {
		setAuthorProperty(decl.item(j), decl, rd);
	    }
	}
    }

    /**
     * Adds the inline style properties to the given style declaration.
     * @param e The element to match.
     * @param pe The pseudo-element to match.
     * @param rd The result style declaration.
     */
    protected void addInlineStyleProperties(Element e,
					    String pe,
					    CSSOMReadOnlyStyleDeclaration rd) {
	if (e instanceof ElementCSSInlineStyle) {
	    boolean hasStyle = true;
	    if (pe == null || pe.equals("") ||
		e instanceof ExtendedElementCSSInlineStyle) {
		hasStyle = ((ExtendedElementCSSInlineStyle)e).hasStyle();
	    }
	    if (hasStyle) {
		CSSStyleDeclaration inlineDecl;
                inlineDecl = ((ElementCSSInlineStyle)e).getStyle();
		int len = inlineDecl.getLength();
		for (int i = 0; i < len; i++) {
		    setAuthorProperty(inlineDecl.item(i), inlineDecl, rd);
		}
	    }
	}
    }

    /**
     * Adds the override style properties to the given style declaration.
     * @param e The element to match.
     * @param pe The pseudo-element to match.
     * @param rd The result style declaration.
     */
    protected void addOverrideStyleProperties
        (Element e, String pe, CSSOMReadOnlyStyleDeclaration rd) {
	CSSStyleDeclaration overrideDecl;
	overrideDecl = ((DocumentCSS)document).getOverrideStyle(e, pe);
	if ((pe == null || pe.equals("")) &&
	    overrideDecl != null) {
	    int len = overrideDecl.getLength();
	    for (int i = 0; i < len; i++) {
		setAuthorProperty(overrideDecl.item(i), overrideDecl, rd);
	    }
	}
    }

    /**
     * Sets a author value to a computed style declaration.
     * @param name The property name.
     * @param decl The style declaration.
     * @param dest The result style declaration.
     */
    protected void setAuthorProperty(String name,
				   CSSStyleDeclaration decl,
				   CSSOMReadOnlyStyleDeclaration dest) {
	CSSOMValue         val   = (CSSOMValue)decl.getPropertyCSSValue(name);
	String             prio  = decl.getPropertyPriority(name);
	CSSOMReadOnlyValue value = val.createReadOnlyCopy();
	dest.setPropertyCSSValue(name,
				 value,
				 prio,
				 CSSOMReadOnlyStyleDeclaration.AUTHOR_ORIGIN);
    }
    
    /**
     * Adds the style rules that match the given element and pseudo-element
     * to the given rule list.
     * @param l The input rule list.
     * @param e The element to match.
     * @param pe The pseudo-element to match.
     * @param rl The result rule list.
     */
    protected void addMatchingRules(CSSRuleList l,
				    Element e,
				    String pe,
				    CSSOMRuleList rl) {
	int llen = l.getLength();
	for (int i = 0; i < llen; i++) {
	    CSSRule rule = l.item(i);
	    switch (rule.getType()) {
	    case CSSRule.STYLE_RULE:
		CSSOMStyleRule sr = (CSSOMStyleRule)rule;
		SelectorList sl = sr.getSelectors();
		int slen = sl.getLength();
		for (int j = 0; j < slen; j++) {
		    ExtendedSelector s = (ExtendedSelector)sl.item(j);
		    if (s.match(e, pe)) {
			rl.append(rule);
		    }
		}
		break;
	    case CSSRule.IMPORT_RULE:
		CSSImportRule ir = (CSSImportRule)rule;
		CSSStyleSheet   is = ir.getStyleSheet();
		if (is != null) {
		    addMatchingRules(is.getCssRules(), e, pe, rl);
		}
		break;
	    case CSSRule.MEDIA_RULE:
		CSSMediaRule mr = (CSSMediaRule)rule;
		if (mediaMatch(mr.getMedia())) {
		    addMatchingRules(mr.getCssRules(), e, pe, rl);
		}
		break;
	    }
	}
    }

    /**
     * Sorts the rules in the given rule list by specificity.
     * @param l The rule list. The list is cleared by the methods.
     * @param e The element to match.
     * @param pe The pseudo-element to match.
     * @return The sorted list.
     */
    protected CSSOMRuleList sortRules(CSSOMRuleList l, Element e, String pe) {
	CSSOMRuleList result = new CSSOMRuleList();
	int llen;
	while ((llen = l.getLength()) > 0) {
	    int min = Integer.MAX_VALUE;
	    int imin = 0;
	    for (int i = 0; i < llen; i++) {
		CSSOMStyleRule rule = (CSSOMStyleRule)l.item(i);
		SelectorList sl = rule.getSelectors();
		int spec = 0;
		int slen;
		if ((slen = sl.getLength()) == 1) {
		    spec = ((ExtendedSelector)sl.item(0)).getSpecificity();
		} else {
		    for (int j = 0; j < slen; j++) {
			ExtendedSelector s = (ExtendedSelector)sl.item(j);
			if (s.match(e, pe)) {
			    spec = s.getSpecificity();
			    break;
			}
		    }
		}
		if (spec < min) {
		    min = spec;
		    imin = i;
		}
	    }
	    result.append(l.item(imin));
	    l.delete(imin);
	}
	return result;
    }

    /**
     * Whether the given media list matches the media list of this
     * ViewCSS object.
     */
    protected boolean mediaMatch(MediaList ml) {
	if (media == null || media.getLength() == 0 || ml.getLength() == 0) {
	    return true;
	}
	for (int i = 0; i < ml.getLength(); i++) {
	    for (int j = 0; j < media.getLength(); j++) {
		if (ml.item(i).equalsIgnoreCase(media.item(j))) {
		    return true;
		}
	    }
	}
	return false;
    }
}
