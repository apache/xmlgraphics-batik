/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
    protected boolean hasNewShapePainter;

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
