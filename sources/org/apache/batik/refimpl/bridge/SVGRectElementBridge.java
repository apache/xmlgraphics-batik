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
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.IllegalAttributeValueException;
import org.apache.batik.bridge.MissingAttributeException;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.refimpl.bridge.resources.Messages;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGElement;

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
    protected void buildShape(BridgeContext ctx,
                              SVGElement svgElement,
                              ShapeNode node,
                              CSSStyleDeclaration decl,
                              UnitProcessor.Context uctx) {

        // parse the x attribute, (default is 0)
        String s = svgElement.getAttributeNS(null, ATTR_X);
        float x = 0;
        if (s.length() != 0) {
            x = SVGUtilities.svgToUserSpace(svgElement,
                                            ATTR_X, s,
                                            uctx,
                                            UnitProcessor.HORIZONTAL_LENGTH);
        }

        // parse the y attribute, (default is 0)
        s = svgElement.getAttributeNS(null, ATTR_Y);
        float y = 0;
        if (s.length() != 0) {
            y = SVGUtilities.svgToUserSpace(svgElement,
                                            ATTR_Y, s,
                                            uctx,
                                            UnitProcessor.VERTICAL_LENGTH);
        }

        // parse the width attribute (required and must be positive)
        s = svgElement.getAttributeNS(null, ATTR_WIDTH);
        float w;
        if (s.length() == 0) {
            throw new MissingAttributeException(
                Messages.formatMessage("rect.width.required", null));
        } else {
            w = SVGUtilities.svgToUserSpace(svgElement,
                                            ATTR_WIDTH, s,
                                            uctx,
                                            UnitProcessor.HORIZONTAL_LENGTH);
            if (w < 0) {
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("rect.width.negative", null));
            }
        }

        // parse the height attribute (required and must be positive)
        s = svgElement.getAttributeNS(null, ATTR_HEIGHT);
        float h;
        if (s.length() == 0) {
            throw new MissingAttributeException(
                Messages.formatMessage("rect.height.required", null));
        } else {
            h = SVGUtilities.svgToUserSpace(svgElement,
                                            ATTR_HEIGHT, s,
                                            uctx,
                                            UnitProcessor.VERTICAL_LENGTH);
            if (h < 0) {
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("rect.height.negative", null));
            }
        }

        // parse the rx attribute (must be positive if any)
        s = svgElement.getAttributeNS(null, SVG_RX_ATTRIBUTE);
        boolean rxs = s.length() != 0;
        float rx = 0;
        if (s.length() != 0) {
            rx = SVGUtilities.svgToUserSpace(svgElement,
                                             SVG_RX_ATTRIBUTE, s,
                                             uctx,
                                             UnitProcessor.HORIZONTAL_LENGTH);
            if (rx < 0) {
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("rect.rx.negative", null));
            }
        }
        rx = (rx > w / 2) ? w / 2 : rx;

        // parse the ry attribute (must be positive if any)
        s = svgElement.getAttributeNS(null, SVG_RY_ATTRIBUTE);
        boolean rys = s.length() != 0;
        float ry = 0;
        if (s.length() != 0) {
            ry = SVGUtilities.svgToUserSpace(svgElement,
                                             SVG_RY_ATTRIBUTE, s,
                                             uctx,
                                             UnitProcessor.VERTICAL_LENGTH);
            if (ry < 0) {
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("rect.ry.negative", null));
            }
        }
        ry = (ry > h / 2) ? h / 2 : ry;
        Shape shape = null;
        if (rxs && rys) {
            if (rx == 0 || ry == 0) {
                shape = new Rectangle2D.Float(x, y, w, h);
            } else {
                shape = new RoundRectangle2D.Float(x, y, w, h, rx*2, ry*2);
            }
        } else if (rxs) {
            if (rx == 0) {
                shape = new Rectangle2D.Float(x, y, w, h);
            } else {
                shape = new RoundRectangle2D.Float(x, y, w, h, rx*2, rx*2);
            }
        } else if (rys) {
            if (ry == 0) {
                shape = new Rectangle2D.Float(x, y, w, h);
            } else {
                shape = new RoundRectangle2D.Float(x, y, w, h, ry*2, ry*2);
            }
        } else {
            shape = new Rectangle2D.Float(x, y, w, h);
        }
        node.setShape(shape);
    }

    public void update(BridgeMutationEvent evt) {
    }

}
