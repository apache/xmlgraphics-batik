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
import org.w3c.dom.css.CSSValue;

/**
 * This class provides methods to manage CSSStyleDeclarationChangeEvent.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSStyleDeclarationChangeSupport {
    /**
     * The fired CSSStyleDeclarationChangeEvent event.
     */
    protected CSSStyleDeclarationChangeEvent event;

    /**
     * The event source.
     */
    protected Object source;

    /**
     * The listeners.
     */
    protected List listeners;

    /**
     * Creates a new CSSStyleDeclarationChangeSupport object.
     * @param source The source of the fired events.
     */
    public CSSStyleDeclarationChangeSupport(Object source) {
	this.source = source;
	event = new CSSStyleDeclarationChangeEvent(source);
    }

    /**
     * Adds a CSSStyleDeclarationChangeListener to the listener list.
     * @param listener The CSSStyleDeclarationChangeListener to be added
     */
    public void addCSSStyleDeclarationChangeListener
	(CSSStyleDeclarationChangeListener listener) {
        if (listeners == null) {
            listeners = new ArrayList(3);
        }
        listeners.add(listener);
    }
    
    /**
     * Removes a CSSStyleDeclarationChangeListener from the listener list.
     * @param listener The CSSStyleDeclarationChangeListener to be removed
     */
    public void removeCSSStyleDeclarationChangeListener
	(CSSStyleDeclarationChangeListener listener) {
        if (listeners == null) {
            return;
        }
	listeners.remove(listener);
    }

    /**
     * Reports the start of a CSSStyleDeclaration update to any registered
     * listeners.
     */
    public void fireCSSStyleDeclarationChangeStart() {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleDeclarationChangeListener)it.next())
		    .cssStyleDeclarationChangeStart(event);
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
     * Reports the annulation of a CSSStyleDeclaration update
     * to any registered listeners.
     */
    public void fireCSSStyleDeclarationChangeCancel() {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleDeclarationChangeListener)it.next())
		    .cssStyleDeclarationChangeCancel(event);
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
     * Reports the end of a CSSStyleDeclaration update to any registered
     * listeners.
     */
    public void fireCSSStyleDeclarationChangeEnd() {
	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSStyleDeclarationChangeListener)it.next())
		    .cssStyleDeclarationChangeEnd(event);
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
     * Reports a bound property update to any registered listeners.
     * No event is fired if old and new are equal.
     * @param property  The name of the property that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     */
    public void fireCSSPropertyChange(String   property, 
				      CSSValue oldValue,
				      CSSValue newValue) {

        if (oldValue != null && oldValue.equals(newValue)) {
            return;
        }

	List targets = null;
	if (listeners != null) {
	    targets = new ArrayList(listeners);
	    CSSPropertyChangeEvent evt = null;
	    evt= new CSSPropertyChangeEvent(source, property, oldValue,
                                            newValue);
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		((CSSPropertyChangeListener)it.next()).cssPropertyChange(evt);
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
}
