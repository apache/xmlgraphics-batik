/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.apache.batik.parser.PreserveAspectRatioParser;
import org.apache.batik.parser.PreserveAspectRatioHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGPreserveAspectRatio;

/**
 * This class provides convenient methods to handle viewport.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class ViewBox implements SVGConstants, ErrorConstants {

    /**
     * No instance of this class is required.
     */
    protected ViewBox() { }

    /**
     * Parses a viewBox attribute.
     * @param value the viewBox
     * @return The 4 viewbox components or null.
     */
    public static float[] parseViewBoxAttribute(Element e, String value) {
        if (value.length() == 0) {
            return null;
        }
        int i = 0;
        float[] result = new float[4];
        StringTokenizer st = new StringTokenizer(value, " ,");
        try {
            while (i < 4 && st.hasMoreTokens()) {
                result[i] = Float.parseFloat(st.nextToken());
                i++;
            }
        } catch (NumberFormatException ex) {
            throw new BridgeException(e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                            new Object[] {SVG_VIEW_BOX_ATTRIBUTE, value, ex});
        }
        if (i != 4) {
            throw new BridgeException(e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                  new Object[] {SVG_VIEW_BOX_ATTRIBUTE, value});
        }
        return result;
    }

    /**
     * Returns the transformation matrix to apply to initalize a
     * viewport or null if the specified viewBox disables the
     * rendering of the element.
     *
     * @param e the element with a viewbox
     * @param w the width of the effective viewport
     * @param h The height of the effective viewport
     */
    public static AffineTransform getPreserveAspectRatioTransform(Element e,
                                                                  float w,
                                                                  float h) {
        String viewBox
            = e.getAttributeNS(null, SVG_VIEW_BOX_ATTRIBUTE);

        String aspectRatio
            = e.getAttributeNS(null, SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE);
        return getPreserveAspectRatioTransform(e, viewBox, aspectRatio, w, h);
    }

    /**
     * Returns the transformation matrix to apply to initalize a
     * viewport or null if the specified viewBox disables the
     * rendering of the element.
     *
     * @param e the element with a viewbox
     * @param viewBox the viewBox definition
     * @param w the width of the effective viewport
     * @param h The height of the effective viewport
     */
    public static
        AffineTransform getPreserveAspectRatioTransform(Element e,
                                                        String viewBox,
                                                        String aspectRatio,
                                                        float w,
                                                        float h) {

        // no viewBox specified
        if (viewBox.length() == 0) {
            return new AffineTransform();
        }

        String s;
        float[] vb = parseViewBoxAttribute(e, viewBox);
        // A negative value for <width> or <height> is an error
        if (vb[2] < 0 || vb[3] < 0) {
            throw new BridgeException(e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                new Object[] {SVG_VIEW_BOX_ATTRIBUTE, viewBox});
        }
        // A value of zero for width or height disables rendering of the element
        if (vb[2] == 0 || vb[3] == 0) {
            return null; // <!> FIXME : must disable !
        }

        AffineTransform result = new AffineTransform();
        // 'preserveAspectRatio' attribute
        PreserveAspectRatioParser p = new PreserveAspectRatioParser();
        PreserveAspectRatio ph = new PreserveAspectRatio();
        p.setPreserveAspectRatioHandler(ph);
        try {
            p.parse(new StringReader(aspectRatio));
        } catch (ParseException ex) {
            throw new BridgeException(e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object[] {SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE,
                                       aspectRatio, ex});
        }

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
        public boolean meet = true;

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
}
