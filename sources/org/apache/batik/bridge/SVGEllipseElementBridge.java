/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.Ellipse2D;

import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;ellipse> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGEllipseElementBridge extends SVGShapeElementBridge {

    /**
     * Constructs a new bridge for the &lt;ellipse> element.
     */
    public SVGEllipseElementBridge() {}

    /**
     * Returns 'ellipse'.
     */
    public String getLocalName() {
        return SVG_ELLIPSE_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGEllipseElementBridge();
    }

    /**
     * Constructs an ellipse according to the specified parameters.
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

        // 'rx' attribute - required
        s = e.getAttributeNS(null, SVG_RX_ATTRIBUTE);
        float rx;
        if (s.length() != 0) {
            rx = UnitProcessor.svgHorizontalLengthToUserSpace
                (s, SVG_RX_ATTRIBUTE, uctx);
        } else {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_RX_ATTRIBUTE, s});
        }

        // 'ry' attribute - required
        s = e.getAttributeNS(null, SVG_RY_ATTRIBUTE);
        float ry;
        if (s.length() != 0) {
            ry = UnitProcessor.svgVerticalLengthToUserSpace
                (s, SVG_RY_ATTRIBUTE, uctx);
        } else {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_RY_ATTRIBUTE, s});
        }

	// A value of zero disables rendering of the element
	if ((rx == 0) || (ry == 0)) {
            shapeNode.setShape(null);
	    return;
	}

        shapeNode.setShape(new Ellipse2D.Float(cx-rx, cy-ry, rx*2, ry*2));
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_CX_ATTRIBUTE) ||
            attrName.equals(SVG_CY_ATTRIBUTE) ||
            attrName.equals(SVG_RX_ATTRIBUTE) ||
            attrName.equals(SVG_RY_ATTRIBUTE)) {

            buildShape(ctx, e, (ShapeNode)node);
            handleGeometryChanged();
        } else {
            super.handleDOMAttrModifiedEvent(evt);
        }
    }
}
