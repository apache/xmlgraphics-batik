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
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGTransformList;

/**
 * This class implements {@link org.w3c.dom.svg.SVGAnimatedLength}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimatedTransformList
    implements SVGAnimatedTransformList,
               ModificationHandler {
    /**
     * The element this transform list is attached to.
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
    protected SVGOMTransformList baseVal;

    /**
     * Creates a new SVGOMAnimatedTransformList object.
     * @param elt The owner element.
     * @param nsURI The associated attribute namespace URI.
     * @param attr The associated attribute name.
     */
    public SVGOMAnimatedTransformList(SVGOMElement elt, String nsURI,
                                      String attr) {
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
     * org.w3c.dom.svg.SVGAnimatedTransformList#getBaseVal()}.
     */
    public SVGTransformList getBaseVal() {
	if (baseVal == null) {
	    baseVal = new SVGOMTransformList();
	    element.putLiveAttributeValue(attributeNsURI, attributeName,
                                          baseVal);
	    baseVal.setModificationHandler(this);
	    Attr a = element.getAttributeNodeNS(attributeNsURI, attributeName);
	    if (a != null) {
		baseVal.valueChanged(null, a);
	    }
	}
	return baseVal;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedTransformList#getAnimVal()}.
     */
    public SVGTransformList getAnimVal() {
	throw new RuntimeException(" !!! TODO: SVGAnimatedTransformList.getAnimVal()");
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
        return null;
    }
}
