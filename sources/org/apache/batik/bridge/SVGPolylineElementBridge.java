/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Shape;
import java.io.StringReader;

import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.parser.AWTPolylineProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsParser;

import org.w3c.dom.Element;

import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;polyline> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGPolylineElementBridge extends SVGDecoratedShapeElementBridge {

    /**
     * Constructs a new bridge for the &lt;polyline> element.
     */
    public SVGPolylineElementBridge() {}

    /**
     * Returns 'polyline'.
     */
    public String getLocalName() {
        return SVG_POLYLINE_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGPolylineElementBridge();
    }

    /**
     * Constructs a polyline according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes a rect element
     * @param shapeNode the shape node to initialize
     */
    protected void buildShape(BridgeContext ctx,
                              Element e,
                              ShapeNode shapeNode) {

        String s = e.getAttributeNS(null, SVG_POINTS_ATTRIBUTE);
        if (s.length() != 0) {
            AWTPolylineProducer app = new AWTPolylineProducer();
            app.setWindingRule(CSSUtilities.convertFillRule(e));
            try {
                PointsParser pp = new PointsParser();
                pp.setPointsHandler(app);
                pp.parse(s);
            } catch (ParseException ex) {
                BridgeException bex
                    = new BridgeException(e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                          new Object[] {SVG_POINTS_ATTRIBUTE});
                bex.setGraphicsNode(shapeNode);
                throw bex;
            } finally {
                shapeNode.setShape(app.getShape());
            }
        } else {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_POINTS_ATTRIBUTE});
        }
    }

    // dynamic support

    /**
     * Handles DOMAttrModified events.
     *
     * @param evt the DOM mutation event
     */
    protected void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_POINTS_ATTRIBUTE)) {

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
