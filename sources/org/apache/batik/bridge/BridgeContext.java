/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.GVTFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.parser.ParserFactory;
import org.apache.batik.script.InterpreterPool;

import org.w3c.dom.Element;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGSVGElement;
import java.util.List;

/**
 * This class is the global context used by all <tt>Bridge</tt> instances.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @version $Id$
 */
public interface BridgeContext {

    /**
     * Returns the interpreter pool that can be used for scripting.
     */
    InterpreterPool getInterpreterPool();

    /**
     * Returns the document loader to use to load a document.
     */
    DocumentLoader getDocumentLoader();

    /**
     * Returns the current viewport to use to compute percentages and units.
     */
    Viewport getCurrentViewport();

    /**
     * Sets the current viewport to use to compute percentages and
     * units to the specified SVG element.
     * @param newViewport the new viewport
     */
    void setCurrentViewport(Viewport newViewport);

    /**
     * Sets the document loader to use to load a document.
     * @param newDocumentLoader the new document loader
     */
    void setDocumentLoader(DocumentLoader newDocumentLoader);

    /**
     * Returns the <code>UserAgent</code> than can used by the bridge.
     */
    UserAgent getUserAgent();

    /**
     * Returns the GVT builder that is currently used to build the GVT tree.
     */
    GVTBuilder getGVTBuilder();

    /**
     * Sets the GVT builder used to build the GVT tree.
     */
    void setGVTBuilder(GVTBuilder gvtBuilder);

    /**
     * Sets the User agent.
     */
    void setUserAgent(UserAgent ua);

    /**
     * Binds a GraphicsNode and an Element.
     */
    void bind(Element element, GraphicsNode node);

    /**
     * UnBinds an Element and its graphics Node.
     */
    void unbind(Element element);

    /**
     * Returns the GraphicsNode bound to the specified Element if any.
     */
    GraphicsNode getGraphicsNode(Element element);

    /**
     * Returns the view CSS
     */
    ViewCSS getViewCSS();

    /**
     * Sets the view CSS.
     */
    void setViewCSS(ViewCSS viewCSS);

    /**
     * Returns the Element bound to the specified GraphicsNode if any.
     */
    Element getElement(GraphicsNode node);

    /**
     * Binds a style element to a style reference.
     * Several style reference can be bound to the same style element.
     */
    void bind(Element element, StyleReference reference);

    /**
     * Returns an enumeration of all style refence for
     * the specified style element.
     */
    List getStyleReferenceList(Element element);

    /**
     * Returns the bridge associated with the specified element.
     */
    Bridge getBridge(Element element);

    /**
     * Associates the specified <tt>Bridge</tt> object with the specified
     * namespace URI nad local name.
     * @param namespaceURI the namespace URI
     * @param localName the local name
     * @param bridge the bridge object
     */
    void putBridge(String namespaceURI, String localName, Bridge bridge);

    /**
     * Removes the <tt>Bridge</tt> object associated to the specified
     * namespace URI and local name.
     * @param namespaceURI the namespace URI
     * @param localName the local name
     */
    void removeBridge(String namespaceURI, String localName);

    /**
     * Returns the GVT Factory that can be used to create <tt>GraphicsNode</tt>
     * objects.
     */
    GVTFactory getGVTFactory();

    /**
     * Sets the GVT factory to use to create <tt>GraphicsNode</tt>
     * @param gvtFactory the new gvt factory to use
     */
    void setGVTFactory(GVTFactory gvtFactory);

    /**
     * Returns the Parser factory that can be used to parse the
     * attributes of an <tt>Element</tt>.
     */
    ParserFactory getParserFactory();

    /**
     * Sets the Parser factory to use to parse XML attributes.
     * @param parserFactory the new parser factory to use
     */
    void setParserFactory(ParserFactory parserFactory);

    /**
     * Returns a GraphicsNodeRable factory
     */
    GraphicsNodeRableFactory getGraphicsNodeRableFactory();

    /**
     * Sets the GraphicsNodeRableFactory to use.
     * @param f the new GraphicsNodeRableFactory
     */
    void setGraphicsNodeRableFactory(GraphicsNodeRableFactory f);
}
