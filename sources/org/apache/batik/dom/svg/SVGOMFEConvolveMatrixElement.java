/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEConvolveMatrixElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEConvolveMatrixElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEConvolveMatrixElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEConvolveMatrixElement {
    /**
     * The reference to the in attribute.
     */
    protected WeakReference inReference;

    /**
     * The reference to the orderX attribute.
     */
    protected WeakReference orderXReference;

    /**
     * The reference to the orderY attribute.
     */
    protected WeakReference orderYReference;

    /**
     * The reference to the targetX attribute.
     */
    protected WeakReference targetXReference;

    /**
     * The reference to the targetY attribute.
     */
    protected WeakReference targetYReference;

    /**
     * The reference to the divisor attribute.
     */
    protected WeakReference divisorReference;

    /**
     * The reference to the bias attribute.
     */
    protected WeakReference biasReference;

    /**
     * The reference to the kernelUnitLengthX attribute.
     */
    protected WeakReference kernelUnitLengthXReference;

    /**
     * The reference to the kernelUnitLengthY attribute.
     */
    protected WeakReference kernelUnitLengthYReference;

    /**
     * The reference to the preserveAlpha attribute.
     */
    protected WeakReference preserveAlphaReference;

    /**
     * The attribute-value map map.
     */
    protected static Map attributeValues = new HashMap(3);
    static {
        Map values = new HashMap(2);
        values.put("edgeMode",  "duplicate");
        attributeValues.put(null, values);
    }

    /**
     * Creates a new SVGOMFEConvolveMatrixElement object.
     */
    public SVGOMFEConvolveMatrixElement() {
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
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return "feConvolveMatrix";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEConvolveMatrixElement#getIn1()}.
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
     * SVGFEConvolveMatrixElement#getEdgeMode()}.
     */
    public SVGAnimatedEnumeration getEdgeMode() {
        throw new RuntimeException(" !!! SVGFEConvolveMatrixElement#getEdgeMode()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEConvolveMatrixElement#getKernelMatrix()}.
     */
    public SVGAnimatedNumberList getKernelMatrix() {
        throw new RuntimeException(" !!! SVGFEConvolveMatrixElement#getKernelMatrix()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getOrderX()}.
     */
    public SVGAnimatedInteger getOrderX() {
	SVGAnimatedInteger result;
	if (orderXReference == null ||
	    (result = (SVGAnimatedInteger)orderXReference.get()) == null) {
	    result = new SVGOMAnimatedInteger(this, null, ATTR_ORDER_X);
	    orderXReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getOrderY()}.
     */
    public SVGAnimatedInteger getOrderY() {
	SVGAnimatedInteger result;
	if (orderYReference == null ||
	    (result = (SVGAnimatedInteger)orderYReference.get()) == null) {
	    result = new SVGOMAnimatedInteger(this, null, ATTR_ORDER_Y);
	    orderYReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getTargetX()}.
     */
    public SVGAnimatedInteger getTargetX() {
	SVGAnimatedInteger result;
	if (targetXReference == null ||
	    (result = (SVGAnimatedInteger)targetXReference.get()) == null) {
	    result = new SVGOMAnimatedInteger(this, null, ATTR_TARGET_X);
	    targetXReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getTargetY()}.
     */
    public SVGAnimatedInteger getTargetY() {
	SVGAnimatedInteger result;
	if (targetYReference == null ||
	    (result = (SVGAnimatedInteger)targetYReference.get()) == null) {
	    result = new SVGOMAnimatedInteger(this, null, ATTR_TARGET_Y);
	    targetYReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getDivisor()}.
     */
    public SVGAnimatedNumber getDivisor() {
	SVGAnimatedNumber result;
	if (divisorReference == null ||
	    (result = (SVGAnimatedNumber)divisorReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_DIVISOR);
	    divisorReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElementElement#getBias()}.
     */
    public SVGAnimatedNumber getBias() {
	SVGAnimatedNumber result;
	if (biasReference == null ||
	    (result = (SVGAnimatedNumber)biasReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_BIAS);
	    biasReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getKernelUnitLengthX()}.
     */
    public SVGAnimatedLength getKernelUnitLengthX() {
	SVGAnimatedLength result;
	if (kernelUnitLengthXReference == null ||
	    (result = (SVGAnimatedLength)kernelUnitLengthXReference.get()) ==
            null) {
	    result = new SVGOMAnimatedLength
                (this, null, ATTR_KERNEL_UNIT_LENGTH_X);
	    kernelUnitLengthXReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getKernelUnitLengthY()}.
     */
    public SVGAnimatedLength getKernelUnitLengthY() {
	SVGAnimatedLength result;
	if (kernelUnitLengthYReference == null ||
	    (result = (SVGAnimatedLength)kernelUnitLengthYReference.get()) ==
            null) {
	    result = new SVGOMAnimatedLength
                (this, null, ATTR_KERNEL_UNIT_LENGTH_Y);
	    kernelUnitLengthYReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElementElement#getPreserveAlpha()}.
     */
    public SVGAnimatedBoolean getPreserveAlpha() {
	SVGAnimatedBoolean result;
	if (preserveAlphaReference == null ||
	    (result = (SVGAnimatedBoolean)preserveAlphaReference.get()) ==
            null) {
	    result = new SVGOMAnimatedBoolean(this, null, ATTR_PRESERVE_ALPHA);
	    preserveAlphaReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * Returns the default attribute values in a map.
     * @return null if this element has no attribute with a default value.
     */
    protected Map getDefaultAttributeValues() {
        return attributeValues;
    }
}
