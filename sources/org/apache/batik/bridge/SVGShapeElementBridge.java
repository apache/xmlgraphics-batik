/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.RenderingHints;

import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.w3c.dom.Element;

/**
 * The base bridge class for shapes. Subclasses bridge <tt>ShapeNode</tt>.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class SVGShapeElementBridge extends AbstractGraphicsNodeBridge {

    /**
     * Constructs a new bridge for SVG shapes.
     */
    protected SVGShapeElementBridge() {}

    /**
     * Creates a graphics node using the specified BridgeContext and
     * for the specified element.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        ShapeNode shapeNode = (ShapeNode)super.createGraphicsNode(ctx, e);
	if (shapeNode == null) {
	    return null;
	}
        // delegates to subclasses the shape construction
        buildShape(ctx, e, shapeNode);

        // 'shape-rendering' and 'color-rendering'
        RenderingHints hints = CSSUtilities.convertShapeRendering(e, null);
        hints = CSSUtilities.convertColorRendering(e, hints);
        if (hints != null) {
            shapeNode.setRenderingHints(hints);
        }
        return shapeNode;
    }

    /**
     * Creates a <tt>ShapeNode</tt>.
     */
    protected GraphicsNode instantiateGraphicsNode() {
        return new ShapeNode();
    }

    /**
     * Builds using the specified BridgeContext and element, the
     * specified graphics node.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @param node the graphics node to build
     */
    public void buildGraphicsNode(BridgeContext ctx,
                                  Element e,
                                  GraphicsNode node) {
        ShapeNode shapeNode = (ShapeNode)node;
        shapeNode.setShapePainter(createShapePainter(ctx, e, shapeNode));
        super.buildGraphicsNode(ctx, e, node);
    }

    /**
     * Creates the shape painter associated to the specified element.
     * This implementation creates a shape painter considering the
     * various fill and stroke properties.
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
        return PaintServer.convertFillAndStroke(e, shapeNode, ctx);
    }

    /**
     * Initializes the specified ShapeNode's shape defined by the
     * specified Element and using the specified bridge context.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the shape node to build
     * @param node the shape node to initialize
     */
    protected abstract void buildShape(BridgeContext ctx,
                                       Element e,
                                       ShapeNode node);

    /**
     * Returns false as shapes are not a container.
     */
    public boolean isComposite() {
        return false;
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when the geometry of an graphical element has changed.
     */
    protected  void handleGeometryChanged() {
        super.handleGeometryChanged();
        ShapeNode shapeNode = (ShapeNode)node;
        shapeNode.setShapePainter(createShapePainter(ctx, e, shapeNode));
    }

    /**
     * This flag bit indicates if a new shape painter has already been created.
     * Avoid creating one ShapePainter per CSS property change
     */
    private boolean hasNewShapePainter;

    /**
     * Invoked when CSS properties have changed on an element.
     *
     * @param evt the CSSEngine event that describes the update
     */
    public void handleCSSEngineEvent(CSSEngineEvent evt) {
        hasNewShapePainter = false;
        super.handleCSSEngineEvent(evt);
    }

    /**
     * Invoked for each CSS property that has changed.
     */
    protected void handleCSSPropertyChanged(int property) {
        switch(property) {
        case SVGCSSEngine.FILL_INDEX:
        case SVGCSSEngine.FILL_OPACITY_INDEX:
        case SVGCSSEngine.STROKE_INDEX:
        case SVGCSSEngine.STROKE_OPACITY_INDEX:
        case SVGCSSEngine.STROKE_WIDTH_INDEX:
        case SVGCSSEngine.STROKE_LINECAP_INDEX:
        case SVGCSSEngine.STROKE_LINEJOIN_INDEX:
        case SVGCSSEngine.STROKE_MITERLIMIT_INDEX:
        case SVGCSSEngine.STROKE_DASHARRAY_INDEX:
        case SVGCSSEngine.STROKE_DASHOFFSET_INDEX: {
            if (!hasNewShapePainter) {
                hasNewShapePainter = true;
                ShapeNode shapeNode = (ShapeNode)node;
                ShapePainter painter = 
                    PaintServer.convertFillAndStroke(e, shapeNode, ctx);
                shapeNode.setShapePainter(createShapePainter(ctx, e, shapeNode));
            }
            break;
        }
        case SVGCSSEngine.SHAPE_RENDERING_INDEX: {
            RenderingHints hints = node.getRenderingHints();
            hints = CSSUtilities.convertShapeRendering(e, hints);
            if (hints != null) {
                node.setRenderingHints(hints);
            }
            break;
          }
        case SVGCSSEngine.COLOR_RENDERING_INDEX: {
            RenderingHints hints = node.getRenderingHints();
            hints = CSSUtilities.convertColorRendering(e, hints);
            if (hints != null) {
                node.setRenderingHints(hints);
            }
            break;
        } 
        default:
            super.handleCSSPropertyChanged(property);
        }
    }
}
