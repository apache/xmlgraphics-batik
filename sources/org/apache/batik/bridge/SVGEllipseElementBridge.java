/*

   Copyright 2001-2003,2006  The Apache Software Foundation 

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

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.apache.batik.dom.svg.AnimatedLiveAttributeValue;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGOMEllipseElement;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;

import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;ellipse> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGEllipseElementBridge extends SVGShapeElementBridge {

    /**
     * Constructs a new bridge for the &lt;ellipse> element.
     */
    public SVGEllipseElementBridge() {}

    /**
     * Returns 'ellipse'.
     */
    public String getLocalName() {
        return SVG_ELLIPSE_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGEllipseElementBridge();
    }

    /**
     * Constructs an ellipse according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes a rect element
     * @param shapeNode the shape node to initialize
     */
    protected void buildShape(BridgeContext ctx,
                              Element e,
                              ShapeNode shapeNode) {
        try {
            SVGOMEllipseElement ee = (SVGOMEllipseElement) e;

            // 'cx' attribute - default is 0
            float cx = ee.getCx().getAnimVal().getValue();

            // 'cy' attribute - default is 0
            float cy = ee.getCy().getAnimVal().getValue();

            // 'rx' attribute - required
            float rx = ee.getRx().getAnimVal().getValue();

            // 'ry' attribute - required
            float ry = ee.getRy().getAnimVal().getValue();

            shapeNode.setShape(new Ellipse2D.Float(cx - rx, cy - ry,
                                                   rx * 2, ry * 2));
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
            if (ln.equals(SVG_CX_ATTRIBUTE)
                    || ln.equals(SVG_CY_ATTRIBUTE)
                    || ln.equals(SVG_RX_ATTRIBUTE)
                    || ln.equals(SVG_RY_ATTRIBUTE)) {
                buildShape(ctx, e, (ShapeNode)node);
                handleGeometryChanged();
                return;
            }
        }
        super.handleAnimatedAttributeChanged(alav);
    }

    protected ShapePainter createShapePainter(BridgeContext ctx,
                                              Element e,
                                              ShapeNode shapeNode) {
        Rectangle2D r2d = shapeNode.getShape().getBounds2D();
        if ((r2d.getWidth() == 0) || (r2d.getHeight() == 0))
            return null;
        return super.createShapePainter(ctx, e, shapeNode);
    }
}
