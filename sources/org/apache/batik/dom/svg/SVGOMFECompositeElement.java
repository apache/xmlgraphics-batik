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
import org.w3c.dom.svg.SVGFECompositeElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFECompositeElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFECompositeElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFECompositeElement {

    /**
     * The reference to the in attribute.
     */
    protected WeakReference inReference;

    /**
     * The reference to the in2 attribute.
     */
    protected WeakReference in2Reference;

    /**
     * The reference to the k1 attribute.
     */
    protected WeakReference k1Reference;

    /**
     * The reference to the k2 attribute.
     */
    protected WeakReference k2Reference;

    /**
     * The reference to the k3 attribute.
     */
    protected WeakReference k3Reference;

    /**
     * The reference to the k4 attribute.
     */
    protected WeakReference k4Reference;

    /**
     * The attribute-value map map.
     */
    protected static Map attributeValues = new HashMap(3);
    static {
        Map values = new HashMap(2);
        values.put("operator",  "over");
        attributeValues.put(null, values);
    }

    /**
     * Creates a new SVGOMFECompositeElement object.
     */
    protected SVGOMFECompositeElement() {
    }

    /**
     * Creates a new SVGOMFECompositeElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFECompositeElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return "feComposite";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFECompositeElement#getIn1()}.
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
     * SVGFECompositeElement#getIn2()}.
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
     * SVGFECompositeElement#getOperator()}.
     */
    public SVGAnimatedEnumeration getOperator() {
        throw new RuntimeException(" !!! SVGFECompositeElement#getOperator()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFECompositeElement#getK1()}.
     */
    public SVGAnimatedNumber getK1() {
	SVGAnimatedNumber result;
	if (k1Reference == null ||
	    (result = (SVGAnimatedNumber)k1Reference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_K1);
	    k1Reference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFECompositeElement#getK2()}.
     */
    public SVGAnimatedNumber getK2() {
	SVGAnimatedNumber result;
	if (k2Reference == null ||
	    (result = (SVGAnimatedNumber)k2Reference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_K2);
	    k2Reference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFECompositeElement#getK3()}.
     */
    public SVGAnimatedNumber getK3() {
	SVGAnimatedNumber result;
	if (k3Reference == null ||
	    (result = (SVGAnimatedNumber)k3Reference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_K3);
	    k3Reference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFECompositeElement#getK4()}.
     */
    public SVGAnimatedNumber getK4() {
	SVGAnimatedNumber result;
	if (k4Reference == null ||
	    (result = (SVGAnimatedNumber)k4Reference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_K4);
	    k4Reference = new WeakReference(result);
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
        return new SVGOMFECompositeElement();
    }
}
