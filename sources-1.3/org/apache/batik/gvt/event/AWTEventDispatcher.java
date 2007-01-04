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
package org.apache.batik.gvt.event;

import java.awt.event.KeyEvent;

import org.apache.batik.gvt.GraphicsNode;

/**
 * A concrete version of {@link org.apache.batik.gvt.event.AWTEventDispatcher}.
 *
 * This class is used for JDKs &lt; 1.4, which don't have MouseWheelEvent
 * support.  For JDKs &gt;= 1.4, the file
 * sources-1.4/org/apache/batik/gvt/event/AWTEventDispatcher defines a
 * version of this class that does support MouseWheelEvents.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AWTEventDispatcher extends AbstractAWTEventDispatcher {

    /**
     * Dispatches the specified AWT key event.
     * @param evt the key event to dispatch
     */
    protected void dispatchKeyEvent(KeyEvent evt) {
        currentKeyEventTarget = lastHit;
        GraphicsNode target =
            currentKeyEventTarget == null ? root : currentKeyEventTarget;
        processKeyEvent
            (new GraphicsNodeKeyEvent(currentKeyEventTarget,
                                      evt.getID(),
                                      evt.getWhen(),
                                      evt.getModifiers(),
                                      getCurrentLockState(),
                                      evt.getKeyCode(),
                                      evt.getKeyChar(),
                                      0));
    }
}
