/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.w3c.dom.events.MutationEvent;

/**
 * This class implements the {@link org.w3c.dom.Attr} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractAttr extends AbstractParentNode implements Attr {
    /**
     * The name of this node.
     */
    protected String nodeName;

    /**
     * Whether this attribute was not specified in the original document.
     */
    protected boolean unspecified;

    /**
     * The owner element.
     */
    protected transient AbstractElement ownerElement;

    /**
     * Creates a new Attr object.
     */
    protected AbstractAttr() {
    }

    /**
     * Creates a new Attr object.
     * @param name  The attribute name for validation purposes.
     * @param owner The owner document.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: if name contains invalid characters,
     */
    protected AbstractAttr(String name, AbstractDocument owner)
        throws DOMException {
	ownerDocument = owner;
	if (!DOMUtilities.isValidName(name)) {
	    throw createDOMException(DOMException.INVALID_CHARACTER_ERR,
				     "xml.name",
				     new Object[] { name });
	}
    }

    /**
     * Sets the node name.
     */
    public void setNodeName(String v) {
	nodeName = v;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     * @return {@link #nodeName}.
     */
    public String getNodeName() {
	return nodeName;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeType()}.
     * @return {@link org.w3c.dom.Node#ATTRIBUTE_NODE}
     */
    public short getNodeType() {
	return ATTRIBUTE_NODE;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeValue()}.
     * @return The content of the attribute.
     */
    public String getNodeValue() throws DOMException {
        Node first = getFirstChild();
        if (first == null) {
            return "";
        }
        Node n = first.getNextSibling();
        if (n == null) {
            return first.getNodeValue();
        }
	StringBuffer result = new StringBuffer(first.getNodeValue());
	do {
	    result.append(n.getNodeValue());
            n = n.getNextSibling();
	} while (n != null);
	return result.toString();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#setNodeValue(String)}.
     */
    public void setNodeValue(String nodeValue) throws DOMException {
	if (isReadonly()) {
	    throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
				     "readonly.node",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
	}

        String s = getNodeValue();

	// Remove all the children
	Node n;
	while ((n = getFirstChild()) != null) {
	    removeChild(n);
	}

        String val = (nodeValue == null) ? "" : nodeValue;

	// Create and append a new child.
	n = getOwnerDocument().createTextNode(val);
	appendChild(n);

        if (ownerElement != null) {
            ownerElement.fireDOMAttrModifiedEvent(nodeName,
                                                  this,
                                                  s,
                                                  val,
                                                  MutationEvent.MODIFICATION);
        }
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Attr#getName()}.
     * @return {@link #getNodeName()}.
     */
    public String getName() {
	return getNodeName();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Attr#getSpecified()}.
     * @return !{@link #unspecified}.
     */
    public boolean getSpecified() {
	return !unspecified;
    }

    /**
     * Sets the specified attribute.
     */
    public void setSpecified(boolean v) {
	unspecified = !v;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Attr#getValue()}.
     * @return {@link #getNodeValue()}.
     */
    public String getValue() {
	return getNodeValue();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Attr#setValue(String)}.
     */
    public void setValue(String value) throws DOMException {
	setNodeValue(value);
    }

    /**
     * Sets the owner element.
     */
    public void setOwnerElement(AbstractElement v) {
	ownerElement = v;
    }
    
    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Attr#getOwnerElement()}.
     */
    public Element getOwnerElement() {
	return ownerElement;
    }

    /**
     * Exports this node to the given document.
     */
    protected Node export(Node n, AbstractDocument d) {
	super.export(n, d);
	AbstractAttr aa = (AbstractAttr)n;
	aa.nodeName = nodeName;
	aa.unspecified = false;
	return n;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	super.deepExport(n, d);
	AbstractAttr aa = (AbstractAttr)n;
	aa.nodeName = nodeName;
	aa.unspecified = false;
	return n;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	super.copyInto(n);
	AbstractAttr aa = (AbstractAttr)n;
	aa.nodeName = nodeName;
	aa.unspecified = unspecified;
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	super.deepCopyInto(n);
	AbstractAttr aa = (AbstractAttr)n;
	aa.nodeName = nodeName;
	aa.unspecified = unspecified;
	return n;
    }

    /**
     * Checks the validity of a node to be inserted.
     */
    protected void checkChildType(Node n) {
	switch (n.getNodeType()) {
	case TEXT_NODE:
	case ENTITY_REFERENCE_NODE:
	case DOCUMENT_FRAGMENT_NODE:
	    break;
	default:
	    throw createDOMException
                (DOMException.HIERARCHY_REQUEST_ERR,
                 "child.type",
                 new Object[] { new Integer(getNodeType()),
                                            getNodeName(),
                                new Integer(n.getNodeType()),
                                            n.getNodeName() });
	}
    }

    /**
     * Fires a DOMSubtreeModified event.
     */
    protected void fireDOMSubtreeModifiedEvent() {
	AbstractDocument doc = getCurrentDocument();
	if (doc.getEventsEnabled()) {
	    super.fireDOMSubtreeModifiedEvent();
	    if (getOwnerElement() != null) {
		((AbstractElement)getOwnerElement()).
                    fireDOMSubtreeModifiedEvent();
	    }
	}
    }
}
