/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGFESpotLightElement;

/**
 * This class implements {@link SVGFESpotLightElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFESpotLightElement
    extends    SVGOMElement
    implements SVGFESpotLightElement {

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
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_SPOT_LIGHT_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getX()}.
     */
    public SVGAnimatedNumber getX() {
        return getAnimatedNumberAttribute(null, SVG_X_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getY()}.
     */
    public SVGAnimatedNumber getY() {
        return getAnimatedNumberAttribute(null, SVG_Y_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getZ()}.
     */
    public SVGAnimatedNumber getZ() {
        return getAnimatedNumberAttribute(null, SVG_Z_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtX()}.
     */
    public SVGAnimatedNumber getPointsAtX() {
        return getAnimatedNumberAttribute(null, SVG_POINTS_AT_X_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtY()}.
     */
    public SVGAnimatedNumber getPointsAtY() {
        return getAnimatedNumberAttribute(null, SVG_POINTS_AT_Y_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtZ()}.
     */
    public SVGAnimatedNumber getPointsAtZ() {
        return getAnimatedNumberAttribute(null, SVG_POINTS_AT_Z_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpotLightElement#getSpecularExponent()}.
     */
    public SVGAnimatedNumber getSpecularExponent() {
        return getAnimatedNumberAttribute
            (null, SVG_SPECULAR_EXPONENT_ATTRIBUTE, 1f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpotLightElement#getLimitingConeAngle()}.
     */
    public SVGAnimatedNumber getLimitingConeAngle() {
        return getAnimatedNumberAttribute
            (null, SVG_LIMITING_CONE_ANGLE_ATTRIBUTE, 0f);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFESpotLightElement();
    }
}
