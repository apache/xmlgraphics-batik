/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving graphics node mouse events.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface GraphicsNodeMouseListener extends EventListener {

    /**
     * Invoked when the mouse has been clicked on a graphics node.
     * @param evt the graphics node mouse event
     */
    void mouseClicked(GraphicsNodeMouseEvent evt);

    /**
     * Invoked when a mouse button has been pressed on a graphics node.
     * @param evt the graphics node mouse event
     */
    void mousePressed(GraphicsNodeMouseEvent evt);

    /**
     * Invoked when a mouse button has been released on a graphics node.
     * @param evt the graphics node mouse event
     */
    void mouseReleased(GraphicsNodeMouseEvent evt);

    /**
     * Invoked when the mouse enters a graphics node.
     * @param evt the graphics node mouse event
     */
    void mouseEntered(GraphicsNodeMouseEvent evt);

    /**
     * Invoked when the mouse exits a graphics node.
     * @param evt the graphics node mouse event
     */
    void mouseExited(GraphicsNodeMouseEvent evt);

    /**
     * Invoked when a mouse button is pressed on a graphics node and then
     * dragged.
     * @param evt the graphics node mouse event
     */
    void mouseDragged(GraphicsNodeMouseEvent evt);

    /**
     * Invoked when the mouse button has been moved on a node.
     * @param evt the graphics node mouse event
     */
     void mouseMoved(GraphicsNodeMouseEvent evt);

}
