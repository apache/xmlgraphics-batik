/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.bridge;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.events.UIEvent;

/**
 * A class that manages focus on elements.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class FocusManager {

    /**
     * The element that has the focus so far.
     */
    protected EventTarget lastFocusEventTarget;

    /**
     * The document.
     */
    protected Document document;

    /**
     * The EventListener that tracks 'mouseclick' events.
     */
    protected EventListener mouseclickListener;

    /**
     * The EventListener that tracks 'DOMFocusIn' events.
     */
    protected EventListener domFocusInListener;

    /**
     * The EventListener that tracks 'DOMFocusOut' events.
     */
    protected EventListener domFocusOutListener;

    /**
     * The EventListener that tracks 'mouseover' events.
     */
    protected EventListener mouseoverListener;

    /**
     * The EventListener that tracks 'mouseout' events.
     */
    protected EventListener mouseoutListener;

    /**
     * Constructs a new <tt>FocusManager</tt> for the specified document.
     *
     * @param doc the document
     */
    public FocusManager(Document doc) {
        document = doc;
        EventTarget target = (EventTarget)doc;

        mouseclickListener = new MouseClickTacker();
        target.addEventListener("click", mouseclickListener, true);

        mouseoverListener = new MouseOverTacker();
        target.addEventListener("mouseover", mouseoverListener, true);

        mouseoutListener = new MouseOutTacker();
        target.addEventListener("mouseout", mouseoutListener, true);

        domFocusInListener = new DOMFocusInTracker();
        target.addEventListener("DOMFocusIn", domFocusInListener, true);

        domFocusOutListener = new DOMFocusOutTracker();
        target.addEventListener("DOMFocusOut", domFocusOutListener, true);
    }

    /**
     * Returns the current element that has the focus or null if any.
     */
    public EventTarget getCurrentEventTarget() {
        return lastFocusEventTarget;
    }

    /**
     * Removes all listeners attached to the document and that manage focus.
     */
    public void dispose() {
        if (document == null) return;
        EventTarget target = (EventTarget)document;
        target.removeEventListener("click", mouseclickListener, true);
        target.removeEventListener("mouseover", mouseoverListener, true);
        target.removeEventListener("mouseout", mouseoutListener, true);
        target.removeEventListener("DOMFocusIn", domFocusInListener, true);
        target.removeEventListener("DOMFocusOut", domFocusOutListener, true);
        lastFocusEventTarget = null;
        document = null;
    }

    /**
     * The class that is responsible for tracking 'mouseclick' changes.
     */
    protected class MouseClickTacker implements EventListener {

        public void handleEvent(Event evt) {
            MouseEvent mevt = (MouseEvent)evt;
            fireDOMActivateEvent(evt.getTarget(), mevt.getDetail());
        }
    }

    /**
     * The class that is responsible for tracking 'DOMFocusIn' changes.
     */
    protected class DOMFocusInTracker implements EventListener {

        public void handleEvent(Event evt) {
            if (lastFocusEventTarget != null && 
                lastFocusEventTarget != evt.getTarget()) {
                fireDOMFocusOutEvent(lastFocusEventTarget);
            }
            lastFocusEventTarget = evt.getTarget();
        }
    }

    /**
     * The class that is responsible for tracking 'DOMFocusOut' changes.
     */
    protected class DOMFocusOutTracker implements EventListener {

        public void handleEvent(Event evt) {
            lastFocusEventTarget = null;
        }
    }

    /**
     * The class that is responsible to update the focus according to
     * 'mouseover' event.
     */
    protected class MouseOverTacker implements EventListener {

        public void handleEvent(Event evt) {
            EventTarget target = evt.getTarget();
            fireDOMFocusInEvent(target);
        }
    }

    /**
     * The class that is responsible to update the focus according to
     * 'mouseout' event.
     */
    protected class MouseOutTacker implements EventListener {

        public void handleEvent(Event evt) {
            EventTarget target = evt.getTarget();
            fireDOMFocusOutEvent(target);
        }
    }

    /**
     * Fires a 'DOMFocusIn' event to the specified target.
     *
     * @param target the event target
     */
    protected void fireDOMFocusInEvent(EventTarget target) {
        DocumentEvent docEvt = 
            (DocumentEvent)((Element)target).getOwnerDocument();
        UIEvent uiEvt = (UIEvent)docEvt.createEvent("UIEvents");
        uiEvt.initUIEvent("DOMFocusIn", true, false, null, 0);
        target.dispatchEvent(uiEvt);
    }

    /**
     * Fires a 'DOMFocusOut' event to the specified target.
     *
     * @param target the event target
     */
    protected void fireDOMFocusOutEvent(EventTarget target) {
        DocumentEvent docEvt = 
            (DocumentEvent)((Element)target).getOwnerDocument();
        UIEvent uiEvt = (UIEvent)docEvt.createEvent("UIEvents");
        uiEvt.initUIEvent("DOMFocusOut", true, false, null, 0);
        target.dispatchEvent(uiEvt);
    }
    
    /**
     * Fires a 'DOMActivate' event to the specified target.
     *
     * @param target the event target
     * @param detailArg the detailArg parameter of the event
     */
    protected void fireDOMActivateEvent(EventTarget target, int detailArg) {
        DocumentEvent docEvt = 
            (DocumentEvent)((Element)target).getOwnerDocument();
        UIEvent uiEvt = (UIEvent)docEvt.createEvent("UIEvents");
        uiEvt.initUIEvent("DOMActivate", true, true, null, detailArg);
        target.dispatchEvent(uiEvt);
    }
}
