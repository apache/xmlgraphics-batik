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
import org.w3c.dom.svg.SVGFEPointLightElement;

/**
 * This class implements {@link SVGFEPointLightElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEPointLightElement
    extends    SVGOMElement
    implements SVGFEPointLightElement {

    /**
     * Creates a new SVGOMFEPointLightElement object.
     */
    protected SVGOMFEPointLightElement() {
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
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_POINT_LIGHT_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEPointLightElement#getX()}.
     */
    public SVGAnimatedNumber getX() {
        return getAnimatedNumberAttribute(null, SVG_X_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEPointLightElement#getY()}.
     */
    public SVGAnimatedNumber getY() {
        return getAnimatedNumberAttribute(null, SVG_Y_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEPointLightElement#getZ()}.
     */
    public SVGAnimatedNumber getZ() {
        return getAnimatedNumberAttribute(null, SVG_Y_ATTRIBUTE, 0f);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEPointLightElement();
    }
}
