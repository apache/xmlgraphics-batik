/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.batik.css.ElementWithID;
import org.apache.batik.css.ElementWithPseudoClass;
import org.apache.batik.dom.AbstractAttr;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractElement;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.HashTable;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * This class implements the {@link org.w3c.dom.svg.SVGElement} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMElement
    extends    AbstractElement
    implements SVGElement,
               ElementWithID,
               ElementWithPseudoClass {
    /**
     * The element ID attribute name.
     */
    protected final static String ID_NAME = "id";

    /**
     * The { element name, attribute value map } map.
     */
    protected static Map elementDefaultValues = new HashMap(11);
    
    /**
     * Is this element immutable?
     */
    protected boolean readonly;

    /**
     * The element prefix.
     */
    protected String prefix = "";

    /**
     * The live attribute values map.
     */
    protected Map liveAttributeValues;

    /**
     * Creates a new Element object.
     */
    protected SVGOMElement() {
    }

    /**
     * Creates a new Element object.
     * @param prefix The namespace prefix.
     * @param owner  The owner document.
     */
    protected SVGOMElement(String prefix, AbstractDocument owner) {
        ownerDocument = owner;
        setPrefix(prefix);
	initializeAttributes();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGElement#getId()}.
     */
    public String getId() {
        return getID();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGElement#setId(String)}.
     */
    public void setId(String id) {
        setAttribute(ID_NAME, id);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGElement#getOwnerSVGElement()}.
     */
    public SVGSVGElement getOwnerSVGElement() {
        for (Node n = getParentNode(); n != null; n = n.getParentNode()) {
            if (n instanceof SVGSVGElement) {
                return (SVGSVGElement)n;
            }
        }
        return null;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGElement#getViewportElement()}.
     */
    public SVGElement getViewportElement() {
        for (Node n = getParentNode(); n != null; n = n.getParentNode()) {
            if (n instanceof SVGFitToViewBox) {
                return (SVGElement)n;
            }
        }
        return null;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     */
    public String getNodeName() {
        return (prefix == null || prefix.equals(""))
            ? getLocalName() : prefix + ":" + getLocalName();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
        return SVGDOMImplementation.SVG_NAMESPACE_URI;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#setPrefix(String)}.
     */
    public void setPrefix(String prefix) throws DOMException {
        if (isReadonly()) {
	    throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
				     "readonly.node",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
        }
        if (prefix != null &&
            !prefix.equals("") &&
            !DOMUtilities.isValidName(prefix)) {
	    throw createDOMException(DOMException.INVALID_CHARACTER_ERR,
				     "prefix",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName(),
						    prefix });
        }
        this.prefix = prefix;
    }

    // ExtendedNode //////////////////////////////////////////////////

    /**
     * Tests whether this node is readonly.
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * Sets this node readonly attribute.
     */
    public void setReadonly(boolean v) {
        readonly = v;
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

    /**
     * Creates the attribute list.
     */
    protected NamedNodeMap createAttributes() {
	return new SVGNamedNodeHashMap();
    }

    /**
     * Exports this node to the given document.
     */
    protected Node export(Node n, AbstractDocument d) {
	super.export(n, d);
	SVGOMElement ae = (SVGOMElement)n;
	ae.prefix = prefix;
	return n;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	super.deepExport(n, d);
	SVGOMElement ae = (SVGOMElement)n;
	ae.prefix = prefix;
	return n;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	super.copyInto(n);
	SVGOMElement ae = (SVGOMElement)n;
	ae.prefix = prefix;
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	super.deepCopyInto(n);
	SVGOMElement ae = (SVGOMElement)n;
	ae.prefix = prefix;
	return n;
    }

    /**
     * Registers a default attribute value.
     */
    protected static void registerDefaultAttributeValue(String element,
							String nsURI,
							String name,
							String value) {
	Map attrValues = (Map)elementDefaultValues.get(element);
	if (attrValues == null) {
	    elementDefaultValues.put(element, attrValues = new HashMap(11));
	}
	Map attrs = (Map)attrValues.get(nsURI);
	if (attrs == null) {
	    attrValues.put(nsURI, attrs = new HashMap(11));
	}
	attrs.put(name, value);
    }

    /**
     * Resets an attribute to the default value.
     * @return true if a default value is known for the given attribute.
     */
    protected boolean resetAttribute(String nsURI, String name) {
	Map m = (Map)elementDefaultValues.get(getLocalName());
	if (m == null) {
	    return false;
	}
	m = (Map)m.get(nsURI);
	if (m == null) {
	    return false;
	}
	Object value = m.get(name);
	if (value == null) {
	    return false;
	}
	setDefaultAttributeValue(nsURI, name, value);
	return true;
    }

    /**
     * Initializes the attributes of this element to their default value.
     */
    protected void initializeAttributes() {
	Map m = (Map)elementDefaultValues.get(getLocalName());
	if (m == null) {
	    return;
	}
	Iterator it = m.keySet().iterator();
	while (it.hasNext()) {
	    Object key = it.next();
	    Map n = (Map)m.get(key);
	    Iterator it2 = n.keySet().iterator();
	    while (it2.hasNext()) {
		Object key2 = it2.next();
		setDefaultAttributeValue(key, key2, n.get(key2));
	    }
	}
    }

    /**
     * An auxiliary method for initializeAttributes() and resetAttribute().
     */
    protected void setDefaultAttributeValue(Object nsURI,
					    Object name,
					    Object value) {
	if (attributes == null) {
	    attributes = createAttributes();
	}
	((SVGNamedNodeHashMap)attributes).setAttribute((String)nsURI,
						       (String)name,
						       (String)value);
    }

    /**
     * Puts a live attribute value into the table.
     * @param nsURI The attribute namespace uri.
     * @param attr The attribute name.
     * @param lav The LiveAttributeValue object to add.
     */
    protected void putLiveAttributeValue(String nsURI,
					 String attr,
					 LiveAttributeValue lav) {
	if (liveAttributeValues == null) {
	    liveAttributeValues = new HashMap(11);
	}
	HashMap hm = (HashMap)liveAttributeValues.get(nsURI);
	if (hm == null) {
	    liveAttributeValues.put(nsURI, hm = new HashMap(11));
	}
	hm.put(attr, new WeakReference(lav));
    }

    /**
     * An implementation of the {@link org.w3c.dom.NamedNodeMap}.
     */
    protected class SVGNamedNodeHashMap extends NamedNodeHashMap {
        /**
         * Creates a new SVGNamedNodeHashMap object.
         */
        public SVGNamedNodeHashMap() {
        }

	/**
	 * Adds a node to the map.
 	 */
	public Node setNamedItem(String name, Node arg)  throws DOMException {
	    Attr result = (Attr)super.setNamedItem(name, arg);

	    if (liveAttributeValues != null) {
		HashMap hm = (HashMap)liveAttributeValues.get(namespaceURI);
		if (hm != null) {
		    WeakReference wr = (WeakReference)hm.get(name);
		    LiveAttributeValue lav;
		    if (wr != null &&
                        (lav = (LiveAttributeValue)wr.get()) != null) {
			lav.valueChanged(result, (Attr)arg);
		    }
		}
	    }

	    return result;
	}

	/**
	 * Adds an unspecified attribute to the map.
	 */
	public void setAttribute(String nsURI, String name, String value) {
	    Attr attr = getOwnerDocument().createAttributeNS((String)nsURI,
							     (String)name);
	    attr.setValue((String)value);
	    ((AbstractAttr)attr).setSpecified(false);
	    if (nsURI == null) {
		setNamedItem(name, attr);
	    } else {
		if (tableNS == null) {
		    tableNS = new HashTable();
		}
		NamedNodeHashMap attrs = (NamedNodeHashMap)tableNS.get(nsURI);
		if (attrs == null) {
		    tableNS.put(nsURI, attrs = new NamedNodeHashMap());
		    attrs.namespaceURI = nsURI;
		}
		attrs.setNamedItem(attr.getLocalName(), attr);
	    }
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
		throw createDOMException
                    (DOMException.NOT_FOUND_ERR,
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
            
            // Reset the attribute to its default value
            if (!resetAttribute(this.namespaceURI, name)) {
                // Mutation event
                SVGOMElement.this.fireDOMAttrModifiedEvent(name, n.getNodeValue(), "");
            }
	    return n;
	}
    }
}
