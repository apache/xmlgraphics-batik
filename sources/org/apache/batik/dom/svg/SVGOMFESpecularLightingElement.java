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
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFESpecularLightingElement;

/**
 * This class implements {@link SVGFESpecularLightingElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFESpecularLightingElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFESpecularLightingElement {

    /**
     * Creates a new SVGOMFESpecularLightingElement object.
     */
    protected SVGOMFESpecularLightingElement() {
    }

    /**
     * Creates a new SVGOMFESpecularLightingElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFESpecularLightingElement(String prefix,
                                          AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_SPECULAR_LIGHTING_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpecularLightingElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        throw new RuntimeException("!!! TODO getIn1()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpecularLightingElement#getSurfaceScale()}.
     */
    public SVGAnimatedNumber getSurfaceScale() {
        throw new RuntimeException("!!! TODO getSurfaceScale()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpecularLightingElement#getSpecularConstant()}.
     */
    public SVGAnimatedNumber getSpecularConstant() {
        throw new RuntimeException("!!! TODO getSpecularConstant()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpecularLightingElement#getSpecularExponent()}.
     */
    public SVGAnimatedNumber getSpecularExponent() {
        throw new RuntimeException("!!! TODO getSpecularExponent()");
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFESpecularLightingElement();
    }
}
