/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.util.SVGConstants;

/**
 * The base bridge class for SVG elements.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class AbstractSVGBridge implements Bridge, SVGConstants {

    /**
     * The update handler to notify each time the GVT product
     * associated to this bridge changes.
     */
    protected BridgeUpdateHandler handler;

    /**
     * The private key of the update handler.
     */
    protected int handlerKey;

    /**
     * Constructs a new abstract bridge for SVG elements.
     */
    protected AbstractSVGBridge() {}

    /**
     * Returns the SVG namespace URI.
     */
    public String getNamespaceURI() {
        return SVG_NAMESPACE_URI;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        // <!> FIXME: temporary fix for progressive implementation
        //System.out.println("use static bridge for: "+getLocalName());
        return this;
    }

    /**
     * Returns the handler that is called each time this bridge
     * updates its GVT product.
     */
    public BridgeUpdateHandler getBridgeUpdateHandler() {
        return handler;
    }

    /**
     * Sets the handler that is used to track each update of this
     * bridge's GVT product.
     *
     * @param handler the handler to call
     * @param handlerKey a private key the handler might use when it registers
     */
    public void setBridgeUpdateHandler(BridgeUpdateHandler handler, 
                                       int handlerKey) {
        this.handler = handler;
        this.handlerKey = handlerKey;
    }

    /**
     * Notifies the BridgeUpdateHandler using the specified event that
     * an update is starting.
     *
     * @param evt the BridgeUpdateHandler event 
     */
    protected void fireBridgeUpdateStarting(BridgeUpdateEvent evt) {
        if (handler != null) {
            evt.setHandlerKey(handlerKey);
            handler.bridgeUpdateStarting(evt);
        }
    }

    /**
     * Notifies the BridgeUpdateHandler using the specified event that
     * an update is complete.
     *
     * @param evt the BridgeUpdateHandler event 
     */
    protected void fireBridgeUpdateCompleted(BridgeUpdateEvent evt) {
        if (handler != null) {
            evt.setHandlerKey(handlerKey);
            handler.bridgeUpdateCompleted(evt);
        }
    }
}
