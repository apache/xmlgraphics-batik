/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.color.ICC_ColorSpace;
import java.awt.geom.Rectangle2D;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Map;

import org.apache.batik.css.AbstractViewCSS;
import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.Marker;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.StrokeShapePainter;
import org.apache.batik.ext.awt.image.renderable.Clip;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.color.ICCColorSpaceExt;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.gvt.filter.BackgroundRable8Bit;
import org.apache.batik.ext.awt.image.renderable.FloodRable8Bit;
import org.apache.batik.ext.awt.image.renderable.FilterAlphaRable;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGICCColor;
import org.w3c.dom.svg.SVGColor;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGNumberList;
import org.w3c.dom.svg.SVGPaint;

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
     * Returns the View CSS associated to the specified element.
     * @param e the element
     */
    public static ViewCSS getViewCSS(Element e) {
        return (ViewCSS)((SVGOMDocument)e.getOwnerDocument()).getDefaultView();
    }

    /**
     * Returns the computed style of the specified element.
     * @param e the element
     */
    public static CSSStyleDeclaration getComputedStyle(Element e) {
        return getViewCSS(e).getComputedStyle(e, null);
    }

    /**
     * Partially computes the style in the use tree and set it in
     * the target tree.
     * Note: This method must be called only when 'def' has been added
     * to the tree.
     */
    public static void computeStyleAndURIs(Element use, ViewCSS uv,
                                    Element def, ViewCSS dv, URL url) {
        String href = XLinkSupport.getXLinkHref(def);

        if (!href.equals("")) {
            try {
                XLinkSupport.setXLinkHref(def, new URL(url, href).toString());
            } catch (MalformedURLException e) {
            }
        }

        CSSOMReadOnlyStyleDeclaration usd;
        AbstractViewCSS uview = (AbstractViewCSS)uv;

        usd = (CSSOMReadOnlyStyleDeclaration)uview.computeStyle(use, null);
        try {
            updateURIs(usd, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ((AbstractViewCSS)dv).setComputedStyle(def, null, usd);

        for (Node un = use.getFirstChild(), dn = def.getFirstChild();
             un != null;
             un = un.getNextSibling(), dn = dn.getNextSibling()) {
            if (un.getNodeType() == Node.ELEMENT_NODE) {
                computeStyleAndURIs((Element)un, uv, (Element)dn, dv, url);
            }
        }
    }

    /**
     * Updates the URIs in the given style declaration.
     */
    protected static void updateURIs(CSSOMReadOnlyStyleDeclaration sd, URL url)
        throws MalformedURLException {
        int len = sd.getLength();
        for (int i = 0; i < len; i++) {
            String name = sd.item(i);
            CSSValue val = sd.getLocalPropertyCSSValue(name);
            if (val != null &&
                val.getCssValueType() ==
                CSSPrimitiveValue.CSS_PRIMITIVE_VALUE) {
                CSSPrimitiveValue pv = (CSSPrimitiveValue)val;
                if (pv.getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
                    CSSOMReadOnlyValue v =
                        new CSSOMReadOnlyValue
                    (new ImmutableString(CSSPrimitiveValue.CSS_URI,
                                         new URL(url, pv.getStringValue()).toString()));
                    sd.setPropertyCSSValue(name, v,
                                           sd.getLocalPropertyPriority(name),
                                       sd.getLocalPropertyOrigin(name));
                }
            }
        }
    }

    /**
     * Returns the viewport
     */
    public static Rectangle2D convertEnableBackground(SVGElement svgElement,
                                                      CSSStyleDeclaration decl,
                                                      UnitProcessor.Context uctx) {
        CSSValue val;
        val = decl.getPropertyCSSValue(CSS_ENABLE_BACKGROUND_PROPERTY);

        if (val.getCssValueType() != val.CSS_VALUE_LIST) {
            return null; // accumulate
        }
        try {
            CSSValueList lst = (CSSValueList)val;
            int len = lst.getLength();
            switch (len) {
            case 1:
                return CompositeGraphicsNode.VIEWPORT; // new
            case 5:
                CSSPrimitiveValue v = (CSSPrimitiveValue)lst.item(1);
                short type = v.getPrimitiveType();
                float x = UnitProcessor.cssToUserSpace(type,
                                                       v.getFloatValue(type),
                                                       svgElement,
                                                       UnitProcessor.OTHER_LENGTH,
                                                       uctx);

                v = (CSSPrimitiveValue)lst.item(2);
                type = v.getPrimitiveType();
                float y = UnitProcessor.cssToUserSpace(type,
                                                       v.getFloatValue(type),
                                                       svgElement,
                                                       UnitProcessor.OTHER_LENGTH,
                                                       uctx);

                v = (CSSPrimitiveValue)lst.item(3);
                type = v.getPrimitiveType();
                float w = UnitProcessor.cssToUserSpace(type,
                                                       v.getFloatValue(type),
                                                       svgElement,
                                                       UnitProcessor.OTHER_LENGTH,
                                                       uctx);
                if (w < 0) {
                    return null;
                }

                v = (CSSPrimitiveValue)lst.item(4);
                type = v.getPrimitiveType();
                float h = UnitProcessor.cssToUserSpace(type,
                                                       v.getFloatValue(type),
                                                       svgElement,
                                                       UnitProcessor.OTHER_LENGTH,
                                                       uctx);

                if (h < 0) {
                    return null;
                }
                return new Rectangle2D.Float(x, y, w, h);
            }
        } catch (Exception e) {
            // CSS errors must be silently ignored.
        }
        return null;
    }

    /**
     * Initializes the composite corresponding to the
     * opacity attribute in the input <tt>GraphicsNode</tt>
     */
    public static Composite convertOpacityToComposite(CSSPrimitiveValue val) {
        float opacity = convertOpacity(val);
        Composite composite = null;
        if (opacity > 0) {
            if (opacity < 1) {
                composite = AlphaComposite.getInstance
                    (AlphaComposite.SRC_OVER, opacity);
            } else {
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
     * Returns true if the specified element is visible, false
     * otherwise. Check the 'visibility' property.
     * @param e the element
     */
    public static boolean convertVisibility(Element e) {
        CSSStyleDeclaration decl = getComputedStyle(e);
        CSSValue v = decl.getPropertyCSSValue(CSS_VISIBILITY_PROPERTY);
        if (v.getCssValueType() == CSSValue.CSS_INHERIT) {
            return true;
        } else {
            return (((CSSPrimitiveValue)v).getStringValue().charAt(0) == 'v');
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
      * Returns the <tt>Shape</tt> referenced by the input element's
      * <tt>clip-path</tt> attribute.
      *
      * @param clipedElement the element with the clip-path CSS attribute
      * @param gn the graphics node that represents the clipedElement
      * @param ctx the context to use
      */
    public static Clip convertClipPath(Element clipedElement,
                                       GraphicsNode gn,
                                       BridgeContext ctx) {

        CSSStyleDeclaration decl = getComputedStyle(clipedElement);

         CSSPrimitiveValue clipValue =
             (CSSPrimitiveValue)decl.getPropertyCSSValue(CSS_CLIP_PATH_PROPERTY);

         switch(clipValue.getPrimitiveType()){
         case CSSPrimitiveValue.CSS_IDENT:
             return null; // 'clip-path:none'

         case CSSPrimitiveValue.CSS_URI:
             String uriString = clipValue.getStringValue();
             URIResolver ur =
                 new URIResolver((SVGDocument)clipedElement.getOwnerDocument(),
                                 ctx.getDocumentLoader());

             Element clipPathElement = null;
             try {
                 clipPathElement = ur.getElement(uriString);
             } catch (Exception ex) {
                 throw new IllegalAttributeValueException(
                     Messages.formatMessage("bad.uri",
                                            new Object[] {uriString}));
             }
             // Now use the bridge to create the Clip
             Bridge bridge = ctx.getBridge(clipPathElement);
             if (bridge == null || !(bridge instanceof ClipBridge)) {
                 throw new IllegalAttributeValueException(
                     Messages.formatMessage("clipPath.reference.illegal",
                                new Object[] {clipPathElement.getLocalName()}));
             }
             ClipBridge clipBridge = (ClipBridge)bridge;
             SVGOMDocument doc =
                 (SVGOMDocument)clipPathElement.getOwnerDocument();
             Clip clip = clipBridge.createClip(ctx,
                                               gn,
                                               clipPathElement,
                                               clipedElement);
             return clip;
         default:
             throw new Error(); // can't be reached
         }
    }

     /**
      * Returns the <tt>Mask</tt> referenced by the input element's
      * <tt>mask</tt> attribute.
      *
      * @param maskedElement the element with the mask CSS attribute
      * @param gn the graphics node that represents the maskedElement
      * @param ctx the context to use
      */
     public static Mask convertMask(Element maskedElement,
                                    GraphicsNode  gn,
                                     BridgeContext ctx) {

         CSSStyleDeclaration decl = getComputedStyle(maskedElement);

         CSSPrimitiveValue maskValue
             = (CSSPrimitiveValue)decl.getPropertyCSSValue(CSS_MASK_PROPERTY);

         switch(maskValue.getPrimitiveType()){
         case CSSPrimitiveValue.CSS_IDENT:
             return null; // 'mask:none'

         case CSSPrimitiveValue.CSS_URI:
             String uriString = maskValue.getStringValue();
             URIResolver ur =
                 new URIResolver((SVGDocument)maskedElement.getOwnerDocument(),
                                 ctx.getDocumentLoader());

             Element maskElement = null;
             try {
                 maskElement = ur.getElement(uriString);
             } catch (Exception ex) {
                 throw new IllegalAttributeValueException(
                     Messages.formatMessage("bad.uri",
                                            new Object[] {uriString}));
             }
             // Now use the bridge to create the Mask
             Bridge bridge = ctx.getBridge(maskElement);
             if (bridge == null || !(bridge instanceof MaskBridge)) {
                 throw new IllegalAttributeValueException(
                     Messages.formatMessage("mask.reference.illegal",
                                    new Object[] {maskElement.getLocalName()}));
             }
             MaskBridge maskBridge = (MaskBridge)bridge;
             SVGOMDocument doc = (SVGOMDocument)maskElement.getOwnerDocument();
             Mask mask =  maskBridge.createMask(gn,
                                                ctx,
                                                maskElement,
                                                maskedElement);
             return mask;
         default:
             throw new Error(); // can't be reached
         }

     }

    /**
     * Returns the <tt>ShapePainter</tt> for the specified Element
     * using the specified context and css style declaration.
     * @param shape shape to be stroked and filled
     * @param svgElement the SVG Element
     * @param ctx the bridge context
     * @param decl the css style declaration
     * @param uctx the UnitProcessor context
     */
    public static ShapePainter convertStrokeAndFill(Shape shape,
                                                    SVGElement svgElement,
                                                    GraphicsNode node,
                                                    BridgeContext ctx,
                                                    CSSStyleDeclaration decl,
                                                    UnitProcessor.Context uctx){

        // resolve fill
        ShapePainter fillPainter = convertFill(shape, svgElement, node, ctx,
                                               decl, uctx);
        // resolve stroke
        ShapePainter strokePainter = convertStroke(shape, svgElement, node, ctx,
                                                   decl, uctx);
        ShapePainter painter = null;
        if (fillPainter != null && strokePainter != null) {
            CompositeShapePainter comp = new CompositeShapePainter(shape);
            comp.addShapePainter(fillPainter);
            comp.addShapePainter(strokePainter);
            painter = comp;
        } else if (fillPainter != null) {
            painter = fillPainter;
        } else if (strokePainter != null) {
            painter = strokePainter;
        }
        return painter;
    }

    /**
     * Returns the <tt>ShapePainter</tt> used to draw the outline of
     * the specified Element using the specified context and css style
     * declaration.
     * @param shape shape to be stroked.
     * @param svgElement the SVG Element
     * @param ctx the bridge context
     * @param decl the css style declaration
     * @param uctx the UnitProcessor context
     */
    public static ShapePainter convertStroke(Shape shape, SVGElement svgElement,
                                             GraphicsNode node,
                                             BridgeContext ctx,
                                             CSSStyleDeclaration decl,
                                             UnitProcessor.Context uctx) {

        Stroke stroke = convertStrokeToBasicStroke(svgElement, ctx, decl, uctx);
        Paint paint = convertStrokeToPaint(svgElement, node, ctx, decl, uctx);
        StrokeShapePainter painter = new StrokeShapePainter(shape);
        painter.setStroke(stroke);
        painter.setPaint(paint);
        return painter;
    }

    /**
     * Returns a Paint object that corresponds to the various
     * stroke attributes in the input element
     */
    public static Paint convertStrokeToPaint(SVGElement element,
                                             GraphicsNode node,
                                             BridgeContext ctx,
                                             CSSStyleDeclaration decl,
                                             UnitProcessor.Context uctx) {
        CSSValue val = decl.getPropertyCSSValue(CSS_STROKE_PROPERTY);
        CSSPrimitiveValue vv = (CSSPrimitiveValue)decl.getPropertyCSSValue
            (CSS_STROKE_OPACITY_PROPERTY);
        float opacity = convertOpacity(vv);

        return convertPaintDefinitionToPaint(element, node, ctx, decl, uctx,
                                             val, opacity, true);
    }

    /**
     * @param val the value of the Paint definition property
     * @param opacity opacity associated with the paint definition
     */
    public static Paint convertPaintDefinitionToPaint(SVGElement element,
                                                      GraphicsNode node,
                                                      BridgeContext ctx,
                                                      CSSStyleDeclaration decl,
                                                      UnitProcessor.Context uctx,
                                                      CSSValue val, float opacity,
                                                      boolean isStroke){

        if (val.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue v = (CSSPrimitiveValue)val;

            switch(v.getPrimitiveType()) {
            case CSSPrimitiveValue.CSS_IDENT:
                return null; // 'stroke:none'

            case CSSPrimitiveValue.CSS_RGBCOLOR:
                return convertColor(v.getRGBColorValue(), opacity);
            case CSSPrimitiveValue.CSS_URI:
                return convertURIToPaint(element, node, ctx, decl,
                                         uctx, v.getStringValue(), isStroke);
            default:
                throw new Error(); // can't be reached
            }
        } else { // SVGPaint
            SVGPaint p = (SVGPaint)val;
            Paint paint = null;

            switch (p.getPaintType()) {
            case SVGPaint.SVG_PAINTTYPE_RGBCOLOR_ICCCOLOR:
                return convertRGBICCColor(element, p, opacity, ctx);

            case SVGPaint.SVG_PAINTTYPE_URI_NONE:
                // If silentConvertURIPaint returns null, this
                // will return null, hence be equivalent to a 
                // value of none.
                return silentConvertURIPaint(element, node, ctx, decl, 
                                             uctx, p, opacity, isStroke);
                
            case SVGPaint.SVG_PAINTTYPE_URI_CURRENTCOLOR:
                paint = silentConvertURIPaint(element, node, ctx, decl, 
                                              uctx, p, opacity, isStroke);
                if(paint == null){
                    CSSValue vvv = decl.getPropertyCSSValue(CSS_COLOR_PROPERTY);
                    paint = convertColor(((CSSPrimitiveValue)vvv).getRGBColorValue(), opacity);
                }
                return paint;

            case SVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR:
                paint = silentConvertURIPaint(element, node, ctx, decl, 
                                              uctx, p, opacity, isStroke);
                if(paint == null){
                    // Could not convert paint, fall back to RGB color definition
                    paint = convertColor(p.getRGBColor(), opacity);
                }
                return paint;
            case SVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR_ICCCOLOR:
                paint = silentConvertURIPaint(element, node, ctx, decl,
                                              uctx, p, opacity, isStroke);
                if(paint == null){
                    paint = convertRGBICCColor(element, p, opacity, ctx);
                }
                return paint;
            default:
                throw new Error(); // can't be reached
            }
        }
    }

    /**
     * Converts the input Paint using it's URI. If the Paint cannot be converted, for
     * any reason, this returns null and does not throw any exception
     */
    public static Paint silentConvertURIPaint(SVGElement e, GraphicsNode node, BridgeContext ctx, 
                                              CSSStyleDeclaration decl, UnitProcessor.Context uctx, SVGPaint p, 
                                              float opacity, boolean isStroke){
        Paint paint = null;
        try{
            paint = convertURIToPaint(e, node, ctx, decl, uctx, p.getUri(), isStroke);
        }catch(Exception ex){
            // Could not convert paint. Jus return null
        }

        return paint;
    }

    /**
     * Returns a Color object that corresponds to the input Paint's ICC
     * color value or null if the  related color profile could not be used or loaded for any
     * reason.
     */
    public static Color convertRGBICCColor(Element e, SVGColor p, 
                                           float opacity,
                                           BridgeContext ctx){
        SVGICCColor iccColor = p.getICCColor();
        Color color = null;
        if(iccColor != null){
            color = convertICCColor(e, iccColor, opacity, ctx);
        }

        if(color == null){
            color = convertColor(p.getRGBColor(), opacity);
        }

        return color;
    }

    /**
     * Returns a Color object that corresponds to the input Paint's ICC
     * color value or null if the  related color profile could not be used or loaded for any
     * reason.
     */
    public static Color convertICCColor(Element e, SVGICCColor c, 
                                        float opacity,
                                        BridgeContext ctx){
        // 
        // Get ICC Profile's name
        //
        String iccProfileName = c.getColorProfile();
        if(iccProfileName == null){
            return null;
        }

        //
        // Now, ask the bridge to map the ICC profile name
        // to an ICC_Profile object
        //
        SVGColorProfileElementBridge profileBridge =
            (SVGColorProfileElementBridge)
            ctx.getBridge(SVG_NAMESPACE_URI,
                          SVG_COLOR_PROFILE_TAG);

        if(profileBridge == null){
            return null;
        }

        ICCColorSpaceExt profileCS
            = profileBridge.build(iccProfileName,
                                  ctx, e);

        if(profileCS == null){
            return null;
        }

        //
        // Now, convert the colors to an array of floats
        //
        float[] colorValue 
            = convertSVGNumberList(c.getColors());
        
        if(colorValue == null){
            return null;
        }

        for(int i=0; i<colorValue.length; i++){
            System.out.println("colorValue[" + i + "] = " + colorValue[i]);
            // colorValue[i] /= 255f;
        }

        System.out.println("opacity : " + opacity);

        // Convert values to RGB
        float rgb[] = profileCS.intendedToRGB(colorValue);
        for(int i=0; i<colorValue.length; i++){
            System.out.println("rgb[" + i + "] = " + rgb[i]);
        }

        return new Color(rgb[0], rgb[1], rgb[2], opacity);
    }

    /**
     * Converts an SVGNumberList into a float array
     */
    public static float[] convertSVGNumberList(SVGNumberList l){
        int n = l.getNumberOfItems();
        float fl[] = new float[n];
        try{
            for(int i=0; i<n; i++){
                fl[i] = l.getItem(i).getValue();
            }
        }catch(DOMException e){
            e.printStackTrace();
            fl = null;
        }
        
        return fl;
    }

    /**
     * Returns a Stoke object that corresponds to the various
     * stroke attributes in the input element
     */
    public static
        BasicStroke convertStrokeToBasicStroke(SVGElement svgElement,
                                               BridgeContext ctx,
                                               CSSStyleDeclaration decl,
                                               UnitProcessor.Context uctx) {
        // resolve the java.awt.Stroke of the StrokeShapePainter
        CSSPrimitiveValue v =
         (CSSPrimitiveValue)decl.getPropertyCSSValue(CSS_STROKE_WIDTH_PROPERTY);
        short type = v.getPrimitiveType();
        // 'stroke-width'
        float width
            = UnitProcessor.cssToUserSpace(type,
                                           v.getFloatValue(type),
                                           svgElement,
                                           UnitProcessor.OTHER_LENGTH,
                                           uctx);

        // 'stroke-linecap'
        v = (CSSPrimitiveValue)decl.getPropertyCSSValue(CSS_STROKE_LINECAP_PROPERTY);
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
        v = (CSSPrimitiveValue)decl.getPropertyCSSValue(CSS_STROKE_LINEJOIN_PROPERTY);
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
        v = (CSSPrimitiveValue)decl.getPropertyCSSValue(CSS_STROKE_MITERLIMIT_PROPERTY);
        float miterlimit = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
        miterlimit = (miterlimit < 1)? 1 : miterlimit;

        // 'stroke-dasharray'
        CSSValue vv = decl.getPropertyCSSValue(CSS_STROKE_DASHARRAY_PROPERTY);
        float [] dashArray = null;
        float dashOffset = 0;
        if (vv.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            CSSValueList l = (CSSValueList) vv;
            int length = l.getLength();
            dashArray = new float[length];
            float dashArraySum = 0;
            for (int i=0; i < length; ++i) {
                v = (CSSPrimitiveValue) l.item(i);
                type = v.getPrimitiveType();
                dashArray[i] =
                    UnitProcessor.cssToUserSpace(type,
                                                 v.getFloatValue(type),
                                                 svgElement,
                                                 UnitProcessor.OTHER_LENGTH,
                                                 uctx);
                dashArraySum += dashArray[i];
            }

            if(dashArraySum == 0){
                // The spec. says that if the sum is zero, the effect is 
                // as if there was no dashes, i.e., we have a solid stroke
                dashArray = null;
            }
            else{
                // 'stroke-dashoffset'
                v = (CSSPrimitiveValue)decl.getPropertyCSSValue
                    (CSS_STROKE_DASHOFFSET_PROPERTY);
                type = v.getPrimitiveType();
                dashOffset =
                    UnitProcessor.cssToUserSpace(type,
                                                 v.getFloatValue(type),
                                                 svgElement,
                                                 UnitProcessor.OTHER_LENGTH,
                                                 uctx);
            }
        }

        BasicStroke stroke = new BasicStroke(width,
                                             linecap,
                                             linejoin,
                                             miterlimit,
                                             dashArray,
                                             dashOffset);

        return stroke;
    }

    /**
     * Returns the <tt>ShapePainter</tt> used to fill the specified
     * Element using the specified context and css style declaration.
     * @param shape Shape to be filled
     * @param svgElement the SVG Element
     * @param ctx the bridge context
     * @param decl the css style declaration
     * @param uctx the UnitProcessor context
     */
    public static ShapePainter convertFill(Shape shape,
                                           SVGElement svgElement,
                                           GraphicsNode node,
                                           BridgeContext ctx,
                                           CSSStyleDeclaration decl,
                                           UnitProcessor.Context uctx) {

        FillShapePainter painter = null;
        Paint fillPaint = convertFillToPaint(svgElement,
                                             node,
                                             ctx,
                                             decl,
                                             uctx);
        if(fillPaint != null){
            painter = new FillShapePainter(shape);
            painter.setPaint(fillPaint);
        }

        return painter;
    }

    /**
     * Converts the element referenced by uri into a Paint object
     */
    public static Paint convertURIToPaint(SVGElement svgElement,
                                          GraphicsNode node,
                                          BridgeContext ctx,
                                          CSSStyleDeclaration decl,
                                          UnitProcessor.Context uctx,
                                          String uri, boolean isStroke){

        URIResolver ur =
            new URIResolver((SVGDocument)svgElement.getOwnerDocument(),
                            ctx.getDocumentLoader());

        Element paintElement = null;
        try {
            paintElement = ur.getElement(uri);
        } catch (Exception ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("bad.uri",
                                       new Object[] {uri}));
        }

        Bridge bridge = ctx.getBridge(paintElement);
        if (bridge == null || !(bridge instanceof PaintBridge)) {
            throw new IllegalAttributeValueException(
                    Messages.formatMessage("paint.reference.illegal",
                                   new Object[] {paintElement.getLocalName()}));

        }
        PaintBridge paintBridge = (PaintBridge)bridge;
        SVGOMDocument doc = (SVGOMDocument)paintElement.getOwnerDocument();
        Paint paint = null;
        if(isStroke){
            paint = paintBridge.createStrokePaint(ctx,
                                                  node,
                                                  svgElement,
                                                  paintElement);
        }
        else{
            paint = paintBridge.createFillPaint(ctx,
                                                node,
                                                svgElement,
                                                paintElement);
        }            
        return paint;
    }

    /**
     * Returns the <tt>Paint</tt> used to fill the specified
     * Element using the specified context and css style declaration.
     * @param decl the css style declaration
     */
    public static Paint convertFillToPaint(SVGElement element,
                                           GraphicsNode node,
                                           BridgeContext ctx,
                                           CSSStyleDeclaration decl,
                                           UnitProcessor.Context uctx) {

        CSSValue val = decl.getPropertyCSSValue(CSS_FILL_PROPERTY);
        CSSPrimitiveValue vv =
            (CSSPrimitiveValue)decl.getPropertyCSSValue
            (CSS_FILL_OPACITY_PROPERTY);
        float opacity = convertOpacity(vv);
        
        return convertPaintDefinitionToPaint(element, node, ctx, decl, uctx, 
                                             val, opacity, false);
    }

    /**
     * Converts a marker property to a Marker object
     */
    public static Marker convertMarker(SVGElement paintedElement,
                                       String markerProperty,
                                       BridgeContext ctx,
                                       CSSStyleDeclaration decl,
                                       UnitProcessor.Context uctx){
        CSSPrimitiveValue v =
            (CSSPrimitiveValue) decl.getPropertyCSSValue(markerProperty);

        if(v == null){
            return null;
        }

        switch(v.getPrimitiveType()){
        case CSSPrimitiveValue.CSS_IDENT:
            // value is 'none'
            return null;
        case CSSPrimitiveValue.CSS_URI:
            return convertURIToMarker(v.getStringValue(),
                                      paintedElement,
                                      ctx, decl, uctx);
        default:
            throw new Error(); // can't be reached.
        }
    }

    /**
     * Converts a URI to a Marker
     */
    public static Marker convertURIToMarker(String markerURI,
                                            SVGElement paintedElement,
                                            BridgeContext ctx,
                                            CSSStyleDeclaration decl,
                                            UnitProcessor.Context uctx){
        URIResolver ur =
            new URIResolver((SVGDocument)paintedElement.getOwnerDocument(),
                            ctx.getDocumentLoader());

        Element markerElement = null;
        try {
            markerElement = ur.getElement(markerURI);
        } catch (Exception ex) {
            throw new IllegalAttributeValueException
                (Messages.formatMessage("bad.uri",
                                        new Object[] {markerURI}));
        }

        Bridge bridge = ctx.getBridge(markerElement);
        if ((bridge == null) || !(bridge instanceof MarkerBridge)){
            throw new IllegalAttributeValueException
                    (Messages.formatMessage("marker.reference.illegal",
                                           new Object[] {markerElement.getLocalName()}));
        }

        MarkerBridge markerBridge = (MarkerBridge)bridge;
        Marker marker = markerBridge.buildMarker(ctx,
                                                 markerElement,
                                                 paintedElement);
        return marker;

    }

    /**
     * Returns the <tt>Paint</tt> used to fill the specified
     * Element using the specified context and css style declaration.
     * @param decl the css style declaration
     */
    public static Color convertFloodColorToPaint(Element e, CSSStyleDeclaration decl,
                                                 BridgeContext ctx) {
        CSSValue val = decl.getPropertyCSSValue(CSS_FLOOD_COLOR_PROPERTY);
        CSSPrimitiveValue vv =
            (CSSPrimitiveValue)decl.getPropertyCSSValue(CSS_FLOOD_OPACITY_PROPERTY);
        float opacity = convertOpacity(vv);

        if (val.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue v = (CSSPrimitiveValue)val;
            return convertColor(v.getRGBColorValue(), opacity);
        } else { // SVGColor
            SVGColor c = (SVGColor)val;
            return convertRGBICCColor(e, c, 
                                      opacity, ctx);
                                      
        }
    }

    /**
     * Returns the <tt>Color</tt> corresponding to the lighting-color
     * attribute.
     * @param decl the css style declaration
     */
    public static Color convertLightingColor(Element e, CSSStyleDeclaration decl,
                                             BridgeContext ctx) {
        CSSValue val = decl.getPropertyCSSValue(CSS_LIGHTING_COLOR_PROPERTY);

        if (val.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue v = (CSSPrimitiveValue)val;
            return convertColor(v.getRGBColorValue(), 1);
        } else { // SVGPaint
            SVGColor c = (SVGColor)val;
            return convertRGBICCColor(e, c, 
                                      1, ctx);
                                      
        }
    }

    /**
     * Returns the <tt>Color</tt> corresponding to the stop
     * attributes.
     * @param decl the css style declaration
     */
    public static Color convertStopColorToPaint(Element e, CSSStyleDeclaration decl, 
                                                float extraOpacity, BridgeContext ctx) {
        CSSValue val = decl.getPropertyCSSValue(CSS_STOP_COLOR_PROPERTY);
        CSSPrimitiveValue vv =
            (CSSPrimitiveValue)decl.getPropertyCSSValue(CSS_STOP_OPACITY_PROPERTY);
        float opacity = extraOpacity*convertOpacity(vv);

        if (val.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue v = (CSSPrimitiveValue)val;
            return convertColor(v.getRGBColorValue(), opacity);
        } else { // SVGColor
            SVGColor c = (SVGColor)val;
            return convertRGBICCColor(e, c, 
                                      opacity, ctx);
        }
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
            throw new Error(); // can't be reached
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
        v = (CSSPrimitiveValue)decl.getPropertyCSSValue(CSS_FONT_SIZE_PROPERTY);

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

        CSSStyleDeclaration decl = getComputedStyle(element);

        CSSPrimitiveValue filterValue
            = (CSSPrimitiveValue)decl.getPropertyCSSValue(CSS_FILTER_PROPERTY);

        switch(filterValue.getPrimitiveType()){
        case CSSPrimitiveValue.CSS_IDENT:
            return null; // 'filter:none'
        case CSSPrimitiveValue.CSS_URI:
            String uriString = filterValue.getStringValue();
            URIResolver ur =
                new URIResolver((SVGDocument)element.getOwnerDocument(),
                                ctx.getDocumentLoader());

            Element filterElement = null;
            try {
                filterElement = ur.getElement(uriString);
            } catch (Exception ex) {
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("bad.uri",
                                           new Object[] {uriString}));
            }
            Bridge bridge = ctx.getBridge(filterElement);
            if (bridge == null || !(bridge instanceof FilterBridge)) {
                 throw new IllegalAttributeValueException(
                     Messages.formatMessage("filter.reference.illegal",
                                  new Object[] {filterElement.getLocalName()}));
            }
            FilterBridge filterBridge = (FilterBridge)bridge;
            SVGOMDocument doc = (SVGOMDocument)filterElement.getOwnerDocument();
            Filter filter = filterBridge.create(node,
                                                ctx,
                                                filterElement,
                                                element,
                                                null,   // in
                                                null,   // filterRegion
                                                null);  // filterMap
            return filter;
        default:
            throw new Error(); // can't be reached
        }
    }

    public static Filter getFilterSource(GraphicsNode  node,
                                         String        inAttr,
                                         BridgeContext ctx,
                                         Element       filteredElement,
                                         Filter        in,
                                         Map           filterMap) {

        int inValue = SVGUtilities.parseInAttribute(inAttr);

        switch (inValue) {
        case SVGUtilities.EMPTY:
            return in;

        case SVGUtilities.IDENTIFIER:
            return (Filter)filterMap.get(inAttr);

        case SVGUtilities.SOURCE_GRAPHIC:
            return (Filter)filterMap.get(VALUE_SOURCE_GRAPHIC);

        case SVGUtilities.SOURCE_ALPHA:
            in = (Filter)filterMap.get(VALUE_SOURCE_GRAPHIC);
            in =  new FilterAlphaRable(in);
            return in;

        case SVGUtilities.FILL_PAINT: {
            CSSStyleDeclaration cssDecl = getComputedStyle(filteredElement);
            UnitProcessor.Context uctx
                = new DefaultUnitProcessorContext(ctx, cssDecl);
            Paint paint = convertFillToPaint((SVGElement)filteredElement,
                                             node, ctx, cssDecl, uctx);
            if (paint == null) {
                // create a transparent flood
                paint = new Color(0, 0, 0, 0);
            }
            return new FloodRable8Bit(infiniteFilterRegion, paint);
        }

        case SVGUtilities.STROKE_PAINT: {
            CSSStyleDeclaration cssDecl = getComputedStyle(filteredElement);
            UnitProcessor.Context uctx
                = new DefaultUnitProcessorContext(ctx, cssDecl);
            Paint paint = convertStrokeToPaint((SVGElement)filteredElement,
                                               node, ctx, cssDecl, uctx);
            return new FloodRable8Bit(infiniteFilterRegion, paint);
        }

        case SVGUtilities.BACKGROUND_IMAGE:
            return new BackgroundRable8Bit(node,
                                 ctx.getGraphicsNodeRenderContext());

        case SVGUtilities.BACKGROUND_ALPHA:
            in = new BackgroundRable8Bit(node,
                                 ctx.getGraphicsNodeRenderContext());
            in = new FilterAlphaRable(in);
            return in;

        default:
            throw new Error(); // can't be reached
        }
    }

    // This is a bit of a hack but we set the flood bounds to
    // -floatmax/2 -> floatmax/2 (should cover the area ok).
    static Rectangle2D infiniteFilterRegion
        = new Rectangle2D.Float(-Float.MAX_VALUE/2,
                                -Float.MAX_VALUE/2,
                                Float.MAX_VALUE,
                                Float.MAX_VALUE);
    /**
     * Converts the input value to a ratio. If the input value ends
     * with a % character, it is considered a percentage. Otherwise,
     * it is considered a plain floating point value
     */
    public static float convertRatio(String v){
        float d = 1;
        if (v.endsWith("%")) {
            v = v.substring(0, v.length() - 1);
            d = 100;
        }
        float r = Float.parseFloat(v)/d;
        if (r < 0) {
            r = 0;
        } else if (r > 1) {
            r = 1;
        }
        return r;
    }
}
