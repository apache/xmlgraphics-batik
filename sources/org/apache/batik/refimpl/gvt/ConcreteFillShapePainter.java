/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.Paint;

import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

/**
 * The default implementation of the <tt>FillShapePainter</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ConcreteFillShapePainter implements FillShapePainter {

    /** The paint attribute used to fill the shape. */
    protected Paint paint;

    /**
     * Constructs a new <tt>ShapePainter</tt> that can be used to fill
     * a <tt>Shape</tt>.
     */
    public ConcreteFillShapePainter() {}

    public void setPaint(Paint newPaint) {
        this.paint = newPaint;
    }

    public void paint(Shape shape,
                      Graphics2D g2d,
                      GraphicsNodeRenderContext ctx) {
        if (paint != null) {
            g2d.setPaint(paint);
            g2d.fill(shape);
        }
    }
}
