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

import java.util.EventListener;

import java.lang.reflect.Array;

import javax.swing.event.EventListenerList;

/**
 * Class for receiving InputEvents and dispatching them as GraphicsNodeEvents.
 * Mouse events are dispatched to their "containing" node (the GraphicsNode
 * corresponding to the mouse event coordinate). Searches for containment
 * are performed from the EventDispatcher's "root" node.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @author <a href="cjolif@ilog.fr>Christophe Jolif</a>
 * @version $Id$
 */
public class ConcreteEventDispatcher extends AbstractEventDispatcher {

    private EventListenerList glisteners;

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
     * Add a GraphicsNodeMouseListener which is notified of all MouseEvents
     * dispatched.
     */
    public void
        addGlobalGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
        if (glisteners == null) {
            glisteners = new EventListenerList();
        }
        glisteners.add(GraphicsNodeMouseListener.class, l);
    }


    /**
     * Remove a "global" GraphicsNodeMouseListener.
     * @see org.apache.batik.gvt.event.EventDispatcher#addGlobalGraphicsNodeMouseListener
     */
    public void
        removeGlobalGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
        if (glisteners != null) {
            glisteners.remove(GraphicsNodeMouseListener.class, l);
        }
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
        if (root == null)
            return;
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

    public EventListener [] getListeners(Class listenerType) {
        Object array =
            Array.newInstance(listenerType,
                              glisteners.getListenerCount(listenerType));
        Object[] pairElements = glisteners.getListenerList();
        for (int i = 0, j = 0;i < pairElements.length-1; i+=2) {
            if (pairElements[i].equals(listenerType)) {
                Array.set(array, j, pairElements[i+1]);
                ++j;
            }
        }
        return (EventListener[]) array;
    }

    /**
     * Calls global listeners.
     */
    private void globalMouseEvent(GraphicsNodeMouseEvent gvtevt)
    {
        if (glisteners != null) {
            GraphicsNodeMouseListener[] listeners =
                (GraphicsNodeMouseListener[])
                getListeners(GraphicsNodeMouseListener.class);
            switch (gvtevt.getID()) {
            case GraphicsNodeMouseEvent.MOUSE_MOVED:
                for (int i = 0; i < listeners.length; i++) {
                    listeners[i].
                        mouseMoved(gvtevt);
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_DRAGGED:
                for (int i = 0; i < listeners.length; i++) {
                    listeners[i].
                        mouseDragged(gvtevt);
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_ENTERED:
                for (int i = 0; i < listeners.length; i++) {
                    listeners[i].
                        mouseEntered(gvtevt);
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_EXITED:
                for (int i = 0; i < listeners.length; i++) {
                    listeners[i].
                        mouseExited(gvtevt);
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_CLICKED:
                for (int i = 0; i < listeners.length; i++) {
                    listeners[i].
                        mouseClicked(gvtevt);
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_PRESSED:
                for (int i = 0; i < listeners.length; i++) {
                    listeners[i].
                        mousePressed(gvtevt);
                }
                break;
            case GraphicsNodeMouseEvent.MOUSE_RELEASED:
                for (int i = 0; i < listeners.length; i++) {
                    listeners[i].
                        mouseReleased(gvtevt);
                }
                break;
            default:
                throw new Error("Unknown Mouse Event type: "+gvtevt.getID());
            }
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
                        (gvtevt = new GraphicsNodeMouseEvent(node,
                                                             MouseEvent.
                                                             MOUSE_ENTERED,
                                                             evt.getWhen(),
                                                             evt.
                                                             getModifiers(),
                                                             evt.getX(),
                                                             evt.getY(),
                                                             evt.
                                                             getClickCount()));
                    globalMouseEvent(gvtevt);
                }
        } else if (lastHit != null) {
                lastHit.processMouseEvent
                    (gvtevt = new GraphicsNodeMouseEvent(lastHit,
                                                         MouseEvent.
                                                         MOUSE_EXITED,
                                                         evt.getWhen(),
                                                         evt.getModifiers(),
                                                         evt.getX(),
                                                         evt.getY(),
                                                         evt.getClickCount()));
                globalMouseEvent(gvtevt);
        }

        // in all cases, dispatch the original event
        if (node != null) {
            node.processMouseEvent(gvtevt =
                                   new GraphicsNodeMouseEvent(node, evt));
            globalMouseEvent(gvtevt);
        }
        lastHit = node;
    }
}
