/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.apache.batik.parser.ParseException;
import org.apache.batik.gvt.ShapeNode;

import org.w3c.dom.Element;

import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;circle> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGCircleElementBridge extends SVGShapeElementBridge {

    /**
     * Constructs a new bridge for the &lt;circle> element.
     */
    public SVGCircleElementBridge() {}

    /**
     * Returns 'circle'.
     */
    public String getLocalName() {
        return SVG_CIRCLE_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGCircleElementBridge();
    }

    /**
     * Constructs a circle according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes a rect element
     * @param shapeNode the shape node to initialize
     */
    protected void buildShape(BridgeContext ctx,
                              Element e,
                              ShapeNode shapeNode) {

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, e);
        String s;

        // 'cx' attribute - default is 0
        s = e.getAttributeNS(null, SVG_CX_ATTRIBUTE);
        float cx = 0;
        if (s.length() != 0) {
            cx = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (s, SVG_CX_ATTRIBUTE, uctx);
        }

        // 'cy' attribute - default is 0
        s = e.getAttributeNS(null, SVG_CY_ATTRIBUTE);
        float cy = 0;
        if (s.length() != 0) {
            cy = UnitProcessor.svgVerticalCoordinateToUserSpace
                (s, SVG_CY_ATTRIBUTE, uctx);
        }

        // 'r' attribute - required
        s = e.getAttributeNS(null, SVG_R_ATTRIBUTE);
        float r;
        if (s.length() != 0) {
            r = UnitProcessor.svgOtherLengthToUserSpace
                (s, SVG_R_ATTRIBUTE, uctx);
        } else {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_R_ATTRIBUTE, s});
        }
	// A value of zero disables rendering of the element
	if (r == 0) {
	    return;
	}
        float x = cx - r;
        float y = cy - r;
        float w = r * 2;
        shapeNode.setShape(new Ellipse2D.Float(x, y, w, w));
    }

    // dynamic support

    /**
     * Handles DOMAttrModified events.
     *
     * @param evt the DOM mutation event
     */
    protected void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_CX_ATTRIBUTE) ||
            attrName.equals(SVG_CY_ATTRIBUTE) ||
            attrName.equals(SVG_R_ATTRIBUTE)) {

            BridgeUpdateEvent be = new BridgeUpdateEvent(this);
            fireBridgeUpdateStarting(be);
            buildShape(ctx, e, (ShapeNode)node);
            if (((ShapeNode)node).getShape() == null) {
                // <!> FIXME: disable the rendering
            }
            fireBridgeUpdateCompleted(be);
        } else {
            super.handleDOMAttrModifiedEvent(evt);
        }
    }
}
