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

import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.css.HiddenChildElement;
import org.apache.batik.css.value.ValueConstants;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.Marker;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGElement;

/**
 * Turns a marker element into a <tt>Marker</tt> object
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGMarkerElementBridge implements MarkerBridge {
    /**
     * @param ctx the context to use
     * @param elem the &lt;marker&gt element to be converted to a Marker object
     * @return a Marker object representing the Element
     */
    public Marker buildMarker(BridgeContext ctx,
                              Element markerElement,
                              Element paintedElement){
        // CSS setup.
        SVGOMDocument md = (SVGOMDocument)markerElement.getOwnerDocument();
        SVGOMDocument pd = (SVGOMDocument)paintedElement.getOwnerDocument();

        ViewCSS viewCSS = (ViewCSS)pd.getDefaultView();

        Element inst;
        if (md == pd) {
            inst = (Element)markerElement.cloneNode(true);
        } else {
            inst = (Element)paintedElement.getOwnerDocument().importNode
                (markerElement, true);
        }
        Element g = pd.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        ((HiddenChildElement)g).setParentElement(paintedElement);

        CSSOMReadOnlyStyleDeclaration gsd =
            (CSSOMReadOnlyStyleDeclaration)viewCSS.getComputedStyle(g, null);

        gsd.setPropertyCSSValue
            (CSS_MARKER_PROPERTY,
             new CSSOMReadOnlyValue(ValueConstants.NONE_VALUE),
             gsd.getLocalPropertyPriority(CSS_MARKER_PROPERTY),
             gsd.getLocalPropertyOrigin(CSS_MARKER_PROPERTY));
        gsd.setPropertyCSSValue
            (CSS_MARKER_START_PROPERTY,
             new CSSOMReadOnlyValue(ValueConstants.NONE_VALUE),
             gsd.getLocalPropertyPriority(CSS_MARKER_START_PROPERTY),
             gsd.getLocalPropertyOrigin(CSS_MARKER_START_PROPERTY));
        gsd.setPropertyCSSValue
            (CSS_MARKER_MID_PROPERTY,
             new CSSOMReadOnlyValue(ValueConstants.NONE_VALUE),
             gsd.getLocalPropertyPriority(CSS_MARKER_START_PROPERTY),
             gsd.getLocalPropertyOrigin(CSS_MARKER_START_PROPERTY));
        gsd.setPropertyCSSValue
            (CSS_MARKER_END_PROPERTY,
             new CSSOMReadOnlyValue(ValueConstants.NONE_VALUE),
             gsd.getLocalPropertyPriority(CSS_MARKER_START_PROPERTY),
             gsd.getLocalPropertyOrigin(CSS_MARKER_START_PROPERTY));


        g.appendChild(inst);

        if (md != pd) {
            CSSUtilities.computeStyleAndURIs
                (markerElement, (ViewCSS)md.getDefaultView(),
                 inst, viewCSS, ((SVGOMDocument)md).getURLObject());
        }

        CSSOMReadOnlyStyleDeclaration cssDecl
            = (CSSOMReadOnlyStyleDeclaration)viewCSS.getComputedStyle(inst, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx,
                                              cssDecl);
        GVTBuilder builder = ctx.getGVTBuilder();

        CompositeGraphicsNode markerContentNode
            = new CompositeGraphicsNode();

        // build the GVT tree that represents the marker
        boolean hasChildren = false;
        for(Node node=inst.getFirstChild();
                 node != null;
                 node = node.getNextSibling()) {

            // check if the node is a valid Element
            if (node.getNodeType() != node.ELEMENT_NODE) {
                continue;
            }
            Element child = (Element) node;
            GraphicsNode markerNode = builder.build(ctx, child) ;
            // check if a GVT node has been created
            if (markerNode == null) {
                continue; // skip element as <marker> can contain <defs>...
            }
            hasChildren = true;
            markerContentNode.getChildren().add(markerNode);
        }

        if (!hasChildren) {
            System.out.println("No content in marker element");
            return null; // no marker content defined
        }

        // Extract the Marker's reference point coordinates
        String s = inst.getAttributeNS(null, SVG_REFX_ATTRIBUTE);
        float refX = 0;
        if (s.length() == 0) {
            s = SVG_DEFAULT_VALUE_MARKER_REFX;
        }

        refX = SVGUtilities.svgToUserSpace(inst,
                                           SVG_REFX_ATTRIBUTE, s,
                                           uctx,
                                           UnitProcessor.HORIZONTAL_LENGTH);

        // parse the refY attribute, (default is 0)
        s = inst.getAttributeNS(null, SVG_REFY_ATTRIBUTE);
        float refY = 0;
        if (s.length() == 0) {
            s = SVG_DEFAULT_VALUE_MARKER_REFY;
        }

        refY = SVGUtilities.svgToUserSpace(inst,
                                           SVG_REFY_ATTRIBUTE, s,
                                           uctx,
                                           UnitProcessor.VERTICAL_LENGTH);

        // Extract the Marker's width/height
        s = inst.getAttributeNS(null, SVG_MARKER_WIDTH_ATTRIBUTE);
        float markerWidth = 0;
        if (s.length() == 0) {
            s = SVG_DEFAULT_VALUE_MARKER_MARKER_WIDTH;
        }

        markerWidth= SVGUtilities.svgToUserSpace(inst,
                                                 SVG_MARKER_WIDTH_ATTRIBUTE, s,
                                                 uctx,
                                                 UnitProcessor.HORIZONTAL_LENGTH);

        // a zero markerWidth disables the marker
        if(markerWidth == 0){
            System.out.println("markerWidth is 0");
            return null;
        }

        // a negative markerWidth is an error
        if(markerWidth < 0){
            throw new IllegalAttributeValueException
                ( Messages.formatMessage("marker.markerWidth.illegal",
                                         new Object[] {s}));
        }

        s = inst.getAttributeNS(null, SVG_MARKER_HEIGHT_ATTRIBUTE);
        float markerHeight = 0;
        if (s.length() == 0) {
            s = SVG_DEFAULT_VALUE_MARKER_MARKER_HEIGHT;
        }

        markerHeight = SVGUtilities.svgToUserSpace(inst,
                                                   SVG_MARKER_HEIGHT_ATTRIBUTE, s,
                                                   uctx,
                                                   UnitProcessor.HORIZONTAL_LENGTH);

        // a zero markerHeight disables the marker
        if(markerHeight == 0){
            System.out.println("markerHeight is 0");
            return null;
        }

        // a negative markerHeight is an error
        if(markerHeight < 0){
            throw new IllegalAttributeValueException
                ( Messages.formatMessage("marker.markerHeight.illegal",
                                         new Object[] {s}));
        }

        // Extract the Marker's orient
        s = inst.getAttributeNS(null, SVG_ORIENT_ATTRIBUTE);
        double orient = 0;
        boolean autoOrient = false;
        if (s.length() == 0) {
            s = SVG_DEFAULT_VALUE_MARKER_ORIENT;
        }

        if (VALUE_AUTO.equals(s) ){
            orient = Double.NaN;
        }
        else{
            orient =
                SVGUtilities.convertSVGNumber(SVG_ORIENT_ATTRIBUTE, s);
        }

        // Extract the overflow property
        CSSPrimitiveValue vbOverflow =
            (CSSPrimitiveValue)cssDecl.getPropertyCSSValue(CSS_OVERFLOW_PROPERTY);

        String overFlowValue = vbOverflow.getStringValue();
        if(overFlowValue.length() == 0){
            overFlowValue = CSS_HIDDEN_VALUE;
        }

        boolean overflow = true;
        if(CSS_HIDDEN_VALUE.equals(overFlowValue)){
            overflow = false;
        }

        // Extract the marker units
        s = inst.getAttributeNS(null,
                                      SVG_MARKER_UNITS_ATTRIBUTE);

        if (s.length() == 0) {
            s = SVG_DEFAULT_VALUE_MARKER_MARKER_UNITS;
        }

        String markerUnits = s;

        // Extract the viewBox and preserveAspectRatio
        s = inst.getAttributeNS(null, ATTR_VIEW_BOX);
        boolean hasViewBox = false;
        // viewBox -> viewPort (0, 0, markerWidth, markerHeight)
        AffineTransform preserveAspectRatioTransform = null;

        s = inst.getAttributeNS(null, ATTR_VIEW_BOX);
        if (s.length() > 0){
            preserveAspectRatioTransform
                = SVGUtilities.getPreserveAspectRatioTransform
                ((SVGElement)inst,
                 markerWidth,
                 markerHeight);
        }

        //
        // Compute the transform for the markerContentNode
        //
        AffineTransform markerTxf = new AffineTransform();

        float strokeWidth = 1;
        if(markerUnits.equals(SVG_STROKE_WIDTH_VALUE)){
            CSSStyleDeclaration cssDeclPainted
                = CSSUtilities.getComputedStyle(paintedElement);

            UnitProcessor.Context uctxPainted
                = new DefaultUnitProcessorContext(ctx,
                                                  cssDeclPainted);
            CSSPrimitiveValue v =
                (CSSPrimitiveValue)cssDeclPainted.getPropertyCSSValue(CSS_STROKE_WIDTH_PROPERTY);

            short type = v.getPrimitiveType();
            strokeWidth
                = UnitProcessor.cssToUserSpace(type,
                                               v.getFloatValue(type),
                                               (SVGElement)paintedElement,
                                               UnitProcessor.OTHER_LENGTH,
                                               uctxPainted);

            markerTxf.scale(strokeWidth,
                            strokeWidth);
        }

        if(preserveAspectRatioTransform != null){
            markerTxf.concatenate(preserveAspectRatioTransform);
        }


        markerContentNode.setTransform(markerTxf);

        //
        // Set the markerContentNode's clipping area
        // depending on the overflow property
        //
        if(overflow == false){
            Rectangle2D markerClip
                = new Rectangle2D.Float(0, 0, strokeWidth*markerWidth,
                                        strokeWidth*markerHeight);

            CompositeGraphicsNode newMarkerContentNode
                = new CompositeGraphicsNode();

            newMarkerContentNode.getChildren().add(markerContentNode);

            GraphicsNodeRenderContext rc =
                ctx.getGraphicsNodeRenderContext();

            Filter clipSrc = new GraphicsNodeRable8Bit
                (newMarkerContentNode, rc);

            newMarkerContentNode.setClip
                (new ClipRable8Bit(clipSrc, markerClip));

            markerContentNode = newMarkerContentNode;
        }


        //
        // Build Marker object now
        //

        //
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
}
