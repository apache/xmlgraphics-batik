/*

   Copyright 2001-2004,2006  The Apache Software Foundation 

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
import java.awt.geom.Point2D;

import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.dom.svg.AnimatedLiveAttributeValue;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGOMPathElement;
import org.apache.batik.dom.svg.SVGPathContext;
import org.apache.batik.ext.awt.geom.PathLength;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.parser.AWTPathProducer;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGPathSeg;
import org.w3c.dom.svg.SVGPathSegArcAbs;
import org.w3c.dom.svg.SVGPathSegArcRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothRel;
import org.w3c.dom.svg.SVGPathSegLinetoAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalRel;
import org.w3c.dom.svg.SVGPathSegLinetoRel;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalRel;
import org.w3c.dom.svg.SVGPathSegList;
import org.w3c.dom.svg.SVGPathSegMovetoAbs;
import org.w3c.dom.svg.SVGPathSegMovetoRel;

/**
 * Bridge class for the &lt;path> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGPathElementBridge extends SVGDecoratedShapeElementBridge 
       implements SVGPathContext {

    /**
     * default shape for the update of 'd' when
     * the value is the empty string.
     */
    protected static final Shape DEFAULT_SHAPE = new GeneralPath();

    /**
     * Constructs a new bridge for the &lt;path> element.
     */
    public SVGPathElementBridge() {}

    /**
     * Returns 'path'.
     */
    public String getLocalName() {
        return SVG_PATH_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGPathElementBridge();
    }

    /**
     * Constructs a path according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes a rect element
     * @param shapeNode the shape node to initialize
     */
    protected void buildShape(BridgeContext ctx,
                              Element e,
                              ShapeNode shapeNode) {

        SVGOMPathElement pe = (SVGOMPathElement) e;
        AWTPathProducer app = new AWTPathProducer();
        try {
            // 'd' attribute - required
            SVGPathSegList p = pe.getAnimatedPathSegList();
            app.setWindingRule(CSSUtilities.convertFillRule(e));
            app.startPath();

            for (int i = 0; i < p.getNumberOfItems(); i++) {
                SVGPathSeg seg = p.getItem(i);
                switch (seg.getPathSegType()) {
                    case SVGPathSeg.PATHSEG_CLOSEPATH:
                        app.closePath();
                        break;
                    case SVGPathSeg.PATHSEG_MOVETO_ABS: {
                        SVGPathSegMovetoAbs s = (SVGPathSegMovetoAbs) seg;
                        app.movetoAbs(s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_MOVETO_REL: {
                        SVGPathSegMovetoRel s = (SVGPathSegMovetoRel) seg;
                        app.movetoRel(s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_LINETO_ABS: {
                        SVGPathSegLinetoAbs s = (SVGPathSegLinetoAbs) seg;
                        app.linetoAbs(s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_LINETO_REL: {
                        SVGPathSegLinetoRel s = (SVGPathSegLinetoRel) seg;
                        app.linetoRel(s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS: {
                        SVGPathSegCurvetoCubicAbs s =
                            (SVGPathSegCurvetoCubicAbs) seg;
                        app.curvetoCubicAbs
                            (s.getX1(), s.getY1(), s.getX2(), s.getY2(),
                             s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_CURVETO_CUBIC_REL: {
                        SVGPathSegCurvetoCubicRel s =
                            (SVGPathSegCurvetoCubicRel) seg;
                        app.curvetoCubicRel
                            (s.getX1(), s.getY1(), s.getX2(), s.getY2(),
                             s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS: {
                        SVGPathSegCurvetoQuadraticAbs s =
                            (SVGPathSegCurvetoQuadraticAbs) seg;
                        app.curvetoQuadraticAbs
                            (s.getX1(), s.getY1(), s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_REL: {
                        SVGPathSegCurvetoQuadraticRel s =
                            (SVGPathSegCurvetoQuadraticRel) seg;
                        app.curvetoQuadraticRel
                            (s.getX1(), s.getY1(), s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_ARC_ABS: {
                        SVGPathSegArcAbs s = (SVGPathSegArcAbs) seg;
                        app.arcAbs
                            (s.getR1(), s.getR2(), s.getAngle(),
                             s.getLargeArcFlag(), s.getSweepFlag(),
                             s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_ARC_REL: {
                        SVGPathSegArcRel s = (SVGPathSegArcRel) seg;
                        app.arcRel
                            (s.getR1(), s.getR2(), s.getAngle(),
                             s.getLargeArcFlag(), s.getSweepFlag(),
                             s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_ABS: {
                        SVGPathSegLinetoHorizontalAbs s =
                            (SVGPathSegLinetoHorizontalAbs) seg;
                        app.linetoHorizontalAbs(s.getX());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_REL: {
                        SVGPathSegLinetoHorizontalRel s =
                            (SVGPathSegLinetoHorizontalRel) seg;
                        app.linetoHorizontalRel(s.getX());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_LINETO_VERTICAL_ABS: {
                        SVGPathSegLinetoVerticalAbs s =
                            (SVGPathSegLinetoVerticalAbs) seg;
                        app.linetoVerticalAbs(s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_LINETO_VERTICAL_REL: {
                        SVGPathSegLinetoVerticalRel s =
                            (SVGPathSegLinetoVerticalRel) seg;
                        app.linetoVerticalRel(s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_ABS: {
                        SVGPathSegCurvetoCubicSmoothAbs s =
                            (SVGPathSegCurvetoCubicSmoothAbs) seg;
                        app.curvetoCubicSmoothAbs
                            (s.getX2(), s.getY2(), s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_REL: {
                        SVGPathSegCurvetoCubicSmoothRel s =
                            (SVGPathSegCurvetoCubicSmoothRel) seg;
                        app.curvetoCubicSmoothRel
                            (s.getX2(), s.getY2(), s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS: {
                        SVGPathSegCurvetoQuadraticSmoothAbs s =
                            (SVGPathSegCurvetoQuadraticSmoothAbs) seg;
                        app.curvetoQuadraticSmoothAbs(s.getX(), s.getY());
                        break;
                    }
                    case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL: {
                        SVGPathSegCurvetoQuadraticSmoothRel s =
                            (SVGPathSegCurvetoQuadraticSmoothRel) seg;
                        app.curvetoQuadraticSmoothRel(s.getX(), s.getY());
                        break;
                    }
                }
            }
        } catch (LiveAttributeException ex) {
            throw new BridgeException
                (ctx, ex.getElement(),
                 ex.isMissing() ? ERR_ATTRIBUTE_MISSING
                                : ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] { ex.getAttributeName(), ex.getValue() });
        } finally {
            shapeNode.setShape(app.getShape());
        }
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when the animated value of an animatable attribute has changed.
     */
    public void handleAnimatedAttributeChanged
            (AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null &&
                alav.getLocalName().equals(SVG_D_ATTRIBUTE)) {
            buildShape(ctx, e, (ShapeNode) node);
            handleGeometryChanged();
        } else {
            super.handleAnimatedAttributeChanged(alav);
        }
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

    // SVGPathContext ////////////////////////////////////////////////////////

    /**
     * The cached Shape used for computing the path length.
     */
    protected Shape pathLengthShape;

    /**
     * The cached PathLength object used for computing the path length.
     */
    protected PathLength pathLength;

    /**
     * Returns the PathLength object that tracks the length of the path.
     */
    protected PathLength getPathLengthObj() {
        Shape s = ((ShapeNode)node).getShape();
        if (pathLengthShape != s) {
            pathLength = new PathLength(s);
            pathLengthShape = s;
        }
        return pathLength;
    }

    /**
     * Returns the total length of the path.
     */
    public float getTotalLength() {
        PathLength pl = getPathLengthObj();
        return pl.lengthOfPath();
    }

    /**
     * Returns the point at the given distance along the path.
     */
    public Point2D getPointAtLength(float distance) {
        PathLength pl = getPathLengthObj();
        return pl.pointAtLength(distance);
    }
}
