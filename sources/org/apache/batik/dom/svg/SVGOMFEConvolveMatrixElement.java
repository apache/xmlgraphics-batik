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
 * This class implements {@link org.w3c.dom.svg.SVGFEConvolveMatrixElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEConvolveMatrixElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEConvolveMatrixElement {

    /**
     * The DefaultAttributeValueProducer for edgeMode.
     */
    protected final static DefaultAttributeValueProducer
        EDGE_MODE_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_CONVOLVE_MATRIX_EDGE_MODE_DEFAULT_VALUE;
                }
            };

    /**
     * The reference to the in attribute.
     */
    protected transient WeakReference inReference;

    /**
     * The reference to the edgeMode attribute.
     */
    protected transient WeakReference edgeModeReference;

    /**
     * The reference to the kernelMatrix attribute.
     */
    protected transient WeakReference kernelMatrixReference;

    /**
     * The reference to the orderX attribute.
     */
    protected transient WeakReference orderXReference;

    /**
     * The reference to the orderY attribute.
     */
    protected transient WeakReference orderYReference;

    /**
     * The reference to the targetX attribute.
     */
    protected transient WeakReference targetXReference;

    /**
     * The reference to the targetY attribute.
     */
    protected transient WeakReference targetYReference;

    /**
     * The reference to the divisor attribute.
     */
    protected transient WeakReference divisorReference;

    /**
     * The reference to the bias attribute.
     */
    protected transient WeakReference biasReference;

    /**
     * The reference to the kernelUnitLengthX attribute.
     */
    protected transient WeakReference kernelUnitLengthXReference;

    /**
     * The reference to the kernelUnitLengthY attribute.
     */
    protected transient WeakReference kernelUnitLengthYReference;

    /**
     * The reference to the preserveAlpha attribute.
     */
    protected transient WeakReference preserveAlphaReference;

    /**
     * The attribute-value map map.
     */
    protected static Map attributeValues = new HashMap(3);
    static {
        Map values = new HashMap(2);
        values.put(SVG_EDGE_MODE_ATTRIBUTE, SVG_DUPLICATE_VALUE);
        attributeValues.put(null, values);
    }

    // The enumeration maps.
    protected final static Map STRING_TO_SHORT_EDGE_MODE = new HashMap(5);
    protected final static Map SHORT_TO_STRING_EDGE_MODE = new HashMap(5);
    static {
        STRING_TO_SHORT_EDGE_MODE.put(SVG_DUPLICATE_VALUE,
                                      SVGOMAnimatedEnumeration.createShort((short)1));
        STRING_TO_SHORT_EDGE_MODE.put(SVG_WRAP_VALUE,
                                      SVGOMAnimatedEnumeration.createShort((short)2));
        STRING_TO_SHORT_EDGE_MODE.put(SVG_NONE_VALUE,
                                      SVGOMAnimatedEnumeration.createShort((short)3));

        SHORT_TO_STRING_EDGE_MODE.put(SVGOMAnimatedEnumeration.createShort((short)1),
                                      SVG_DUPLICATE_VALUE);
        SHORT_TO_STRING_EDGE_MODE.put(SVGOMAnimatedEnumeration.createShort((short)2),
                                      SVG_WRAP_VALUE);
        SHORT_TO_STRING_EDGE_MODE.put(SVGOMAnimatedEnumeration.createShort((short)3),
                                      SVG_NONE_VALUE);
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
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_CONVOLVE_MATRIX_TAG;
    }

    /**
     * <b>DOM</b>: Implements { @link !!!
     * SVGFEConvolveMatrixElement#getIn1()}.
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
     * SVGFEConvolveMatrixElement#getEdgeMode()}.
     */
    public SVGAnimatedEnumeration getEdgeMode() {
        SVGAnimatedEnumeration result;
        if (edgeModeReference == null ||
            (result = (SVGAnimatedEnumeration)edgeModeReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_EDGE_MODE_ATTRIBUTE,
                                                  STRING_TO_SHORT_EDGE_MODE,
                                                  SHORT_TO_STRING_EDGE_MODE,
                                                  EDGE_MODE_DEFAULT_VALUE_PRODUCER);
            edgeModeReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEConvolveMatrixElement#getKernelMatrix()}.
     */
    public SVGAnimatedNumberList getKernelMatrix() {
        SVGAnimatedNumberList result;
        if (kernelMatrixReference == null ||
            (result = (SVGOMAnimatedNumberList)kernelMatrixReference.get()) == null) {
            result = new SVGOMAnimatedNumberList(this, null,
                                                 SVG_KERNEL_MATRIX_ATTRIBUTE,
                                                 null);
            kernelMatrixReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getOrderX()}.
     */
    public SVGAnimatedInteger getOrderX() {
        SVGAnimatedInteger result;
        if (orderXReference == null ||
            (result = (SVGAnimatedInteger)orderXReference.get()) == null) {
            result = new SVGOMAnimatedInteger(this, null, SVG_ORDER_X_ATTRIBUTE, null);
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
            result = new SVGOMAnimatedInteger(this, null, SVG_ORDER_Y_ATTRIBUTE, null);
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
            result = new SVGOMAnimatedInteger(this, null, SVG_TARGET_X_ATTRIBUTE, null);
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
            result = new SVGOMAnimatedInteger(this, null, SVG_TARGET_Y_ATTRIBUTE, null);
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
            result = new SVGOMAnimatedNumber(this, null, SVG_DIVISOR_ATTRIBUTE, null);
            divisorReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getBias()}.
     */
    public SVGAnimatedNumber getBias() {
        SVGAnimatedNumber result;
        if (biasReference == null ||
            (result = (SVGAnimatedNumber)biasReference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_BIAS_ATTRIBUTE, null);
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
            (result = (SVGAnimatedLength)kernelUnitLengthXReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null,
                                             SVG_KERNEL_UNIT_LENGTH_X_ATTRIBUTE,
                                             null);
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
            (result = (SVGAnimatedLength)kernelUnitLengthYReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null,
                                             SVG_KERNEL_UNIT_LENGTH_Y_ATTRIBUTE,
                                             null);
            kernelUnitLengthYReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEConvolveMatrixElement#getPreserveAlpha()}.
     */
    public SVGAnimatedBoolean getPreserveAlpha() {
        SVGAnimatedBoolean result;
        if (preserveAlphaReference == null ||
            (result = (SVGAnimatedBoolean)preserveAlphaReference.get()) == null) {
            result = new SVGOMAnimatedBoolean(this, null, SVG_PRESERVE_ALPHA_ATTRIBUTE);
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

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEConvolveMatrixElement();
    }
}
