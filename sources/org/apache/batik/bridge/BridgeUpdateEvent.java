/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.EventObject;

/**
 * Describes an update from a <tt>DynamicBridge</tt>
 * 
 * @author <a href="mailto:vincent.hardy@apache.org">Vincent Hardy</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class BridgeUpdateEvent extends EventObject {

    /** The handler key. */
    private int handlerKey;

    /* The new value of the GVT product. */
    private Object newValue;

    /* The old value of the GVT product. */
    private Object oldValue;

    /**
     * Constructs a new <tt>BridgeUpdateEvent</tt>.
     *
     * @param source the source of the event
     */
    public BridgeUpdateEvent(Object source){
        super(source);
    }

    /**
     * Returns the BridgeUpdateHandler's key.
     */
    public int getHandlerKey(){
        return handlerKey;
    }

    /**
     * Sets the BridgeUpdateHandler's key to the specified value.
     *
     * @param handlerKey the key of the BridgeUpdateHandler
     */
    public void setHandlerKey(int handlerKey){
        this.handlerKey = handlerKey;
    }

    /**
     * Returns the new GVT product resulting from the update or null
     * if the update has not been completed yet.
     */
    public Object getNewValue(){
        return newValue;
    }

    /**
     * Sets the new GVT product to the specified value.
     *
     * @param newValue the new GVT product resulting from the update
     */
    public void setNewValue(Object newValue){
        this.newValue = newValue;
    }

    /**
     * Returns the old GVT product before the update.
     */
    public Object getOldValue(){
        return oldValue;
    }

    /**
     * Sets the old GVT product to the specified value.
     *
     * @param oldValue the old GVT product before the update
     */
    public void setOldValue(Object oldValue){
        this.oldValue = oldValue;
    }
}
