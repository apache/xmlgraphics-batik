/*

   Copyright 2001-2004  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.dom.svg.AnimatedLiveAttributeValue;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGOMPolylineElement;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.parser.AWTPolylineProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsParser;

import org.w3c.dom.Element;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

/**
 * Bridge class for the &lt;polyline> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGPolylineElementBridge extends SVGDecoratedShapeElementBridge {

    /**
     * default shape for the update of 'points' when
     * the value is the empty string.
     */
    protected static final Shape DEFAULT_SHAPE = new GeneralPath();

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

        try {
            SVGOMPolylineElement pe = (SVGOMPolylineElement) e;
            SVGPointList pl = pe.getAnimatedPoints();
            int size = pl.getNumberOfItems();
            if (size == 0) {
                shapeNode.setShape(DEFAULT_SHAPE);
            } else {
                AWTPolylineProducer app = new AWTPolylineProducer();
                app.setWindingRule(CSSUtilities.convertFillRule(e));
                app.startPoints();
                for (int i = 0; i < size; i++) {
                    SVGPoint p = pl.getItem(i);
                    app.point(p.getX(), p.getY());
                }
                app.endPoints();
                shapeNode.setShape(app.getShape());
            }
        } catch (LiveAttributeException ex) {
            throw new BridgeException
                (ex.getElement(),
                 ex.isMissing() ? ERR_ATTRIBUTE_MISSING
                                : ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] { ex.getAttributeName(), ex.getValue() });
        }
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when the animated value of an animatable attribute has changed.
     */
    public void handleAnimatedAttributeChanged
            (AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null) {
            String ln = alav.getLocalName();
            if (ln.equals(SVG_POINTS_ATTRIBUTE)) {
                buildShape(ctx, e, (ShapeNode)node);
                handleGeometryChanged();
                return;
            }
        }
        super.handleAnimatedAttributeChanged(alav);
    }

    protected void handleCSSPropertyChanged(int property) {
        switch(property) {
        case SVGCSSEngine.FILL_RULE_INDEX:
            buildShape(ctx, e, (ShapeNode) node);
            handleGeometryChanged();
            break;
        default:
            super.handleCSSPropertyChanged(property);
        }
    }
}
