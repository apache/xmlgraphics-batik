/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.w3c.dom.Element;

import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.util.CSSConstants;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * A collection of utility method for text.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public abstract class TextUtilities implements CSSConstants, ErrorConstants {

    /**
     * Returns the float array that represents a set of horizontal
     * values or percentage.
     *
     * @param element the element that defines the specified coordinates
     * @param attrName the name of the attribute (used by error handling)
     * @param valueStr the delimited string containing values of the coordinate
     * @param ctx the bridge context
     */
    public static
        float[] svgHorizontalCoordinateArrayToUserSpace(Element element,
                                                        String attrName,
                                                        String valueStr,
                                                        BridgeContext ctx) {

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);
        ArrayList values = new ArrayList();
        StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        int c = 0; // must count, can't rely in ArrayList.size()
        while (st.hasMoreTokens()) {
            values.add
                (new Float(UnitProcessor.svgHorizontalCoordinateToUserSpace
                           (st.nextToken(), attrName, uctx)));
            c++;
        }
        float[] floats = new float[c];
        for (int i=0; i<c; ++i) {
            floats[i] = ((Float) values.get(i)).floatValue();
        }
        return floats;
    }

    /**
     * Returns the float array that represents a set of values or percentage.
     *
     *
     * @param element the element that defines the specified coordinates
     * @param attrName the name of the attribute (used by error handling)
     * @param valueStr the delimited string containing values of the coordinate
     * @param ctx the bridge context
     */
    public static
        float[] svgVerticalCoordinateArrayToUserSpace(Element element,
                                                      String attrName,
                                                      String valueStr,
                                                      BridgeContext ctx) {

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);
        ArrayList values = new ArrayList();
        StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        int c = 0; // must count, can't rely in ArrayList.size()
        while (st.hasMoreTokens()) {
            values.add
                (new Float(UnitProcessor.svgVerticalCoordinateToUserSpace
                           (st.nextToken(), attrName, uctx)));
            c++;
        }
        float[] floats = new float[c];
        for (int i=0; i<c; ++i) {
            floats[i] = ((Float) values.get(i)).floatValue();
        }
        return floats;
    }


    public static float[] svgRotateArrayToFloats(Element element,
                                                 String attrName,
                                                 String valueStr,
                                                 BridgeContext ctx) {

        StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        float[] floats = new float[st.countTokens()];
        int c = 0;
        String s;
        while (st.hasMoreTokens()) {
            try {
                s = st.nextToken();
                floats[c] = (float)Math.toRadians(SVGUtilities.convertSVGNumber(s));
                c++;
            } catch (NumberFormatException ex) {
                throw new BridgeException
                    (element, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object [] {attrName, valueStr});
            }
        }
        return floats;
    }



    /**
     * Converts the font-size CSS value to a float value.
     * @param svgElement the SVG Element
     * @param ctx the bridge context
     * @param decl the css style declaration
     * @param uctx the UnitProcessor context
     */
    public static float convertFontSize(Element svgElement,
                                        BridgeContext ctx,
                                        CSSOMReadOnlyStyleDeclaration decl,
                                        UnitProcessor.Context uctx) {

        CSSPrimitiveValue v
            = (CSSPrimitiveValue)decl.getPropertyCSSValueInternal
            (CSS_FONT_SIZE_PROPERTY);

        short t = v.getPrimitiveType();
        switch (t) {
        case CSSPrimitiveValue.CSS_IDENT:
            float fs = uctx.getMediumFontSize();
            fs = parseFontSize(v.getStringValue(), fs);
            return UnitProcessor.cssToUserSpace(fs,
                                                CSSPrimitiveValue.CSS_PT,
                                                UnitProcessor.VERTICAL_LENGTH,
                                                uctx);
        default:
            return UnitProcessor.cssToUserSpace(v.getFloatValue(t),
                                                t,
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
}
