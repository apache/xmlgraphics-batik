/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Rectangle2D;

/**
 * This class describes the area on which a filter operates
 * (in user space). 
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface FilterRegion {
    /**
     * @param node the GraphicsNode to which the filter applies
     */
    public Rectangle2D getRegion(GraphicsNode node);
}
