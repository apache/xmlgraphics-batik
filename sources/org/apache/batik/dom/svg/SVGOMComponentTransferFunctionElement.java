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

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGComponentTransferFunctionElement;

/**
 * This class represents the component transfer function elements.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMComponentTransferFunctionElement
    extends    SVGOMElement
    implements SVGComponentTransferFunctionElement
{
    /**
     * The slope attribute reference.
     */
    protected WeakReference slopeReference;
    
    /**
     * The intercept attribute reference.
     */
    protected WeakReference interceptReference;
    
    /**
     * The amplitude attribute reference.
     */
    protected WeakReference amplitudeReference;
    
    /**
     * The exponent attribute reference.
     */
    protected WeakReference exponentReference;
    
    /**
     * The offset attribute reference.
     */
    protected WeakReference offsetReference;
    
    /**
     * Creates a new Element object.
     */
    protected SVGOMComponentTransferFunctionElement() {
    }

    /**
     * Creates a new Element object.
     * @param prefix The namespace prefix.
     * @param owner  The owner document.
     */
    protected SVGOMComponentTransferFunctionElement(String prefix,
                                                    AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGComponentTransferFunctionElement#getType()}.
     */
    public SVGAnimatedEnumeration getType() {
	throw new RuntimeException(" !!! TODO: SVGOMComponentTransferFunctionElement.getType()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGComponentTransferFunctionElement#getTableValues()}.
     */
    public SVGAnimatedNumberList getTableValues() {
	throw new RuntimeException(" !!! TODO: SVGOMComponentTransferFunctionElement.getTableValues()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGComponentTransferFunctionElement#getSlope()}.
     */
    public SVGAnimatedNumber getSlope() {
	SVGAnimatedNumber result;
	if (slopeReference == null ||
	    (result = (SVGAnimatedNumber)slopeReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_SLOPE);
	    slopeReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGComponentTransferFunctionElement#getIntercept()}.
     */
    public SVGAnimatedNumber getIntercept() {
	SVGAnimatedNumber result;
	if (interceptReference == null ||
	    (result = (SVGAnimatedNumber)interceptReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_INTERCEPT);
	    interceptReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGComponentTransferFunctionElement#getAmplitude()}.
     */
    public SVGAnimatedNumber getAmplitude() {
	SVGAnimatedNumber result;
	if (amplitudeReference == null ||
	    (result = (SVGAnimatedNumber)amplitudeReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_AMPLITUDE);
	    amplitudeReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGComponentTransferFunctionElement#getExponent()}.
     */
    public SVGAnimatedNumber getExponent() {
	SVGAnimatedNumber result;
	if (exponentReference == null ||
	    (result = (SVGAnimatedNumber)exponentReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_EXPONENT);
	    exponentReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGComponentTransferFunctionElement#getOffset()}.
     */
    public SVGAnimatedNumber getOffset() {
	SVGAnimatedNumber result;
	if (offsetReference == null ||
	    (result = (SVGAnimatedNumber)offsetReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_OFFSET);
	    offsetReference = new WeakReference(result);
	}
	return result;
    }
}
