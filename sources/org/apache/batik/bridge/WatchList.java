/*

   Copyright 2006 The Apache Software Foundation 

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

package org.apache.batik.bridge;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.EventListener;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>deweese</a>
 * @version $Id: skel.el,v 1.1 2003/05/13 21:04:46 deweese Exp $
 */
public class WatchList {

    public Set toWatch = new HashSet();
    public EventListener el;

    public WatchList() {
    }

    static class WatchPair {
        public EventTarget et;
        public String eventType;

        WatchPair(EventTarget et, String eventType) {
            this.et        = et;
            this.eventType = eventType;
        }

        public boolean equals(Object obj) {
            if (obj instanceof WatchPair) {
                WatchPair wp = (WatchPair)obj;
                return ((et == wp.et) && (eventType == wp.eventType));
            }
            return false;
        }

        public int hashCode() {
            return et.hashCode() ^ eventType.hashCode();
        }
    };

    public void addToWatchList(EventTarget et, String eventType) {
        if (el != null) 
            throw new IllegalStateException
                ("Can't add to watch list after attaching listeners");
        toWatch.add(new WatchPair(et, eventType));
    }

    public void addListener(EventListener l, BridgeContext ctx) {
        Iterator iter = toWatch.iterator();
        while (iter.hasNext()) {
            WatchPair wp = (WatchPair)iter.next();
            wp.et.addEventListener(wp.eventType, l, false);
            ctx.storeEventListener(wp.et, wp.eventType, l, false);
        }
        el = l;
    }

    public void dispose() {
        Iterator iter = toWatch.iterator();
        while (iter.hasNext()) {
            WatchPair wp = (WatchPair)iter.next();
            wp.et.removeEventListener(wp.eventType, el, false);
        }
    }
};
