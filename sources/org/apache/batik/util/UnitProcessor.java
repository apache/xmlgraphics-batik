/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.Reader;
import java.io.StringReader;

import org.apache.batik.css.HiddenChildElementSupport;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.util.resources.Messages;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGTransformable;

/**
 * This class contains utility methods for processing the SVG and CSS
 * units.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
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
     * This class does not need to be instantiated.
     */
    protected UnitProcessor() {
    }

    /**
     * Converts a SVG length value to user space.
     * @param t the unit type like specified in the CSSPrimitiveType interface.
     * @param v the length value.
     * @param e the element.
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH or OTHER_LENGTH.
     * @param c The context.
     * @exception RuntimeException If an invalid unit type is specified.
     */
    public static float cssToUserSpace(short t, float v, SVGElement e, short d,
                                       Context c) throws RuntimeException {
        if (t == CSSPrimitiveValue.CSS_NUMBER) {
            return v;
        }
        float f = c.getPixelToMM();
        switch (t) {
        case CSSPrimitiveValue.CSS_PX:
            return v;
        case CSSPrimitiveValue.CSS_MM:
            return (v / f);
        case CSSPrimitiveValue.CSS_CM:
            return (v * 10 / f);
        case CSSPrimitiveValue.CSS_IN:
            return (v * 25.4f / f);
        case CSSPrimitiveValue.CSS_PT:
            return (v * 25.4f / (72 * f));
        case CSSPrimitiveValue.CSS_PC:
            return (v * 25.4f / (6 * f));
        case CSSPrimitiveValue.CSS_EMS:
            return emsToPixels(v, e, d, c);
        case CSSPrimitiveValue.CSS_EXS:
            return exsToPixels(v, e, d, c);
        case CSSPrimitiveValue.CSS_PERCENTAGE:
            return percentagesToPixels(v, e, d, c);
        default:
            throw new IllegalArgumentException
                (Messages.formatMessage("invalid.css.unit",
                                        new Object[] { new Integer(t) }));
        }
    }

    /**
     * Converts a SVG length value to screen pixels.
     * @param t the unit type like specified in the SVGLength interface.
     * @param v the length value.
     * @param e the element.
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH or OTHER_LENGTH.
     * @param c The context.
     * @exception RuntimeException If an invalid unit type is specified.
     */
    public static float userSpaceToSVG(short t, float v, SVGElement e, short d,
                                       Context c) throws RuntimeException {
        if (t == SVGLength.SVG_LENGTHTYPE_NUMBER) {
            return v;
        }
        float f = c.getPixelToMM();
        switch (t) {
        case SVGLength.SVG_LENGTHTYPE_PX:
            return v;
        case SVGLength.SVG_LENGTHTYPE_MM:
            return (v * f);
        case SVGLength.SVG_LENGTHTYPE_CM:
            return (v * f / 10);
        case SVGLength.SVG_LENGTHTYPE_IN:
            return (v * f / 25.4f);
        case SVGLength.SVG_LENGTHTYPE_PT:
            return (v * (72 * f) / 25.4f);
        case SVGLength.SVG_LENGTHTYPE_PC:
            return (v * (6 * f) / 25.4f);
        case SVGLength.SVG_LENGTHTYPE_EMS:
            return pixelsToEms(v, e, d, c);
        case SVGLength.SVG_LENGTHTYPE_EXS:
            return pixelsToExs(v, e, d, c);
        case SVGLength.SVG_LENGTHTYPE_PERCENTAGE:
            return pixelsToPercentages(v, e, d, c);
        default:
            throw new IllegalArgumentException
                (Messages.formatMessage("invalid.svg.unit",
                                        new Object[] { new Integer(t) }));
        }
    }

    /**
     * Converts a SVG length value to screen pixels.
     * @param t the unit type like specified in the SVGLength interface.
     * @param v the length value.
     * @param e the element.
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH or OTHER_LENGTH.
     * @param c The context.
     * @exception RuntimeException If an invalid unit type is specified.
     */
    public static float svgToUserSpace(short t, float v, SVGElement e, short d,
                                       Context c) throws RuntimeException {
        if (t == SVGLength.SVG_LENGTHTYPE_NUMBER) {
            return v;
        }
        float f = c.getPixelToMM();
        switch (t) {
        case SVGLength.SVG_LENGTHTYPE_PX:
            return v;
        case SVGLength.SVG_LENGTHTYPE_MM:
            return (v / f);
        case SVGLength.SVG_LENGTHTYPE_CM:
            return (v * 10 / f);
        case SVGLength.SVG_LENGTHTYPE_IN:
            return (v * 25.4f / f);
        case SVGLength.SVG_LENGTHTYPE_PT:
            return (v * 25.4f / (72 * f));
        case SVGLength.SVG_LENGTHTYPE_PC:
            return (v * 25.4f / (6 * f));
        case SVGLength.SVG_LENGTHTYPE_EMS:
            return emsToPixels(v, e, d, c);
        case SVGLength.SVG_LENGTHTYPE_EXS:
            return exsToPixels(v, e, d, c);
        case SVGLength.SVG_LENGTHTYPE_PERCENTAGE:
            return percentagesToPixels(v, e, d, c);
        default:
            throw new IllegalArgumentException
                (Messages.formatMessage("invalid.svg.unit",
                                        new Object[] { new Integer(t) }));
        }
/*
        if (t == SVGLength.SVG_LENGTHTYPE_NUMBER) {
            return v;
        }
        float f = c.getPixelToMM();
        switch (t) {
        case SVGLength.SVG_LENGTHTYPE_PX:
            return pixelsToNumber(v, e, d, c);
        case SVGLength.SVG_LENGTHTYPE_MM:
            return pixelsToNumber(v / f, e, d, c);
        case SVGLength.SVG_LENGTHTYPE_CM:
            return pixelsToNumber(v * 10 / f, e, d, c);
        case SVGLength.SVG_LENGTHTYPE_IN:
            return pixelsToNumber(v * 25.4f / f, e, d, c);
        case SVGLength.SVG_LENGTHTYPE_PT:
            return pixelsToNumber(v * 25.4f / (72 * f), e, d, c);
        case SVGLength.SVG_LENGTHTYPE_PC:
            return pixelsToNumber(v * 25.4f / (6 * f), e, d, c);
        case SVGLength.SVG_LENGTHTYPE_EMS:
            return pixelsToNumber(emsToPixels(v, e, d, c), e, d, c);
        case SVGLength.SVG_LENGTHTYPE_EXS:
            return pixelsToNumber(exsToPixels(v, e, d, c), e, d, c);
        case SVGLength.SVG_LENGTHTYPE_PERCENTAGE:
            return pixelsToNumber(percentagesToPixels(v, e, d, c), e, d, c);
        default:
            throw new RuntimeException
                (formatMessage("invalid.svg.unit",
                               new Object[] { new Integer(t) }));
        }
        */

    }

    /**
     * Converts a pixels units to user space units.
     * @param v the length value.
     * @param e the element.
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH or OTHER_LENGTH.
     * @param c The context.
     * @exception RuntimeException If an invalid unit type is specified.
     */
/*    protected static float pixelsToNumber(float v, SVGElement e, short d,
                                          Context c) {
        // Compute the current transformation matrix (CTM).
        AffineTransform ctm = null;
        for (Element t = e;
             t != null;
             t = HiddenChildElementSupport.getParentElement(t)) {
            if (t instanceof SVGTransformable) {
                if (ctm == null) {
                    ctm = new AffineTransform();
                }
                String s = t.getAttributeNS(null, "transform");
                Reader r = new StringReader(s);
                ParserFactory pf = c.getParserFactory();
                AffineTransform at;
                at = AWTTransformProducer.createAffineTransform(r, pf);
                ctm.preConcatenate(at);
            } else if (t == e) {
                break;
            } else if (t instanceof SVGSVGElement) {
                SVGSVGElement elt = (SVGSVGElement)t;
                // !!! Use a parser for SVGLength
                SVGLength len = elt.getWidth().getBaseVal();
                float w = svgToUserSpace(len.getUnitType(),
                                         len.getValueInSpecifiedUnits(),
                                         elt,
                                         HORIZONTAL_LENGTH,
                                         c);
                len = elt.getHeight().getBaseVal();
                float h = svgToUserSpace(len.getUnitType(),
                                         len.getValueInSpecifiedUnits(),
                                         elt,
                                         VERTICAL_LENGTH,
                                         c);
                AffineTransform at;
                at = SVGUtilities.getPreserveAspectRatioTransform
                    ((SVGElement)t, w, h, c.getParserFactory());
                ctm.preConcatenate(at);
                break;
            }
        }
        if (ctm == null) {
            return v;
        }

        try {
            ctm = ctm.createInverse();
        } catch (NoninvertibleTransformException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        Point2D pt1 = new Point2D.Float();
        Point2D pt2;
        if (d == OTHER_LENGTH) {
            SVGSVGElement svg = c.getViewport();
            if (svg == null) {
                return v;
            }
            // !!! Use a parser for SVGLength
            SVGLength len = svg.getWidth().getBaseVal();
            double dx = svgToUserSpace(len.getUnitType(),
                                       len.getValueInSpecifiedUnits(),
                                       svg,
                                       HORIZONTAL_LENGTH,
                                       c);
            len = svg.getHeight().getBaseVal();
            double dy = svgToUserSpace(len.getUnitType(),
                                       len.getValueInSpecifiedUnits(),
                                       svg,
                                       VERTICAL_LENGTH,
                                       c);
            double vppx = Math.sqrt(dx * dx + dy * dy);
            pt2 = new Point2D.Float((float)dx, (float)dy);
            pt1 = ctm.transform(pt1, pt1);
            pt2 = ctm.transform(pt2, pt2);
            dx = pt2.getX() - pt1.getX();
            dy = pt2.getY() - pt1.getY();
            double vpduser = Math.sqrt(dx * dx + dy * dy);
            return (float)(v * vpduser / vppx);
        } else {
            pt2 = (d == HORIZONTAL_LENGTH)
                ? new Point2D.Float(v, 0)
                : new Point2D.Float(0, v);
            pt1 = ctm.transform(pt1, pt1);
            pt2 = ctm.transform(pt2, pt2);
            double dx = pt2.getX() - pt1.getX();
            double dy = pt2.getY() - pt1.getY();
            int sgn = (v < 0) ? -1 : 1;
            return (float)(sgn * Math.sqrt(dx * dx + dy * dy));
        }
    }
*/
    /**
     * Converts pixels units to ems units.
     * @param v the length value.
     * @param e the element.
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH or OTHER_LENGTH.
     * @param c The context.
     * @exception RuntimeException If an invalid unit type is specified.
     */
    protected static float pixelsToEms(float v, SVGElement e, short d,
                                       Context c) {
        if (e == null) {
            throw new RuntimeException
                (Messages.formatMessage("element.needed", null));
        }
        CSSPrimitiveValue val = c.getFontSize(e);
        short type = val.getPrimitiveType();
        return v / cssToUserSpace
            (type,
             val.getFloatValue(type),
             (SVGElement)HiddenChildElementSupport.getParentElement(e),
             d,
             c);
    }

    /**
     * Converts ems units to pixels units.
     * @param v the length value.
     * @param e the element.
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH or OTHER_LENGTH.
     * @param c The context.
     * @exception RuntimeException If an invalid unit type is specified.
     */
    protected static float emsToPixels(float v, SVGElement e, short d,
                                       Context c) {
        if (e == null) {
            throw new RuntimeException
                (Messages.formatMessage("element.needed", null));
        }
        CSSPrimitiveValue val = c.getFontSize(e);
        short type = val.getPrimitiveType();
        return v * cssToUserSpace
            (type,
             val.getFloatValue(type),
             (SVGElement)HiddenChildElementSupport.getParentElement(e),
             d,
             c);
    }

    /**
     * Converts pixels units to exs units.
     * @param v the length value.
     * @param e the element.
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH or OTHER_LENGTH.
     * @param c The context.
     * @exception RuntimeException If an invalid unit type is specified.
     */
    protected static float pixelsToExs(float v, SVGElement e, short d,
                                       Context c) {
        if (e == null) {
            throw new RuntimeException
                (Messages.formatMessage("element.needed", null));
        }
        CSSPrimitiveValue val = c.getFontSize(e);
        short type = val.getPrimitiveType();
        float fs = cssToUserSpace
            (type,
             val.getFloatValue(type),
             (SVGElement)HiddenChildElementSupport.getParentElement(e),
             d,
             c);
        float xh = c.getXHeight(e);
        return v / xh / fs;
    }

    /**
     * Converts exs units to pixels units.
     * @param v the length value.
     * @param e the element.
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH or OTHER_LENGTH.
     * @param c The context.
     * @exception RuntimeException If an invalid unit type is specified.
     */
    protected static float exsToPixels(float v, SVGElement e, short d,
                                       Context c) {
        if (e == null) {
            throw new RuntimeException
                (Messages.formatMessage("element.needed", null));
        }
        CSSPrimitiveValue val = c.getFontSize(e);
        short type = val.getPrimitiveType();
        float fs = cssToUserSpace
            (type,
             val.getFloatValue(type),
             (SVGElement)HiddenChildElementSupport.getParentElement(e),
             d,
             c);
        float xh = c.getXHeight(e);
        return v * xh * fs;
    }

    /**
     * Converts percentages units to pixels units.
     * @param v the length value.
     * @param e the element.
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH or OTHER_LENGTH.
     * @param c The context.
     * @exception RuntimeException If an invalid unit type is specified.
     */
    protected static float pixelsToPercentages(float v, SVGElement e, short d,
                                               Context c) {
        if (e == null) {
            throw new RuntimeException
                (Messages.formatMessage("element.needed", null));
        }
        if (d == HORIZONTAL_LENGTH) {
            float w = c.getViewportWidth();
            return v * 100 / w;
        } else if (d == VERTICAL_LENGTH) {
            float h = c.getViewportHeight();
            return v * 100 / h;
        } else {
            double w = c.getViewportWidth();
            double h = c.getViewportHeight();
            double vpp = Math.sqrt(w * w + h * h) / Math.sqrt(2);
            return (float)(v * 100 / vpp);
        }
    }

    /**
     * Converts percentages units to pixels units.
     * @param v the length value.
     * @param e the element.
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH or OTHER_LENGTH.
     * @param c The context.
     * @exception RuntimeException If an invalid unit type is specified.
     */
    protected static float percentagesToPixels(float v, SVGElement e, short d,
                                               Context c) {
        if (e == null) {
            throw new RuntimeException
                (Messages.formatMessage("element.needed", null));
        }
        if (d == HORIZONTAL_LENGTH) {
            float w = c.getViewportWidth();
            return w * v / 100;
        } else if (d == VERTICAL_LENGTH) {
            float h = c.getViewportHeight();
            return h * v / 100;
        } else {
            double w = c.getViewportWidth();
            double h = c.getViewportHeight();
            double vpp = Math.sqrt(w * w + h * h) / Math.sqrt(2);
            return (float)(vpp * v / 100);
        }
    }

    /**
     * An utility method to implement Context.getFontSize(SVGElement e).
     */
    public static CSSPrimitiveValue getFontSize(SVGElement e,
                                                CSSStyleDeclaration d) {
        return(CSSPrimitiveValue)d.getPropertyCSSValue
            (CSSConstants.CSS_FONT_SIZE_PROPERTY);
    }

    /**
     * Converts a SVG length value to screen pixels.
     * @param value the length value
     * @param e the element.
     * @param d HORIZONTAL_LENGTH, VERTICAL_LENGTH or OTHER_LENGTH.
     * @param c The context.
     * @exception RuntimeException If an invalid unit type is specified.
     */
    public static float svgToUserSpace(String value, SVGElement e, short d,
                                       Context c) {
        if (value.length() == 0) {
            return 0;
        }
        LengthParser p = new LengthParser();
        UnitResolver ur = new UnitResolver();
        p.setLengthHandler(ur);
        p.parse(new StringReader(value));
        return svgToUserSpace(ur.unit, ur.value, e, d, c);
    }

    /**
     * A simple class that can convert units.
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
         * Returns the pixel to mm factor.
         */
        float getPixelToMM();

        /**
         * Returns the font-size medium value in pt.
         */
        float getMediumFontSize();

        /**
         * Returns the font-size value.
         */
        CSSPrimitiveValue getFontSize(SVGElement e);

        /**
         * Returns the x-height value.
         */
        float getXHeight(SVGElement e);

        /**
         * Returns the viewport width used to compute units.
         */
        float getViewportWidth();

        /**
         * Returns the viewport height used to compute units.
         */
        float getViewportHeight();
    }
}
