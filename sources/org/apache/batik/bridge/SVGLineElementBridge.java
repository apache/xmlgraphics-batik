/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.Line2D;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGElement;

/**
 * A factory for the &lt;line> SVG element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGLineElementBridge extends SVGDecoratedShapeElementBridge {

    /**
     * Returns a <tt>Line2D.Float</tt>.
     */
    protected void buildShape(BridgeContext ctx,
                              SVGElement svgElement,
                              ShapeNode node,
                              CSSStyleDeclaration decl,
                              UnitProcessor.Context uctx) {

        // parse the x1 attribute, (default is 0)
        String s = svgElement.getAttributeNS(null, ATTR_X1);
        float x1 = 0;
        if (s.length() != 0) {
            x1 = SVGUtilities.svgToUserSpace(svgElement,
                                             ATTR_X1, s,
                                             uctx,
                                             UnitProcessor.HORIZONTAL_LENGTH);
        }

        // parse the y1 attribute, (default is 0)
        s = svgElement.getAttributeNS(null, ATTR_Y1);
        float y1 = 0;
        if (s.length() != 0) {
            y1 = SVGUtilities.svgToUserSpace(svgElement,
                                             ATTR_Y1, s,
                                             uctx,
                                             UnitProcessor.VERTICAL_LENGTH);
        }

        // parse the x2 attribute, (default is 0)
        s = svgElement.getAttributeNS(null, ATTR_X2);
        float x2 = 0;
        if (s.length() != 0) {
            x2 = SVGUtilities.svgToUserSpace(svgElement,
                                             ATTR_X2, s,
                                             uctx,
                                             UnitProcessor.HORIZONTAL_LENGTH);
        }

        // parse the y2 attribute, (default is 0)
        s = svgElement.getAttributeNS(null, ATTR_Y2);
        float y2 = 0;
        if (s.length() != 0) {
            y2 = SVGUtilities.svgToUserSpace(svgElement,
                                             ATTR_Y2, s,
                                             uctx,
                                             UnitProcessor.VERTICAL_LENGTH);
        }

        node.setShape(new Line2D.Float(x1, y1, x2, y2));
    }
}
