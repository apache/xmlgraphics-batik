/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.DOMException;

/**
 * This class implements a simple method for handling the node 'prefix'.
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas Deweese</a>
 * @version $Id$
 */
public abstract class PrefixableStylableExtensionElement
    extends StylableExtensionElement {

    /**
     * The element prefix.
     */
    protected String prefix = null;

    /**
     * Creates a new BatikStarElement object.
     */
    protected PrefixableStylableExtensionElement() {
    }

    /**
     * Creates a new BatikStarElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public PrefixableStylableExtensionElement(String prefix, 
                                              AbstractDocument owner) {
        super(prefix, owner);
        setPrefix(prefix);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     */
    public String getNodeName() {
        return (prefix == null || prefix.equals(""))
            ? getLocalName() : prefix + ":" + getLocalName();
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
}
