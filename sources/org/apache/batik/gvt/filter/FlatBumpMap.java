/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import java.awt.Rectangle;

/**
 * Models a flat bumpMap
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public class FlatBumpMap implements BumpMap {
    /**
     * @return surface scale used by this bump map.
     */
    public double getSurfaceScale(){
        return 1;
    }

    /**
     * @param x x-axis coordinate for which the normal is computed
     * @param y y-axis coordinate for which the normal is computed
     */
    public double[][][] getNormalArray(int x, int y, int w, int h){
        double[][][] N = new double[h][w][];
        double[] n = {0, 0, 1};
        for(int i=0; i<h; i++){
            for(int j=0; j<w; j++){
                N[i][j] = n;
            }
        }

        return N;
    }
    
    /*
     * @return true if the normal is constant over the surface
     */
    public boolean isConstant(Rectangle area){
        return true;
    }
}

