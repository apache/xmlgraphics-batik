/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.apache.batik.bridge.UserAgent;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.ParserFactory;
import org.apache.batik.parser.PreserveAspectRatioHandler;
import org.apache.batik.parser.PreserveAspectRatioParser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGPreserveAspectRatio;

/**
 * This class contains utility methods for SVG.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGUtilities implements SVGConstants {
    /**
     * This class does not need to be instantiated.
     */
    protected SVGUtilities() {
    }

    /**
     * Represents an empty string attribute.
     */
    public final static int EMPTY = 0;

    // filters 'in' attribute ////////////////////////////////////////////

    /**
     * Represents 'BackgroundAlpha'.
     */
    public final static int BACKGROUND_ALPHA = 1;

    /**
     * Represents 'BackgroundImage'.
     */
    public final static int BACKGROUND_IMAGE = 2;

    /**
     * Represents 'FillPaint'.
     */
    public final static int FILL_PAINT = 3;

    /**
     * Represents 'SourceAlpha'.
     */
    public final static int SOURCE_ALPHA = 4;

    /**
     * Represents 'SourceGraphic'.
     */
    public final static int SOURCE_GRAPHIC = 5;

    /**
     * Represents 'StrokePaint'.
     */
    public final static int STROKE_PAINT = 6;

    /**
     * Represents an identifier.
     */
    public final static int IDENTIFIER = 7;

    /**
     * Represents 'objectBoundingBox'.
     */
    public final static int OBJECT_BOUNDING_BOX = 0;

    /**
     * Represents 'userSpaceOnUse'.
     */
    public final static int USER_SPACE_ON_USE = 1;

    /**
     * Parse the given specified coordinate system.
     */
    public static int parseCoordinateSystem(String value) {
        int len = value.length();
        if (len == 0) {
            value = VALUE_OBJECT_BOUNDING_BOX;
            len = value.length();
        }
        if (len != 0) {
            switch(value.charAt(0)) {
            case 'u':
                if (len == VALUE_USER_SPACE_ON_USE.length() &&
                    value.charAt(1) == 's' &&
                    value.charAt(2) == 'e' &&
                    value.charAt(3) == 'r' &&
                    value.charAt(4) == 'S' &&
                    value.charAt(5) == 'p' &&
                    value.charAt(6) == 'a' &&
                    value.charAt(7) == 'c' &&
                    value.charAt(8) == 'e' &&
                    value.charAt(9) == 'O' &&
                    value.charAt(10) == 'n' &&
                    value.charAt(11) == 'U' &&
                    value.charAt(12) == 's' &&
                    value.charAt(13) == 'e') {
                    return USER_SPACE_ON_USE;
            }
            case 'o':
                if (len == VALUE_OBJECT_BOUNDING_BOX.length() &&
                    value.charAt(1) == 'b' &&
                    value.charAt(2) == 'j' &&
                    value.charAt(3) == 'e' &&
                    value.charAt(4) == 'c' &&
                    value.charAt(5) == 't' &&
                    value.charAt(6) == 'B' &&
                    value.charAt(7) == 'o' &&
                    value.charAt(8) == 'u' &&
                    value.charAt(9) == 'n' &&
                    value.charAt(10) == 'd' &&
                    value.charAt(11) == 'i' &&
                    value.charAt(12) == 'n' &&
                    value.charAt(13) == 'g' &&
                    value.charAt(14) == 'B' &&
                    value.charAt(15) == 'o' &&
                    value.charAt(16) == 'x') {
                    return OBJECT_BOUNDING_BOX;
                }
            }
        }
        throw new IllegalArgumentException("Bad coordinate system: "+value);
    }

    /**
     * Parses the given 'in' attribute value.
     * @return one of BACKGROUND_ALPHA, BACKGROUND_IMAGE, FILL_PAINT,
     *         SOURCE_ALPHA, SOURCE_GRAPHIC, STROKE_PAINT, IDENTIFIER or
     *         EMPTY.
     */
    public static int parseInAttribute(String value) {
        int len = value.length();
        if (value.length() == 0) {
            return EMPTY;
        }

        switch (value.charAt(0)) {
        case 'B':
            if (len != 15) {
                return IDENTIFIER;
            }
            switch (value.charAt(10)) {
            case 'A':
                if (value.charAt(1) != 'a' ||
                    value.charAt(2) != 'c' ||
                    value.charAt(3) != 'k' ||
                    value.charAt(4) != 'g' ||
                    value.charAt(5) != 'r' ||
                    value.charAt(6) != 'o' ||
                    value.charAt(7) != 'u' ||
                    value.charAt(8) != 'n' ||
                    value.charAt(9) != 'd' ||
                    value.charAt(11) != 'l' ||
                    value.charAt(12) != 'p' ||
                    value.charAt(13) != 'h' ||
                    value.charAt(14) != 'a') {
                    return IDENTIFIER;
                }
                return BACKGROUND_ALPHA;
            case 'I':
                if (value.charAt(1) != 'a' ||
                    value.charAt(2) != 'c' ||
                    value.charAt(3) != 'k' ||
                    value.charAt(4) != 'g' ||
                    value.charAt(5) != 'r' ||
                    value.charAt(6) != 'o' ||
                    value.charAt(7) != 'u' ||
                    value.charAt(8) != 'n' ||
                    value.charAt(9) != 'd' ||
                    value.charAt(11) != 'm' ||
                    value.charAt(12) != 'a' ||
                    value.charAt(13) != 'g' ||
                    value.charAt(14) != 'e') {
                    return IDENTIFIER;
                }
                return BACKGROUND_IMAGE;
            default:
                return IDENTIFIER;
            }
        case 'F':
            if (len != 9 ||
                value.charAt(1) != 'i' ||
                value.charAt(2) != 'l' ||
                value.charAt(3) != 'l' ||
                value.charAt(4) != 'P' ||
                value.charAt(5) != 'a' ||
                value.charAt(6) != 'i' ||
                value.charAt(7) != 'n' ||
                value.charAt(8) != 't') {
                return IDENTIFIER;
            }
            return FILL_PAINT;
        case 'S':
            if (len < 11) {
                return IDENTIFIER;
            }
            switch (value.charAt(1)) {
            case 'o':
                switch (value.charAt(6)) {
                case 'A':
                    if (len != 11 ||
                        value.charAt(2) != 'u' ||
                        value.charAt(3) != 'r' ||
                        value.charAt(4) != 'c' ||
                        value.charAt(5) != 'e' ||
                        value.charAt(7) != 'l' ||
                        value.charAt(8) != 'p' ||
                        value.charAt(9) != 'h' ||
                        value.charAt(10) != 'a') {
                        return IDENTIFIER;
                    }
                    return SOURCE_ALPHA;
                case 'G':
                    if (len != 13 ||
                        value.charAt(2) != 'u' ||
                        value.charAt(3) != 'r' ||
                        value.charAt(4) != 'c' ||
                        value.charAt(5) != 'e' ||
                        value.charAt(7) != 'r' ||
                        value.charAt(8) != 'a' ||
                        value.charAt(9) != 'p' ||
                        value.charAt(10) != 'h' ||
                        value.charAt(11) != 'i' ||
                        value.charAt(12) != 'c') {
                        return IDENTIFIER;
                    }
                    return SOURCE_GRAPHIC;
                default:
                    return IDENTIFIER;
                }
            case 't':
                if (len != 11 ||
                    value.charAt(2) != 'r' ||
                    value.charAt(3) != 'o' ||
                    value.charAt(4) != 'k' ||
                    value.charAt(5) != 'e' ||
                    value.charAt(6) != 'P' ||
                    value.charAt(7) != 'a' ||
                    value.charAt(8) != 'i' ||
                    value.charAt(9) != 'n' ||
                    value.charAt(10) != 't') {
                    return IDENTIFIER;
                }
                return STROKE_PAINT;
            default:
                return IDENTIFIER;
            }
        default:
            return IDENTIFIER;
        }
    }

    /**
     * Parses a viewBox attribute.
     * @return The 4 viewbox components or null.
     */
    public static float[] parseViewBoxAttribute(String value) {
        if (value.length() == 0) {
            return null;
        }
        int i = 0;
        float[] result = new float[4];
        StringTokenizer st = new StringTokenizer(value, " ,");
        while (i < 4 && st.hasMoreTokens()) {
            result[i] = Float.parseFloat(st.nextToken());
            i++;
        }
        return result;
    }

    /**
     * Returns the transformation to apply to initalize a viewport.
     * @param elt The document node.
     * @param w   The effective viewport width.
     * @param h   The effective viewport height.
     */
    public static AffineTransform getPreserveAspectRatioTransform
            (SVGElement    elt,
             float         w,
             float         h,
             ParserFactory pf) {
        AffineTransform result = new AffineTransform();
        String vba = elt.getAttributeNS(null, ATTR_VIEW_BOX);
        float[] vb = parseViewBoxAttribute(vba);

        if (vb == null ||
            vb[2] == 0 ||
            vb[3] == 0) {
            return result;
        }

        PreserveAspectRatioParser p = pf.createPreserveAspectRatioParser();
        PreserveAspectRatio ph = new PreserveAspectRatio();
        p.setPreserveAspectRatioHandler(ph);

        p.parse(new StringReader(elt.getAttributeNS
                                 (null, ATTR_PRESERVE_ASPECT_RATIO)));

        float vpar  = vb[2] / vb[3];
        float svgar = w / h;

        if (ph.align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_NONE) {
                result.scale(w / vb[2], h / vb[3]);
                result.translate(-vb[0], -vb[1]);
        } else if (vpar < svgar && ph.meet || vpar >= svgar && !ph.meet) {
            float sf = h / vb[3];
            result.scale(sf, sf);
            switch (ph.align) {
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMIN:
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMID:
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMAX:
                result.translate(-vb[0], -vb[1]);
                break;
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMIN:
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMID:
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMAX:
                result.translate(-vb[0] - (vb[2] - w * vb[3] / h) / 2 , -vb[1]);
                break;
            default:
                result.translate(-vb[0] - (vb[2] - w * vb[3] / h) , -vb[1]);
            }
        } else {
            float sf = w / vb[2];
            result.scale(sf, sf);
            switch (ph.align) {
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMIN:
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMIN:
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMAXYMIN:
                result.translate(-vb[0], -vb[1]);
                break;
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMID:
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMID:
            case SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMAXYMID:
                result.translate(-vb[0], -vb[1] - (vb[3] - h * vb[2] / w) / 2);
                break;
            default:
                result.translate(-vb[0], -vb[1] - (vb[3] - h * vb[2] / w));
            }
        }
        return result;
    }

    /**
     * To store the preserveAspectRatio attribute values.
     */
    protected static class PreserveAspectRatio
        implements PreserveAspectRatioHandler {
        public short align;
        public boolean meet;

        /**
         * Invoked when the PreserveAspectRatio parsing starts.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void startPreserveAspectRatio() throws ParseException {
        }

        /**
         * Invoked when 'none' been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void none() throws ParseException {
            align = SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_NONE;
        }

        /**
         * Invoked when 'xMaxYMax' has been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void xMaxYMax() throws ParseException {
            align = SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMAXYMAX;
        }

        /**
         * Invoked when 'xMaxYMid' has been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void xMaxYMid() throws ParseException {
            align = SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMAXYMID;
        }

        /**
         * Invoked when 'xMaxYMin' has been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void xMaxYMin() throws ParseException {
            align = SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMAXYMIN;
        }

        /**
         * Invoked when 'xMidYMax' has been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void xMidYMax() throws ParseException {
            align = SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMAX;
        }

        /**
         * Invoked when 'xMidYMid' has been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void xMidYMid() throws ParseException {
            align = SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMID;
        }

        /**
         * Invoked when 'xMidYMin' has been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void xMidYMin() throws ParseException {
            align = SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMIN;
        }

        /**
         * Invoked when 'xMinYMax' has been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void xMinYMax() throws ParseException {
            align = SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMAX;
        }

        /**
         * Invoked when 'xMinYMid' has been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void xMinYMid() throws ParseException {
            align = SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMID;
        }

        /**
         * Invoked when 'xMinYMin' has been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void xMinYMin() throws ParseException {
            align = SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMIN;
        }

        /**
         * Invoked when 'meet' has been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void meet() throws ParseException {
            meet = true;
        }

        /**
         * Invoked when 'slice' has been parsed.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void slice() throws ParseException {
            meet = false;
        }

        /**
         * Invoked when the PreserveAspectRatio parsing ends.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void endPreserveAspectRatio() throws ParseException {
        }
    }

    /**
     * Returns the content of the 'desc' child of the given element.
     */
    public static String getDescription(SVGElement elt) {
        String result = "";
        boolean preserve = false;
        Node n = elt.getFirstChild();
        if (n != null && n.getNodeType() == Node.ELEMENT_NODE) {
            String name = (n.getPrefix() == null)
                ? n.getNodeName()
                : n.getLocalName();
            if (name.equals(TAG_DESC)) {
                preserve
                    = ((SVGLangSpace)n).getXMLspace().equals(VALUE_PRESERVE);
                for (n = n.getFirstChild();
                     n != null;
                     n = n.getNextSibling()) {
                    if (n.getNodeType() == Node.TEXT_NODE) {
                        result += n.getNodeValue();
                    }
                }
            }
        }
        return (preserve)
            ? XMLSupport.preserveXMLSpace(result)
            : XMLSupport.defaultXMLSpace(result);
    }

    /**
     * Tests whether or not the given element match a user agent.
     */
    public static boolean matchUserAgent(Element elt, UserAgent ua) {
        if (elt.hasAttributeNS(null, ATTR_SYSTEM_LANGUAGE)) {
            // Evaluates the system languages
            String sl = elt.getAttributeNS(null, ATTR_SYSTEM_LANGUAGE);
            StringTokenizer st = new StringTokenizer(sl, ",");
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (matchUserLanguage(s, ua.getLanguages())) {
                    return true;
                }
            }
        }
        // !!! TODO requiredFeatures, requiredExtensions
        return false;
    }

    /**
     * Tests whether the given language specification match the
     * user preferences.
     */
    protected static boolean matchUserLanguage(String s, String userLanguages) {
        StringTokenizer st = new StringTokenizer(userLanguages, ",");
        while (st.hasMoreTokens()) {
            String t = st.nextToken();
            if (s.startsWith(t)) {
                if (s.length() > t.length()) {
                    return (s.charAt(t.length()) == '-')
                        ? true
                        : false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Parses an SVG integer
     */
    public static int convertSVGInteger(String intStr){
        return Integer.parseInt(intStr);
    }

    /**
     * Parses an SVG number
     */
    public static float convertSVGNumber(String numStr){
        return Float.parseFloat(numStr);
    }

    // ------------------------------------------------------------------------
    // Region computation for <filter> <mask> and <pattern>
    // ------------------------------------------------------------------------

    /**
     * Creates a <tt>Rectangle2D</tt> for the &lt;filter> element.
     * Processing the element as the top one in the filter chain.
     *
     * @param filterElement the &lt;filter> element
     * @param filteredElement the element to filter
     * @param node the graphics node that represents the element to filter
     * @param uctx the context used to compute units and percentages
     */
    public static
        Rectangle2D convertFilterChainRegion(Element filterElement,
                                             Element filteredElement,
                                             GraphicsNode node,
                                             UnitProcessor.Context uctx) {

        return convertRegion(filterElement,
                             filteredElement,
                             node,
                             uctx,
                             ATTR_FILTER_UNITS,
                             VALUE_OBJECT_BOUNDING_BOX,
                             VALUE_FILTER_X_DEFAULT,
                             VALUE_FILTER_Y_DEFAULT,
                             VALUE_FILTER_WIDTH_DEFAULT,
                             VALUE_FILTER_HEIGHT_DEFAULT);
    }

    /**
     * Creates a <tt>Rectangle2D</tt> for the input &lt;mask> element.
     *
     * @param maskElement the &lt;mask> element
     * @param maskedElement the element to mask
     * @param node the graphics node that represents the element to mask
     * @param uctx the context used to compute units and percentages
     */
    public static
        Rectangle2D convertMaskRegion(Element maskElement,
                                      Element maskedElement,
                                      GraphicsNode node,
                                      UnitProcessor.Context uctx) {

        return convertRegion(maskElement,
                             maskedElement,
                             node,
                             uctx,
                             ATTR_MASK_UNITS,
                             VALUE_OBJECT_BOUNDING_BOX,
                             VALUE_MASK_X_DEFAULT,
                             VALUE_MASK_Y_DEFAULT,
                             VALUE_MASK_WIDTH_DEFAULT,
                             VALUE_MASK_HEIGHT_DEFAULT);
    }

    /**
     * Creates a <tt>Rectangle2D</tt> for the input &lt;pattern> element.
     *
     * @param patternElement the &lt;pattern> element
     * @param paintedElement the element to paint
     * @param node the graphics node that represents the element to paint
     * @param uctx the context used to compute units and percentages
     */
    public static
        Rectangle2D convertPatternRegion(Element patternElement,
                                         Element paintedElement,
                                         GraphicsNode node,
                                         UnitProcessor.Context uctx) {

        return convertRegion(patternElement,
                             paintedElement,
                             node,
                             uctx,
                             ATTR_PATTERN_UNITS,
                             VALUE_OBJECT_BOUNDING_BOX,
                             VALUE_PATTERN_X_DEFAULT,
                             VALUE_PATTERN_Y_DEFAULT,
                             VALUE_PATTERN_WIDTH_DEFAULT,
                             VALUE_PATTERN_HEIGHT_DEFAULT);
    }

    /**
     * Creates a <tt>Rectangle2D</tt> using the specified parameters.
     *
     * @param filterElement the filter Element or equivalent
     * @param filteredElement the element which uses the filter
     * @param node the graphics node representing the filtered element
     * @param uctx the context used to compute units and percentages
     * @param unitsAttr the units to consider on the specified filter element
     * @param unitsDefault the default value of the units if not specified on
     *                     the filter element
     * @param xDefault the default value of the x attribute, null means required
     * @param yDefault the default value of the x attribute, null means required
     * @param widthDefault the default value of the x attribute, null
     *                     means required
     * @param heightDefault the default value of the height attribute,
     *                      null means required
     */
    protected static Rectangle2D convertRegion(Element filterElement,
                                               Element filteredElement,
                                               GraphicsNode node,
                                               UnitProcessor.Context uctx,
                                               String unitsAttr,
                                               String unitsDefault,
                                               String xDefault,
                                               String yDefault,
                                               String widthDefault,
                                               String heightDefault) {

        String units = filterElement.getAttributeNS(null, unitsAttr);
        if (units.length() == 0) {
            units = unitsDefault;
        }
        int unitsType = parseCoordinateSystem(units);
        switch(unitsType) {
        case OBJECT_BOUNDING_BOX:
            return convertObjectBoundingBoxRegion(filterElement,
                                                  filteredElement,
                                                  node,
                                                  uctx,
                                                  xDefault,
                                                  yDefault,
                                                  widthDefault,
                                                  heightDefault);
        case USER_SPACE_ON_USE:
            return convertUserSpaceOnUseRegion(filterElement,
                                               filteredElement,
                                               node,
                                               uctx,
                                               xDefault,
                                               yDefault,
                                               widthDefault,
                                               heightDefault);
        default:
            /* Never happen: Bad coordinate system is catched previously */
            throw new Error();
        }
    }

    /**
     * Creates a <tt>Rectangle2D</tt> for the input filter or equivalent element
     * in the 'objectBoundingBox' coordinate system.
     *
     * @param filterElement the filter Element or equivalent
     * @param filteredElement the element which uses the filter
     * @param node the graphics node representing the filtered element
     * @param uctx the context used to compute units and percentages
     * @param xDefault the default value of the x attribute, null means required
     * @param yDefault the default value of the x attribute, null means required
     * @param widthDefault the default value of the x attribute, null
     *                     means required
     * @param heightDefault the default value of the height attribute,
     *                      null means required
     */
    protected static
        Rectangle2D convertObjectBoundingBoxRegion(Element filterElement,
                                                   Element filteredElement,
                                                   GraphicsNode node,
                                                   UnitProcessor.Context uctx,
                                                   String xDefault,
                                                   String yDefault,
                                                   String widthDefault,
                                                   String heightDefault) {

        SVGElement svgElement = (SVGElement)filteredElement;
        // x, y, width and height will hold the filter region size
        double x, y, width, height;

        // Resolve each of x, y, widht and height values.
        // For each value, we distinguish two cases: percentages
        // and other. If a percentage value is used, it is converted
        // to a 'FilterRegion' space coordinate by division by 100
        // Otherwise, standard unit conversion is used.
        LengthParser p = uctx.getParserFactory().createLengthParser();
        UnitProcessor.UnitResolver ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);

        // parse the x attribute
        String floatStr = getAttribute(filterElement, ATTR_X, xDefault);
        p.parse(new StringReader(floatStr));

        if(ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE){
            x = ur.value / 100;
        } else {
            x = UnitProcessor.svgToUserSpace(ur.unit,
                                             ur.value,
                                             svgElement,
                                             UnitProcessor.HORIZONTAL_LENGTH,
                                             uctx);
        }

        // parse the y attribute
        floatStr = getAttribute(filterElement, ATTR_Y, yDefault);
        ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);
        p.parse(new StringReader(floatStr));

        if (ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE){
            y = ur.value / 100;
        } else {
            y = UnitProcessor.svgToUserSpace(ur.unit, ur.value,
                                             svgElement,
                                             UnitProcessor.VERTICAL_LENGTH,
                                             uctx);
        }

        // parse the width attribute
        floatStr = getAttribute(filterElement, ATTR_WIDTH, widthDefault);
        ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);
        p.parse(new StringReader(floatStr));

        if (ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE){
            width = ur.value / 100;
        } else {
            width = UnitProcessor.svgToUserSpace(ur.unit,
                                                 ur.value,
                                                 svgElement,
                                                UnitProcessor.HORIZONTAL_LENGTH,
                                                 uctx);
        }

        // parse the height attribute
        floatStr = getAttribute(filterElement, ATTR_HEIGHT, heightDefault);
        ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);
        p.parse(new StringReader(floatStr));

        if(ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE){
            height = ur.value / 100;
        } else {
            height= UnitProcessor.svgToUserSpace(ur.unit, ur.value,
                                                 svgElement,
                                                 UnitProcessor.VERTICAL_LENGTH,
                                                 uctx);
        }

        // Now, take the bounds of the GraphicsNode into account
        Rectangle2D gnBounds = node.getGeometryBounds();

        height *= gnBounds.getHeight();
        width  *= gnBounds.getWidth();
        x       = gnBounds.getX() + x*gnBounds.getWidth();
        y       = gnBounds.getY() + y*gnBounds.getHeight();

        if (width < 0) {
            throw new IllegalArgumentException("width must be positive");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height must be positive");
        }

        return new Rectangle2D.Double(x, y, width, height);
    }

    /**
     * Creates a <tt>Rectangle2D</tt> for the input filter or equivalent element
     * in the 'userSpaceOnUse' coordinate system.
     *
     * @param filterElement the filter Element or equivalent
     * @param filteredElement the element which uses the filter
     * @param node the graphics node representing the filtered element
     * @param uctx the context used to compute units and percentages
     * @param xDefault the default value of the x attribute, null means required
     * @param yDefault the default value of the x attribute, null means required
     * @param widthDefault the default value of the x attribute, null
     *                     means required
     * @param heightDefault the default value of the height attribute,
     *                      null means required
     */
    protected static
        Rectangle2D convertUserSpaceOnUseRegion(Element filterElement,
                                                Element filteredElement,
                                                GraphicsNode node,
                                                UnitProcessor.Context uctx,
                                                String xDefault,
                                                String yDefault,
                                                String widthDefault,
                                                String heightDefault) {

        SVGElement svgElement = (SVGElement)filteredElement;

        // x, y, width and height will hold the filter region size
        double x, y, width, height;

        // parse the x attribute
        String floatStr = getAttribute(filterElement, ATTR_X, xDefault);
        x = UnitProcessor.svgToUserSpace(floatStr,
                                         svgElement,
                                         UnitProcessor.HORIZONTAL_LENGTH,
                                         uctx);

        // parse the y attribute
        floatStr = getAttribute(filterElement, ATTR_Y, yDefault);
        y = UnitProcessor.svgToUserSpace(floatStr,
                                         svgElement,
                                         UnitProcessor.VERTICAL_LENGTH,
                                         uctx);

        // parse the width attribute
        floatStr = getAttribute(filterElement, ATTR_WIDTH, widthDefault);
        width = UnitProcessor.svgToUserSpace(floatStr,
                                             svgElement,
                                             UnitProcessor.HORIZONTAL_LENGTH,
                                             uctx);

        // parse the height attribute
        floatStr = getAttribute(filterElement, ATTR_HEIGHT, heightDefault);
        height = UnitProcessor.svgToUserSpace(floatStr,
                                              svgElement,
                                              UnitProcessor.VERTICAL_LENGTH,
                                              uctx);
        if (width < 0) {
            throw new IllegalArgumentException("width must be positive");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height must be positive");
        }
        return new Rectangle2D.Double(x, y, width, height);
    }

    /**
     * Returns the attribute on the specified element with the
     * specified name and the specified default value.
     *
     * @param element the element that defined the attribute
     * @param attrName the name of the attribute to return
     * @param defaultValue the default value of the attribute, null
     *                     means required.
     */
    protected static String getAttribute(Element element,
                                         String attrName,
                                         String defaultValue) {

        String valueStr = element.getAttributeNS(null, attrName);
        if (valueStr.length() == 0) {
            if (defaultValue == null) { // check if attribute is required or not
                throw new IllegalArgumentException(attrName+" required");
            } else {
                valueStr = defaultValue;
            }
        }
        return valueStr;
    }

    // ------------------------------------------------------------------------
    // Coordinate computation for <linearGradient> and <radialGradient>
    // ------------------------------------------------------------------------

    /**
     * Creates a <tt>Point2D</tt> for the input x and y attribute value in the
     * 'units' coordinate system.
     *
     * @param svgElement the element that defines the specified coordinates
     * @param xStr the 'x' coordinate
     * @param yStr the 'y' coordinate
     * @param units the coordinate system
     * @param node the node that represents the element
     * @param uctx the context used to compute units and percentages
     */
    public static Point2D convertPoint(SVGElement svgElement,
                                       String xStr,
                                       String yStr,
                                       String units,
                                       GraphicsNode node,
                                       UnitProcessor.Context uctx) {
        // INTERNAL : check for correct arguments - should never happen
        if (xStr == null || xStr.length() == 0) {
            throw new IllegalArgumentException(
                "The x coordinate is null or empty");
        }
        if (yStr == null || yStr.length() == 0) {
            throw new IllegalArgumentException(
                "The y coordinate is null or empty");
        }

        int unitsType = parseCoordinateSystem(units);
        switch(unitsType) {
        case OBJECT_BOUNDING_BOX:
            return convertObjectBoundingBoxPoint(svgElement,
                                                 xStr,
                                                 yStr,
                                                 node,
                                                 uctx);
        case USER_SPACE_ON_USE:
            return convertUserSpaceOnUsePoint(svgElement,
                                              xStr,
                                              yStr,
                                              node,
                                              uctx);
        default:
            /* Never happen: Bad coordinate system is catched previously */
            throw new Error();
        }
    }

    /**
     * Creates a <tt>Point2D</tt> for the input x and y attribute value in the
     * 'objectBoundingBox' coordinate system.
     *
     * @param svgElement the element that defines the specified coordinates
     * @param xStr the 'x' coordinate
     * @param yStr the 'y' coordinate
     * @param node the node that represents the element
     * @param uctx the context used to compute units and percentages
     */
    protected static
        Point2D convertObjectBoundingBoxPoint(SVGElement svgElement,
                                              String xStr,
                                              String yStr,
                                              GraphicsNode node,
                                              UnitProcessor.Context uctx) {


        float x, y;

        // For each value, we distinguish two cases: percentages
        // and other. If a percentage value is used, it is converted
        // to a 'bounding box' space coordinate by division by 100
        // Otherwise, standard unit conversion is used.
        LengthParser p = uctx.getParserFactory().createLengthParser();
        UnitProcessor.UnitResolver ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);

        // parse the x attribute
        p.parse(new StringReader(xStr));
        if (ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
            x = ur.value / 100f;
        } else {
            x = UnitProcessor.svgToUserSpace(ur.unit,
                                             ur.value,
                                             svgElement,
                                             UnitProcessor.HORIZONTAL_LENGTH,
                                             uctx);
        }

        // parse the y attribute
        ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);
        p.parse(new StringReader(yStr));
        if (ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
            y = ur.value / 100f;
        } else {
            y = UnitProcessor.svgToUserSpace(ur.unit,
                                             ur.value,
                                             svgElement,
                                             UnitProcessor.VERTICAL_LENGTH,
                                             uctx);
        }

        return new Point2D.Float(x, y);
    }

    /**
     * Creates a <tt>Point2D</tt> for the input x and y attribute value in the
     * 'userSpaceOnUse' coordinate system.
     *
     * @param svgElement the element that defines the specified coordinates
     * @param xStr the 'x' coordinate
     * @param yStr the 'y' coordinate
     * @param node the node that represents the element
     * @param uctx the context used to compute units and percentages
     */
    protected static
        Point2D convertUserSpaceOnUsePoint(SVGElement svgElement,
                                           String xStr,
                                           String yStr,
                                           GraphicsNode node,
                                           UnitProcessor.Context uctx) {

        // parse the x attribute
        float x = UnitProcessor.svgToUserSpace(xStr,
                                               svgElement,
                                               UnitProcessor.HORIZONTAL_LENGTH,
                                               uctx);

        // parse the y attribute
        float y = UnitProcessor.svgToUserSpace(yStr,
                                               svgElement,
                                               UnitProcessor.VERTICAL_LENGTH,
                                               uctx);
        return new Point2D.Float(x, y);
    }

    // ------------------------------------------------------------------------
    // Length computation for <radialGradient>
    // ------------------------------------------------------------------------

    /**
     * Creates a float value from the input value in the 'units'
     * coordinate system.
     *
     * @param svgElement the element that defines the specified length
     * @param lengthStr the length value
     * @param units the coordinate system
     * @param node the node that represents the element
     * @param uctx the context used to compute units and percentages
     */
    public static float convertLength(SVGElement svgElement,
                                      String lengthStr,
                                      String units,
                                      GraphicsNode node,
                                      UnitProcessor.Context uctx) {
        // INTERNAL : check for correct arguments - should never happen
        if (lengthStr == null || lengthStr.length() == 0) {
            throw new IllegalArgumentException("The length is null or empty");
        }

        int unitsType = parseCoordinateSystem(units);
        switch(unitsType) {
        case OBJECT_BOUNDING_BOX:
            return convertObjectBoundingBoxLength(svgElement,
                                                  lengthStr,
                                                  node,
                                                  uctx);
        case USER_SPACE_ON_USE:
            return convertUserSpaceOnUseLength(svgElement,
                                               lengthStr,
                                               node,
                                               uctx);
        default:
            /* Never happen: Bad coordinate system is catched previously */
            throw new Error();
        }
    }

    /**
     * Creates a float value from the input value in the 'objectBoundingBox'
     * coordinate system.
     *
     * @param svgElement the element that defines the specified length
     * @param lengthStr the length value
     * @param node the node that represents the element
     * @param uctx the context used to compute units and percentages
     */
    protected static
        float convertObjectBoundingBoxLength(SVGElement svgElement,
                                             String lengthStr,
                                             GraphicsNode node,
                                             UnitProcessor.Context uctx) {

        float length;

        // We distinguish two cases: percentages
        // and other. If a percentage value is used, it is converted
        // to a 'bounding box' space coordinate by division by 100
        // Otherwise, standard unit conversion is used.
        LengthParser p = uctx.getParserFactory().createLengthParser();
        UnitProcessor.UnitResolver ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);

        // parse the length
        p.parse(new StringReader(lengthStr));
        if (ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
            length = ur.value / 100f;
        } else {
            length = UnitProcessor.svgToUserSpace(ur.unit,
                                                  ur.value,
                                                  svgElement,
                                                  UnitProcessor.OTHER_LENGTH,
                                                  uctx);
        }
        if (length < 0) {
            throw new IllegalArgumentException("A length must be positive");
        }
        return length;
    }

    /**
     * Creates a float value from the input value in the 'userSpaceOnUse'
     * coordinate system.
     *
     * @param svgElement the element that defines the specified length
     * @param lengthStr the length value
     * @param node the node that represents the element
     * @param uctx the context used to compute units and percentages
     */
    protected static
        float convertUserSpaceOnUseLength(SVGElement svgElement,
                                          String lengthStr,
                                          GraphicsNode node,
                                          UnitProcessor.Context uctx) {

        float length = UnitProcessor.svgToUserSpace(lengthStr,
                                                    svgElement,
                                                    UnitProcessor.OTHER_LENGTH,
                                                    uctx);
        if (length < 0) {
            throw new IllegalArgumentException("A length must be positive");
        }
        return length;
    }

    // ------------------------------------------------------------------------
    // AffineTransform computation
    // ------------------------------------------------------------------------

    /**
     * Creates an <tt>AffineTransform</tt> with the specified
     * additional transform, in the space of the specified graphics
     * node according to the 'units' coordinate system.
     *
     * @param at the additional transform
     * @param node the graphics node that defines the coordinate space
     * @param units the coordinate system
     */
    public static AffineTransform convertAffineTransform(AffineTransform at,
                                                         GraphicsNode node,
                                                         String units) {
        int unitsType = parseCoordinateSystem(units);
        AffineTransform Mx = new AffineTransform();
        switch(unitsType) {
        case OBJECT_BOUNDING_BOX:
            // Compute the transform to be in the GraphicsNode coordinate system
            Rectangle2D bounds = node.getGeometryBounds();
            Mx.translate(bounds.getX(), bounds.getY());
            Mx.scale(bounds.getWidth(), bounds.getHeight());
            break;
        case USER_SPACE_ON_USE:
            // Nothing to do
            break;
        default:
            /* Never happen: Bad coordinate system is catched previously */
            throw new Error();
        }
        Mx.concatenate(at);
        return Mx;
    }

    /**
     * Creates a <tt>Rectangle2D</tt> for the input filter
     * primitive element, processing the element as a node in
     * a filter chain.
     */
    public static Rectangle2D
        convertFilterPrimitiveRegion2(Element filterPrimitiveElement,
                                      Element filteredElement,
                                      Rectangle2D defaultRegion,
                                      GraphicsNode node,
                                      UnitProcessor.Context uctx){
        // Get unit. Comes from parent node.
        Node parentNode = filterPrimitiveElement.getParentNode();
        String units = "";
        if((parentNode != null)
           &&
           (parentNode.getNodeType() == parentNode.ELEMENT_NODE)){
            units = ((Element)parentNode).getAttributeNS(null, ATTR_PRIMITIVE_UNITS);
        }
        if(units.length() == 0){
            units = VALUE_USER_SPACE_ON_USE;
        }

        SVGElement svgElement = (SVGElement)filteredElement;

        // x, y, width and height will hold the filter
        // region size
        Double x=null, y=null, width=null, height=null;

        if(VALUE_OBJECT_BOUNDING_BOX.equals(units)){
            //
            // Values are in 'objectBoundingBox' units
            // Resolve each of x, y, widht and height values.
            // For each value, we distinguish two cases: percentages
            // and other. If a percentage value is used, it is converted
            // to a 'FilterRegion' space coordinate by division by 100
            // Otherwise, standard unit conversion is used.
            LengthParser p = uctx.getParserFactory().createLengthParser();
            UnitProcessor.UnitResolver ur = new UnitProcessor.UnitResolver();
            p.setLengthHandler(ur);

            // x  value
            String floatStr = filterPrimitiveElement.getAttributeNS(null, ATTR_X);
            if(floatStr.length() > 0){
                p.parse(new StringReader(floatStr));

                if(ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE){
                    x = new Double(ur.value / 100f);
                }
                else{
                    x = new Double(UnitProcessor.svgToUserSpace(ur.unit,
                                                               ur.value,
                                                               svgElement,
                                                               UnitProcessor.HORIZONTAL_LENGTH,
                                                               uctx));
                }
            }

            // y value
            floatStr = filterPrimitiveElement.getAttributeNS(null, ATTR_Y);
            if(floatStr.length() > 0){
                ur = new UnitProcessor.UnitResolver();
                p.setLengthHandler(ur);
                p.parse(new StringReader(floatStr));

                if(ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE){
                    y = new Double(ur.value / 100f);
                }
                else{
                    y = new Double(UnitProcessor.svgToUserSpace(ur.unit, ur.value,
                                                               svgElement,
                                                               UnitProcessor.VERTICAL_LENGTH,
                                                               uctx));
                }
            }

            // width  value
            floatStr = filterPrimitiveElement.getAttributeNS(null, ATTR_WIDTH);
            if(floatStr.length() > 0){
                ur = new UnitProcessor.UnitResolver();
                p.setLengthHandler(ur);
                p.parse(new StringReader(floatStr));

                if(ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE){
                    width = new Double(ur.value / 100f);
                }
                else{
                    width = new Double(UnitProcessor.svgToUserSpace(ur.unit, ur.value,
                                                                   svgElement,
                                                                   UnitProcessor.HORIZONTAL_LENGTH,
                                                                   uctx));
                }
            }

            // height value
            floatStr = filterPrimitiveElement.getAttributeNS(null, ATTR_HEIGHT);
            if(floatStr.length() > 0){
                ur = new UnitProcessor.UnitResolver();
                p.setLengthHandler(ur);
                p.parse(new StringReader(floatStr));

                if(ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE){
                    height = new Double(ur.value / 100f);
                }
                else{
                    height= new Double(UnitProcessor.svgToUserSpace(ur.unit, ur.value,
                                                                   svgElement,
                                                                   UnitProcessor.VERTICAL_LENGTH,
                                                                   uctx));
                }
            }

            Rectangle2D bounds = node.getBounds();
            if(x != null){
                x = new Double(bounds.getX() + x.doubleValue()*bounds.getWidth());
            }

            if(y != null){
                y = new Double(bounds.getY() + y.doubleValue()*bounds.getHeight());
            }

            if(width != null){
                width = new Double(bounds.getWidth()*width.doubleValue());
            }

            if(height != null){
                height = new Double(bounds.getHeight()*height.doubleValue());
            }
        }

        else{
            //
            // Values are in 'userSpaceOnUse'. Everything, including percentages,
            // can be resolved now (percentages refer to the viewPort
            //

            // Now, resolve each of the x, y, width and height values
            String floatStr = filterPrimitiveElement.getAttributeNS(null, ATTR_X);
            if(floatStr.length() > 0){
                x = new Double(UnitProcessor.svgToUserSpace(floatStr,
                                                           svgElement,
                                                           UnitProcessor.HORIZONTAL_LENGTH,
                                                           uctx));
            }

            floatStr = filterPrimitiveElement.getAttributeNS(null, ATTR_Y);
            if(floatStr.length() > 0){
                y = new Double(UnitProcessor.svgToUserSpace(floatStr,
                                                           svgElement,
                                                           UnitProcessor.VERTICAL_LENGTH,
                                                           uctx));
            }

            floatStr = filterPrimitiveElement.getAttributeNS(null, ATTR_WIDTH);
            if(floatStr.length() > 0){
                width = new Double(UnitProcessor.svgToUserSpace(floatStr,
                                                               svgElement,
                                                               UnitProcessor.HORIZONTAL_LENGTH,
                                                               uctx));
            }

            floatStr = filterPrimitiveElement.getAttributeNS(null, ATTR_HEIGHT);
            if(floatStr.length() > 0){
                height = new Double(UnitProcessor.svgToUserSpace(floatStr,
                                                                svgElement,
                                                                UnitProcessor.VERTICAL_LENGTH,
                                                                uctx));
            }
        }

        if(x == null){
            x = new Double(defaultRegion.getX());
        }
        if(y == null){
            y = new Double(defaultRegion.getY());
        }
        if(width == null){
            width = new Double(defaultRegion.getWidth());
        }
        if(height == null){
            height = new Double(defaultRegion.getHeight());
        }

        return new Rectangle2D.Double(x.doubleValue(),
                                      y.doubleValue(),
                                      width.doubleValue(),
                                      height.doubleValue());
    }

    /**
     * Parses a Float value pair. This assumes that the input attribute
     * value is of the form &lt;number&gt; [&lt;number&gt;]
     */
    public static Float[] buildFloatPair(String attrValue) {
        StringTokenizer st = new StringTokenizer(attrValue);
        Float pair[] = new Float[2];
        if(st.countTokens()>0){
            // Get first value
            String firstValue = st.nextToken();
            try{
                pair[0] = new Float(Float.parseFloat(firstValue));
            } catch(NumberFormatException e) {
                throw new Error(e.getMessage());
            }
            if((pair[0] != null) && (st.hasMoreTokens())){
                String secondValue = st.nextToken();
                try{
                    pair[1] = new Float(Float.parseFloat(secondValue));
                } catch(NumberFormatException e){
                    throw new Error(e.getMessage());
                }
            }
        }
        return pair;
    }
}

