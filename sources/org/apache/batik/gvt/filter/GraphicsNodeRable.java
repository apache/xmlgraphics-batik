/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import org.apache.batik.gvt.GraphicsNode;

/**
 * This interface allows <tt>GraphicsNode</tt> to be seen as 
 * <tt>RenderableImages</tt>, which can be used for operations such as 
 * filtering, masking or compositing.
 * Given a <tt>GraphicsNode</tt>, a <tt>GraphicsNodeRable</tt> can be
 * created through a <tt>GraphicsNodeRableFactory</tt>.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface GraphicsNodeRable extends Filter {
    /**
     * Returns the <tt>GraphicsNode</tt> for which a rendering can be obtained
     * @return the <tt>GraphicsNode</tt> associated with this image.
     */
    public GraphicsNode getGraphicsNode();

    /**
     * Sets the <tt>GraphicsNode</tt> associated with this image.
     */
    public void setGraphicsNode(GraphicsNode node);
}
