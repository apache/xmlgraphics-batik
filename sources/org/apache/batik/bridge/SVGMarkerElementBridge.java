/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.css.HiddenChildElement;
import org.apache.batik.css.value.ValueConstants;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.Marker;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;
import org.apache.batik.util.CSSConstants;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGElement;

/**
 * Bridge class for the &lt;marker> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGMarkerElementBridge implements MarkerBridge,
                                               CSSConstants,
                                               SVGConstants,
                                               ErrorConstants {

    /**
     * Constructs a new bridge for the &lt;marker> element.
     */
    protected SVGMarkerElementBridge() {}

    /**
     * Creates a <tt>Marker</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param markerElement the element that represents the marker
     * @param paintedElement the element that references the marker element
     */
    public Marker createMarker(BridgeContext ctx,
                               Element markerElement,
                               Element paintedElement) {

        GVTBuilder builder = ctx.getGVTBuilder();

        CompositeGraphicsNode markerContentNode
            = new CompositeGraphicsNode();

        // build the GVT tree that represents the marker
        boolean hasChildren = false;
        for(Node n = markerElement.getFirstChild();
            n != null;
            n = n.getNextSibling()) {

            // check if the node is a valid Element
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element child = (Element)n;
            GraphicsNode markerNode = builder.build(ctx, child) ;
            // check if a GVT node has been created
            if (markerNode == null) {
                continue; // skip element as <marker> can contain <defs>...
            }
            hasChildren = true;
            markerContentNode.getChildren().add(markerNode);
        }
        if (!hasChildren) {
            return null; // no marker content defined
        }

        String s;
        UnitProcessor.Context uctx
            = UnitProcessor.createContext(ctx, paintedElement);

        // 'markerWidth' attribute - default is 3
        float markerWidth = 3;
        s = markerElement.getAttributeNS(null, SVG_MARKER_WIDTH_ATTRIBUTE);
        if (s.length() != 0) {
            markerWidth = UnitProcessor.svgHorizontalLengthToUserSpace
                (s, SVG_MARKER_WIDTH_ATTRIBUTE, uctx);
        }
        if (markerWidth == 0) {
            // A value of zero disables rendering of the element.
            return null;
        }

        // 'markerHeight' attribute - default is 3
        float markerHeight = 3;
        s = markerElement.getAttributeNS(null, SVG_MARKER_HEIGHT_ATTRIBUTE);
        if (s.length() != 0) {
            markerHeight = UnitProcessor.svgVerticalLengthToUserSpace
                (s, SVG_MARKER_HEIGHT_ATTRIBUTE, uctx);
        }
        if (markerHeight == 0) {
            // A value of zero disables rendering of the element.
            return null;
        }

        // 'orient' attribute - default is '0'
        double orient;
        s = markerElement.getAttributeNS(null, SVG_ORIENT_ATTRIBUTE);
        if (s.length() == 0) {
            orient = 0;
        } else if (SVG_AUTO_VALUE.equals(s)) {
            orient = Double.NaN;
        } else {
            try {
                orient = SVGUtilities.convertSVGNumber(s);
            } catch (NumberFormatException ex) {
                throw new BridgeException
                    (markerElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object [] {SVG_ORIENT_ATTRIBUTE, s});
            }
        }

        // 'stroke-width' property
        CSSStyleDeclaration decl
            = CSSUtilities.getComputedStyle(paintedElement);
        CSSValue v = decl.getPropertyCSSValue(CSS_STROKE_WIDTH_PROPERTY);
        float strokeWidth = UnitProcessor.cssOtherLengthToUserSpace
            (v, CSS_STROKE_WIDTH_PROPERTY, uctx);

        // 'markerUnits' attribute - default is 'strokeWidth'
        short unitsType;
        s = markerElement.getAttributeNS(null, SVG_MARKER_UNITS_ATTRIBUTE);
        if (s.length() == 0) {
            unitsType = SVGUtilities.STROKE_WIDTH;
        } else {
            unitsType = SVGUtilities.parseMarkerCoordinateSystem
                (markerElement, SVG_MARKER_UNITS_ATTRIBUTE, s);
        }

        //
        //
        //

        // compute an additional transform for 'strokeWidth' coordinate system
        AffineTransform markerTxf;
        if (unitsType == SVGUtilities.STROKE_WIDTH) {
            markerTxf = new AffineTransform();
            markerTxf.scale(strokeWidth, strokeWidth);
        } else {
            markerTxf = new AffineTransform();
        }

        // 'viewBox' and 'preserveAspectRatio' attributes
        // viewBox -> viewport(0, 0, markerWidth, markerHeight)
        AffineTransform preserveAspectRatioTransform
            = ViewBox.getPreserveAspectRatioTransform(markerElement,
                                                      markerWidth,
                                                      markerHeight);
        if (preserveAspectRatioTransform == null) {
            // disable the rendering of the element
            return null;
        } else {
            markerTxf.concatenate(preserveAspectRatioTransform);
        }
        // now we can set the transform to the 'markerContentNode'
        markerContentNode.setTransform(markerTxf);

        // 'overflow' property
        if (CSSUtilities.convertOverflow(markerElement)) { // overflow:hidden
            Rectangle2D markerClip;
            float [] offsets = CSSUtilities.convertClip(markerElement);
            if (offsets == null) { // clip:auto
                markerClip
                    = new Rectangle2D.Float(0,
                                            0,
                                            strokeWidth * markerWidth,
                                            strokeWidth * markerHeight);
            } else { // clip:rect(<x>, <y>, <w>, <h>)
                // offsets[0] = top
                // offsets[1] = right
                // offsets[2] = bottom
                // offsets[3] = left
                markerClip = new Rectangle2D.Float
                    (offsets[3],
                     offsets[0],
                     strokeWidth * markerWidth - offsets[1],
                     strokeWidth * markerHeight - offsets[2]);
            }

            CompositeGraphicsNode comp = new CompositeGraphicsNode();
            comp.getChildren().add(markerContentNode);
            Filter clipSrc = new GraphicsNodeRable8Bit
                (comp, ctx.getGraphicsNodeRenderContext());
            comp.setClip(new ClipRable8Bit(clipSrc, markerClip));
            markerContentNode = comp;
        }

        // 'refX' attribute - default is 0
        float refX = 0;
        s = markerElement.getAttributeNS(null, SVG_REF_X_ATTRIBUTE);
        if (s.length() != 0) {
            refX = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (s, SVG_REF_X_ATTRIBUTE, uctx);
        }

        // 'refY' attribute - default is 0
        float refY = 0;
        s = markerElement.getAttributeNS(null, SVG_REF_Y_ATTRIBUTE);
        if (s.length() != 0) {
            refY = UnitProcessor.svgVerticalCoordinateToUserSpace
                (s, SVG_REF_Y_ATTRIBUTE, uctx);
        }

        // TK: Warning at this time, refX and refY are relative to the
        // paintedElement's coordinate system. We need to move the
        // reference point to the marker's coordinate system

        // Watch out: the reference point is defined a little weirdly in the
        // SVG spec., but the bottom line is that the marker content should
        // not be translated. Rather, the reference point should be computed
        // in viewport space (this  is what the following transform
        // does) and used when placing the marker.
        //
        float ref[] = {refX, refY};
        markerTxf.transform(ref, 0, ref, 0, 1);
        Marker marker = new Marker(markerContentNode,
                                   new Point2D.Float(ref[0], ref[1]),
                                   orient);

        return marker;
    }

    /**
     * Performs an update according to the specified event.
     *
     * @param evt the event describing the update to perform */
    public void update(BridgeMutationEvent evt) {
        throw new Error("Not implemented");
    }
}
