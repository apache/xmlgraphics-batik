/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * A factory for the &lt;rect> SVG element.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGRectElementBridge extends SVGShapeElementBridge {

    /**
     * Returns a <tt>Rectangle2D.Float</tt> or a
     * <tt>RoundRectangle2D.Float</tt> depending on the 'x', 'y',
     * 'width', 'height', 'rx' and 'ry' attributes.
     */
    protected Shape createShape(BridgeContext ctx,
                                SVGElement svgElement,
                                CSSStyleDeclaration decl,
                                UnitProcessor.Context uctx) {
        String s = svgElement.getAttributeNS(null, ATTR_X);
        float x = UnitProcessor.svgToUserSpace(s,
                                               svgElement,
                                               UnitProcessor.HORIZONTAL_LENGTH,
                                               uctx);
        s = svgElement.getAttributeNS(null, ATTR_Y);
        float y = UnitProcessor.svgToUserSpace(s,
                                               svgElement,
                                               UnitProcessor.VERTICAL_LENGTH,
                                               uctx);
        s = svgElement.getAttributeNS(null, ATTR_WIDTH);
        float w = UnitProcessor.svgToUserSpace(s,
                                               svgElement,
                                               UnitProcessor.HORIZONTAL_LENGTH,
                                               uctx);
        s = svgElement.getAttributeNS(null, ATTR_HEIGHT);
        float h = UnitProcessor.svgToUserSpace(s,
                                               svgElement,
                                               UnitProcessor.VERTICAL_LENGTH,
                                               uctx);
        s = svgElement.getAttributeNS(null, ATTR_RX);
        boolean rxs = s.length() != 0;
        float rx = UnitProcessor.svgToUserSpace(s,
                                                svgElement,
                                                UnitProcessor.HORIZONTAL_LENGTH,
                                                uctx);
        rx = (rx > w / 2) ? w / 2 : rx;
        s = svgElement.getAttributeNS(null, ATTR_RY);
        boolean rys = s.length() != 0;
        float ry = UnitProcessor.svgToUserSpace(s,
                                                svgElement,
                                                UnitProcessor.VERTICAL_LENGTH,
                                                uctx);
        ry = (ry > w / 2) ? w / 2 : ry;

        if (rxs && rys) {
            if (rx == 0 || ry == 0) {
                return new Rectangle2D.Float(x, y, w, h);
            } else {
                return new RoundRectangle2D.Float(x, y, w, h, rx*2, ry*2);
            }
        } else if (rxs) {
            if (rx == 0) {
                return new Rectangle2D.Float(x, y, w, h);
            } else {
                return new RoundRectangle2D.Float(x, y, w, h, rx*2, rx*2);
            }
        } else if (rys) {
            if (ry == 0) {
                return new Rectangle2D.Float(x, y, w, h);
            } else {
                return new RoundRectangle2D.Float(x, y, w, h, ry*2, ry*2);
            }
        } else {
            return new Rectangle2D.Float(x, y, w, h);
        }
    }
}
