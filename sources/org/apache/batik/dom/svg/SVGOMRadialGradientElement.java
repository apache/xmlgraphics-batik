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
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGRadialGradientElement;

/**
 * This class implements {@link SVGRadialGradientElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMRadialGradientElement
    extends    SVGOMGradientElement
    implements SVGRadialGradientElement {

    /**
     * Creates a new SVGOMRadialGradientElement object.
     */
    protected SVGOMRadialGradientElement() {
    }

    /**
     * Creates a new SVGOMRadialGradientElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMRadialGradientElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_RADIAL_GRADIENT_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getCx()}.
     */
    public SVGAnimatedLength getCx() {
        throw new RuntimeException(" !!! TODO: getCx()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getCy()}.
     */
    public SVGAnimatedLength getCy() {
        throw new RuntimeException(" !!! TODO: getCy()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getR()}.
     */
    public SVGAnimatedLength getR() {
        throw new RuntimeException(" !!! TODO: getR()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getFx()}.
     */
    public SVGAnimatedLength getFx() {
        throw new RuntimeException(" !!! TODO: getFx()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRadialGradientElement#getFy()}.
     */
    public SVGAnimatedLength getFy() {
        throw new RuntimeException(" !!! TODO: getFy()");
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMRadialGradientElement();
    }
}
