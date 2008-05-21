/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;

import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;foreignObject&gt; element.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class SVGForeignObjectElementBridge extends AbstractGraphicsNodeBridge {

    /**
     * The ForeignObjectHandler object that will construct the GVT
     * representation of the foreign content.
     */
    protected ForeignObjectHandler handler;

    /**
     * The foreign content.
     */
    protected Element foreignContent;

    /**
     * Constructs a new bridge for a &lt;foreignObject&gt; element.
     */
    public SVGForeignObjectElementBridge() {
    }

    /**
     * Returns 'foreignObject'.
     */
    public String getLocalName() {
        return SVG_FOREIGN_OBJECT_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGForeignObjectElementBridge();
    }

    /**
     * Creates a graphics node using the specified BridgeContext and for the
     * specified element.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        Node n = e.getFirstChild();
        while (n != null && n.getNodeType() != Node.ELEMENT_NODE) {
            n = n.getNextSibling();
        }
        if (n == null) {
            return null;
        }

        Element elt = (Element) n;

        CompositeGraphicsNode cgn =
            (CompositeGraphicsNode) super.createGraphicsNode(ctx, e);
        if (cgn == null) {
            return null;
        }

        ForeignObjectHandler handler =
            ctx.createForeignObjectHandler(elt.getNamespaceURI());

        if (handler == null) {
            return null;
        }

        buildForeignObjectNode(ctx, e, elt, cgn, handler);

        if (ctx.isDynamic()) {
            this.handler = handler;
            this.foreignContent = elt;
        }

        return cgn;
    }

    /**
     * Builds the graphics node according to the ForeignObjectHandler.
     */
    public void buildForeignObjectNode(BridgeContext ctx,
                                       Element e,
                                       Element foreignContent,
                                       CompositeGraphicsNode cgn,
                                       ForeignObjectHandler handler) {

        while (!cgn.isEmpty()) {
            cgn.remove(cgn.size() - 1);
        }

        UnitProcessor.Context uctx = updatePosition(ctx, e, cgn);
        String s;

        // 'width' attribute - required
        s = e.getAttributeNS(null, SVG_WIDTH_ATTRIBUTE);
        float w;
        if (s.length() != 0) {
            w = UnitProcessor.svgHorizontalLengthToUserSpace
                (s, SVG_WIDTH_ATTRIBUTE, uctx);
        } else {
            throw new BridgeException(ctx, e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_WIDTH_ATTRIBUTE, s});
        }

        // 'height' attribute - required
        s = e.getAttributeNS(null, SVG_HEIGHT_ATTRIBUTE);
        float h;
        if (s.length() != 0) {
            h = UnitProcessor.svgVerticalLengthToUserSpace
                (s, SVG_HEIGHT_ATTRIBUTE, uctx);
        } else {
            throw new BridgeException(ctx, e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_HEIGHT_ATTRIBUTE, s});
        }

        if (w == 0 || h == 0) {
            return;
        }

        GraphicsNode gn = handler.createGraphicsNode(ctx, foreignContent, w, h);

        if (gn != null) {
            cgn.add(gn);
        }
    }

    /**
     * Updates the transform on the graphics node to reflect the 'x' and 'y'
     * attributes on the element.
     */
    protected UnitProcessor.Context updatePosition
            (BridgeContext ctx,
             Element e,
             CompositeGraphicsNode cgn) {

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, e);
        String s;

        // 'x' attribute - default is 0
        s = e.getAttributeNS(null, SVG_X_ATTRIBUTE);
        float x = 0;
        if (s.length() != 0) {
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (s, SVG_X_ATTRIBUTE, uctx);
        }

        // 'y' attribute - default is 0
        s = e.getAttributeNS(null, SVG_Y_ATTRIBUTE);
        float y = 0;
        if (s.length() != 0) {
            y = UnitProcessor.svgVerticalCoordinateToUserSpace
                (s, SVG_Y_ATTRIBUTE, uctx);
        }

        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        cgn.setTransform(at);

        return uctx;
    }

    /**
     * Creates a {@link CompositeGraphicsNode}.
     */
    protected GraphicsNode instantiateGraphicsNode() {
        return new CompositeGraphicsNode();
    }

    /**
     * Returns false as 'foreignObject' elements do not have SVG content.
     */
    public boolean isComposite() {
        return false;
    }

    /**
     * This method is invoked during the build phase if the document
     * is dynamic. The responsability of this method is to ensure that
     * any dynamic modifications of the element this bridge is
     * dedicated to, happen on its associated GVT product.
     */
    protected void initializeDynamicSupport(BridgeContext ctx,
                                            Element e,
                                            GraphicsNode node) {
        if (!ctx.isInteractive()) {
            return;
        }

        if (ctx.isDynamic()) {
            // Only do this for dynamic not interactive.
            this.e = e;
            this.node = node;
            this.ctx = ctx;
            ((SVGOMElement) e).setSVGContext(this);
        }
    }

    // BridgeUpdateHandler implementation ////////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        Node evtNode = evt.getRelatedNode();

        if (attrName.equals(SVG_X_ATTRIBUTE)
                || attrName.equals(SVG_Y_ATTRIBUTE)) {
            updatePosition(ctx, e, (CompositeGraphicsNode) node);
        } else if (attrName.equals(SVG_WIDTH_ATTRIBUTE)
                || attrName.equals(SVG_HEIGHT_ATTRIBUTE)) {
            float oldV = 0, newV = 0;
            String s = evt.getPrevValue();
            UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, e);

            if (s.length() != 0) {
                oldV = UnitProcessor.svgHorizontalCoordinateToUserSpace
                    (s, attrName, uctx);
            }
            s = evt.getNewValue();
            if (s.length() != 0) {
                newV = UnitProcessor.svgHorizontalCoordinateToUserSpace
                    (s, attrName, uctx);
            }
            if (oldV == newV) {
                return;
            }

            buildForeignObjectNode
                (ctx, e, foreignContent, (CompositeGraphicsNode) node, handler);
        } else {
            super.handleDOMAttrModifiedEvent(evt);
        }
    }
}
