/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.StringReader;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.IllegalAttributeValueException;
import org.apache.batik.bridge.MissingAttributeException;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.parser.AWTPolylineProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsParser;
import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGElement;

/**
 * A factory for the &ltpolyline> element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGPolylineElementBridge extends SVGShapeElementBridge {

    /**
     * Returns an <tt>GeneralPath</tt>.
     */
    protected void buildShape(BridgeContext ctx,
                              SVGElement elt,
                              ShapeNode node,
                              CSSStyleDeclaration decl,
                              UnitProcessor.Context uctx) {

        // parse the fill rule CSS property
        CSSPrimitiveValue v;
        v = (CSSPrimitiveValue)decl.getPropertyCSSValue(CSS_FILL_RULE_PROPERTY);
        int wr = (CSSUtilities.rule(v) == CSSUtilities.RULE_NONZERO)
            ? PathIterator.WIND_NON_ZERO
            : PathIterator.WIND_EVEN_ODD;

        // parse the points attribute, (required)
        String pts = elt.getAttributeNS(null, ATTR_POINTS);
        if (pts.length() == 0) {
            throw new MissingAttributeException(
                Messages.formatMessage("polyline.points.required", null));
        }
        PointsParser p = ctx.getParserFactory().createPointsParser();
        AWTPolylineProducer ph = new AWTPolylineProducer();
        ph.setWindingRule(wr);
        p.setPointsHandler(ph);
        try {
            p.parse(new StringReader(pts));
        } catch (ParseException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("polyline.points.invalid",
                                       new Object[] {ex.getMessage()}),
                node);
        } finally {
            node.setShape(ph.getShape());
        }
    }
}
