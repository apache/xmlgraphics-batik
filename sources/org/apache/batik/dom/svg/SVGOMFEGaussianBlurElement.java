/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;
import java.util.StringTokenizer;

import org.apache.batik.dom.AbstractDocument;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEGaussianBlurElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEGaussianBlurElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEGaussianBlurElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEGaussianBlurElement {

    /**
     * The reference to the in attribute.
     */
    protected transient WeakReference inReference;

    /**
     * The reference to the stdDeviation x attribute.
     */
    protected transient WeakReference stdDeviationXReference;

    /**
     * The reference to the stdDeviation y attribute.
     */
    protected transient WeakReference stdDeviationYReference;

    /**
     * Creates a new SVGOMFEGaussianBlurElement object.
     */
    protected SVGOMFEGaussianBlurElement() {
    }

    /**
     * Creates a new SVGOMFEGaussianBlurElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEGaussianBlurElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_GAUSSIAN_BLUR_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEGaussianBlurElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
	SVGAnimatedString result;
	if (inReference == null ||
	    (result = (SVGAnimatedString)inReference.get()) == null) {
	    result = new SVGOMAnimatedString(this, null, SVG_IN_ATTRIBUTE);
	    inReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEGaussianBlurElement#getStdDeviationX()}.
     */
    public SVGAnimatedNumber getStdDeviationX() {
        SVGAnimatedNumber result;
        if (stdDeviationXReference == null ||
            (result = (SVGAnimatedNumber)stdDeviationXReference.get()) == null) {
            result = new SVGAnimatedNumber() {
                public float getBaseVal() {
                    Attr a = getAttributeNodeNS(null, SVG_STD_DEVIATION_ATTRIBUTE);
                    if (a != null) {
                        StringTokenizer st = new StringTokenizer(a.getValue(), " ");
                        if (st.hasMoreTokens()) {
                            return Float.parseFloat(st.nextToken());
                        }
                    }
                    return 0;
                }
                public void setBaseVal(float baseVal) throws DOMException {
                    String sdy = "";
                    Attr a = getAttributeNodeNS(null, SVG_STD_DEVIATION_ATTRIBUTE);
                    if (a != null) {
                        StringTokenizer st = new StringTokenizer(a.getValue(), " ");
                        if (st.hasMoreTokens()) {
                            st.nextToken();
                            if (st.hasMoreTokens()) {
                                sdy = st.nextToken();
                            }
                        }
                    }
                    setAttributeNS(null, SVG_STD_DEVIATION_ATTRIBUTE,
                                   Float.toString(baseVal) +
                                   ((sdy.length() == 0) ? "" :" " + sdy));
                }
                public float getAnimVal() {
                    throw new RuntimeException(" !!! TODO");
                }
            };
            stdDeviationXReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEGaussianBlurElement#getStdDeviationY()}.
     */
    public SVGAnimatedNumber getStdDeviationY() {
        SVGAnimatedNumber result;
        if (stdDeviationYReference == null ||
            (result = (SVGAnimatedNumber)stdDeviationYReference.get()) == null) {
            result = new SVGAnimatedNumber() {
                public float getBaseVal() {
                    Attr a = getAttributeNodeNS(null, SVG_STD_DEVIATION_ATTRIBUTE);
                    if (a != null) {
                        StringTokenizer st = new StringTokenizer(a.getValue(), " ");
                        if (st.hasMoreTokens()) {
                            String s = st.nextToken();
                            if (st.hasMoreTokens()) {
                                return Float.parseFloat(st.nextToken());
                            }
                            return Float.parseFloat(s);
                        }
                    }
                    return 0;
                }
                public void setBaseVal(float baseVal) throws DOMException {
                    Attr a = getAttributeNodeNS(null, SVG_STD_DEVIATION_ATTRIBUTE);
                    String sdx = "0 ";
                    if (a != null) {
                        StringTokenizer st = new StringTokenizer(a.getValue(), " ");
                        if (st.hasMoreTokens()) {
                            sdx = st.nextToken() + " ";
                        }
                    }
                    setAttributeNS(null, SVG_STD_DEVIATION_ATTRIBUTE,
                                   sdx + Float.toString(baseVal));
                }
                public float getAnimVal() {
                    throw new RuntimeException(" !!! TODO");
                }
            };
            stdDeviationYReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEGaussianBlurElement#setStdDeviation(float,float)}.
     */
    public void setStdDeviation (float devX, float devY) {
        setAttributeNS(null, SVG_STD_DEVIATION_ATTRIBUTE,
                       Float.toString(devX) + " " + Float.toString(devY));
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEGaussianBlurElement();
    }
}
