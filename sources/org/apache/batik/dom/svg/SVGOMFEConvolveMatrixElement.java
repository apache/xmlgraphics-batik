/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.DoublyIndexedTable;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEConvolveMatrixElement;

/**
 * This class implements {@link SVGFEConvolveMatrixElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEConvolveMatrixElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEConvolveMatrixElement {

    /**
     * The attribute initializer.
     */
    protected final static AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(1);
        attributeInitializer.addAttribute(null,
                                          null,
                                          SVG_EDGE_MODE_ATTRIBUTE,
                                          SVG_DUPLICATE_VALUE);
    }

    /**
     * Creates a new SVGOMFEConvolveMatrixElement object.
     */
    protected SVGOMFEConvolveMatrixElement() {
    }

    /**
     * Creates a new SVGOMFEConvolveMatrixElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEConvolveMatrixElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_CONVOLVE_MATRIX_TAG;
    }

    /**
     * <b>DOM</b>: Implements { @link SVGFEConvolveMatrixElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        throw new RuntimeException("!!! TODO: getIn1()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getEdgeMode()}.
     */
    public SVGAnimatedEnumeration getEdgeMode() {
        throw new RuntimeException("!!! TODO: getEdgeMode()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getKernelMatrix()}.
     */
    public SVGAnimatedNumberList getKernelMatrix() {
        throw new RuntimeException("!!! TODO: getKernelMatrix()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getOrderX()}.
     */
    public SVGAnimatedInteger getOrderX() {
        throw new RuntimeException("!!! TODO: getOrderX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getOrderY()}.
     */
    public SVGAnimatedInteger getOrderY() {
        throw new RuntimeException("!!! TODO: getOrderY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getTargetX()}.
     */
    public SVGAnimatedInteger getTargetX() {
        throw new RuntimeException("!!! TODO: getTargetX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getTargetY()}.
     */
    public SVGAnimatedInteger getTargetY() {
        throw new RuntimeException("!!! TODO: getTargetY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEConvolveMatrixElement#getDivisor()}.
     */
    public SVGAnimatedNumber getDivisor() {
        throw new RuntimeException("!!! TODO: getDivisor()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getBias()}.
     */
    public SVGAnimatedNumber getBias() {
        throw new RuntimeException("!!! TODO: getBias()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getKernelUnitLengthX()}.
     */
    public SVGAnimatedLength getKernelUnitLengthX() {
        throw new RuntimeException("!!! TODO: getKernelUnitLengthX()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getKernelUnitLengthY()}.
     */
    public SVGAnimatedLength getKernelUnitLengthY() {
        throw new RuntimeException("!!! TODO: getKernelUnitLengthY()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getPreserveAlpha()}.
     */
    public SVGAnimatedBoolean getPreserveAlpha() {
        throw new RuntimeException("!!! TODO: getPreserveAlpha()");
    }

    /**
     * Returns the AttributeInitializer for this element type.
     * @return null if this element has no attribute with a default value.
     */
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEConvolveMatrixElement();
    }
}
