/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;

import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

/**
 * An implementation of the <tt>CanvasGraphicsNode</tt> interface.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @version $Id$
 */
public class ConcreteCanvasGraphicsNode extends ConcreteCompositeGraphicsNode
        implements CanvasGraphicsNode {

    /** The background of this canvas graphics node. */
    protected Paint backgroundPaint;
    /** The size of this canvas graphics node. */
    protected Dimension2D size;

    /**
     * Constructs a new empty canvas graphics node.
     */
    public ConcreteCanvasGraphicsNode() {}

    //
    // Properties methods
    //

    public void setBackgroundPaint(Paint newBackgroundPaint) {
        Paint oldBackgroundPaint = backgroundPaint;
        this.backgroundPaint = newBackgroundPaint;
        firePropertyChange("backgroundPaint",
                           oldBackgroundPaint, newBackgroundPaint);
    }

    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    //
    // Geometric methods
    //

    public void setSize(Dimension2D newSize) {
        Dimension2D oldSize = size;
        this.size = newSize;
        firePropertyChange("size", oldSize, newSize);
    }

    public Dimension2D getSize() {
        return (Dimension2D) size.clone();
    }

    //
    // Drawing methods
    //

    public void primitivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        if (backgroundPaint != null) {
            g2d.setPaint(backgroundPaint);
            g2d.fill(g2d.getClip()); // Fast paint for huge background area
        }
        super.primitivePaint(g2d, rc);
    }
}
