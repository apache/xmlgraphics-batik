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
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEGaussianBlurElement
{
    /**
     * The reference to the in attribute.
     */
    protected WeakReference inReference;

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
        return TAG_FE_GAUSSIAN_BLUR;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEGaussianBlurElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
	SVGAnimatedString result;
	if (inReference == null ||
	    (result = (SVGAnimatedString)inReference.get()) == null) {
	    result = new SVGOMAnimatedString(this, null, ATTR_IN);
	    inReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEGaussianBlurElement#getStdDeviationX()}.
     */
    public SVGAnimatedNumber getStdDeviationX() {
        throw new RuntimeException(" !!! SVGFEGaussianBlurElement#getStdDeviationX()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEGaussianBlurElement#getStdDeviationY()}.
     */
    public SVGAnimatedNumber getStdDeviationY() {
        throw new RuntimeException(" !!! SVGFEGaussianBlurElement#getStdDeviationY()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEGaussianBlurElement#setStdDeviation(float,float)}.
     */
    public void setStdDeviation (float devX, float devY) {
        throw new RuntimeException(" !!! SVGFEGaussianBlurElement#setStdDeviation()");
    }
}
