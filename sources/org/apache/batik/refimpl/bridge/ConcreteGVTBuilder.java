/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.util.List;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.GVTBuilder;

import org.apache.batik.css.AbstractViewCSS;
import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.HiddenChildElement;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.bridge.Bridge;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGSymbolElement;
import org.w3c.dom.svg.SVGTests;

/**
 * This class is responsible for creating the GVT tree from
 * the SVG Document.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ConcreteGVTBuilder implements GVTBuilder, SVGConstants {
    /**
     * Builds a GVT tree using the specified context and SVG document.
     * @param ctx the context to use
     * @param svgDocument the DOM tree that represents an SVG document
     */
    public GraphicsNode build(BridgeContext ctx, Document svgDocument){

        Element svgRoot = svgDocument.getDocumentElement();

        // Now, build corresponding canvas

        GraphicsNodeBridge graphicsNodeBridge =
            (GraphicsNodeBridge)ctx.getBridge(svgRoot);

        if (graphicsNodeBridge == null)
            throw new IllegalArgumentException(
                 "Bridge for "+svgRoot.getTagName()+" is not registered");

        GraphicsNode treeRoot;
        treeRoot = graphicsNodeBridge.createGraphicsNode(ctx, svgRoot);

        buildComposite(ctx,
                       (CompositeGraphicsNode)treeRoot,
                       svgRoot.getFirstChild());

        // Adds the Listener on Attr Modified event.
        ((EventTarget)svgRoot).
            addEventListener("DOMAttrModified",
                             new BridgeDOMAttrModifiedListener
                                 ((ConcreteBridgeContext)ctx),
                             true);

        EventListener listener;
        listener = new BridgeDOMInsertedRemovedListener
            ((ConcreteBridgeContext)ctx);
        // Adds the Listener on Attr Modified event.
        ((EventTarget)svgRoot).
            addEventListener("DOMNodeInserted", listener, true);
        // Adds the Listener on Attr Modified event.
        ((EventTarget)svgRoot).
            addEventListener("DOMNodeRemoved", listener, true);

        // <!> TODO as previous lines this should be done only if we want
        // binding !!!!
        BridgeEventSupport.addGVTListener(ctx, svgRoot);
        BridgeEventSupport.loadScripts(ctx, svgDocument);

        return treeRoot;
    }


    /**
     * Builds a GVT tree using the specified context and SVG element
     * @param ctx the context to use
     * @param element element for which a GVT representation should be built
     */
    public GraphicsNode build(BridgeContext ctx, Element element){

        GraphicsNodeBridge graphicsNodeBridge =
            (GraphicsNodeBridge)ctx.getBridge(element);

        if (graphicsNodeBridge == null)
            throw new IllegalArgumentException(
                 "Bridge for "+element.getTagName()+" is not registered");

        GraphicsNode treeRoot = graphicsNodeBridge.createGraphicsNode(ctx, element);

        if(treeRoot instanceof CompositeGraphicsNode){
            buildComposite(ctx,
                           (CompositeGraphicsNode)treeRoot,
                           element.getFirstChild());
        }

        return treeRoot;
    }

    /**
     * Creates GraphicsNode from the children of the input SVGElement and
     * appends them to the input CompositeGraphicsNode.
     */
    protected void buildComposite(BridgeContext ctx,
                                  CompositeGraphicsNode composite,
                                  Node first){
        for (Node child = first;
             child != null;
             child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                buildGraphicsNode(ctx, composite, (Element)child);
            }
        }
    }

    /**
     * Build a single node.
     */
    protected void buildGraphicsNode(BridgeContext ctx,
                                     CompositeGraphicsNode composite,
                                     Element e) {
        List gvtChildList = composite.getChildren();

        Bridge bridge = ctx.getBridge(e);
        if ((bridge != null) && (bridge instanceof GraphicsNodeBridge)) {
            GraphicsNodeBridge gnb = (GraphicsNodeBridge)bridge;
            GraphicsNode childGVTNode
                = gnb.createGraphicsNode(ctx, e);

            gvtChildList.add(childGVTNode);
            if (gnb.isContainer()) {
                buildComposite(ctx,
                               (CompositeGraphicsNode)childGVTNode,
                               e.getFirstChild());
            } else if (e.getLocalName().equals(TAG_USE)) {
                String href = XLinkSupport.getXLinkHref(e);
                if (href.startsWith("#")) {
                    Document doc = e.getOwnerDocument();
                    Element elt = doc.getElementById(href.substring(1));
                    Element inst = (Element)elt.cloneNode(true);
                    if (inst instanceof SVGSymbolElement) {
                        Element tmp = e.getOwnerDocument().createElementNS
                            (SVG_NAMESPACE_URI, TAG_SVG);
                        for (Node n = inst.getFirstChild();
                             n != null;
                             n = inst.getFirstChild()) {
                            tmp.appendChild(n);
                        }
                        NamedNodeMap attrs = inst.getAttributes();
                        int len = attrs.getLength();
                        for (int i = 0; i < len; i++) {
                            Attr attr = (Attr)attrs.item(i);
                            String ns = attr.getNamespaceURI();
                            tmp.setAttributeNS(attr.getNamespaceURI(),
                                               attr.getName(),
                                               attr.getValue());
                        }
                        tmp.setAttributeNS(null, ATTR_WIDTH, "100%");
                        tmp.setAttributeNS(null, ATTR_HEIGHT, "100%");
                        inst = tmp;
                    }
                    ((HiddenChildElement)inst).setParentElement(e);
                    if (inst instanceof SVGSVGElement) {
                        if (e.hasAttributeNS(null, ATTR_WIDTH)) {
                            inst.setAttributeNS(null, ATTR_WIDTH,
                                                e.getAttributeNS(null,
                                                                 ATTR_WIDTH));
                        }
                        if (e.hasAttributeNS(null, ATTR_HEIGHT)) {
                            inst.setAttributeNS(null, ATTR_HEIGHT,
                                                e.getAttributeNS(null,
                                                                 ATTR_HEIGHT));
                        }
                    }
                    /*
                    SVGDocument svgdoc = (SVGDocument)doc;
                    computeStyle(elt,  (ViewCSS)svgdoc.getRootElement(),
                                 inst, ctx.getViewCSS());
                    */
                    buildGraphicsNode(ctx,
                                      (CompositeGraphicsNode)childGVTNode,
                                      inst);
                }
            } else if (e.getLocalName().equals(TAG_SWITCH)) {
                for (Node n = e.getFirstChild();
                     n != null;
                     n = n.getNextSibling()) {
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        if (n instanceof SVGTests) {
                            if (SVGUtilities.matchUserAgent
                                ((Element)n,
                                 ctx.getUserAgent())) {
                                buildGraphicsNode
                                    (ctx,
                                     (CompositeGraphicsNode)childGVTNode,
                                     (Element)n);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     */
    protected void computeStyle(Element use, ViewCSS uv,
                                Element def, ViewCSS dv) {
        CSSOMReadOnlyStyleDeclaration usd;
        AbstractViewCSS uview = (AbstractViewCSS)uv;
        usd = (CSSOMReadOnlyStyleDeclaration)uview.computeStyle(use, null);
        ((AbstractViewCSS)dv).setComputedStyle(def, null, usd);
        for (Node un = use.getFirstChild(), dn = def.getFirstChild();
             un != null;
             un = un.getNextSibling(), dn = dn.getNextSibling()) {
            if (un.getNodeType() == Node.ELEMENT_NODE) {
                computeStyle((Element)un, uv, (Element)dn, dv);
            }
        }
    }
}
