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
import org.w3c.dom.svg.SVGFESpotLightElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFESpotLightElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFESpotLightElement
    extends    SVGOMElement
    implements SVGFESpotLightElement {

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
     * The reference to the pointsAtX attribute.
     */
    protected WeakReference pointsAtXReference;

    /**
     * The reference to the pointsAtY attribute.
     */
    protected WeakReference pointsAtYReference;

    /**
     * The reference to the pointsAtZ attribute.
     */
    protected WeakReference pointsAtZReference;

    /**
     * The reference to the specularExponent attribute.
     */
    protected WeakReference specularExponentReference;

    /**
     * The reference to the limitingConeAngle attribute.
     */
    protected WeakReference limitingConeAngleReference;

    /**
     * Creates a new SVGOMFESpotLightElement object.
     */
    protected SVGOMFESpotLightElement() {
    }

    /**
     * Creates a new SVGOMFESpotLightElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFESpotLightElement(String prefix,
                                   AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return TAG_FE_SPOT_LIGHT;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getX()}.
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
     * org.w3c.dom.svg.SVGFESpotLightElement#getY()}.
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
     * org.w3c.dom.svg.SVGFESpotLightElement#getZ()}.
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

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getPointsAtX()}.
     */
    public SVGAnimatedNumber getPointsAtX() {
	SVGAnimatedNumber result;
	if (pointsAtXReference == null ||
	    (result = (SVGAnimatedNumber)pointsAtXReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_POINTS_AT_X);
	    pointsAtXReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getPointsAtY()}.
     */
    public SVGAnimatedNumber getPointsAtY() {
	SVGAnimatedNumber result;
	if (pointsAtYReference == null ||
	    (result = (SVGAnimatedNumber)pointsAtYReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_POINTS_AT_Y);
	    pointsAtYReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getPointsAtZ()}.
     */
    public SVGAnimatedNumber getPointsAtZ() {
	SVGAnimatedNumber result;
	if (pointsAtZReference == null ||
	    (result = (SVGAnimatedNumber)pointsAtZReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_POINTS_AT_Z);
	    pointsAtZReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getSpecularExponent()}.
     */
    public SVGAnimatedNumber getSpecularExponent() {
	SVGAnimatedNumber result;
	if (specularExponentReference == null ||
	    (result = (SVGAnimatedNumber)specularExponentReference.get()) ==
            null) {
	    result = new SVGOMAnimatedNumber(this, null,
                                             ATTR_SPECULAR_EXPONENT);
	    specularExponentReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getLimitingConeAngle()}.
     */
    public SVGAnimatedNumber getLimitingConeAngle() {
	SVGAnimatedNumber result;
	if (limitingConeAngleReference == null ||
	    (result = (SVGAnimatedNumber)limitingConeAngleReference.get()) ==
            null) {
	    result = new SVGOMAnimatedNumber(this, null,
                                             ATTR_LIMITING_CONE_ANGLE);
	    limitingConeAngleReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFESpotLightElement();
    }
}
