/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.gvt;

/**
 * Renders the shape of a <tt>ShapeNode</tt> with multiple
 * <tt>ShapePainter</tt>s. ShapePainters are invoked in the same order
 * they were added.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface CompositeShapePainter extends ShapePainter {

    /**
     * Adds the specified shape painter to this composite shape painter.
     * @param shapePainter the shape painter to add
     */
    void addShapePainter(ShapePainter shapePainter);

}
