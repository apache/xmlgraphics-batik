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
import org.apache.batik.parser.AWTPolygonProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsParser;

import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;polygon> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGPolygonElementBridge extends SVGDecoratedShapeElementBridge {

    /**
     * Constructs a new bridge for the &lt;polygon> element.
     */
    public SVGPolygonElementBridge() {}

    /**
     * Constructs a polygon according to the specified parameters.
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
            AWTPolygonProducer app = new AWTPolygonProducer();
            app.setWindingRule(CSSUtilities.convertFillRule(e));
            try {
                PointsParser pp = new PointsParser();
                pp.setPointsHandler(app);
                pp.parse(new StringReader(s));
            } catch (ParseException ex) {
                throw new BridgeException(e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                      new Object[] {SVG_POINTS_ATTRIBUTE});
            } finally {
                shapeNode.setShape(app.getShape());
            }
        } else {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                  new Object[] {SVG_POINTS_ATTRIBUTE});
        }
    }
}
