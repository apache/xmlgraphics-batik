/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg;

import org.apache.batik.dom.AbstractDocument;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.extension.StylableExtensionElement;

/**
 * This class implements a star shape extension to sVG
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class BatikStarElement
    extends    StylableExtensionElement 
    implements BatikExtConstants {

    /**
     * The element prefix.
     */
    protected String prefix = null;

    /**
     * Creates a new BatikStarElement object.
     */
    protected BatikStarElement() {
    }

    /**
     * Creates a new BatikStarElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public BatikStarElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     */
    public String getNodeName() {
        return (prefix == null || prefix.equals(""))
            ? getLocalName() : prefix + ":" + getLocalName();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return BATIK_EXT_STAR_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
        return BATIK_EXT_NAMESPACE_URI;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#setPrefix(String)}.
     */
    public void setPrefix(String prefix) throws DOMException {
        if (isReadonly()) {
            throw createDOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "readonly.node",
                 new Object[] { new Integer(getNodeType()), getNodeName() });
        }

        if (prefix != null &&
            !prefix.equals("") &&
            !DOMUtilities.isValidName(prefix)) {
            throw createDOMException
                (DOMException.INVALID_CHARACTER_ERR, "prefix",
                 new Object[] { new Integer(getNodeType()), 
                                getNodeName(),
                                prefix });
        }

        this.prefix = prefix;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new BatikStarElement();
    }
}
