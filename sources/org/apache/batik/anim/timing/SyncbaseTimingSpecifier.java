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

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * A class to handle syncbase SMIL timing specifiers.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class SyncbaseTimingSpecifier extends OffsetTimingSpecifier {

    /**
     * The ID of the syncbase element.
     */
    protected String syncbaseID;

    /**
     * The syncbase element.
     */
    protected TimedElement syncbaseElement;

    /**
     * Whether this specifier specifies a sync to the begin or the end
     * of the syncbase element.
     */
    protected boolean syncBegin;

    /**
     * Map of {@link Interval}s to <!--a {@link WeakReference} to -->an
     * {@link InstanceTime}.
     */
    protected HashMap instances = new HashMap();

    /**
     * Creates a new SyncbaseTimingSpecifier object.
     */
    public SyncbaseTimingSpecifier(TimedElement owner, boolean isBegin,
                                   float offset, String syncbaseID,
                                   boolean syncBegin) {
        super(owner, isBegin, offset);
        this.syncbaseID = syncbaseID;
        this.syncBegin = syncBegin;
        this.syncbaseElement = owner.getTimedElementById(syncbaseID);
        syncbaseElement.addDependent(this, syncBegin);
    }

    /**
     * Returns a string representation of this timing specifier.
     */
    public String toString() {
        return syncbaseID + "." + (syncBegin ? "begin" : "end")
            + (offset != 0 ? super.toString() : "");
    }

    /**
     * Called by the timebase element when it creates a new Interval.
     */
    void newInterval(Interval interval) {
        InstanceTime instance =
            new InstanceTime(this, (syncBegin ? interval.getBegin()
                                              : interval.getEnd()) + offset,
                             interval, true);
        instances.put(interval, instance);
        interval.addDependent(instance, syncBegin);
        owner.addInstanceTime(instance, isBegin);
    }

    /**
     * Called by the timebase element when it deletes an Interval.
     */
    void removeInterval(Interval interval) {
        InstanceTime instance = (InstanceTime) instances.get(interval);
        interval.removeDependent(instance, syncBegin);
        owner.removeInstanceTime(instance, isBegin);
    }

    /**
     * Called by an {@link InstanceTime} created by this TimingSpecifier
     * to indicate that its value has changed.
     */
    void handleTimebaseUpdate(InstanceTime instanceTime, float newTime) {
        owner.instanceTimeChanged(instanceTime, isBegin);
    }
}
