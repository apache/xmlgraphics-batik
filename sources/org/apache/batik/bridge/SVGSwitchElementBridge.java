/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;

import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGTests;

/**
 * Bridge class for the &lt;switch> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGSwitchElementBridge extends AbstractSVGBridge
    implements GraphicsNodeBridge {

    /**
     * Constructs a new bridge for the &lt;switch> element.
     */
    public SVGSwitchElementBridge() {}

    /**
     * Returns 'switch'.
     */
    public String getLocalName() {
        return SVG_SWITCH_TAG;
    }

    /**
     * Creates a <tt>GraphicsNode</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        GraphicsNode refNode = null;
        GVTBuilder builder = ctx.getGVTBuilder();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element ref = (Element)n;
                if (n instanceof SVGTests
                    && SVGUtilities.matchUserAgent(ref, ctx.getUserAgent())) {
                    return builder.build(ctx, ref);
                }
            }
        }
        return null;
    }

    /**
     * Builds using the specified BridgeContext and element, the
     * specified graphics node.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @param node the graphics node to build
     */
    public void buildGraphicsNode(BridgeContext ctx,
                                  Element e,
                                  GraphicsNode node) {
        // bind the specified element and its associated graphics node if needed
        if (ctx.isDynamic()) {
            ctx.bind(e, node);
        }
    }

    /**
     * Performs an update according to the specified event.
     *
     * @param evt the event describing the update to perform
     */
    public void update(BridgeMutationEvent evt) {
        throw new Error("Not implemented");
    }

    /**
     * Returns false as the &lt;switch> element is not a container.
     */
    public boolean isComposite() {
        return false;
    }
}
