/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedNumber;

/**
 * This class implements {@link org.w3c.dom.svg.SVGAnimatedNumber}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimatedNumber implements SVGAnimatedNumber {
    /**
     * The associated element.
     */
    protected Element element;

    /**
     * The associated attribute namespace URI.
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
     * Creates a new SVGAnimatedNumber object.
     * @param elt The associated element.
     * @param nsURI The associated element namespace URI.
     * @param attr The associated attribute name.
     * @param def The default value producer.
     */
    public SVGOMAnimatedNumber(Element elt, String nsURI, String attr,
                               DefaultAttributeValueProducer def) {
	element = elt;
	attributeNsURI = nsURI;
	attributeName = attr;
        defaultValueProducer = def;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedNumber#getBaseVal()}.
     */
    public float getBaseVal() {
        Attr a = element.getAttributeNodeNS(attributeNsURI, attributeName);
        if (a != null) {
            return Float.parseFloat(a.getValue());
        } else if (defaultValueProducer != null) {
            return Float.parseFloat(defaultValueProducer.getDefaultAttributeValue());
        }
        return 0;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedNumber#setBaseVal(float)}.
     */
    public void setBaseVal(float baseVal) throws DOMException {
	element.setAttributeNS(attributeNsURI, attributeName,
                               Float.toString(baseVal));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedNumber#getAnimVal()}.
     */
    public float getAnimVal() {
	throw new RuntimeException(" !!! TODO: SVGOMAnimatedNumber.getAnimVal()");
    }
}
