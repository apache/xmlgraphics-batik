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
import java.awt.geom.GeneralPath;
import java.awt.geom.Area;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

/**
 * The default implementation of the <tt>CompositeShapePainter</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ConcreteCompositeShapePainter implements CompositeShapePainter {

    /** The enclosed <tt>ShapePainter</tt>s of this composite shape painter. */
    protected ShapePainter [] painters;
    /** The number of shape painter. */
    protected int count;

    /**
     * Constructs a new empty composite <tt>ShapePainter</tt>.
     */
    public ConcreteCompositeShapePainter() {}

    public void addShapePainter(ShapePainter shapePainter) {
        if (shapePainter == null) {
            throw new IllegalArgumentException("ShapePainter can't be null");
        }
        if (painters == null) {
            painters = new ShapePainter[2];
        }
        if (count == painters.length) {
            ShapePainter [] newPainters = new ShapePainter[(count*3)/2 + 1];
            System.arraycopy(painters, 0, newPainters, 0, count);
            painters = newPainters;
        }
        painters[count++] = shapePainter;
    }

    public void paint(Shape shape,
                      Graphics2D g2d,
                      GraphicsNodeRenderContext ctx) {
        if (painters != null) {
            for (int i=0; i < count; ++i) {
                painters[i].paint(shape, g2d, ctx);
            }
        }
    }

    public Shape getPaintedArea(Shape shape){
        // <!> FIX ME: Use of GeneralPath is a work around Area problems.
        GeneralPath paintedArea = new GeneralPath();
        if (painters != null) {
            for (int i=0; i < count; ++i) {
                paintedArea.append(painters[i].getPaintedArea(shape), false);
            }
        }
        return paintedArea;
    }
}
