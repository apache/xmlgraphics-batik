/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.gvt;

import java.awt.*;
import java.awt.geom.*;

import org.apache.batik.gvt.*;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.refimpl.gvt.ConcreteGVTFactory;

/**
 * A simple demo.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public class SimpleDemo implements GVTDemoSetup {

    public GraphicsNode createGraphicsNode() {
        GVTFactory f = ConcreteGVTFactory.getGVTFactoryImplementation();
        CanvasGraphicsNode canvas = f.createCanvasGraphicsNode();
        canvas.setBackgroundPaint(Color.white);

        Shape shape;
        ShapeNode shapeNode;
        FillShapePainter fillPainter;
        StrokeShapePainter strokePainter;
        CompositeShapePainter shapePainter;

        //
        // Build a simple rectangle with 'stroke', 'fill', 'transform'
        // and 'rendering hints' attributes
        //

        shape = new Rectangle2D.Float(10, 10, 100, 50);

        strokePainter = f.createStrokeShapePainter();
        strokePainter.setStroke(new BasicStroke(4f));
        strokePainter.setPaint(Color.black);

        fillPainter = f.createFillShapePainter();
        fillPainter.setPaint(Color.blue);

        shapePainter = f.createCompositeShapePainter();
        shapePainter.addShapePainter(fillPainter);
        shapePainter.addShapePainter(strokePainter);

        shapeNode = f.createShapeNode();
        shapeNode.setShape(shape);
        shapeNode.setShapePainter(shapePainter);
        shapeNode.setTransform(AffineTransform.getRotateInstance(Math.PI/3,
                                                                 55,
                                                                 30));
        shapeNode.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                   RenderingHints.VALUE_ANTIALIAS_ON);
        canvas.getChildren().add(shapeNode);

        //
        // Build an ellipse with 'fill' attribute
        //

        shape = new Ellipse2D.Float(100, 70, 75, 90);

        fillPainter = f.createFillShapePainter();
        fillPainter.setPaint(Color.red);

        shapeNode = f.createShapeNode();
        shapeNode.setShape(shape);
        shapeNode.setShapePainter(fillPainter);

        canvas.getChildren().add(shapeNode);

        return canvas;
    }

    public GraphicsNodeRenderContext createGraphicsContext() {
        return null; // FIXME ??? or maybe OK for this simple demo
    }

    public EventDispatcher createEventDispatcher() {
        return null; // FIXME ??? or maybe OK for this simple demo
    }

}
