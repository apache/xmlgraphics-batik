/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.bridge;

import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class is responsible for creating a GVT tree using an SVG DOM tree.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class GVTBuilder implements SVGConstants {

    /**
     * Constructs a new builder.
     */
    public GVTBuilder() { }

    /**
     * Builds using the specified bridge context the specified SVG document.
     *
     * @param ctx the bridge context
     * @param document the SVG document to build
     * @exception BridgeException if an error occured while constructing
     * the GVT tree
     */
    public GraphicsNode build(BridgeContext ctx, Document document) {
        // the bridge context is now associated to one document
        ctx.setDocument(document);
        ctx.initializeDocument(document);

        // inform the bridge context the builder to use
        ctx.setGVTBuilder(this);

        // build the GVT tree
        RootGraphicsNode rootNode = new RootGraphicsNode();
        Element svgElement = document.getDocumentElement();
        GraphicsNode topNode = null;
        try {
            // get the appropriate bridge according to the specified element
            Bridge bridge = ctx.getBridge(svgElement);
            if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
                return null;
            }
            // create the associated composite graphics node
            GraphicsNodeBridge gnBridge = (GraphicsNodeBridge)bridge;
            topNode = gnBridge.createGraphicsNode(ctx, svgElement);
            if (topNode == null) {
                return null;
            }
            rootNode.getChildren().add(topNode);

            buildComposite(ctx, svgElement, (CompositeGraphicsNode)topNode);
            gnBridge.buildGraphicsNode(ctx, svgElement, topNode);
        } catch (BridgeException ex) {
            // update the exception with the missing parameters
            ex.setGraphicsNode(rootNode);
            Element errElement = ex.getElement();
            ex.setLineNumber(ctx.getDocumentLoader().getLineNumber(errElement));
            //ex.printStackTrace();
            throw ex; // re-throw the udpated exception
        }

        // For cursor handling
        if (ctx.isInteractive()) {
            ctx.addUIEventListeners(document);

            // register GVT listeners for AWT event support
            BridgeEventSupport.addGVTListener(ctx, document);
        }

        // <!> FIXME: TO BE REMOVED
        if (ctx.isDynamic()) {
            // register DOM listeners for dynamic support
            ctx.addDOMListeners();
        }
        return rootNode;
    }

    /**
     * Builds using the specified bridge context the specified Element.
     *
     * @param ctx the bridge context
     * @param e the element to build
     * @exception BridgeException if an error occured while constructing
     * the GVT tree
     */
    public GraphicsNode build(BridgeContext ctx, Element e) {
        // get the appropriate bridge according to the specified element
        Bridge bridge = ctx.getBridge(e);
        if (bridge instanceof GenericBridge) {
            // If it is a GenericBridge just handle it and return.
            ((GenericBridge) bridge).handleElement(ctx, e);
            return null;
        } else if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
            return null;
        }
        // create the associated graphics node
        GraphicsNodeBridge gnBridge = (GraphicsNodeBridge)bridge;
        // check the display property
        if (!gnBridge.getDisplay(e)) {
            return null;
        }
        GraphicsNode gn = gnBridge.createGraphicsNode(ctx, e);
        if (gn != null) {
            if (gnBridge.isComposite()) {
                buildComposite(ctx, e, (CompositeGraphicsNode)gn);
            } else {
                handleGenericBridges(ctx, e);
            }
            gnBridge.buildGraphicsNode(ctx, e, gn);
        }
        // <!> FIXME: see build(BridgeContext, Element)
        // + may load the script twice (for example
        // outside 'use' is ok versus local 'use' maybe wrong).
        if (ctx.isDynamic()) {
            //BridgeEventSupport.loadScripts(ctx, e);
        }
        return gn;
    }

    /**
     * Builds a composite Element.
     *
     * @param ctx the bridge context
     * @param e the element to build
     * @param parent the composite graphics node, parent of the
     *               graphics node to build
     * @exception BridgeException if an error occured while constructing
     * the GVT tree
     */
    protected void buildComposite(BridgeContext ctx,
                                  Element e,
                                  CompositeGraphicsNode parentNode) {
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                buildGraphicsNode(ctx, (Element)n, parentNode);
            }
        }
    }

    /**
     * Builds a 'leaf' Element.
     *
     * @param ctx the bridge context
     * @param e the element to build
     * @param parent the composite graphics node, parent of the
     *               graphics node to build
     * @exception BridgeException if an error occured while constructing
     * the GVT tree
     */
    protected void buildGraphicsNode(BridgeContext ctx,
                                     Element e,
                                     CompositeGraphicsNode parentNode) {
        // Check for interruption.
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedBridgeException();
        }
        // get the appropriate bridge according to the specified element
        Bridge bridge = ctx.getBridge(e);
        if (bridge instanceof GenericBridge) {
            // If it is a GenericBridge just handle it and return.
            ((GenericBridge) bridge).handleElement(ctx, e);
            return;
        } else if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
            return;
        }
        // check the display property
        if (!CSSUtilities.convertDisplay(e)) {
            return;
        }
        GraphicsNodeBridge gnBridge = (GraphicsNodeBridge)bridge;
        try {
            // create the associated graphics node
            GraphicsNode gn = gnBridge.createGraphicsNode(ctx, e);
            if (gn != null) {
                // attach the graphics node to the GVT tree now !
                parentNode.getChildren().add(gn);
                // check if the element has children to build
                if (gnBridge.isComposite()) {
                    buildComposite(ctx, e, (CompositeGraphicsNode)gn);
                } else {
                    // if not then still handle the GenericBridges
                    handleGenericBridges(ctx, e);
                }
                gnBridge.buildGraphicsNode(ctx, e, gn);
            }
        } catch (BridgeException ex) {
            // some bridge may decide that the node in error can be
            // displayed (e.g. polyline, path...)
            // In this case, the exception contains the GraphicsNode
            GraphicsNode errNode = ex.getGraphicsNode();
            if (errNode != null) {
                parentNode.getChildren().add(errNode);
                gnBridge.buildGraphicsNode(ctx, e, errNode);
                ex.setGraphicsNode(null);
            }
            //ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * Handles any GenericBridge elements which are children of the
     * specified element.
     * @param ctx the bridge context
     * @param e the element whose child elements should be handled
     */
    protected void handleGenericBridges(BridgeContext ctx, Element e) {
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Element) {
                Element e2 = (Element) n;
                Bridge b = ctx.getBridge(e2);
                if (b instanceof GenericBridge) {
                    ((GenericBridge) b).handleElement(ctx, e2);
                }
            }
        }
    }
}
