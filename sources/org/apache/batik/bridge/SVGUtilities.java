/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PreserveAspectRatioHandler;
import org.apache.batik.parser.PreserveAspectRatioParser;
import org.apache.batik.parser.PreserveAspectRatioParser;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGPreserveAspectRatio;
import org.xml.sax.SAXException;

/**
 * This class contains utility methods for SVG.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
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

    // region coordinate system //////////////////////////////////////////

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
     *
     * @pram value the units type to parse
     * @exception IllegalArgumentException if the value is not
     * 'objectBoundingBox' or 'userSpaceOnUse'
     */
    public static int parseCoordinateSystem(String value) {
        int len = value.length();
        // INTERNAL : check for correct argument - should never happen
        if (len == 0) {
            throw new Error("Can't accept empty coordinate system");
        }
        if (SVG_USER_SPACE_ON_USE_VALUE.equals(value)) {
            return USER_SPACE_ON_USE;
        } else if (SVG_OBJECT_BOUNDING_BOX_VALUE.equals(value)) {
            return OBJECT_BOUNDING_BOX;
        } else {
            throw new IllegalArgumentException();
        }
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
             float         h) {
        AffineTransform result = new AffineTransform();
        String vba = elt.getAttributeNS(null, ATTR_VIEW_BOX);
        float[] vb = parseViewBoxAttribute(vba);

        if (vb == null ||
            vb[2] == 0 ||
            vb[3] == 0) {
            return result;
        }

        PreserveAspectRatioParser p = new PreserveAspectRatioParser();
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
            if (name.equals(SVG_DESC_TAG)) {
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
        if (elt.hasAttributeNS(null, SVG_SYSTEM_LANGUAGE_ATTRIBUTE)) {
            // Evaluates the system languages
            String sl = elt.getAttributeNS(null, SVG_SYSTEM_LANGUAGE_ATTRIBUTE);
            StringTokenizer st = new StringTokenizer(sl, ",");
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (matchUserLanguage(s, ua.getLanguages())) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }

        // !!! TODO requiredFeatures, requiredExtensions
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
     * Parses an SVG integer.
     *
     * @param attrName the attribute that has the specified value
     * @param numStr the integer value to parse
     * @exception IllegalAttributeValueException if intStr is not a parsable
     *                                           integer
     */
    public static int convertSVGInteger(String attrName, String intStr){
        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("integer.invalid",
                                       new Object[] {intStr, attrName}));
        }
    }

    /**
     * Parses an SVG number
     *
     * @param attrName the attribute that has the specified value
     * @param numStr the float value to parse
     * @exception IllegalAttributeValueException if intStr is not a parsable
     *                                           float
     */
    public static float convertSVGNumber(String attrName, String numStr){
        try {
            return Float.parseFloat(numStr);
        } catch (NumberFormatException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("float.invalid",
                                       new Object[] {numStr, attrName}));

        }
    }

    // ------------------------------------------------------------------------
    // Convenient methods for gradients, pattern and filter for xlink:href
    // ------------------------------------------------------------------------

    /**
     * Returns the value of the attribute if defined one of the
     * element or one of its ancestor (using xlink:href).
     *
     * @param element the element
     * @param namespaceURI the namespace URI of the attribute to return
     * @param attrName the name of the attribute to search
     */
    public static String getChainableAttributeNS(Element element,
                                                 String namespaceURI,
                                                 String attrName,
                                                 DocumentLoader loader) {
        Element e = element;
        List refs = new LinkedList();
        for (;;) {
            String v = e.getAttributeNS(namespaceURI, attrName);
            if (v.length() > 0) { // exit if attribute defined
                return v;
            }
            String uriStr = XLinkSupport.getXLinkHref(e);
            if (uriStr.length() == 0) { // exit if no more xlink:href
                return "";
            }
            SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
            URL baseURL = ((SVGOMDocument)svgDoc).getURLObject();
            try {
                URL url = new URL(baseURL, uriStr);
                Iterator iter = refs.iterator();
                while (iter.hasNext()) {
                    URL urlTmp = (URL)iter.next();
                    if (urlTmp.sameFile(url) &&
                            urlTmp.getRef().equals(url.getRef())) {
                        throw new IllegalAttributeValueException(
                            "circular reference on "+e);
                    }
                }
                URIResolver resolver = new URIResolver(svgDoc, loader);
                e = resolver.getElement(url.toString());
                refs.add(url);
            } catch(MalformedURLException ex) {
                throw new IllegalAttributeValueException("bad url on "+uriStr);
            } catch(IOException ex) {
                throw new IllegalAttributeValueException("I/O error on "+uriStr);
            }
        }
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
                                             GraphicsNodeRenderContext rc,
                                             UnitProcessor.Context uctx,
                                             DocumentLoader loader) {
        String xStr = getChainableAttributeNS(filterElement,
                                              null,
                                              SVG_X_ATTRIBUTE,
                                              loader);
        String yStr = getChainableAttributeNS(filterElement,
                                              null,
                                              SVG_Y_ATTRIBUTE,
                                              loader);
        String wStr = getChainableAttributeNS(filterElement,
                                              null,
                                              SVG_WIDTH_ATTRIBUTE,
                                              loader);
        String hStr = getChainableAttributeNS(filterElement,
                                              null,
                                              SVG_HEIGHT_ATTRIBUTE,
                                              loader);
        String unitsStr = getChainableAttributeNS(filterElement,
                                                  null,
                                                  SVG_FILTER_UNITS_ATTRIBUTE,
                                                  loader);
        return convertRegion(filterElement,
                             filteredElement,
                             node,
                             rc,
                             uctx,
                             unitsStr,
                             SVG_OBJECT_BOUNDING_BOX_VALUE,
                             xStr,
                             DEFAULT_VALUE_FILTER_X,
                             yStr,
                             DEFAULT_VALUE_FILTER_Y,
                             wStr,
                             DEFAULT_VALUE_FILTER_WIDTH,
                             hStr,
                             DEFAULT_VALUE_FILTER_HEIGHT);
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
                                      GraphicsNodeRenderContext rc,
                                      UnitProcessor.Context uctx) {

        String xStr = maskElement.getAttributeNS(null, SVG_X_ATTRIBUTE);
        String yStr = maskElement.getAttributeNS(null, SVG_Y_ATTRIBUTE);
        String wStr = maskElement.getAttributeNS(null, SVG_WIDTH_ATTRIBUTE);
        String hStr = maskElement.getAttributeNS(null, SVG_HEIGHT_ATTRIBUTE);
        String unitsStr = maskElement.getAttributeNS(null, SVG_MASK_UNITS_ATTRIBUTE);
        return convertRegion(maskElement,
                             maskedElement,
                             node,
                             rc,
                             uctx,
                             unitsStr,
                             SVG_OBJECT_BOUNDING_BOX_VALUE,
                             xStr,
                             DEFAULT_VALUE_FILTER_X,
                             yStr,
                             DEFAULT_VALUE_FILTER_Y,
                             wStr,
                             DEFAULT_VALUE_FILTER_WIDTH,
                             hStr,
                             DEFAULT_VALUE_FILTER_HEIGHT);
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
                                         GraphicsNodeRenderContext rc,
                                         UnitProcessor.Context uctx,
                                         DocumentLoader loader) {

        String xStr = getChainableAttributeNS(patternElement,
                                              null,
                                              SVG_X_ATTRIBUTE,
                                              loader);
        String yStr = getChainableAttributeNS(patternElement,
                                              null,
                                              SVG_Y_ATTRIBUTE,
                                              loader);
        String wStr = getChainableAttributeNS(patternElement,
                                              null,
                                              SVG_WIDTH_ATTRIBUTE,
                                              loader);
        String hStr = getChainableAttributeNS(patternElement,
                                              null,
                                              SVG_HEIGHT_ATTRIBUTE,
                                              loader);
        String unitsStr = getChainableAttributeNS(patternElement,
                                                  null,
                                                  ATTR_PATTERN_UNITS,
                                                  loader);
        return convertRegion(patternElement,
                             paintedElement,
                             node,
                             rc,
                             uctx,
                             unitsStr,
                             SVG_OBJECT_BOUNDING_BOX_VALUE,
                             xStr,
                             DEFAULT_VALUE_FILTER_X,
                             yStr,
                             DEFAULT_VALUE_FILTER_Y,
                             wStr,
                             DEFAULT_VALUE_FILTER_WIDTH,
                             hStr,
                             DEFAULT_VALUE_FILTER_HEIGHT);
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
                                               GraphicsNodeRenderContext rc,
                                               UnitProcessor.Context uctx,
                                               String units,
                                               String unitsDefault,
                                               String xStr,
                                               String xDefault,
                                               String yStr,
                                               String yDefault,
                                               String wStr,
                                               String wDefault,
                                               String hStr,
                                               String hDefault) {
        // handle default or required filter region attributes
        if (units.length() == 0) {
            units = unitsDefault;
        }
        // the x attribute
        if (xStr.length() == 0) {
            if (xDefault == null) {
                throw new MissingAttributeException(
                    Messages.formatMessage("region.x.required",
                        new Object[] {filterElement.getLocalName()}));
            } else {
                xStr = xDefault;
            }
        }
        // the y attribute
        if (yStr.length() == 0) {
            if (yDefault == null) {
                throw new MissingAttributeException(
                    Messages.formatMessage("region.y.required",
                        new Object[] {filterElement.getLocalName()}));
            } else {
                yStr = yDefault;
            }
        }
        // the width attribute
        if (wStr.length() == 0) {
            if (wDefault == null) {
                throw new MissingAttributeException(
                    Messages.formatMessage("region.width.required",
                        new Object[] {filterElement.getLocalName()}));
            } else {
                wStr = wDefault;
            }
        }
        // the height attribute
        if (hStr.length() == 0) {
            if (hDefault == null) {
                throw new MissingAttributeException(
                    Messages.formatMessage("region.height.required",
                        new Object[] {filterElement.getLocalName()}));
            } else {
                hStr = hDefault;
            }
        }


        int unitsType;
        try {
            unitsType = parseCoordinateSystem(units);
        } catch (IllegalArgumentException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("region.units.invalid",
                                  new Object[] {units,
                                                filterElement.getLocalName()}));
        }
        double x, y, w, h;
        short hd = UnitProcessor.HORIZONTAL_LENGTH;
        short vd = UnitProcessor.VERTICAL_LENGTH;

        switch(unitsType) {
        case OBJECT_BOUNDING_BOX:
            x = svgToObjectBoundingBox(filteredElement,
                                       SVG_X_ATTRIBUTE, xStr,
                                       uctx, hd);
            y = svgToObjectBoundingBox(filteredElement,
                                       SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE, yStr,
                                       uctx, vd);
            w = svgToObjectBoundingBox(filteredElement,
                                       SVG_WIDTH_ATTRIBUTE, wStr,
                                       uctx, hd);
            h = svgToObjectBoundingBox(filteredElement,
                                       SVG_HEIGHT_ATTRIBUTE, hStr,
                                       uctx, vd);
            // Now, take the bounds of the GraphicsNode into account
            Rectangle2D gnBounds = node.getGeometryBounds(rc);
            if(gnBounds != null){
                x = gnBounds.getX() + x*gnBounds.getWidth();
                y = gnBounds.getY() + y*gnBounds.getHeight();
                w *= gnBounds.getWidth();
                h *= gnBounds.getHeight();
            }
            else{
                x = 0; 
                y = 0; 
                w = 0; 
                h = 0;
            }
            break;
        case USER_SPACE_ON_USE:
            x = svgToUserSpaceOnUse(filteredElement,
                                    SVG_X_ATTRIBUTE, xStr,
                                    uctx, hd);
            y = svgToUserSpaceOnUse(filteredElement,
                                    SVG_Y_ATTRIBUTE, yStr,
                                    uctx, vd);
            w = svgToUserSpaceOnUse(filteredElement,
                                    SVG_WIDTH_ATTRIBUTE, wStr,
                                    uctx, hd);
            h = svgToUserSpaceOnUse(filteredElement,
                                    SVG_HEIGHT_ATTRIBUTE, hStr,
                                    uctx, vd);
            break;
        default:
            /* Never happen: Bad coordinate system is catched previously */
            throw new Error();
        }

        if (w < 0) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("region.width.illegal",
                    new Object[] {filterElement.getLocalName()}));
        }
        if (h < 0) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("region.height.illegal",
                    new Object[] {filterElement.getLocalName()}));
        }

        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Creates a <tt>Rectangle2D</tt> for the input filter primitive element.
     * Processing the element as the top one in the filter chain.
     *
     * @param filterPrimitiveElement the primitive filter
     * @param filteredElement the element which uses the filter
     * @param defaultRegion the default region to filter
     * @param filterRegion the filter chain region
     * @param node the graphics node that represents the element to filter
     * @param uctx the context used to compute units and percentages
     */
    public static
        Rectangle2D convertFilterPrimitiveRegion(Element filterPrimitiveElement,
                                                 Element filteredElement,
                                                 Rectangle2D defaultRegion,
                                                 Rectangle2D filterRegion,
                                                 GraphicsNode node,
                                                 GraphicsNodeRenderContext rc,
                                                 UnitProcessor.Context uctx,
                                                 DocumentLoader loader) {

        // Get coordinate system from the parent node.
        Node parentNode = filterPrimitiveElement.getParentNode();
        String units = "";
        if((parentNode != null) &&
               (parentNode.getNodeType() == parentNode.ELEMENT_NODE)) {
            Element parent = (Element) parentNode;
            units = getChainableAttributeNS(parent,
                                            null,
                                            SVG_PRIMITIVE_UNITS_ATTRIBUTE,
                                            loader);
        }
        if(units.length() == 0){
            units = SVG_USER_SPACE_ON_USE_VALUE;
        }

        String xStr = filterPrimitiveElement.getAttributeNS(null, SVG_X_ATTRIBUTE);
        String yStr = filterPrimitiveElement.getAttributeNS(null, SVG_Y_ATTRIBUTE);
        String wStr = filterPrimitiveElement.getAttributeNS(null, SVG_WIDTH_ATTRIBUTE);
        String hStr = filterPrimitiveElement.getAttributeNS(null, SVG_HEIGHT_ATTRIBUTE);

        int unitsType;
        try {
            unitsType = parseCoordinateSystem(units);
        } catch (IllegalArgumentException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("region.units.invalid",
                                  new Object[] {units,
                                                SVG_PRIMITIVE_UNITS_ATTRIBUTE,
                                       filterPrimitiveElement.getLocalName()}));
        }

        double x = defaultRegion.getX();
        double y = defaultRegion.getY();
        double w = defaultRegion.getWidth();
        double h = defaultRegion.getHeight();
        short hd = UnitProcessor.HORIZONTAL_LENGTH;
        short vd = UnitProcessor.VERTICAL_LENGTH;

        switch(unitsType) {
        case OBJECT_BOUNDING_BOX:
            Rectangle2D gnBounds = node.getGeometryBounds(rc);
            if (xStr.length() != 0) {
                x = svgToObjectBoundingBox(filteredElement,
                                           SVG_X_ATTRIBUTE, xStr,
                                           uctx, hd);
                x = gnBounds.getX() + x*gnBounds.getWidth();
            }
            if (yStr.length() != 0) {
                y = svgToObjectBoundingBox(filteredElement,
                                           SVG_Y_ATTRIBUTE, yStr,
                                           uctx, vd);
                y = gnBounds.getY() + y*gnBounds.getHeight();
            }
            if (wStr.length() != 0) {
                w = svgToObjectBoundingBox(filteredElement,
                                           SVG_WIDTH_ATTRIBUTE, wStr,
                                           uctx, hd);
                w *= gnBounds.getWidth();
            }
            if (hStr.length() != 0) {
                h = svgToObjectBoundingBox(filteredElement,
                                           SVG_HEIGHT_ATTRIBUTE, hStr,
                                           uctx, vd);
                h *= gnBounds.getHeight();
            }
            break;
        case USER_SPACE_ON_USE:
            if (xStr.length() != 0) {
                x = svgToUserSpaceOnUse(filteredElement,
                                        SVG_X_ATTRIBUTE, xStr,
                                        uctx, hd);
            }
            if (yStr.length() != 0) {
                y = svgToUserSpaceOnUse(filteredElement,
                                        SVG_Y_ATTRIBUTE, yStr,
                                        uctx, vd);
            }
            if (wStr.length() != 0) {
                w = svgToUserSpaceOnUse(filteredElement,
                                        SVG_WIDTH_ATTRIBUTE, wStr,
                                        uctx, hd);
            }
            if (hStr.length() != 0) {
                h = svgToUserSpaceOnUse(filteredElement,
                                        SVG_HEIGHT_ATTRIBUTE, hStr,
                                        uctx, vd);
            }
            break;
        default:
            /* Never happen: Bad coordinate system is catched previously */
            throw new Error();
        }

        if (w < 0) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("region.width.illegal",
                    new Object[] {filterPrimitiveElement.getLocalName()}));
        }
        if (h < 0) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("region.height.illegal",
                    new Object[] {filterPrimitiveElement.getLocalName()}));
        }

        Rectangle2D region = new Rectangle2D.Double(x, y, w, h);
        region.intersect(region, filterRegion, region);
        return region;
    }

    // ------------------------------------------------------------------------
    // Coordinate computation for <linearGradient> and <radialGradient>
    //
    // WARNING: We need some special methods for gradients to convert
    // coordinates or length. As our gradients are implemented as
    // java.awt.Paint They are already in user space, so we don't need
    // to take care of the bounds of the painted element
    // ------------------------------------------------------------------------

    /**
     * Creates a <tt>Point2D</tt> for the input x and y attribute value in the
     * 'units' coordinate system.
     *
     * @param element the element that defines the specified coordinates
     * @param attrXName the name of the X attribute (used by error handling)
     * @param xStr the 'x' coordinate
     * @param attrYName the name of the Y attribute (used by error handling)
     * @param yStr the 'y' coordinate
     * @param unitsType the coordinate system
     * @param uctx the context used to compute units and percentages
     * @exception IllegalAttributeValueException if one of the specified is
     *                                           not valid
     */
    public static Point2D convertGradientPoint(Element element,
                                               String attrXName,
                                               String xStr,
                                               String attrYName,
                                               String yStr,
                                               int unitsType,
                                               UnitProcessor.Context uctx) {

        float x, y;
        short hd = UnitProcessor.HORIZONTAL_LENGTH;
        short vd = UnitProcessor.VERTICAL_LENGTH;

        switch(unitsType) {
        case OBJECT_BOUNDING_BOX:
            x = svgToObjectBoundingBox(element, attrXName, xStr, uctx, hd);
            y = svgToObjectBoundingBox(element, attrYName, yStr, uctx, vd);
            break;
        case USER_SPACE_ON_USE:
            x = svgToUserSpaceOnUse(element, attrXName, xStr, uctx, hd);
            y = svgToUserSpaceOnUse(element, attrYName, yStr, uctx, vd);
            break;
        default:
            /* Never happen: Bad coordinate system is catched previously */
            throw new Error();
        }
        return new Point2D.Float(x, y);
    }

    // ------------------------------------------------------------------------
    // Length computation for <radialGradient>
    // ------------------------------------------------------------------------

    /**
     * Creates a float value from the input value in the 'units'
     * coordinate system.
     *
     * @param element the element that defines the specified length
     * @param attrLengthName the name of the attribute (used by error handling)
     * @param lengthStr the length value
     * @param unitsType the coordinate system
     * @param uctx the context used to compute units and percentages
     * @exception IllegalAttributeValueException if the value is not a valid
     */
    public static float convertGradientLength(Element element,
                                              String attrLengthName,
                                              String lengthStr,
                                              int unitsType,
                                              UnitProcessor.Context uctx) {

        float length;
        short d = UnitProcessor.OTHER_LENGTH;
        switch(unitsType) {
        case OBJECT_BOUNDING_BOX:
            length = svgToObjectBoundingBox(element,
                                            attrLengthName, lengthStr,
                                            uctx, d);
            break;
        case USER_SPACE_ON_USE:
            length = svgToUserSpaceOnUse(element,
                                         attrLengthName, lengthStr,
                                         uctx, d);
            break;
        default:
            /* Never happen: Bad coordinate system is catched previously */
            throw new Error();
        }
        if (length < 0) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("length.illegal",
                                       new Object[] {attrLengthName}));
        }
        return length;
    }

    // ------------------------------------------------------------------------
    // AffineTransform computation
    // ------------------------------------------------------------------------

    /**
     * Creates an <tt>AffineTransform</tt> using the element and its specified
     * attribute.
     *
     * @param the e that defines a transform
     * @param the attribute that defines the transform
     * @param pf the parser factory to use
     */
    public static AffineTransform convertAffineTransform(String transformStr) {
        try {
            StringReader r = new StringReader(transformStr);
            return AWTTransformProducer.createAffineTransform(r);
        } catch (ParseException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("transform.invalid",
                                       new Object[] {transformStr,
                                                     ex.getMessage()}));
        }
    }

    /**
     * Creates an <tt>AffineTransform</tt> with the specified
     * additional transform, in the space of the specified graphics
     * node according to the 'units' coordinate system.
     *
     * @param at the additional transform
     * @param node the graphics node that defines the coordinate space
     * @param unitsType the coordinate system
     */
    public static AffineTransform convertAffineTransform(AffineTransform at,
                                                 GraphicsNode node,
                                                 GraphicsNodeRenderContext rc,
                                                 int unitsType) {
        AffineTransform Mx = new AffineTransform();
        switch(unitsType) {
        case OBJECT_BOUNDING_BOX:
            // Compute the transform to be in the GraphicsNode coordinate system
            Rectangle2D bounds = node.getGeometryBounds(rc);
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

    // ------------------------------------------------------------------------
    // Float pair builder
    // ------------------------------------------------------------------------

    /**
     * Parses a Float value pair. This assumes that the input attribute
     * value is of the form &lt;number&gt; [&lt;number&gt;]
     */
    public static Float[] buildFloatPair(String attrValue) {
        StringTokenizer st = new StringTokenizer(attrValue);
        Float pair[] = new Float[2];
        if(st.countTokens() > 0) {
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

    // ------------------------------------------------------------------------
    // SVGLength computation
    // ------------------------------------------------------------------------

    /**
     * Returns the float that represents a specified value or percentage.
     *
     * @param element the element that defines the specified coordinates
     * @param attrName the name of the attribute (used by error handling)
     * @param valueStr the value of the coordinate
     * @param uctx the context used to compute units and percentages
     * @param direction HORIZONTAL_LENGTH | VERTICAL_LENGTH | OTHER_LENGTH
     * @exception IllegalAttributeValueException if the value is not a valid
     */
    public static float svgToUserSpace(Element element,
                                       String attrName,
                                       String valueStr,
                                       UnitProcessor.Context uctx,
                                       short direction) {

        // INTERNAL : check for correct arguments - should never happen
        if (valueStr == null || valueStr.length() == 0) {
            throw new Error("The value is null or empty");
        }

        LengthParser p = new LengthParser();
        UnitProcessor.UnitResolver ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);
        try {
            p.parse(new StringReader(valueStr));
        } catch (ParseException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("length.invalid",
                                       new Object[] {valueStr, attrName}));
        }
        return UnitProcessor.svgToUserSpace(ur.unit,
                                            ur.value,
                                            (SVGElement)element,
                                            direction,
                                            uctx);
    }

    /**
     * Returns the float array that represents a set of values or percentage.
     *
     * @param element the element that defines the specified coordinates
     * @param attrName the name of the attribute (used by error handling)
     * @param valueStr the delimited string containing values of the coordinate
     * @param uctx the context used to compute units and percentages
     * @param direction HORIZONTAL_LENGTH | VERTICAL_LENGTH | OTHER_LENGTH
     * @exception IllegalAttributeValueException if the value is not a valid
     */
    public static float[] svgToUserSpaceArray(Element element,
                                       String attrName,
                                       String valueStr,
                                       UnitProcessor.Context uctx,
                                       short direction) {

        // INTERNAL : check for correct arguments - should never happen
        if (valueStr == null || valueStr.length() == 0) {
            throw new Error("The value is null or empty");
        }

        LengthParser p = new LengthParser();
        UnitProcessor.UnitResolver ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);
        ArrayList values = new ArrayList();
        StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        int c = 0; // must count, can't rely in ArrayList.size()
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            try {
                p.parse(new StringReader(s));
                ++c;
            } catch (ParseException ex) {
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("length.invalid",
                                       new Object[] {s, attrName}));
            }
            values.add(new Float(UnitProcessor.svgToUserSpace(ur.unit,
                                            ur.value,
                                            (SVGElement)element,
                                            direction,
                                            uctx)));
        }
        float[] floats = new float[c];
        for (int i=0; i<c; ++i) {
            floats[i] = ((Float) values.get(i)).floatValue();
        }
        return floats;
    }

    /**
     * Returns the float that represents a specified value or
     * percentage. This method is used when the coordinate system is
     * 'objectBoundingBox' but does <b>not</b> transform the value to match
     * the bounding box of the object.
     *
     * @param element the element that defines the specified coordinates
     * @param attrName the name of the attribute (used by error handling)
     * @param valueStr the value of the coordinate
     * @param uctx the context used to compute units and percentages
     * @param direction HORIZONTAL_LENGTH | VERTICAL_LENGTH | OTHER_LENGTH
     * @exception IllegalAttributeValueException if the value is not a valid
     */
    protected static float svgToObjectBoundingBox(Element element,
                                                  String attrName,
                                                  String valueStr,
                                                  UnitProcessor.Context uctx,
                                                  short direction) {

        // INTERNAL : check for correct arguments - should never happen
        if (valueStr == null || valueStr.length() == 0) {
            throw new Error("The value is null or empty");
        }

        //
        // We distinguish two cases:
        //
        // a. Percentages : If a percentage value is used, it is converted
        // to a 'bounding box' space coordinate by division by 100
        //
        // b. Otherwise, the value is used as is
        //
        LengthParser p = new LengthParser();
        UnitProcessor.UnitResolver ur = new UnitProcessor.UnitResolver();
        p.setLengthHandler(ur);
        try {
            p.parse(new StringReader(valueStr));
        } catch (ParseException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("length.invalid",
                                       new Object[] {valueStr, attrName}));
        }
        float value = ur.value;
        if (ur.unit == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
            value /= 100f;
        }
        return value;
    }

    /**
     * Returns the float that represents a specified value or percentage.
     *
     * @param element the element that defines the specified coordinates
     * @param attrName the name of the attribute (used by error handling)
     * @param valueStr the value of the coordinate
     * @param uctx the context used to compute units and percentages
     * @param direction HORIZONTAL_LENGTH | VERTICAL_LENGTH | OTHER_LENGTH
     * @exception IllegalAttributeValueException if the value is not a valid
     */
    protected static float svgToUserSpaceOnUse(Element element,
                                               String attrName,
                                               String valueStr,
                                               UnitProcessor.Context uctx,
                                               short direction) {

        return svgToUserSpace(element, attrName, valueStr, uctx, direction);
    }
}
