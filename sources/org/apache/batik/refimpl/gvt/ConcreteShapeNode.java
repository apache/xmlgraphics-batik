/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

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
     * Constructs a new empty shape node.
     */
    public ConcreteShapeNode() {}

    //
    // Properties methods
    //

    public void setShape(Shape newShape) {
        Shape oldShape = shape;
        this.shape = newShape;
        firePropertyChange("shape", oldShape, newShape);
    }

    public Shape getShape() {
        return shape;
    }

    public void setShapePainter(ShapePainter newShapePainter) {
        ShapePainter oldShapePainter = shapePainter;
        this.shapePainter = newShapePainter;
        firePropertyChange("shapePainter",
                               oldShapePainter, newShapePainter);
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
        if (shapePainter == null) {
            return;
        }
        // Save default rendering attributes
        AffineTransform defaultTransform = g2d.getTransform();
        RenderingHints defaultHints = g2d.getRenderingHints();
        // Apply the rendering attributes of this node
        if (transform != null) {
            g2d.transform(transform);
        }
        if (hints != null) {
            g2d.addRenderingHints(hints);
        }
        // Paint the shape
        shapePainter.paint(shape, g2d, rc);
        // Restore default rendering attributes
        g2d.setTransform(defaultTransform);
        g2d.setRenderingHints(defaultHints);
    }

    //
    // Geometric methods
    //

    public Rectangle2D getBounds() {
        // <!> FIXME : TODO At the moment this is the same algorithms as getRenderBounds
        Rectangle2D usrBounds = shape.getBounds2D();
        if(transform != null){
            usrBounds 
                = transform.createTransformedShape(usrBounds).getBounds2D();
        }

        return usrBounds;
    }

    public Rectangle2D getRenderBounds() {
        Rectangle2D usrBounds = shape.getBounds2D();
        if(transform != null){
            usrBounds 
                = transform.createTransformedShape(usrBounds).getBounds2D();
        }

        return usrBounds;
    }

    public Shape getOutline() {
        // <!> FIXME : TODO
        // may not be the intended behavior for stroked shapes, since the outline
        // does not include the rendered extents
        return shape;
    }
}
