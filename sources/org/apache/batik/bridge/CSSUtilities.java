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
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Map;
import java.util.HashMap;

import org.apache.batik.css.AbstractViewCSS;
import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.css.value.ImmutableString;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.ext.awt.color.ICCColorSpaceExt;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.Filter;

import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.CSSConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.css.Rect;
import org.w3c.dom.svg.SVGICCColor;
import org.w3c.dom.svg.SVGColor;
import org.w3c.dom.svg.SVGNumberList;
import org.w3c.dom.svg.SVGPaint;

/**
 * A collection of utility method involving CSS property. The listed
 * methods bellow could be used as convenient methods to create
 * concrete objects regarding to CSS properties.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class CSSUtilities implements CSSConstants, ErrorConstants {

    /**
     * No instance of this class is required.
     */
    protected CSSUtilities() {}

    /////////////////////////////////////////////////////////////////////////
    // Global methods
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the View CSS associated to the specified element.
     * @param e the element
     */
    public static AbstractViewCSS getViewCSS(Element e) {
        return (AbstractViewCSS)
            ((SVGOMDocument)e.getOwnerDocument()).getDefaultView();
    }

    /**
     * Returns the computed style of the specified element.
     * @param e the element
     */
    public static CSSOMReadOnlyStyleDeclaration getComputedStyle(Element e) {
        return getViewCSS(e).getComputedStyleInternal(e, null);
    }

    /////////////////////////////////////////////////////////////////////////
    // 'enable-background'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the subregion of user space where access to the
     * background image is allowed to happen.
     *
     * @param e the container element
     */
    public static
        Rectangle2D convertEnableBackground(Element e,
                                            UnitProcessor.Context uctx) {

        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        CSSValue v
            = decl.getPropertyCSSValueInternal(CSS_ENABLE_BACKGROUND_PROPERTY);
        if (v.getCssValueType() != v.CSS_VALUE_LIST) {
            return null; // accumulate
        }
        CSSValueList l = (CSSValueList)v;
        int length = l.getLength();
        switch (length) {
        case 1:
            return CompositeGraphicsNode.VIEWPORT; // new
        case 5: // new <x>,<y>,<width>,<height>
            v = l.item(1);
            float x = UnitProcessor.cssHorizontalCoordinateToUserSpace
                (v, CSS_ENABLE_BACKGROUND_PROPERTY, uctx);
            v = l.item(2);
            float y = UnitProcessor.cssVerticalCoordinateToUserSpace
                (v, CSS_ENABLE_BACKGROUND_PROPERTY, uctx);
            v = l.item(3);
            float w = UnitProcessor.cssHorizontalLengthToUserSpace
                (v, CSS_ENABLE_BACKGROUND_PROPERTY, uctx);
            v = l.item(4);
            float h = UnitProcessor.cssVerticalLengthToUserSpace
                (v, CSS_ENABLE_BACKGROUND_PROPERTY, uctx);
            return new Rectangle2D.Float(x, y, w, h);
        default:
            // If more than zero but less than four of the values
            // <x>,<y>,<width> and <height> are specified or if zero
            // values are specified for <width> or <height>,
            // BackgroundImage and BackgroundAlpha are processed as if
            // background image processing were not enabled.
            return null;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // 'color-interpolation-filters'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the color space for the specified filter element. Checks the
     * 'color-interpolation-filters' property.
     *
     * @param filterElement the element
     * @return true if the color space is linear, false otherwise (sRGB).
     */
    public static boolean convertColorInterpolationFilter(Element filterElement) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(filterElement);
        CSSPrimitiveValue v
            = (CSSPrimitiveValue) decl.getPropertyCSSValueInternal
            (CSS_COLOR_INTERPOLATION_FILTERS_PROPERTY);

        return CSS_LINEARRGB_VALUE.equals(v.getStringValue());
    }

    /////////////////////////////////////////////////////////////////////////
    // 'color-interpolation'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the color space for the specified element. Checks the
     * 'color-interpolation' property
     *
     * @param e the element
     */
    public static MultipleGradientPaint.ColorSpaceEnum
        convertColorInterpolation(Element e) {

        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        CSSPrimitiveValue v
            = (CSSPrimitiveValue) decl.getPropertyCSSValueInternal
            (CSS_COLOR_INTERPOLATION_PROPERTY);

        return CSS_LINEARRGB_VALUE.equals(v.getStringValue())
            ? MultipleGradientPaint.LINEAR_RGB
            : MultipleGradientPaint.SRGB;
    }

    /////////////////////////////////////////////////////////////////////////
    // 'color-rendering', 'text-rendering', 'image-rendering', 'shape-rendering'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the rendering hints for the specified shape element or null
     * none has been specified. Checks the 'shape-rendering' property.
     *
     * @param e the element
     */
    public static Map convertShapeRendering(Element e) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        CSSPrimitiveValue v = (CSSPrimitiveValue)
            decl.getPropertyCSSValueInternal(CSS_SHAPE_RENDERING_PROPERTY);
        String s = v.getStringValue();
        if (s.charAt(0) == 'a') { // auto
            return null;
        }
        Map hints = new HashMap();
        switch(s.charAt(0)) {
        case 'o': // optimizeSpeed
            hints.put(RenderingHints.KEY_RENDERING,
                      RenderingHints.VALUE_RENDER_SPEED);
            hints.put(RenderingHints.KEY_ANTIALIASING,
                      RenderingHints.VALUE_ANTIALIAS_OFF);
            break;
        case 'c': // crispEdges
            hints.put(RenderingHints.KEY_RENDERING,
                      RenderingHints.VALUE_RENDER_DEFAULT);
            hints.put(RenderingHints.KEY_ANTIALIASING,
                      RenderingHints.VALUE_ANTIALIAS_OFF);
            break;
        case 'g': // geometricPrecision
            hints.put(RenderingHints.KEY_RENDERING,
                      RenderingHints.VALUE_RENDER_QUALITY);
            hints.put(RenderingHints.KEY_ANTIALIASING,
                      RenderingHints.VALUE_ANTIALIAS_ON);
            break;
        }
        return hints;
    }

    /**
     * Returns the rendering hints for the specified text element or null
     * none has been specified. Checks the 'text-rendering' property.
     *
     * @param e the element
     */
    public static Map convertTextRendering(Element e) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        CSSPrimitiveValue v = (CSSPrimitiveValue)
            decl.getPropertyCSSValueInternal(CSS_TEXT_RENDERING_PROPERTY);
        String s = v.getStringValue();
        if (s.charAt(0) == 'a') { // auto
            return null;
        }
        Map hints = new HashMap();
        switch(s.charAt(8)) {
        case 's': // optimizeSpeed
            hints.put(RenderingHints.KEY_RENDERING,
                      RenderingHints.VALUE_RENDER_SPEED);
            hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                      RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            hints.put(RenderingHints.KEY_ANTIALIASING,
                      RenderingHints.VALUE_ANTIALIAS_OFF);
            hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
                      RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            break;
        case 'l': // optimizeLegibility
            hints.put(RenderingHints.KEY_RENDERING,
                      RenderingHints.VALUE_RENDER_QUALITY);
            hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            hints.put(RenderingHints.KEY_ANTIALIASING,
                      RenderingHints.VALUE_ANTIALIAS_ON);
            hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
                      RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            break;
        case 'c': // geometricPrecision
            hints.put(RenderingHints.KEY_RENDERING,
                      RenderingHints.VALUE_RENDER_QUALITY);
            hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                      RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
            hints.put(RenderingHints.KEY_ANTIALIASING,
                      RenderingHints.VALUE_ANTIALIAS_DEFAULT);
            hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
                      RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            break;
        }
        return hints;
    }

    /**
     * Returns the rendering hints for the specified image element or null
     * none has been specified. Checks the 'image-rendering' property.
     *
     * @param e the element
     */
    public static Map convertImageRendering(Element e) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        CSSPrimitiveValue v = (CSSPrimitiveValue)
            decl.getPropertyCSSValueInternal(CSS_IMAGE_RENDERING_PROPERTY);
        String s = v.getStringValue();
        if (s.charAt(0) == 'a') { // auto
            return null;
        }
        Map hints = new HashMap();
        switch(s.charAt(8)) {
        case 's': // optimizeSpeed
            hints.put(RenderingHints.KEY_RENDERING,
                      RenderingHints.VALUE_RENDER_SPEED);
            hints.put(RenderingHints.KEY_INTERPOLATION,
                      RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            break;
        case 'q': // optimizeQuality
            hints.put(RenderingHints.KEY_RENDERING,
                      RenderingHints.VALUE_RENDER_QUALITY);
            hints.put(RenderingHints.KEY_INTERPOLATION,
                      RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            break;
        }
        return hints;
    }

    /**
     * Returns the rendering hints for the specified element or null
     * none has been specified. Checks the 'color-rendering' property.
     *
     * @param e the element
     */
    public static Map convertColorRendering(Element e) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        CSSPrimitiveValue v = (CSSPrimitiveValue)
            decl.getPropertyCSSValueInternal(CSS_COLOR_RENDERING_PROPERTY);
        String s = v.getStringValue();
        if (s.charAt(0) == 'a') { // auto
            return null;
        }
        // System.out.println("Str: " + s + "[8] = '" + s.charAt(8) + "'");
        Map hints = new HashMap();
        switch(s.charAt(8)) {
        case 's': // optimizeSpeed
            hints.put(RenderingHints.KEY_COLOR_RENDERING,
                      RenderingHints.VALUE_COLOR_RENDER_SPEED);
            hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                      RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            break;
        case 'q': // optimizeQuality
            hints.put(RenderingHints.KEY_COLOR_RENDERING,
                      RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                      RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            break;
        }
        return hints;
    }

    /////////////////////////////////////////////////////////////////////////
    // 'display'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns true if the specified element has to be displayed, false
     * otherwise. Checks the 'display' property.
     *
     * @param e the element
     */
    public static boolean convertDisplay(Element e) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        CSSValue v = decl.getPropertyCSSValueInternal(CSS_DISPLAY_PROPERTY);
        return (((CSSPrimitiveValue)v).getStringValue().charAt(0) != 'n');
    }

    /////////////////////////////////////////////////////////////////////////
    // 'visibility'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns true if the specified element is visible, false
     * otherwise. Checks the 'visibility' property.
     *
     * @param e the element
     */
    public static boolean convertVisibility(Element e) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        CSSValue v = decl.getPropertyCSSValueInternal(CSS_VISIBILITY_PROPERTY);
        if (v.getCssValueType() == CSSValue.CSS_INHERIT) {
            // workaround for the CSS2 spec which indicates that the
            // initial value is 'inherit'. So if we get 'inherit' it
            // means that we are on the outermost svg element and we
            // always return true.
            return true;
        } else {
            return (((CSSPrimitiveValue)v).getStringValue().charAt(0) == 'v');
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // 'opacity'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns a composite object that represents the 'opacity' of the
     * specified element.
     *
     * @param e the element
     */
    public static Composite convertOpacity(Element e) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        CSSValue v =
            getComputedStyle(e).getPropertyCSSValueInternal
            (CSS_OPACITY_PROPERTY);
        float opacity = PaintServer.convertOpacity(v);
        if (opacity <= 0f) {
            return null;
        } else if (opacity >= 1f) {
            return AlphaComposite.SrcOver;
        } else {
            return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // 'overflow' and 'clip'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns true if the 'overflow' property indicates that an
     * additional clip is required, false otherwise. An additional
     * clip is needed if the 'overflow' property is 'scroll' or
     * 'hidden'.
     *
     * @param e the element with the 'overflow' property
     */
    public static boolean convertOverflow(Element e) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        CSSPrimitiveValue overflow =
            (CSSPrimitiveValue)decl.getPropertyCSSValueInternal
            (CSS_OVERFLOW_PROPERTY);
        String s = overflow.getStringValue();
        // clip if 'hidden' or 'scroll'
        return (s.charAt(0) == 'h') || (s.charAt(0) == 's');
    }

    /**
     * Returns an array of floating offsets representing the 'clip'
     * property or null if 'auto'. The offsets are specified in the
     * order top, right, bottom, left.
     *
     * @param e the element with the 'clip' property
     */
    public static float[] convertClip(Element e) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        CSSPrimitiveValue clip =
            (CSSPrimitiveValue)decl.getPropertyCSSValueInternal
            (CSS_CLIP_PROPERTY);
        switch (clip.getPrimitiveType()) {
        case CSSPrimitiveValue.CSS_RECT:
            float [] off = new float[4];
            Rect r = clip.getRectValue();
            off[0] = r.getTop().getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
            off[1] = r.getRight().getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
            off[2] = r.getBottom().getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
            off[3] = r.getLeft().getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
            return off;
        case CSSPrimitiveValue.CSS_IDENT:
            return null; // 'auto' means no offsets
        default:
            throw new Error(); // can't be reached
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // 'filter'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns a <tt>Filter</tt> referenced by the specified element
     * and which applies on the specified graphics node.
     * Handle the 'filter' property.
     *
     * @param filteredElement the element that references the filter
     * @param filteredNode the graphics node associated to the element to filter
     * @param ctx the bridge context
     */
    public static Filter convertFilter(Element filteredElement,
                                       GraphicsNode filteredNode,
                                       BridgeContext ctx) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(filteredElement);

        CSSPrimitiveValue filterValue =
            (CSSPrimitiveValue)decl.getPropertyCSSValueInternal
            (CSS_FILTER_PROPERTY);

        switch(filterValue.getPrimitiveType()){
        case CSSPrimitiveValue.CSS_IDENT:
            return null; // 'filter:none'
        case CSSPrimitiveValue.CSS_URI:
            String uri = filterValue.getStringValue();
            Element filter = ctx.getReferencedElement(filteredElement, uri);
            Bridge bridge = ctx.getBridge(filter);
            if (bridge == null || !(bridge instanceof FilterBridge)) {
                throw new BridgeException(filteredElement,
                                          ERR_CSS_URI_BAD_TARGET,
                                          new Object[] {uri});
            }
            return ((FilterBridge)bridge).createFilter(ctx,
                                                       filter,
                                                       filteredElement,
                                                       filteredNode);
        default:
            throw new Error(); // can't be reached
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // 'clip-path' and 'clip-rule'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns a <tt>Clip</tt> referenced by the specified element and
     * which applies on the specified graphics node.
     * Handle the 'clip-path' property.
     *
     * @param clipedElement the element that references the clip
     * @param clipedNode the graphics node associated to the element to clip
     * @param ctx the bridge context
     */
    public static ClipRable convertClipPath(Element clipedElement,
                                            GraphicsNode clipedNode,
                                            BridgeContext ctx) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(clipedElement);

        CSSPrimitiveValue clipValue =
            (CSSPrimitiveValue)decl.getPropertyCSSValueInternal
            (CSS_CLIP_PATH_PROPERTY);

        switch(clipValue.getPrimitiveType()){
        case CSSPrimitiveValue.CSS_IDENT:
            return null; // 'clip-path:none'
        case CSSPrimitiveValue.CSS_URI:
            String uri = clipValue.getStringValue();
            Element clipPath = ctx.getReferencedElement(clipedElement, uri);
            Bridge bridge = ctx.getBridge(clipPath);
            if (bridge == null || !(bridge instanceof ClipBridge)) {
                throw new BridgeException(clipedElement,
                                          ERR_CSS_URI_BAD_TARGET,
                                          new Object[] {uri});
            }
            return ((ClipBridge)bridge).createClip(ctx,
                                                   clipPath,
                                                   clipedElement,
                                                   clipedNode);
        default:
            throw new Error(); // can't be reached
        }
    }

    /**
     * Returns the 'clip-rule' for the specified element.
     *
     * @param e the element interested in its a 'clip-rule'
     * @return GeneralPath.WIND_NON_ZERO | GeneralPath.WIND_EVEN_ODD
     */
    public static int convertClipRule(Element e) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        return rule(decl.getPropertyCSSValueInternal(CSS_CLIP_RULE_PROPERTY));
    }

    /////////////////////////////////////////////////////////////////////////
    // 'mask'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns a <tt>Mask</tt> referenced by the specified element and
     * which applies on the specified graphics node.
     * Handle the 'mask' property.
     *
     * @param maskedElement the element that references the mask
     * @param maskedNode the graphics node associated to the element to mask
     * @param ctx the bridge context
     */
    public static Mask convertMask(Element maskedElement,
                                   GraphicsNode maskedNode,
                                   BridgeContext ctx) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(maskedElement);

        CSSPrimitiveValue maskValue =
            (CSSPrimitiveValue)decl.getPropertyCSSValueInternal
            (CSS_MASK_PROPERTY);

        switch(maskValue.getPrimitiveType()){
        case CSSPrimitiveValue.CSS_IDENT:
            return null; // 'mask:none'
        case CSSPrimitiveValue.CSS_URI:
            String uri = maskValue.getStringValue();
            Element mask = ctx.getReferencedElement(maskedElement, uri);
            Bridge bridge = ctx.getBridge(mask);
            if (bridge == null || !(bridge instanceof MaskBridge)) {
                throw new BridgeException(maskedElement,
                                          ERR_CSS_URI_BAD_TARGET,
                                          new Object[] {uri});
            }
            return ((MaskBridge)bridge).createMask(ctx,
                                                   mask,
                                                   maskedElement,
                                                   maskedNode);
        default:
            throw new Error(); // can't be reached
        }
    }

    /**
     * Returns the 'fill-rule' for the specified element.
     *
     * @param e the element interested in its a 'fill-rule'
     * @return GeneralPath.WIND_NON_ZERO | GeneralPath.WIND_EVEN_ODD
     */
    public static int convertFillRule(Element e) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        return rule(decl.getPropertyCSSValueInternal
                    (CSS_FILL_RULE_PROPERTY));
    }

    /////////////////////////////////////////////////////////////////////////
    // 'lighting-color'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Converts the color defined on the specified lighting filter element
     * to a <tt>Color</tt>.
     *
     * @param e the lighting filter element
     * @param ctx the bridge context
     */
    public static Color convertLightingColor(Element e, BridgeContext ctx) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);

        CSSValue colorDef = decl.getPropertyCSSValueInternal
            (CSS_LIGHTING_COLOR_PROPERTY);
        if (colorDef.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue v = (CSSPrimitiveValue)colorDef;
            return PaintServer.convertColor(v.getRGBColorValue(), 1);
        } else {
            return PaintServer.convertRGBICCColor
                (e, (SVGColor)colorDef, 1, ctx);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // 'flood-color' and 'flood-opacity'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Converts the color defined on the specified &lt;feFlood>
     * element to a <tt>Color</tt>.
     *
     * @param e the feFlood element
     * @param ctx the bridge context
     */
    public static Color convertFloodColor(Element e, BridgeContext ctx) {
        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(e);
        float opacity = PaintServer.convertOpacity
            (decl.getPropertyCSSValueInternal(CSS_FLOOD_OPACITY_PROPERTY));

        CSSValue colorDef
            = decl.getPropertyCSSValueInternal(CSS_FLOOD_COLOR_PROPERTY);
        if (colorDef.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue v = (CSSPrimitiveValue)colorDef;
            return PaintServer.convertColor(v.getRGBColorValue(), opacity);
        } else {
            return PaintServer.convertRGBICCColor
                (e, (SVGColor)colorDef, opacity, ctx);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // 'stop-color'
    /////////////////////////////////////////////////////////////////////////

    /**
     * Converts the color defined on the specified &lt;stop> element
     * to a <tt>Color</tt>.
     *
     * @param stopElement the stop element
     * @param opacity the paint opacity
     * @param ctx the bridge context to use
     */
    public static Color convertStopColor(Element stopElement,
                                         float opacity,
                                         BridgeContext ctx) {

        CSSOMReadOnlyStyleDeclaration decl = getComputedStyle(stopElement);

        CSSValue colorDef
            = decl.getPropertyCSSValueInternal(CSS_STOP_COLOR_PROPERTY);

        float stopOpacity = PaintServer.convertOpacity
            (decl.getPropertyCSSValueInternal(CSS_STOP_OPACITY_PROPERTY));
        opacity *= stopOpacity;

        if (colorDef.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue v = (CSSPrimitiveValue)colorDef;
            return PaintServer.convertColor(v.getRGBColorValue(), opacity);
        } else {
            return PaintServer.convertRGBICCColor
                (stopElement, (SVGColor)colorDef, opacity, ctx);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // CSS support for <use>
    /////////////////////////////////////////////////////////////////////////

    /**
     * Partially computes the style in the 'def' tree and set it in the 'use'
     * tree.
     * <p>Note: This method must be called only when 'use' has been
     * added to the DOM tree.
     *
     * @param refElement the referenced element
     * @param localRefElement the referenced element in the current document
     */
    public static void computeStyleAndURIs(Element refElement,
                                           Element localRefElement) {

        SVGOMDocument document
            = (SVGOMDocument)localRefElement.getOwnerDocument();
        ViewCSS view = (ViewCSS)document.getDefaultView();

        SVGOMDocument refDocument
            = (SVGOMDocument)refElement.getOwnerDocument();
        ViewCSS refView = (ViewCSS)refDocument.getDefaultView();

        URL url = refDocument.getURLObject();

        computeStyleAndURIs(refElement,
                            refView,
                            localRefElement,
                            view,
                            url);
    }

    /**
     * Partially computes the style in the use tree and set it in
     * the target tree.
     * Note: This method must be called only when 'def' has been added
     * to the tree.
     */
    static void computeStyleAndURIs(Element use, ViewCSS uv,
                                            Element def, ViewCSS dv,
                                            URL url) {

        String href = XLinkSupport.getXLinkHref(def);

        if (!href.equals("")) {
            try {
                XLinkSupport.setXLinkHref(def, new URL(url, href).toString());
            } catch (MalformedURLException e) { }
        }

        CSSOMReadOnlyStyleDeclaration usd;
        AbstractViewCSS uview = (AbstractViewCSS)uv;

        usd = (CSSOMReadOnlyStyleDeclaration)uview.computeStyle(use, null);
        try {
            updateURIs(usd, url);
        } catch (MalformedURLException ex) { }
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
    public static void updateURIs(CSSOMReadOnlyStyleDeclaration sd, URL url)
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
                    CSSOMReadOnlyValue v = new CSSOMReadOnlyValue
                        (new ImmutableString
                         (CSSPrimitiveValue.CSS_URI,
                          new URL(url, pv.getStringValue()).toString()));
                    sd.setPropertyCSSValue(name, v,
                                           sd.getLocalPropertyPriority(name),
                                           sd.getLocalPropertyOrigin(name));
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Additional utility methods used internally
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the winding rule represented by the specified CSSValue.
     *
     * @param v the value that represents the rule
     * @return GeneralPath.WIND_NON_ZERO | GeneralPath.WIND_EVEN_ODD
     */
    protected static int rule(CSSValue v) {
        return (((CSSPrimitiveValue)v).getStringValue().charAt(0) == 'n')
            ? GeneralPath.WIND_NON_ZERO
            : GeneralPath.WIND_EVEN_ODD;
    }
}
