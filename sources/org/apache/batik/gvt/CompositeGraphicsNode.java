/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.gvt;

import java.util.List;
import org.apache.batik.gvt.event.CompositeGraphicsNodeListener;

/**
 * A CompositeGraphicsNode is a graphics node that can contain graphics nodes.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface CompositeGraphicsNode extends GraphicsNode {

    /**
     * Returns the list of children or null if any.
     */
    List getChildren();

    /**
     * Adds the specified composite graphics node listener to receive
     * composite graphics node events from this node.
     * @param l the composite graphics node listener to add 
     */
    void addCompositeGraphicsNodeListener(CompositeGraphicsNodeListener l);

    /**
     * Removes the specified composite graphics node listener so that it
     * no longer receives composite graphics node events from this node.
     * @param l the composite graphics node listener to remove
     */
    void removeCompositeGraphicsNodeListener(CompositeGraphicsNodeListener l);
}
