/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import org.apache.batik.gvt.GVTFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.parser.ParserFactory;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.StyleReference;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;

import org.apache.batik.script.InterpreterPool;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.views.DocumentView;
import org.w3c.dom.css.ViewCSS;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;


/**
 * This class is the global context used by all <tt>Bridge</tt>
 * instances.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class ConcreteBridgeContext implements BridgeContext {

    /** The Update manager.*/
    private BridgeUpdateManager updateManager;

    /**
     * HashMap:
     * key is an SVG Element,
     * value is a GraphicsNode
     */
    private HashMap elementNodeMap;

    /**
     * HashMap:
     * key is GraphicsNode
     * value is a SVG Element.
     */
    private HashMap nodeElementMap;

    /**
     * HashMap:
     * key is style Element,
     * value is a list of styleReference
     */
    private HashMap elementStyleAttMap;

    /**
     * HashMap:
     * key is GraphicsNode.
     * value is list of styleElement.
     */
    private HashMap nodeStyleMap;

    /**
     * The factory that creates GVT objects.
     */
    private GVTFactory gvtFactory;

    /**
     * The factory that creates parsers.
     */
    private ParserFactory parserFactory;

    /**
     * The pool that contains all bridges.
     */
    private BridgePool bridgePool = new BridgePool();

    /**
     * The interpreter pool.
     */
    private InterpreterPool interpreterPool;

    /**
     * The user agent.
     */
    private UserAgent userAgent;

    /**
     * The CSS view.
     */
    private ViewCSS viewCSS;

    /**
     * The GVT Builder.
     */
    private GVTBuilder gvtBuilder;

    /**
     * The factory class for vending <tt>GraphicsNodeRable</tt> objects.
     */
    private GraphicsNodeRableFactory graphicsNodeRableFactory;

    /**
     * The document loader used to load/create Document.
     */
    private DocumentLoader documentLoader;

    /**
     * Constructs a new empty <tt>BridgeContext</tt>.
     */
    public ConcreteBridgeContext() {
        updateManager = new BridgeUpdateManager(this);
    }

    public GVTBuilder getGVTBuilder() {
        return gvtBuilder;
    }

    public void setGVTBuilder(GVTBuilder gvtBuilder) {
        this.gvtBuilder = gvtBuilder;
    }

    public DocumentLoader getDocumentLoader() {
        return documentLoader;
    }

    public void setDocumentLoader(DocumentLoader newDocumentLoader) {
        this.documentLoader = newDocumentLoader;
    }

    public ViewCSS getViewCSS() {
        return viewCSS;
    }

    public void setViewCSS(ViewCSS viewCSS) {
        this.viewCSS = viewCSS;
    }

    public BridgeUpdateManager getBridgeUpdateManager(){
        return updateManager;
    }

    public InterpreterPool getInterpreterPool() {
        return interpreterPool;
    }

    public void setInterpreterPool(InterpreterPool interpreterPool) {
        this.interpreterPool = interpreterPool;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(UserAgent userAgent) {
        this.userAgent = userAgent;
    }

    public Bridge getBridge(Element element) {
        String namespaceURI = element.getNamespaceURI();
        String localName = element.getLocalName();
        return bridgePool.getBridge(namespaceURI, localName);
    }

    public void putBridge(String namespaceURI, String localName,
                          Bridge bridge) {
        bridgePool.putBridge(namespaceURI, localName, bridge);
    }

    public void removeBridge(String namespaceURI, String localName) {
        bridgePool.removeBridge(namespaceURI, localName);
    }

    /**
     * Binds and Element (SVG element) to a GraphicsNode.
     */
    public void bind(Element element, GraphicsNode node){
        // Create the hashmap lazily
        if (elementNodeMap == null) {
            elementNodeMap = new HashMap();
            nodeElementMap = new HashMap();
        }
        elementNodeMap.put(element, node);
        nodeElementMap.put(node, element);
    }

    /**
     * UnBinds an Element (SVG element) and its corresponding GraphicsNode.
     */
    public void unbind(Element element){
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
     * Removes all bindings between a style Element and the
     * specified GraphicsNode.
     */
    private void removeStyleReferences(GraphicsNode node){
        // Get the list of style Elements used by this node
        if (nodeStyleMap == null) return;
        List styles = (List)nodeStyleMap.get(node);
        if (styles != null)
            nodeStyleMap.remove(node);
        for (Iterator it = styles.iterator(); it.hasNext();){
            Element style = (Element)it.next();
            removeStyleReference(node, style);
        }
    }

    /**
     * Removes all StyleReference corresponding to the specified GraphicsNode.
     */
    private void removeStyleReference(GraphicsNode node, Element style){
        if (elementStyleAttMap == null) return;
        LinkedList list = (LinkedList)elementStyleAttMap.get(style);
        List removed = null;
        if (list == null) return;
        for (Iterator it = list.iterator(); it.hasNext();){
            StyleReference styleRef = (StyleReference)it.next();
            if (styleRef.getGraphicsNode()==node) {
                if (removed == null)
                    removed = new LinkedList();
                removed.add(styleRef);
            }
        }


        if (removed != null) {
            for (Iterator it = removed.iterator(); it.hasNext();)
                list.remove(it.next());
        }
        if (list.size() == 0)
            elementStyleAttMap.remove(style);

    }

    /**
     * Returns the GraphicsNode bound to the specified SVG Element.
     */
    public GraphicsNode getGraphicsNode(Element element){
        if (elementNodeMap != null)
            return (GraphicsNode)elementNodeMap.get(element);
        else
            return null;
    }

    /**
     * Returns the SVG Element bound to the specified GraphicsNode.
     */
    public Element getElement(GraphicsNode node){
        if (nodeElementMap != null)
            return (Element)nodeElementMap.get(node);
        else
            return null;
    }

    public void bind(Element element, StyleReference reference){
        if (elementStyleAttMap == null) {
            elementStyleAttMap = new HashMap();
        }
        LinkedList list = (LinkedList)elementStyleAttMap.get(element);
        if (list == null) {
            list = new LinkedList();
            elementStyleAttMap.put(element, list);
        }
        list.add(reference);

        if (nodeStyleMap==null)
            nodeStyleMap = new HashMap();

        GraphicsNode node = reference.getGraphicsNode();
        list = (LinkedList)nodeStyleMap.get(node);
        if (list == null) {
            list = new LinkedList();
            nodeStyleMap.put(node, list);
        }
        list.add(element);
    }


    public List getStyleReferenceList(Element element){
        if (elementStyleAttMap == null)
            return Collections.EMPTY_LIST;
        else {
            LinkedList list = (LinkedList)elementStyleAttMap.get(element);
            if (list != null)
                return list;
            else
                return Collections.EMPTY_LIST;
        }
    }

    public GVTFactory getGVTFactory(){
        return gvtFactory;
    }

    public void setGVTFactory(GVTFactory gvtFactory){
        this.gvtFactory = gvtFactory;
    }

    public void setParserFactory(ParserFactory parserFactory){
        this.parserFactory = parserFactory;
    }

    public ParserFactory getParserFactory(){
        return parserFactory;
    }

    public GraphicsNodeRableFactory getGraphicsNodeRableFactory(){
        return graphicsNodeRableFactory;
    }

    public void setGraphicsNodeRableFactory(GraphicsNodeRableFactory f) {
        graphicsNodeRableFactory = f;
    }
}
