/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

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

    public Bridge getInstance(){
        return this;
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
                    refNode = builder.build(ctx, ref);
                    break;
                }
            }
        }
        if (refNode == null) {
            return null;
        }
        CompositeGraphicsNode group = new CompositeGraphicsNode();
        group.add(refNode);
        // 'transform'
        String s = e.getAttributeNS(null, SVG_TRANSFORM_ATTRIBUTE);
        if (s.length() != 0) {
            group.setTransform
                (SVGUtilities.convertTransform(e, SVG_TRANSFORM_ATTRIBUTE, s));
        }
        return group;
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
        if (ctx.isInteractive()) {
            ctx.bind(e, node);
        }
    }

    /**
     * Returns true if the graphics node has to be displayed, false
     * otherwise.
     */
    public boolean getDisplay(Element e) {
        return CSSUtilities.convertDisplay(e);
    }

    /**
     * Returns false as the &lt;switch> element is not a container.
     */
    public boolean isComposite() {
        return false;
    }
}
