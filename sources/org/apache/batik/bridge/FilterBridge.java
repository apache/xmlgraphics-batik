/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.Map;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;

import org.w3c.dom.Element;

/**
 * Implementations of this interface are able to bridge a specific filter,
 * modeled by a DOM element, to a concrete <tt>Filter<tt>.

 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface FilterBridge extends Bridge {
    /**
     * Returns the <tt>Filter</tt> that implements the filter 
     * operation modeled by the input DOM element
     *
     * @param filteredNode the GVT node to which the filter will be attached.
     * @param bridgeContext the context to use.
     * @param filterElement DOM element that represents a filter abstraction
     * @param filteredElement DOM element that references the input filter element.
     * @param in the <tt>Filter</tt> that represents the current
     *        filter input if the filter chain.
     * @param filterRegion the filter area defined for the filter chained 
     *        the new node will be part of.
     * @param filterMap a map where the mediator can map a name to the 
     *        <tt>Filter</tt> it creates. Other <tt>FilterBridge</tt>s
     *        can then access a filter node from the filterMap if they
     *        know its name.
     */
    public Filter create(GraphicsNode filteredNode,
                         BridgeContext bridgeContext,
                         Element filterElement,
                         Element filteredElement,
                         Filter in,
                         FilterRegion filterRegion,
                         Map filterMap);

    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     *
     * @param bridgeContext the context to use.
     * @param filterElement DOM element that represents the filter abstraction
     * @param filterNode image that implements the filter abstraction and whose
     *        state should be updated to reflect the filterElement's current
     *        state.
     */
    public void update(BridgeContext bridgeContext,
                       Element filterElement,
                       Filter filter,
                       Map filterMap);
}
