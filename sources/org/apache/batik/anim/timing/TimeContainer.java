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
import java.util.LinkedHashSet;

/**
 * An abstract base class for time container elements.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public abstract class TimeContainer extends TimedElement {

    /**
     * The child {@link TimedElement}s of this time container.
     */
    protected LinkedHashSet children = new LinkedHashSet();

    /**
     * Adds a {@link TimedElement} to this container.
     */
    public void addChild(TimedElement e) {
        children.add(e);
        e.parent = this;
        setRoot(e, root);
        root.fireElementAdded(e);
    }

    /**
     * Recursively sets the {@link TimedDocumentRoot} of the given
     * {@link TimedElement} and any of its descendants.
     */
    protected void setRoot(TimedElement e, TimedDocumentRoot root) {
        e.root = root;
        if (e instanceof TimeContainer) {
            TimeContainer c = (TimeContainer) e;
            TimedElement[] es = c.getChildren();
            for (int i = 0; i < es.length; i++) {
                setRoot(es[i], root);
            }
        }
    }

    /**
     * Removes a {@link TimedElement} from this container.
     */
    public void removeChild(TimedElement e) {
        children.remove(e);
        e.parent = null;
        setRoot(e, null);
        root.fireElementRemoved(e);
    }

    /**
     * Returns an array of the children of this container.
     */
    public TimedElement[] getChildren() {
        return (TimedElement[]) children.toArray(new TimedElement[0]);
    }

    /**
     * Calculates the local simple time.
     */
    protected void sampleAt(float parentSimpleTime) {
        super.sampleAt(parentSimpleTime);
        sampleChildren(parentSimpleTime);
    }

    /**
     * Samples all the child timed elements.
     */
    protected void sampleChildren(float parentSimpleTime) {
        Iterator i = children.iterator();
        while (i.hasNext()) {
            TimedElement e = (TimedElement) i.next();
            e.sampleAt(parentSimpleTime);
        }
    }

    /**
     * Resets this element.
     */
    protected void reset(boolean clearCurrentBegin) {
        super.reset(clearCurrentBegin);
        Iterator i = children.iterator();
        while (i.hasNext()) {
            TimedElement e = (TimedElement) i.next();
            e.reset(clearCurrentBegin);
        }
    }

    /**
     * Returns the default begin time for the given child
     * timed element.
     */
    public abstract float getDefaultBegin(TimedElement child);
}
