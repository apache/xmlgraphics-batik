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
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEColorMatrixElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEColorMatrixElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEColorMatrixElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEColorMatrixElement {


    // The enumeration maps
    protected final static Map STRING_TO_SHORT_TYPE = new HashMap(6);
    protected final static Map SHORT_TO_STRING_TYPE = new HashMap(6);
    static {
        STRING_TO_SHORT_TYPE.put(SVG_MATRIX_VALUE,
                                  SVGOMAnimatedEnumeration.createShort((short)1));
        STRING_TO_SHORT_TYPE.put(SVG_SATURATE_VALUE,
                                  SVGOMAnimatedEnumeration.createShort((short)2));
        STRING_TO_SHORT_TYPE.put(SVG_HUE_ROTATE_VALUE,
                                  SVGOMAnimatedEnumeration.createShort((short)3));
        STRING_TO_SHORT_TYPE.put(SVG_LUMINANCE_TO_ALPHA_VALUE,
                                  SVGOMAnimatedEnumeration.createShort((short)4));

        SHORT_TO_STRING_TYPE.put(SVGOMAnimatedEnumeration.createShort((short)1),
                                 SVG_MATRIX_VALUE);
        SHORT_TO_STRING_TYPE.put(SVGOMAnimatedEnumeration.createShort((short)2),
                                 SVG_SATURATE_VALUE);
        SHORT_TO_STRING_TYPE.put(SVGOMAnimatedEnumeration.createShort((short)3),
                                 SVG_HUE_ROTATE_VALUE);
        SHORT_TO_STRING_TYPE.put(SVGOMAnimatedEnumeration.createShort((short)4),
                                 SVG_LUMINANCE_TO_ALPHA_VALUE);
    }

    /**
     * The attribute-value map map.
     */
    protected static Map attributeValues = new HashMap(3);
    static {
        Map values = new HashMap(2);
        values.put(SVG_TYPE_ATTRIBUTE,  SVG_MATRIX_VALUE);
        attributeValues.put(null, values);
    }

    /**
     * The reference to the in attribute.
     */
    protected transient WeakReference inReference;

    /**
     * The reference to the type attribute.
     */
    protected transient WeakReference typeReference;

    /**
     * The reference to the values attribute.
     */
    protected transient WeakReference valuesReference;

    /**
     * Creates a new SVGOMFEColorMatrixElement object.
     */
    protected SVGOMFEColorMatrixElement() {
    }

    /**
     * Creates a new SVGOMFEColorMatrixElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEColorMatrixElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_COLOR_MATRIX_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEColorMatrixElement#getIn1()}.
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
     * SVGFEColorMatrixElement#getType()}.
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
     * SVGFEColorMatrixElement#getValues()}.
     */
    public SVGAnimatedNumberList getValues() {
	SVGAnimatedNumberList result;
	if (valuesReference == null ||
	    (result = (SVGAnimatedNumberList)valuesReference.get()) == null) {
            DefaultAttributeValueProducer davp;
            davp = new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    String s = getAttributeNS(null, SVG_TYPE_ATTRIBUTE);
                    if (s.equals(SVG_MATRIX_VALUE)) {
                        return "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 1 0";
                    } else if (s.equals(SVG_SATURATE_VALUE)) {
                        return "1";
                    } else if (s.equals(SVG_HUE_ROTATE_VALUE)) {
                        return "0";
                    } else {
                        return "";
                    }
                }
            };
	    result = new SVGOMAnimatedNumberList(this, null, SVG_VALUES_ATTRIBUTE, davp);
	    valuesReference = new WeakReference(result);
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
        return new SVGOMFEColorMatrixElement();
    }
}
