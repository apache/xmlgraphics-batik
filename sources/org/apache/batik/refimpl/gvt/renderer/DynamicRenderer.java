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

    public void repaint(Shape area) {
        System.out.println("repaint "+area.getBounds());
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
            Gx.preConcatenate(at);
            node = node.getParent();
        }
        return Gx;
    }

    class UpdateListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            System.out.println("propertyChange " + evt.getPropertyName());
            repaint(getBoundsInRendererSpace((GraphicsNode) evt.getSource()));
        }
    }

}
