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
        throw new RuntimeException("!!! TODO getX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getY()}.
     */
    public SVGAnimatedNumber getY() {
        throw new RuntimeException("!!! TODO getY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getZ()}.
     */
    public SVGAnimatedNumber getZ() {
        throw new RuntimeException("!!! TODO getZ()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtX()}.
     */
    public SVGAnimatedNumber getPointsAtX() {
        throw new RuntimeException("!!! TODO getPointsAtX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtY()}.
     */
    public SVGAnimatedNumber getPointsAtY() {
        throw new RuntimeException("!!! TODO getPointsAtY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtZ()}.
     */
    public SVGAnimatedNumber getPointsAtZ() {
        throw new RuntimeException("!!! TODO getPointsAtZ()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getSpecularExponent()}.
     */
    public SVGAnimatedNumber getSpecularExponent() {
        throw new RuntimeException("!!! TODO getSpecularExponent()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getLimitingConeAngle()}.
     */
    public SVGAnimatedNumber getLimitingConeAngle() {
        throw new RuntimeException("!!! TODO getLimitingConeAngle()");
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFESpotLightElement();
    }
}
