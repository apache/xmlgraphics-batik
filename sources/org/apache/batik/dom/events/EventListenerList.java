/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
