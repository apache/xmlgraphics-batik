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
     * Internal Cache: Sensitive bounds
     */
    private Rectangle2D sensitiveBounds;

    /**
     * Internal Cache: The painted area.
     */
    private Shape paintedArea;

    /**
     * Internal Cache: The sensitive area.
     */
    private Shape sensitiveArea;

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
        fireGraphicsNodeChangeStarted();
        invalidateGeometryCache();
        this.shape = newShape;
        if(this.shapePainter != null){
            if (newShape != null) {
                this.shapePainter.setShape(newShape);
            } else {
                this.shapePainter = null;
            }
        }
        fireGraphicsNodeChangeCompleted();
    }

    /**
     * Returns the shape of this <tt>ShapeNode</tt>.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Sets the <tt>ShapePainter</tt> used by this shape node to render its
     * shape.
     *
     * @param newShapePainter the new ShapePainter to use
     */
    public void setShapePainter(ShapePainter newShapePainter) {
        fireGraphicsNodeChangeStarted();
        invalidateGeometryCache();
        this.shapePainter = newShapePainter;
        if(shapePainter != null && shape != this.shapePainter.getShape()){
            shapePainter.setShape(shape);
        }
        fireGraphicsNodeChangeCompleted();
    }

    /**
     * Returns the <tt>ShapePainter</tt> used by this shape node to render its
     * shape.
     */
    public ShapePainter getShapePainter() {
        return shapePainter;
    }

    //
    // Drawing methods
    //

    /**
     * Paints this node.
     *
     * @param g2d the Graphics2D to use
     */
    public void paint(Graphics2D g2d) {
        if (isVisible) {
            super.paint(g2d);
        }

    }

    /**
     * Paints this node without applying Filter, Mask, Composite, and clip.
     *
     * @param g2d the Graphics2D to use
     */
    public void primitivePaint(Graphics2D g2d) {
        if (shapePainter != null) {
            shapePainter.paint(g2d);
        }
    }

    //
    // Geometric methods
    //

    /**
     * Invalidates this <tt>ShapeNode</tt>. This node and all its ancestors have
     * been informed that all its cached values related to its bounds must be
     * recomputed.
     */
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        primitiveBounds = null;
        geometryBounds = null;
        sensitiveBounds = null;
        paintedArea = null;
        sensitiveArea = null;
    }

    /**
     * Returns true if the specified Point2D is inside the boundary of this
     * node, false otherwise.
     *
     * @param p the specified Point2D in the user space
     */
    public boolean contains(Point2D p) {
        switch(pointerEventType) {
        case VISIBLE_PAINTED:
        case VISIBLE_FILL:
        case VISIBLE_STROKE:
        case VISIBLE:
            if (!isVisible) return false;
            // Fall Through
        case PAINTED:
        case FILL:
        case STROKE:
        case ALL: {
            Rectangle2D b = getSensitiveBounds();
            if (b == null || !b.contains(p))
                return false;

            Shape s = getSensitiveArea();
            if (s == null)  return false;
            return s.contains(p);
        }
        case NONE:
        default:
            return false;
        }
    }

    /**
     * Returns true if the interior of this node intersects the interior of a
     * specified Rectangle2D, false otherwise.
     *
     * @param r the specified Rectangle2D in the user node space
     */
    public boolean intersects(Rectangle2D r) {
        Rectangle2D b = getBounds();
        if (b != null) {
            return (b.intersects(r) &&
                    paintedArea != null &&
                    paintedArea.intersects(r));
        }
        return false;
    }

    /**
     * Returns the bounds of the area covered by this node's primitive paint.
     */
    public Rectangle2D getPrimitiveBounds() {
        if (primitiveBounds == null) {
            if ((shape == null) || (shapePainter == null)) {
                return null;
            }
            // paintedArea = shapePainter.getPaintedArea();
            // primitiveBounds = paintedArea.getBounds2D();
            primitiveBounds = shapePainter.getPaintedBounds2D();

            // Make sure we haven't been interrupted
            if (Thread.currentThread().isInterrupted()) {
                // The Thread has been interrupted. Invalidate
                // any cached values and proceed.
                invalidateGeometryCache();
            }
        }
        return primitiveBounds;
    }

    /**
     * Returns the bounds of the sensitive area covered by this node,
     * This includes the stroked area but does not include the effects
     * of clipping, masking or filtering.
     */
    public Rectangle2D getSensitiveBounds() {
        if (sensitiveBounds != null)
            return sensitiveBounds;

        Shape sensitive = getSensitiveArea();
        if (sensitive == null) return null;
        sensitiveBounds = sensitive.getBounds2D();
        return sensitiveBounds;
    }

    /**
     * Returns the shape that represents the sensitive area of this graphics
     * node.
     */
    public Shape getSensitiveArea() {
        if (sensitiveArea != null) 
            return sensitiveArea;

        // <!> NOT REALLY NICE CODE BUT NO OTHER WAY
        ShapePainter strokeShapePainter = null;
        ShapePainter fillShapePainter = null;
        if (shapePainter instanceof StrokeShapePainter) {
            strokeShapePainter = shapePainter;
        } else if (shapePainter instanceof FillShapePainter) {
            fillShapePainter = shapePainter;
        } else {
            CompositeShapePainter cp = (CompositeShapePainter)shapePainter;
            for (int i=0; i < cp.getShapePainterCount(); ++i) {
                ShapePainter sp = cp.getShapePainter(i);
                if (sp instanceof StrokeShapePainter) {
                    strokeShapePainter = sp;
                } else if (sp instanceof FillShapePainter) {
                    fillShapePainter = sp;
                }
            }
        }

        switch(pointerEventType) {
        case VISIBLE_PAINTED:
        case PAINTED:
            sensitiveArea = shapePainter.getPaintedArea();
            break;
        case VISIBLE_FILL:
        case FILL:
            if (fillShapePainter != null) {
                sensitiveArea = fillShapePainter.getSensitiveArea();
            }
            break;
        case VISIBLE_STROKE:
        case STROKE:
            if (strokeShapePainter != null) {
                sensitiveArea = strokeShapePainter.getSensitiveArea();
            }
            break;
        case VISIBLE:
        case ALL:
            sensitiveArea = shapePainter.getSensitiveArea();
            break;
        case NONE:
        default:
            // nothing to tdo
        }
        return sensitiveArea;
    }

    /**
     * Returns the bounds of the area covered by this node, without taking any
     * of its rendering attribute into account. i.e., exclusive of any clipping,
     * masking, filtering or stroking, for example.
     */
    public Rectangle2D getGeometryBounds(){
        if (geometryBounds == null) {
            if (shape == null) {
                return null;
            }
            geometryBounds = normalizeRectangle(shape.getBounds2D());
        }
        return geometryBounds;
    }

    /**
     * Returns the outline of this node.
     */
    public Shape getOutline() {
        return shape;
    }
}
