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
import org.apache.batik.gvt.RootGraphicsNode;

/**
 * Simple implementation of the Renderer that supports dynamic updates.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DynamicRenderer extends StaticRenderer {

    protected UpdateListener updateListener = new UpdateListener();
    protected RepaintHandler repaintHandler;

    public DynamicRenderer(BufferedImage offScreen) {
        super(offScreen);
    }

    public void setTree(GraphicsNode newTreeRoot){
        if (!(newTreeRoot instanceof RootGraphicsNode)) {
            throw new IllegalArgumentException("not a RootGraphicsNode");
        }
        RootGraphicsNode root = (RootGraphicsNode) this.treeRoot;
        if (updateListener != null && root != null) {
            root.removeGlobalPropertyChangeListener(updateListener);
        }
        root = (RootGraphicsNode) newTreeRoot;
        super.setTree(root);
        root.addGlobalPropertyChangeListener(updateListener);
    }

    public void setRepaintHandler(RepaintHandler repaintHandler) {
        this.repaintHandler = repaintHandler;
    }

    public RepaintHandler getRepaintHandler() {
        return repaintHandler;
    }

    public void repaint(Shape area) {
        System.out.println("********* repaint "+area.getBounds());
        super.repaint(area);
    }

    /**
     * Returns the bounds of the specified graphics node in the
     * current user space coordinate system.
     * @param node the graphics node
     */
    protected Shape getBoundsInRendererSpace(GraphicsNode node) {
        Rectangle2D bounds = node.getBounds();
        AffineTransform Gx = getGlobalTransform(node);
        try {
            AffineTransform GxInv = Gx.createInverse();
            return GxInv.createTransformedShape(bounds);
        } catch(NoninvertibleTransformException ex) {
            return null;
        }
    }

    protected AffineTransform getGlobalTransform(GraphicsNode node) {
        AffineTransform Gx = new AffineTransform();
        RootGraphicsNode root = node.getRoot();
        while (node != root) {
            AffineTransform at = node.getTransform();
            if (at != null) {
                Gx.preConcatenate(at);
            }
            node = node.getParent();
        }
        return Gx;
    }

    /**
     * Simple listener that fire the repaint handler when the GVT tree
     * has been modified.
     */
    class UpdateListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            GraphicsNode node = (GraphicsNode) evt.getSource();
            Shape aoi = getBoundsInRendererSpace(node);
            System.out.println(node+" propertyChange " +
                               evt.getPropertyName()+
                               aoi);
            repaintHandler.notifyRepaintedRegion(aoi);
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
        void notifyRepaintedRegion(Shape aoi);

    }
}
