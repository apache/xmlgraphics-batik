/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.Dimension2D;
import java.io.InterruptedIOException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.batik.css.HiddenChildElementSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.TextPainter;
import org.apache.batik.script.InterpreterPool;
import org.apache.batik.util.Service;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.gvt.filter.ConcreteGraphicsNodeRableFactory;

/**
 * This class represents a context used by the various bridges and the
 * builder. A bridge context is associated to a particular document
 * and cannot be reused.
 *
 * The context encapsulates the dynamic bindings between DOM elements
 * and GVT nodes, graphic contexts such as a <tt>GraphicsNodeRenderContext</tt>,
 * and the different objects required by the GVT builder to interpret
 * a SVG DOM tree such as the current viewport or the user agent.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class BridgeContext implements ErrorConstants {

    /**
     * The GVT builder that might be used to create a GVT subtree.
     */
    protected GVTBuilder gvtBuilder;

    /**
     * The viewports.
     * key is an Element -
     * value is a Viewport
     */
    protected Map viewportMap = new HashMap();

    /**
     * The viewport stack. Used in building time.
     */
    protected List viewportStack = new LinkedList();

    /**
     * The user agent.
     */
    protected UserAgent userAgent;

    /**
     * Binding Map:
     * key is an SVG Element -
     * value is a GraphicsNode
     */
    protected HashMap elementNodeMap;

    /**
     * Binding Map:
     * key is GraphicsNode -
     * value is a SVG Element.
     */
    protected HashMap nodeElementMap;

    /**
     * Binding Map:
     * key is style Element -
     * value is a list of styleReference
     */
    protected HashMap elementStyleAttMap;

    /**
     * Binding Map:
     * key is GraphicsNode -
     * value is list of StyleElement.
     */
    protected HashMap nodeStyleMap;

    /**
     * Bridge Map:
     * Keys are namespace URI - values are HashMap (with keys are local
     * name and values are a Bridge instance).
     */
    protected HashMap namespaceURIMap;

    /**
     * The interpreter pool used to handle scripts.
     */
    protected InterpreterPool interpreterPool;

    /**
     * The document loader used to load/create Document.
     */
    protected DocumentLoader documentLoader;

    /**
     * The size of the document.
     */
    protected Dimension2D documentSize;

    /**
     * The text painter to use. Typically, you can specify the text painter that
     * will be used be text nodes.
     */
    protected TextPainter textPainter;

    /**
     * Constructs a new empty bridge context.
     */
    protected BridgeContext() {}

    /**
     * By default we share a unique instance of InterpreterPool.
     */
    private static InterpreterPool sharedPool = new InterpreterPool();

    /**
     * Constructs a new bridge context.
     * @param userAgent the user agent
     */
    public BridgeContext(UserAgent userAgent) {
        this(userAgent,
             sharedPool,
             new DocumentLoader(userAgent));
    }

    /**
     * Constructs a new bridge context.
     * @param userAgent the user agent
     * @param documentLoader document loader
     */
    public BridgeContext(UserAgent userAgent,
                         DocumentLoader loader) {
        this(userAgent, sharedPool, loader);
    }

    /**
     * Constructs a new bridge context.
     * @param userAgent the user agent
     * @param interpreterPool the interpreter pool
     * @param documentLoader document loader
     */
    public BridgeContext(UserAgent userAgent,
                         InterpreterPool interpreterPool,
                         DocumentLoader documentLoader) {
        this.userAgent = userAgent;
        this.viewportMap.put(userAgent, new UserAgentViewport(userAgent));
        this.interpreterPool = interpreterPool;
        this.documentLoader = documentLoader;
        registerSVGBridges(this);
    }

    /////////////////////////////////////////////////////////////////////////
    // properties
    /////////////////////////////////////////////////////////////////////////

    /**
     * Sets the text painter that will be used by text nodes. This attributes
     * might be used by bridges (especially SVGTextElementBridge) to set the
     * text painter of each TextNode.
     *
     * @param textPainter the text painter for text nodes 
     */
    public void setTextPainter(TextPainter textPainter) {
	this.textPainter = textPainter;
    }

    /**
     * Returns the text painter that will be used be text nodes.
     */
    public TextPainter getTextPainter() {
	return textPainter;
    }

    /**
     * Returns the user agent of this bridge context.
     */
    public UserAgent getUserAgent() {
        return userAgent;
    }

    /**
     * Sets the user agent to the specified user agent.
     * @param userAgent the user agent
     */
    protected void setUserAgent(UserAgent userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Sets the GVT builder that uses this context.
     */
    protected void setGVTBuilder(GVTBuilder gvtBuilder) {
        this.gvtBuilder = gvtBuilder;
    }

    /**
     * Returns the GVT builder that is currently used to build the GVT tree.
     */
    public GVTBuilder getGVTBuilder() {
        return gvtBuilder;
    }

    /**
     * Returns the interpreter pool used to handle scripts.
     */
    public InterpreterPool getInterpreterPool() {
        return interpreterPool;
    }

    /**
     * Sets the interpreter pool used to handle scripts to the
     * specified interpreter pool.
     * @param interpreterPool the interpreter pool
     */
    protected void setInterpreterPool(InterpreterPool interpreterPool) {
        this.interpreterPool = interpreterPool;
    }

    /**
     * Returns the document loader used to load external documents.
     */
    public DocumentLoader getDocumentLoader() {
        return documentLoader;
    }

    /**
     * Sets the document loader used to load external documents.
     * @param newDocumentLoader the new document loader
     */
    protected void setDocumentLoader(DocumentLoader newDocumentLoader) {
        this.documentLoader = newDocumentLoader;
    }

    /////////////////////////////////////////////////////////////////////////
    // convenient methods
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the element referenced by the specified element by the
     * specified uri. The referenced element can not be a Document.
     *
     * @param e the element referencing
     * @param uri the uri of the referenced element
     */
    public Element getReferencedElement(Element e, String uri) {
        try {
            SVGDocument document = (SVGDocument)e.getOwnerDocument();
            URIResolver ur = new URIResolver(document, documentLoader);
            Element ref = ur.getElement(uri, e);
            if (ref == null) {
                throw new BridgeException(e, ERR_URI_REFERENCE_A_DOCUMENT,
                                          new Object[] {uri});
            } else {
                return ref;
            }
        } catch (MalformedURLException ex) {
            throw new BridgeException(e, ERR_URI_MALFORMED,
                                      new Object[] {uri});
        } catch (InterruptedIOException ex) {
            throw new InterruptedBridgeException();
        } catch (IOException ex) {
            throw new BridgeException(e, ERR_URI_IO,
                                      new Object[] {uri});
        } catch (IllegalArgumentException ex) {
            throw new BridgeException(e, ERR_URI_REFERENCE_A_DOCUMENT,
                                      new Object[] {uri});
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // methods to access to the current state of the bridge context
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the actual size of the document or null if the document
     * has not been built yet.
     */
    public Dimension2D getDocumentSize() {
        return documentSize;
    }

    /**
     * Sets the size of the document to the specified dimension.
     *
     * @param d the actual size of the SVG document
     */
    protected void setDocumentSize(Dimension2D d) {
        this.documentSize = d;
    }

    /**
     * Returns the viewport of the specified element.
     * @param e the element interested in its viewport
     */
    public Viewport getViewport(Element e) {
        if (viewportStack != null) { // building time
            if (viewportStack.size() > 0) {
                return (Viewport)viewportStack.get(0);
            } else {
                return (Viewport)viewportMap.get(userAgent);
            }
        } else {
            // search the first parent which has defined a viewport
            e = HiddenChildElementSupport.getParentElement(e);
            while (e != null) {
                Viewport viewport = (Viewport)viewportMap.get(e);
                if (viewport != null) {
                    return viewport;
                }
                e = HiddenChildElementSupport.getParentElement(e);
            }
            return (Viewport)viewportMap.get(userAgent);
        }
    }

    /**
     * Starts a new viewport from the specified element.
     * @param e the element that starts the viewport
     * @param viewport the viewport of the element
     */
    public void openViewport(Element e, Viewport viewport) {
        viewportMap.put(e, viewport);
        viewportStack.add(0, viewport);
    }

    /**
     * Closes the viewport associated to the specified element.
     * @param e the element that closes its viewport
     */
    public void closeViewport(Element e) {
        viewportMap.remove(e);
        viewportStack.remove(0);
    }

    /**
     * Returns true if the bridge should support dynamic SVG content,
     * false otherwise.
     */
    public boolean isDynamic() {
        return true;
    }

    /////////////////////////////////////////////////////////////////////////
    // binding methods
    /////////////////////////////////////////////////////////////////////////

    /**
     * Binds the specified GraphicsNode to the specified Element. This method
     * automatically bind the graphics node to the element and the element to
     * the graphics node.
     * @param element the element to bind to the specified graphics node
     * @param node the graphics node to bind to the specified element
     */
    public void bind(Element element, GraphicsNode node) {
        if (elementNodeMap == null) {
            elementNodeMap = new HashMap();
            nodeElementMap = new HashMap();
        }
        elementNodeMap.put(element, node);
        nodeElementMap.put(node, element);
    }

    /**
     * Removes the binding of the specified Element. This method
     * unbinds the specified element to its associated graphics node,
     * remove the binding from the graphics node to the specified
     * element, and all the style references associated to the
     * specified element are also removed.
     * @param element the element to unbind
     */
    public void unbind(Element element) {
        if (elementNodeMap == null) {
            return;
        }
        GraphicsNode node = (GraphicsNode)elementNodeMap.get(element);
        elementNodeMap.remove(element);
        nodeElementMap.remove(node);
        // Removes all styles bound to this GraphicsNode
        removeStyleReferences(node);
    }

    /**
     * Returns the GraphicsNode associated to the specified Element or
     * null if any.
     * @param element the element associated to the graphics node to return
     */
    public GraphicsNode getGraphicsNode(Element element) {
        if (elementNodeMap != null) {
            return (GraphicsNode)elementNodeMap.get(element);
        } else {
            return null;
        }
    }

    /**
     * Returns the Element associated to the specified GraphicsNode or
     * null if any.
     * @param node the graphics node associated to the element to return
     */
    public Element getElement(GraphicsNode node) {
        if (nodeElementMap != null) {
            return (Element)nodeElementMap.get(node);
        } else {
            return null;
        }
    }

    /**
     * Binds a style element to a style reference.
     * Several style reference can be bound to the same style element.
     * @param element the element
     * @param reference the style reference
     */
    public void bind(Element element, StyleReference reference) {
        if (elementStyleAttMap == null) {
            elementStyleAttMap = new HashMap();
        }
        LinkedList list = (LinkedList)elementStyleAttMap.get(element);
        if (list == null) {
            list = new LinkedList();
            elementStyleAttMap.put(element, list);
        }
        list.add(reference);
        if (nodeStyleMap == null)
            nodeStyleMap = new HashMap();

        GraphicsNode node = reference.getGraphicsNode();
        list = (LinkedList)nodeStyleMap.get(node);
        if (list == null) {
            list = new LinkedList();
            nodeStyleMap.put(node, list);
        }
        list.add(element);
    }

    /**
     * Returns an enumeration of all style refence for the specified
     * style element.
     * @param element the element
     */
    public List getStyleReferenceList(Element element) {
        if (elementStyleAttMap == null) {
            return Collections.EMPTY_LIST;
        } else {
            LinkedList list = (LinkedList)elementStyleAttMap.get(element);
            if (list != null) {
                return list;
            } else {
                return Collections.EMPTY_LIST;
            }
        }
    }

    /**
     * Removes all bindings between a style Element and the specified
     * GraphicsNode.
     * @param node the graphics node
     */
    private void removeStyleReferences(GraphicsNode node){
        // Get the list of style Elements used by this node
        if (nodeStyleMap == null) {
            return;
        }
        List styles = (List)nodeStyleMap.get(node);
        if (styles != null) {
            nodeStyleMap.remove(node);
        }
        for (Iterator it = styles.iterator(); it.hasNext();){
            Element style = (Element)it.next();
            removeStyleReference(node, style);
        }
    }

    /**
     * Removes all StyleReference corresponding to the specified GraphicsNode.
     * @param node the graphics node
     * @param style the style element
     */
    private void removeStyleReference(GraphicsNode node, Element style){
        if (elementStyleAttMap == null) {
            return;
        }
        LinkedList list = (LinkedList)elementStyleAttMap.get(style);
        List removed = null;
        if (list == null) {
            return;
        }
        for (Iterator it = list.iterator(); it.hasNext();){
            StyleReference styleRef = (StyleReference)it.next();
            if (styleRef.getGraphicsNode()==node) {
                if (removed == null) {
                    removed = new LinkedList();
                }
                removed.add(styleRef);
            }
        }
        if (removed != null) {
            for (Iterator it = removed.iterator(); it.hasNext();) {
                list.remove(it.next());
            }
        }
        if (list.size() == 0) {
            elementStyleAttMap.remove(style);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // bridge support
    /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the bridge associated with the specified element.
     * @param element the element
     */
    public Bridge getBridge(Element element) {
        if (namespaceURIMap == null || element == null) {
            return null;
        }
        String namespaceURI = element.getNamespaceURI();
        String localName = element.getLocalName();
        namespaceURI = ((namespaceURI == null)? "" : namespaceURI);
        HashMap localNameMap = (HashMap) namespaceURIMap.get(namespaceURI);
        if (localNameMap == null) {
            return null;
        }
        return (Bridge)localNameMap.get(localName);
    }

    /**
     * Returns the bridge associated with the element type
     * @param nameSpaceURI namespace of the requested element
     * @param localName element's local name
     *
     */
    public Bridge getBridge(String namespaceURI,
                            String localName){
        HashMap localNameMap = (HashMap) namespaceURIMap.get(namespaceURI);
        if (localNameMap == null) {
            return null;
        }
        return (Bridge)localNameMap.get(localName);
    }

    /**
     * Associates the specified <tt>Bridge</tt> object with the specified
     * namespace URI and local name.
     * @param namespaceURI the namespace URI
     * @param localName the local name
     * @param bridge the bridge that manages the element
     */
    public void putBridge(String namespaceURI, String localName,
                          Bridge bridge) {
        // debug
        if (!(namespaceURI.equals(bridge.getNamespaceURI())
              && localName.equals(bridge.getLocalName()))) {
            throw new Error("Invalid Bridge: "+
                            namespaceURI+"/"+bridge.getNamespaceURI()+" "+
                            localName+"/"+bridge.getLocalName()+" "+
                            bridge.getClass());
        }

        if (namespaceURIMap == null) {
            namespaceURIMap = new HashMap();
        }
        namespaceURI = ((namespaceURI == null)? "" : namespaceURI);
        HashMap localNameMap = (HashMap) namespaceURIMap.get(namespaceURI);
        if (localNameMap == null) {
            localNameMap = new HashMap();
            namespaceURIMap.put(namespaceURI, localNameMap);
        }
        localNameMap.put(localName, bridge);
    }

    /**
     * Associates the specified <tt>Bridge</tt> object with it's 
     * namespace URI and local name.
     * @param bridge the bridge that manages the element
     */
    public void putBridge(Bridge bridge) {
        putBridge(bridge.getNamespaceURI(),
                  bridge.getLocalName(),
                  bridge);
    }

    /**
     * Removes the <tt>Bridge</tt> object associated to the specified
     * namespace URI and local name.
     * @param namespaceURI the namespace URI
     * @param localName the local name
     */
    public void removeBridge(String namespaceURI, String localName) {
        if (namespaceURIMap == null) {
            return;
        }
        namespaceURI = ((namespaceURI == null)? "" : namespaceURI);
        HashMap localNameMap = (HashMap) namespaceURIMap.get(namespaceURI);
        if (localNameMap != null) {
            localNameMap.remove(localName);
            if (localNameMap.isEmpty()) {
                namespaceURIMap.remove(namespaceURI);
                if (namespaceURIMap.isEmpty()) {
                    namespaceURIMap = null;
                }
            }
        }
    }

   /**
     * Registers the bridges to handle SVG 1.0 elements.
     * @param ctx the bridge context to initialize
     */
    public static void registerSVGBridges(BridgeContext ctx) {
        UserAgent ua = ctx.getUserAgent();
        Iterator iter = getBridgeExtensions().iterator();

        while(iter.hasNext()) {
            BridgeExtension be = (BridgeExtension)iter.next();
            be.registerTags(ctx);
            ua.registerExtension(be);
        }
    }

    static List extensions = null;

    public synchronized static List getBridgeExtensions() {
        if (extensions != null)
            return extensions;

        extensions = new LinkedList();
        extensions.add(new SVGBridgeExtension());

        Iterator iter = Service.providers(BridgeExtension.class);

        while (iter.hasNext()) {
            BridgeExtension be = (BridgeExtension)iter.next();
            float priority  = be.getPriority();
            ListIterator li = extensions.listIterator();
            for (;;) {
                if (!li.hasNext()) {
                    li.add(be);
                    break;
                }
                BridgeExtension lbe = (BridgeExtension)li.next();
                if (lbe.getPriority() > priority) {
                    li.previous();
                    li.add(be);
                    break;
                }
            }
        }
        return extensions;
    }
        
 }

