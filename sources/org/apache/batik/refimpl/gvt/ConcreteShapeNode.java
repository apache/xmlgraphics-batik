/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;

/**
 * An implementation of the <tt>ShapeNode</tt> interface.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ConcreteShapeNode extends AbstractGraphicsNode
    implements ShapeNode {

    /**
     * The shape that describes this <tt>ShapeNode</tt>.
     */
    protected Shape shape;

    /**
     * The shape painter used to paint the shape of this shape node.
     */
    protected ShapePainter shapePainter;

    /**
     * Cache: Primitive bounds
     */
    private Rectangle2D primitiveBounds;

    /**
     * Cache: Geometry bounds
     */
    private Rectangle2D geometryBounds;

    /**
     * Cache: The painted area.
     */
    private Shape paintedArea;

    /**
     * Constructs a new empty shape node.
     */
    public ConcreteShapeNode() {}

    //
    // Properties methods
    //

    public void setShape(Shape newShape) {
        //Rectangle2D oldBounds = getBounds();
        invalidateGeometryCache();
        Shape oldShape = shape;
        this.shape = newShape;
        firePropertyChange("shape", oldShape, newShape);
        //fireGraphicsNodePaintListener(oldBounds);
    }

    public Shape getShape() {
        return shape;
    }

    public void setShapePainter(ShapePainter newShapePainter) {
        //Rectangle2D oldBounds = getBounds();
        invalidateGeometryCache();
        ShapePainter oldShapePainter = shapePainter;
        this.shapePainter = newShapePainter;
        firePropertyChange("shapePainter",
                               oldShapePainter, newShapePainter);
        //fireGraphicsNodePaintListener(oldBounds);
    }

    public ShapePainter getShapePainter() {
        return shapePainter;
    }

    //
    // Drawing methods
    //

    public boolean hasProgressivePaint() {
        // <!> FIXME : TODO
        throw new Error("Not yet implemented");
    }

    public void progressivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        // <!> FIXME : TODO
        throw new Error("Not yet implemented");
    }

    public void primitivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        if (shapePainter != null) {
            shapePainter.paint(shape, g2d, rc);
        }
    }

    //
    // Geometric methods
    //

    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        primitiveBounds = null;
        geometryBounds = null;
        paintedArea = null;
    }

    public boolean contains(Point2D p, GraphicsNodeRenderContext rc) {
        Rectangle2D b = getBounds(rc);
        if (b != null) {
            return (b.contains(p) &&
                    paintedArea != null &&
                    paintedArea.contains(p));
        }
        return false;
    }

    public boolean intersects(Rectangle2D r, GraphicsNodeRenderContext rc) {
        Rectangle2D b = getBounds(rc);
        if (b != null) {
            return (b.intersects(r) &&
                    paintedArea != null &&
                    paintedArea.intersects(r));
        }
        return false;
    }

    public Rectangle2D getPrimitiveBounds(GraphicsNodeRenderContext rc) {
        if (primitiveBounds == null) {
            if ((shape == null) || (shapePainter == null)) {
                return null;
            }
            paintedArea = shapePainter.getPaintedArea(shape);
            primitiveBounds = paintedArea.getBounds2D();
        }
        return primitiveBounds;
    }

    public Rectangle2D getGeometryBounds(GraphicsNodeRenderContext rc){
        if (geometryBounds == null) {
            if (shape == null) {
                return null;
            }
            geometryBounds = shape.getBounds2D();
        }
        return geometryBounds;
    }

    public Shape getOutline(GraphicsNodeRenderContext rc) {
        return shape;
    }
}
