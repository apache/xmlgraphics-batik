/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.gvt;

/**
 * A graphics node that is a placeholder for another graphics node to
 * control access to it.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface ProxyGraphicsNode extends LeafGraphicsNode {

    /**
     * Sets the graphics node to proxy to the specified graphics node.
     * @param newSource the new graphics node to proxy
     */
    void setSource(GraphicsNode newSource);

    /**
     * Returns the proxy's graphics node.
     */
    GraphicsNode getSource();

}
