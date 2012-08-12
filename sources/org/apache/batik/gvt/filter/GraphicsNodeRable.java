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

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;

/**
 * This interface allows <code>GraphicsNode</code> to be seen as
 * <code>RenderableImages</code>, which can be used for operations such as
 * filtering, masking or compositing.
 * Given a <code>GraphicsNode</code>, a <code>GraphicsNodeRable</code> can be
 * created through a <code>GraphicsNodeRableFactory</code>.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface GraphicsNodeRable extends Filter {
    /**
     * Returns the <code>GraphicsNode</code> for which a rendering can be obtained
     * @return the <code>GraphicsNode</code> associated with this image.
     */
    GraphicsNode getGraphicsNode();

    /**
     * Sets the <code>GraphicsNode</code> associated with this image.
     */
    void setGraphicsNode(GraphicsNode node);

    /**
     * Returns true if this Rable get's it's contents by calling
     * primitivePaint on the associated <code>GraphicsNode</code> or
     * false if it uses paint.
     */
    boolean getUsePrimitivePaint();

    /**
     * Set to true if this Rable should get it's contents by calling
     * primitivePaint on the associated <code>GraphicsNode</code> or false
     * if it should use paint.
     */
    void setUsePrimitivePaint(boolean usePrimitivePaint);
}
