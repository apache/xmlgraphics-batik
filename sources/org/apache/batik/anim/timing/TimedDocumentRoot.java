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

import org.apache.batik.util.DoublyIndexedSet;

/**
 * An abstract base class for the root time container element
 * for a document.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public abstract class TimedDocumentRoot extends TimeContainer {

    /**
     * The wallclock time that the document began.
     */
    protected Calendar documentBeginTime;

    /**
     * Time offset that has accumulated while the document is paused.
     */
    protected float accumulatedOffset;

    /**
     * Allows the use of accessKey() timing specifiers with a single
     * character, as specified in SVG 1.1.
     */
    protected boolean useSVG11AccessKeys;

    /**
     * Allows the use of accessKey() timing specifiers with a DOM 3
     * key name, as specified in SVG 1.2.
     */
    protected boolean useSVG12AccessKeys;

    /**
     * A set to determine when propagation of new Instance times should
     * be stopped.
     */
    protected DoublyIndexedSet propagationFlags = new DoublyIndexedSet();

    /**
     * Creates a new TimedDocumentRoot.
     * @param useSVG11AccessKeys allows the use of accessKey() timing
     *                           specifiers with a single character
     * @param useSVG12AccessKeys allows the use of accessKey() with a
     *                           DOM 3 key name
     */
    public TimedDocumentRoot(boolean useSVG11AccessKeys,
                             boolean useSVG12AccessKeys) {
        root = this;
        this.useSVG11AccessKeys = useSVG11AccessKeys;
        this.useSVG12AccessKeys = useSVG12AccessKeys;
    }

    /**
     * Returns the implicit duration of the element.  The document root
     * has an {@link #INDEFINITE} implicit duration.
     */
    protected float getImplicitDur() {
        return INDEFINITE;
    }

    /**
     * Returns the default begin time for the given child
     * timed element.  In SVG, this is always 0, since the
     * only time container is the root SVG element, which acts
     * like a 'par'.
     */
    public float getDefaultBegin(TimedElement child) {
        return 0.0f;
    }

    /**
     * Samples the entire timegraph at the given time.
     */
    public void seekTo(float time) {
        Trace.enter(this, "seekTo", new Object[] { new Float(time) } ); try {
        root.clearPropagationFlags();
        // No time containers in SVG, so we don't have to worry
        // about a partial ordering of timed elements to sample.
        TimedElement[] es = getChildren();
        for (int i = 0; i < es.length; i++) {
            // System.err.print("[" + ((Test.AnimateElement) es[i]).id + "] ");
            es[i].sampleAt(time);
        }
        boolean needsUpdates;
        do {
            needsUpdates = false;
            for (int i = 0; i < es.length; i++) {
                if (es[i].shouldUpdateCurrentInterval) {
                    needsUpdates = true;
                    // System.err.print("{" + ((Test.AnimateElement) es[i]).id + "} ");
                    es[i].sampleAt(time);
                }
            }
        } while (needsUpdates);
        } finally { Trace.exit(); }
    }

    /**
     * Resets the entire timegraph.
     */
    public void resetDocument(Calendar documentBeginTime) {
        if (documentBeginTime == null) {
            this.documentBeginTime = Calendar.getInstance();
        } else {
            this.documentBeginTime = documentBeginTime;
        }
        reset(true);
    }

    /**
     * Returns the wallclock time that the document began.
     */
    public Calendar getDocumentBeginTime() {
        return documentBeginTime;
    }

    /**
     * Converts a wallclock time to document time.
     */
    public float convertWallclockTime(Calendar time) {
        long begin = documentBeginTime.getTimeInMillis();
        long t = time.getTimeInMillis();
        return (t - begin) / 1000f;
    }

    /**
     * Returns whether the specified newly created {@link Interval} should 
     * propagate its times to the given {@link TimingSpecifier}.
     * @param i the Interval that has just been created
     * @param ts the TimingSpecifier that is a dependent of the Interval
     * @param isBegin whether the dependency is on the begin or end time of
     *        the Interval
     */
    boolean shouldPropagate(Interval i, TimingSpecifier ts, boolean isBegin) {
        InstanceTime it = isBegin ? i.getBeginInstanceTime()
                                  : i.getEndInstanceTime();
        if (propagationFlags.contains(it, ts)) {
            return false;
        }
        propagationFlags.add(it, ts);
        return true;
    }

    void clearPropagationFlags() {
        propagationFlags.clear();
    }

    /**
     * Returns the namespace URI of the event that corresponds to the given
     * animation event name.
     */
    protected abstract String getEventNamespaceURI(String eventName);

    /**
     * Returns the type of the event that corresponds to the given
     * animation event name.
     */
    protected abstract String getEventType(String eventName);

    /**
     * Returns the name of the repeat event.
     * @return either "repeat" or "repeatEvent"
     */
    protected abstract String getRepeatEventName();
}
