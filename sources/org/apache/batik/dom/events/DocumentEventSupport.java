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

import org.apache.batik.dom.util.HashTable;
import org.w3c.dom.DOMException;
import org.w3c.dom.events.Event;

/**
 * This class implements the behavior of DocumentEvent.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DocumentEventSupport {
    
    /**
     * The Event type.
     */
    public static final String EVENT_TYPE = "Events";
    
    /**
     * The MutationEvent type.
     */
    public static final String MUTATION_EVENT_TYPE = "MutationEvents";
    
    /**
     * The MouseEvent type.
     */
    public static final String MOUSE_EVENT_TYPE = "MouseEvents";

    /**
     * The UIEvent type.
     */
    public static final String UI_EVENT_TYPE = "UIEvents";

    /**
     * The KeyEvent type.
     */
    public static final String KEY_EVENT_TYPE = "KeyEvents";

    /**
     * The event factories table.
     */
    protected HashTable eventFactories = new HashTable();
    {
        eventFactories.put(EVENT_TYPE.toLowerCase(),
                           new SimpleEventFactory());
        eventFactories.put(MUTATION_EVENT_TYPE.toLowerCase(),
                           new MutationEventFactory());
        eventFactories.put(MOUSE_EVENT_TYPE.toLowerCase(),
                           new MouseEventFactory());
        eventFactories.put(KEY_EVENT_TYPE.toLowerCase(),
                           new KeyEventFactory());
        eventFactories.put(UI_EVENT_TYPE.toLowerCase(),
                           new UIEventFactory());
    }

    /**
     * Creates a new Event depending on the specified parameter.
     *
     * @param eventType The <code>eventType</code> parameter specifies the 
     *   type of <code>Event</code> interface to be created.  If the 
     *   <code>Event</code> interface specified is supported by the 
     *   implementation  this method will return a new <code>Event</code> of 
     *   the interface type requested.  If the  <code>Event</code> is to be 
     *   dispatched via the <code>dispatchEvent</code> method the  
     *   appropriate event init method must be called after creation in order 
     *   to initialize the <code>Event</code>'s values.  As an example, a 
     *   user wishing to synthesize some kind of  <code>UIEvent</code> would 
     *   call <code>createEvent</code> with the parameter "UIEvent".  The  
     *   <code>initUIEvent</code> method could then be called on the newly 
     *   created <code>UIEvent</code> to set the specific type of UIEvent to 
     *   be dispatched and set its context information.The 
     *   <code>createEvent</code> method is used in creating 
     *   <code>Event</code>s when it is either  inconvenient or unnecessary 
     *   for the user to create an <code>Event</code> themselves.  In cases 
     *   where the implementation provided <code>Event</code> is 
     *   insufficient, users may supply their own <code>Event</code> 
     *   implementations for use with the <code>dispatchEvent</code> method.
     *
     * @return The newly created <code>Event</code>
     *
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: Raised if the implementation does not support the 
     *   type of <code>Event</code> interface requested
     */
    public Event createEvent(String eventType)
	    throws DOMException {
        EventFactory ef = (EventFactory)eventFactories.get(eventType.toLowerCase());
        if (ef == null) {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR,
                                   "Bad event type: " + eventType);
        }
        return ef.createEvent();
    }

    /**
     * Registers a new EventFactory object.
     */
    public void registerEventFactory(String eventType,
                                            EventFactory factory) {
        eventFactories.put(eventType.toLowerCase(), factory);
    }


    /**
     * This interface represents an event factory.
     */
    public interface EventFactory {
        /**
         * Creates a new Event object.
         */
        Event createEvent();
    }

    /**
     * To create a simple event.
     */
    protected static class SimpleEventFactory implements EventFactory {
        /**
         * Creates a new Event object.
         */
        public Event createEvent() {
            return new DOMEvent();
        }
    }

    /**
     * To create a mutation event.
     */
    protected static class MutationEventFactory implements EventFactory {
        /**
         * Creates a new Event object.
         */
        public Event createEvent() {
            return new DOMMutationEvent();
        }
    }

    /**
     * To create a mouse event.
     */
    protected static class MouseEventFactory implements EventFactory {
        /**
         * Creates a new Event object.
         */
        public Event createEvent() {
            return new DOMMouseEvent();
        }
    }

    /**
     * To create a key event.
     */
    protected static class KeyEventFactory implements EventFactory {
        /**
         * Creates a new Event object.
         */
        public Event createEvent() {
            return new DOMKeyEvent();
        }
    }

    /**
     * To create a UI event.
     */
    protected static class UIEventFactory implements EventFactory {
        /**
         * Creates a new Event object.
         */
        public Event createEvent() {
            return new DOMUIEvent();
        }
    }
}
