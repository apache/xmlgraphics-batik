/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.geom.Rectangle2D;

/**
 * A <tt>FilterRegionTransformer</tt> is used by a <tt>FilterChainRegion</tt>
 * to transform its region from the <tt>FilterRegion</tt> space to 
 * the user space. This is used when a region is defined relative 
 * to a <tt>GraphicsNode</tt>'s bounding box.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface FilterRegionTransformer {
    /**
     * Converts the input region to the filter region space.
     */
    public Rectangle2D toFilterRegionSpace(Rectangle2D region);

    /**
     * Converts the input region to user space
     */
    public Rectangle2D toUserSpace(Rectangle2D region);
}
