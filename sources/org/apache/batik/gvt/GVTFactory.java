/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

/**
 * The <tt>GVTFactory</tt> implementation.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class GVTFactory {

    private static GVTFactory singleton = new GVTFactory();

    /**
     * Creates the GVTFactory instance.
     */
    public static GVTFactory getGVTFactoryImplementation() {
        return singleton;
    }

    /**
     * No instance of this class
     */
    protected GVTFactory() {}

    //
    // Methods for vending GraphicsNode objects
    //

    public CanvasGraphicsNode createCanvasGraphicsNode() {
        return new CanvasGraphicsNode();
    }

    public CompositeGraphicsNode createCompositeGraphicsNode() {
        return new CompositeGraphicsNode();
    }

    public ShapeNode createShapeNode() {
        return new ShapeNode();
    }

    public TextNode createTextNode() {
        return new TextNode();
    }

    public RootGraphicsNode createRootGraphicsNode() {
        return new RootGraphicsNode();
    }

    public ImageNode createImageNode() {
        return new ImageNode();
    }

    public RasterImageNode createRasterImageNode() {
        return new RasterImageNode();
    }

    //
    // Methods for vending rendering objects
    //

    public StrokeShapePainter createStrokeShapePainter() {
        return new StrokeShapePainter();
    }

    public FillShapePainter createFillShapePainter() {
        return new FillShapePainter();
    }

    public CompositeShapePainter createCompositeShapePainter() {
        return new CompositeShapePainter();
    }
}
