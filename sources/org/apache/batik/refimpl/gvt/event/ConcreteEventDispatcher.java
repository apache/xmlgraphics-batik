/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.event;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Class for receiving InputEvents and dispatching them as GraphicsNodeEvents.
 * Mouse events are dispatched to their "containing" node (the GraphicsNode
 * corresponding to the mouse event coordinate). Searches for containment
 * are performed from the EventDispatcher's "root" node.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class ConcreteEventDispatcher extends AbstractEventDispatcher {

    public ConcreteEventDispatcher() {
	super();
    }

    public void mousePressed(MouseEvent evt) { 
	dispatch(evt);
    }

    public void mouseReleased(MouseEvent evt) {
	dispatch(evt);
    }

    public void mouseEntered(MouseEvent evt) {
	dispatch(evt);
    }

    public void mouseExited(MouseEvent evt) {
	dispatch(evt);
    }

    public void mouseClicked(MouseEvent evt) {
	dispatch(evt);
    }

    public void mouseMoved(MouseEvent evt) { 
        dispatch(evt);
    }

    public void mouseDragged(MouseEvent evt) { 
	dispatch(evt);
    }

    public void keyPressed(KeyEvent evt) { 
        dispatch(evt);
    }

    public void keyReleased(KeyEvent evt) { 
	dispatch(evt);
    }

    public void keyTyped(KeyEvent evt) { 
	dispatch(evt);
    }

    /**
     * Add a GraphicsNodeMouseListener which is notified of all MouseEvents dispatched.
     */
    public void addGlobalGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
	;
    }


    /**
     * Remove a "global" GraphicsNodeMouseListener.
     * @see org.apache.batik.gvt.event.EventDispatcher#addGlobalGraphicsNodeMouseListener
     */
    public void removeGlobalGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
	;
    }


    /**
     * Add a GraphicsNodeKeyListener which is notified of all KeyEvents dispatched.
     */
    public void addGlobalGraphicsNodeKeyListener(GraphicsNodeKeyListener l) {
	;
    }


    /**
     * Remove a "global" GraphicsNodeKeyListener.
     * @see org.apache.batik.gvt.event.EventDispatcher#addGlobalGraphicsNodeMouseListener
     */
    public void removeGlobalGraphicsNodeKeyListener(GraphicsNodeKeyListener l) {
	;
    }

    /**
     * Add a GraphicsNodeFocusChangeListener which is notified when the node focus changes..
     */
    public void addFocusChangeListener(GraphicsNodeFocusChangeListener l) {
	;
    }


    /**
     * Remove a "global" GraphicsNodeFocusChangeListener.
     * @see org.apache.batik.gvt.event.EventDispatcher#addFocusChangeListener
     */
    public void removeFocusChangeListener(GraphicsNodeFocusChangeListener l) {
	;
    }

    private GraphicsNode lastHit = null;

    public void dispatch(InputEvent evt) {
	if (evt instanceof MouseEvent) {
	    dispatchMouseEvent((MouseEvent) evt);
	} else if (evt instanceof KeyEvent) {
	    if (isNodeIncrementEvent(evt)) {
		incrementKeyTarget();
	    } else if (isNodeDecrementEvent(evt)) {
		decrementKeyTarget();
	    } else {
		dispatchKeyEvent((KeyEvent) evt);
	    }
	}
    }

    private void incrementKeyTarget() {
	if (currentKeyEventTarget == null) {
	    ;
	} else {
	    currentKeyEventTarget = null; // TODO: Needs implementation.
	}
    }
	
    private void decrementKeyTarget() {
	throw new Error("Decrement not implemented."); // TODO: Not implemented.
    }
	

    private GraphicsNode currentKeyEventTarget = null;

    private void dispatchKeyEvent(KeyEvent evt) {
	if (currentKeyEventTarget != null) {
	    currentKeyEventTarget.processKeyEvent
                (new GraphicsNodeKeyEvent(currentKeyEventTarget,
                                          evt.getID(),
                                          evt.getWhen(),
                                          evt.getModifiers(),
                                          evt.getKeyCode(),
                                          evt.getKeyChar()));
	}
    }

    private void dispatchMouseEvent(MouseEvent evt) {
	GraphicsNodeMouseEvent gvtevt;
        Point2D p = new Point2D.Float(evt.getX(), evt.getY());

	if (baseTransform != null) {
	    p = baseTransform.transform(p, null);
	}

   	GraphicsNode node = root.nodeHitAt(p);

	// If the receiving node has changed, send a notification

	if (node != null) {
		if (lastHit == null) {
		    node.processMouseEvent
                        (new GraphicsNodeMouseEvent(node, 
                                                    MouseEvent.MOUSE_ENTERED,
                                                    evt.getWhen(), 
                                                    evt.getModifiers(),
                                                    evt.getX(), 
                                                    evt.getY(), 
                                                    evt.getClickCount()));
		}

	} else if (lastHit != null) {
		lastHit.processMouseEvent
                    (new GraphicsNodeMouseEvent(lastHit, 
                                                MouseEvent.MOUSE_EXITED,
                                                evt.getWhen(), 
                                                evt.getModifiers(),
                                                evt.getX(), 
                                                evt.getY(), 
                                                evt.getClickCount()));
	}

      	// in all cases, dispatch the original event
	if (node != null) {
	    node.processMouseEvent(new GraphicsNodeMouseEvent(node, evt));
	}
	lastHit = node;
    }
}
