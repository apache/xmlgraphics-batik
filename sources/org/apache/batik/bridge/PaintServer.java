/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import org.apache.batik.ext.awt.color.ICCColorSpaceExt;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.Marker;
import org.apache.batik.gvt.MarkerShapePainter;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.StrokeShapePainter;
import org.apache.batik.util.CSSConstants;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Element;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.RGBColor;

import org.w3c.dom.svg.SVGColor;
import org.w3c.dom.svg.SVGICCColor;
import org.w3c.dom.svg.SVGPaint;

/**
 * A collection of utility methods to deliver <tt>java.awt.Paint</tt>,
 * <tt>java.awt.Stroke</tt> objects that could be used to paint a
 * shape. This class also provides additional methods the deliver SVG
 * Paint using the ShapePainter interface.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class PaintServer
    implements SVGConstants, CSSConstants, ErrorConstants {

    /**
     * No instance of this class is required.
     */
    protected PaintServer() {}


    /////////////////////////////////////////////////////////////////////////
    // 'marker-start', 'marker-mid', 'marker-end' delegates to the PaintServer
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns a <tt>ShapePainter</tt> defined on the specified
     * element and for the specified shape node.
     *
     * @param paintedElement the element with the marker CSS properties
     * @param node the shape node
     * @param ctx the bridge context
     */
    public static ShapePainter convertMarkers(Element paintedElement,
                                              ShapeNode node,
                                              BridgeContext ctx) {

        CSSStyleDeclaration decl
            = CSSUtilities.getComputedStyle(paintedElement);

        CSSValue v;

        v = decl.getPropertyCSSValue(CSS_MARKER_START_PROPERTY);
        Marker startMarker
            = convertMarker(paintedElement, (CSSPrimitiveValue)v, ctx);

        v = decl.getPropertyCSSValue(CSS_MARKER_MID_PROPERTY);
        Marker midMarker
            = convertMarker(paintedElement, (CSSPrimitiveValue)v, ctx);

        v = decl.getPropertyCSSValue(CSS_MARKER_END_PROPERTY);
        Marker endMarker
            = convertMarker(paintedElement, (CSSPrimitiveValue)v, ctx);

        if (startMarker != null || midMarker != null || endMarker != null) {
            MarkerShapePainter p = new MarkerShapePainter(node.getShape());
            p.setStartMarker(startMarker);
            p.setMiddleMarker(midMarker);
            p.setEndMarker(endMarker);
            return p;
        } else {
            return null;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // org.apache.batik.gvt.Marker
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns a <tt>Marker</tt> defined on the specified element by
     * the specified value, and for the specified shape node.
     *
     * @param paintedElement the painted element
     * @param v the CSS value describing the marker to construct
     * @param ctx the bridge context
     */
    public static Marker convertMarker(Element paintedElement,
                                       CSSPrimitiveValue v,
                                       BridgeContext ctx) {

        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
            return null; // 'none'
        } else {
            String uri = v.getStringValue();
            Element markerElement
                = ctx.getReferencedElement(paintedElement, uri);
            Bridge bridge = ctx.getBridge(markerElement);
            if (bridge == null || !(bridge instanceof MarkerBridge)) {
                throw new BridgeException
                    (paintedElement, ERR_CSS_URI_BAD_TARGET,
                     new Object[] {uri});
            }
            return ((MarkerBridge)bridge).createMarker
                (ctx, markerElement, paintedElement);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // 'stroke', 'fill' ... converts to ShapePainter
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns a <tt>ShapePainter</tt> defined on the specified element and
     * for the specified shape node, and using the specified bridge
     * context.
     *
     * @param e the element interested in a shape painter
     * @param node the shape node
     * @param ctx the bridge context
     */
    public static ShapePainter convertFillAndStroke(Element e,
                                                    ShapeNode node,
                                                    BridgeContext ctx) {

        Paint fillPaint = convertFillPaint(e, node, ctx);
        Paint strokePaint = convertStrokePaint(e, node, ctx);
        Shape shape = node.getShape();

        if (fillPaint != null && strokePaint != null) {
            FillShapePainter fp = new FillShapePainter(shape);
            fp.setPaint(fillPaint);

            StrokeShapePainter sp = new StrokeShapePainter(shape);
            sp.setStroke(PaintServer.convertStroke(e, ctx));
            sp.setPaint(strokePaint);
            CompositeShapePainter cp = new CompositeShapePainter(shape);
            cp.addShapePainter(fp);
            cp.addShapePainter(sp);
            return cp;
        } else if (strokePaint != null) {
            StrokeShapePainter sp = new StrokeShapePainter(shape);
            sp.setStroke(PaintServer.convertStroke(e, ctx));
            sp.setPaint(strokePaint);
            return sp;
        } else if (fillPaint != null) {
            FillShapePainter fp = new FillShapePainter(shape);
            fp.setPaint(fillPaint);
            return fp;
        } else {
            return null;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // java.awt.Paint
    /////////////////////////////////////////////////////////////////////////

    /**
     * Converts for the specified element, its stroke paint properties
     * to a Paint object.
     *
     * @param strokedElement the element interested in a Paint
     * @param strokedNode the graphics node to stroke
     * @param ctx the bridge context
     */
    public static Paint convertStrokePaint(Element strokedElement,
                                           GraphicsNode strokedNode,
                                           BridgeContext ctx) {
        CSSStyleDeclaration decl =
            CSSUtilities.getComputedStyle(strokedElement);
        // 'stroke-opacity'
        float opacity = convertOpacity
            (decl.getPropertyCSSValue(CSS_STROKE_OPACITY_PROPERTY));
        // 'stroke'
        CSSValue paintDef = decl.getPropertyCSSValue(CSS_STROKE_PROPERTY);

        return convertPaint(strokedElement,
                            strokedNode,
                            paintDef,
                            opacity,
                            ctx);
    }

    /**
     * Converts for the specified element, its fill paint properties
     * to a Paint object.
     *
     * @param filledElement the element interested in a Paint
     * @param filledNode the graphics node to fill
     * @param ctx the bridge context
     */
    public static Paint convertFillPaint(Element filledElement,
                                         GraphicsNode filledNode,
                                         BridgeContext ctx) {
        CSSStyleDeclaration decl =
            CSSUtilities.getComputedStyle(filledElement);
        // 'fill-opacity'
        float opacity = convertOpacity
            (decl.getPropertyCSSValue(CSS_FILL_OPACITY_PROPERTY));
        // 'fill'
        CSSValue paintDef = decl.getPropertyCSSValue(CSS_FILL_PROPERTY);

        return convertPaint(filledElement,
                            filledNode,
                            paintDef,
                            opacity,
                            ctx);
    }

    /**
     * Converts a Paint definition to a concrete <tt>java.awt.Paint</tt>
     * instance according to the specified parameters.
     *
     * @param paintedElement the element interested in a Paint
     * @param decl the CSS declaration of the painted element
     * @param paintedNode the graphics node to paint (objectBoundingBox)
     * @param paintDef the paint definition
     * @param opacity the opacity to consider for the Paint
     * @param ctx the bridge context
     */
    protected static Paint convertPaint(Element paintedElement,
                                        GraphicsNode paintedNode,
                                        CSSValue paintDef,
                                        float opacity,
                                        BridgeContext ctx) {

        //
        // <!> FIXME: In the next version of the spec, we should be
        // able to remove the if statement and use a SVGPaint all the time
        //
        // The CSS engine will give us a SVGPaint in any cases. We also
        // should also be able to use strong typing on convertXXX methods.
        //
        if (paintDef.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue v = (CSSPrimitiveValue)paintDef;
            switch (v.getPrimitiveType()) {
            case CSSPrimitiveValue.CSS_IDENT:
                return null; // none
            case CSSPrimitiveValue.CSS_RGBCOLOR:
                return convertColor(v.getRGBColorValue(), opacity);
            case CSSPrimitiveValue.CSS_URI:
                return convertURIPaint(paintedElement,
                                       paintedNode,
                                       paintDef,
                                       opacity,
                                       ctx);
            default:
                throw new Error(); // can't be reached
            }
        } else { // SVGPaint
            SVGPaint p = (SVGPaint)paintDef;
            Paint paint = null;
            switch (p.getPaintType()) {
            case SVGPaint.SVG_PAINTTYPE_NONE:
                return null; // 'none'
            case SVGPaint.SVG_PAINTTYPE_RGBCOLOR:
                return convertColor(p.getRGBColor(), opacity);
/* -- The one which is missing in our current SVG DOM bindings --
            case SVGPaint.SVG_PAINTTYPE_URI:
                return convertURIPaint(paintedElement,
                                       paintedNode,
                                       p,
                                       opacity,
                                       ctx);
*/
            case SVGPaint.SVG_PAINTTYPE_RGBCOLOR_ICCCOLOR:
                return convertRGBICCColor(paintedElement,
                                          (SVGColor)paintDef,
                                          opacity,
                                          ctx);
            case SVGPaint.SVG_PAINTTYPE_URI_NONE:
                return silentConvertURIPaint(paintedElement,
                                             paintedNode,
                                             (SVGPaint)paintDef,
                                             opacity,
                                             ctx);
            case SVGPaint.SVG_PAINTTYPE_URI_CURRENTCOLOR:
                paint = silentConvertURIPaint(paintedElement,
                                              paintedNode,
                                              (SVGPaint)paintDef,
                                              opacity,
                                              ctx);
                if (paint == null) { // no paint found
                    CSSStyleDeclaration decl =
                        CSSUtilities.getComputedStyle(paintedElement);
                    CSSValue v = decl.getPropertyCSSValue(CSS_COLOR_PROPERTY);
                    paint =  convertColor
                        (((CSSPrimitiveValue)v).getRGBColorValue(), opacity);
                }
                return paint;
            case SVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR:
                paint = silentConvertURIPaint(paintedElement,
                                              paintedNode,
                                              (SVGPaint)paintDef,
                                              opacity,
                                              ctx);
                if (paint == null) { // no paint found
                    paint = convertColor(p.getRGBColor(), opacity);
                }
                return paint;
            case SVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR_ICCCOLOR:
                paint = silentConvertURIPaint(paintedElement,
                                              paintedNode,
                                              (SVGPaint)paintDef,
                                              opacity,
                                              ctx);
                if (paint == null) { // no paint found
                    paint = convertRGBICCColor(paintedElement,
                                               (SVGColor)paintDef,
                                               opacity,
                                               ctx);
                }
                return paint;
           default:
                throw new Error(); // can't be reached
            }
        }
    }

    /**
     * Converts a Paint specified by URI without sending any error.
     * if a problem occured while processing the URI, it just returns
     * null (same effect as 'none')
     *
     * @param paintedElement the element interested in a Paint
     * @param paintedNode the graphics node to paint (objectBoundingBox)
     * @param paintDef the paint definition
     * @param opacity the opacity to consider for the Paint
     * @param ctx the bridge context
     * @return the paint object or null when impossible
     */
    public static Paint silentConvertURIPaint(Element paintedElement,
                                              GraphicsNode paintedNode,
                                              CSSValue paintDef,
                                              float opacity,
                                              BridgeContext ctx) {
        Paint paint = null;
        try {
            paint = convertURIPaint
                (paintedElement, paintedNode, paintDef, opacity, ctx);
        } catch (BridgeException ex) { /* ignore BridgeException */ }
        return paint;
    }

    /**
     * Converts a Paint specified as a URI.
     *
     * @param paintedElement the element interested in a Paint
     * @param paintedNode the graphics node to paint (objectBoundingBox)
     * @param paintDef the paint definition
     * @param opacity the opacity to consider for the Paint
     * @param ctx the bridge context
     */
    public static Paint convertURIPaint(Element paintedElement,
                                        GraphicsNode paintedNode,
                                        CSSValue paintDef,
                                        float opacity,
                                        BridgeContext ctx) {

        String uri = ((CSSPrimitiveValue)paintDef).getStringValue();
        Element paintElement = ctx.getReferencedElement(paintedElement, uri);

        Bridge bridge = ctx.getBridge(paintElement);
        if (bridge == null || !(bridge instanceof PaintBridge)) {
            throw new BridgeException(paintedElement, ERR_CSS_URI_BAD_TARGET,
                                      new Object[] {uri});
        }
        return ((PaintBridge)bridge).createPaint(ctx,
                                                 paintElement,
                                                 paintedElement,
                                                 paintedNode,
                                                 opacity);
    }

    /**
     * Returns a Color object that corresponds to the input Paint's
     * ICC color value or an RGB color if the related color profile
     * could not be used or loaded for any reason.
     *
     * @param paintedElement the element using the color
     * @param color the color definition
     * @param opacity the opacity
     * @param ctx the bridge context to use
     */
    public static Color convertRGBICCColor(Element paintedElement,
                                           SVGColor colorDef,
                                           float opacity,
                                           BridgeContext ctx) {
        SVGICCColor iccColor = colorDef.getICCColor();
        Color color = null;
        if (iccColor != null){
            color = convertICCColor(paintedElement, iccColor, opacity, ctx);
        }
        if (color == null){
            color = convertColor(colorDef.getRGBColor(), opacity);
        }
        return color;
    }

    /**
     * Returns a Color object that corresponds to the input Paint's
     * ICC color value or null if the related color profile could not
     * be used or loaded for any reason.
     *
     * @param paintedElement the element using the color
     * @param c the ICC color definition
     * @param opacity the opacity
     * @param ctx the bridge context to use
     */
    public static Color convertICCColor(Element e,
                                        SVGICCColor c,
                                        float opacity,
                                        BridgeContext ctx){
        // Get ICC Profile's name
        String iccProfileName = c.getColorProfile();
        if (iccProfileName == null){
            return null;
        }
        // Ask the bridge to map the ICC profile name to an  ICC_Profile object
        SVGColorProfileElementBridge profileBridge
            = (SVGColorProfileElementBridge)
            ctx.getBridge(SVG_NAMESPACE_URI, SVG_COLOR_PROFILE_TAG);
        if (profileBridge == null){
            return null; // no bridge for color profile
        }

        ICCColorSpaceExt profileCS
            = profileBridge.createICCColorSpaceExt(ctx, e, iccProfileName);
        if (profileCS == null){
            return null; // no profile
        }

        // Now, convert the colors to an array of floats
        float[] colorValue = SVGUtilities.convertSVGNumberList(c.getColors());
        if(colorValue == null){
            return null; // no color value
        }
        // Convert values to RGB
        float[] rgb = profileCS.intendedToRGB(colorValue);
        return new Color(rgb[0], rgb[1], rgb[2], opacity);
    }

    /**
     * Converts the given RGBColor and opacity to a Color object.
     * @param c The CSS color to convert.
     * @param o The opacity value (0 <= o <= 1).
     */
    public static Color convertColor(RGBColor c, float opacity) {
        int r = resolveColorComponent(c.getRed());
        int g = resolveColorComponent(c.getGreen());
        int b = resolveColorComponent(c.getBlue());
        return new Color(r, g, b, Math.round(opacity * 255f));
    }

    /////////////////////////////////////////////////////////////////////////
    // java.awt.stroke
    /////////////////////////////////////////////////////////////////////////

    /**
     * Converts a <tt>Stroke</tt> object defined on the specified element.
     *
     * @param strokedElement the element on which the stroke is specified
     */
    public static Stroke convertStroke(Element strokedElement,
                                       BridgeContext ctx) {

        // percentages and units are relative to the strokedElement's viewport
        UnitProcessor.Context uctx
            = UnitProcessor.createContext(ctx, strokedElement);
        CSSStyleDeclaration decl
            = CSSUtilities.getComputedStyle(strokedElement);
        CSSValue v;

        v = decl.getPropertyCSSValue(CSS_STROKE_WIDTH_PROPERTY);
        float width = UnitProcessor.cssOtherLengthToUserSpace
            (v, CSS_STROKE_WIDTH_PROPERTY, uctx);

        v = decl.getPropertyCSSValue(CSS_STROKE_LINECAP_PROPERTY);
        int linecap = convertStrokeLinecap((CSSPrimitiveValue)v);

        v = decl.getPropertyCSSValue(CSS_STROKE_LINEJOIN_PROPERTY);
        int linejoin = convertStrokeLinejoin((CSSPrimitiveValue)v);

        v = decl.getPropertyCSSValue(CSS_STROKE_MITERLIMIT_PROPERTY);
        float miterlimit = convertStrokeMiterlimit((CSSPrimitiveValue)v);

        v = decl.getPropertyCSSValue(CSS_STROKE_DASHARRAY_PROPERTY);
        float [] dasharray = convertStrokeDasharray(v, uctx);

        float dashoffset = 0;
        if (dasharray != null) {
            v = decl.getPropertyCSSValue(CSS_STROKE_DASHOFFSET_PROPERTY);
            dashoffset = UnitProcessor.cssOtherLengthToUserSpace
                (v, CSS_STROKE_DASHOFFSET_PROPERTY, uctx);
        }
        return new BasicStroke(width,
                               linecap,
                               linejoin,
                               miterlimit,
                               dasharray,
                               dashoffset);
    }

    /////////////////////////////////////////////////////////////////////////
    // Stroke utility methods
    /////////////////////////////////////////////////////////////////////////

    /**
     * Converts the 'stroke-dasharray' property to a list of float
     * number in user units.
     *
     * @param v the CSS value describing the dasharray property
     * @param uctx the unit processor context used to resolve units
     */
    public static
        float [] convertStrokeDasharray(CSSValue v,
                                        UnitProcessor.Context uctx) {
        float [] dasharray = null;
        if (v.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            CSSValueList l = (CSSValueList)v;
            int length = l.getLength();
            dasharray = new float[length];
            float sum = 0;
            for (int i=0; i < dasharray.length; ++i) {
                CSSValue vv = l.item(i);
                float dash = UnitProcessor.cssOtherLengthToUserSpace
                    (vv, CSS_STROKE_DASHARRAY_PROPERTY, uctx);
                dasharray[i] = dash;
                sum += dash;
            }
            if (sum == 0) {
                /* 11.4 - If the sum of the <length>'s is zero, then
                 * the stroke is rendered as if a value of none were specified.
                 */
                dasharray = null;
            }
        }
        return dasharray;
    }

    /**
     * Converts the 'miterlimit' property to the appropriate float number.
     * @param v the CSS value describing the miterlimit property
     */
    public static float convertStrokeMiterlimit(CSSPrimitiveValue v) {
        float miterlimit = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
        return (miterlimit < 1f) ? 1f : miterlimit;
    }

    /**
     * Converts the 'linecap' property to the appropriate BasicStroke constant.
     * @param v the CSS value describing the linecap property
     */
    public static int convertStrokeLinecap(CSSPrimitiveValue v) {
        String s = v.getStringValue();
        switch (s.charAt(0)) {
        case 'b':
            return BasicStroke.CAP_BUTT;
        case 'r':
            return BasicStroke.CAP_ROUND;
        case 's':
            return BasicStroke.CAP_SQUARE;
        default:
            throw new Error(); // can't be reached
        }
    }

    /**
     * Converts the 'linejoin' property to the appropriate BasicStroke constant.
     * @param v the CSS value describing the linejoin property
     */
    public static int convertStrokeLinejoin(CSSPrimitiveValue v) {
        String s = v.getStringValue();
        switch (s.charAt(0)) {
        case 'm':
            return BasicStroke.JOIN_MITER;
        case 'r':
            return BasicStroke.JOIN_ROUND;
        case 'b':
            return BasicStroke.JOIN_BEVEL;
        default:
            throw new Error(); // can't be reached
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Paint utility methods
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the value of one color component (0 <= result <= 255).
     * @param v the value that defines the color component
     */
    public static int resolveColorComponent(CSSPrimitiveValue v) {
        float f;
        switch(v.getPrimitiveType()) {
        case CSSPrimitiveValue.CSS_PERCENTAGE:
            f = v.getFloatValue(CSSPrimitiveValue.CSS_PERCENTAGE);
            f = (f > 100f) ? 100f : (f < 0f) ? 0f : f;
            return Math.round(255f * f / 100f);
        case CSSPrimitiveValue.CSS_NUMBER:
            f = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
            f = (f > 255f) ? 255f : (f < 0f) ? 0f : f;
            return Math.round(f);
        default:
            throw new Error(); // can't be reached
        }
    }

    /**
     * Returns the opacity represented by the specified CSSValue.
     * @param v the value that represents the opacity
     * @return the opacity between 0 and 1
     */
    public static float convertOpacity(CSSValue v) {
        float r =
            ((CSSPrimitiveValue)v).getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
        return (r < 0f) ? 0f : (r > 1f) ? 1f : r;
    }
}
