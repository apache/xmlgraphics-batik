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
import java.awt.geom.AffineTransform;

/**
 * Interface for receiving InputEvents and dispatching them as GraphicsNodeEvents.
 * Mouse events are dispatched to their "containing" node (the GraphicsNode
 * corresponding to the mouse event coordinate). Searches for containment
 * are performed from the EventDispatcher's "root" node.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public interface EventDispatcher {

    /*
     * Sets the root node for MouseEvent dispatch containment searches 
     * and field selections.
     */
    public void setRootNode(GraphicsNode root);


    /*
     * Sets the base transform applied to MouseEvent coordinates prior to
     * dispatch..
     */
    public void setBaseTransform(AffineTransform t);

    /*
     * Convert the InputEvent to a corresponding GraphicsNodeEvent and
     * dispatch to the appropriate GraphicsNodes.
     * If the InputEvent is a MouseEvent the dispatch is performed
     * to each GraphicsNode which contains the MouseEvent coordinate,
     * until the event is consumed.
     * If the InputEvent is a KeyEvent, it is dispatched to the currently
     * selected GraphicsNode.
     * @see org.apache.batik.gvt.EventDispatcher#setNodeIncrementEvent
     * @see org.apache.batik.gvt.EventDispatcher#setNodeDecrementEvent
     */
    public void dispatch(InputEvent e);

    /**
     * Add a GraphicsNodeMouseListener which is notified of all MouseEvents dispatched.
     */
    public void addGlobalGraphicsNodeMouseListener(GraphicsNodeMouseListener l);


    /**
     * Remove a "global" GraphicsNodeMouseListener.
     * @see org.apache.batik.gvt.event.EventDispatcher#addGlobalGraphicsNodeMouseListener
     */
    public void removeGlobalGraphicsNodeMouseListener(GraphicsNodeMouseListener l);


    /**
     * Add a GraphicsNodeKeyListener which is notified of all KeyEvents dispatched.
     */
    public void addGlobalGraphicsNodeKeyListener(GraphicsNodeKeyListener l);


    /**
     * Remove a "global" GraphicsNodeKeyListener.
     * @see org.apache.batik.gvt.event.EventDispatcher#addGlobalGraphicsNodeMouseListener
     */
    public void removeGlobalGraphicsNodeKeyListener(GraphicsNodeKeyListener l);

    /**
     * Add a GraphicsNodeFocusChangeListener which is notified when the node focus changes..
     */
    public void addFocusChangeListener(GraphicsNodeFocusChangeListener l);


    /**
     * Remove a "global" GraphicsNodeFocusChangeListener.
     * @see org.apache.batik.gvt.event.EventDispatcher#addFocusChangeListener
     */
    public void removeFocusChangeListener(GraphicsNodeFocusChangeListener l);

    /*
     * Associates all InputEvents of type <tt>e.getID()</tt>
     * with "incrementing" of the currently selected GraphicsNode.
     */
    public void setNodeIncrementEvent(InputEvent e);

    /*
     * Associates all InputEvents of type <tt>e.getID()</tt>
     * with "decrementing" of the currently selected GraphicsNode.
     * The notion of "currently selected" GraphicsNode is used
     * for dispatching KeyEvents.
     */
    public void setNodeDecrementEvent(InputEvent e);


}

