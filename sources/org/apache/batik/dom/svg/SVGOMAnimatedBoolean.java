/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedBoolean;

/**
 * This class implements {@link org.w3c.dom.svg.SVGAnimatedBoolean}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimatedBoolean implements SVGAnimatedBoolean {
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
     * Creates a new SVGAnimatedBoolean object.
     * @param elt The associated element.
     * @param nsURI the associated attribute namespace URI.
     * @param attr The associated attribute name.
     */
    public SVGOMAnimatedBoolean(Element elt, String nsURI, String attr) {
	element = elt;
	attributeNsURI = nsURI;
	attributeName = attr;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedBoolean#getBaseVal()}.
     */
    public boolean getBaseVal() {
	String s = element.getAttributeNS(attributeNsURI, attributeName);
	return s.equals("true");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedBoolean#getBaseVal()}.
     */
    public void setBaseVal(boolean baseVal) throws DOMException {
	element.setAttributeNS(attributeNsURI, attributeName,
			       (baseVal)? "true" : "false");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedBoolean#getAnimVal()}.
     */
    public boolean getAnimVal() {
	throw new RuntimeException(" !!! TODO: SVGOMAnimatedBoolean.getAnimVal()");
    }
}
