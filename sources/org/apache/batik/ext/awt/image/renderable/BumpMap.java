/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Rectangle;

/**
 * Models a map of elevations, used by various lighting filters.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public interface BumpMap {
    /**
     * @return surface scale used by this bump map.
     */
    public double getSurfaceScale();

    /**
     * @param x x-axis coordinate for which the normal is computed
     * @param y y-axis coordinate for which the normal is computed
     */
    public double[][][] getNormalArray(int x, int y, 
                                       int width, int height);
    
    /*
     * @return true if the normal is constant over the surface
     */
    public boolean isConstant(Rectangle area);
}

