/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import java.awt.event.KeyEvent;

import org.apache.batik.gvt.GraphicsNode;

/**
 * An event which indicates that a keystroke occurred in a graphics node.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class GraphicsNodeKeyEvent extends GraphicsNodeInputEvent {

    int keyCode;
    char keyChar;
    
    /**
     * The id for the "keyPressed" event.
     */
    public static final int KEY_PRESSED = KeyEvent.KEY_PRESSED;
    /**
     * The id for the "keyReleased" event.
     */
    public static final int KEY_RELEASED = KeyEvent.KEY_RELEASED;
    /**
     * The id for the "keyTyped" event.
     */
    public static final int KEY_TYPED = KeyEvent.KEY_TYPED;

    /**
     * Constructs a new graphics node key event.
     * @param source the graphics node where the event originated
     * @param id the id of this event
     * @param when the time the event occurred
     * @param modifiers the modifier keys down while event occurred
     */
    public GraphicsNodeKeyEvent(GraphicsNode source, int id,
                                long when, int modifiers, int keyCode,
                                char keyChar) {
        super(source, id, when, modifiers);
        this.keyCode = keyCode;
        this.keyChar = keyChar;
    }

    /**
     * Return the integer code for the physical key pressed. Not localized.
     * @see java.awt.event.KeyEvent#getKeyCode
     */
    public int getKeyCode() {
        return keyCode;
    }

    /**
     * Return a character corresponding to  physical key pressed. May be localized.
     * @see java.awt.event.KeyEvent#getKeyChar
     */
    public char getKeyChar() {
        return keyChar;
    }

}
