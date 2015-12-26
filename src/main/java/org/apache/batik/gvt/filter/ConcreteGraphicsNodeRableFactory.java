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
package org.apache.batik.gvt.filter;

import org.apache.batik.gvt.GraphicsNode;

/**
 * This interface lets <code>GraphicsNode</code> create instances of
 * <code>GraphicsNodeRable</code> appropriate for the filter module
 * implementation.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ConcreteGraphicsNodeRableFactory implements GraphicsNodeRableFactory {
    /**
     * Returns a <code>GraphicsNodeRable</code> initialized with the
     * input <code>GraphicsNode</code>.
     */
    public GraphicsNodeRable createGraphicsNodeRable(GraphicsNode node){
        return (GraphicsNodeRable)node.getGraphicsNodeRable(true);
    }
}
