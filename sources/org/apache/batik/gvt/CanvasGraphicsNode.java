/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Paint;
import java.awt.geom.Dimension2D;

/**
 * The top-level graphics node with a background color.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface CanvasGraphicsNode extends CompositeGraphicsNode {

    /**
     * Sets the background paint of this canvas graphics node.
     * @param newBackgroundPaint the new background paint
     */
    void setBackgroundPaint(Paint newBackgroundPaint);

    /**
     * Returns the background paint of this canvas graphics node.
     * @return the background paint
     */
    Paint getBackgroundPaint();

    /**
     * Sets the size of this canvas graphics node.
     * @param newSize the new size of this canvas graphics node
     */
    void setSize(Dimension2D newSize);

    /**
     * Returns the size  of this canvas graphics node.
     * @return the size of this canvas graphics node
     */
    Dimension2D getSize();

}
