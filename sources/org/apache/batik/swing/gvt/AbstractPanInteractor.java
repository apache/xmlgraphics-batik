/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.gvt;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * This class represents a pan interactor.
 * To use it, just redefine the {@link
 * InteractorAdapter#startInteraction(InputEvent)} method.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractPanInteractor extends InteractorAdapter {

    /**
     * The cursor for panning.
     */
    public final static Cursor PAN_CURSOR = new Cursor(Cursor.MOVE_CURSOR);

    /**
     * Whether the interactor has finished.
     */
    protected boolean finished = true;

    /**
     * The mouse x start position.
     */
    protected int xStart;

    /**
     * The mouse y start position.
     */
    protected int yStart;

    /**
     * The mouse x current position.
     */
    protected int xCurrent;

    /**
     * The mouse y current position.
     */
    protected int yCurrent;

    /**
     * To store the previous cursor.
     */
    protected Cursor previousCursor;

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
        if (!finished) {
            mouseExited(e);
            return;
        }
        
        finished = false;

        xStart = e.getX();
        yStart = e.getY();

        JGVTComponent c = (JGVTComponent)e.getSource();

        previousCursor = c.getCursor();
        c.setCursor(PAN_CURSOR);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        if (finished) {
            return;
        }
        finished = true;

        JGVTComponent c = (JGVTComponent)e.getSource();

        xCurrent = e.getX();
        yCurrent = e.getY();

        AffineTransform at =
            AffineTransform.getTranslateInstance(xCurrent - xStart,
                                                 yCurrent - yStart);
        AffineTransform rt =
            (AffineTransform)c.getRenderingTransform().clone();
        rt.preConcatenate(at);
        c.setRenderingTransform(rt);

        if (c.getCursor() == PAN_CURSOR) {
            c.setCursor(previousCursor);
        }
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
        finished = true;
        
        JGVTComponent c = (JGVTComponent)e.getSource();
        c.setPaintingTransform(null);
        if (c.getCursor() == PAN_CURSOR) {
            c.setCursor(previousCursor);
        }
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

        xCurrent = e.getX();
        yCurrent = e.getY();

        AffineTransform at =
            AffineTransform.getTranslateInstance(xCurrent - xStart,
                                                 yCurrent - yStart);
        c.setPaintingTransform(at);
    }
}
