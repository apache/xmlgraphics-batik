/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import java.awt.geom.Rectangle2D;

/**
 * Interface for filter regions. Regions cannot be computed
 * until rendering time, which explains the need for this
 * class.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface FilterRegion {
    /**
     * @return the region, in user space
     */
    public Rectangle2D getRegion();
}
