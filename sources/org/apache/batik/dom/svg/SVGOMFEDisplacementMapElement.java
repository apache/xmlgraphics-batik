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
     * The reference to the in attribute.
     */
    protected WeakReference inReference;

    /**
     * The reference to the in2 attribute.
     */
    protected WeakReference in2Reference;

    /**
     * The reference to the scale attribute.
     */
    protected WeakReference scaleReference;

    /**
     * The attribute-value map map.
     */
    protected static Map attributeValues = new HashMap(2);
    static {
        Map values = new HashMap(3);
        values.put("xChannelSelector",  "A");
        values.put("yChannelSelector",  "A");
        attributeValues.put(null, values);
    }

    /**
     * Creates a new SVGOMFEDisplacementMap object.
     */
    public SVGOMFEDisplacementMapElement() {
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
        return TAG_FE_DISPLACEMENT_MAP;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getIn1()}.
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
     * SVGFEDisplacementMapElement#getIn2()}.
     */
    public SVGAnimatedString getIn2() {
	SVGAnimatedString result;
	if (in2Reference == null ||
	    (result = (SVGAnimatedString)in2Reference.get()) == null) {
	    result = new SVGOMAnimatedString(this, null, ATTR_IN2);
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
	    result = new SVGOMAnimatedNumber(this, null, ATTR_SCALE);
	    scaleReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getXChannelSelector()}.
     */
    public SVGAnimatedEnumeration getXChannelSelector() {
        throw new RuntimeException(" !!! SVGFEDisplacementMapElement#getXChannelSelector()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getYChannelSelector()}.
     */
    public SVGAnimatedEnumeration getYChannelSelector() {
        throw new RuntimeException(" !!! SVGFEDisplacementMapElement#getYChannelSelector()");
    }

    /**
     * Returns the default attribute values in a map.
     * @return null if this element has no attribute with a default value.
     */
    protected Map getDefaultAttributeValues() {
        return attributeValues;
    }
}
