/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSValue;

/**
 * This method provides methods to manage the events related to
 * to the CSSStyleSheet class.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class CSSStyleSheetChangeSupport {
    /**
     * The event source.
     */
    protected Object source;

    /**
     * The listeners.
     */
    protected List listeners;

    /**
     * Creates a new CSSStyleSheetChangeSupport object.
     * @param source The source of the fired events.
     */
    public CSSStyleSheetChangeSupport(Object source) {
	this.source = source;
    }

    /**
     * Adds a CSSStyleSheetChangeListener to the listener list.
     * @param listener The CSSStyleSheetChangeListener to be added
     */
    public void addCSSStyleSheetChangeListener
	(CSSStyleSheetChangeListener listener) {
        if (listeners == null) {
            listeners = new ArrayList(3);
        }
        listeners.add(listener);
    }
    
    /**
     * Removes a CSSStyleSheetChangeListener from the listener list.
     * @param listener The CSSStyleSheetChangeListener to be removed
     */
    public void removeCSSStyleSheetChangeListener
	(CSSStyleSheetChangeListener listener) {
        if (listeners == null) {
            return;
        }
	listeners.remove(listener);
    }

    /**
     * Reports a style sheet update to any registered listeners.
     * @param rule  The rule added.
     */
    public void fireCSSRuleAdded(CSSRule rule) {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    CSSRuleEvent evt = new CSSRuleEvent(source, rule);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleSheetChangeListener)it.next()).cssRuleAdded(evt);
	    }
	}
    }

    /**
     * Reports a style sheet update to any registered listeners.
     * @param rule  The rule removed.
     */
    public void fireCSSRuleRemoved(CSSRule rule) {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    CSSRuleEvent evt = evt= new CSSRuleEvent(source, rule);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleSheetChangeListener)it.next()).cssRuleRemoved(evt);
	    }
	}
    }

    /**
     * Fires an existing CSSStyleDeclarationChangeEvent that reports the
     * start of a CSSStyleDeclaration update to any registered listeners.
     */
    public void fireCSSStyleDeclarationChangeStart
        (CSSStyleDeclarationChangeEvent evt) {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleDeclarationChangeListener)it.next())
		    .cssStyleDeclarationChangeStart(evt);
	    }
        }
    }

    /**
     * Fires an existing CSSStyleDeclarationChangeEvent that reports the
     * annulation of a CSSStyleDeclaration update to any registered listeners.
     */
    public void fireCSSStyleDeclarationChangeCancel
        (CSSStyleDeclarationChangeEvent evt) {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleDeclarationChangeListener)it.next())
		    .cssStyleDeclarationChangeCancel(evt);
	    }
        }
    }

    /**
     * Fires an existing CSSStyleDeclarationChangeEvent that reports the
     * end of a CSSStyleDeclaration update to any registered listeners.
     */
    public void fireCSSStyleDeclarationChangeEnd
        (CSSStyleDeclarationChangeEvent evt) {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleDeclarationChangeListener)it.next())
		    .cssStyleDeclarationChangeEnd(evt);
	    }
        }
    }

    /**
     * Fires an existing CSSPropertyChangeEvent to any registered listeners.
     * No event is fired if the given event's old and new values are equal.
     * @param evt  The CSSPropertyChangeEvent object.
     */
    public void fireCSSPropertyChange(CSSPropertyChangeEvent evt) {
	CSSValue old = evt.getOldValue();
        if (old != null && old.equals(evt.getNewValue())) {
            return;
        }

	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSPropertyChangeListener)it.next()).cssPropertyChange(evt);
	    }
        }
    }
    
    /**
     * Fires an existing CSSStyleRuleChangeEvent that reports the
     * start of a CSSStyleRule update to any registered listeners.
     */
    public void fireCSSStyleRuleChangeStart(CSSStyleRuleChangeEvent evt) {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleRuleChangeListener)it.next()).
                    cssStyleRuleChangeStart(evt);
	    }
        }
    }

    /**
     * Fires an existing CSSStyleRuleChangeEvent that reports the
     * annulation of a CSSStyleRule update to any registered listeners.
     */
    public void fireCSSStyleRuleChangeCancel(CSSStyleRuleChangeEvent evt) {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleRuleChangeListener)it.next()).
                    cssStyleRuleChangeCancel(evt);
	    }
        }
    }

    /**
     * Fires an existing CSSStyleRuleChangeEvent that reports the
     * end of a CSSStyleRule update to any registered listeners.
     */
    public void fireCSSStyleRuleChangeEnd(CSSStyleRuleChangeEvent evt) {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleRuleChangeListener)it.next()).
                    cssStyleRuleChangeEnd(evt);
	    }
        }
    }

    /**
     * Fires an existing SelectorListChangeEvent to any registered listeners.
     * No event is fired if the given event's old and new values are equal.
     * @param evt  The SelectorListChangeEvent object.
     */
    public void fireSelectorListChange(SelectorListChangeEvent evt) {
	SelectorList old = evt.getOldValue();
        if (old != null && old.equals(evt.getNewValue())) {
            return;
        }

	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleRuleChangeListener)it.next()).
                    selectorListChange(evt);
	    }
        }
    }
}
