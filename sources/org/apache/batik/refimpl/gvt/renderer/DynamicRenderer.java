/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.renderer;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.gvt.event.GraphicsNodePaintEvent;
import org.apache.batik.gvt.event.GraphicsNodePaintListener;

/**
 * Simple implementation of the Renderer that supports dynamic updates.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DynamicRenderer extends StaticRenderer {

    /**
     * The listener that receives <tt>PropertyChangeEvent</tt> and
     * notify the <tt>RepaintHandler</tt> that a region has to be repainted.
     */
    protected UpdateListener updateListener = new UpdateListener();

    /**
     * The handler to notify that a region has to be repainted.
     */
    protected RepaintHandler repaintHandler;

    /**
     * Constructs a new dynamic renderer with the specified buffer image.
     * @param offScreen the offscreen buffer to use
     */
    public DynamicRenderer(BufferedImage offScreen) {
        super(offScreen);
    }

    /**
     * Constructs a new dynamic renderer with the specified buffer image.
     * @param offScreen the offscreen buffer to use
     * @param rc the GraphicsNodeRenderContext to use
     */
    public DynamicRenderer(BufferedImage offScreen, 
                                  GraphicsNodeRenderContext rc) {
        super(offScreen, rc);
    }

    public void setTree(GraphicsNode newTreeRoot){
        if (!(newTreeRoot instanceof RootGraphicsNode)) {
            throw new IllegalArgumentException("not a RootGraphicsNode");
        }
        RootGraphicsNode root = (RootGraphicsNode) this.treeRoot;
        if (repaintHandler != null && root != null) {
            //root.removeGlobalPropertyChangeListener(updateListener);
            root.removeGraphicsNodePaintListener(updateListener);
        }
        root = (RootGraphicsNode) newTreeRoot;
        super.setTree(root);
        //root.addGlobalPropertyChangeListener(updateListener);
        if (repaintHandler != null) {
            root.addGraphicsNodePaintListener(updateListener);
        }
    }

    /**
     * Sets the repaint handler to the specified repaint handler.
     * @param repaintHandler the new repaint handler of this renderer
     */
    public void setRepaintHandler(RepaintHandler repaintHandler) {
        this.repaintHandler = repaintHandler;
    }

    /**
     * Returns the repaint handler of this dynamic renderer.
     */
    public RepaintHandler getRepaintHandler() {
        return repaintHandler;
    }

    /**
     * Simple listener that fire the repaint handler when the GVT tree
     * has been modified.
     */
    protected class UpdateListener implements PropertyChangeListener,
                                              GraphicsNodePaintListener {

        public void propertyChange(PropertyChangeEvent evt) {
        }

        public void graphicsNodeModified(GraphicsNodePaintEvent evt) {
            GraphicsNode node = (GraphicsNode) evt.getSource();
            AffineTransform Gx = node.getGlobalTransform();
            Shape oldAoi = evt.getOldBounds();
            Shape newAoi =
                Gx.createTransformedShape(node.getBounds()).getBounds();
            repaintHandler.notifyRepaintedRegion(oldAoi, newAoi,
                                                 DynamicRenderer.this);
        }
    }

    /**
     * Handler interface for receiving the area of interest when an
     * area has been modified.
     */
    public static interface RepaintHandler {

        /**
         * Notifies that the specified area of interest need to be repainted.
         * @param aoi the area of interest to repaint
         */
        void notifyRepaintedRegion(Shape oldAoi, Shape newAoi,
                                                 Renderer renderer);

    }
}
