/*

   Copyright 2005  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.events;

import org.apache.batik.dom.xbl.OriginalEvent;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

/**
 * An interface DOM 3 custom events should implement to be used with
 * Batik's DOM implementation.
 * XXX This description is out of date.
 * <p>
 *   This interface exists because of issues running under JDK &lt; 1.5.
 *   In these environments where only the DOM Level 2 interfaces are
 *   available, the {@link org.w3c.dom.events.CustomEvent} interface
 *   will inherit from the DOM Level 2 {@link org.w3c.dom.events.Event}
 *   interface, so it will miss out on a number of important methods which
 *   are needed for the event processing in {@link EventSupport}.
 * </p>
 * <p>
 *   If a custom event object passed in to {@link org.w3c.dom.Node#dispatchEvent}
 *   does not implement this interface, reflection will be used to access
 *   the needed methods.
 * </p>
 * <p>
 *   This interface also has two methods--{@link #resumePropagation} and
 *   {@link #setOriginalEvent}--which allow custom events to be handled
 *   properly.
 * </p>
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public interface CustomEvent
        extends org.w3c.dom.events.CustomEvent, OriginalEvent {

    /**
     * Clears the 'propagationStopped' and 'immediatePropagationStopped'
     * flags.  This is needed for standard event dispatching and should
     * only be called by the DOM Events implementation.
     */
    void resumePropagation();

    /**
     * Sets the target for this event.  This is needed for initialization
     * of the custom event object and should only be called by the DOM Events
     * implementation.
     */
    void setTarget(EventTarget target);

    /**
     * Clones this event object and sets the 'originalEvent' field of the
     * clone to be equal to the original event object.  This is needed
     * for sXBL event retargetting and should only be called by the
     * DOM Events implementation.
     */
    CustomEvent retarget(EventTarget target);

    /**
     * Returns the event from which this event was cloned.
     */
    Event getOriginalEvent();

    // Members inherited from DOM Level 3 Events org.w3c.dom.events.Event
    // interface follow.

    /**
     * Returns the namespace URI of this custom event.
     * @see org.w3c.dom.events.Event#getNamespaceURI
     */
    String getNamespaceURI();

    /**
     * Indicates whether this object implements the
     * {@link org.w3c.dom.events.CustomEvent} interface.
     * This must return true for classes implementing this interface.
     * @see org.w3c.dom.events.Event#isCustom
     */
    boolean isCustom();

    /**
     * Stops event listeners of the same group being triggered.
     * @see org.w3c.dom.events.Event#stopImmediatePropagation
     */
    void stopImmediatePropagation();

    /**
     * Returns whether {@link #stopImmediatePropagation} has been called.
     * @see org.w3c.dom.events.Event#isDefaultPrevented
     */
    boolean isDefaultPrevented();

    /**
     * Initializes this event object.
     * @see org.w3c.dom.events.Event#initEventNS
     */
    void initEventNS(String namespaceURIArg, 
                     String eventTypeArg, 
                     boolean canBubbleArg, 
                     boolean cancelableArg);
}
