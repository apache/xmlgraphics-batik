/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

/**
 * Factory class for vending GVT objects.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface GVTFactory {

    //
    // Methods for vending GraphicsNode objects
    //

    /**
     * Creates a new <tt>CanvasGraphicsNode</tt>.
     */
    CanvasGraphicsNode createCanvasGraphicsNode();

    /**
     * Creates a new <tt>CompositeGraphicsNode</tt>.
     */
    CompositeGraphicsNode createCompositeGraphicsNode();

    /**
     * Creates a new <tt>ShapeNode</tt>.
     */
    ShapeNode createShapeNode();

    /**
     * Creates a new <tt>TextNode</tt>.
     */
    TextNode createTextNode();

    /**
     * Creates a new <tt>RootGraphicsNode</tt>.
     */
    RootGraphicsNode createRootGraphicsNode();

    /**
     * Creates a new <tt>ImageNode</tt>.
     */
    ImageNode createImageNode();

    /**
     * Creates a new <tt>RasterImageNode</tt>.
     */
    RasterImageNode createRasterImageNode();

    /**
     * Creates a new <tt>ProxyGraphicsNode</tt>.
     */
    ProxyGraphicsNode createProxyGraphicsNode();

    //
    // Methods for vending rendering objects
    //

    /**
     * Creates a new <tt>StrokeShapePainter</tt>.
     */
    StrokeShapePainter createStrokeShapePainter();

    /**
     * Creates a new <tt>FillShapePainter</tt>.
     */
    FillShapePainter createFillShapePainter();

    /**
     * Creates a new <tt>CompositeShapePainter</tt>.
     */
    CompositeShapePainter createCompositeShapePainter();

}
