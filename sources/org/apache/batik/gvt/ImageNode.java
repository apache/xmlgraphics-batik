/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;

/**
 * A graphics node that represents an image described as a graphics node.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface ImageNode extends LeafGraphicsNode {

    /**
     * Sets the image of this image node.
     * @param newImage the new image of this image node
     */
    void setImage(GraphicsNode newImage);

    /**
     * Returns the image of this image node.
     * @return the image of this image node
     */
    GraphicsNode getImage();

}
