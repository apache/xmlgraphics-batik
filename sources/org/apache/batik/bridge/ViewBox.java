/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.util.StringTokenizer;

import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.parser.FragmentIdentifierHandler;
import org.apache.batik.parser.FragmentIdentifierParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PreserveAspectRatioParser;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
     * Parses the specified reference (from a URI) and returns the appropriate
     * transform.
     *
     * @param ref the reference of the URI that may specify additional attribute
     *            values such as the viewBox, preserveAspectRatio or a transform
     * @param e the element interested in its view transform
     * @param w the width of the effective viewport
     * @param h The height of the effective viewport
     * @exception BridgeException if an error occured while computing the
     *            preserveAspectRatio transform
     */
    public static AffineTransform getViewTransform(String ref,
                                                   Element e,
                                                   float w,
                                                   float h) {

        // no reference has been specified, no extra viewBox is defined
        if (ref == null || ref.length() == 0) {
            return getPreserveAspectRatioTransform(e, w, h);
        }

        ViewHandler vh = new ViewHandler();
        FragmentIdentifierParser p = new FragmentIdentifierParser();
        p.setFragmentIdentifierHandler(vh);
        p.parse(ref);

        Element attrDefElement = e; // the element that defines the attributes
        if (vh.hasId) {
            Document document = e.getOwnerDocument();
            attrDefElement = document.getElementById(vh.id);
        }
        if (attrDefElement == null) {
            throw new BridgeException(e, ERR_URI_MALFORMED,
                                      new Object[] {ref});
        }
        // if the referenced element is not a view, the attribute
        // values to use are those defined on the enclosed svg element
        if (!(attrDefElement.getNamespaceURI().equals(SVG_NAMESPACE_URI)
              && attrDefElement.getLocalName().equals(SVG_VIEW_TAG))) {
            attrDefElement = getClosestAncestorSVGElement(e);
        }

        // 'viewBox'
        float [] vb;
        if (vh.hasViewBox) {
            vb = vh.viewBox;
        } else {
            String viewBoxStr = attrDefElement.getAttributeNS
                (null, SVG_VIEW_BOX_ATTRIBUTE);
            vb = parseViewBoxAttribute(attrDefElement, viewBoxStr);
        }

        // 'preserveAspectRatio'
        short align;
        boolean meet;
        if (vh.hasPreserveAspectRatio) {
            align = vh.align;
            meet = vh.meet;
        } else {
            String aspectRatio = attrDefElement.getAttributeNS
                (null, SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE);
            PreserveAspectRatioParser pp = new PreserveAspectRatioParser();
            ViewHandler ph = new ViewHandler();
            pp.setPreserveAspectRatioHandler(ph);
            try {
                pp.parse(aspectRatio);
            } catch (ParseException ex) {
                throw new BridgeException
                    (attrDefElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object[] {SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE,
                                       aspectRatio, ex});
            }
            align = ph.align;
            meet = ph.meet;
        }

        // the additional transform that may appear on the URI
        AffineTransform transform
            = getPreserveAspectRatioTransform(vb, align, meet, w, h);
        if (vh.hasTransform) {
            transform.concatenate(vh.getAffineTransform());
        }
        return transform;
    }

    /**
     * Returns the closest svg element ancestor of the specified element.
     *
     * @param e the element on which to start the svg element lookup
     */
    private static Element getClosestAncestorSVGElement(Element e) {
        for  (Node n = e;
              n != null && n.getNodeType() == Node.ELEMENT_NODE;
              n = n.getParentNode()) {
            Element tmp = (Element)n;
            if (tmp.getNamespaceURI().equals(SVG_NAMESPACE_URI)
                && tmp.getLocalName().equals(SVG_SVG_TAG)) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * Returns the transformation matrix to apply to initalize a viewport or
     * null if the specified viewBox disables the rendering of the element.
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
     * Returns the transformation matrix to apply to initalize a viewport or
     * null if the specified viewBox disables the rendering of the element.
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
        float[] vb = parseViewBoxAttribute(e, viewBox);

        // 'preserveAspectRatio' attribute
        PreserveAspectRatioParser p = new PreserveAspectRatioParser();
        ViewHandler ph = new ViewHandler();
        p.setPreserveAspectRatioHandler(ph);
        try {
            p.parse(aspectRatio);
        } catch (ParseException ex) {
            throw new BridgeException
                (e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE,
                                   aspectRatio, ex});
        }

        return getPreserveAspectRatioTransform(vb, ph.align, ph.meet, w, h);
    }

    /**
     * Returns the transformation matrix to apply to initalize a viewport or
     * null if the specified viewBox disables the rendering of the element.
     *
     * @param e the element with a viewbox
     * @param vb the viewBox definition as float
     * @param w the width of the effective viewport
     * @param h The height of the effective viewport 
     */
    public static
        AffineTransform getPreserveAspectRatioTransform(Element e,
							float [] vb,
							float w,
							float h) {

        String aspectRatio
            = e.getAttributeNS(null, SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE);

        // 'preserveAspectRatio' attribute
        PreserveAspectRatioParser p = new PreserveAspectRatioParser();
        ViewHandler ph = new ViewHandler();
        p.setPreserveAspectRatioHandler(ph);
        try {
            p.parse(aspectRatio);
        } catch (ParseException ex) {
            throw new BridgeException
                (e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE,
                                   aspectRatio, ex});
        }

        return getPreserveAspectRatioTransform(vb, ph.align, ph.meet, w, h);
    }

    /**
     * Parses a viewBox attribute.
     *
     * @param value the viewBox
     * @return The 4 viewbox components or null.
     */
    public static float[] parseViewBoxAttribute(Element e, String value) {
        if (value.length() == 0) {
            return null;
        }
        int i = 0;
        float[] vb = new float[4];
        StringTokenizer st = new StringTokenizer(value, " ,");
        try {
            while (i < 4 && st.hasMoreTokens()) {
                vb[i] = Float.parseFloat(st.nextToken());
                i++;
            }
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {SVG_VIEW_BOX_ATTRIBUTE, value, ex});
        }
        if (i != 4) {
            throw new BridgeException
                (e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {SVG_VIEW_BOX_ATTRIBUTE, value});
        }
        // A negative value for <width> or <height> is an error
        if (vb[2] < 0 || vb[3] < 0) {
            throw new BridgeException
                (e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {SVG_VIEW_BOX_ATTRIBUTE, value});
        }
        // A value of zero for width or height disables rendering of the element
        if (vb[2] == 0 || vb[3] == 0) {
            return null; // <!> FIXME : must disable !
        }
        return vb;
    }

    /**
     * Returns the preserveAspectRatio transform according to the specified
     * parameters.
     *
     * @param vb the viewBox definition
     * @param align the alignment definition
     * @param meet true means 'meet', false means 'slice'
     * @param w the width of the region in which the document has to fit into
     * @param h the height of the region in which the document has to fit into
     */
    public static
        AffineTransform getPreserveAspectRatioTransform(float [] vb,
                                                        short align,
                                                        boolean meet,
                                                        float w,
                                                        float h) {
        if (vb == null) {
            return new AffineTransform();
        }

        AffineTransform result = new AffineTransform();
        float vpar  = vb[2] / vb[3];
        float svgar = w / h;

        if (align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_NONE) {
            result.scale(w / vb[2], h / vb[3]);
            result.translate(-vb[0], -vb[1]);
        } else if (vpar < svgar && meet || vpar >= svgar && !meet) {
            float sf = h / vb[3];
            result.scale(sf, sf);
            switch (align) {
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
            switch (align) {
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
     * This class can be used to store the value of the attribute viewBox or can
     * also be used to store the various attribute value that can be specified
     * on a SVG URI fragments.
     */
    protected static class ViewHandler extends AWTTransformProducer
        implements FragmentIdentifierHandler {


        /**
         * Constructs a new <tt>ViewHandler</tt> instance.
         */
        protected ViewHandler() { }

        //////////////////////////////////////////////////////////////////////
        // TransformListHandler
        //////////////////////////////////////////////////////////////////////

        public boolean hasTransform;

        public void endTransformList() throws ParseException {
            super.endTransformList();
            hasTransform = true;
        }

        //////////////////////////////////////////////////////////////////////
        // FragmentIdentifierHandler
        //////////////////////////////////////////////////////////////////////

        public boolean hasId;
        public boolean hasViewBox;
        public boolean hasViewTargetParams;
        public boolean hasZoomAndPanParams;

        public String id;
        public float [] viewBox;
        public String viewTargetParams;
        public boolean isMagnify;

        /**
         * Invoked when the fragment identifier starts.
         * @exception ParseException if an error occured while processing the
         *                           fragment identifier
         */
        public void startFragmentIdentifier() throws ParseException { }

        /**
         * Invoked when an ID has been parsed.
         * @param s The string that represents the parsed ID.
         * @exception ParseException if an error occured while processing the
         *                           fragment identifier
         */
        public void idReference(String s) throws ParseException {
            id = s;
            hasId = true;
        }

        /**
         * Invoked when 'viewBox(x,y,width,height)' has been parsed.
         * @param x&nbsp;y&nbsp;width&nbsp;height the coordinates of the
         * viewbox.
         * @exception ParseException if an error occured while processing the
         *                           fragment identifier
         */
        public void viewBox(float x, float y, float width, float height)
            throws ParseException {

            hasViewBox = true;
            viewBox = new float[4];
            viewBox[0] = x;
            viewBox[1] = y;
            viewBox[2] = width;
            viewBox[3] = height;
        }

        /**
         * Invoked when a view target specification starts.
         * @exception ParseException if an error occured while processing the
         *                           fragment identifier
         */
        public void startViewTarget() throws ParseException { }

        /**
         * Invoked when a identifier has been parsed within a view target
         * specification.
         * @param name the target name.
         * @exception ParseException if an error occured while processing the
         *                           fragment identifier
         */
        public void viewTarget(String name) throws ParseException {
            viewTargetParams = name;
            hasViewTargetParams = true;
        }

        /**
         * Invoked when a view target specification ends.
         * @exception ParseException if an error occured while processing the
         *                           fragment identifier
         */
        public void endViewTarget() throws ParseException { }

        /**
         * Invoked when a 'zoomAndPan' specification has been parsed.
         * @param magnify true if 'magnify' has been parsed.
         * @exception ParseException if an error occured while processing the
         *                           fragment identifier
         */
        public void zoomAndPan(boolean magnify) {
            isMagnify = magnify;
            hasZoomAndPanParams = true;
        }

        /**
         * Invoked when the fragment identifier ends.
         * @exception ParseException if an error occured while processing the
         *                           fragment identifier
         */
        public void endFragmentIdentifier() throws ParseException { }

        //////////////////////////////////////////////////////////////////////
        // PreserveAspectRatioHandler
        //////////////////////////////////////////////////////////////////////

        public boolean hasPreserveAspectRatio;

        public short align;
        public boolean meet = true;

        /**
         * Invoked when the PreserveAspectRatio parsing starts.
         * @exception ParseException if an error occured while processing
         * the transform
         */
        public void startPreserveAspectRatio() throws ParseException { }

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
            hasPreserveAspectRatio = true;
        }
    }
}
