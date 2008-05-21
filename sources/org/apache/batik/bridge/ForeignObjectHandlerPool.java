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
package org.apache.batik.bridge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.batik.util.Service;

/**
 * A class that can construct {@link ForeignObjectHandler}s for a given
 * namespace URI.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class ForeignObjectHandlerPool {

    /**
     * The default ForeignObjectHandlerFactory map.
     */
    protected static Map defaultFactories = new HashMap(3);

    /**
     * The ForeignObjectHandlerFactory map.
     */
    protected Map factories = new HashMap(3);

    static {
        Iterator it = Service.providers(ForeignObjectHandlerFactory.class);
        while (it.hasNext()) {
            ForeignObjectHandlerFactory factory =
                (ForeignObjectHandlerFactory) it.next();
            defaultFactories.put(factory.getNamespaceURI(), factory);
        }
    }

    /**
     * Creates a new ForeignObjectHandlerPool.
     */
    public ForeignObjectHandlerPool() {
        factories.putAll(defaultFactories);
    }

    /**
     * Creates a new ForeignObjectHandler for the given namespace URI.
     * @param ns the namespace URI of the elements the returned
     *           ForeignObjectHandler should be able to handle
     */
    public ForeignObjectHandler createForeignObjectHandler(String ns) {
        ForeignObjectHandlerFactory factory =
            (ForeignObjectHandlerFactory) factories.get(ns);
        if (factory == null) {
            return null;
        }
        return factory.createHandler();
    }

    /**
     * Adds for the specified namespace URI, the specified
     * ForeignObjectHandlerFactory.
     */
    public void putForeignObjectHandlerFactory
            (String ns, ForeignObjectHandlerFactory factory) {
        factories.put(ns, factory);
    }

    /**
     * Removes the ForeignObjectHandlerFactory associated with the specified
     * namespace URI.
     */
    public void removeForeignObjectHandlerFactory(String ns) {
        factories.remove(ns);
    }
}
