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
	       ModificationHandler {

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
     * The default value producer.
     */
    protected DefaultAttributeValueProducer defaultValueProducer;

    /**
     * The baseVal attribute.
     */
    protected SVGOMLength baseVal;

    /**
     * Creates a new SVGOMAnimatedLength object.
     * @param elt The owner element.
     * @param nsURI The associated attribute namespace URI.
     * @param attr The associated attribute name.
     * @param def The default value producer.
     */
    public SVGOMAnimatedLength(SVGOMElement elt, String nsURI, String attr,
                               DefaultAttributeValueProducer def) {
	element = elt;
	attributeNsURI = nsURI;
	attributeName = attr;
        defaultValueProducer = def;
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
	    baseVal.setModificationHandler(this);
	    Attr a = element.getAttributeNodeNS(attributeNsURI, attributeName);
	    if (a != null) {
		baseVal.valueChanged(null, a);
	    } else if (defaultValueProducer != null) {
                baseVal.parseLength(defaultValueProducer.getDefaultAttributeValue());
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

    // ModificationHandler ///////////////////////////////////////////////

    /**
     * Implements {@link ModificationHandler#valueChanged(Object,String)}.
     */
    public void valueChanged(Object object, String value) {
	element.setAttributeNS(attributeNsURI, attributeName, value);
    }

    /**
     * Implements {@link ModificationHandler#getObject(Object,String)}.
     */
    public Object getObject(Object key) {
        return element;
    }
}
