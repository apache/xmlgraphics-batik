/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * A graphics node which provides a placeholder for another graphics
 * node. This node is self defined except that it delegates to the
 * enclosed (proxied) graphics node, its paint routine and bounds
 * computation.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ProxyGraphicsNode extends AbstractGraphicsNode {

    /**
     * The graphics node to proxy.
     */
    protected GraphicsNode source;

    /**
     * Constructs a new empty proxy graphics node.
     */
    public ProxyGraphicsNode() {}

    /**
     * Sets the graphics node to proxy to the specified graphics node.
     * @param source the graphics node to proxy
     */
    public void setSource(GraphicsNode source) {
        this.source = source;
    }

    /**
     * Returns the proxied graphics node.
     */
    public GraphicsNode getSource() {
        return source;
    }

    /**
     * Paints this node without applying Filter, Mask, Composite and clip.
     * @param g2d the Graphics2D to use
     * @param rc the GraphicsNodeRenderContext to use
     */
    public void primitivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        if (source != null) {
            source.paint(g2d, rc);
        }
    }

    /**
     * Returns the bounds of the area covered by this node's
     * primitive paint. ie. the proxied graphics node's bounds.
     * <b>Note</b>: The boundaries of some nodes (notably, text element nodes)
     * cannot be precisely determined independent of their
     * GraphicsNodeRenderContext.
     *
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
     */
    public Rectangle2D getPrimitiveBounds(GraphicsNodeRenderContext rc) {
        if (source != null) {
            return source.getBounds(rc);
        } else {
            return new Rectangle(0, 0, 0, 0);
        }
    }

    /**
     * Returns the bounds of the area covered by this node, without
     * taking any of its rendering attribute into account, i.e., exclusive
     * of any clipping, masking, filtering or stroking, for example.
     * <b>Note</b>: The boundaries of some nodes (notably, text element nodes)
     * cannot be precisely determined independent of their
     * GraphicsNodeRenderContext.
     *
     * @param rc the GraphicsNodeRenderContext for which this dimension applies
      */
    public Rectangle2D getGeometryBounds(GraphicsNodeRenderContext rc){
        if (source != null) {
            return source.getGeometryBounds(rc);
        } else {
            return new Rectangle(0, 0, 0, 0);
        }
    }

    /**
     * Returns the outline of this node.
     */
    public Shape getOutline(GraphicsNodeRenderContext rc) {
        if (source != null) {
            return source.getOutline(rc);
        } else {
            return null;
        }
    }
}
