/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.Line2D;

import org.apache.batik.parser.ParseException;
import org.apache.batik.gvt.ShapeNode;

import org.w3c.dom.Element;

import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;line> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGLineElementBridge extends SVGDecoratedShapeElementBridge {

    /**
     * Constructs a new bridge for the &lt;line> element.
     */
    public SVGLineElementBridge() {}

    /**
     * Returns 'line'.
     */
    public String getLocalName() {
        return SVG_LINE_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGLineElementBridge();
    }

    /**
     * Constructs a line according to the specified parameters.
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

        // 'x1' attribute - default is 0
        s = e.getAttributeNS(null, SVG_X1_ATTRIBUTE);
        float x1 = 0;
        if (s.length() != 0) {
            x1 = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (s, SVG_X1_ATTRIBUTE, uctx);
        }

        // 'y1' attribute - default is 0
        s = e.getAttributeNS(null, SVG_Y1_ATTRIBUTE);
        float y1 = 0;
        if (s.length() != 0) {
            y1 = UnitProcessor.svgVerticalCoordinateToUserSpace
                (s, SVG_Y1_ATTRIBUTE, uctx);
        }

        // 'x2' attribute - default is 0
        s = e.getAttributeNS(null, SVG_X2_ATTRIBUTE);
        float x2 = 0;
        if (s.length() != 0) {
            x2 = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (s, SVG_X2_ATTRIBUTE, uctx);
        }

        // 'y2' attribute - default is 0
        s = e.getAttributeNS(null, SVG_Y2_ATTRIBUTE);
        float y2 = 0;
        if (s.length() != 0) {
            y2 = UnitProcessor.svgVerticalCoordinateToUserSpace
                (s, SVG_Y2_ATTRIBUTE, uctx);
        }

        shapeNode.setShape(new Line2D.Float(x1, y1, x2, y2));
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_X1_ATTRIBUTE) ||
            attrName.equals(SVG_Y1_ATTRIBUTE) ||
            attrName.equals(SVG_X2_ATTRIBUTE) ||
            attrName.equals(SVG_Y2_ATTRIBUTE)) {

            buildShape(ctx, e, (ShapeNode)node);
            handleGeometryChanged();
        } else {
            super.handleDOMAttrModifiedEvent(evt);
        }
    }
}
