/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

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
    implements SVGComponentTransferFunctionElement {

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
     * SVGComponentTransferFunctionElement#getType()}.
     */
    public SVGAnimatedEnumeration getType() {
        throw new RuntimeException("!!! TODO: getType");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getTableValues()}.
     */
    public SVGAnimatedNumberList getTableValues() {
        throw new RuntimeException("!!! TODO: getTableValues");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getSlope()}.
     */
    public SVGAnimatedNumber getSlope() {
        throw new RuntimeException("!!! TODO: getSlope");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getIntercept()}.
     */
    public SVGAnimatedNumber getIntercept() {
        throw new RuntimeException("!!! TODO: getIntercept");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getAmplitude()}.
     */
    public SVGAnimatedNumber getAmplitude() {
        throw new RuntimeException("!!! TODO: getAmplitude");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getExponent()}.
     */
    public SVGAnimatedNumber getExponent() {
        throw new RuntimeException("!!! TODO: getExponent");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGComponentTransferFunctionElement#getOffset()}.
     */
    public SVGAnimatedNumber getOffset() {
        throw new RuntimeException("!!! TODO: getOffset");
    }
}
