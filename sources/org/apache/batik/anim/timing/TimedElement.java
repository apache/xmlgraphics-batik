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

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.batik.parser.ClockParser;
import org.apache.batik.parser.ClockHandler;
import org.apache.batik.parser.ParseException;

import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 * An abstract base class for elements that can have timing applied to them.
 * The concrete versions of this class do not necessarily have to be the
 * same as the DOM class, and in fact, this will mostly be impossible unless
 * creating new DOM classes that inherit from these elements.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public abstract class TimedElement {

    // Constants for fill mode.
    public static final int FILL_REMOVE = 0;
    public static final int FILL_FREEZE = 1;

    // Constants for restart mode.
    public static final int RESTART_ALWAYS          = 0;
    public static final int RESTART_WHEN_NOT_ACTIVE = 1;
    public static final int RESTART_NEVER           = 2;

    // Constants for time values.
    public static final float INDEFINITE = Float.POSITIVE_INFINITY;
    public static final float UNRESOLVED = Float.NaN;

    /**
     * The root time container.
     */
    protected TimedDocumentRoot root;

    /**
     * The parent time container.
     */
    protected TimeContainer parent;

    /**
     * Timing specifiers for the begin times of this element.
     */
    protected TimingSpecifier[] beginTimes;

    /**
     * Timing specifiers for the end times of this element.
     */
    protected TimingSpecifier[] endTimes;

    /**
     * Duration of this element, if {@code {@link #durMedia} = false}.
     * If unspecified, it will be {@link #UNRESOLVED}.
     */
    protected float simpleDur;

    /**
     * Whether the simple duration of this element should be equal
     * to the implicit duration.
     */
    protected boolean durMedia;

    /**
     * The number of repeats.  If unspecified, it will be
     * {@link #UNRESOLVED}.
     */
    protected float repeatCount;

    /**
     * The duration of repeats.  If unspecified, it will be
     * {@link #UNRESOLVED}.
     */
    protected float repeatDur;

    /**
     * The current repeat iteration.
     */
    protected int currentRepeatIteration;

    /**
     * The local active time of the last repeat.
     */
    protected float lastRepeatTime;

    /**
     * The fill mode for this element.  Uses the FILL_* constants
     * defined in this class.
     */
    protected int fillMode;

    /**
     * The restart mode for this element.  Uses the RESTART_* constants
     * defined in this class.
     */
    protected int restartMode;

    /**
     * The minimum active duration of this element.
     */
    protected float min;

    /**
     * Whether the min value was specified as 'media'.
     */
    protected boolean minMedia;

    /**
     * The maximum active duration of this element.
     */
    protected float max;

    /**
     * Whether the max value was specified as 'media'.
     */
    protected boolean maxMedia;

    /**
     * Whether the element is currently active.
     */
    protected boolean isActive;

    /**
     * Whether the element is currently frozen.
     */
    protected boolean isFrozen;

    /**
     * The current time of this element in local active time.
     */
    protected float lastSampleTime;

    /**
     * List of begin InstanceTimes.
     */
    protected Vector beginInstanceTimes = new Vector();

    /**
     * List of end InstanceTimes.
     */
    protected Vector endInstanceTimes = new Vector();

    /**
     * The current Interval.
     */
    protected Interval currentInterval;

    /**
     * The InstanceTime that defined the begin time of the current interval.
     */
    protected InstanceTime currentIntervalBeginInstance;

    /**
     * The end time of the previous interval, initially
     * {@link Float#NEGATIVE_INFINITY}.
     */
    protected float lastIntervalEnd;

    /**
     * List of previous intervals.
     */
    protected LinkedList previousIntervals = new LinkedList();

    /**
     * List of TimingSpecifiers on other elements that depend on this
     * element's begin times.
     */
    protected LinkedList beginDependents = new LinkedList();

    /**
     * List of TimingSpecifiers on other elements that depend on this
     * element's end times.
     */
    protected LinkedList endDependents = new LinkedList();

    /**
     * Whether the list of instance times should be checked to update
     * the current interval.
     */
    protected boolean shouldUpdateCurrentInterval = true;

    /**
     * Creates a new TimedElement.
     */
    public TimedElement() {
        beginTimes = new TimingSpecifier[0];
        endTimes = beginTimes;
        simpleDur = UNRESOLVED;
        repeatCount = UNRESOLVED;
        repeatDur = UNRESOLVED;
        lastRepeatTime = UNRESOLVED;
        max = INDEFINITE;
        lastSampleTime = UNRESOLVED;
        lastIntervalEnd = Float.NEGATIVE_INFINITY;
    }

    /**
     * Returns the root time container of this timed element.
     */
    public TimedDocumentRoot getRoot() {
        return root;
    }

    /**
     * Returns the current active time of this element.
     */
    public float getActiveTime() {
        return lastSampleTime;
    }

    /**
     * Returns the current simple time of this element.
     */
    public float getSimpleTime() {
        return lastSampleTime - lastRepeatTime;
    }

    /**
     * Called by a TimingSpecifier of this element when a new
     * InstanceTime is created.  This will be in response to an event
     * firing, a DOM method being called or a new Instance being
     * created by a syncbase element.
     */
    protected void addInstanceTime(InstanceTime time, boolean isBegin) {
        Vector instanceTimes = isBegin ? beginInstanceTimes : endInstanceTimes;
        int index = Collections.binarySearch(instanceTimes, time);
        if (index < 0) {
            index = -(index + 1);
        }
        instanceTimes.insertElementAt(time, index);
        shouldUpdateCurrentInterval = true;
    }

    /**
     * Called by a TimingSpecifier of this element when an InstanceTime
     * should be removed.  This will be in response to the pruning of an
     * Interval.
     */
    protected void removeInstanceTime(InstanceTime time, boolean isBegin) {
        Vector instanceTimes = isBegin ? beginInstanceTimes : endInstanceTimes;
        int index = Collections.binarySearch(instanceTimes, time);
        for (int i = index; index >= 0; index--) {
            InstanceTime it = (InstanceTime) instanceTimes.get(i);
            if (it == time) {
                instanceTimes.remove(i);
                return;
            }
            if (it.compareTo(time) != 0) {
                break;
            }
        }
        int len = instanceTimes.size();
        for (int i = index + 1; index < len; i++) {
            InstanceTime it = (InstanceTime) instanceTimes.get(i);
            if (it == time) {
                instanceTimes.remove(i);
                return;
            }
            if (it.compareTo(time) != 0) {
                break;
            }
        }
        shouldUpdateCurrentInterval = true;
        System.err.println("Instance time not found!"); // XXX
    }

    /**
     * Called by a TimingSpecifier of this element when an InstanceTime
     * has been updated.  This will be in response to a dependent
     * syncbase change.
     */
    protected void instanceTimeChanged(InstanceTime time, boolean isBegin) {
        shouldUpdateCurrentInterval = true;
    }

    /**
     * Adds a dependent TimingSpecifier for this element.
     */
    protected void addDependent(TimingSpecifier dependent, boolean forBegin) {
        if (forBegin) {
            beginDependents.add(dependent);
        } else {
            endDependents.add(dependent);
        }
    }

    /**
     * Removes a dependent TimingSpecifier for this element.
     */
    protected void removeDependent(TimingSpecifier dependent,
                                   boolean forBegin) {
        if (forBegin) {
            beginDependents.remove(dependent);
        } else {
            endDependents.remove(dependent);
        }
    }

    /**
     * Returns the simple duration time of this element.
     */
    public float getSimpleDur() {
        if (durMedia) {
            return getImplicitDur();
        } else if (isUnresolved(simpleDur)) {
            if (isUnresolved(repeatCount) && isUnresolved(repeatDur)
                    && endTimes.length > 0) {
                return INDEFINITE;
            }
            return getImplicitDur();
        } else {
            return simpleDur;
        }
    }

    /**
     * Returns whether the given time value is equal to the
     * {@link #UNRESOLVED} value.
     */
    public static boolean isUnresolved(float t) {
        return Float.isNaN(t);
    }

    /**
     * Returns the active duration time of this element.
     */
    public float getActiveDur(float B, float end) {
        float d = getSimpleDur();
        float PAD;
        if (!isUnresolved(end) && d == INDEFINITE) {
            PAD = minusTime(end, B);
        } else {
            float IAD;
            if (d == 0) {
                IAD = 0;
            } else {
                if (isUnresolved(repeatDur) && isUnresolved(repeatCount)) {
                    IAD = d;
                } else {
                    float p1 = isUnresolved(repeatCount)
                                    ? INDEFINITE
                                    : multiplyTime(d, repeatCount);
                    float p2 = isUnresolved(repeatDur)
                                    ? INDEFINITE
                                    : repeatDur;
                    IAD = minTime(minTime(p1, p2), INDEFINITE);
                }
            }
            if (isUnresolved(end) || end == INDEFINITE) {
                PAD = IAD;
            } else {
                PAD = minTime(IAD, minusTime(end, B));
            }
        }
        return minTime(max, maxTime(min, PAD));
    }

    /**
     * Subtracts one simple time from another.
     */
    protected float minusTime(float t1, float t2) {
        if (isUnresolved(t1) || isUnresolved(t2)) {
            return UNRESOLVED;
        }
        if (t1 == INDEFINITE || t2 == INDEFINITE) {
            return INDEFINITE;
        }
        return t1 - t2;
    }

    /**
     * Subtracts one simple time from another.
     */
    protected float multiplyTime(float t, float n) {
        if (isUnresolved(t) || t == INDEFINITE) {
            return t;
        }
        return t * n;
    }

    /**
     * Returns the minimum of two time values.
     */
    protected float minTime(float t1, float t2) {
        if (t1 == 0.0f || t2 == 0.0f) {
            return 0.0f;
        }
        if ((t1 == INDEFINITE || isUnresolved(t1))
                && t2 != INDEFINITE && !isUnresolved(t2)) {
            return t2;
        }
        if ((t2 == INDEFINITE || isUnresolved(t2))
                && t1 != INDEFINITE && !isUnresolved(t1)) {
            return t1;
        }
        if (t1 == INDEFINITE && isUnresolved(t2)
                || isUnresolved(t1) && t2 == INDEFINITE) {
            return INDEFINITE;
        }
        if (t1 < t2) {
            return t1;
        }
        return t2;
    }

    /**
     * Returns the maximum of two time values.
     */
    protected float maxTime(float t1, float t2) {
        if ((t1 == INDEFINITE || isUnresolved(t1))
                && t2 != INDEFINITE && !isUnresolved(t2)) {
            return t1;
        }
        if ((t2 == INDEFINITE || isUnresolved(t2))
                && t1 != INDEFINITE && !isUnresolved(t1)) {
            return t2;
        }
        if (t1 == INDEFINITE && isUnresolved(t2)
                || isUnresolved(t1) && t2 == INDEFINITE) {
            return UNRESOLVED;
        }
        if (t1 > t2) {
            return t1;
        }
        return t2;
    }

    /**
     * Returns the implicit duration of the element.  Currently, nested time
     * containers are not supported by SVG so this just returns 0 by default.
     * This should be overriden in derived classes that play media, since
     * they will have an implicit duration.
     */
    protected float getImplicitDur() {
        return 0.0f;
    }

    /**
     * Notifies dependents of a new interval.
     */
    protected void notifyNewInterval(Interval interval) {
        Iterator i = beginDependents.iterator();
        while (i.hasNext()) {
            TimingSpecifier ts = (TimingSpecifier) i.next();
            ts.newInterval(interval);
        }
        i = endDependents.iterator();
        while (i.hasNext()) {
            TimingSpecifier ts = (TimingSpecifier) i.next();
            ts.newInterval(interval);
        }
    }

    /**
     * Notifies dependents of a removed interval.
     */
    protected void notifyRemoveInterval(Interval interval) {
        Iterator i = beginDependents.iterator();
        while (i.hasNext()) {
            TimingSpecifier ts = (TimingSpecifier) i.next();
            ts.removeInterval(interval);
        }
        i = endDependents.iterator();
        while (i.hasNext()) {
            TimingSpecifier ts = (TimingSpecifier) i.next();
            ts.removeInterval(interval);
        }
    }

    /**
     * Calculates the local simple time.
     */
    protected void sampleAt(float parentSimpleTime) {
        float time = parentSimpleTime; // No time containers in SVG.
        if (currentInterval != null) {
            float begin = currentInterval.getBegin();
            if (lastSampleTime < begin && time >= begin) {
                if (!isActive) {
                    toActive(begin);
                }
                isActive = true;
                lastRepeatTime = begin;
                fireTimeEvent("beginEvent", currentInterval.getBegin(), 0);
            }
        }
        boolean wasActive = isActive;
        // For each sample, we might need to update the current interval's
        // begin and end times, or end the current interval and compute
        // a new one.
        boolean hasEnded = currentInterval != null && time >= currentInterval.getEnd();
        // Fire any repeat events that should have been fired since the
        // last sample.
        if (currentInterval != null) {
            float d = getSimpleDur();
            while (time - lastRepeatTime >= d
                    && lastRepeatTime + d < currentInterval.getEnd()) {
                lastRepeatTime += d;
                currentRepeatIteration++;
                fireTimeEvent(root.getRepeatEventName(), lastRepeatTime,
                              currentRepeatIteration);
            }
        }
        while (shouldUpdateCurrentInterval || hasEnded) {
            if (hasEnded) {
                previousIntervals.add(currentInterval);
                isActive = false;
                isFrozen = false;
                fireTimeEvent("endEvent", currentInterval.getEnd(), 0);
            }
            boolean first =
                currentInterval == null && previousIntervals.isEmpty();
            if (currentInterval == null || hasEnded) {
                if (first || restartMode != RESTART_NEVER) {
                    float beginAfter;
                    if (first) {
                        beginAfter = Float.NEGATIVE_INFINITY;
                    } else {
                        beginAfter = ((Interval) previousIntervals.getLast()).getEnd();
                    }
                    currentInterval = computeInterval(first, false, beginAfter);
                    if (currentInterval != null) {
                        notifyNewInterval(currentInterval);
                        float beginEventTime = currentInterval.getBegin();
                        if (time >= beginEventTime) {
                            lastRepeatTime = beginEventTime;
                            if (beginEventTime < 0) {
                                beginEventTime = 0;
                            }
                            isActive = true;
                            isFrozen = false;
                            fireTimeEvent("beginEvent", beginEventTime, 0);
                            float d = getSimpleDur();
                            while (time - lastRepeatTime >= d) {
                                lastRepeatTime += d;
                                currentRepeatIteration++;
                                fireTimeEvent(root.getRepeatEventName(), lastRepeatTime,
                                              currentRepeatIteration);
                            }
                        }
                    }
                }
            } else {
                float currentBegin = currentInterval.getBegin();
                if (currentBegin > time) {
                    // Interval hasn't started yet.
                    float beginAfter;
                    if (previousIntervals.isEmpty()) {
                        beginAfter = Float.NEGATIVE_INFINITY;
                    } else {
                        beginAfter = ((Interval) previousIntervals.getLast()).getEnd();
                    }
                    Interval interval = computeInterval(false, false, beginAfter);
                    if (interval == null) {
                        notifyRemoveInterval(currentInterval);
                        currentInterval = null;
                    } else {
                        float newBegin = interval.getBegin();
                        float newEnd = interval.getEnd();
                        if (currentBegin != newBegin) {
                            currentInterval.setBegin(newBegin);
                        }
                        if (currentInterval.getEnd() != newEnd) {
                            currentInterval.setEnd(newEnd);
                        }
                    }
                } else {
                    // Interval has already started.
                    Interval interval = computeInterval(false, true, currentBegin);
                    float newEnd = interval.getEnd();
                    if (currentInterval.getEnd() != newEnd) {
                        currentInterval.setEnd(newEnd);
                    }
                }
            }
            shouldUpdateCurrentInterval = false;
            hasEnded = currentInterval != null && time >= currentInterval.getEnd();
        }

        if (!wasActive && isActive) {
            isFrozen = false;
            toActive(currentInterval.getBegin());
        } else if (wasActive && !isActive) {
            isFrozen = fillMode == FILL_FREEZE;
            toInactive(isFrozen);
        }

        float d = getSimpleDur();
        if (isActive) {
            sampledAt(time - lastRepeatTime, d, currentRepeatIteration);
        } else if (isFrozen) {
            Interval previousInterval = (Interval) previousIntervals.getLast();
            float end = previousInterval.getEnd();
            if ((end - lastRepeatTime) % d == 0) {
                sampledLastValue(currentRepeatIteration);
            } else {
                sampledAt(end, d, currentRepeatIteration);
            }
        }

        lastSampleTime = time;
    }

    /**
     * Computes an interval from the begin and end instance time lists.
     * @param first indicates whether this is the first interval to compute
     * @param fixedBegin if true, specifies that the value given for
     *                   {@code beginAfter} is taken to be the actual begin
     *                   time for the interval; only the end value is computed.
     * @param beginAfter the earliest possible begin time for the computed
     *                   interval.
     */
    protected Interval computeInterval(boolean first, boolean fixedBegin,
                                       float beginAfter) {
        Iterator beginIterator = beginInstanceTimes.iterator();
        Iterator endIterator = endInstanceTimes.iterator();
        float parentSimpleDur = parent.getSimpleDur();
        InstanceTime endInstanceTime = endIterator.hasNext()
            ? (InstanceTime) endIterator.next()
            : null;
        boolean firstEnd = true;
        InstanceTime beginInstanceTime = null;
        for (;;) {
            float tempBegin;
            if (fixedBegin) {
                tempBegin = beginAfter;
            } else {
                for (;;) {
                    if (!beginIterator.hasNext()) {
                        // ran out of begin values
                        return null;
                    }
                    beginInstanceTime = (InstanceTime) beginIterator.next();
                    tempBegin = beginInstanceTime.getTime();
                    if (tempBegin >= beginAfter) {
                        break;
                    }
                }
            }
            if (tempBegin >= parentSimpleDur) {
                // the begin value is after the parent has ended
                return null;
            }
            float tempEnd;
            if (endTimes.length == 0) {
                // no 'end' attribute specified
                tempEnd = tempBegin + getActiveDur(tempBegin, INDEFINITE);
            } else {
                if (endInstanceTimes.isEmpty()) {
                    tempEnd = UNRESOLVED;
                } else {
                    tempEnd = endInstanceTime.getTime();
                    if (first && !firstEnd && tempEnd == tempBegin
                            || !first && tempEnd == currentInterval.getEnd()) {
                        for (;;) {
                            if (!endIterator.hasNext()) {
                                return null;
                            }
                            endInstanceTime = (InstanceTime) endIterator.next();
                            tempEnd = endInstanceTime.getTime();
                            if (tempEnd > tempBegin) {
                                break;
                            }
                        }
                    }
                    firstEnd = false;
                    for (;;) {
                        if (tempEnd >= tempBegin) {
                            break;
                        }
                        if (!endIterator.hasNext()) {
                            return null;
                        }
                        endInstanceTime = (InstanceTime) endIterator.next();
                        tempEnd = endInstanceTime.getTime();
                    }
                }
                float ad = getActiveDur(tempBegin, tempEnd);
                if (tempBegin + ad < tempEnd) {
                    tempEnd = tempBegin + ad;
                }
            }
            if (!first || tempEnd > 0) {
                if (restartMode == RESTART_ALWAYS && beginIterator.hasNext()) {
                    InstanceTime nextBeginInstance =
                        (InstanceTime) beginIterator.next();
                    float nextBegin = nextBeginInstance.getTime();
                    if (nextBegin < tempEnd) {
                        tempEnd = nextBegin;
                    }
                }
                currentIntervalBeginInstance = beginInstanceTime;
                return new Interval(tempBegin, tempEnd);
            }
            if (fixedBegin) {
                return null;
            }
            beginAfter = tempEnd;
        }
    }

    /**
     * Resets this element.
     */
    protected void reset(boolean clearCurrentBegin) {
        Iterator i = beginInstanceTimes.iterator(); 
        while (i.hasNext()) {
            InstanceTime it = (InstanceTime) i.next();
            if (it.getClearOnReset() &&
                    (clearCurrentBegin
                        || currentInterval == null
                        || currentIntervalBeginInstance != it)) {
                i.remove();
            }
        }
        i = endInstanceTimes.iterator();
        while (i.hasNext()) {
            InstanceTime it = (InstanceTime) i.next();
            if (it.getClearOnReset()) {
                i.remove();
            }
        }
        if (isFrozen) {
            removeFill();
        }
        currentRepeatIteration = 0;
        lastRepeatTime = UNRESOLVED;
        isActive = false;
        isFrozen = false;
        lastSampleTime = UNRESOLVED;
        // XXX should reconvert resolved syncbase/wallclock/media-marker time
        //     instances into the parent simple timespace
    }

    /**
     * Parses the animation attributes for this timed element.
     */
    public void parseAttributes(String begin, String dur, String end,
                                String min, String max, String repeatCount,
                                String repeatDur, String fill,
                                String restart) throws ParseException {
        parseBegin(begin);
        parseDur(dur);
        parseEnd(end);
        parseMin(min);
        parseMax(max);
        parseRepeatCount(repeatCount);
        parseRepeatDur(repeatDur);
        parseFill(fill);
        parseRestart(restart);
    }

    /**
     * Parses a new 'begin' attribute.
     */
    public void parseBegin(String begin) throws ParseException {
        beginTimes = TimingSpecifierListProducer.parseTimingSpecifierList
            (TimedElement.this, true, begin,
             root.useSVG11AccessKeys, root.useSVG12AccessKeys);
    }

    /**
     * Parses a new 'dur' attribute.
     */
    public void parseDur(String dur) throws ParseException {
        if (dur.equals("media")) {
            durMedia = true;
            simpleDur = UNRESOLVED;
        } else {
            durMedia = false;
            if (dur.length() == 0 || dur.equals("indefinite")) {
                simpleDur = INDEFINITE;
            } else {
                simpleDur = parseClockValue(dur);
                if (simpleDur < 0) {
                    simpleDur = INDEFINITE;
                }
            }
        }
    }

    /**
     * Parses a clock value and returns it as a float.
     */
    protected float parseClockValue(String s) throws ParseException {
        ClockParser p = new ClockParser();
        class Handler implements ClockHandler {
            protected float v = 0;
            public void clockValue(float newClockValue) {
                v = newClockValue;
            }
        };
        Handler h = new Handler();
        p.setClockHandler(h);
        p.parse(s);
        return h.v;
    }

    /**
     * Parses a new 'end' attribute.
     */
    public void parseEnd(String end) throws ParseException {
        endTimes = TimingSpecifierListProducer.parseTimingSpecifierList
            (TimedElement.this, false, end,
             root.useSVG11AccessKeys, root.useSVG12AccessKeys);
    }

    /**
     * Parses a new 'min' attribute.
     */
    public void parseMin(String min) throws ParseException {
        if (min.equals("media")) {
            minMedia = true;
        } else {
            minMedia = false;
            if (min.length() == 0) {
                this.min = 0;
            } else {
                this.min = parseClockValue(min);
                if (this.min < 0) {
                    this.min = 0;
                }
            }
        }
    }

    /**
     * Parses a new 'max' attribute.
     */
    public void parseMax(String max) throws ParseException {
        if (max.equals("media")) {
            maxMedia = true;
        } else {
            maxMedia = false;
            if (max.length() == 0 || max.equals("indefinite")) {
                this.max = INDEFINITE;
            } else {
                this.max = parseClockValue(max);
                if (this.max < 0) {
                    this.max = 0;
                }
            }
        }
    }

    /**
     * Parses a new 'repeatCount' attribute.
     */
    public void parseRepeatCount(String repeatCount) throws ParseException {
        if (repeatCount.length() == 0) {
            this.repeatCount = UNRESOLVED;
        } else if (repeatCount.equals("indefinite")) {
            this.repeatCount = INDEFINITE;
        } else {
            try {
                this.repeatCount = Float.parseFloat(repeatCount);
                if (this.repeatCount > 0) {
                    return;
                }
            } catch (NumberFormatException ex) {
            }
            // XXX
            throw new RuntimeException
                ("Invalid value for 'repeatCount': \"" + repeatCount + "\"");
        }
    }

    /**
     * Parses a new 'repeatDur' attribute.
     */
    public void parseRepeatDur(String repeatDur) throws ParseException {
        if (repeatDur.length() == 0) {
            this.repeatDur = UNRESOLVED;
        } else if (repeatDur.equals("indefinite")) {
            this.repeatDur = INDEFINITE;
        } else {
            this.repeatDur = parseClockValue(repeatDur);
        }
    }

    /**
     * Parses a new 'fill' attribute.
     */
    public void parseFill(String fill) throws ParseException {
        if (fill.length() == 0 || fill.equals("remove")) {
            fillMode = FILL_REMOVE;
        } else if (fill.equals("freeze")) {
            fillMode = FILL_FREEZE;
        } else {
            // XXX
            throw new RuntimeException
                ("Invalid value for 'fill': \"" + fill + "\"");
        }
    }

    /**
     * Parses a new 'restart' attribute.
     */
    public void parseRestart(String restart) throws ParseException {
        if (restart.length() == 0 || restart.equals("always")) {
            restartMode = RESTART_ALWAYS;
        } else if (restart.equals("whenNotActive")) {
            restartMode = RESTART_WHEN_NOT_ACTIVE;
        } else if (restart.equals("never")) {
            restartMode = RESTART_NEVER;
        } else {
            // XXX
            throw new RuntimeException
                ("Invalid value for 'restart': \"" + restart + "\"");
        }
    }

    /**
     * Initializes this timed element.
     */
    public void initialize() {
        for (int i = 0; i < beginTimes.length; i++) {
            beginTimes[i].initialize();
        }
        for (int i = 0; i < endTimes.length; i++) {
            beginTimes[i].initialize();
        }
    }

    /**
     * Deinitializes this timed element.
     */
    public void deinitialize() {
        for (int i = 0; i < beginTimes.length; i++) {
            beginTimes[i].deinitialize();
        }
        for (int i = 0; i < endTimes.length; i++) {
            beginTimes[i].deinitialize();
        }
    }

    /**
     * Fires a TimeEvent of the given type on this element.
     * @param eventType the type of TimeEvent ("beginEvent", "endEvent"
     *                  or "repeatEvent").
     * @param time the timestamp of the event object
     * @param detail the repeat iteration, if this event is a repeat event
     */
    protected void fireTimeEvent(String eventType, float time, int detail) {
        Calendar t = (Calendar) root.getDocumentBeginTime().clone();
        t.add(Calendar.MILLISECOND, (int) Math.round(time * 1e3));
        fireTimeEvent(eventType, t, detail);
    }

    /**
     * Fires a TimeEvent of the given type on this element.
     * @param eventType the type of TimeEvent ("beginEvent", "endEvent"
     *                  or "repeatEvent").
     * @param time the timestamp of the event object
     */
    protected abstract void fireTimeEvent(String eventType, Calendar time,
                                          int detail);

    /**
     * Invoked to indicate this timed element became active at the
     * specified time.
     * @param begin the time the element became active, in document simple time
     */
    protected abstract void toActive(float begin);

    /**
     * Invoked to indicate that this timed element became inactive.
     * @param isFrozen whether the element is frozen or not
     */
    protected abstract void toInactive(boolean isFrozen);

    /**
     * Invoked to indicate that this timed element has had its fill removed.
     */
    protected abstract void removeFill();

    /**
     * Invoked to indicate that this timed element has been sampled at the
     * given time.
     * @param simpleTime the sample time in local simple time
     * @param simpleDur the simple duration of the element
     * @param repeatIteration the repeat iteration during which the element
     *                        was sampled
     */
    protected abstract void sampledAt(float simpleTime, float simpleDur,
                                      int repeatIteration);

    /**
     * Invoked to indicate that this timed element has been sampled
     * at the end of its active time, at an integer multiple of the
     * simple duration.  This is the "last" value that will be used
     * for filling, which cannot be sampled normally.
     */
    protected abstract void sampledLastValue(int repeatIteration);

    /**
     * Returns the timed element with the given ID.
     */
    protected abstract TimedElement getTimedElementById(String id);

    /**
     * Returns the event target with the given ID.
     */
    protected abstract EventTarget getEventTargetById(String id);

    /**
     * Returns the event target that should be listened to for
     * access key events.
     */
    protected abstract EventTarget getRootEventTarget();

    /**
     * Returns a string representation of the given time value.
     */
    public static String toString(float time) {
        if (time == Float.NaN) {
            return "UNRESOLVED";
        } else if (time == Float.POSITIVE_INFINITY) {
            return "INDEFINITE";
        } else {
            return Float.toString(time);
        }
    }
}
