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

/**
 * This is a development only class. It is used temporarily in the
 * SVG DOM implementation for SVG elements whose DOM support has not
 * been put in
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGOMToBeImplementedElement
    extends SVGGraphicsElement {
    
    /**
     * This element's local name
     */
    private String localName;

    /**
     * Creates a new SVGOMToBeImplementedElement object.
     */
    protected SVGOMToBeImplementedElement() {
    }

    /**
     * Creates a new SVGOMToBeImplementedElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     * @param localName the local name for the element.
     */
    public SVGOMToBeImplementedElement(String prefix, AbstractDocument owner,
                                       String localName) {
        super(prefix, owner);
        this.localName = localName;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMToBeImplementedElement();
    }
}
