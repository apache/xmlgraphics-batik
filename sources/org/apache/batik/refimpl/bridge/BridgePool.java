/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import org.apache.batik.bridge.Bridge;
import java.util.HashMap;

/**
 * A pool that contains <tt>Bridge</tt> objects indexed by a namespace
 * URI and a local name.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
class BridgePool {

    /**
     * The Bridge map.
     * Keys are namespace URI, values are HashMap (with keys are local
     * name and values are factories).
     */
    protected HashMap namespaceURIMap;

    /**
     * Constructs a new empty <tt>BridgePool</tt>.
     */
    public BridgePool() {}

    /**
     * Returns the <tt>Bridge</tt> object associated to the specified
     * namespace URI and local name.
     * @param namespaceURI the namespace URI
     * @param localName the local name
     * @return the bridge object
     */
    public Bridge getBridge(String namespaceURI, String localName) {
        if (namespaceURIMap == null) {
            return null;
        }
        namespaceURI = ((namespaceURI == null)? "" : namespaceURI);
        HashMap localNameMap = (HashMap) namespaceURIMap.get(namespaceURI);
        if (localNameMap == null) {
            return null;
        }
        return (Bridge) localNameMap.get(localName);
    }

    /**
     * Associates the specified <tt>Bridge</tt> object with the specified
     * namespace URI nad local name.
     * @param namespaceURI the namespace URI
     * @param localName the local name
     * @param bridge the bridge object
     */
    public void putBridge(String namespaceURI, String localName, Bridge bridge){
        namespaceURI = ((namespaceURI == null)? "" : namespaceURI);
        if (namespaceURIMap == null) {
            namespaceURIMap = new HashMap();
        }
        HashMap localNameMap = (HashMap) namespaceURIMap.get(namespaceURI);
        if (localNameMap == null) {
            localNameMap = new HashMap();
            namespaceURIMap.put(namespaceURI, localNameMap);
        }
        localNameMap.put(localName, bridge);
    }

    /**
     * Removes the <tt>Bridge</tt> object associated to the specified
     * namespace URI and local name.
     * @param namespaceURI the namespace URI
     * @param localName the local name
     */
    public void removeBridge(String namespaceURI, String localName) {
        namespaceURI = ((namespaceURI == null)? "" : namespaceURI);
        if (namespaceURIMap == null) {
            return;
        }
        HashMap localNameMap = (HashMap) namespaceURIMap.get(namespaceURI);
        if (localNameMap != null) {
            localNameMap.remove(localName);
            if (localNameMap.isEmpty()) {
                namespaceURIMap.remove(namespaceURI);
                if (namespaceURIMap.isEmpty()) {
                    namespaceURIMap = null;
                }
            }
        }
    }
}
