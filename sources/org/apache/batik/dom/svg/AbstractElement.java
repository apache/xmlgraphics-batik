/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.ElementWithBaseURI;
import org.apache.batik.css.ElementWithID;
import org.apache.batik.css.ElementWithPseudoClass;
import org.apache.batik.css.HiddenChildElement;
import org.apache.batik.css.HiddenChildElementSupport;

import org.apache.batik.dom.AbstractAttr;
import org.apache.batik.dom.AbstractDocument;

import org.apache.batik.dom.events.NodeEventTarget;

import org.apache.batik.util.SoftDoublyIndexedTable;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.w3c.dom.events.MutationEvent;

/**
 * This class provides a superclass to implement an SVG element, or
 * an element interoperable with the SVG elements.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractElement
    extends org.apache.batik.dom.AbstractElement
    implements ElementWithBaseURI,
               ElementWithID,
               ElementWithPseudoClass,
               HiddenChildElement {
    
    /**
     * The parent element.
     */
    protected transient Element parentElement;

    /**
     * The live attribute values.
     */
    protected transient SoftDoublyIndexedTable liveAttributeValues;

    /**
     * The cascaded style, if any.
     */
    protected transient CSSOMReadOnlyStyleDeclaration cascadedStyle;

    /**
     * Creates a new Element object.
     */
    protected AbstractElement() {
    }

    /**
     * Creates a new Element object.
     * @param prefix The namespace prefix.
     * @param owner  The owner document.
     */
    protected AbstractElement(String prefix, AbstractDocument owner) {
        ownerDocument = owner;
        setPrefix(prefix);
	initializeAttributes();
    }

    // ElementWithBaseURI ////////////////////////////////////////////

    /**
     * Returns this element's base URI.
     */
    public String getBaseURI() {
        try {
            return new java.net.URL(((SVGOMDocument)ownerDocument).getURLObject(),
                                    XMLBaseSupport.getXMLBase(this)).toString();
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // ElementWithID /////////////////////////////////////////////////

    /**
     * Sets the element ID attribute name.
     * @param uri The namespace uri.
     * @param s   The attribute local name.
     */
    public void setIDName(String uri, String s) {
        if (uri != null || s == null || !s.equals("id")) {
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
        return getAttribute("id");
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

    // HiddenChildElement ////////////////////////////////////////////

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

    /**
     * Sets the cascaded style of this element.
     */
    public CSSOMReadOnlyStyleDeclaration getCascadedStyle() {
        return cascadedStyle;
    }

    /**
     * Sets the cascaded style of this element.
     */
    public void setCascadedStyle(CSSOMReadOnlyStyleDeclaration sd) {
        cascadedStyle = sd;
    }

    /**
     * Implements {@link NodeEventTarget#getParentNodeEventTarget()}.
     */
    public NodeEventTarget getParentNodeEventTarget() {
        return (NodeEventTarget)
            HiddenChildElementSupport.getParentElement(this);
    }

    // Attributes /////////////////////////////////////////////////////////

    /**
     * Returns the live attribute value associated with given attribute, if any.
     * @param ns The attribute's namespace.
     * @param ln The attribute's local name.
     */
    public LiveAttributeValue getLiveAttributeValue(String ns, String ln) {
        if (liveAttributeValues == null) {
            return null;
        }
        return (LiveAttributeValue)liveAttributeValues.get(ns, ln);
    }

    /**
     * Associates a live attribute value to this element.
     * @param ns The attribute's namespace.
     * @param ln The attribute's local name.
     * @param val The live value.
     */
    public void putLiveAttributeValue(String ns, String ln, LiveAttributeValue val) {
        if (liveAttributeValues == null) {
            liveAttributeValues = new SoftDoublyIndexedTable();
        }
        liveAttributeValues.put(ns, ln, val);
    }

    /**
     * Returns the AttributeInitializer for this element type.
     * @return null if this element has no attribute with a default value.
     */
    protected AttributeInitializer getAttributeInitializer() {
        return null;
    }

    /**
     * Initializes the attributes of this element to their default value.
     */
    protected void initializeAttributes() {
        AttributeInitializer ai = getAttributeInitializer();
        if (ai != null) {
            ai.initializeAttributes(this);
        }
    }

    /**
     * Resets an attribute to the default value.
     * @return true if a default value is known for the given attribute.
     */
    protected boolean resetAttribute(String ns, String prefix, String ln) {
        AttributeInitializer ai = getAttributeInitializer();
        if (ai == null) {
            return false;
        }
        return ai.resetAttribute(this, ns, prefix, ln);
    }

    /**
     * Creates the attribute list.
     */
    protected NamedNodeMap createAttributes() {
	return new ExtendedNamedNodeHashMap();
    }

    /**
     * Sets an unspecified attribute.
     * @param nsURI The attribute namespace URI.
     * @param name The attribute's qualified name.
     * @param value The attribute's default value.
    */
    public void setUnspecifiedAttribute(String nsURI, String name, String value) {
	if (attributes == null) {
	    attributes = createAttributes();
	}
        ((ExtendedNamedNodeHashMap)attributes).
            setUnspecifiedAttribute(nsURI, name, value);
    }

    /**
     * Called when an attribute has been added.
     */
    protected void attrAdded(Attr node, String newv) {
        LiveAttributeValue lav = getLiveAttributeValue(node);
        if (lav != null) {
            lav.attrAdded(node, newv);
        }
    }

    /**
     * Called when an attribute has been modified.
     */
    protected void attrModified(Attr node, String oldv, String newv) {
        LiveAttributeValue lav = getLiveAttributeValue(node);
        if (lav != null) {
            lav.attrModified(node, oldv, newv);
        }
    }

    /**
     * Called when an attribute has been removed.
     */
    protected void attrRemoved(Attr node, String oldv) {
        LiveAttributeValue lav = getLiveAttributeValue(node);
        if (lav != null) {
            lav.attrRemoved(node, oldv);
        }
    }

    /**
     * Gets Returns the live attribute value associated with given attribute, if any.
     */
    private LiveAttributeValue getLiveAttributeValue(Attr node) {
        String ns = node.getNamespaceURI();
        return getLiveAttributeValue(ns, (ns == null)
                                     ? node.getNodeName()
                                     : node.getLocalName());
    }

    /**
     * An implementation of the {@link NamedNodeMap}.
     */
    protected class ExtendedNamedNodeHashMap extends NamedNodeHashMap {

        /**
         * Creates a new ExtendedNamedNodeHashMap object.
         */
        public ExtendedNamedNodeHashMap() {
        }

	/**
	 * Adds an unspecified attribute to the map.
         * @param nsURI The attribute namespace URI.
         * @param name The attribute's qualified name.
         * @param value The attribute's default value.
	 */
	public void setUnspecifiedAttribute(String nsURI, String name, String value) {
	    Attr attr = getOwnerDocument().createAttributeNS(nsURI, name);
	    attr.setValue(value);
	    ((AbstractAttr)attr).setSpecified(false);
            setNamedItemNS(attr);
	}

 	/**
	 * <b>DOM</b>: Implements {@link NamedNodeMap#removeNamedItemNS(String,String)}.
	 */
	public Node removeNamedItemNS(String namespaceURI, String localName)
	    throws DOMException {
	    if (isReadonly()) {
		throw createDOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR,
                     "readonly.node.map",
                     new Object[] {});
	    }
	    if (localName == null) {
		throw createDOMException(DOMException.NOT_FOUND_ERR,
					 "attribute.missing",
					 new Object[] { "" });
	    }
	    AbstractAttr n = (AbstractAttr)remove(namespaceURI, localName);
	    if (n == null) {
		throw createDOMException(DOMException.NOT_FOUND_ERR,
					 "attribute.missing",
					 new Object[] { localName });
	    }
	    n.setOwnerElement(null);
            String prefix = n.getPrefix();
	    
            // Reset the attribute to its default value
            if (!resetAttribute(namespaceURI, prefix, localName)) {
                // Mutation event
                fireDOMAttrModifiedEvent(n.getNodeName(), n, n.getNodeValue(), "",
                                         MutationEvent.REMOVAL);
            }
            return n;
	}
    }
}
