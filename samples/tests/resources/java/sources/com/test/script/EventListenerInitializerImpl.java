/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package com.test.script;

import org.w3c.dom.Element;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import org.w3c.dom.svg.EventListenerInitializer;
import org.w3c.dom.svg.SVGDocument;

/**
 * This class implements the EventListenerInitializer interface.
 *
 * @author <a href="mailto:cjolif@apache.org">Christophe Jolif</a>
 * @version $Id$
 */
public class EventListenerInitializerImpl implements EventListenerInitializer {

    /**
     * This method is called by the SVG viewer
     * when the scripts are loaded to register
     * the listener needed.
     * @param doc The current document.
     */
    public void initializeEventListeners(SVGDocument doc) {
        System.err.println(">>>>>>>>>>>>>>>>>>> SVGDocument : " + doc);
        ((EventTarget)doc.getElementById("testContent")).
            addEventListener("mousedown", new EventListener() {
                public void handleEvent(Event evt) {
                    ((Element)evt.getTarget()).setAttributeNS(null, "fill", "orange");
                }
            }, false);
    }
}

