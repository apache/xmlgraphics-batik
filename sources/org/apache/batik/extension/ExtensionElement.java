/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGOMElement;

/**
 * This class implements the basic features an element must have in order
 * to be usable as a foreign element within an SVGOMDocument.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class ExtensionElement 
    extends SVGOMElement {

    /**
     * Creates a new Element object.
     */
    protected ExtensionElement() {
    }

    /**
     * Creates a new Element object.
     * @param name The element name, for validation purposes.
     * @param owner The owner document.
     */
    protected ExtensionElement(String name, AbstractDocument owner) {
        super(name, owner);
    }

    /**
     * Tests whether this node is readonly.
     */
    public boolean isReadonly() {
        return false;
    }

    /**
     * Sets this node readonly attribute.
     */
    public void setReadonly(boolean v) {
    }
}
