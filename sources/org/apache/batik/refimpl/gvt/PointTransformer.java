/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import java.awt.geom.Point2D;

/**
 * A <tt>PointTransfomer</tt> is used by a <tt>TransformedPoint</tt>
 * to transform its coordinates. This is used when a point is defined
 * relative to a <tt>GraphicsNode</tt>'s bounding box.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface PointTransformer {

    /**
     * Converts the input point to the point space
     */
    Point2D toPointSpace(Point2D point);

    /**
     * Converts the input point to user space
     */
    Point2D toUserSpace(Point2D point);
}
