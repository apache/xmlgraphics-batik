/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Shape;

/**
 * A graphics node that represents a shape.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface ShapeNode extends LeafGraphicsNode {

    /**
     * Sets the shape of this shape node.
     * @param newShape the new shape of this shape node
     */
    void setShape(Shape newShape);

    /**
     * Returns the shape of this shape node.
     * @return the shape of this shape node
     */
    Shape getShape();

    /**
     * Sets the ShapePainter used by this shape node to render the shape.
     * @param newShapePainter the new ShapePainter to use
     */
    void setShapePainter(ShapePainter newShapePainter);

    /**
     * Returns the ShapePainter used by this shape node to render the shape.
     * @return the ShapePainter used to render the shape
     */
    ShapePainter getShapePainter();

}
