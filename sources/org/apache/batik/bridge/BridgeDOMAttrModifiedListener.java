/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.Iterator;
import java.util.List;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.MutationEvent;

/**
 * This class listens to <code>DOMAttrModified</code> events on the
 * SVG DOM tree and propagate the changes to the bridge.
 *
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @version $Id$
 */
public class BridgeDOMAttrModifiedListener implements EventListener {

    private BridgeContext context;

    /**
     * Creates the listener.
     * @param context the bridge context
     */
    public BridgeDOMAttrModifiedListener(BridgeContext context){
        this.context = context;
    }

    /**
     * Handles the mutation of an attribute
     * @param evt the DOM event
     */
    public void handleEvent(Event evt){
        MutationEvent event = (MutationEvent)evt;
        BridgeMutationEvent mevent;

        Element element = (Element)evt.getTarget();
        GraphicsNode gn = context.getGraphicsNode(element);

        if (gn != null){ // The mutation happends on a Graphics Node
            mevent = new BridgeMutationEvent(element,
                                             context,
                                             BridgeMutationEvent.PROPERTY_MUTATION_TYPE);

            mevent.setAttrName(event.getAttrName());
            mevent.setAttrNewValue(event.getNewValue());
            mevent.setGraphicsNode(gn);
            context.getBridgeUpdateManager().addDirtyNode(gn, mevent);

        } else {
            // A mutation on a style element (for example a Gradient)

            List references = context.getStyleReferenceList(element);
            if (!references.isEmpty()) {

                StyleReference reference;
                BridgeMutationEvent elevt;
                mevent = new BridgeMutationEvent(element,
                                                 context,
                                                 BridgeMutationEvent.PROPERTY_MUTATION_TYPE);

                mevent.setAttrName(event.getAttrName());
                mevent.setAttrNewValue(event.getNewValue());


                for (Iterator it = references.iterator(); it.hasNext();) {
                    reference = (StyleReference)it.next();
                    elevt = new  BridgeMutationEvent(context.getElement(reference.getGraphicsNode()),
                                                     context,
                                                     BridgeMutationEvent.STYLE_MUTATION_TYPE);
                    elevt.setGraphicsNode(reference.getGraphicsNode());
                    elevt.setAttrName(reference.getStyleAttribute());
                    elevt.setStyleMutationEvent(mevent);
                    context.getBridgeUpdateManager().addDirtyNode(gn, elevt);
                }
            }
        }
        // probably many other cases.
    }
}
