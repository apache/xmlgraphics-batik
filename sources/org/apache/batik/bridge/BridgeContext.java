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
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterPool;
import org.apache.batik.util.Service;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Document;
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
     * The document is bridge context is dedicated to.
     */
    protected Document document;

    /**
     * The GVT builder that might be used to create a GVT subtree.
     */
    protected GVTBuilder gvtBuilder;

    /**
     * The interpreter cache per document.
     * key is the language -
     * value is a Interpreter
     */
    protected Map interpreterMap = new HashMap(7);

    /**
     * A hash map of all the font families already matched. This is
     * to reduce the number of instances of GVTFontFamilies and to
     * hopefully reduce the time taken to search for a matching SVG font.
     */
    private HashMap fontFamilyMap;

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
     * The BridgeUpdateHandler stack. Used in building time.
     */
    protected List bridgeUpdateHandlerStack = new LinkedList();

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
     * Whether the bridge must support dynamic features.
     */
    protected boolean dynamic;

    /**
     * The update manager.
     */
    protected UpdateManager updateManager;

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

    // properties

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
     * Returns the document this bridge context is dedicated to.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Returns the map of font families
     */
    public HashMap getFontFamilyMap(){
        if (fontFamilyMap == null){
            fontFamilyMap = new HashMap();
        }

        return fontFamilyMap;
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
     * Sets the document this bridge context is dedicated to, to the
     * specified document.
     * @param document the document
     */
    protected void setDocument(Document document) {
        if (this.document != document){
            fontFamilyMap = null;
        }
        this.document = document;
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
     * Returns a Interpreter for the specified language.
     *
     * @param language the scripting language
     */
    public Interpreter getInterpreter(String language) {
        if (document == null) {
            throw new RuntimeException("Unknown document");
        }
        Interpreter interpreter = (Interpreter)interpreterMap.get(language);
        if (interpreter == null) {
            interpreter = interpreterPool.createInterpreter(document, language);
            interpreterMap.put(language, interpreter);
        }
        return interpreter;
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

    // convenient methods

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
                throw new BridgeException(e, ERR_URI_BAD_TARGET,
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

    // methods to access to the current state of the bridge context

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
        if (viewportStack != null) {
            // building time
            return (Viewport)viewportStack.get(0);
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
        if (viewportStack == null) {
            viewportStack = new LinkedList();
        }
        viewportStack.add(0, viewport);
    }

    /**
     * Closes the viewport associated to the specified element.
     * @param e the element that closes its viewport
     */
    public void closeViewport(Element e) {
        //viewportMap.remove(e); FIXME: potential memory leak
        viewportStack.remove(0);
        if (viewportStack.size() == 0) {
            viewportStack = null;
        }
    }

    /**
     * Returns true if the bridge should support dynamic SVG content,
     * false otherwise.
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * Sets the dynamic mode.
     */
    public void setDynamic(boolean b) {
        dynamic = b;
    }

    /**
     * Returns the update manager, if the bridge supports dynamic features.
     */
    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    /**
     * Sets the update manager.
     */
    protected void setUpdateManager(UpdateManager um) {
        updateManager = um;
    }

    // binding methods

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
     * Removes the binding of the specified Element.
     * @param element the element to unbind
     */
    public void unbind(Element element) {
        if (elementNodeMap == null) {
            return;
        }
        GraphicsNode node = (GraphicsNode)elementNodeMap.get(element);
        elementNodeMap.remove(element);
        nodeElementMap.remove(node);
    }

    /**
     * Returns the GraphicsNode associated to the specified Element or
     * null if any.
     *
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
     *
     * @param node the graphics node associated to the element to return
     */
    public Element getElement(GraphicsNode node) {
        if (nodeElementMap != null) {
            return (Element)nodeElementMap.get(node);
        } else {
            return null;
        }
    }

    // bridge support
 
    /**
     * Returns the bridge associated with the specified element.
     *
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
        Bridge bridge = (Bridge)localNameMap.get(localName);
        if (dynamic) {
            return bridge == null ? null : bridge.getInstance();
        } else {
            return bridge;
        }
    }

    /**
     * Returns true if the specified element has a GraphicsNodeBridge
     * associated to it, false otherwise.
     *
     * @param element the element
     */
    public boolean hasGraphicsNodeBridge(Element element) {
        if (namespaceURIMap == null || element == null) {
            return false;
        }
        String namespaceURI = element.getNamespaceURI();
        String localName = element.getLocalName();
        namespaceURI = ((namespaceURI == null)? "" : namespaceURI);
        HashMap localNameMap = (HashMap) namespaceURIMap.get(namespaceURI);
        if (localNameMap == null) {
            return false;
        }
        return (localNameMap.get(localName) instanceof GraphicsNodeBridge);
    }

    /**
     * Returns the bridge associated with the element type
     *
     * @param nameSpaceURI namespace of the requested element
     * @param localName element's local name
     *
     */
    public Bridge getBridge(String namespaceURI, String localName) {
        HashMap localNameMap = (HashMap) namespaceURIMap.get(namespaceURI);
        if (localNameMap == null) {
            return null;
        }
        Bridge bridge = (Bridge)localNameMap.get(localName);
        if (dynamic) {
            return bridge == null ? null : bridge.getInstance();
        } else {
            return bridge;
        }
    }

    /**
     * Associates the specified <tt>Bridge</tt> object with the specified
     * namespace URI and local name.
     * @param namespaceURI the namespace URI
     * @param localName the local name
     * @param bridge the bridge that manages the element
     */
    public void putBridge(String namespaceURI, String localName, Bridge bridge) {
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
     *
     * @param bridge the bridge that manages the element
     */
    public void putBridge(Bridge bridge) {
        putBridge(bridge.getNamespaceURI(), bridge.getLocalName(), bridge);
    }

    /**
     * Removes the <tt>Bridge</tt> object associated to the specified
     * namespace URI and local name.
     *
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

    // dynamic support

    public void pushBridgeUpdateHandler(BridgeUpdateHandler handler) {
        bridgeUpdateHandlerStack.add(0, new BridgeUpdateHandlerInfo(handler));
    }

    public void setCurrentBridgeUpdateHandlerKey(int handlerKey) {
        BridgeUpdateHandlerInfo info = 
            (BridgeUpdateHandlerInfo)bridgeUpdateHandlerStack.get(0);
        info.handlerKey = handlerKey;
    }

    public BridgeUpdateHandler getCurrentBridgeUpdateHandler() {
        BridgeUpdateHandlerInfo info = 
            (BridgeUpdateHandlerInfo)bridgeUpdateHandlerStack.get(0);
        return info.handler;
    }

    public int getCurrentBridgeUpdateHandlerKey() {
        BridgeUpdateHandlerInfo info = 
            (BridgeUpdateHandlerInfo)bridgeUpdateHandlerStack.get(0);
        return info.handlerKey;
    }

    public void popBridgeUpdateHandler() {
        bridgeUpdateHandlerStack.remove(0);
    }

    protected static class BridgeUpdateHandlerInfo {

        protected BridgeUpdateHandler handler;
        protected int handlerKey;

        public BridgeUpdateHandlerInfo(BridgeUpdateHandler handler) {
            this.handler = handler;
        }
    }

    // bridge extensions support

   /**
     * Registers the bridges to handle SVG 1.0 elements.
     *
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

    /**
     * Returns the extensions supported by this bridge context.
     */
    public synchronized static List getBridgeExtensions() {
        if (extensions != null) {
            return extensions;
        }
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

