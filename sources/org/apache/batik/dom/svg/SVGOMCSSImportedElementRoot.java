/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.css.engine.CSSImportedElementRoot;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractDocumentFragment;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class implements {@link org.w3c.dom.DocumentFragment} interface.
 * It is used to implement the SVG use element behavioUr.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMCSSImportedElementRoot
    extends    AbstractDocumentFragment
    implements CSSImportedElementRoot {

    /**
     * The parent CSS element.
     */
    protected Element cssParentElement;

    /**
     * Creates a new DocumentFragment object.
     */
    protected SVGOMCSSImportedElementRoot() {
    }

    /**
     * Creates a new DocumentFragment object.
     */
    public SVGOMCSSImportedElementRoot(AbstractDocument owner,
                                       Element parent) {
	ownerDocument = owner;
        cssParentElement = parent;
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

    // CSSImportedElementRoot ///////////////////////////////

    /**
     * Returns the parent of the imported element, from the CSS
     * point of view.
     */
    public Element getCSSParentElement() {
        return cssParentElement;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMCSSImportedElementRoot();
    }
}
