/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import org.apache.batik.gvt.GraphicsNode;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;


/**
 * Abstract class for receiving InputEvents and dispatching them as GraphicsNodeEvents.
 * Mouse events are dispatched to their "containing" node (the GraphicsNode
 * corresponding to the mouse event coordinate). Searches for containment
 * are performed from the EventDispatcher's "root" node.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public abstract class AbstractEventDispatcher 
    implements EventDispatcher, MouseListener, MouseMotionListener, KeyListener {

    /*
     * The root GraphicsNode as determined by setRootNode().
     */
    protected GraphicsNode root = null;

    /*
     * The base AffineTransform for InputEvent-to-GraphicsNodeEvent coordinates
     *  as determined by setBaseTransform().
     */
    protected AffineTransform baseTransform = null;

    private int nodeIncrementEventID = KeyEvent.KEY_PRESSED;
    private int nodeIncrementEventCode = KeyEvent.VK_TAB;
    private int nodeIncrementEventModifiers = 0;
    private int nodeDecrementEventID = KeyEvent.KEY_PRESSED;
    private int nodeDecrementEventCode = KeyEvent.VK_TAB;
    private int nodeDecrementEventModifiers = InputEvent.SHIFT_MASK;

    /*
     * Sets the root node for MouseEvent dispatch containment searches 
     * and field selections.
     */
    public void setRootNode(GraphicsNode root) {
	this.root = root;
    }

    /*
     * Sets the base transform applied to MouseEvent coordinates prior to
     * dispatch..
     */
    public void setBaseTransform(AffineTransform t) {
	baseTransform = t;
    }

    /*
     * Associates all InputEvents of type <tt>e.getID()</tt>
     * with "incrementing" of the currently selected GraphicsNode.
     */
    public void setNodeIncrementEvent(InputEvent e) {
	nodeIncrementEventID = e.getID();
	if (e instanceof KeyEvent) {
	    nodeIncrementEventCode = ((KeyEvent) e).getKeyCode();
	}
	nodeIncrementEventModifiers = e.getModifiers();
    }

    /*
     * Associates all InputEvents of type <tt>e.getID()</tt>
     * with "decrementing" of the currently selected GraphicsNode.
     * The notion of "currently selected" GraphicsNode is used
     * for dispatching KeyEvents.
     */
    public void setNodeDecrementEvent(InputEvent e) {
	nodeDecrementEventID = e.getID();
	if (e instanceof KeyEvent) {
	    nodeDecrementEventCode = ((KeyEvent) e).getKeyCode();
	}
	nodeDecrementEventModifiers = e.getModifiers();
    }

    /*
     * Returns true if the input event e is a node increment event.
     * @see org.apache.batik.gvt.event.EventDispatcher#setNodeDecrementEvent
     */
    protected boolean isNodeIncrementEvent(InputEvent e) {
	// TODO: Improve code readability!
	return ((e.getID() == nodeIncrementEventID) &&
		((e instanceof KeyEvent) ? 
		     ( ((KeyEvent) e).getKeyCode() == nodeIncrementEventCode) : true) &&
		((e.getModifiers() & nodeIncrementEventModifiers) != 0));
    }

    /*
     * Returns true if the input event e is a node decrement event.
     * @see org.apache.batik.gvt.event.EventDispatcher#setNodeDecrementEvent
     */
    protected boolean isNodeDecrementEvent(InputEvent e) {
	// TODO: Improve code readability!
	return ((e.getID() == nodeDecrementEventID) &&
		((e instanceof KeyEvent) ? 
		     ( ((KeyEvent) e).getKeyCode() == nodeDecrementEventCode) : true) &&
		((e.getModifiers() & nodeDecrementEventModifiers) != 0  ));

    }

}
