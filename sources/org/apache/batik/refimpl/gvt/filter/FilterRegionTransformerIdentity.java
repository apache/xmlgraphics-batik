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
 * This implementation of <tt>FilterRegionTransformer</tt> 
 * is used when the input <tt>FilterRegion</tt> space is the
 * same as user space. This implementation does not transform
 * the input region and simply returns its input.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class FilterRegionTransformerIdentity implements FilterRegionTransformer {
    /**
     * Converts the input region to the filter region space.
     */
    public Rectangle2D toFilterRegionSpace(Rectangle2D region){
        return region;
    }

    /**
     * Converts the input region to user space
     */
    public Rectangle2D toUserSpace(Rectangle2D region){
        return region;
    }
}
