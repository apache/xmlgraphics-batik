/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;

/**
 * Bridge class for vending <tt>Filter</tt> objects.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public interface FilterBridge extends Bridge {

    /**
     * Creates a <tt>Filter</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param filterElement the element that defines the filter
     * @param filteredElement the element that references the filter element
     * @param filteredNode the graphics node to filter
     */
    Filter createFilter(BridgeContext ctx,
                        Element filterElement,
                        Element filteredElement,
                        GraphicsNode filteredNode);

}
