/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * Describes an update from a <tt>DynamicBridge</tt>
 * 
 * @author <a href="mailto:vincent.hardy@apache.org">Vincent Hardy</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class BridgeUpdateEvent {
    
    private int handlerKey;
    private Object newValue;
    private Object oldValue;

    /**
     *
     */
    public BridgeUpdateEvent(){
    }

    /**
     *
     */
    public int getHandlerKey(){
        return handlerKey;
    }

    /**
     * 
     */
    public void setHandlerKey(int handlerKey){
        this.handlerKey = handlerKey;
    }

    /**
     * 
     */
    public Object getNewValue(){
        return newValue;
    }

    /**
     * 
     */
    public void setNewValue(Object newValue){
        this.newValue = newValue;
    }

    /**
     * 
     */
    public Object getOldValue(){
        return oldValue;
    }

    /**
     * 
     */
    public void setOldValue(Object oldValue){
        this.oldValue = oldValue;
    }

}
