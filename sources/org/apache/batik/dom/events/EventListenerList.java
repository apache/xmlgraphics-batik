/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.events;

import org.w3c.dom.events.EventListener;

/**
 * A simple list of EventListener. Listeners are always added at the
 * head of the list.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 */
public class EventListenerList {

    private Entry first;
    private int n = 0;

    /**
     * Returns an array of the event listeners of this list, or null if any.
     */
    public EventListener [] getEventListeners() {
	if (first == null) {
	    return null;
	}
	EventListener [] listeners = new EventListener[n];
	Entry current = first;
	for (int i=0; i < n; ++i, current = current.next) {
	    listeners[i] = current.listener;
	}
	return listeners;
    }

    /**
     * Adds the specified event listener.
     * @param listener the event listener to add
     */
    public void add(EventListener listener) {
	first = new Entry(listener, first);
	n++;
    }

    /**
     * Removes the specified event listener.
     * @param listener the event listener to remove
     */
    public void remove(EventListener listener) {
	if (first == null) {
	    return;
	} else if (first.listener == listener) {
	    first = first.next;
	    --n;
	} else {
	    Entry prev = first;
	    Entry e = first.next;
	    while (e != null && e.listener != listener) {
		prev = e;
		e = e.next;
	    }
	    if (e != null) {
		prev.next = e.next;
		--n;
	    }
	}
    }

    /**
     * Returns true of the specified event listener has already been
     * added to this list, false otherwise.
     * @param listener the listener th check
     */
    public boolean contains(EventListener listener) {
	for (Entry e=first; e != null; e = e.next) {
	    if (listener == e.listener) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Returns the number of listeners in the list.
     */
    public int size() {
        return n;
    }

    // simple entry for the list
    private static class Entry {
	EventListener listener;
	Entry next;

	public Entry(EventListener listener, Entry next) {
	    this.listener = listener;
	    this.next = next;
	}
    }
}
