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
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEBlendElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEBlendElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEBlendElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEBlendElement {

    // The enumeration maps
    protected final static Map STRING_TO_SHORT_MODE = new HashMap(7);
    protected final static Map SHORT_TO_STRING_MODE = new HashMap(7);
    static {
        STRING_TO_SHORT_MODE.put(SVG_NORMAL_VALUE,
                                  SVGOMAnimatedEnumeration.createShort((short)1));
        STRING_TO_SHORT_MODE.put(SVG_MULTIPLY_VALUE,
                                  SVGOMAnimatedEnumeration.createShort((short)2));
        STRING_TO_SHORT_MODE.put(SVG_SCREEN_VALUE,
                                  SVGOMAnimatedEnumeration.createShort((short)3));
        STRING_TO_SHORT_MODE.put(SVG_DARKEN_VALUE,
                                  SVGOMAnimatedEnumeration.createShort((short)4));
        STRING_TO_SHORT_MODE.put(SVG_LIGHTEN_VALUE,
                                  SVGOMAnimatedEnumeration.createShort((short)5));

        SHORT_TO_STRING_MODE.put(SVGOMAnimatedEnumeration.createShort((short)1),
                                 SVG_NORMAL_VALUE);
        SHORT_TO_STRING_MODE.put(SVGOMAnimatedEnumeration.createShort((short)2),
                                 SVG_MULTIPLY_VALUE);
        SHORT_TO_STRING_MODE.put(SVGOMAnimatedEnumeration.createShort((short)3),
                                 SVG_SCREEN_VALUE);
        SHORT_TO_STRING_MODE.put(SVGOMAnimatedEnumeration.createShort((short)4),
                                 SVG_DARKEN_VALUE);
        SHORT_TO_STRING_MODE.put(SVGOMAnimatedEnumeration.createShort((short)5),
                                 SVG_LIGHTEN_VALUE);
    }

    /**
     * The reference to the in attribute.
     */
    protected transient WeakReference inReference;

    /**
     * The reference to the in2 attribute.
     */
    protected transient WeakReference in2Reference;

    /**
     * The reference to the mode attribute.
     */
    protected transient WeakReference modeReference;

    /**
     * The attribute-value map map.
     */
    protected static Map attributeValues = new HashMap(2);
    static {
        Map values = new HashMap(2);
        values.put(SVG_MODE_ATTRIBUTE,  SVG_NORMAL_VALUE);
        attributeValues.put(null, values);
    }

    /**
     * Creates a new SVGOMFEBlendElement object.
     */
    protected SVGOMFEBlendElement() {
    }

    /**
     * Creates a new SVGOMFEBlendElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEBlendElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_BLEND_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEBlendElement#getIn1()}.
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
     * SVGFEBlendElement#getIn2()}.
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
     * SVGFEBlendElement#getMode()}.
     */
    public SVGAnimatedEnumeration getMode() {
        SVGAnimatedEnumeration result;
        if (modeReference == null ||
            (result = (SVGAnimatedEnumeration)modeReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_MODE_ATTRIBUTE,
                                                  STRING_TO_SHORT_MODE,
                                                  SHORT_TO_STRING_MODE,
                                                  null);
            modeReference = new WeakReference(result);
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
        return new SVGOMFEBlendElement();
    }
}
