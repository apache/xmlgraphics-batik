/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.gvt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.NoninvertibleTransformException;

/**
 * This class represents a rotate interactor.
 * To use it, just redefine the {@link
 * InteractorAdapter#startInteraction(InputEvent)} method.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AbstractRotateInteractor extends InteractorAdapter {

    /**
     * Whether the interactor has finished.
     */
    protected boolean finished;

    /**
     * The initial rotation angle.
     */
    protected double initialRotation;

    /**
     * Tells whether the interactor has finished.
     */
    public boolean endInteraction() {
        return finished;
    }

    // MouseListener ///////////////////////////////////////////////////////
        
    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        finished = false;
        JGVTComponent c = (JGVTComponent)e.getSource();

        Dimension d = c.getSize();
        double dx = e.getX() - d.width / 2;
        double dy = e.getY() - d.height / 2;
        double cos = -dy / Math.sqrt(dx * dx + dy * dy);
        initialRotation = (dx > 0) ? Math.acos(cos) : -Math.acos(cos);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        finished = true;
        JGVTComponent c = (JGVTComponent)e.getSource();

        AffineTransform at = rotateTransform(c.getSize(), e.getX(), e.getY());
        at.concatenate(c.getRenderingTransform());
        c.setRenderingTransform(at);
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
        finished = true;
        JGVTComponent c = (JGVTComponent)e.getSource();
        c.setPaintingTransform(null);
    }

    // MouseMotionListener /////////////////////////////////////////////////

    /**
     * Invoked when a mouse button is pressed on a component and then 
     * dragged.  Mouse drag events will continue to be delivered to
     * the component where the first originated until the mouse button is
     * released (regardless of whether the mouse position is within the
     * bounds of the component).
     */
    public void mouseDragged(MouseEvent e) {
        JGVTComponent c = (JGVTComponent)e.getSource();

        c.setPaintingTransform(rotateTransform(c.getSize(), e.getX(), e.getY()));
    }

    /**
     * Returns the rotate transform.
     */
    protected AffineTransform rotateTransform(Dimension d, int x, int y) {
        double dx = x - d.width / 2;
        double dy = y - d.height / 2;
        double cos = -dy / Math.sqrt(dx * dx + dy * dy);
        double angle = (dx > 0) ? Math.acos(cos) : -Math.acos(cos);

        angle -= initialRotation;

        return AffineTransform.getRotateInstance(angle, d.width / 2, d.height / 2);
    }
}
