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
 * No-op. Considers width/height to be in user space already.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class DimensionTransformerIdentity implements DimensionTransformer {

    /**
     * Converts the input width to a width in user space
     */
    public float widthToUserSpace(float width){
        return width;
    }

    /**
     * Converts the input height to a height in user space
     */
    public float heightToUserSpace(float height){
        return height;
    }
}
