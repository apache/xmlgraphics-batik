/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGElement;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.FilterBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GVTFactory;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.StrokeShapePainter;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.refimpl.bridge.resources.Messages;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Paint;
import java.awt.Stroke;

/**
 * A collection of utility methods involving CSS.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSUtilities implements SVGConstants {

    /**
     * No instance of this class.
     */
    protected CSSUtilities() {}

    /**
     * Initializes the composite corresponding to the 
     * opacity attribute in the input <tt>GraphicsNode</tt>
     */
    public static Composite convertOpacityToComposite(CSSPrimitiveValue val){
        float opacity = convertOpacity(val);
        Composite composite = null;
        if(opacity > 0) {
            if(opacity < 1){
                composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       opacity);
            }
            else{
                composite = AlphaComposite.SrcOver;
            }
        }
        return composite;
    }                                                      

    /**
     * Returns the opacity value represented by the given CSSValue.
     */
    public static float convertOpacity(CSSPrimitiveValue v) {
        float result = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
        return (result < 0f) ? 0f : (result > 1f) ? 1f : result;
    }

    /**
     * Represents a rule property 'nonzero' value.
     */
    public final static int RULE_NONZERO = 0;

    /**
     * Represents a rule property 'evenodd' value.
     */
    public final static int RULE_EVENODD = 1;

    /**
     * Returns the rule value (nonzero | evenodd) represented by the given
     * CSSValue.
     */
    public static int rule(CSSPrimitiveValue v) {
        if (v.getStringValue().charAt(0) == 'n') {
            return RULE_NONZERO;
        } else {
            return RULE_EVENODD;
        }
    }

    /**
     * Converts the given RGBColor and opacity to a Color object.
     * @param c The CSS color to convert.
     * @param o The opacity value (0 <= o <= 1).
     */
    public static Color convertColor(RGBColor c, float o) {
        int r = resolveColorComponent(c.getRed());
        int g = resolveColorComponent(c.getGreen());
        int b = resolveColorComponent(c.getBlue());
        return new Color(r, g, b, Math.round(o * 255f));
    }

    /**
     * Returns the <tt>ShapePainter</tt> for the specified Element
     * using the specified context and css style declaration.
     * @param svgElement the SVG Element
     * @param ctx the bridge context
     * @param decl the css style declaration
     * @param uctx the UnitProcessor context
     */
    public static ShapePainter convertStrokeAndFill(SVGElement svgElement,
                                                    BridgeContext ctx,
                                                    CSSStyleDeclaration decl,
                                                    UnitProcessor.Context uctx){
        GVTFactory f = ctx.getGVTFactory();
        CompositeShapePainter painter = f.createCompositeShapePainter();
        // resolve fill
        ShapePainter fillPainter = convertFill(svgElement, ctx, decl, uctx);
        if (fillPainter != null) {
            painter.addShapePainter(fillPainter);
        }
        // resolve stroke
        ShapePainter strokePainter = convertStroke(svgElement, ctx, decl, uctx);
        if (strokePainter != null) {
            painter.addShapePainter(strokePainter);
        }
        return painter;
    }

    /**
     * Returns the <tt>ShapePainter</tt> used to draw the outline of
     * the specified Element using the specified context and css style
     * declaration.
     * @param svgElement the SVG Element
     * @param ctx the bridge context
     * @param decl the css style declaration
     * @param uctx the UnitProcessor context
     */
    public static ShapePainter convertStroke(SVGElement svgElement,
                                             BridgeContext ctx,
                                             CSSStyleDeclaration decl,
                                             UnitProcessor.Context uctx) {
        GVTFactory f = ctx.getGVTFactory();
        StrokeShapePainter painter = null;

        // resolve the paint of the StrokeShapePainter
        CSSPrimitiveValue v
            = (CSSPrimitiveValue) decl.getPropertyCSSValue(STROKE_PROPERTY);
        switch(v.getPrimitiveType()) {
        case CSSPrimitiveValue.CSS_IDENT:
            return null; // stroke:'none'
        case CSSPrimitiveValue.CSS_RGBCOLOR:
            painter = f.createStrokeShapePainter();
            CSSPrimitiveValue vv = (CSSPrimitiveValue)
                decl.getPropertyCSSValue(STROKE_OPACITY_PROPERTY);
            float opacity = convertOpacity(vv);
            Color c = convertColor(v.getRGBColorValue(), opacity);
            painter.setPaint(c);
            break;
        case CSSPrimitiveValue.CSS_URI:
            // <!> FIXME : TODO !!!
            throw new Error("Not yet implemented");
        }

        // resolve the java.awt.Stroke of the StrokeShapePainter
        v = (CSSPrimitiveValue) decl.getPropertyCSSValue(STROKE_WIDTH_PROPERTY);
        short type = v.getPrimitiveType();
        // 'stroke-width'
        float width
            = UnitProcessor.cssToUserSpace(type,
                                           v.getFloatValue(type),
                                           svgElement,
                                           UnitProcessor.OTHER_LENGTH,
                                           uctx);
        // 'stroke-linecap'
        v = (CSSPrimitiveValue)decl.getPropertyCSSValue
            (STROKE_LINECAP_PROPERTY);
        int linecap;
        switch(parseStrokeLinecapProperty(v.getStringValue())) {
        case LINECAP_BUTT:
            linecap = BasicStroke.CAP_BUTT;
            break;
        case LINECAP_ROUND:
            linecap = BasicStroke.CAP_ROUND;
            break;
        case LINECAP_SQUARE:
            linecap = BasicStroke.CAP_SQUARE;
            break;
        default:
            throw new Error(); // can't be reached
        }
        // 'stroke-linejoin'
        v = (CSSPrimitiveValue)decl.getPropertyCSSValue
            (STROKE_LINEJOIN_PROPERTY);
        int linejoin;
        switch(parseStrokeLinejoinProperty(v.getStringValue())) {
        case LINEJOIN_MITER:
            linejoin = BasicStroke.JOIN_MITER;
            break;
        case LINEJOIN_BEVEL:
            linejoin = BasicStroke.JOIN_BEVEL;
            break;
        case LINEJOIN_ROUND:
            linejoin = BasicStroke.JOIN_ROUND;
            break;
        default:
            throw new Error(); // can't be reached
        }
        // 'stroke-miterlimit'
        v = (CSSPrimitiveValue)decl.getPropertyCSSValue
            (STROKE_MITERLIMIT_PROPERTY);
        float miterlimit = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
        miterlimit = (miterlimit < 1)? 1 : miterlimit;
        // 'stroke-dasharray'
        CSSValue vv = decl.getPropertyCSSValue(STROKE_DASHARRAY_PROPERTY);
        float [] dashArray = null;
        float dashOffset = 0;
        if (vv.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            CSSValueList l = (CSSValueList) vv;
            int length = l.getLength();
            dashArray = new float[length];
            for (int i=0; i < length; ++i) {
                v = (CSSPrimitiveValue) l.item(i);
                type = v.getPrimitiveType();
                dashArray[i] =
                    UnitProcessor.cssToUserSpace(type,
                                                 v.getFloatValue(type),
                                                 svgElement,
                                                 UnitProcessor.OTHER_LENGTH,
                                                 uctx);
            }
            // 'stroke-dashoffset'
            v = (CSSPrimitiveValue)decl.getPropertyCSSValue
                (STROKE_DASHOFFSET_PROPERTY);
            type = v.getPrimitiveType();
            dashOffset =
                UnitProcessor.cssToUserSpace(type,
                                             v.getFloatValue(type),
                                             svgElement,
                                             UnitProcessor.OTHER_LENGTH,
                                             uctx);
        }
        Stroke stroke = new BasicStroke(width,
                                        linecap,
                                        linejoin,
                                        miterlimit,
                                        dashArray,
                                        dashOffset);
        painter.setStroke(stroke);
        return painter;
    }

    /**
     * Returns the <tt>ShapePainter</tt> used to fill the specified
     * Element using the specified context and css style declaration.
     * @param svgElement the SVG Element
     * @param ctx the bridge context
     * @param decl the css style declaration
     * @param uctx the UnitProcessor context
     */
    public static ShapePainter convertFill(SVGElement svgElement,
                                           BridgeContext ctx,
                                           CSSStyleDeclaration decl,
                                           UnitProcessor.Context uctx) {
        GVTFactory f = ctx.getGVTFactory();
        FillShapePainter painter = null;
        // resolve the paint of the FillShapePainter
        CSSPrimitiveValue v
            = (CSSPrimitiveValue) decl.getPropertyCSSValue(FILL_PROPERTY);
        switch(v.getPrimitiveType()) {
        case CSSPrimitiveValue.CSS_IDENT:
            return null; // stroke:'none'
        case CSSPrimitiveValue.CSS_RGBCOLOR:
            painter = f.createFillShapePainter();
            CSSPrimitiveValue vv = (CSSPrimitiveValue)
                decl.getPropertyCSSValue(FILL_OPACITY_PROPERTY);
            float opacity = convertOpacity(vv);
            Color c = convertColor(v.getRGBColorValue(), opacity);
            painter.setPaint(c);
            break;
        case CSSPrimitiveValue.CSS_URI:
            // <!> FIXME : TODO !!!
            throw new Error("Not yet implemented");
        }
        return painter;
    }

    /**
     * Returns the <tt>Paint</tt> used to fill the specified
     * Element using the specified context and css style declaration.
     * @param decl the css style declaration
     */
    public static Paint convertFillToPaint(CSSStyleDeclaration decl) {
        CSSPrimitiveValue v
            = (CSSPrimitiveValue) decl.getPropertyCSSValue(FILL_PROPERTY);
        switch(v.getPrimitiveType()) {
        case CSSPrimitiveValue.CSS_IDENT:
            return null; // fill:'none'
        case CSSPrimitiveValue.CSS_RGBCOLOR:
            CSSPrimitiveValue vv = (CSSPrimitiveValue)
                decl.getPropertyCSSValue(FILL_OPACITY_PROPERTY);
            float opacity = convertOpacity(vv);
            Color c = convertColor(v.getRGBColorValue(), opacity);
            return c;
        case CSSPrimitiveValue.CSS_URI:
            // <!> FIXME : TODO !!!
        }
        throw new Error("Not yet implemented");
    }

    /**
     * Returns the <tt>Paint</tt> used to fill the specified
     * Element using the specified context and css style declaration.
     * @param decl the css style declaration
     */
    public static Color convertFloodColorToPaint(CSSStyleDeclaration decl) {
        CSSPrimitiveValue v
            = (CSSPrimitiveValue) decl.getPropertyCSSValue
            (FLOOD_COLOR_PROPERTY);
        CSSPrimitiveValue vv = (CSSPrimitiveValue)
            decl.getPropertyCSSValue(FLOOD_OPACITY_PROPERTY);
        float opacity = convertOpacity(vv);

        System.out.println("Flood color: " + v.getCssText());
        return convertColor(v.getRGBColorValue(), opacity);
    }

    /**
     * Returns the value of one color component (0 <= result <= 255).
     */
    protected static int resolveColorComponent(CSSPrimitiveValue v) {
        switch(v.getPrimitiveType()) {
        case CSSPrimitiveValue.CSS_PERCENTAGE:
            float f = v.getFloatValue(CSSPrimitiveValue.CSS_PERCENTAGE);
            f = (f > 100f) ? 100f : (f < 0f) ? 0f : f;
            return Math.round(255f * f / 100f);
        case CSSPrimitiveValue.CSS_NUMBER:
            f = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
            f = (f > 255f) ? 255f : (f < 0f) ? 0f : f;
            return Math.round(f);
        default:
            throw new IllegalArgumentException
                (Messages.formatMessage
                 ("css.primitive.type",
                  new Object[] { new Integer(v.getPrimitiveType()) }));
        }
    }

    /**
     * Represents stroke-linecap property 'butt' value.
     */
    public final static int LINECAP_BUTT = 0;

    /**
     * Represents stroke-linecap property 'round' value.
     */
    public final static int LINECAP_ROUND = 1;

    /**
     * Represents stroke-linecap property 'square' value.
     */
    public final static int LINECAP_SQUARE = 2;

    /**
     * Parses the specified string that describes the stroke-linecap property.
     * @param the string to parse
     * @return LINECAP_BUTT |  LINECAP_ROUND |  LINECAP_SQUARE
     */
    public static int parseStrokeLinecapProperty(String s) {
        switch(s.charAt(0)) {
        case 'b':
            return LINECAP_BUTT;
        case 'r':
            return LINECAP_ROUND;
        case 's':
            return LINECAP_SQUARE;
        default:
            throw new Error(); // can't be reached
        }
    }

    /**
     * Represents stroke-linejoin property 'bevel' value.
     */
    public final static int LINEJOIN_BEVEL = 0;

    /**
     * Represents stroke-linejoin property 'round' value.
     */
    public final static int LINEJOIN_ROUND = 1;

    /**
     * Represents stroke-linejoin property 'square' value.
     */
    public final static int LINEJOIN_MITER = 2;

    /**
     * Parses the specified string that describes the stroke-linejoin property.
     * @param the string to parse
     * @return LINEJOIN_MITER | LINEJOIN_ROUND | LINEJOIN_BEVEL
     */
    public static int parseStrokeLinejoinProperty(String s) {
        switch(s.charAt(0)) {
        case 'm':
            return LINEJOIN_MITER;
        case 'r':
            return LINEJOIN_ROUND;
        case 'b':
            return LINEJOIN_BEVEL;
        default:
            throw new Error(); // can't be reached
        }
    }

    /**
     * Converts the font-size CSS value to a float value.
     * @param svgElement the SVG Element
     * @param ctx the bridge context
     * @param decl the css style declaration
     * @param uctx the UnitProcessor context
     */
    public static float convertFontSize(SVGElement svgElement,
                                        BridgeContext ctx,
                                        CSSStyleDeclaration decl,
                                        UnitProcessor.Context uctx) {
        CSSPrimitiveValue v;
        v = (CSSPrimitiveValue)decl.getPropertyCSSValue(FONT_SIZE_PROPERTY);

        short t = v.getPrimitiveType();
        switch (t) {
        case CSSPrimitiveValue.CSS_IDENT:
            float fs = uctx.getMediumFontSize();
            fs = parseFontSize(v.getStringValue(), fs);
            return UnitProcessor.cssToUserSpace(CSSPrimitiveValue.CSS_PT,
                                                fs,
                                                svgElement,
                                                UnitProcessor.VERTICAL_LENGTH,
                                                uctx);
        default:
            return UnitProcessor.cssToUserSpace(t,
                                                v.getFloatValue(t),
                                                svgElement,
                                                UnitProcessor.VERTICAL_LENGTH,
                                                uctx);
        }
    }

    /**
     * Parses a font-size identifier.
     * @param s The font size identifier.
     * @param m The medium font size.
     * @return The computed font size.
     */
    public static float parseFontSize(String s, float m) {
        switch (s.charAt(0)) {
        case 'm':
            return m;
        case 's':
            return (float)(m / 1.2);
        case 'l':
            return (float)(m * 1.2);
        default: // 'x'
            switch (s.charAt(1)) {
            case 'x':
                switch (s.charAt(3)) {
                case 's':
                    return (float)(((m / 1.2) / 1.2) / 1.2);
                default: // 'l'
                    return (float)(m * 1.2 * 1.2 * 1.2);
                }
            default: // '-'
                switch (s.charAt(2)) {
                case 's':
                    return (float)((m / 1.2) / 1.2);
                default: // 'l'
                    return (float)(m * 1.2 * 1.2);
                }
            }
        }
    }

    /**
     * Returns the <tt>Filter</tt> referenced by the input
     * <tt>GraphicsNode</tt>.
     */
    public static Filter convertFilter(Element element,
                                       GraphicsNode node,
                                       BridgeContext ctx){
        CSSStyleDeclaration decl
            = ctx.getViewCSS().getComputedStyle(element, null);

        //
        // Build filter based on filter 
        //
        CSSPrimitiveValue filterValue
            = (CSSPrimitiveValue)decl.getPropertyCSSValue(ATTR_FILTER);
        String uriString = null;
        switch(filterValue.getPrimitiveType()){
        case CSSPrimitiveValue.CSS_IDENT:
            // NONE
            break;
        case CSSPrimitiveValue.CSS_URI:
            uriString = filterValue.getStringValue();
            break;
        default:
            throw new Error("filterValue's primitive type is: " +
                            filterValue.getPrimitiveType());
        }
        
        Filter filter = null;

        if(uriString != null){
            if(uriString.startsWith("#")){
                uriString = uriString.substring(1);
                Element filterElement 
                    = element.getOwnerDocument().getElementById(uriString);
                if(filterElement != null){
                    FilterBridge filterBridge
                        = (FilterBridge)ctx.getBridge(filterElement);
                    
                    filter = filterBridge.create(node,
                                                 ctx,
                                                 filterElement,
                                                 element,
                                                 null,   // in
                                                 null,   // filterRegion
                                                 null);  // filterMap
                } else {
                    System.out.println("Could not find : " + uriString +
                                       " in document");
                }
            }
        }

        return filter;
    }
    
}
