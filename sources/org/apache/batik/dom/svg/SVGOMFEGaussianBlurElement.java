/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;
import org.apache.batik.dom.AbstractDocument;
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
    extends    SVGOMElement
    implements SVGFEGaussianBlurElement {
    /**
     * The reference to the x attribute.
     */
    protected WeakReference xReference;

    /**
     * The reference to the y attribute.
     */
    protected WeakReference yReference;

    /**
     * The reference to the width attribute.
     */
    protected WeakReference widthReference;

    /**
     * The reference to the height attribute.
     */
    protected WeakReference heightReference;

    /**
     * Creates a new SVGOMFEGaussianBlurElement object.
     */
    public SVGOMFEGaussianBlurElement() {
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
        return "feGaussianBlur";
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEGaussianBlurElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        throw new RuntimeException(" !!! SVGFEGaussianBlurElement#getIn1()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEGaussianBlurElement#getStdDeviationX()}.
     */
    public SVGAnimatedNumber getStdDeviationX() {
        throw new RuntimeException(" !!! SVGFEGaussianBlurElement#getStdDeviationX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEGaussianBlurElement#getStdDeviationY()}.
     */
    public SVGAnimatedNumber getStdDeviationY() {
        throw new RuntimeException(" !!! SVGFEGaussianBlurElement#getStdDeviationY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEGaussianBlurElement#setStdDeviation(float,float)}.
     */
    public void setStdDeviation (float devX, float devY) {
        throw new RuntimeException(" !!! SVGFEGaussianBlurElement#setStdDeviation()");
    }
    

    // SVGFilterPrimitiveStandardAttributes ////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getX()}.
     */
    public SVGAnimatedLength getX() {
	SVGAnimatedLength result;
	if (xReference == null ||
	    (result = (SVGAnimatedLength)xReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "x");
	    xReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getY()}.
     */
    public SVGAnimatedLength getY() {
	SVGAnimatedLength result;
	if (yReference == null ||
	    (result = (SVGAnimatedLength)yReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "y");
	    yReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getWidth()}.
     */
    public SVGAnimatedLength getWidth() {
	SVGAnimatedLength result;
	if (widthReference == null ||
	    (result = (SVGAnimatedLength)widthReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "width");
	    widthReference = new WeakReference(result);
	}
	return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getHeight()}.
     */
    public SVGAnimatedLength getHeight() {
	SVGAnimatedLength result;
	if (heightReference == null ||
	    (result = (SVGAnimatedLength)heightReference.get()) == null) {
	    result = new SVGOMAnimatedLength(this, null, "height");
	    heightReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getResult()}.
     */
    public SVGAnimatedString getResult() {
        throw new RuntimeException(" !!! SVGFilterPrimitiveStandardAttributes#getResult()");
    }
}
