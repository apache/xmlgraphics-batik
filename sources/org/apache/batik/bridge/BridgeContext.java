/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.batik.dom.svg.SVGDocumentLoader;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.script.ConcreteInterpreterPool;
import org.apache.batik.script.InterpreterPool;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.views.DocumentView;

// FIXME: TO BE REMOVED
import org.apache.batik.gvt.renderer.StaticRendererFactory;
import org.apache.batik.gvt.filter.ConcreteGraphicsNodeRableFactory;
import org.apache.batik.gvt.renderer.StaticRenderer;
// END FIXME

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
public class BridgeContext implements SVGConstants {

    /**
     * The view CSS.
     */
    protected ViewCSS viewCSS;

    /**
     * The GVT builder that might be used to create a GVT subtree.
     */
    protected GVTBuilder gvtBuilder;

    /**
     * The current viewport.
     */
    protected Viewport viewport;

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
     * The current <tt>GraphicsNodeRenderContext</tt> for <tt>GraphicsNode</tt>.
     */
    protected GraphicsNodeRenderContext rc;

    /**
     * The interpreter pool used to handle scripts.
     */
    protected InterpreterPool interpreterPool;

    /**
     * The document loader used to load/create Document.
     */
    protected DocumentLoader documentLoader;

    /**
     * Constructs a new empty bridge context.
     */
    protected BridgeContext() {}

    /**
     * Constructs a new bridge context.
     * @param userAgent the user agent
     * @param rc the graphics node renderer context
     */
    public BridgeContext(UserAgent userAgent,
                         GraphicsNodeRenderContext rc) {
        this(userAgent,
             rc,
             new ConcreteInterpreterPool(),
             new BufferedDocumentLoader(
                 new SVGDocumentLoader(userAgent.getXMLParserClassName())));
    }

    /**
     * Constructs a new bridge context.
     * @param userAgent the user agent
     * @param rc the graphics node renderer context
     * @param interpreterPool the interpreter pool
     * @param documentLoader document loader
     */
    public BridgeContext(UserAgent userAgent,
                         GraphicsNodeRenderContext rc,
                         InterpreterPool interpreterPool,
                         DocumentLoader documentLoader) {
        this.userAgent = userAgent;
        this.viewport = new UserAgentViewport(userAgent);
        this.rc = rc;
        this.interpreterPool = interpreterPool;
        this.documentLoader = documentLoader;
        registerSVGBridges(this);
    }

    /**
     * Should be partially removed when removing the ViewCSS from the bridge.
     */
    void initialize(SVGDocument svgDocument, GVTBuilder gvtBuilder) {
        this.viewCSS = (ViewCSS)svgDocument.getRootElement();
        this.gvtBuilder = gvtBuilder;
    }

    //
    // methods for the attributes that can be shared between multiple documents
    //

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
    public void setUserAgent(UserAgent userAgent) {
        this.userAgent = userAgent;
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
    public void setInterpreterPool(InterpreterPool interpreterPool) {
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
    public void setDocumentLoader(DocumentLoader newDocumentLoader) {
        this.documentLoader = newDocumentLoader;
    }

    /**
     * Returns a <tt>GraphicsNodeRenderContext</tt> to use.
     */
    public GraphicsNodeRenderContext getGraphicsNodeRenderContext() {
        return rc;
    }

    /**
     * Sets the <tt>GraphicsNodeRenderContext</tt> to use.
     * @param rc the new GraphicsNodeRenderContext
     */
    public void setGraphicsNodeRenderContext(GraphicsNodeRenderContext rc) {
        this.rc = rc;
    }

    //
    // Properties that describe the current state of this bridge context
    //

    /**
     * Returns the viewport to use to compute percentages and units.
     */
    public Viewport getViewport() {
        return viewport;
    }

    /**
     * Sets the viewport to use to compute percentages and units to the
     * specified viewport.
     * @param newViewport the new viewport
     */
    public void setViewport(Viewport newViewport) {
        this.viewport = newViewport;
    }

    /**
     * Returns the view CSS.
     */
    public ViewCSS getViewCSS() {
        return viewCSS;
    }

    /**
     * Sets the view CSS used to access the style to the specified view CSS.
     * @param newViewCSS the new view CSS to consider
     */
    public void setViewCSS(ViewCSS newViewCSS) {
        this.viewCSS = newViewCSS;
    }

    //
    // Bindings methods
    //

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

    //
    // Bridge support
    //

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
     * Associates the specified <tt>Bridge</tt> object with the specified
     * namespace URI and local name.
     * @param namespaceURI the namespace URI
     * @param localName the local name
     * @param bridge the bridge that manages the element
     */
    public void putBridge(String namespaceURI, String localName,
                          Bridge bridge) {
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

        // local bridges to handle elements in custom namespace
        if (globalBridges != null) {
            Iterator it = globalBridges.keySet().iterator();
            while (it.hasNext()) {
                String ns = (String)it.next();
                Map m = (Map)globalBridges.get(ns);
                if (m != null) {
                    Iterator mit = m.keySet().iterator();
                    while (mit.hasNext()) {
                        String ln = (String)mit.next();
                        ctx.putBridge(ns, ln, (Bridge)m.get(ln));
                    }
                }
            }
        }

        // bridges to handle elements in the SVG namespace
        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_A_TAG,
                      new SVGAElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_CIRCLE_TAG,
                      new SVGCircleElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_CLIP_PATH_TAG,
                      new SVGClipPathElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_ELLIPSE_TAG,
                      new SVGEllipseElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_BLEND_TAG,
                      new SVGFeBlendElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI, SVG_FE_COLOR_MATRIX_TAG,
                      new SVGFeColorMatrixElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_COMPONENT_TRANSFER_TAG,
                      new SVGFeComponentTransferElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_COMPOSITE_TAG,
                      new SVGFeCompositeElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_CONVOLVE_MATRIX_TAG,
                      new SVGFeConvolveMatrixElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_DIFFUSE_LIGHTING_TAG,
                      new SVGFeDiffuseLightingElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_DISPLACEMENT_MAP_TAG,
                      new SVGFeDisplacementMapElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_FLOOD_TAG,
                      new SVGFeFloodElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_GAUSSIAN_BLUR_TAG,
                      new SVGFeGaussianBlurElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_IMAGE_TAG,
                      new SVGFeImageElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_MERGE_TAG,
                      new SVGFeMergeElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_MORPHOLOGY_TAG,
                      new SVGFeMorphologyElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_OFFSET_TAG,
                      new SVGFeOffsetElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_SPECULAR_LIGHTING_TAG,
                      new SVGFeSpecularLightingElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_TILE_TAG,
                      new SVGFeTileElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FE_TURBULENCE_TAG,
                      new SVGFeTurbulenceElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_FILTER_TAG,
                      new SVGFilterElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_G_TAG,
                      new SVGGElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_IMAGE_TAG,
                      new SVGImageElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_LINE_TAG,
                      new SVGLineElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_LINEAR_GRADIENT_TAG,
                      new SVGLinearGradientBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_MARKER_TAG,
                      new SVGMarkerElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_MASK_TAG,
                      new SVGMaskElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_PATH_TAG,
                      new SVGPathElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_PATTERN_TAG,
                      new SVGPatternElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_POLYLINE_TAG,
                      new SVGPolylineElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_POLYGON_TAG,
                      new SVGPolygonElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_RADIAL_GRADIENT_TAG,
                      new SVGRadialGradientBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_RECT_TAG,
                      new SVGRectElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_SVG_TAG,
                      new SVGSVGElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_SWITCH_TAG,
                      new SVGSwitchElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_TEXT_TAG,
                      new SVGTextElementBridge());

        ctx.putBridge(SVG_NAMESPACE_URI,
                      SVG_USE_TAG,
                      new SVGUseElementBridge());
    }

    /**
     * The global bridges.
     */
    protected static HashMap globalBridges;

    /**
     * Registers a new global bridge.
     */
    public static void registerGlobalBridge(String namespaceURI,
                                            String localName,
                                            Bridge bridge) {
        if (globalBridges == null) {
            globalBridges = new HashMap(11);
        }
        Map ns = (Map)globalBridges.get(namespaceURI);
        if (ns == null) {
            globalBridges.put(namespaceURI, ns = new HashMap(11));
        }
        ns.put(localName, bridge);
    }

    /** The Update manager.*/
    private BridgeUpdateManager updateManager = new BridgeUpdateManager(this);

    /**
     * Returns the update manager.
     * <b>Experimental method for dynamic behavior.</b>
     */
    public BridgeUpdateManager getBridgeUpdateManager(){
        return updateManager;
    }

    /**
     * The factory class for vending <tt>GraphicsNodeRable</tt> objects.
     */
    private GraphicsNodeRableFactory graphicsNodeRableFactory =
        new ConcreteGraphicsNodeRableFactory();

    public GraphicsNodeRableFactory getGraphicsNodeRableFactory(){
        return graphicsNodeRableFactory;
    }

    public void setGraphicsNodeRableFactory(GraphicsNodeRableFactory f) {
        graphicsNodeRableFactory = f;
    }

    //
    // --- END TO BE REMOVED ---
    //

}
