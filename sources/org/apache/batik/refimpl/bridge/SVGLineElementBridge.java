/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Shape;
import java.awt.geom.Line2D;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * A factory for the &lt;line> SVG element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGLineElementBridge extends SVGShapeElementBridge {

    /**
     * Returns a <tt>Line2D.Float</tt>.
     */
    protected Shape createShape(BridgeContext ctx,
                                SVGElement svgElement,
                                CSSStyleDeclaration decl,
                                UnitProcessor.Context uctx) {
        String s = svgElement.getAttributeNS(null, ATTR_X1);
        float x1 = UnitProcessor.svgToUserSpace(s,
                                                svgElement,
                                                UnitProcessor.HORIZONTAL_LENGTH,
                                                uctx);
        s = svgElement.getAttributeNS(null, ATTR_Y1);
        float y1 = UnitProcessor.svgToUserSpace(s,
                                                svgElement,
                                                UnitProcessor.VERTICAL_LENGTH,
                                                uctx);
        s = svgElement.getAttributeNS(null, ATTR_X2);
        float x2 = UnitProcessor.svgToUserSpace(s,
                                                svgElement,
                                                UnitProcessor.HORIZONTAL_LENGTH,
                                                uctx);
        s = svgElement.getAttributeNS(null, ATTR_Y2);
        float y2 = UnitProcessor.svgToUserSpace(s,
                                                svgElement,
                                                UnitProcessor.VERTICAL_LENGTH,
                                                uctx);
        return new Line2D.Float(x1, y1, x2, y2);
    }
}
