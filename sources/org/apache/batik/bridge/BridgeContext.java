/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Cursor;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.css.engine.CSSEngineListener;
import org.apache.batik.css.engine.SystemColorSupport;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.TextPainter;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterPool;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.svg.SVGDocument;

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
public class BridgeContext implements ErrorConstants, CSSContext {

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
     * A Map of all the font families already matched. This is
     * to reduce the number of instances of GVTFontFamilies and to
     * hopefully reduce the time taken to search for a matching SVG font.
     */
    private Map fontFamilyMap;

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
        documentLoader.setBridgeContext(this);
        registerSVGBridges(this);
    }

    /**
     * Initializes the given document.
     */
    protected void initializeDocument(Document document) {
        SVGOMDocument doc = (SVGOMDocument)document;
        CSSEngine eng = doc.getCSSEngine();
        if (eng == null) {
            SVGDOMImplementation impl;
            impl = (SVGDOMImplementation)doc.getImplementation();
            eng = impl.createCSSEngine(doc, this);
            doc.setCSSEngine(eng);
            eng.setMedia(userAgent.getMedia());
            String uri = userAgent.getUserStyleSheetURI();
            if (uri != null) {
                try {
                    URL url = new URL(uri);
                    eng.setUserAgentStyleSheet
                        (eng.parseStyleSheet(url, "all"));
                } catch (MalformedURLException e) {
                    userAgent.displayError(e);
                }
            }
            eng.setAlternateStyleSheet(userAgent.getAlternateStyleSheet());
        }
    }

    // properties ////////////////////////////////////////////////////////////

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
     * Returns the document this bridge context is dedicated to.
     */
    public Document getDocument() {
        return document;
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
     * Returns the map of font families
     */
    public Map getFontFamilyMap(){
        if (fontFamilyMap == null){
            fontFamilyMap = new HashMap();
        }
        return fontFamilyMap;
    }

    /**
     * Sets the map of font families to the specified value.
     *
     *@param fontFamilyMap the map of font families
     */
    protected void setFontFamilyMap(Map fontFamilyMap) {
        this.fontFamilyMap = fontFamilyMap;
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
     * Returns the GVT builder that is currently used to build the GVT tree.
     */
    public GVTBuilder getGVTBuilder() {
        return gvtBuilder;
    }

    /**
     * Sets the GVT builder that uses this context.
     */
    protected void setGVTBuilder(GVTBuilder gvtBuilder) {
        this.gvtBuilder = gvtBuilder;
    }

    /**
     * Returns the interpreter pool used to handle scripts.
     */
    public InterpreterPool getInterpreterPool() {
        return interpreterPool;
    }

    /**
     * Returns the focus manager.
     */
    public FocusManager getFocusManager() {
        return focusManager;
    }

    /**
     * Returns the cursor manager
     */
    public CursorManager getCursorManager() {
        return cursorManager;
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
     * Returns true if the document is dynamic, false otherwise.
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * Sets the document as a dynamic document. Call this method
     * before the build phase (ie. before <tt>gvtBuilder.build(...)</tt>)
     * otherwise, that will have no effect.
     *
     *@param b the document state
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

    // reference management //////////////////////////////////////////////////

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
        } catch (SecurityException ex) {
            throw new BridgeException(e, ERR_URI_UNSECURE,
                                      new Object[] {uri});
        }
    }

    // Viewport //////////////////////////////////////////////////////////////

    /**
     * Returns the viewport of the specified element.
     *
     * @param e the element interested in its viewport
     */
    public Viewport getViewport(Element e) {
        if (viewportStack != null) {
            // building time
            if (viewportStack.size() == 0) {
                // outermost svg element
                return (Viewport)viewportMap.get(userAgent);
            } else {
                // current viewport
                return (Viewport)viewportStack.get(0);
            }
        } else {
            // search the first parent which has defined a viewport
            e = SVGUtilities.getParentElement(e);
            while (e != null) {
                Viewport viewport = (Viewport)viewportMap.get(e);
                if (viewport != null) {
                    return viewport;
                }
                e = SVGUtilities.getParentElement(e);
            }
            return (Viewport)viewportMap.get(userAgent);
        }
    }

    /**
     * Starts a new viewport from the specified element.
     *
     * @param e the element that defines a new viewport
     * @param viewport the viewport of the element
     */
    public void openViewport(Element e, Viewport viewport) {
        viewportMap.put(e, viewport);
        if (viewportStack == null) {
            viewportStack = new LinkedList();
        }
        viewportStack.add(0, viewport);
    }

    public void removeViewport(Element e) {
        viewportMap.remove(e);
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

    // Bindings //////////////////////////////////////////////////////////////

    /**
     * Binds the specified GraphicsNode to the specified Element. This method
     * automatically bind the graphics node to the element and the element to
     * the graphics node.
     *
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
     *
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

    // Bridge management /////////////////////////////////////////////////////
 
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
        String localName = element.getLocalName();
        String namespaceURI = element.getNamespaceURI();
        namespaceURI = ((namespaceURI == null)? "" : namespaceURI);
        HashMap localNameMap = (HashMap) namespaceURIMap.get(namespaceURI);
        if (localNameMap == null) {
            return false;
        }
        return (localNameMap.get(localName) instanceof GraphicsNodeBridge);
    }

    /**
     * Returns the bridge associated with the specified element.
     *
     * @param element the element
     */
    public Bridge getBridge(Element element) {
        if (namespaceURIMap == null || element == null) {
            return null;
        }
        String localName = element.getLocalName();
        String namespaceURI = element.getNamespaceURI();
        namespaceURI = ((namespaceURI == null)? "" : namespaceURI);
        return getBridge(namespaceURI, localName);
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
        // start assert
        if (!(namespaceURI.equals(bridge.getNamespaceURI())
              && localName.equals(bridge.getLocalName()))) {
            throw new Error("Invalid Bridge: "+
                            namespaceURI+"/"+bridge.getNamespaceURI()+" "+
                            localName+"/"+bridge.getLocalName()+" "+
                            bridge.getClass());
        }
        // end assert
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

    // dynamic support ////////////////////////////////////////////////////////

    /**
     * The list of all EventListener attached by bridges that need to
     * be removed on a dispose() call.
     */
    protected List eventListenerList = new LinkedList();

    /**
     * The DOM EventListener to receive 'DOMCharacterDataModified' event.
     */
    protected EventListener domCharacterDataModifiedListener;

    /**
     * The DOM EventListener to receive 'DOMAttrModified' event.
     */
    protected EventListener domAttrModifiedEventListener;

    /**
     * The DOM EventListener to receive 'DOMNodeInserted' event.
     */
    protected EventListener domNodeInsertedEventListener;

    /**
     * The DOM EventListener to receive 'DOMNodeRemoved' event.
     */
    protected EventListener domNodeRemovedEventListener;

    /**
     * The CSSEngine listener to receive CSSEngineEvent.
     */
    protected CSSEngineListener cssPropertiesChangedListener;

    /**
     * The EventListener that is responsible of managing DOM focus event.
     */
    protected FocusManager focusManager;

    /**
     * Manages cursors and performs caching when appropriate
     */
    protected CursorManager cursorManager = new CursorManager(this);

    /**
     * Adds EventListeners to the input document to handle the cursor 
     * property.
     * This is not done in the addDOMListeners method because 
     * addDOMListeners is only used for dynamic content whereas 
     * cursor support is provided for all content.
     * Also note that it is very important that the listeners be
     * registered for the capture phase as the 'default' behavior
     * for cursors is handled by the BridgeContext during the 
     * capture phase and the 'custom' behavior (handling of 'auto'
     * on anchors, for example), is handled during the bubling phase.
     */
    public void addUIEventListeners(Document doc) {
        EventTarget evtTarget = (EventTarget)doc.getDocumentElement();

        DOMMouseOverEventListener domMouseOverListener =
            new DOMMouseOverEventListener();
        evtTarget.addEventListener(SVGConstants.SVG_EVENT_MOUSEOVER, 
                                   domMouseOverListener,
                                   true);
        storeEventListener(evtTarget, SVGConstants.SVG_EVENT_MOUSEOVER, 
                           domMouseOverListener, true);

        DOMMouseOutEventListener domMouseOutListener =
            new DOMMouseOutEventListener();
        evtTarget.addEventListener(SVGConstants.SVG_EVENT_MOUSEOUT,
                                   domMouseOutListener,
                                   true);
        storeEventListener(evtTarget, SVGConstants.SVG_EVENT_MOUSEOUT, 
                           domMouseOutListener, true);

    }

    /**
     * Adds EventListeners to the DOM and CSSEngineListener to the
     * CSSEngine to handle any modifications on the DOM tree or style
     * properties and update the GVT tree in response.
     */
    public void addDOMListeners() {
        EventTarget evtTarget = (EventTarget)document;

        domAttrModifiedEventListener = new DOMAttrModifiedEventListener();
        evtTarget.addEventListener("DOMAttrModified",
                                   domAttrModifiedEventListener,
                                   true);

        domNodeInsertedEventListener = new DOMNodeInsertedEventListener();
        evtTarget.addEventListener("DOMNodeInserted",
                                   domNodeInsertedEventListener,
                                   true);

        domNodeRemovedEventListener = new DOMNodeRemovedEventListener();
        evtTarget.addEventListener("DOMNodeRemoved",
                                   domNodeRemovedEventListener,
                                   true);

        domCharacterDataModifiedListener = 
            new DOMCharacterDataModifiedListener();
        evtTarget.addEventListener("DOMCharacterDataModified",
                                   domCharacterDataModifiedListener,
                                   true);


        focusManager = new FocusManager(document);

        SVGOMDocument svgDocument = (SVGOMDocument)document;
        CSSEngine cssEngine = svgDocument.getCSSEngine();
        cssPropertiesChangedListener = new CSSPropertiesChangedListener();
        cssEngine.addCSSEngineListener(cssPropertiesChangedListener);
    }

    /**
     * Adds to the eventListenerList the specified event listener
     * registration.
     */
    protected void storeEventListener(EventTarget t,
                                      String s,
                                      EventListener l,
                                      boolean b) {
        eventListenerList.add(new EventListenerMememto(t, s, l, b));
    }

    /**
     * A class used to store an EventListener added to the DOM.
     */
    protected static class EventListenerMememto {

        public EventTarget target;
        public EventListener listener;
        public boolean useCapture;
        public String eventType;

        public EventListenerMememto(EventTarget t, 
                                    String s, 
                                    EventListener l, 
                                    boolean b) {
            target = t;
            eventType = s;
            listener = l;
            useCapture = b;
        }
    }

    /**
     * Disposes this BridgeContext.
     */
    public void dispose() {

        // remove all listeners added by Bridges
        Iterator iter = eventListenerList.iterator();
        while (iter.hasNext()) {
            EventListenerMememto m = (EventListenerMememto)iter.next();
            m.target.removeEventListener(m.eventType,
                                         m.listener,
                                         m.useCapture);
        }

        EventTarget evtTarget = (EventTarget)document;

        evtTarget.removeEventListener("DOMAttrModified",
                                      domAttrModifiedEventListener, 
                                      true);
        evtTarget.removeEventListener("DOMNodeInserted",
                                      domNodeInsertedEventListener, 
                                      true);
        evtTarget.removeEventListener("DOMNodeRemoved",
                                      domNodeRemovedEventListener, 
                                      true);
        evtTarget.removeEventListener("DOMCharacterDataModified",
                                      domCharacterDataModifiedListener, 
                                      true);

        SVGOMDocument svgDocument = (SVGOMDocument)document;
        CSSEngine cssEngine = svgDocument.getCSSEngine();
        if (cssEngine != null) {
            cssEngine.removeCSSEngineListener(cssPropertiesChangedListener);
            cssEngine.dispose();
        }
        if (focusManager != null) {
            focusManager.dispose();
        }
    }

    /**
     * Returns the SVGContext associated to the specified Node or null if any.
     */
    protected static SVGContext getSVGContext(Node node) {
        if (node instanceof SVGOMElement) {
            return ((SVGOMElement)node).getSVGContext();
        } else {
            return null;
        }
    }

    /**
     * Returns the SVGContext associated to the specified Node or null if any.
     */
    protected static BridgeUpdateHandler getBridgeUpdateHandler(Node node) {
        SVGContext ctx = getSVGContext(node);
        return (ctx == null) ? null : (BridgeUpdateHandler)ctx;
    }

    /**
     * The DOM EventListener invoked when an attribute is modified.
     */
    protected class DOMAttrModifiedEventListener implements EventListener {

        /**
         * Handles 'DOMAttrModified' event type.
         */
        public void handleEvent(Event evt) {
            Node node = (Node)evt.getTarget();
            BridgeUpdateHandler h = getBridgeUpdateHandler(node);
            if (h != null) {
                try {
                    h.handleDOMAttrModifiedEvent((MutationEvent)evt);
                } catch (Exception e) {
                    userAgent.displayError(e);
                }
            }
        }
    }

    /**
     * The DOM EventListener invoked when the mouse exits an element
     */
    protected class DOMMouseOutEventListener implements EventListener {
        public void handleEvent(Event evt) {
            MouseEvent me = (MouseEvent)evt;
            Element newTarget = (Element)me.getRelatedTarget();
            Cursor cursor = CursorManager.DEFAULT_CURSOR;
            if (newTarget != null)
                cursor = CSSUtilities.convertCursor
                    (newTarget, BridgeContext.this);
            if (cursor == null) 
                cursor = CursorManager.DEFAULT_CURSOR;

            userAgent.setSVGCursor(cursor);
        }
    }


    /**
     * The DOM EventListener invoked when the mouse mouves over a new 
     * element.
     * 
     * Here is how cursors are handled:
     * 
     */
    protected class DOMMouseOverEventListener implements EventListener {
        /**
         * Handles 'mouseover' MouseEvent event type.
         */
        public void handleEvent(Event evt) {
            Element target = (Element)evt.getTarget();
            Cursor cursor = CSSUtilities.convertCursor(target, BridgeContext.this);
            
            if (cursor != null) {
                userAgent.setSVGCursor(cursor);
            }
        }
    }
    
    /**
     * The DOM EventListener invoked when a node is added.
     */
    protected class DOMNodeInsertedEventListener implements EventListener {

        /**
         * Handles 'DOMNodeInserted' event type.
         */
        public void handleEvent(Event evt) {
            MutationEvent me = (MutationEvent)evt;
            BridgeUpdateHandler h = 
                getBridgeUpdateHandler(me.getRelatedNode());
            if (h != null) {
                try {
                    h.handleDOMNodeInsertedEvent(me);
                } catch (Exception e) {
                    userAgent.displayError(e);
                }
            }
        }
    }

    /**
     * The DOM EventListener invoked when a node is removed.
     */
    protected class DOMNodeRemovedEventListener implements EventListener {

        /**
         * Handles 'DOMNodeRemoved' event type.
         */
        public void handleEvent(Event evt) {
            Node node = (Node)evt.getTarget();
            BridgeUpdateHandler h = getBridgeUpdateHandler(node);
            if (h != null) {
                try {
                    h.handleDOMNodeRemovedEvent((MutationEvent)evt);
                } catch (Exception e) {
                    userAgent.displayError(e);
                }
            }
        }
    }

    /**
     * The DOM EventListener invoked when a character data is changed.
     */
    protected class DOMCharacterDataModifiedListener implements EventListener {

        /**
         * Handles 'DOMNodeRemoved' event type.
         */
        public void handleEvent(Event evt) {
            Node node = (Node)evt.getTarget();
            while (node != null && !(node instanceof SVGOMElement)) {
                node = node.getParentNode();
            }
            BridgeUpdateHandler h = getBridgeUpdateHandler(node);
            if (h != null) {
                try {
                    h.handleDOMCharacterDataModified((MutationEvent)evt);
                } catch (Exception e) {
                    userAgent.displayError(e);
                }
            }
        }
    }

    /**
     * The CSSEngineListener invoked when CSS properties are modified
     * on a particular element.
     */
    protected class CSSPropertiesChangedListener implements CSSEngineListener {

        /**
         * Handles CSSEngineEvent that describes the CSS properties
         * that have changed on a particular element.
         */
        public void propertiesChanged(CSSEngineEvent evt) {
            SVGContext ctx = getSVGContext(evt.getElement());
            if (ctx != null && (ctx instanceof BridgeUpdateHandler)) {
                ((BridgeUpdateHandler)ctx).handleCSSEngineEvent(evt);
            }
        }
    }

    // CSS context ////////////////////////////////////////////////////////////

    /**
     * Returns the Value corresponding to the given system color.
     */
    public Value getSystemColor(String ident) {
        return SystemColorSupport.getSystemColor(ident);
    }

    /**
     * Returns the value corresponding to the default font.
     */
    public Value getDefaultFontFamily() {
        // No cache needed since the default font family is asked only
        // one time on the root element (only if it does not have its
        // own font-family).
        SVGOMDocument doc = (SVGOMDocument)document;
        String str = userAgent.getDefaultFontFamily();
        return doc.getCSSEngine().parsePropertyValue
            (SVGConstants.CSS_FONT_FAMILY_PROPERTY, str);
    }

    /**
     * Returns a lighter font-weight.
     */
    public float getLighterFontWeight(float f) {
        return userAgent.getLighterFontWeight(f);
    }

    /**
     * Returns a bolder font-weight.
     */
    public float getBolderFontWeight(float f) {
        return userAgent.getBolderFontWeight(f);
    }

    /**
     * Returns the size of a px CSS unit in millimeters.
     */
    public float getPixelUnitToMillimeter() {
        return userAgent.getPixelUnitToMillimeter();
    }

    /**
     * Returns the size of a px CSS unit in millimeters.
     * This will be removed after next release.
     * @see #getPixelUnitToMillimeter()
     */
    public float getPixelToMillimeter() {
        return getPixelUnitToMillimeter();
            
    }

    /**
     * Returns the medium font size.
     */
    public float getMediumFontSize() {
        return userAgent.getMediumFontSize();
    }

    /**
     * Returns the width of the block which directly contains the
     * given element.
     */
    public float getBlockWidth(Element elt) {
        return getViewport(elt).getWidth();
    }

    /**
     * Returns the height of the block which directly contains the
     * given element.
     */
    public float getBlockHeight(Element elt) {
        return getViewport(elt).getHeight();
    }

    /**
     * This method throws a SecurityException if the resource
     * found at url and referenced from docURL
     * should not be loaded.
     * 
     * This is a convenience method to call checkLoadExternalResource
     * on the ExternalResourceSecurity strategy returned by 
     * getExternalResourceSecurity.
     *
     * @param scriptURL url for the script, as defined in
     *        the script's xlink:href attribute. If that
     *        attribute was empty, then this parameter should
     *        be null
     * @param docURL url for the document into which the 
     *        script was found.
     */
    public void 
        checkLoadExternalResource(ParsedURL resourceURL,
                                  ParsedURL docURL) throws SecurityException {
        userAgent.checkLoadExternalResource(resourceURL,
                                            docURL);
    }


    // bridge extensions support //////////////////////////////////////////////

    protected static List extensions = null;

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

