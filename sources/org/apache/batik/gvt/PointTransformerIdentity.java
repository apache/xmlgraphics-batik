/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Point2D;

/**
 * This <tt>PointTransfomer</tt> implementation is used when the
 * point's space is the same as user space.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class PointTransformerIdentity implements PointTransformer {

    /**
     * Converts the input point to the point space
     */
    public Point2D toPointSpace(Point2D point){
        return point;
    }

    /**
     * Converts the input point to user space
     */
    public Point2D toUserSpace(Point2D point){
        return point;
    }
}
