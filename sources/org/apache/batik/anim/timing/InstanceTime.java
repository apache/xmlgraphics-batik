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

/**
 * A class that represents an instance time created from a timing
 * specification.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class InstanceTime implements Comparable {

    /**
     * The time.
     */
    protected float time;

    /**
     * The {@link TimingSpecifier} that created this InstanceTime.
     */
    protected TimingSpecifier creator;

    /**
     * Whether this InstanceTime should be removed from an element's
     * begin or end instance time lists upon reset.
     */
    protected boolean clearOnReset;

    /**
     * The Interval on which this InstanceTime is dependent, or null
     * if the InstanceTime was not created from a syncbase value.
     */
    protected Interval timebase;

    /**
     * Creates a new InstanceTime.
     * @param creator the TimingSpecifier that created this InstanceTime
     * @param time the new time, in parent simple time
     * @param timebase the interval from which this InstanceTime was created
     * @param clearOnReset whether this InstanceTime should be removed from
     *                     an instance time list upon element reset
     */
    public InstanceTime(TimingSpecifier creator,
                        float time,
                        Interval timebase,
                        boolean clearOnReset) {
        Trace.enter(this, null, new Object[] { creator, new Float(time), timebase, new Boolean(clearOnReset) } ); try {
        this.creator = creator;
        // XXX Convert time from the creator's syncbase's
        //     time system into this time system.  Not
        //     strictly necessary in SVG.
        this.time = time;
        this.timebase = timebase;
        this.clearOnReset = clearOnReset;
        } finally { Trace.exit(); }
    }

    /**
     * Returns whether the InstanceTime should be removed from the
     * element's begin or end instance time list when it is reset.
     */
    public boolean getClearOnReset() {
        return clearOnReset;
    }

    /**
     * Returns the time of this instance time.
     */
    public float getTime() {
        return time;
    }

    /**
     * Called by the dependent Interval to indicate that its time
     * has changed.
     * @param newTime the new time, in parent simple time
     */
    void dependentUpdate(float newTime) {
        Trace.enter(this, "dependentUpdate", new Object[] { new Float(newTime) } ); try {
        // XXX Convert time from the creator's syncbase's
        //     time system into this time system.  Not
        //     strictly necessary in SVG.
        time = newTime;
        if (creator != null) {
            creator.handleTimebaseUpdate(this, time);
        }
        } finally { Trace.exit(); }
    }

    /**
     * Returns a string representation of this InstanceTime.
     */
    public String toString() {
        return Float.toString(time);
    }

    // Comparable ////////////////////////////////////////////////////////////

    /**
     * Compares this InstanceTime with another.
     */
    public int compareTo(Object o) {
        return Float.compare(time, ((InstanceTime) o).time);
    }
}
