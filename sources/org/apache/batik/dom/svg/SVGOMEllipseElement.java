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
import org.w3c.dom.svg.SVGEllipseElement;

/**
 * This class implements {@link SVGEllipseElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMEllipseElement
    extends    SVGGraphicsElement
    implements SVGEllipseElement {

    /**
     * Creates a new SVGOMEllipseElement object.
     */
    protected SVGOMEllipseElement() {
    }

    /**
     * Creates a new SVGOMEllipseElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMEllipseElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_ELLIPSE_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGEllipseElement#getCx()}.
     */
    public SVGAnimatedLength getCx() {
        throw new RuntimeException("!!! TODO: getCx()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGEllipseElement#getCy()}.
     */
    public SVGAnimatedLength getCy() {
        throw new RuntimeException("!!! TODO: getCy()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGEllipseElement#getRx()}.
     */
    public SVGAnimatedLength getRx() {
        throw new RuntimeException("!!! TODO: getRx()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGEllipseElement#getRy()}.
     */
    public SVGAnimatedLength getRy() {
        throw new RuntimeException("!!! TODO: getRy()");
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMEllipseElement();
    }
}
