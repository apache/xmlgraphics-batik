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

/**
 * This class provides methods to manage CSSStyleRuleChangeEvent.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSStyleRuleChangeSupport
    extends CSSStyleDeclarationChangeSupport {
    /**
     * The fired CSSStyleRuleChangeEvent event.
     */
    protected CSSStyleRuleChangeEvent ruleEvent;

    /**
     * Creates a new CSSStyleRuleChangeSupport object.
     * @param source The source of the fired events.
     */
    public CSSStyleRuleChangeSupport(Object source) {
	super(source);
	ruleEvent = new CSSStyleRuleChangeEvent(source);
    }

    /**
     * Adds a CSSStyleRuleChangeListener to the listener list.
     * @param listener The CSSStyleRuleChangeListener to be added
     */
    public void addCSSStyleRuleChangeListener
        (CSSStyleRuleChangeListener listener) {
	addCSSStyleDeclarationChangeListener(listener);
    }
    
    /**
     * Removes a CSSStyleRuleChangeListener from the listener list.
     * @param listener The CSSStyleRuleChangeListener to be removed
     */
    public void removeCSSStyleRuleChangeListener
        (CSSStyleRuleChangeListener listener) {
	removeCSSStyleDeclarationChangeListener(listener);
    }

    /**
     * Reports the start of a CSSStyleRule update to any registered listeners.
     */
    public void fireCSSStyleRuleChangeStart() {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleRuleChangeListener)it.next())
		    .cssStyleRuleChangeStart(ruleEvent);
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
     * Reports the annulation of a CSSStyleRule update
     * to any registered listeners.
     */
    public void fireCSSStyleRuleChangeCancel() {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleRuleChangeListener)it.next()).
                    cssStyleRuleChangeCancel(ruleEvent);
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
     * Reports the end of a CSSStyleRule update to any registered listeners.
     */
    public void fireCSSStyleRuleChangeEnd() {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleRuleChangeListener)it.next()).
                    cssStyleRuleChangeEnd(ruleEvent);
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
     * Reports a selector list update to any registered listeners.
     * No event is fired if old and new are equal.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     */
    public void fireSelectorListChange(SelectorList oldValue,
				       SelectorList newValue) {

        if (oldValue != null && oldValue.equals(newValue)) {
            return;
        }

	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    SelectorListChangeEvent evt = null;
	    evt= new SelectorListChangeEvent(source, oldValue, newValue);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleRuleChangeListener)it.next()).
                    selectorListChange(evt);
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
