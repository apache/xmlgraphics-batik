/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.BuilderException;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.GraphicsNodeBridge;

import org.apache.batik.css.AbstractViewCSS;
import org.apache.batik.css.HiddenChildElement;
import org.apache.batik.css.AbstractViewCSS;
import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.css.HiddenChildElement;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeException;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
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

        RootGraphicsNode root = ctx.getGVTFactory().createRootGraphicsNode();
        Element svgRoot = svgDocument.getDocumentElement();

        // Now, build corresponding canvas
        Bridge bridge = ctx.getBridge(svgRoot);
        if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
            return root;
        }

        GraphicsNode treeRoot = null;
        GraphicsNodeBridge graphicsNodeBridge = (GraphicsNodeBridge) bridge;

        try {
            treeRoot = graphicsNodeBridge.createGraphicsNode(ctx, svgRoot);
            buildComposite(ctx,
                           (CompositeGraphicsNode)treeRoot,
                           svgRoot.getFirstChild());
            graphicsNodeBridge.buildGraphicsNode(treeRoot, ctx, svgRoot);

            // <!> TODO this should be done only if we want binding !!!!
            BridgeEventSupport.loadScripts(ctx, svgDocument);
            EventTarget target = (EventTarget) svgRoot;
            target.addEventListener("DOMAttrModified",
                                    new BridgeDOMAttrModifiedListener
                                    ((ConcreteBridgeContext)ctx),
                                    true);
            EventListener listener =
               new BridgeDOMInsertedRemovedListener((ConcreteBridgeContext)ctx);
            // Adds the Listener on Attr Modified event.
            target.addEventListener("DOMNodeInserted", listener, true);
            // Adds the Listener on Attr Modified event.
            target.addEventListener("DOMNodeRemoved", listener, true);
            BridgeEventSupport.addGVTListener(ctx, svgRoot);
            // <!> END TODO

        } catch (BuilderException ex) {
            ex.setRootGraphicsNode(root);
            throw ex;
        } finally {
            if (treeRoot != null) {
                root.getChildren().add(treeRoot);
            }
        }
        return root;
    }


    /**
     * Builds a GVT tree using the specified context and SVG element
     * @param ctx the context to use
     * @param element element for which a GVT representation should be built
     */
    public GraphicsNode build(BridgeContext ctx, Element element){

        Bridge bridge = ctx.getBridge(element);

        if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
            return null;
        }

        GraphicsNode treeRoot = null;
        GraphicsNodeBridge graphicsNodeBridge = (GraphicsNodeBridge)bridge;
        treeRoot = graphicsNodeBridge.createGraphicsNode(ctx, element);
        if(treeRoot instanceof CompositeGraphicsNode) {
            buildComposite(ctx,
                           (CompositeGraphicsNode)treeRoot,
                           element.getFirstChild());
        }
        graphicsNodeBridge.buildGraphicsNode(treeRoot, ctx, element);
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

        Bridge bridge = ctx.getBridge(e);

        if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
            return;
        }
        GraphicsNode childGVTNode = null;
        GraphicsNodeBridge gnb = (GraphicsNodeBridge)bridge;
        List gvtChildList = composite.getChildren();
        try {
            childGVTNode = gnb.createGraphicsNode(ctx, e);
            gvtChildList.add(childGVTNode);
            if (gnb.isContainer()) {
                buildComposite(ctx,
                               (CompositeGraphicsNode)childGVTNode,
                               e.getFirstChild());
            } else if (TAG_USE.equals(e.getLocalName())) {
                URIResolver ur;
                ur = new URIResolver((SVGDocument)e.getOwnerDocument(),
                                 ctx.getDocumentLoader());

                String href = XLinkSupport.getXLinkHref(e);
                try {
                    Node n = ur.getNode(href);
                    if (n.getOwnerDocument() == null) {
                        throw new Error("Can't use documents");
                    }
                    Element elt = (Element)n;
                    boolean local =
                        n.getOwnerDocument() == e.getOwnerDocument();

                    Element inst;
                    if (local) {
                        inst = (Element)elt.cloneNode(true);
                    } else {
                        inst = (Element)e.getOwnerDocument().
                            importNode(elt, true);
                    }

                    if (inst instanceof SVGSymbolElement) {
                        Element tmp = e.getOwnerDocument().createElementNS
                            (SVG_NAMESPACE_URI, TAG_SVG);
                        for (n = inst.getFirstChild();
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

                    if (!local) {
                        SVGOMDocument doc;
                        doc = (SVGOMDocument)elt.getOwnerDocument();
                        SVGOMDocument d;
                        d = (SVGOMDocument)e.getOwnerDocument();
                        computeStyleAndURIs(elt,  (ViewCSS)doc.getDefaultView(),
                                            inst, (ViewCSS)d.getDefaultView(),
                                           ((SVGOMDocument)doc).getURLObject());
                    }

                    buildGraphicsNode(ctx,
                                      (CompositeGraphicsNode)childGVTNode,
                                      inst);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (TAG_SWITCH.equals(e.getLocalName())) {
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
            gnb.buildGraphicsNode(childGVTNode, ctx, e);
        } catch (BridgeException ex) {
            if (ex.getGraphicsNode() != null) {
                GraphicsNode gn = ex.getGraphicsNode();
                gnb.buildGraphicsNode(gn, ctx, e);
                gvtChildList.add(gn);
            } else if (childGVTNode != null) {
                // we have to remove the graphics node if it has been added
                gvtChildList.remove(childGVTNode);
            }
            throw new BuilderException(e, ex.getMessage());
        } catch (DOMException ex) {
            if (childGVTNode != null) {
                // we have to remove the graphics node if it has been added
                gvtChildList.remove(childGVTNode);
            }
            throw new BuilderException(e, ex.getMessage());
        }
    }

/**
 * Partially computes the style in the use tree and set it in
 * the target tree.
 */
public void computeStyleAndURIs(Element use, ViewCSS uv,
                                Element def, ViewCSS dv, URL url)
    throws MalformedURLException {
    String href = XLinkSupport.getXLinkHref(def);

    if (!href.equals("")) {
        XLinkSupport.setXLinkHref(def, new URL(url, href).toString());
    }

    CSSOMReadOnlyStyleDeclaration usd;
    AbstractViewCSS uview = (AbstractViewCSS)uv;

    usd = (CSSOMReadOnlyStyleDeclaration)uview.computeStyle(use, null);
    updateURIs(usd, url);
    ((AbstractViewCSS)dv).setComputedStyle(def, null, usd);

    for (Node un = use.getFirstChild(), dn = def.getFirstChild();
         un != null;
         un = un.getNextSibling(), dn = dn.getNextSibling()) {
        if (un.getNodeType() == Node.ELEMENT_NODE) {
            computeStyleAndURIs((Element)un, uv, (Element)dn, dv, url);
        }
    }
}

/**
 * Updates the URIs in the given style declaration.
 */
protected void updateURIs(CSSOMReadOnlyStyleDeclaration sd, URL url)
    throws MalformedURLException {
    int len = sd.getLength();
    for (int i = 0; i < len; i++) {
        String name = sd.item(i);
        CSSValue val = sd.getLocalPropertyCSSValue(name);
        if (val != null &&
            val.getCssValueType() ==
            CSSPrimitiveValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue pv = (CSSPrimitiveValue)val;
            if (pv.getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
                CSSOMReadOnlyValue v =
                    new CSSOMReadOnlyValue
                    (new ImmutableString(CSSPrimitiveValue.CSS_URI,
                                         new URL(url, pv.getStringValue()).toString()));
                sd.setPropertyCSSValue(name, v,
                                       sd.getLocalPropertyPriority(name),
                                       sd.getLocalPropertyOrigin(name));
            }
        }
    }
}

}
