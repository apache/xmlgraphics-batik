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
import org.w3c.dom.svg.SVGTSpanElement;

/**
 * This class implements {@link SVGTSpanElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMTSpanElement
    extends    SVGOMTextPositioningElement
    implements SVGTSpanElement {

    /**
     * Creates a new SVGOMTSpanElement object.
     */
    protected SVGOMTSpanElement() {
    }

    /**
     * Creates a new SVGOMTSpanElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMTSpanElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_TSPAN_TAG;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMTSpanElement();
    }
}
