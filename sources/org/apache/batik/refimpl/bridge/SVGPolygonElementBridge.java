/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Shape;
import java.awt.geom.PathIterator;

import java.io.IOException;
import java.io.StringReader;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.MissingAttributeException;
import org.apache.batik.parser.AWTPolygonProducer;
import org.apache.batik.refimpl.bridge.resources.Messages;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGElement;

/**
 * A factory for the &ltpolygon> element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGPolygonElementBridge extends SVGShapeElementBridge {

    /**
     * Returns an <tt>GeneralPath</tt>.
     */
    protected Shape createShape(BridgeContext ctx,
                                SVGElement elt,
                                CSSStyleDeclaration decl,
                                UnitProcessor.Context uctx) {

        // parse the fill rule CSS property
        CSSPrimitiveValue v;
        v = (CSSPrimitiveValue)decl.getPropertyCSSValue(FILL_RULE_PROPERTY);
        int wr = (CSSUtilities.rule(v) == CSSUtilities.RULE_NONZERO)
            ? PathIterator.WIND_NON_ZERO
            : PathIterator.WIND_EVEN_ODD;

        // parse the points attribute, (required)
        String pts = elt.getAttributeNS(null, ATTR_POINTS);
        if (pts.length() == 0) {
            throw new MissingAttributeException(
                Messages.formatMessage("polygon.points.required", null));
        }
        Shape shape = null;
        try {
            shape = AWTPolygonProducer.createShape(new StringReader(pts),
                                                   wr,
                                                   ctx.getParserFactory());
        } catch (IOException e) { /* Nothing to do */ }

        return shape;
    }
}
