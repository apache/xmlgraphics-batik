/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Dimension2D;

/**
 * Used to transform dimensions into user space coordinates.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface DimensionTransformer {

    /**
     * Converts the input width to a width in user space
     */
    float widthToUserSpace(float width);

    /**
     * Converts the input height to a height in user space
     */
    float heightToUserSpace(float height);
}
