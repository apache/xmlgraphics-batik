/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * A graphics node which provides a placeholder for another graphics node. This
 * node is self defined except that it delegates to the enclosed (proxied)
 * graphics node, its paint routine and bounds computation.
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
     *
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
     *
     * @param g2d the Graphics2D to use
     */
    public void primitivePaint(Graphics2D g2d) {
        if (source != null) {
            source.paint(g2d);
        }
    }

    /**
     * Returns the bounds of the area covered by this node's primitive paint.
     */
    public Rectangle2D getPrimitiveBounds() {
        if (source != null) {
            return source.getBounds();
        } else {
            return null;
        }
    }

    /**
     * Returns the bounds of the area covered by this node, without taking any
     * of its rendering attribute into account. i.e., exclusive of any clipping,
     * masking, filtering or stroking, for example.
     */
    public Rectangle2D getGeometryBounds() {
        if (source != null) {
            return source.getGeometryBounds();
        } else {
            return null;
        }
    }

    /**
     * Returns the outline of this node.
     */
    public Shape getOutline() {
        if (source != null) {
            return source.getOutline();
        } else {
            return null;
        }
    }
}
