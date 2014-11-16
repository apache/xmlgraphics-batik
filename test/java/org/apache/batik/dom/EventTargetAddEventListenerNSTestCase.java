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
package org.apache.batik.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests EventTarget.addEventListenerNS.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
@Ignore
public class EventTargetAddEventListenerNSTestCase extends DOM3Test {
    static class Listener1 implements EventListener {
        int count = 0;
        public void handleEvent(Event e) {
            count++;
        }
        int getCount() {
            int c = count;
            count = 0;
            return c;
        }
    }
    static class Listener2 implements EventListener {
        int count = 0;
        public void handleEvent(Event e) {
            count++;
            e.stopPropagation();
        }
        int getCount() {
            int c = count;
            count = 0;
            return c;
        }
    }
    public boolean runImplBasic() throws Exception {
        Listener1 l1 = new Listener1();
        Listener2 l2 = new Listener2();

        Document doc = newDoc();
        Element e = doc.createElementNS(null, "test");
        AbstractNode et = (AbstractNode) e;
        doc.appendChild(e);
        et.addEventListenerNS(XML_EVENTS_NAMESPACE_URI, "DOMAttrModified", l1, false, null);
        et.addEventListenerNS(null, "DOMAttrModified", l1, false, null);
        e.setAttributeNS(null, "test", "abc");
        boolean pass = l1.getCount() == 2;
        et.addEventListenerNS(XML_EVENTS_NAMESPACE_URI, "DOMAttrModified", l2, false, "g1");
        et.addEventListenerNS(null, "DOMAttrModified", l2, false, "g1");
        e.setAttributeNS(null, "test", "def");

        pass = pass && l2.getCount() == 2;

        return pass;
    }
}
