/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.w3c.dom.events.EventListener;

/**
 * Class to manager event listeners for one event type.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class EventListenerList {
    /**
     * Cache of listeners with any namespace URI.
     */
    private ArrayList<Entry> listeners = new ArrayList<Entry>();

    /**
     * Adds a listener.
     */
    public void addListener(String namespaceURI,
                            Object group,
                            EventListener listener) {
        for (Entry e : listeners) {
            if (e.listener == listener
                && (namespaceURI != null && namespaceURI.equals(e.namespaceURI)
                    || namespaceURI == null && e.namespaceURI == null)) {
                // Listener is already in the list, so do nothing.
                return;
            }
        }
        listeners.add(new Entry(listener, namespaceURI, group));
    }

    /**
     * Removes a listener.
     */
    public void removeListener(String namespaceURI,
                               EventListener listener) {
        for(Iterator<Entry> it = listeners.iterator(); it.hasNext(); ) {
            Entry e = it.next();
            if (listener == e.listener
                && (namespaceURI != null && namespaceURI.equals(e.namespaceURI)
                    || namespaceURI == null && e.namespaceURI == null) ) {
                it.remove();
                return;
            }
        }
    }

    /**
     * Returns an array containing all event listener entries.
     */
    public Collection<Entry> getEventListeners() {
        return listeners;
    }

    /**
     * Returns whether there is an event listener for the given namespace URI.
     */
    public boolean hasEventListener(String namespaceURI) {
        if (namespaceURI == null) {
            return !listeners.isEmpty();
        }
        for (Entry e : listeners) {
            if (namespaceURI.equals(e.namespaceURI)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of event listeners stored in this object.
     */
    public int size() {
        return listeners.size();
    }

    /**
     * EventListenerTable entry class.
     */
    public class Entry {
        
        /**
         * The event listener.
         */
        protected EventListener listener;

        /**
         * The namespace URI of the event the listener is listening for.
         */
        protected String namespaceURI;

        /**
         * The event group.
         */
        protected Object group;

        /**
         * Creates a new Entry object.
         */
        public Entry(EventListener listener,
                     String namespaceURI,
                     Object group) {
            this.listener = listener;
            this.namespaceURI = namespaceURI;
            this.group = group;
        }

        /**
         * Returns the event listener.
         */
        public EventListener getListener() {
            return listener;
        }

        /**
         * Returns the event group.
         */
        public Object getGroup() {
            return group;
        }

        /**
         * Returns the event namespace URI.
         */
        public String getNamespaceURI() {
            return namespaceURI;
        }
    }
}
