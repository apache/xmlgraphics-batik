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
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEDisplacementMapElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEDisplacementMapElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEDisplacementMapElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEDisplacementMapElement {

    /**
     * The DefaultAttributeValueProducer for scale.
     */
    protected final static DefaultAttributeValueProducer
        SCALE_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_DEFAULT_VALUE_FE_DISPLACEMENT_MAP_SCALE;
                }
            };

    /**
     * The reference to the in attribute.
     */
    protected transient WeakReference inReference;

    /**
     * The reference to the in2 attribute.
     */
    protected transient WeakReference in2Reference;

    /**
     * The reference to the scale attribute.
     */
    protected transient WeakReference scaleReference;

    /**
     * The reference to the xChannelSelector attribute.
     */
    protected transient WeakReference xChannelSelectorReference;

    /**
     * The reference to the yChannelSelector attribute.
     */
    protected transient WeakReference yChannelSelectorReference;

    /**
     * The attribute-value map map.
     */
    protected static Map attributeValues = new HashMap(2);
    static {
        Map values = new HashMap(3);
        values.put(SVG_X_CHANNEL_SELECTOR_ATTRIBUTE, SVG_A_VALUE);
        values.put(SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE, SVG_A_VALUE);
        attributeValues.put(null, values);
    }

    // The enumeration maps
    protected final static Map STRING_TO_SHORT_X_CHANNEL_SELECTOR = new HashMap(6);
    protected final static Map SHORT_TO_STRING_X_CHANNEL_SELECTOR = new HashMap(6);
    protected final static Map STRING_TO_SHORT_Y_CHANNEL_SELECTOR =
        STRING_TO_SHORT_X_CHANNEL_SELECTOR;
    protected final static Map SHORT_TO_STRING_Y_CHANNEL_SELECTOR =
        SHORT_TO_STRING_X_CHANNEL_SELECTOR;
    static {
        STRING_TO_SHORT_X_CHANNEL_SELECTOR.put
            (SVG_R_VALUE,
             SVGOMAnimatedEnumeration.createShort((short)1));
        STRING_TO_SHORT_X_CHANNEL_SELECTOR.put
            (SVG_G_VALUE,
             SVGOMAnimatedEnumeration.createShort((short)2));
        STRING_TO_SHORT_X_CHANNEL_SELECTOR.put
            (SVG_B_VALUE,
             SVGOMAnimatedEnumeration.createShort((short)3));
        STRING_TO_SHORT_X_CHANNEL_SELECTOR.put
            (SVG_A_VALUE,
             SVGOMAnimatedEnumeration.createShort((short)4));

        SHORT_TO_STRING_X_CHANNEL_SELECTOR.put
            (SVGOMAnimatedEnumeration.createShort((short)1),
             SVG_A_VALUE);
        SHORT_TO_STRING_X_CHANNEL_SELECTOR.put
            (SVGOMAnimatedEnumeration.createShort((short)2),
             SVG_R_VALUE);
        SHORT_TO_STRING_X_CHANNEL_SELECTOR.put
            (SVGOMAnimatedEnumeration.createShort((short)3),
             SVG_G_VALUE);
        SHORT_TO_STRING_X_CHANNEL_SELECTOR.put
            (SVGOMAnimatedEnumeration.createShort((short)4),
             SVG_B_VALUE);
    }

    /**
     * Creates a new SVGOMFEDisplacementMap object.
     */
    protected SVGOMFEDisplacementMapElement() {
    }

    /**
     * Creates a new SVGOMFEDisplacementMapElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEDisplacementMapElement(String prefix,
                                         AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_DISPLACEMENT_MAP_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getIn1()}.
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
     * SVGFEDisplacementMapElement#getIn2()}.
     */
    public SVGAnimatedString getIn2() {
	SVGAnimatedString result;
	if (in2Reference == null ||
	    (result = (SVGAnimatedString)in2Reference.get()) == null) {
	    result = new SVGOMAnimatedString(this, null, SVG_IN2_ATTRIBUTE);
	    in2Reference = new WeakReference(result);
	}
	return result;
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEDisplacementMapElement#getScale()}.
     */
    public SVGAnimatedNumber getScale() {
	SVGAnimatedNumber result;
	if (scaleReference == null ||
	    (result = (SVGAnimatedNumber)scaleReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_SCALE,
                                             SCALE_DEFAULT_VALUE_PRODUCER);
	    scaleReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getXChannelSelector()}.
     */
    public SVGAnimatedEnumeration getXChannelSelector() {
        SVGAnimatedEnumeration result;
        if (xChannelSelectorReference == null ||
            (result = (SVGAnimatedEnumeration)xChannelSelectorReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_X_CHANNEL_SELECTOR_ATTRIBUTE,
                                                  STRING_TO_SHORT_X_CHANNEL_SELECTOR,
                                                  SHORT_TO_STRING_X_CHANNEL_SELECTOR,
                                                  null);
            xChannelSelectorReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getYChannelSelector()}.
     */
    public SVGAnimatedEnumeration getYChannelSelector() {
        SVGAnimatedEnumeration result;
        if (yChannelSelectorReference == null ||
            (result = (SVGAnimatedEnumeration)yChannelSelectorReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE,
                                                  STRING_TO_SHORT_Y_CHANNEL_SELECTOR,
                                                  SHORT_TO_STRING_Y_CHANNEL_SELECTOR,
                                                  null);
            yChannelSelectorReference = new WeakReference(result);
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
        return new SVGOMFEDisplacementMapElement();
    }
}
