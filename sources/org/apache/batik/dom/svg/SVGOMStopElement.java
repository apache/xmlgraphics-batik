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
import org.w3c.dom.svg.SVGStopElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGStopElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMStopElement
    extends    SVGStylableElement
    implements SVGStopElement {

    /**
     * Creates a new SVGOMStopElement object.
     */
    protected SVGOMStopElement() {
    }

    /**
     * Creates a new SVGOMStopElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMStopElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_STOP_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStopElement#getOffset()}.
     */
    public SVGAnimatedNumber getOffset() {
        throw new RuntimeException(" !!! TODO: getOffset()");
    }
    
    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMStopElement();
    }    
}
