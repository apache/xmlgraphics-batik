/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.geom;

import java.awt.Shape;

/**
 * The <code>ExtendedShape</code> class represents a geometric
 * path constructed from straight lines, quadratic and cubic (Bézier)
 * curves and elliptical arcs.
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public interface ExtendedShape extends Shape {
    /**
     * Get an extended Path iterator that may return SEG_ARCTO commands
     */
    public ExtendedPathIterator getExtendedPathIterator();

}
