/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.HashTable;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.MutationEvent;

/**
 * This class implements the {@link org.w3c.dom.Element} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractElement
    extends    AbstractParentChildNode
    implements Element {

    /**
     * The attributes of this element.
     */
    protected NamedNodeMap attributes;

    /**
     * Creates a new AbstractElement object.
     */
    protected AbstractElement() {
    }

    /**
     * Creates a new AbstractElement object.
     * @param name  The element name for validation purposes.
     * @param owner The owner document.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: if name contains invalid characters,
     */
    protected AbstractElement(String name, AbstractDocument owner) {
	ownerDocument = owner;
	if (!DOMUtilities.isValidName(name)) {
	    throw createDOMException(DOMException.INVALID_CHARACTER_ERR,
				     "xml.name",
				     new Object[] { name });
	}
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeType()}.
     * @return {@link org.w3c.dom.Node#ELEMENT_NODE}
     */
    public short getNodeType() {
	return ELEMENT_NODE;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#hasAttributes()}.
     */
    public boolean hasAttributes() {
	return attributes != null && attributes.getLength() != 0;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getAttributes()}.
     */
    public NamedNodeMap getAttributes() {
	return (attributes == null)
            ? attributes = createAttributes()
            : attributes;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Element#getTagName()}.
     * @return {@link #getNodeName()}.
     */
    public String getTagName() {
	return getNodeName();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Element#hasAttribute(String)}.
     */
    public boolean hasAttribute(String name) {
	return attributes != null && attributes.getNamedItem(name) != null;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Element#getAttribute(String)}.
     */
    public String getAttribute(String name) {
	if (attributes == null) {
	    return "";
	}
	Attr attr = (Attr)attributes.getNamedItem(name);
	return (attr == null) ? "" : attr.getValue();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#setAttribute(String,String)}.
     */
    public void setAttribute(String name, String value) throws DOMException {
	if (attributes == null) {
	    attributes = createAttributes();
	}
	Attr attr = getOwnerDocument().createAttribute(name);
	attr.setValue(value);
	attributes.setNamedItem(attr);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#removeAttribute(String)}.
     */
    public void removeAttribute(String name) throws DOMException {
	if (attributes == null) {
	    throw createDOMException(DOMException.NOT_FOUND_ERR,
				     "attribute.missing",
				     new Object[] { name });
	}
	attributes.removeNamedItem(name);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#getAttributeNode(String)}.
     */
    public Attr getAttributeNode(String name) {
	if (attributes == null) {
	    return null;
	}
	return (Attr)attributes.getNamedItem(name);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#setAttributeNode(Attr)}.
     */
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
	if (newAttr == null) {
	    return null;
	}
	if (attributes == null) {
	    attributes = createAttributes();
	}
	return (Attr)attributes.setNamedItemNS(newAttr);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#removeAttributeNode(Attr)}.
     */
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
	if (oldAttr == null) {
	    return null;
	}
	if (attributes == null) {
	    throw createDOMException(DOMException.NOT_FOUND_ERR,
				     "attribute.missing",
				     new Object[] { oldAttr.getName() });
	}
	return (Attr)attributes.removeNamedItem(oldAttr.getNodeName());
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#getElementsByTagName(String)}.
     */
    public NodeList getElementsByTagName(String name) {
	Node n = getFirstChild();
	if (n == null || name == null) {
	    return EMPTY_NODE_LIST;
	}
	Nodes result = new Nodes();
	getElementsByTagName(this, name, result);
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#normalize()}.
     */
    public void normalize() {
	super.normalize();
	if (attributes != null) {
	    NamedNodeMap map = getAttributes();
	    for (int i = map.getLength() - 1; i >= 0; i--) {
		map.item(i).normalize();
	    }
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#hasAttributeNS(String,String)}.
     */
    public boolean hasAttributeNS(String namespaceURI, String localName) {
	return attributes != null &&
	       attributes.getNamedItemNS(namespaceURI, localName) != null;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#getAttributeNS(String,String)}.
     */
    public String getAttributeNS(String namespaceURI, String localName) {
	if (attributes == null) {
	    return "";
	}
	Attr attr = (Attr)attributes.getNamedItemNS(namespaceURI, localName);
	return (attr == null) ? "" : attr.getValue();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#setAttributeNS(String,String,String)}.
     */
    public void setAttributeNS(String namespaceURI, 
			       String qualifiedName, 
			       String value) throws DOMException {
	if (attributes == null) {
	    attributes = createAttributes();
	}
	Attr attr = getOwnerDocument().createAttributeNS(namespaceURI,
                                                         qualifiedName);
	attr.setValue(value);
	attributes.setNamedItemNS(attr);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#removeAttributeNS(String,String)}.
     */
    public void removeAttributeNS(String namespaceURI, 
				  String localName) throws DOMException {
	if (attributes == null) {
	    throw createDOMException(DOMException.NOT_FOUND_ERR,
				     "attribute.missing",
				     new Object[] { localName });
	}
	attributes.removeNamedItemNS(namespaceURI, localName);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#getAttributeNodeNS(String,String)}.
     */
    public Attr getAttributeNodeNS(String namespaceURI, 
				   String localName) {
	if (attributes == null) {
	    return null;
	}
	return (Attr)attributes.getNamedItemNS(namespaceURI, localName);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#setAttributeNodeNS(Attr)}.
     */
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
	if (newAttr == null) {
	    return null;
	}
	if (attributes == null) {
	    attributes = createAttributes();
	}
	return (Attr)attributes.setNamedItemNS(newAttr);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#getElementsByTagNameNS(String,String)}.
     */
    public NodeList getElementsByTagNameNS(String namespaceURI,
                                           String localName) {
	Node n = getFirstChild();
	if (n == null || localName == null) {
	    return EMPTY_NODE_LIST;
	}
	Nodes result = new Nodes();
	getElementsByTagNameNS(this, namespaceURI, localName, result);
	return result;
    }

    /**
     * Creates the attribute list.
     */
    protected NamedNodeMap createAttributes() {
	return new NamedNodeHashMap();
    }

    /**
     * An auxiliary method of getElementsByTagName.
     */
    protected static void getElementsByTagName(Node node, String name,
                                               Nodes list) {
	if (node.getNodeType() == ELEMENT_NODE) {
	    if (name.equals("*") || name.equals(node.getNodeName())) {
		list.append(node);
	    }
	}
	for (Node n = node.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
	    getElementsByTagName(n, name, list);
	}
    }

    /**
     * An auxiliary method for getElementsByTagNameNS.
     */
    protected static void getElementsByTagNameNS(Node   node,
						 String ns,
						 String name,
						 Nodes  list) {
	if (node.getNodeType() == ELEMENT_NODE) {
	    if (stringMatches(ns, node.getNamespaceURI()) &&
		(name.equals("*") || name.equals(node.getLocalName()))) {
		list.append(node);
	    }
	}
	for (Node n = node.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
	    getElementsByTagNameNS(n, ns, name, list);
	}
    }

    /**
     * String matching for getElementsByTagNameNS function.
     */
    private static boolean stringMatches(String s1, String s2) {
	if (s1 == null && s2 == null) {
	    return true;
	}
	if (s1 == null || s2 == null) {
	    return false;
	}
	if (s1.equals("*")) {
	    return true;
	}
	return s1.equals(s2);
    }

    /**
     * Exports this node to the given document.
     * @param n The clone node.
     * @param d The destination document.
     */
    protected Node export(Node n, AbstractDocument d) {
	super.export(n, d);
	AbstractElement ae = (AbstractElement)n;
	if (attributes != null) {
	    NamedNodeMap map = attributes;
	    for (int i = map.getLength() - 1; i >= 0; i--) {
		AbstractAttr aa = (AbstractAttr)map.item(i);
		if (aa.getSpecified()) {
		    Attr attr = (Attr)aa.deepExport(aa.cloneNode(false), d);
		    if (aa instanceof AbstractAttrNS) {
			ae.setAttributeNodeNS(attr);
		    } else {
			ae.setAttributeNode(attr);
		    }
		}
	    }
	}
	return n;
    }

    /**
     * Deeply exports this node to the given document.
     * @param n The clone node.
     * @param d The destination document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	super.deepExport(n, d);
	AbstractElement ae = (AbstractElement)n;
	if (attributes != null) {
	    NamedNodeMap map = attributes;
	    for (int i = map.getLength() - 1; i >= 0; i--) {
		AbstractAttr aa = (AbstractAttr)map.item(i);
		if (aa.getSpecified()) {
		    Attr attr = (Attr)aa.deepExport(aa.cloneNode(false), d);
		    if (aa instanceof AbstractAttrNS) {
			ae.setAttributeNodeNS(attr);
		    } else {
			ae.setAttributeNode(attr);
		    }
		}
	    }
	}
	return n;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	super.copyInto(n);
	AbstractElement ae = (AbstractElement)n;
	if (attributes != null) {
	    NamedNodeMap map = attributes;
	    for (int i = map.getLength() - 1; i >= 0; i--) {
		AbstractAttr aa = (AbstractAttr)map.item(i).cloneNode(false);
		if (aa instanceof AbstractAttrNS) {
		    ae.setAttributeNodeNS(aa);
		} else {
		    ae.setAttributeNode(aa);
		}
	    }
	}
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	super.deepCopyInto(n);
	AbstractElement ae = (AbstractElement)n;
	if (attributes != null) {
	    NamedNodeMap map = attributes;
	    for (int i = map.getLength() - 1; i >= 0; i--) {
		AbstractAttr aa = (AbstractAttr)map.item(i).cloneNode(true);
		if (aa instanceof AbstractAttrNS) {
		    ae.setAttributeNodeNS(aa);
		} else {
		    ae.setAttributeNode(aa);
		}
	    }
	}
	return n;
    }

    /**
     * Checks the validity of a node to be inserted.
     * @param n The node to be inserted.
     */
    protected void checkChildType(Node n) {
	switch (n.getNodeType()) {
	case ELEMENT_NODE:
	case PROCESSING_INSTRUCTION_NODE:
	case COMMENT_NODE:
	case TEXT_NODE:
	case CDATA_SECTION_NODE:
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
     * Fires a DOMAttrModified event.
     * <!> WARNING: public accessor because of compilation problems
     *     on Solaris. Do not change.
     *
     * @param name The attribute name.
     * @param oldv The old value of the attribute.
     * @param newv The new value of the attribute.
     */
    public void fireDOMAttrModifiedEvent(String name, String oldv,
                                            String newv) {
	AbstractDocument doc = getCurrentDocument();
	if (doc.getEventsEnabled() && !oldv.equals(newv)) {
	    DocumentEvent de = (DocumentEvent)doc;
	    MutationEvent ev = (MutationEvent)de.createEvent("MutationEvents");
	    ev.initMutationEvent("DOMAttrModified",
				 true,  // canBubbleArg
				 false, // cancelableArg
				 null,  // relatedNodeArg
				 oldv,  // prevValueArg
				 newv,  // newValueArg
				 name,  // attrNameArg
                                 ev.MODIFICATION);
	    dispatchEvent(ev);
	}
    }

    /**
     * An implementation of the {@link org.w3c.dom.NamedNodeMap}.
     */
    public class NamedNodeHashMap implements NamedNodeMap, Serializable {

	/**
	 * The place where the nodes in the anonymous namespace are stored.
	 */
	protected HashTable table = new HashTable();

	/**
	 * The place where the the nodes that use namespaces are stored.
	 */
	protected HashTable tableNS;

	/**
	 * The current namespace URI.
	 */
	public String namespaceURI;

	/**
	 * Creates a new NamedNodeHashMap object.
	 */
	public NamedNodeHashMap() {
	}

	/**
	 * <b>DOM</b>: Implements {@link
         * org.w3c.dom.NamedNodeMap#getNamedItem(String)}.
	 */
	public Node getNamedItem(String name) {
	    if (name == null) {
		return null;
	    }
	    return (Node)table.get(name);
	}

	/**
	 * <b>DOM</b>: Implements {@link
         * org.w3c.dom.NamedNodeMap#setNamedItem(Node)}.
	 */
	public Node setNamedItem(Node arg) throws DOMException {
	    if (arg == null) {
		return null;
	    }
	    checkNode(arg);

	    return setNamedItem(arg.getNodeName(), arg);
	}

	/**
	 * <b>DOM</b>: Implements {@link
	 * org.w3c.dom.NamedNodeMap#removeNamedItem(String)}.
	 */
	public Node removeNamedItem(String name) throws DOMException {
	    if (isReadonly()) {
		throw createDOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                     "readonly.node.map",
                     new Object[] {});
	    }
	    if (name == null) {
		throw createDOMException(DOMException.NOT_FOUND_ERR,
					 "attribute.missing",
					 new Object[] { "" });
	    }
	    AbstractAttr n = (AbstractAttr)table.remove(name);
	    if (n == null) {
		throw createDOMException(DOMException.NOT_FOUND_ERR,
					 "attribute.missing",
					 new Object[] { name });
	    }
	    n.setOwnerElement(null);
	    
	    // Mutation event
	    fireDOMAttrModifiedEvent(name, n.getNodeValue(), "");
	    return n;
	}

	/**
	 * <b>DOM</b>: Implements {@link org.w3c.dom.NamedNodeMap#item(int)}.
	 */
	public Node item(int index) {
	    int i = table.size();
	    if (index < i) {
		return (Node)table.item(index);
	    }
	    index -= i;
	    if (tableNS != null) {
		for (int k = 0; k < tableNS.size(); k++) {
		    NamedNodeHashMap hm = (NamedNodeHashMap)tableNS.item(k);
		    i = hm.getLength();
		    if (index < i) {
			return (Node)hm.item(index);
		    }
		    index -= i;
		}
	    }
	    return null;
	}

	/**
	 * <b>DOM</b>: Implements {@link org.w3c.dom.NamedNodeMap#getLength()}.
	 */
	public int getLength() {
	    int result = table.size();
	    if (tableNS != null) {
		for (int i = 0; i < tableNS.size(); i++) {
		    NamedNodeHashMap hm = (NamedNodeHashMap)tableNS.item(i);
		    result += hm.getLength();
		}
	    }
	    return result;
	}

	/**
	 * <b>DOM</b>: Implements {@link
	 * org.w3c.dom.NamedNodeMap#getNamedItemNS(String,String)}.
	 */
	public Node getNamedItemNS(String namespaceURI, String localName) {
	    if (namespaceURI == null) {
		return getNamedItem(localName);
	    }
	    if (tableNS == null) {
		return null;
	    }
	    NamedNodeHashMap attr;
            attr = (NamedNodeHashMap)tableNS.get(namespaceURI);
	    if (attr == null) {
		return null;
	    }
	    return attr.getNamedItem(localName);
	}

	/**
	 * <b>DOM</b>: Implements {@link
         * org.w3c.dom.NamedNodeMap#setNamedItemNS(Node)}.
	 */
	public Node setNamedItemNS(Node arg) throws DOMException {
	    if (arg == null) {
		return null;
	    }
	    String nsURI = arg.getNamespaceURI();
	    if (nsURI == null) {
		return setNamedItem(arg);
	    }
	    checkNode(arg);

	    if (tableNS == null) {
		tableNS = new HashTable();
	    }
	    NamedNodeHashMap attr = (NamedNodeHashMap)tableNS.get(nsURI);
	    if (attr == null) {
		tableNS.put(nsURI, attr = new NamedNodeHashMap());
		attr.namespaceURI = nsURI;
	    }
	    return attr.setNamedItem(arg.getLocalName(), arg);
	}

	/**
	 * <b>DOM</b>: Implements {@link
	 * org.w3c.dom.NamedNodeMap#removeNamedItemNS(String,String)}.
	 */
	public Node removeNamedItemNS(String namespaceURI, String localName)
	    throws DOMException {
	    if (namespaceURI == null) {
		return removeNamedItem(localName);
	    }
	    if (isReadonly()) {
		throw createDOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                     "readonly.node.map",
                     new Object[] {});
	    }
	    if (localName == null || tableNS == null) {
		throw createDOMException(DOMException.NOT_FOUND_ERR,
					 "attribute.missing",
					 new Object[] { localName });
	    }
	    NamedNodeHashMap attr;
            attr = (NamedNodeHashMap)tableNS.get(namespaceURI);
	    if (attr == null) {
		throw createDOMException(DOMException.NOT_FOUND_ERR,
					 "attribute.missing",
					 new Object[] { localName });
	    }
	    return attr.removeNamedItem(localName);
	}

	/**
	 * Adds a node to the map.
 	 */
	public Node setNamedItem(String name, Node arg)  throws DOMException {
	    ((AbstractAttr)arg).setOwnerElement(AbstractElement.this);
	    AbstractAttr result = (AbstractAttr)table.put(name, arg);

	    if (result != null) {
		result.setOwnerElement(null);
		fireDOMAttrModifiedEvent(name,
					 result.getNodeValue(),
					 arg.getNodeValue());
	    } else {
		fireDOMAttrModifiedEvent(name,
					 "",
					 arg.getNodeValue());
	    }
	    return result;
	}

	/**
	 * Checks the validity of a node to add.
	 */
	protected void checkNode(Node arg) {
	    if (isReadonly()) {
		throw createDOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                     "readonly.node.map",
                     new Object[] {});
	    }
	    if (getOwnerDocument() != arg.getOwnerDocument()) {
		throw createDOMException(DOMException.WRONG_DOCUMENT_ERR,
					 "node.from.wrong.document",
					 new Object[] { new Integer
                                                         (arg.getNodeType()),
		                                        arg.getNodeName() });
	    }
	    if (arg.getNodeType() == ATTRIBUTE_NODE &&
		((Attr)arg).getOwnerElement() != null) {
		throw createDOMException(DOMException.WRONG_DOCUMENT_ERR,
					 "inuse.attribute",
					 new Object[] { arg.getNodeName() });
	    }
	}

        // Serialization ///////////////////////////////////////////////////

        // A standard write is enough.

        /**
         * Reads the object from the given stream.
         */
        private void readObject(ObjectInputStream s) 
            throws IOException, ClassNotFoundException {
            s.defaultReadObject();

            int len = getLength();
            for (int i = len - 1; i >= 0; i--) {
                AbstractAttr attr = (AbstractAttr)item(i);
                attr.setOwnerDocument(AbstractElement.this.getOwnerDocument());
                attr.setOwnerElement(AbstractElement.this);
            }
        }
    }
}
