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
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGFEDistantLightElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEDistantLightElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEDistantLightElement
    extends    SVGOMElement
    implements SVGFEDistantLightElement {

    /**
     * The reference to the azimuth attribute.
     */
    protected WeakReference azimuthReference;

    /**
     * The reference to the elevation attribute.
     */
    protected WeakReference elevationReference;

    /**
     * Creates a new SVGOMFEDistantLightElement object.
     */
    protected SVGOMFEDistantLightElement() {
    }

    /**
     * Creates a new SVGOMFEDistantLightElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEDistantLightElement(String prefix,
                                         AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return TAG_FE_DISTANT_LIGHT;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEDistantLightElement#getAzimuth()}.
     */
    public SVGAnimatedNumber getAzimuth() {
	SVGAnimatedNumber result;
	if (azimuthReference == null ||
	    (result = (SVGAnimatedNumber)azimuthReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_AZIMUTH);
	    azimuthReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEDistantLightElement#getElevation()}.
     */
    public SVGAnimatedNumber getElevation() {
	SVGAnimatedNumber result;
	if (elevationReference == null ||
	    (result = (SVGAnimatedNumber)elevationReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_ELEVATION);
	    elevationReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEDistantLightElement();
    }
}
