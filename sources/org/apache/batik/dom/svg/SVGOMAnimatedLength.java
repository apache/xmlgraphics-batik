/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLength;

/**
 * This class implements {@link org.w3c.dom.svg.SVGAnimatedLength}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimatedLength
    implements SVGAnimatedLength,
	       AttributeModifier {
    /**
     * The element this length is attached to.
     */
    protected SVGOMElement element;

    /**
     * The asociated attribute namespace URI.
     */
    protected String attributeNsURI;

    /**
     * The associated attribute name.
     */
    protected String attributeName;

    /**
     * The baseVal attribute.
     */
    protected SVGOMLength baseVal;

    /**
     * Creates a new SVGOMAnimatedLength object.
     * @param elt The owner element.
     * @param nsURI The associated attribute namespace URI.
     * @param attr The associated attribute name.
     */
    public SVGOMAnimatedLength(SVGOMElement elt, String nsURI, String attr) {
	element = elt;
	attributeNsURI = nsURI;
	attributeName = attr;
    }

    /**
     * Returns the associated element.
     */
    public SVGElement getSVGElement() {
        return element;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedLength#getBaseVal()}.
     */
    public SVGLength getBaseVal() {
	if (baseVal == null) {
	    baseVal = new SVGOMLength();
	    element.putLiveAttributeValue(attributeNsURI, attributeName,
                                          baseVal);
	    baseVal.setAttributeModifier(this);
	    Attr a = element.getAttributeNodeNS(attributeNsURI, attributeName);
	    if (a != null) {
		baseVal.valueChanged(null, a);
	    }
	}
	return baseVal;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedLength#getAnimVal()}.
     */
    public SVGLength getAnimVal() {
	throw new RuntimeException(" !!! TODO: SVGAnimatedLength.getAnimVal()");
    }

    /**
     * Implements {@link AttributeModifier#setAttributeValue(String)}.
     */
    public void setAttributeValue(String value) {
	element.setAttributeNS(attributeNsURI, attributeName, value);
    }

    /**
     * Creates a DOM exception with a localized message.
     * @param type The DOM exception type.
     * @param key The key of the message in the resource bundle.
     * @param args The message arguments.
     */
    public DOMException createDOMException(short type, String key,
                                           Object[] args) {
        return element.createDOMException(type, key, args);
    }
}
