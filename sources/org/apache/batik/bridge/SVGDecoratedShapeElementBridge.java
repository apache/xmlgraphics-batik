/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Shape;

import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.w3c.dom.Element;

/**
 * The base bridge class for decorated shapes. Decorated shapes can be
 * filled, stroked and can have markers.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class SVGDecoratedShapeElementBridge
        extends SVGShapeElementBridge {

    /**
     * Constructs a new bridge for SVG decorated shapes.
     */
    protected SVGDecoratedShapeElementBridge() {}

    /**
     * Creates the shape painter associated to the specified element.
     * This implementation creates a shape painter considering the
     * various fill and stroke properties in addition to the marker
     * properties.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the shape painter to use
     * @param shapeNode the shape node that is interested in its shape painter
     */
    protected ShapePainter createShapePainter(BridgeContext ctx,
                                              Element e,
                                              ShapeNode shapeNode) {
        // 'fill'
        // 'fill-opacity'
        // 'stroke'
        // 'stroke-opacity',
        // 'stroke-width'
        // 'stroke-linecap'
        // 'stroke-linejoin'
        // 'stroke-miterlimit'
        // 'stroke-dasharray'
        // 'stroke-dashoffset'
        ShapePainter fillAndStroke
            = super.createShapePainter(ctx, e, shapeNode);

        // marker-start
        // marker-mid
        // marker-end
        ShapePainter markerPainter =
            PaintServer.convertMarkers(e, shapeNode, ctx);

        Shape shape = shapeNode.getShape();
        ShapePainter painter;

        if (markerPainter != null) {
            if (fillAndStroke != null) {
                CompositeShapePainter cp = new CompositeShapePainter(shape);
                cp.addShapePainter(fillAndStroke);
                cp.addShapePainter(markerPainter);
                painter = cp;
            } else {
                painter = markerPainter;
            }
        } else {
            painter = fillAndStroke;
        }
        return painter;
    }
}
