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
     * The DefaultAttributeValueProducer for tableValues.
     */
    protected final static DefaultAttributeValueProducer
        TABLE_VALUES_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_COMPONENT_TRANSFER_FUNCTION_TABLE_VALUES_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for slope.
     */
    protected final static DefaultAttributeValueProducer
        SLOPE_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_COMPONENT_TRANSFER_FUNCTION_SLOPE_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for intercept.
     */
    protected final static DefaultAttributeValueProducer
        INTERCEPT_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_COMPONENT_TRANSFER_FUNCTION_INTERCEPT_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for amplitude.
     */
    protected final static DefaultAttributeValueProducer
        AMPLITUDE_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_COMPONENT_TRANSFER_FUNCTION_AMPLITUDE_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for exponent.
     */
    protected final static DefaultAttributeValueProducer
        EXPONENT_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_COMPONENT_TRANSFER_FUNCTION_EXPONENT_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for offset.
     */
    protected final static DefaultAttributeValueProducer
        OFFSET_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_COMPONENT_TRANSFER_FUNCTION_OFFSET_DEFAULT_VALUE;
                }
            };

    // The enumeration maps
    protected final static Map STRING_TO_SHORT_TYPE = new HashMap(7);
    protected final static Map SHORT_TO_STRING_TYPE = new HashMap(7);
    static {
        STRING_TO_SHORT_TYPE.put(SVG_IDENTITY_VALUE,
                                 SVGOMAnimatedEnumeration.createShort((short)1));
        STRING_TO_SHORT_TYPE.put(SVG_TABLE_VALUE,
                                 SVGOMAnimatedEnumeration.createShort((short)2));
        STRING_TO_SHORT_TYPE.put(SVG_DISCRETE_VALUE,
                                 SVGOMAnimatedEnumeration.createShort((short)3));
        STRING_TO_SHORT_TYPE.put(SVG_LINEAR_VALUE,
                                 SVGOMAnimatedEnumeration.createShort((short)4));
        STRING_TO_SHORT_TYPE.put(SVG_GAMMA_VALUE,
                                 SVGOMAnimatedEnumeration.createShort((short)5));

        SHORT_TO_STRING_TYPE.put(SVGOMAnimatedEnumeration.createShort((short)1),
                                 SVG_IDENTITY_VALUE);
        SHORT_TO_STRING_TYPE.put(SVGOMAnimatedEnumeration.createShort((short)2),
                                 SVG_TABLE_VALUE);
        SHORT_TO_STRING_TYPE.put(SVGOMAnimatedEnumeration.createShort((short)3),
                                 SVG_DISCRETE_VALUE);
        SHORT_TO_STRING_TYPE.put(SVGOMAnimatedEnumeration.createShort((short)4),
                                 SVG_LINEAR_VALUE);
        SHORT_TO_STRING_TYPE.put(SVGOMAnimatedEnumeration.createShort((short)5),
                                 SVG_GAMMA_VALUE);
    }

    /**
     * The type attribute reference.
     */
    protected transient WeakReference typeReference;

    /**
     * The tableValues attribute reference.
     */
    protected transient WeakReference tableValuesReference;

    /**
     * The slope attribute reference.
     */
    protected transient WeakReference slopeReference;

    /**
     * The intercept attribute reference.
     */
    protected transient WeakReference interceptReference;

    /**
     * The amplitude attribute reference.
     */
    protected transient WeakReference amplitudeReference;

    /**
     * The exponent attribute reference.
     */
    protected transient WeakReference exponentReference;

    /**
     * The offset attribute reference.
     */
    protected transient WeakReference offsetReference;

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
        SVGAnimatedEnumeration result;
        if (typeReference == null ||
            (result = (SVGAnimatedEnumeration)typeReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_TYPE_ATTRIBUTE,
                                                  STRING_TO_SHORT_TYPE,
                                                  SHORT_TO_STRING_TYPE,
                                                  null);
            typeReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGComponentTransferFunctionElement#getTableValues()}.
     */
    public SVGAnimatedNumberList getTableValues() {
        SVGAnimatedNumberList result;
        if (tableValuesReference == null ||
            (result = (SVGOMAnimatedNumberList)tableValuesReference.get()) == null) {
            result = new SVGOMAnimatedNumberList(this, null,
                                                 SVG_TABLE_VALUES_ATTRIBUTE,
                                                 TABLE_VALUES_DEFAULT_VALUE_PRODUCER);
            tableValuesReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGComponentTransferFunctionElement#getSlope()}.
     */
    public SVGAnimatedNumber getSlope() {
        SVGAnimatedNumber result;
        if (slopeReference == null ||
            (result = (SVGAnimatedNumber)slopeReference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_SLOPE_ATTRIBUTE,
                                             SLOPE_DEFAULT_VALUE_PRODUCER);
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
            result = new SVGOMAnimatedNumber(this, null, SVG_INTERCEPT_ATTRIBUTE,
                                             INTERCEPT_DEFAULT_VALUE_PRODUCER);
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
            result = new SVGOMAnimatedNumber(this, null, SVG_AMPLITUDE_ATTRIBUTE,
                                             AMPLITUDE_DEFAULT_VALUE_PRODUCER);
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
            result = new SVGOMAnimatedNumber(this, null, SVG_EXPONENT_ATTRIBUTE,
                                             EXPONENT_DEFAULT_VALUE_PRODUCER);
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
            result = new SVGOMAnimatedNumber(this, null, SVG_OFFSET_ATTRIBUTE,
                                             OFFSET_DEFAULT_VALUE_PRODUCER);
            offsetReference = new WeakReference(result);
        }
        return result;
    }
}
