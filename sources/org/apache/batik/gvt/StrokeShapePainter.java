/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Stroke;
import java.awt.Paint;

/**
 * Renders the shape of a <tt>ShapeNode</tt> using a <tt>Stroke</tt>
 * and a <tt>Paint</tt> that decorate the outline of the shape.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface StrokeShapePainter extends ShapePainter {

    /**
     * Sets the stroke of this shape painter.
     * @param newStroke the new stroke of this shape painter
     */
    void setStroke(Stroke newStroke);

    /**
     * Sets the paint used to draw the outline of the shape.
     * @param newPaint the new paint used to draw the outline of the shape
     */
    void setPaint(Paint newPaint);
}
