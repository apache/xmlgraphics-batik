/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg;

import java.awt.Color;
import java.awt.Paint;
import java.util.Vector;

import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.bridge.PaintServer;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.util.CSSConstants;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGColor;

/**
 * Bridge class for a regular polygon element.
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas Deweese</a>
 */
public class SolidColorBridge 
    extends AbstractSVGBridge
    implements PaintBridge, BatikExtConstants, CSSConstants {

    /**
     * Constructs a new bridge for the &lt;rect> element.
     */
    public SolidColorBridge() { /* nothing */ }

    /**
     * Returns the SVG namespace URI.
     */
    public String getNamespaceURI() {
        return BATIK_EXT_NAMESPACE_URI;
    }

    /**
     * Returns 'rect'.
     */
    public String getLocalName() {
        return BATIK_EXT_SOLID_COLOR_TAG;
    }

    /**
     * Creates a <tt>Paint</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param paintElement the element that defines a Paint
     * @param paintedElement the element referencing the paint
     * @param paintedNode the graphics node on which the Paint will be applied
     * @param opacity the opacity of the Paint to create
     */
    public Paint createPaint(BridgeContext ctx,
                             Element paintElement,
                             Element paintedElement,
                             GraphicsNode paintedNode,
                             float opacity) {
        CSSOMReadOnlyStyleDeclaration decl;
        decl = CSSUtilities.getComputedStyle(paintElement);
        CSSValue opacityVal = decl.getPropertyCSSValueInternal
            (BATIK_EXT_SOLID_OPACITY_PROPERTY);
        if (opacityVal != null) {
            float attr = PaintServer.convertOpacity(opacityVal);
            opacity *= attr;
        }

        CSSValue colorDef
            = decl.getPropertyCSSValueInternal(BATIK_EXT_SOLID_COLOR_PROPERTY);
        if (colorDef == null)
            return new Color(0f, 0f, 0f, opacity);

        Color ret = null;
        if (colorDef.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue v = (CSSPrimitiveValue)colorDef;
            ret = PaintServer.convertColor(v.getRGBColorValue(), opacity);
        } else {
            ret = PaintServer.convertRGBICCColor
                (paintElement, (SVGColor)colorDef, opacity, ctx);
        }
        return ret;
    }
}
