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
import org.w3c.dom.svg.SVGCircleElement;

/**
 * This class implements {@link SVGCircleElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMCircleElement
    extends    SVGGraphicsElement
    implements SVGCircleElement {

    /**
     * Creates a new SVGOMCircleElement object.
     */
    protected SVGOMCircleElement() {
    }

    /**
     * Creates a new SVGOMCircleElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMCircleElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_CIRCLE_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGCircleElement#getCx()}.
     */
    public SVGAnimatedLength getCx() {
        throw new RuntimeException("!!! TODO: getCx()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGCircleElement#getCy()}.
     */
    public SVGAnimatedLength getCy() {
        throw new RuntimeException("!!! TODO: getCy()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGCircleElement#getR()}.
     */
    public SVGAnimatedLength getR() {
        throw new RuntimeException("!!! TODO: getR()");
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMCircleElement();
    }
}
