/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;

/**
 * This interface lets <tt>GraphicsNode</tt> create instances of
 * <tt>GraphicsNodeRable</tt> appropriate for the filter module
 * implementation.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ConcreteGraphicsNodeRableFactory implements GraphicsNodeRableFactory {
    /**
     * Returns a <tt>GraphicsNodeRable</tt> initialized with the
     * input <tt>GraphicsNode</tt>.
     */
    public GraphicsNodeRable createGraphicsNodeRable(GraphicsNode node){
        return (GraphicsNodeRable)node.getGraphicsNodeRable(true);
    }
}
