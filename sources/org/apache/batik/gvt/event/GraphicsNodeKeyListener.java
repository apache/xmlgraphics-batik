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
 * The listener interface for receiving graphics node key events.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface GraphicsNodeKeyListener extends EventListener {

    /**
     * Invoked when a key has been pressed.
     * @param evt the graphics node key event
     */
    void keyPressed(GraphicsNodeKeyEvent evt);

    /**
     * Invoked when a key has been released.
     * @param evt the graphics node key event
     */
    void keyReleased(GraphicsNodeKeyEvent evt);

    /**
     * Invoked when a key has been typed.
     * @param evt the graphics node key event
     */
    void keyTyped(GraphicsNodeKeyEvent evt);

}
