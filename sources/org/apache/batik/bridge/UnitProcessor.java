/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.io.StringReader;

import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.util.CSSConstants;

import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.Value;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGLength;

/**
 * This class provides methods to convert SVG length and coordinate to
 * float in user units.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class UnitProcessor {

    /**
     * This constant represents horizontal lengths.
     */
    public final static short HORIZONTAL_LENGTH = 2;

    /**
     * This constant represents vertical lengths.
     */
    public final static short VERTICAL_LENGTH = 1;

    /**
     * This constant represents other lengths.
     */
    public final static short OTHER_LENGTH = 0;

    /**
     * No instance of this class is required.
     */
    protected UnitProcessor() { }

    /**
     * Creates a context for the specified element.
     *
     * @param ctx the bridge context that contains the user agent and
     * viewport definition
     * @param e the element interested in its context
     */
    public static Context createContext(BridgeContext ctx, Element e) {
        return new DefaultContext(ctx, e);
    }

    /////////////////////////////////////////////////////////////////////////
    // SVG methods - objectBoundingBox
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the specified horizontal coordinate in object bounding box
     * coordinate system.
     *
     * @param s the horizontal coordinate
     * @param attr the attribute name that represents the coordinate
     * @param ctx the context used to resolve relative value
     */
    public static
        float svgHorizontalCoordinateToObjectBoundingBox(String s,
                                                         String attr,
                                                         Context ctx) {
        return svgToObjectBoundingBox(s, attr, HORIZONTAL_LENGTH, ctx);
    }

    /**
     * Returns the specified vertical coordinate in object bounding box
     * coordinate system.
     *
     * @param s the vertical coordinate
     * @param attr the attribute name that represents the coordinate
     * @param ctx the context used to resolve relative value
     */
    public static
        float svgVerticalCoordinateToObjectBoundingBox(String s,
                                                       String attr,
                                                       Context ctx) {
        return svgToObjectBoundingBox(s, attr, VERTICAL_LENGTH, ctx);
    }

    /**
     * Returns the specified 'other' coordinate in object bounding box
     * coordinate system.
     *
     * @param s the 'other' coordinate
     * @param attr the attribute name that represents the coordinate
     * @param ctx the context used to resolve relative value
     */
    public static
        float svgOtherCoordinateToObjectBoundingBox(String s,
                                                    String attr,
                                                    Context ctx) {
        return svgToObjectBoundingBox(s, attr, OTHER_LENGTH, ctx);
    }

    /**
     * Returns the specified horizontal length in object bounding box
     * coordinate system. A length must be greater than 0.
     *
     * @param s the 'other' length
     * @param attr the attribute name that represents the length
     * @param ctx the context used to resolve relative value
     */
    public static
        float svgHorizontalLengthToObjectBoundingBox(String s,
                                                     String attr,
                                                     Context ctx) {
        return svgLengthToObjectBoundingBox(s, attr, HORIZONTAL_LENGTH, ctx);
    }

    /**
     * Returns the specified vertical length in object bounding box
     * coordinate system. A length must be greater than 0.
     *
     * @param s the vertical length
     * @param attr the attribute name that represents the length
     * @param ctx the context used to resolve relative value
     */
    public static
        float svgVerticalLengthToObjectBoundingBox(String s,
                                                   String attr,
                                                   Context ctx) {
        return svgLengthToObjectBoundingBox(s, attr, VERTICAL_LENGTH, ctx);
    }

    /**
     * Returns the specified 'other' length in object bounding box
     * coordinate system. A length must be greater than 0.
     *
     * @param s the 'other' length
     * @param attr the attribute name that represents the length
     * @param ctx the context used to resolve relative value
     */
    public static
        float svgOtherLengthToObjectBoundingBox(String s,
                                                String attr,
                                                Context ctx) {
        return svgLengthToObjectBoundingBox(s, attr, OTHER_LENGTH, ctx);
    }

    /**
     * Returns the specified length with the specified direction in
     * user units. A length must be greater than 0.
     *
     * @param s the length
     * @param attr the attribute name that represents the length
     * @param d the direction of the length
     * @param ctx the context used to resolve relative value
     */
    public static float svgLengthToObjectBoundingBox(String s,
                                                     String attr,
                                                     short d,
                                                     Context ctx) {
        float v = svgToObjectBoundingBox(s, attr, d, ctx);
        if (v < 0) {
            throw new BridgeException(ctx.getElement(),
                                      ErrorConstants.ERR_LENGTH_NEGATIVE,
                                      new Object[] {attr, s});
        }
        return v;
    }

    /**
     * Returns the specified value with the specified direction in
     * objectBoundingBox units.
     *
     * @param s the value
     * @param attr the attribute name that represents the value
     * @param d the direction of the value
     * @param ctx the context used to resolve relative value
     */
    public static float svgToObjectBoundingBox(String s,
                                               String attr,
                                               short d,
                                               Context ctx) {
        try {
            LengthParser lengthParser = new LengthParser();
            UnitResolver ur = new UnitResolver();
            lengthParser.setLengthHandler(ur);
            lengthParser.parse(s);
            return svgToObjectBoundingBox(ur.value, ur.unit, d, ctx);
        } catch (ParseException ex) {
            throw new BridgeException(ctx.getElement(),
                                   ErrorConstants.ERR_ATTRIBUTE_VALUE_MALFORMED,
                                      new Object[] {attr, s, ex});
        }
    }

    /**
     * Returns the specified value with the specified direction in
     * objectBoundingBox units.
     *
     * @param s the value
     * @param type the type of the value
     * @param d the direction of the value
     * @param ctx the context used to resolve relative value
     */
    public static float svgToObjectBoundingBox(float value,
                                               short type,
                                               short d,
                                               Context ctx) {
        switch (type) {
        case SVGLength.SVG_LENGTHTYPE_NUMBER:
            // as is
            return value;
        case SVGLength.SVG_LENGTHTYPE_PERCENTAGE:
            // If a percentage value is used, it is converted to a
            // 'bounding box' space coordinate by division by 100
            return value / 100f;
        case SVGLength.SVG_LENGTHTYPE_PX:
        case SVGLength.SVG_LENGTHTYPE_MM:
        case SVGLength.SVG_LENGTHTYPE_CM:
        case SVGLength.SVG_LENGTHTYPE_IN:
        case SVGLength.SVG_LENGTHTYPE_PT:
        case SVGLength.SVG_LENGTHTYPE_PC:
        case SVGLength.SVG_LENGTHTYPE_EMS:
        case SVGLength.SVG_LENGTHTYPE_EXS:
            // <!> FIXME: resolve units in userSpace but consider them
            // in the objectBoundingBox coordinate system
            return svgToUserSpace(value, type, d, ctx);
        default:
            throw new Error(); // can't be reached
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // SVG methods - userSpace
    /////////////////////////////////////////////////////////////////////////


    /**
     * Returns the specified horizontal length in user units. A length
     * must be greater than 0.
     *
     * @param s the horizontal length
     * @param attr the attribute name that represents the length
     * @param ctx the context used to resolve relative value
     */
    public static float svgHorizontalLengthToUserSpace(String s,
                                                       String attr,
                                                       Context ctx) {
        return svgLengthToUserSpace(s, attr, HORIZONTAL_LENGTH, ctx);
    }

    /**
     * Returns the specified vertical length in user units. A length
     * must be greater than 0.
     *
     * @param s the vertical length
     * @param attr the attribute name that represents the length
     * @param ctx the context used to resolve relative value
     */
    public static float svgVerticalLengthToUserSpace(String s,
                                                     String attr,
                                                     Context ctx) {
        return svgLengthToUserSpace(s, attr, VERTICAL_LENGTH, ctx);
    }

    /**
     * Returns the specified 'other' length in user units. A length
     * must be greater than 0.
     *
     * @param s the 'other' length
     * @param attr the attribute name that represents the length
     * @param ctx the context used to resolve relative value
     */
    public static float svgOtherLengthToUserSpace(String s,
                                                  String attr,
                                                  Context ctx) {
        return svgLengthToUserSpace(s, attr, OTHER_LENGTH, ctx);
    }

    /**
     * Returns the specified horizontal coordinate in user units.
     *
     * @param s the horizontal coordinate
     * @param attr the attribute name that represents the length
     * @param ctx the context used to resolve relative value
     */
    public static float svgHorizontalCoordinateToUserSpace(String s,
                                                           String attr,
                                                           Context ctx) {
        return svgToUserSpace(s, attr, HORIZONTAL_LENGTH, ctx);
    }

    /**
     * Returns the specified vertical coordinate in user units.
     *
     * @param s the vertical coordinate
     * @param attr the attribute name that represents the length
     * @param ctx the context used to resolve relative value
     */
    public static float svgVerticalCoordinateToUserSpace(String s,
                                                         String attr,
                                                         Context ctx) {
        return svgToUserSpace(s, attr, VERTICAL_LENGTH, ctx);
    }

    /**
     * Returns the specified 'other' coordinate in user units.
     *
     * @param s the 'other' coordinate
     * @param attr the attribute name that represents the length
     * @param ctx the context used to resolve relative value
     */
    public static float svgOtherCoordinateToUserSpace(String s,
                                                      String attr,
                                                      Context ctx) {
        return svgToUserSpace(s, attr, OTHER_LENGTH, ctx);
    }

    /**
     * Returns the specified length with the specified direction in
     * user units. A length must be greater than 0.
     *
     * @param s the 'other' coordinate
     * @param attr the attribute name that represents the length
     * @param d the direction of the length
     * @param ctx the context used to resolve relative value
     */
    public static float svgLengthToUserSpace(String s,
                                             String attr,
                                             short d,
                                             Context ctx) {
        float v = svgToUserSpace(s, attr, d, ctx);
        if (v < 0) {
            throw new BridgeException(ctx.getElement(),
                                      ErrorConstants.ERR_LENGTH_NEGATIVE,
                                      new Object[] {attr, s});
        } else {
            return v;
        }
    }

    /**
     * Returns the specified coordinate with the specified direction
     * in user units.
     *
     * @param s the 'other' coordinate
     * @param attr the attribute name that represents the length
     * @param d the direction of the coordinate
     * @param ctx the context used to resolve relative value
     */
    public static float svgToUserSpace(String s,
                                       String attr,
                                       short d,
                                       Context ctx) {
        try {
            LengthParser lengthParser = new LengthParser();
            UnitResolver ur = new UnitResolver();
            lengthParser.setLengthHandler(ur);
            lengthParser.parse(s);
            return svgToUserSpace(ur.value, ur.unit, d, ctx);
        } catch (ParseException ex) {
            throw new BridgeException(ctx.getElement(),
                                   ErrorConstants.ERR_ATTRIBUTE_VALUE_MALFORMED,
                                      new Object[] {attr, s, ex});
        }
    }

    /**
     * Converts the specified value of the specified type and
     * direction to user units.
     *
     * @param v the value to convert
     * @param type the type of the value
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH, or OTHER_LENGTH
     * @param ctx the context used to resolve relative value
     */
    public static float svgToUserSpace(float v,
                                       short type,
                                       short d,
                                       Context ctx) {
        switch (type) {
        case SVGLength.SVG_LENGTHTYPE_NUMBER:
        case SVGLength.SVG_LENGTHTYPE_PX:
            return v;
        case SVGLength.SVG_LENGTHTYPE_MM:
            return (v / ctx.getPixelToMM());
        case SVGLength.SVG_LENGTHTYPE_CM:
            return (v * 10f / ctx.getPixelToMM());
        case SVGLength.SVG_LENGTHTYPE_IN:
            return (v * 25.4f / ctx.getPixelToMM());
        case SVGLength.SVG_LENGTHTYPE_PT:
            return (v * 25.4f / (72f * ctx.getPixelToMM()));
        case SVGLength.SVG_LENGTHTYPE_PC:
            return (v * 25.4f / (6f * ctx.getPixelToMM()));
        case SVGLength.SVG_LENGTHTYPE_EMS:
            return emsToPixels(v, d, ctx);
        case SVGLength.SVG_LENGTHTYPE_EXS:
            return exsToPixels(v, d, ctx);
        case SVGLength.SVG_LENGTHTYPE_PERCENTAGE:
            return percentagesToPixels(v, d, ctx);
        default:
            throw new Error(); // can't be reached
        }
    }

    /**
     * Converts the specified value of the specified type and
     * direction to SVG units.
     *
     * @param v the value to convert
     * @param type the type of the value
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH, or OTHER_LENGTH
     * @param ctx the context used to resolve relative value
     */
    public static float userSpaceToSVG(float v,
                                       short type,
                                       short d,
                                       Context ctx) {
        switch (type) {
        case SVGLength.SVG_LENGTHTYPE_NUMBER:
        case SVGLength.SVG_LENGTHTYPE_PX:
            return v;
        case SVGLength.SVG_LENGTHTYPE_MM:
            return (v * ctx.getPixelToMM());
        case SVGLength.SVG_LENGTHTYPE_CM:
            return (v * ctx.getPixelToMM() / 10f);
        case SVGLength.SVG_LENGTHTYPE_IN:
            return (v * ctx.getPixelToMM() / 25.4f);
        case SVGLength.SVG_LENGTHTYPE_PT:
            return (v * (72f * ctx.getPixelToMM()) / 25.4f);
        case SVGLength.SVG_LENGTHTYPE_PC:
            return (v * (6f * ctx.getPixelToMM()) / 25.4f);
        case SVGLength.SVG_LENGTHTYPE_EMS:
            return pixelsToEms(v, d, ctx);
        case SVGLength.SVG_LENGTHTYPE_EXS:
            return pixelsToExs(v, d, ctx);
        case SVGLength.SVG_LENGTHTYPE_PERCENTAGE:
            return pixelsToPercentages(v, d, ctx);
        default:
            throw new Error(); // can't be reached
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Utilities methods for relative length
    /////////////////////////////////////////////////////////////////////////

    /**
     * Converts percentages to user units.
     *
     * @param v the percentage to convert
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH, or OTHER_LENGTH
     * @param ctx the context
     */
    protected static float percentagesToPixels(float v, short d, Context ctx) {
        if (d == HORIZONTAL_LENGTH) {
            float w = ctx.getViewportWidth();
            return w * v / 100f;
        } else if (d == VERTICAL_LENGTH) {
            float h = ctx.getViewportHeight();
            return h * v / 100f;
        } else {
            double w = ctx.getViewportWidth();
            double h = ctx.getViewportHeight();
            double vpp = Math.sqrt(w * w + h * h) / Math.sqrt(2);
            return (float)(vpp * v / 100d);
        }
    }

    /**
     * Converts user units to percentages relative to the viewport.
     *
     * @param v the value to convert
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH, or OTHER_LENGTH
     * @param ctx the context
     */
    protected static float pixelsToPercentages(float v, short d, Context ctx) {
        if (d == HORIZONTAL_LENGTH) {
            float w = ctx.getViewportWidth();
            return v * 100f / w;
        } else if (d == VERTICAL_LENGTH) {
            float h = ctx.getViewportHeight();
            return v * 100f / h;
        } else {
            double w = ctx.getViewportWidth();
            double h = ctx.getViewportHeight();
            double vpp = Math.sqrt(w * w + h * h) / Math.sqrt(2);
            return (float)(v * 100d / vpp);
        }
    }

    /**
     * Converts user units to ems units.
     *
     * @param v the value to convert
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH, or OTHER_LENGTH
     * @param ctx the context
     */
    protected static float pixelsToEms(float v, short d, Context ctx) {
        return v / ctx.getFontSize();
    }

    /**
     * Converts ems units to user units.
     *
     * @param v the value to convert
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH, or OTHER_LENGTH
     * @param ctx the context
     */
    protected static float emsToPixels(float v, short d, Context ctx) {
        return v * ctx.getFontSize();
    }

    /**
     * Converts user units to exs units.
     *
     * @param v the value to convert
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH, or OTHER_LENGTH
     * @param ctx the context
     */
    protected static float pixelsToExs(float v, short d, Context ctx) {
        float xh = ctx.getXHeight();
        return v / xh / ctx.getFontSize();
    }

    /**
     * Converts exs units to user units.
     *
     * @param v the value to convert
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH, or OTHER_LENGTH
     * @param ctx the context
     */
    protected static float exsToPixels(float v, short d, Context ctx) {
        float xh = ctx.getXHeight();
        return v * xh * ctx.getFontSize();
    }


    /**
     * A LengthHandler that convert units.
     */
    public static class UnitResolver implements LengthHandler {

        /** The length value. */
        public float value;
        /** The length type. */
        public short unit = SVGLength.SVG_LENGTHTYPE_NUMBER;

        /**
         * Implements {@link LengthHandler#startLength()}.
         */
        public void startLength() throws ParseException {
        }

        /**
         * Implements {@link LengthHandler#lengthValue(float)}.
         */
        public void lengthValue(float v) throws ParseException {
            this.value = v;
        }

        /**
         * Implements {@link LengthHandler#em()}.
         */
        public void em() throws ParseException {
            this.unit = SVGLength.SVG_LENGTHTYPE_EMS;
        }

        /**
         * Implements {@link LengthHandler#ex()}.
         */
        public void ex() throws ParseException {
            this.unit = SVGLength.SVG_LENGTHTYPE_EXS;
        }

        /**
         * Implements {@link LengthHandler#in()}.
         */
        public void in() throws ParseException {
            this.unit = SVGLength.SVG_LENGTHTYPE_IN;
        }

        /**
         * Implements {@link LengthHandler#cm()}.
         */
        public void cm() throws ParseException {
            this.unit = SVGLength.SVG_LENGTHTYPE_CM;
        }

        /**
         * Implements {@link LengthHandler#mm()}.
         */
        public void mm() throws ParseException {
            this.unit = SVGLength.SVG_LENGTHTYPE_MM;
        }

        /**
         * Implements {@link LengthHandler#pc()}.
         */
        public void pc() throws ParseException {
            this.unit = SVGLength.SVG_LENGTHTYPE_PC;
        }

        /**
         * Implements {@link LengthHandler#pt()}.
         */
        public void pt() throws ParseException {
            this.unit = SVGLength.SVG_LENGTHTYPE_PT;
        }

        /**
         * Implements {@link LengthHandler#px()}.
         */
        public void px() throws ParseException {
            this.unit = SVGLength.SVG_LENGTHTYPE_PX;
        }

        /**
         * Implements {@link LengthHandler#percentage()}.
         */
        public void percentage() throws ParseException {
            this.unit = SVGLength.SVG_LENGTHTYPE_PERCENTAGE;
        }

        /**
         * Implements {@link LengthHandler#endLength()}.
         */
        public void endLength() throws ParseException {
        }
    }

    /**
     * Holds the informations needed to compute the units.
     */
    public interface Context {

        /**
         * Returns the element.
         */
        Element getElement();

        /**
         * Returns the pixel to mm factor.
         */
        float getPixelToMM();

        /**
         * Returns the font-size value.
         */
        float getFontSize();

        /**
         * Returns the x-height value.
         */
        float getXHeight();

        /**
         * Returns the viewport width used to compute units.
         */
        float getViewportWidth();

        /**
         * Returns the viewport height used to compute units.
         */
        float getViewportHeight();
    }

    /**
     * This class is the default context for a particular
     * element. Informations not available on the element are get from
     * the bridge context (such as the viewport or the pixel to
     * millimeter factor.
     */
    public static class DefaultContext implements Context {

        /** The element. */
        protected Element e;
        protected BridgeContext ctx;

        public DefaultContext(BridgeContext ctx, Element e) {
            this.ctx = ctx;
            this.e = e;
        }

        /**
         * Returns the element.
         */
        public Element getElement() {
            return e;
        }

        /**
         * Returns the pixel to mm factor.
         */
        public float getPixelToMM() {
            return ctx.getUserAgent().getPixelToMM();
        }

        /**
         * Returns the font-size value.
         */
        public float getFontSize() {
            return CSSUtilities.getComputedStyle
                (e, SVGCSSEngine.FONT_SIZE_INDEX).getFloatValue();
        }

        /**
         * Returns the x-height value.
         */
        public float getXHeight() {
            return 0.5f;
        }

        /**
         * Returns the viewport width used to compute units.
         */
        public float getViewportWidth() {
            return ctx.getViewport(e).getWidth();
        }

        /**
         * Returns the viewport height used to compute units.
         */
        public float getViewportHeight() {
            return ctx.getViewport(e).getHeight();
        }
    }
}
