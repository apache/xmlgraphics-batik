/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.RenderingHints;
import java.util.Map;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;

import org.w3c.dom.Element;


import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.StrokeShapePainter;
import java.awt.Paint;
import java.awt.Shape;



/**
 * The base bridge class for shapes. Subclasses bridge <tt>ShapeNode</tt>.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class SVGShapeElementBridge
    extends AbstractGraphicsNodeBridge {

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
        if (shapeNode.getShape() == null) {
	    return null; // Disable the rendering if something bad happens
        }
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
        // push 'this' as the current BridgeUpdateHandler for subbridges
        if (ctx.isDynamic()) {
            ctx.pushBridgeUpdateHandler(this);
        }

        ShapeNode shapeNode = (ShapeNode)node;
        shapeNode.setShapePainter(createShapePainter(ctx, e, shapeNode));

        // 'this' is no more the current BridgeUpdateHandler
        if (ctx.isDynamic()) {
            ctx.popBridgeUpdateHandler();
        }

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

    // dynamic support

    /**
     * Invoked when a bridge update is starting.
     *
     * @param evt the evt that describes the incoming update
     */
    public void bridgeUpdateStarting(BridgeUpdateEvent evt) {
        //        System.out.println("("+e.getLocalName()+" "+node+") update started "+evt.getHandlerKey());
    }

    /**
     * Invoked when a bridge update is completed.
     *
     * @param evt the evt that describes the update
     */
    public void bridgeUpdateCompleted(BridgeUpdateEvent evt) {
        //        System.out.println(">>> ("+e.getLocalName()+" "+node+") update completed "+ evt.getHandlerKey());
        switch(evt.getHandlerKey()) {
        case PaintServer.KEY_FILL: {
            Paint paint = (Paint)evt.getNewValue();
            ShapeNode shapeNode = (ShapeNode)node;
            Shape shape = shapeNode.getShape();
            ShapePainter painter = shapeNode.getShapePainter();
            if (painter instanceof FillShapePainter) {
                FillShapePainter fp = (FillShapePainter)painter;
                fp.setPaint(paint);
                shapeNode.setShapePainter(fp);
            } else if (painter instanceof CompositeShapePainter) {
                CompositeShapePainter cp = (CompositeShapePainter)painter;
                FillShapePainter fp = (FillShapePainter)cp.getShapePainter(0);
                fp.setPaint(paint);
                shapeNode.setShapePainter(cp);
            }
            break;
        } case PaintServer.KEY_STROKE: {
              Paint paint = (Paint)evt.getNewValue();
              ShapeNode shapeNode = (ShapeNode)node;
              Shape shape = shapeNode.getShape();
              ShapePainter painter = shapeNode.getShapePainter();
              if (painter instanceof StrokeShapePainter) {
                  StrokeShapePainter sp = (StrokeShapePainter)painter;
                  sp.setPaint(paint);
                  shapeNode.setShapePainter(sp);
              } else if (painter instanceof CompositeShapePainter) {
                  CompositeShapePainter cp = (CompositeShapePainter)painter;
                  StrokeShapePainter sp = 
                      (StrokeShapePainter)cp.getShapePainter(1);
                  sp.setPaint(paint);
                shapeNode.setShapePainter(cp);
              }
              break;
          }
        }
        //        System.out.println("<<< ("+e.getLocalName()+" "+node+") update completed "+ evt.getHandlerKey());
    }
}
