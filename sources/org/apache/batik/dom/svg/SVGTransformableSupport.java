/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;

/**
 * To provide support for the SVGTransformable interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGTransformableSupport {
    /**
     * Creates a new SVGTransformable element.
     */
    public SVGTransformableSupport() {
    }
    
    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTransformable#getNearestViewportElement()}.
     */
    public SVGElement getNearestViewportElement(Element elt) {
	throw new RuntimeException(" !!! TODO: SVGTransformableSupport.getNearestViewportElement()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTransformable#getFarthestViewportElement()}.
     */
    public SVGElement getFarthestViewportElement(Element elt) {
	throw new RuntimeException(" !!! TODO: SVGTransformableSupport.getFarthestViewportElement()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTransformable#getTransform()}.
     */
    public SVGAnimatedTransformList getTransform(Element elt) {
	throw new RuntimeException(" !!! TODO: SVGTransformableSupport.getTransform()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTransformable#getBBox()}.
     */
    public SVGRect getBBox(Element elt) {
	throw new RuntimeException(" !!! TODO: SVGTransformableSupport.getBBox()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTransformable#getCTM()}.
     */
    public SVGMatrix getCTM(Element elt) {
	throw new RuntimeException(" !!! TODO: SVGTransformableSupport.getCTM()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTransformable#getScreenCTM()}.
     */
    public SVGMatrix getScreenCTM(Element elt) {
	throw new RuntimeException(" !!! TODO: SVGTransformableSupport.getScreenCTM()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTransformable#getTransformToElement(SVGElement)}.
     */
    public SVGMatrix getTransformToElement(SVGElement element, Element elt)
	throws SVGException {
	throw new RuntimeException(" !!! TODO: SVGTransformableSupport.getTransformToElement()");
    }
}
