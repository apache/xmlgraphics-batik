/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A shape painter which consists of multiple shape painters.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class CompositeShapePainter implements ShapePainter {

    /**
     * The shape associated with this painter
     */
    protected Shape shape;

    /**
     * The enclosed <tt>ShapePainter</tt>s of this composite shape painter.
     */
    protected ShapePainter [] painters;

    /**
     * The number of shape painter.
     */
    protected int count;

    /**
     * Constructs a new empty <tt>CompositeShapePainter</tt>.
     */
    public CompositeShapePainter(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }

    /**
     * Adds the specified shape painter to the shape painter..
     *
     * @param shapePainter the shape painter to add
     */
    public void addShapePainter(ShapePainter shapePainter) {
        if (shapePainter == null) {
            return;
        }
        if (this.shape != shapePainter.getShape()) {
            shapePainter.setShape(shape);
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

    /**
     * Returns the shape painter at the specified index.
     *
     * @param index the index of the shape painter to return
     */
    public ShapePainter getShapePainter(int index) {
        return painters[index];
    }

    /**
     * Returns the number of shape painter of this composite shape painter.
     */
    public int getShapePainterCount() {
        return count;
    }

    /**
     * Paints the specified shape using the specified Graphics2D.
     *
     * @param g2d the Graphics2D to use
     */
    public void paint(Graphics2D g2d) {
        if (painters != null) {
            for (int i=0; i < count; ++i) {
                painters[i].paint(g2d);
            }
        }
    }

    /**
     * Returns the area painted by this shape painter.
     */
    public Shape getPaintedArea(){
        if (painters != null) {
            Area paintedArea = new Area();
            for (int i=0; i < count; ++i) {
                Shape s = painters[i].getPaintedArea();
                if (s != null) {
                    paintedArea.add(new Area(s));
                }
            }
            return paintedArea;
        } else {
            return null;
        }
    }

    /**
     * Returns the bounds of the area painted by this shape painter
     */
    public Rectangle2D getPaintedBounds2D(){
        if (painters != null) {
            GeneralPath paintedArea = new GeneralPath();
            for (int i=0; i < count; ++i) {
                Shape s = painters[i].getPaintedArea();
                if (s != null) {
                    paintedArea.append(s, false);
                }
            }
            return paintedArea.getBounds2D();
        } else {
            return null;
        }
    }


    /**
     * Sets the Shape this shape painter is associated with.
     *
     * @param shape new shape this painter should be associated with.
     * Should not be null.
     */
    public void setShape(Shape shape){
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        if (painters != null) {
            for (int i=0; i < count; ++i) {
                painters[i].setShape(shape);
            }
        }
        this.shape = shape;
    }

    /**
     * Gets the Shape this shape painter is associated with.
     *
     * @return shape associated with this painter
     */
    public Shape getShape(){
        return shape;
    }
}
