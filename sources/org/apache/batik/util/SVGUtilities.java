/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.awt.geom.AffineTransform;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.ParserFactory;
import org.apache.batik.parser.PreserveAspectRatioHandler;
import org.apache.batik.parser.PreserveAspectRatioParser;
import org.apache.batik.refimpl.gvt.filter.ConcreteFilterRegion;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLangSpace;
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
     * Parses the given 'in' attribute value.
     * @return one of BACKGROUND_ALPHA, BACKGROUND_IMAGE, FILL_PAINT,
     *         SOURCE_ALPHA, SOURCE_GRAPHIC, STROKE_PAINT, IDENTIFIER or
     *         EMPTY.
     */
    public int parseInAttribute(String value) {
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
                        return SOURCE_ALPHA;
                    }
                    return IDENTIFIER;
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
                        return SOURCE_GRAPHIC;
                    }
                    return IDENTIFIER;
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
        if (n.getNodeType() == Node.ELEMENT_NODE) {
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
     * Creates a filter region from a filter element's x, y, width
     * and height attributes. Uses the input <tt>GraphicsNode</tt>
     * as a default for the region bounds
     */
    public static FilterRegion buildFilterRegion(Element filterElement,
                                                 GraphicsNode node){
        // Extract each of the rectangle parameters
        Float x = buildFloat(filterElement,
                                      null,
                                      ATTR_X);
        Float y = buildFloat(filterElement,
                                      null,
                                      ATTR_Y);
        Float width = buildFloat(filterElement,
                                          null,
                                          ATTR_WIDTH);
        Float height = buildFloat(filterElement,
                                           null,
                                           ATTR_HEIGHT);

 
        return new ConcreteFilterRegion(node, x, y, width, height);
    }
    
    /**
     * Returns a Float object corresponding to the input
     * attribute name. A null value is returned if the attribute
     * could not be parsed.
     */
    public static Float buildFloat(Element element,
                                   String uri,
                                   String attrName){
        Float value = null;
        String attrValue = element.getAttributeNS(uri, attrName);
        if(attrValue != null){
            try{
                value = new Float(Float.parseFloat(attrValue));
            }catch(NumberFormatException e){
            }
        }

        System.out.println("Value for : " + uri + "/" + attrName + " : " +
                           value);
        return value;
    }

    /**
     * Parses a Float value pair. This assumes that the input attribute
     * value is of the form <number>, [<number>]
     */
    public static Float[] buildFloatPair(Element element, String uri,
                                         String attrName) {
        String attrValue = element.getAttributeNS(uri, attrName);
        System.out.println("stdDeviation : " + attrValue);
        StringTokenizer st = new StringTokenizer(attrValue);
        Float pair[] = new Float[2];
        if(st.countTokens()>0){
            // Get first value
            String firstValue = st.nextToken();
            try{
                pair[0] = new Float(Float.parseFloat(firstValue));
            }catch(NumberFormatException e){
            }
            
            if((pair[0] != null) && (st.hasMoreTokens())){
                String secondValue = st.nextToken();
                try{
                    pair[1] = new Float(Float.parseFloat(secondValue));
                }catch(NumberFormatException e){}
            }
        }

        System.out.println("pair : " + pair[0] + " / " + pair[1]);
        return pair;
    }
}
