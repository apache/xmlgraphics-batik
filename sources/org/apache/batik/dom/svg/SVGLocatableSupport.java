/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;

/**
 * This class provides support for the SVGLocatable interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGLocatableSupport {
    /**
     * Creates a new SVGLocatable element.
     */
    public SVGLocatableSupport() {
    }
    
    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getNearestViewportElement()}.
     */
    public static SVGElement getNearestViewportElement(Element elt) {
	throw new RuntimeException(" !!! TODO: getNearestViewportElement()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getFarthestViewportElement()}.
     */
    public static SVGElement getFarthestViewportElement(Element elt) {
	throw new RuntimeException(" !!! TODO: getFarthestViewportElement()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getBBox()}.
     */
    public static SVGRect getBBox(Element elt) {
	throw new RuntimeException(" !!! TODO: getBBox()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getCTM()}.
     */
    public static SVGMatrix getCTM(Element elt) {
	throw new RuntimeException(" !!! TODO: getCTM()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getScreenCTM()}.
     */
    public static SVGMatrix getScreenCTM(Element elt) {
	throw new RuntimeException(" !!! TODO: getScreenCTM()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGLocatable#getTransformToElement(SVGElement)}.
     */
    public static SVGMatrix getTransformToElement(SVGElement element, Element elt)
	throws SVGException {
	throw new RuntimeException(" !!! TODO: getTransformToElement()");
    }
}
