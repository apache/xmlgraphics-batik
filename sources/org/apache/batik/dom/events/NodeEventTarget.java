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

import org.w3c.dom.events.EventTarget;

/**
 * A Node that uses an EventSupport for its event registration and
 * dispatch.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface NodeEventTarget extends EventTarget {

    /**
     * Returns the event support instance for this node, or null if any.
     */
    EventSupport getEventSupport();

    /**
     * Returns the parent node event target.
     */
    NodeEventTarget getParentNodeEventTarget();

    // Members inherited from DOM Level 3 Events org.w3c.dom.events.EventTarget
    // follow.

    /**
     *  This method allows the registration of an event listener in a
     * specified group or the default group and, depending on the
     * <code>useCapture</code> parameter, on the capture phase of the DOM
     * event flow or its target and bubbling phases.
     * @param namespaceURI  Specifies the <code>Event.namespaceURI</code>
     *   associated with the event for which the user is registering.
     * @param type  Specifies the <code>Event.type</code> associated with the
     *   event for which the user is registering.
     * @param listener  The <code>listener</code> parameter takes an object
     *   implemented by the user which implements the
     *   <code>EventListener</code> interface and contains the method to be
     *   called when the event occurs.
     * @param useCapture  If true, <code>useCapture</code> indicates that the
     *   user wishes to add the event listener for the capture phase only,
     *   i.e. this event listener will not be triggered during the target
     *   and bubbling phases. If <code>false</code>, the event listener will
     *   only be triggered during the target and bubbling phases.
     * @param evtGroup  The object that represents the event group to
     *   associate with the <code>EventListener</code> (see also ). Use
     *   <code>null</code> to attach the event listener to the default
     *   group.
     * @since DOM Level 3
     */
    void addEventListenerNS(String namespaceURI,
                                   String type,
                                   org.w3c.dom.events.EventListener listener,
                                   boolean useCapture,
                                   Object evtGroup);

    /**
     *  This method allows the removal of an event listener, independently of
     * the associated event group.
     * <br> Calling <code>removeEventListenerNS</code> with arguments which do
     * not identify any currently registered <code>EventListener</code> on
     * the <code>EventTarget</code> has no effect.
     * @param namespaceURI  Specifies the <code>Event.namespaceURI</code>
     *   associated with the event for which the user registered the event
     *   listener.
     * @param type  Specifies the <code>Event.type</code> associated with the
     *   event for which the user registered the event listener.
     * @param listener  The <code>EventListener</code> parameter indicates
     *   the <code>EventListener</code> to be removed.
     * @param useCapture  Specifies whether the <code>EventListener</code>
     *   being removed was registered for the capture phase or not. If a
     *   listener was registered twice, once for the capture phase and once
     *   for the target and bubbling phases, each must be removed
     *   separately. Removal of an event listener registered for the capture
     *   phase does not affect the same event listener registered for the
     *   target and bubbling phases, and vice versa.
     * @since DOM Level 3
     */
    void removeEventListenerNS(String namespaceURI,
                                      String type,
                                      org.w3c.dom.events.EventListener listener,
                                      boolean useCapture);

    /**
     *  This method allows the DOM application to know if an event listener,
     * attached to this <code>EventTarget</code> or one of its ancestors,
     * will be triggered by the specified event type during the dispatch of
     * the event to this event target or one of its descendants.
     * @param namespaceURI  Specifies the <code>Event.namespaceURI</code>
     *   associated with the event.
     * @param type  Specifies the <code>Event.type</code> associated with the
     *   event.
     * @return  <code>true</code> if an event listener will be triggered on
     *   the <code>EventTarget</code> with the specified event type,
     *   <code>false</code> otherwise.
     * @since DOM Level 3
     */
    boolean willTriggerNS(String namespaceURI,
                                 String type);

    /**
     *  This method allows the DOM application to know if this
     * <code>EventTarget</code> contains an event listener registered for
     * the specified event type. This is useful for determining at which
     * nodes within a hierarchy altered handling of specific event types has
     * been introduced, but should not be used to determine whether the
     * specified event type triggers an event listener (see
     * <code>EventTarget.willTriggerNS()</code>).
     * @param namespaceURI  Specifies the <code>Event.namespaceURI</code>
     *   associated with the event.
     * @param type  Specifies the <code>Event.type</code> associated with the
     *   event.
     * @return  <code>true</code> if an event listener is registered on this
     *   <code>EventTarget</code> for the specified event type,
     *   <code>false</code> otherwise.
     * @since DOM Level 3
     */
    boolean hasEventListenerNS(String namespaceURI,
                                      String type);
}
