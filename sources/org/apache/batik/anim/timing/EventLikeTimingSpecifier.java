/*

   Copyright 2006  The Apache Software Foundation 

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
package org.apache.batik.anim.timing;

import org.w3c.dom.events.Event;
import org.w3c.dom.smil.TimeEvent;

/**
 * Abstract class from which all event-like timing specifier classes derive.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public abstract class EventLikeTimingSpecifier extends OffsetTimingSpecifier {

    /**
     * Creates a new EventLikeTimingSpecifier object.
     */
    public EventLikeTimingSpecifier(TimedElement owner, boolean isBegin,
                                    float offset) {
        super(owner, isBegin, offset);
    }

    /**
     * Returns whether this timing specifier is event-like (i.e., if it is
     * an eventbase, accesskey or a repeat timing specifier).
     */
    public boolean isEventCondition() {
        return true;
    }

    /**
     * Invoked to resolve an event-like timing specifier into an instance time.
     */
    public abstract void resolve(Event e);
}
