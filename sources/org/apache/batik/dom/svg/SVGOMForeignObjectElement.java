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
import org.w3c.dom.svg.SVGForeignObjectElement;

/**
 * This class implements {@link SVGForeignObjectElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMForeignObjectElement
    extends    SVGGraphicsElement
    implements SVGForeignObjectElement {

    /**
     * Creates a new SVGOMForeignObjectElement object.
     */
    protected SVGOMForeignObjectElement() {
    }

    /**
     * Creates a new SVGOMForeignObjectElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMForeignObjectElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FOREIGN_OBJECT_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGForeignObjectElement#getX()}.
     */
    public SVGAnimatedLength getX() {
        throw new RuntimeException("!!! TODO: getX()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGForeignObjectElement#getY()}.
     */
    public SVGAnimatedLength getY() {
        throw new RuntimeException("!!! TODO: getY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGForeignObjectElement#getWidth()}.
     */
    public SVGAnimatedLength getWidth() {
        throw new RuntimeException("!!! TODO: getWidth()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGForeignObjectElement#getHeight()}.
     */
    public SVGAnimatedLength getHeight() {
        throw new RuntimeException("!!! TODO: getHeight()");
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMForeignObjectElement();
    }
}
