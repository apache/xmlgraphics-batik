/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.batik.css.sac.ExtendedSelector;
import org.apache.batik.css.value.ImmutableInherit;
import org.apache.batik.css.value.RelativeValueResolver;

import org.w3c.css.sac.SelectorList;
import org.w3c.dom.DOMException;
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
     * The cached computed styles.
     */
    protected ComputedStyleCache styles = new ComputedStyleCache();

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
	return getComputedStyleInternal(elt, pseudoElt);
    }
 
    /**
     * Internal version of getComputedStyle().
     */
    public CSSOMReadOnlyStyleDeclaration getComputedStyleInternal(Element elt,
                                                                  String pseudoElt) {
        pseudoElt = (pseudoElt == null) ? "" : pseudoElt;
        CSSOMReadOnlyStyleDeclaration result = styles.get(elt, pseudoElt);
	
        if (result == null) {
            result = computeStyle(elt, pseudoElt);
            styles.put(elt, pseudoElt, result);
        }
	return result;
    }
 
    /**
     * Sets the computed style in the cache in a way it is not collectable.
     */
    public void setComputedStyle(Element elt,
                                 String pseudoElt,
                                 CSSOMReadOnlyStyleDeclaration sd) {
        pseudoElt = (pseudoElt == null) ? "" : pseudoElt;
        sd.setContext(this, elt);
        styles.putPermanent(elt, pseudoElt, sd);
    }

    /**
     * Disposes the style declarations explicitly setted in the cache.
     */
    public void dispose() {
        styles.dispose();
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
    public CSSOMReadOnlyStyleDeclaration computeStyle(Element elt,
                                                      String pseudoElt) {
        CSSOMReadOnlyStyleDeclaration result;
        result = getCascadedStyle(elt, pseudoElt);

	computeRelativeValues(elt, pseudoElt, result);

	return result;
    }

    /**
     * Computes the cascaded style for the given element and pseudo element.
     */
    public CSSOMReadOnlyStyleDeclaration getCascadedStyle(Element elt,
                                                          String pseudoElt) {
        CSSOMReadOnlyStyleDeclaration result;
        result = new CSSOMReadOnlyStyleDeclaration(this, elt);

	addUserAgentProperties(elt, pseudoElt, result);
	addUserProperties(elt, pseudoElt, result);
	addNonCSSPresentationalHints(elt, pseudoElt, result);
	addAuthorStyleSheetProperties(elt, pseudoElt, result);
	addInlineStyleProperties(elt, pseudoElt, result);
	addOverrideStyleProperties(elt, pseudoElt, result);

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
	    val = (CSSOMReadOnlyValue)rd.getLocalPropertyCSSValue(prop);
            prio = rd.getLocalPropertyPriority(prop);
            orig = rd.getLocalPropertyOrigin(prop);

            if (val == null &&
                (!rvr.isInheritedProperty() ||
                 HiddenChildElementSupport.getParentElement(e) == null)) {
                val = rvr.getDefaultValue();
            } else if (val != null &&
                       (val.getImmutableValue() ==
                        ImmutableInherit.INSTANCE) &&
                       HiddenChildElementSupport.getParentElement(e) != null) {
                val = null;
            }
            rd.setPropertyCSSValue(prop, val, "",
                             CSSOMReadOnlyStyleDeclaration.AUTHOR_ORIGIN);
            if (val != null) {
                rvr.resolveValue(e, pe, this, rd, val, prio, orig);
            }
	}
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
        try {
            CSSOMRuleList authorRules = new CSSOMRuleList();
            StyleSheetList l = ((DocumentStyle)document).getStyleSheets();
            for (int i = 0; i < l.getLength(); i++) {
                CSSStyleSheet ss = (CSSStyleSheet)l.item(i);
                if (!ss.getDisabled() && mediaMatch(ss.getMedia())) {
                    addMatchingRules(ss.getCssRules(),
                                     e,
                                     pe,
                                     authorRules);
                }
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
	} catch (DOMException ex) {
            throw CSSDOMExceptionFactory.createDOMException
                (ex.code, "style.sheet",
                 new Object[] { ex.getMessage() });
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
        try {
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
	} catch (DOMException ex) {
            throw CSSDOMExceptionFactory.createDOMException
                (ex.code, "inline.style",
                 new Object[] { e.getTagName(), ex.getMessage() });
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

	CSSValue           dval   = dest.getLocalPropertyCSSValue(name);
        int                dorg   = dest.getLocalPropertyOrigin(name);
	String             dprio  = dest.getLocalPropertyPriority(name);

        if (dval == null ||
            dorg != CSSOMReadOnlyStyleDeclaration.USER_ORIGIN ||
            dprio.length() == 0) {
            dest.setPropertyCSSValue(name,
                                     value,
                                     prio,
                                     CSSOMReadOnlyStyleDeclaration.AUTHOR_ORIGIN);
        }
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

    /**
     * To cache the computed styles.
     */
    protected static class ComputedStyleCache {

        /**
         * The table used to store the style.
         */
        protected Entry[] table;

        /**
         * The number of entries
         */
        protected int count;

        /**
         * Creates a new ComputedStyleCache.
         */
        public ComputedStyleCache() {
            table = new Entry[11];
        }

        /**
         * Caches the given computed style.
         */
        public void put(Element elt, String pe, CSSOMReadOnlyStyleDeclaration sd) {
            int hash  = hashCode(elt, pe) & 0x7FFFFFFF;
            int index = hash % table.length;
	
            for (Entry e = table[index]; e != null; e = e.next) {
                if ((e.hash == hash) && e.match(elt, pe)) {
                    e.computedStyleReference = new SoftReference(sd);
                    return;
                }
            }

            // The key is not in the hash table
            int len = table.length;
            if (count++ >= (len * 3) >>> 2) {
                rehash();
                index = hash % table.length;
            }
            
            Entry e = new Entry(hash, elt, pe, new SoftReference(sd), table[index]);
            table[index] = e;
        }

        /**
         * Caches the given computed style without possibility of collection.
         */
        public void putPermanent(Element elt, String pe,
                                 CSSOMReadOnlyStyleDeclaration sd) {
            int hash  = hashCode(elt, pe) & 0x7FFFFFFF;
            int index = hash % table.length;
	
            for (Entry e = table[index]; e != null; e = e.next) {
                if ((e.hash == hash) && e.match(elt, pe)) {
                    e.computedStyleReference = new StrongReference(sd);
                    return;
                }
            }

            // The key is not in the hash table
            int len = table.length;
            if (count++ >= (len * 3) >>> 2) {
                rehash();
                index = hash % table.length;
            }
            
            Entry e = new Entry(hash, elt, pe, new StrongReference(sd), table[index]);
            table[index] = e;
        }

        /**
         * Returns the computed style mapped with the given element
         * and pseudo-element, if any.
         */
        public CSSOMReadOnlyStyleDeclaration get(Element elt, String pe) {
            int hash  = hashCode(elt, pe) & 0x7FFFFFFF;
            int index = hash % table.length;
	
            for (Entry e = table[index]; e != null; e = e.next) {
                if ((e.hash == hash) && e.match(elt, pe)) {
                    return (CSSOMReadOnlyStyleDeclaration)e.computedStyleReference.get();
                }
            }
            return null;
        }

        /**
         * Rehash the table
         */
        protected void rehash () {
            Entry[] oldTable = table;
	
            table = new Entry[oldTable.length * 2 + 1];
	
            for (int i = oldTable.length-1; i >= 0; i--) {
                for (Entry old = oldTable[i]; old != null;) {
                    Entry e = old;
                    old = old.next;
                    
                    int index = e.hash % table.length;
                    e.next = table[index];
                    table[index] = e;
                }
            }
        }

        /**
         * Updates the table.
         */
        protected void update() {
            for (int i = table.length - 1; i >= 0; --i) {
                Entry e = table[i];
                Entry p = null;
                if (e != null) {
                    if (e.computedStyleReference.get() == null) {
                        table[i] = e.next;
                        count--;
                    }
                    p = e;
                    e = e.next;
                }
                while (e != null) {
                    if (e.computedStyleReference.get() == null) {
                        p.next = e.next;
                        count--;
                    }
                    p = e;
                    e = e.next;
                }
            }
        }

        /**
         * Removes the permanently cached style declarations.
         */
        public void dispose() {
            for (int i = table.length - 1; i >= 0; --i) {
                Entry e = table[i];
                Entry p = null;
                if (e != null) {
                    if (e.computedStyleReference instanceof StrongReference) {
                        table[i] = e.next;
                        count--;
                    }
                    p = e;
                    e = e.next;
                }
                while (e != null) {
                    if (e.computedStyleReference instanceof StrongReference) {
                        p.next = e.next;
                        count--;
                    }
                    p = e;
                    e = e.next;
                }
            }
        }

        /**
         * Computes a hash code for the given element and pseudo-element.
         */
        protected int hashCode(Element e, String pe) {
            return e.hashCode() ^ pe.hashCode();
        }

        /**
         * To store computed style with a strong reference.
         */
        protected static class StrongReference extends SoftReference {
            
            /**
             * A strong reference.
             */
            protected Object reference;

            /**
             * Creates a new strong reference.
             */
            public StrongReference(Object o) {
                super(o);
                reference = o;
            }
        }

        /**
         * To manage collisions in the table.
         */
        protected static class Entry {
            
            /**
             * The hash code
             */
            public int hash;
	
            /**
             * The element.
             */
            public Element element;

            /**
             * The pseudo-element.
             */
            public String pseudoElement;

            /**
             * The computed style.
             */
            public SoftReference computedStyleReference;

            /**
             * The next entry.
             */
            public Entry next;

            /**
             * Creates a new entry.
             */
            public Entry(int h, Element e, String pe, SoftReference sd, Entry n) {
                hash = h;
                element = e;
                pseudoElement = pe;
                computedStyleReference = sd;
                next = n;
            }

            /**
             * Whether this entry match the given keys.
             */
            public boolean match(Element e, String pe) {
                if (e == element) {
                    if (pe.equals(pseudoElement)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
