/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * This class implements the {@link org.w3c.dom.Document},
 * {@link org.w3c.dom.events.DocumentEvent}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GenericDocument
    extends AbstractDocument {
    /**
     * Is this document immutable?
     */
    protected boolean readonly;

    /**
     * Creates a new uninitialized document.
     */
    public GenericDocument() {
    }

    /**
     * Creates a new uninitialized document.
     */
    public GenericDocument(DocumentType dt, DOMImplementation impl) {
        super(impl);
        if (dt != null) {
            appendChild(dt);
        }
    }

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

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createElement(String)}.
     */
    public Element createElement(String tagName) throws DOMException {
        return new GenericElement(tagName.intern(), this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createDocumentFragment()}.
     */
    public DocumentFragment createDocumentFragment() {
        return new GenericDocumentFragment(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createTextNode(String)}.
     */
    public Text createTextNode(String data) {
        return new GenericText(data, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createComment(String)}.
     */
    public Comment createComment(String data) {
        return new GenericComment(data, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createCDATASection(String)}.
     */
    public CDATASection createCDATASection(String data) throws DOMException {
        return new GenericCDATASection(data, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createProcessingInstruction(String,String)}.
     * @return a {@link StyleSheetProcessingInstruction} if target is
     *         "xml-stylesheet" or a GenericProcessingInstruction otherwise.
     */
    public ProcessingInstruction createProcessingInstruction(String target,
                                                             String data)
        throws DOMException {
        if ("xml-stylesheet".equals(target)) {
            return new StyleSheetProcessingInstruction
                (data, this, (StyleSheetFactory)getImplementation());
        }
        return new GenericProcessingInstruction(target, data, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#getElementById(String)}.
     */
    public Element getElementById(String elementId) {
        if (elementId == null || elementId.equals("")) {
            return null;
        }
        Element e = getDocumentElement();
        if (e == null) {
            return null;
        }
        return getById(elementId, e);
    }

    /**
     * An auxiliary method used by getElementById.
     */
    protected static Element getById(String id, Node node) {
        if (!(node instanceof Element)) {
            return null;
        }
        
        Element e = (Element)node;
        if (e.getAttribute("id").equals(id)) {
            return e;
        }
        for (Node n = node.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            Element result = getById(id, n);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createAttribute(String)}.
     */
    public Attr createAttribute(String name) throws DOMException {
        return new GenericAttr(name.intern(), this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createEntityReference(String)}.
     */
    public EntityReference createEntityReference(String name)
        throws DOMException {
        return new GenericEntityReference(name, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createElementNS(String,String)}.
     */
    public Element createElementNS(String namespaceURI, String qualifiedName)
        throws DOMException {
        if (namespaceURI == null) {
            return new GenericElement(qualifiedName.intern(), this);
        } else {
            return new GenericElementNS(namespaceURI.intern(),
                                        qualifiedName.intern(),
                                        this);
        }
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createAttributeNS(String,String)}.
     */
    public Attr createAttributeNS(String namespaceURI, String qualifiedName)
        throws DOMException {
        if (namespaceURI == null) {
            return new GenericAttr(qualifiedName.intern(), this);
        } else {
            return new GenericAttrNS(namespaceURI.intern(),
                                     qualifiedName.intern(),
                                     this);
        }
    }
}
