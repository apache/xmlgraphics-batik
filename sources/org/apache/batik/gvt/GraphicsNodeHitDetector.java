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
 * The interface for doing hit detection on graphics nodes..
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public interface GraphicsNodeHitDetector {

    /**
     * Returns true if the specified node is sensitive to events at point p,
     * false otherwise.
     * @param node the targetted node
     * @param p the point to check
     */
    public boolean isHit(GraphicsNode target, Point2D p);

}
