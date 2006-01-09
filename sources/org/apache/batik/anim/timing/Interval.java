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

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A class that represents an interval for a timed element.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class Interval {

    /**
     * The begin time for the interval.
     */
    protected float begin;

    /**
     * The end time for the interval.
     */
    protected float end;

    /**
     * The list of {@link InstanceTime} objects that are dependent
     * on the begin time of this Interval.
     */
    protected LinkedList beginDependents = new LinkedList();

    /**
     * The list of {@link InstanceTime} objects that are dependent
     * on the end time of this Interval.
     */
    protected LinkedList endDependents = new LinkedList();

    /**
     * Creates a new Interval.
     */
    public Interval(float begin, float end) {
        this.begin = begin;
        this.end = end;
    }

    /**
     * Returns a string representation of this Interval.
     */
    public String toString() {
        return TimedElement.toString(begin) + ".." + TimedElement.toString(end);
    }

    /**
     * Returns the begin time of this interval.
     */
    public float getBegin() {
        return begin;
    }

    /**
     * Returns the end time of this interval.
     */
    public float getEnd() {
        return end;
    }

    /**
     * Adds a dependent InstanceTime for this Interval.
     */
    void addDependent(InstanceTime dependent, boolean forBegin) {
        if (forBegin) {
            beginDependents.add(dependent);
        } else {
            endDependents.add(dependent);
        }
    }

    /**
     * Removes a dependent InstanceTime for this Interval.
     */
    void removeDependent(InstanceTime dependent, boolean forBegin) {
        if (forBegin) {
            beginDependents.remove(dependent);
        } else {
            endDependents.remove(dependent);
        }
    }

    /**
     * Updates the begin time for this interval.
     */
    void setBegin(float begin) {
        this.begin = begin;
        Iterator i = beginDependents.iterator();
        while (i.hasNext()) {
            InstanceTime it = (InstanceTime) i.next();
            it.dependentUpdate(begin);
        }
    }

    /**
     * Updates the end time for this interval.
     */
    void setEnd(float end) {
        this.end = end;
        Iterator i = endDependents.iterator();
        while (i.hasNext()) {
            InstanceTime it = (InstanceTime) i.next();
            it.dependentUpdate(end);
        }
    }
}
