/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.css.HiddenChildElementSupport;

import org.apache.batik.dom.AbstractDocument;

import org.apache.batik.dom.util.DOMUtilities;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLConstants;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * This class implements the {@link SVGElement} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMElement
    extends    AbstractElement
    implements SVGElement,
               SVGConstants {

    /**
     * Is this element immutable?
     */
    protected transient boolean readonly;

    /**
     * The element prefix.
     */
    protected String prefix;

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
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getId()}.
     */
    public String getId() {
        return getID();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#setId(String)}.
     */
    public void setId(String id) {
        setAttributeNS(null, "id", id);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getXMLbase()}.
     */
    public String getXMLbase() {
        return XMLBaseSupport.getXMLBase(this);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#setXMLbase(String)}.
     */
    public void setXMLbase(String xmlbase) throws DOMException {
        setAttributeNS(XMLConstants.XML_NAMESPACE_URI, "xml:base", xmlbase);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getOwnerSVGElement()}.
     */
    public SVGSVGElement getOwnerSVGElement() {
        for (Element e = HiddenChildElementSupport.getParentElement(this);
             e != null;
             e = HiddenChildElementSupport.getParentElement(e)) {
            if (e instanceof SVGSVGElement) {
                return (SVGSVGElement)e;
            }
        }
        return null;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getViewportElement()}.
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
     * <b>DOM</b>: Implements {@link Node#getNodeName()}.
     */
    public String getNodeName() {
        if (prefix == null || prefix.equals("")) {
            return getLocalName();
        }
        String ln = getLocalName();
        StringBuffer sb = new StringBuffer(prefix.length() + ln.length() + 1);
        sb.append(prefix).append(':').append(ln);
        return sb.toString();
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
        return SVGDOMImplementation.SVG_NAMESPACE_URI;
    }

    /**
     * <b>DOM</b>: Implements {@link Node#setPrefix(String)}.
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

    // Importation/Cloning ///////////////////////////////////////////

    /**
     * Exports this node to the given document.
     */
    protected Node export(Node n, AbstractDocument d) {
	super.export(n, d);
	SVGOMElement e = (SVGOMElement)n;
	e.prefix = prefix;
	return n;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	super.deepExport(n, d);
	SVGOMElement e = (SVGOMElement)n;
	e.prefix = prefix;
	return n;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	super.copyInto(n);
	SVGOMElement e = (SVGOMElement)n;
	e.prefix = prefix;
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	super.deepCopyInto(n);
	SVGOMElement e = (SVGOMElement)n;
	e.prefix = prefix;
	return n;
    }
}
