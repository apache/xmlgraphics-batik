/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import org.apache.batik.ext.awt.image.renderable.Filter;
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

    /**
     * Returns true if this Rable get's it's contents by calling
     * primitivePaint on the associated <tt>GraphicsNode</tt> or
     * false if it uses paint.
     */
    public boolean getUsePrimitivePaint();

    /**
     * Set to true if this Rable should get it's contents by calling
     * primitivePaint on the associated <tt>GraphicsNode</tt> or false
     * if it should use paint.  
     */
    public void setUsePrimitivePaint(boolean usePrimitivePaint);
}
