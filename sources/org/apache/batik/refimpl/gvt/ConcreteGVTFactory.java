/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import org.apache.batik.gvt.*;

/**
 * The <tt>GVTFactory</tt> implementation.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ConcreteGVTFactory implements GVTFactory {

    private static GVTFactory singleton = new ConcreteGVTFactory();

    /**
     * Creates the GVTFactory instance.
     */
    public static GVTFactory getGVTFactoryImplementation() {
        return singleton;
    }

    /**
     * No instance of this class
     */
    protected ConcreteGVTFactory() {}

    //
    // Methods for vending GraphicsNode objects
    //

    public CanvasGraphicsNode createCanvasGraphicsNode() {
        return new ConcreteCanvasGraphicsNode();
    }

    public CompositeGraphicsNode createCompositeGraphicsNode() {
        return new ConcreteCompositeGraphicsNode();
    }

    public ShapeNode createShapeNode() {
        return new ConcreteShapeNode();
    }

    public TextNode createTextNode() {
        return new ConcreteTextNode();
    }

    public RootGraphicsNode createRootGraphicsNode() {
        return new ConcreteRootGraphicsNode();
    }

    public ImageNode createImageNode() {
        return new ConcreteImageNode();
    }

    public RasterImageNode createRasterImageNode() {
        return new ConcreteRasterImageNode();
    }

    public ProxyGraphicsNode createProxyGraphicsNode() {
        throw new Error("Not yet implemented");
    }

    //
    // Methods for vending rendering objects
    //

    public StrokeShapePainter createStrokeShapePainter() {
        return new ConcreteStrokeShapePainter();
    }

    public FillShapePainter createFillShapePainter() {
        return new ConcreteFillShapePainter();
    }

    public CompositeShapePainter createCompositeShapePainter() {
        return new ConcreteCompositeShapePainter();
    }
}
