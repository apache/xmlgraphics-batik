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
import org.w3c.dom.svg.SVGFEDistantLightElement;

/**
 * This class implements {@link SVGFEDistantLightElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEDistantLightElement
    extends    SVGOMElement
    implements SVGFEDistantLightElement {

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
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_DISTANT_LIGHT_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEDistantLightElement#getAzimuth()}.
     */
    public SVGAnimatedNumber getAzimuth() {
        throw new RuntimeException("!!! TODO: getAzimuth");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEDistantLightElement#getElevation()}.
     */
    public SVGAnimatedNumber getElevation() {
        throw new RuntimeException("!!! TODO: getElevation()");
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEDistantLightElement();
    }
}
