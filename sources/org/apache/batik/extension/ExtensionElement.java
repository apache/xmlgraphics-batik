/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension;

import org.apache.batik.css.ElementWithID;
import org.apache.batik.css.ElementWithPseudoClass;
import org.apache.batik.css.HiddenChildElement;
import org.apache.batik.css.HiddenChildElementSupport;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractElement;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class implements the basic features an element must have in order
 * to be usable as a foreign element within an SVGOMDocument.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class ExtensionElement
    extends    AbstractElement
    implements ElementWithID,
               ElementWithPseudoClass,
               HiddenChildElement {
    
    /**
     * The element ID attribute name.
     */
    protected final static String ID_NAME = "id";

    /**
     * The parent element.
     */
    protected transient Element parentElement;

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
    
    // HiddenChildElement //////////////////////////////////////////////////

    /**
     * The parent element of this element.
     */
    public Element getParentElement() {
        return parentElement;
    }

    /**
     * Sets the parent element.
     */
    public void setParentElement(Element elt) {
        parentElement = elt;
    }

    // ExtendedNode //////////////////////////////////////////////////

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

    // ElementWithID /////////////////////////////////////////////////

    /**
     * Sets the element ID attribute name.
     * @param uri The namespace uri.
     * @param s   The attribute local name.
     */
    public void setIDName(String uri, String s) {
        if (uri != null || s == null || !s.equals(ID_NAME)) {
	    throw createDOMException
		(DOMException.NO_MODIFICATION_ALLOWED_ERR,
		 "id.name",
		 new Object[] { s });
        }
    }

    /**
     * Returns the ID of this element or the empty string.
     */
    public String getID() {
        return getAttribute(ID_NAME);
    }

    // ElementWithPseudoClass ////////////////////////////////////////

    /**
     * Whether this element matches the given pseudo-class.
     * This methods supports the :first-child pseudo class.
     */
    public boolean matchPseudoClass(String pseudoClass) {
        if (pseudoClass.equals("first-child")) {
            Node n = getPreviousSibling();
            while (n != null && n.getNodeType() != ELEMENT_NODE) {
                n = n.getPreviousSibling();
            }
            return n == null;
        }
        return false;
    }

}
