/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A graphics node that represents a shape.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ShapeNode extends AbstractGraphicsNode {

    /**
     * The shape that describes this <tt>ShapeNode</tt>.
     */
    protected Shape shape;

    /**
     * The shape painter used to paint the shape of this shape node.
     */
    protected ShapePainter shapePainter;

    /**
     * Internal Cache: Primitive bounds
     */
    private Rectangle2D primitiveBounds;

    /**
     * Internal Cache: Geometry bounds
     */
    private Rectangle2D geometryBounds;

    /**
     * Internal Cache: The painted area.
     */
    private Shape paintedArea;

    /**
     * Constructs a new empty <tt>ShapeNode</tt>.
     */
    public ShapeNode() {}

    //
    // Properties methods
    //

    /**
     * Sets the shape of this <tt>ShapeNode</tt>.
     *
     * @param newShape the new shape of this shape node
     */
    public void setShape(Shape newShape) {
        invalidateGeometryCache();
        this.shape = newShape;
        if(this.shapePainter != null){
            this.shapePainter.setShape(newShape);
        }
    }

    /**
     * Returns the shape of this <tt>ShapeNode</tt>.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Sets the <tt>ShapePainter</tt> used by this shape node to
     * render its shape.
     *
     * @param newShapePainter the new ShapePainter to use
     */
    public void setShapePainter(ShapePainter newShapePainter) {
        invalidateGeometryCache();
        this.shapePainter = newShapePainter;
        if(shape != this.shapePainter.getShape()){
            shapePainter.setShape(shape);
        }
    }

    /**
     * Returns the <tt>ShapePainter</tt> used by this shape node to
     * render its shape.
     */
    public ShapePainter getShapePainter() {
        return shapePainter;
    }

    //
    // Drawing methods
    //

    /**
     * Paints this node if visible.
     *
     * @param g2d the Graphics2D to use
     * @param rc the GraphicsNodeRenderContext to use
     * @exception InterruptedException thrown if the current thread
     * was interrupted during paint
     */
    public void paint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        if (isVisible) {
            super.paint(g2d, rc);
        }

    }

    /**
     * Paints this node without applying Filter, Mask, Composite and clip.
     *
     * @param g2d the Graphics2D to use
     * @param rc the GraphicsNodeRenderContext to use
     */
    public void primitivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        if (shapePainter != null) {
            shapePainter.paint(g2d, rc);
        }
    }

    //
    // Geometric methods
    //

    /**
     * Invalidates this <tt>ShapeNode</tt>. This node and all its
     * ancestors have been informed that all its cached values related
     * to its bounds must be recomputed.
     */
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        primitiveBounds = null;
        geometryBounds = null;
        paintedArea = null;
    }

    /**
     * Tests if the specified Point2D is inside the boundary of this node.
     * <b>Note</b>: The boundaries of some nodes (notably, text element nodes)
     * cannot be precisely determined independent of their
     * GraphicsNodeRenderContext.
     *
     * @param p the specified Point2D in the user space
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     * @return true if the coordinates are inside, false otherwise
     */
    public boolean contains(Point2D p, GraphicsNodeRenderContext rc) {
        Rectangle2D b = getBounds(rc);
        if (b != null) {
            return (b.contains(p) &&
                    paintedArea != null &&
                    paintedArea.contains(p));
        }
        return false;
    }

    /**
     * Tests if the interior of this node intersects the interior of a
     * specified Rectangle2D.
     * <b>Note</b>: The boundaries of some nodes (notably, text element nodes)
     * cannot be precisely determined independent of their
     * GraphicsNodeRenderContext.
     *
     * @param r the specified Rectangle2D in the user node space
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     * @return true if the rectangle intersects, false otherwise
     */
    public boolean intersects(Rectangle2D r, GraphicsNodeRenderContext rc) {
        Rectangle2D b = getBounds(rc);
        if (b != null) {
            return (b.intersects(r) &&
                    paintedArea != null &&
                    paintedArea.intersects(r));
        }
        return false;
    }

    /**
     * Returns the bounds of the area covered by this node's primitive paint.
     * <b>Note</b>: The boundaries of some nodes (notably, text element nodes)
     * cannot be precisely determined independent of their
     * GraphicsNodeRenderContext.
     *
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     */
    public Rectangle2D getPrimitiveBounds(GraphicsNodeRenderContext rc) {
        if (primitiveBounds == null) {
            if ((shape == null) || (shapePainter == null)) {
                return null;
            }
            paintedArea = shapePainter.getPaintedArea(rc);
            primitiveBounds = paintedArea.getBounds2D();
        }
        return primitiveBounds;
    }

    /**
     * Returns the bounds of the area covered by this <tt>ShapeNode</tt>,
     * without taking any of its rendering attribute into account.
     * (i.e., exclusive of any clipping, masking, filtering or stroking...)
     * <b>Note</b>: The boundaries of some nodes (notably, text element nodes)
     * cannot be precisely determined independent of their
     * GraphicsNodeRenderContext.
     *
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     */
    public Rectangle2D getGeometryBounds(GraphicsNodeRenderContext rc){
        if (geometryBounds == null) {
            if (shape == null) {
                return null;
            }
            geometryBounds = shape.getBounds2D();
        }
        return geometryBounds;
    }

    /**
     * Returns the outline of this <tt>ShapeNode</tt>.
     *
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     * @return the outline of this node
     */
    public Shape getOutline(GraphicsNodeRenderContext rc) {
        return shape;
    }
}
