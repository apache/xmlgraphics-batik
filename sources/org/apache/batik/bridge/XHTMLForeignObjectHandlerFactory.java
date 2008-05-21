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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.util.SVGConstants;

import org.xhtmlrenderer.extend.FontContext;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xhtmlrenderer.swing.Java2DOutputDevice;
import org.xhtmlrenderer.swing.Java2DTextRenderer;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A factory class for the XHTML foreign object handler.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class XHTMLForeignObjectHandlerFactory
    implements ForeignObjectHandlerFactory {

    /**
     * Returns the XHTML namespace URI.
     */
    public String getNamespaceURI() {
        return "http://www.w3.org/1999/xhtml";
    }

    /**
     * Creates a new ForeignObjectHandler.
     */
    public ForeignObjectHandler createHandler() {
        return new Handler();
    }

    /**
     * The handler class for XHTML foreign object content.
     */
    protected static class Handler implements ForeignObjectHandler {

        /**
         * Creates a new Handler.
         */
        public Handler() {
            DOMImplementation impl =
                SVGDOMImplementation.getDOMImplementation();
        }

        /**
         * Returns a new GraphicsNode that represents the foreign object content
         * in the given element.
         */
        public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e,
                                               float w, float h) {
            if (!e.getLocalName().equals("html")) {
                return null;
            }

            DOMImplementation impl =
                GenericDOMImplementation.getDOMImplementation();
            Document htmlDoc = impl.createDocument
                ("http://www.w3.org/1999/xhtml", "html", null);
            htmlDoc.replaceChild
                (htmlDoc.importNode(e, true), htmlDoc.getDocumentElement());

            impl = SVGDOMImplementation.getDOMImplementation();
            Document svgDoc = impl.createDocument
                (SVGConstants.SVG_NAMESPACE_URI, SVGConstants.SVG_SVG_TAG, null);

            SVGGraphics2D g2d = new SVGGraphics2D(svgDoc);

            Graphics2DRenderer renderer = new Graphics2DRenderer();
            renderer.getPanel().setOpaque(false);
            SharedContext sc = renderer.getSharedContext();
            sc.setTextRenderer(new TextRenderer());
            renderer.setDocument(htmlDoc, AbstractDocument.getBaseURI(ctx.getDocument()));
            renderer.layout(g2d, new Dimension(Math.round(w), Math.round(h)));
            renderer.render(g2d);

//             try {
//                 g2d.stream(new java.io.OutputStreamWriter(System.out), false);
//             } catch (Exception ex) {
//             }

            g2d.getRoot(svgDoc.getDocumentElement());

            BridgeContext cx = new BridgeContext(ctx.getUserAgent());
            GVTBuilder builder = new GVTBuilder();
            RootGraphicsNode rgn = (RootGraphicsNode) builder.build(cx, svgDoc);
            Object[] nodes = rgn.toArray();
            CompositeGraphicsNode cgn = new CompositeGraphicsNode();
            for (int i = 0; i < nodes.length; i++) {
                cgn.add(nodes[i]);
                ((GraphicsNode) nodes[i]).setClip(null);
            }
            return cgn;
        }
    }

    protected static class TextRenderer extends Java2DTextRenderer {

        public void drawString(OutputDevice outputDevice, String string, float x, float y) {
            Graphics2D graphics = ((Java2DOutputDevice) outputDevice).getGraphics();
            graphics.drawString(string, x, y);
        }

        public void setup(FontContext fontContext) {
        }
    }
}
