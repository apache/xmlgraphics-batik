/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A shape with a additional transform.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class TransformedShape implements Shape {

    /**
     * The shape that defines this <tt>TransformedShape</tt>.
     */
    private Shape shape;

    /**
     * The Transformer used to transform shape
     */
    private AffineTransformSource transformer;

    /**
     * Cache: The transformed shape
     */
    private Shape transformedShape;

    /**
     * Constructs a new shape.
     */
    public TransformedShape(Shape shape, AffineTransformSource transformer) {
        if(shape == null){
            throw new IllegalArgumentException();
        }

        this.shape = shape;

        if(transformer == null){
            throw new IllegalArgumentException();
        }

        this.transformer = transformer;
    }

    public Rectangle getBounds() {
        if (transformedShape == null) {
            AffineTransform t = transformer.getTransform();
            transformedShape = t.createTransformedShape(shape);
        }
        return transformedShape.getBounds();
    }

    public Rectangle2D getBounds2D() {
        if (transformedShape == null) {
            AffineTransform t = transformer.getTransform();
            transformedShape = t.createTransformedShape(shape);
        }
        return transformedShape.getBounds2D();
    }

    public boolean contains(double x, double y) {
        if (transformedShape == null) {
            AffineTransform t = transformer.getTransform();
            transformedShape = t.createTransformedShape(shape);
        }
        return transformedShape.contains(x, y);
    }

    public boolean contains(Point2D p) {
        if (transformedShape == null) {
            AffineTransform t = transformer.getTransform();
            transformedShape = t.createTransformedShape(shape);
        }
        return transformedShape.contains(p);
    }

    public boolean intersects(double x, double y, double w, double h) {
        if (transformedShape == null) {
            AffineTransform t = transformer.getTransform();
            transformedShape = t.createTransformedShape(shape);
        }
        return transformedShape.intersects(x, y, w, h);
    }

    public boolean intersects(Rectangle2D r) {
        if (transformedShape == null) {
            AffineTransform t = transformer.getTransform();
            transformedShape = t.createTransformedShape(shape);
        }
        return transformedShape.intersects(r);
    }

    public boolean contains(double x, double y, double w, double h) {
        if (transformedShape == null) {
            AffineTransform t = transformer.getTransform();
            transformedShape = t.createTransformedShape(shape);
        }
        return transformedShape.contains(x, y, w, h);
    }

    public boolean contains(Rectangle2D r) {
        if (transformedShape == null) {
            AffineTransform t = transformer.getTransform();
            transformedShape = t.createTransformedShape(shape);
        }
        return transformedShape.contains(r);
    }

    public PathIterator getPathIterator(AffineTransform at) {
        if (transformedShape == null) {
            AffineTransform t = transformer.getTransform();
            transformedShape = t.createTransformedShape(shape);
        }
        return transformedShape.getPathIterator(at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        if (transformedShape == null) {
            AffineTransform t = transformer.getTransform();
            transformedShape = t.createTransformedShape(shape);
        }
        return transformedShape.getPathIterator(at, flatness);
    }

}
