/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGFEPointLightElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEPointLightElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEPointLightElement
    extends    SVGOMElement
    implements SVGFEPointLightElement {

    /**
     * The reference to the x attribute.
     */
    protected WeakReference xReference;

    /**
     * The reference to the y attribute.
     */
    protected WeakReference yReference;

    /**
     * The reference to the z attribute.
     */
    protected WeakReference zReference;

    /**
     * Creates a new SVGOMFEPointLightElement object.
     */
    public SVGOMFEPointLightElement() {
    }

    /**
     * Creates a new SVGOMFEPointLightElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEPointLightElement(String prefix,
                                         AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return TAG_FE_POINT_LIGHT;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEPointLightElementElement#getX()}.
     */
    public SVGAnimatedNumber getX() {
	SVGAnimatedNumber result;
	if (xReference == null ||
	    (result = (SVGAnimatedNumber)xReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_X);
	    xReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEPointLightElementElement#getY()}.
     */
    public SVGAnimatedNumber getY() {
	SVGAnimatedNumber result;
	if (yReference == null ||
	    (result = (SVGAnimatedNumber)yReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_Y);
	    yReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEPointLightElementElement#getZ()}.
     */
    public SVGAnimatedNumber getZ() {
	SVGAnimatedNumber result;
	if (zReference == null ||
	    (result = (SVGAnimatedNumber)zReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_Z);
	    zReference = new WeakReference(result);
	}
	return result;
    }
}
