/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Paint;

/**
 * Renders the shape of a <tt>ShapeNode</tt> using a <tt>Paint</tt>
 * object that defines color patterns of the shape.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface FillShapePainter extends ShapePainter {

    /**
     * Sets the paint of this shape painter.
     * @param newPaint the new paint of this shape painter
     */
    void setPaint(Paint newPaint);

}
