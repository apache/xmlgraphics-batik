/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.geom.Rectangle2D;

import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;

/**
 * This implementation of <tt>FilterRegion</tt> is initialized
 * with the source from which the region is pulled when needed.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class FilterSourceRegion implements FilterRegion {
    /**
     * Source from which the bounds are requested
     */
    private Filter sources[];

    /**
     * @param source used to pull the filter region
     */
    public FilterSourceRegion(Filter source){
        if(source == null){
            throw new IllegalArgumentException();
        }

        this.sources = new Filter[] {source};
    }

    /**
     * @param sources used to pull the filter region
     */
    public FilterSourceRegion(Filter sources[]){
        if(sources == null){
            throw new IllegalArgumentException();
        }

        if(sources.length < 1){
            throw new IllegalArgumentException();
        }

        this.sources = new Filter[sources.length];
        for(int i=0; i<sources.length; i++){
            this.sources[i] = sources[i];
            if(this.sources[i] == null){
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * Returns this object's filter region, in user space.
     */
    public Rectangle2D getRegion(){
        Rectangle2D bounds = sources[0].getBounds2D();
        for(int i=0; i<sources.length; i++){
            bounds.add(sources[i].getBounds2D());
        }

        return bounds;
    }
}
