/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import java.lang.ref.WeakReference;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.DocumentEvent;

/**
 * This class implements a wrapper for a Document. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DocumentWrapper
    extends NodeWrapper
    implements Document,
               DocumentEvent {
    
    /**
     * The node cache.
     */
    protected Map nodes = new HashMap(11);

    /**
     * The DOMImplementation wrapper.
     */
    protected DOMImplementationWrapper domImplementationWrapper;

    /**
     * Creates a new DocumentWrapper object.
     */
    public DocumentWrapper(DOMImplementationWrapper diw, Document doc) {
        super(null, doc);
        domImplementationWrapper = diw;
    }

    /**
     * Called from the finalize() method of the given object.
     */
    public void nodeWrapperFinalized(NodeWrapper nw) {
        nodes.remove(nw.getNode());
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Document#getDoctype()}.
     */
    public DocumentType getDoctype() {
        throw new InternalError("!!! Not Implemented");
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Document#getImplementation()}.
     * @return {@link #domImplementationWrapper}
     */
    public DOMImplementation getImplementation() {
        return domImplementationWrapper;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#getDocumentElement()}.
     */
    public Element getDocumentElement() {
        class Query implements Runnable {
            Element result;
            public void run() {
                result = ((Document)node).getDocumentElement();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createElementWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createElement(String)}.
     */
    public Element createElement(final String tagName) throws DOMException {
        class Query implements Runnable {
            Element result;
            public void run() {
                result = ((Document)node).createElement(tagName);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createElementWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createDocumentFragment()}.
     */
    public DocumentFragment createDocumentFragment() {
        class Query implements Runnable {
            DocumentFragment result;
            public void run() {
                result = ((Document)node).createDocumentFragment();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createDocumentFragmentWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createTextNode(String)}.
     */
    public Text createTextNode(final String data) {
        class Query implements Runnable {
            Text result;
            public void run() {
                result = ((Document)node).createTextNode(data);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createTextWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createComment(String)}.
     */
    public Comment createComment(final String data) {
        class Query implements Runnable {
            Comment result;
            public void run() {
                result = ((Document)node).createComment(data);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createCommentWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createCDATASection(String)}.
     */
    public CDATASection createCDATASection(final String data) throws DOMException {
        class Query implements Runnable {
            CDATASection result;
            public void run() {
                result = ((Document)node).createCDATASection(data);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createCDATASectionWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createProcessingInstruction(String,String)}.
     */
    public ProcessingInstruction createProcessingInstruction
        (final String target, final String data) throws DOMException {
        class Query implements Runnable {
            ProcessingInstruction result;
            public void run() {
                result = ((Document)node).createProcessingInstruction(target, data);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createProcessingInstructionWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createAttribute(String)}.
     */
    public Attr createAttribute(final String name) throws DOMException {
        class Query implements Runnable {
            Attr result;
            public void run() {
                result = ((Document)node).createAttribute(name);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createAttrWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createEntityReference(String)}.
     */
    public EntityReference createEntityReference(final String name) throws DOMException {
        class Query implements Runnable {
            EntityReference result;
            public void run() {
                result = ((Document)node).createEntityReference(name);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createEntityReferenceWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#getElementsByTagName(String)}.
     */
    public NodeList getElementsByTagName(final String tagname) {
        class Query implements Runnable {
            NodeList result;
            public void run() {
                result = ((Element)node).getElementsByTagName(tagname);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.result == null) {
            return null;
        }
        return createNodeListWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#importNode(Node,boolean)}.
     */
    public Node importNode(final Node importedNode,
                           final boolean deep) throws DOMException {
        class Query implements Runnable {
            Node result;
            DOMException exception;
            public void run() {
                try {
                    result = ((Document)node).importNode
                        (((NodeWrapper)importedNode).node, deep);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createElementNS(String,String)}.
     */
    public Element createElementNS(final String namespaceURI,
                                   final String qualifiedName)
        throws DOMException {
        class Query implements Runnable {
            Element result;
            public void run() {
                result = ((Document)node).createElementNS(namespaceURI,
                                                          qualifiedName);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createElementWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createAttributeNS(String,String)}.
     */
    public Attr createAttributeNS(final String namespaceURI,
                                  final String qualifiedName)
        throws DOMException {
        class Query implements Runnable {
            Attr result;
            public void run() {
                result = ((Document)node).createAttributeNS(namespaceURI,
                                                            qualifiedName);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createAttrWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#getElementsByTagNameNS(String,String)}.
     */
    public NodeList getElementsByTagNameNS(final String namespaceURI,
                                           final String localName) {
        class Query implements Runnable {
            NodeList result;
            public void run() {
                result = ((Element)node).getElementsByTagNameNS(namespaceURI,
                                                                localName);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.result == null) {
            return null;
        }
        return createNodeListWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#getElementById(String)}.
     */
    public Element getElementById(final String elementId) {
        class Query implements Runnable {
            Element result;
            public void run() {
                result = ((Document)node).getElementById(elementId);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createElementWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.events.DocumentEvent#createEvent(String)}.
     */
    public Event createEvent(final String eventType) throws DOMException {
        class Query implements Runnable {
            Event result;
            DOMException exception;
            public void run() {
                try {
                    result = ((DocumentEvent)node).createEvent(eventType);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }        
        return domImplementationWrapper.createEventWrapper(this, q.result);
    }

    /**
     * Creates an EventWrapper.
     */
    public Event createEventWrapper(Event evt) {
        return domImplementationWrapper.createEventWrapper(this, evt);
    }

    /**
     * Invokes the given Runnable from the associated RunnableQueue
     * thread.
     */
    public void invokeAndWait(Runnable r) {
        domImplementationWrapper.invokeAndWait(r);
    }

    /**
     * Invokes the given Runnable from the associated RunnableQueue
     * thread.
     */
    public void invokeLater(Runnable r) {
        domImplementationWrapper.invokeLater(r);
    }

    /**
     * Invokes the given Runnable from the event listeners RunnableQueue
     * thread.
     */
    public void invokeEventListener(Runnable r) {
        domImplementationWrapper.invokeEventListener(r);
    }

    /**
     * Creates a wrapper for the given node.
     */
    public Node createNodeWrapper(Node n) {
        if (n == null) {
            return null;
        }
        // Assume getNodeType() is thread-safe.
        switch (n.getNodeType()) {
        case Node.ATTRIBUTE_NODE:
            return createAttrWrapper((Attr)n);
        case Node.COMMENT_NODE:
            return createCommentWrapper((Comment)n);
        case Node.CDATA_SECTION_NODE:
            return createCDATASectionWrapper((CDATASection)n);
        case Node.ELEMENT_NODE:
            return createElementWrapper((Element)n);
        case Node.ENTITY_REFERENCE_NODE:
            return createEntityReferenceWrapper((EntityReference)n);
        case Node.DOCUMENT_FRAGMENT_NODE:
            return createDocumentFragmentWrapper((DocumentFragment)n);
        case Node.PROCESSING_INSTRUCTION_NODE:
            return createProcessingInstructionWrapper((ProcessingInstruction)n);
        case Node.TEXT_NODE:
            return createTextWrapper((Text)n);
        default:
            throw new InternalError("!!! To Be Implemented" + n.getNodeType());
        }
    }

    /**
     * Creates a wrapper for the given element.
     */
    protected Element createElementWrapper(Element e) {
        if (e == null) {
            return null;
        }
        Element result = (Element)getNode(e);
        if (result == null) {
            result = new ElementWrapper(this, e);
            nodes.put(e, result);
        }
        return result;
    }

    /**
     * Creates a wrapper for the given Attr.
     */
    protected Attr createAttrWrapper(Attr a) {
        if (a == null) {
            return null;
        }
        Attr result = (Attr)getNode(a);
        if (result == null) {
            result = new AttrWrapper(this, a);
            nodes.put(a, new WeakReference(result));
        }
        return result;
    }

    /**
     * Creates a wrapper for the given Comment.
     */
    protected CDATASection createCDATASectionWrapper(CDATASection c) {
        if (c == null) {
            return null;
        }
        CDATASection result = (CDATASection)getNode(c);
        if (result == null) {
            result = new CDATASectionWrapper(this, c);
            nodes.put(c, new WeakReference(result));
        }
        return result;
    }

    /**
     * Creates a wrapper for the given Comment.
     */
    protected Comment createCommentWrapper(Comment c) {
        if (c == null) {
            return null;
        }
        Comment result = (Comment)getNode(c);
        if (result == null) {
            result = new CommentWrapper(this, c);
            nodes.put(c, new WeakReference(result));
        }
        return result;
    }

    /**
     * Creates a wrapper for the given EntityReference.
     */
    protected EntityReference createEntityReferenceWrapper(EntityReference er) {
        if (er == null) {
            return null;
        }
        EntityReference result = (EntityReference)getNode(er);
        if (result == null) {
            result = new EntityReferenceWrapper(this, er);
            nodes.put(er, new WeakReference(result));
        }
        return result;
    }

    /**
     * Creates a wrapper for the given Text.
     */
    protected Text createTextWrapper(Text t) {
        if (t == null) {
            return null;
        }
        Text result = (Text)getNode(t);
        if (result == null) {
            result = new TextWrapper(this, t);
            nodes.put(t, new WeakReference(result));
        }
        return result;
    }

    /**
     * Creates a wrapper for the given DocumentFragment.
     */
    protected DocumentFragment createDocumentFragmentWrapper(DocumentFragment df) {
        if (df == null) {
            return null;
        }
        return new DocumentFragmentWrapper(this, df);
    }

    /**
     * Creates a wrapper for the given ProcessingInstruction.
     */
    protected ProcessingInstruction createProcessingInstructionWrapper
        (ProcessingInstruction pi) {
        if (pi == null) {
            return null;
        }
        ProcessingInstruction result = (ProcessingInstruction)getNode(pi);
        if (result == null) {
            result = new ProcessingInstructionWrapper(this, pi);
            nodes.put(pi, new WeakReference(result));
        }
        return result;
    }

    /**
     * Creates a wrapper for the given node list.
     */
    protected NodeList createNodeListWrapper(NodeList nl) {
        if (nl == null) {
            return null;
        }
        return new NodeListWrapper(this, nl);
    }

    /**
     * Creates a wrapper for the given node map.
     */
    protected NamedNodeMap createNamedNodeMapWrapper(NamedNodeMap nm) {
        return new NamedNodeMapWrapper(this, nm);
    }

    /**
     * Returns the node associated with the given object.
     */
    protected Object getNode(Object o) {
        WeakReference wr = (WeakReference)nodes.get(o);
        if (wr == null) {
            return null;
        }
        return wr.get();
    }
}
