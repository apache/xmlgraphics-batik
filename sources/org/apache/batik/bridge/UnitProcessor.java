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
public abstract class UnitProcessor
    extends org.apache.batik.util.UnitProcessor {

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
            return org.apache.batik.util.UnitProcessor.
                svgToObjectBoundingBox(s, attr, d, ctx);
        } catch (ParseException ex) {
            throw new BridgeException(ctx.getElement(),
                                  ErrorConstants.ERR_ATTRIBUTE_VALUE_MALFORMED,
                                      new Object[] {attr, s, ex});
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
            return org.apache.batik.util.UnitProcessor.
                svgToUserSpace(s, attr, d, ctx);
        } catch (ParseException ex) {
            throw new BridgeException(ctx.getElement(),
                                 ErrorConstants.ERR_ATTRIBUTE_VALUE_MALFORMED,
                                      new Object[] {attr, s, ex});
        }
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
         * Returns the size of a px CSS unit in millimeters.
         */
        public float getPixelUnitToMillimeter() {
            return ctx.getUserAgent().getPixelUnitToMillimeter();
        }

        /**
         * Returns the size of a px CSS unit in millimeters.
         * This will be removed after next release.
         * @see #getPixelUnitToMillimeter();
         */
        public float getPixelToMM() {
            return getPixelUnitToMillimeter();
            
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
