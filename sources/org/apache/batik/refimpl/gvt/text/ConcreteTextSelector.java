/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.text;

import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.text.CharacterIterator;

import org.apache.batik.gvt.*;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;

/**
 * ConcreteTextSelector.java:
 * A simple implementation of GraphicsNodeMouseListener for text selection.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */

public class ConcreteTextSelector implements Selector {

    private GraphicsNode selectionNode = null;
    private GraphicsNode currentNode = null;
    private int firstHit;
    private int lastHit;
    private GraphicsNodeRenderContext renderContext;
    // XXX: below is used by our current "direct" approach to selection
    // highlighting.  It should probably be migrated to a
    // strategy that sends highlight requests directly to the Renderer.
    private Graphics2D g2d;

    public ConcreteTextSelector(GraphicsNodeRenderContext rc) {
        renderContext = rc;
    }

    public void setGraphics2D(Graphics2D g2d) {
        this.g2d = g2d;
    }

    public void mouseClicked(GraphicsNodeMouseEvent evt) {
        checkSelectGesture(evt);
    }

    public void mouseDragged(GraphicsNodeMouseEvent evt) {
        checkSelectGesture(evt);
    }

    public void mouseEntered(GraphicsNodeMouseEvent evt) {
        currentNode = evt.getGraphicsNode();
        //report(evt, "Entered");
    }

    public void mouseExited(GraphicsNodeMouseEvent evt) {
        currentNode = null;
        //report(evt, "Exited");
    }

    public void mouseMoved(GraphicsNodeMouseEvent evt) {
        ;
        //report(evt, "Moved");
    }

    public void mousePressed(GraphicsNodeMouseEvent evt) {
        checkSelectGesture(evt);
        //report(evt, "Pressed");
    }


    public void mouseReleased(GraphicsNodeMouseEvent evt) {
        checkSelectGesture(evt);
        //report(evt, "Released");
    }

    public void keyPressed(GraphicsNodeKeyEvent evt) {
        report(evt, "keyPressed");
    }

    public void keyReleased(GraphicsNodeKeyEvent evt) {
        report(evt, "keyReleased");
    }

    public void keyTyped(GraphicsNodeKeyEvent evt) {
        report(evt, "keyTyped");
    }

    /*
     * Checks the event to see if it is a selection gesture and processes it
     * accordingly.
     * @param evt the GraphicsNodeEvent, which may be a "select gesture"
     * Param evt is a GraphicsNodeEvent rather than a GraphicsNodeMouseEvent
     * for future extension, so we can use Shift-arrow, etc.
     */
    protected void checkSelectGesture(GraphicsNodeEvent evt) {

        GraphicsNodeMouseEvent mevt = null;
        if (evt instanceof GraphicsNodeMouseEvent) {
            mevt = (GraphicsNodeMouseEvent) evt;
        }

        GraphicsNode source = evt.getGraphicsNode();

        // TODO: re-order branching below to check instanceof Selectable first

        if (isSelectContinueGesture(evt)) {
            if ((source instanceof Selectable) && (mevt != null)) {
                Point2D p = new Point2D.Double(mevt.getX(), mevt.getY());
                AffineTransform t = source.getTransform();
                if (t == null) {
                    t = new AffineTransform();
                } else {
                    t = (AffineTransform) t.clone();
                }
                if (source instanceof TextNode) {
                    Point2D location = ((TextNode) source).getLocation();
                    t.translate(-location.getX(), -location.getY());
                }
                p = t.transform(p, null);
                //System.out.println("Select action at "+p);
                if (selectionNode != source) {
                     // have been dragged into new node!
                     System.out.println("Select (Entering) at "+p);
                     ((Selectable) source).selectAt(p.getX(), p.getY(),
                                                        renderContext);;
                     selectionNode = source;
                } else {
                    // Shape oldShape = source.getHighlightShape();
                    // Shape newShape =
                    ((Selectable) source).selectTo(p.getX(), p.getY(), renderContext);
                    /* TODO: fire a property change event on source!
                     * Perhaps selectTo should return the outline shape ?
                     * thus: source.firePropertyChangeEvent(
                     *         new PropertyChangeEvent(source,
                     *                                 Selectable.HIGHLIGHT_CHANGE,
                     *                                 oldshape, newShape));
                     */
                }
            }
        } else if (isSelectStartGesture(evt)) {
            if ((source instanceof Selectable) && (mevt != null)) {
                selectionNode = source;
                Point2D p = new Point2D.Double(mevt.getX(), mevt.getY());
                AffineTransform t = source.getTransform();
                if (t == null) {
                    t = new AffineTransform();
                } else {
                    t = (AffineTransform) t.clone();
                }
                if (source instanceof TextNode) {
                    Point2D location = ((TextNode) source).getLocation();
                    t.translate(-location.getX(), -location.getY());
                }
                p = t.transform(p, null);
                //System.out.println("Select at "+p);
                ((Selectable) source).selectAt(p.getX(), p.getY(), renderContext);
            }
        } else if (isSelectEndGesture(evt)) {
            if ((source instanceof Selectable) && (mevt != null)) {
                selectionNode = source;
                Point2D p = new Point2D.Double(mevt.getX(), mevt.getY());
                AffineTransform t = source.getTransform();
                if (t == null) {
                    t = new AffineTransform();
                } else {
                    t = (AffineTransform) t.clone();
                }
                if (source instanceof TextNode) {
                    Point2D location = ((TextNode) source).getLocation();
                   t.translate(-location.getX(), -location.getY());
                }
                p = t.transform(p, null);
                //System.out.println("Select to "+p);
                // Object oldSelection = source.getSelection();
                // Shape oldShape = source.getHighlightShape();
                // Shape newShape =
                ((Selectable) source).selectTo(p.getX(), p.getY(), renderContext);
                /* TODO: fire a property change event on source!
                 * Perhaps selectTo should return the outline shape ?
                 * thus: source.firePropertyChangeEvent(
                 *         new PropertyChangeEvent(source,
                 *                                 Selectable.HIGHLIGHT_CHANGE,
                 *                                 oldshape, newShape));
                 *
                 * followed by:
                 *        source.firePropertyChangeEvent(
                 *          new PropertyChangeEvent(source,
                 *                                 Selectable.SELECTION_CHANGE,
                 *                                 oldSelection, newSelection));
                 *
                 */
                Object oldSelection = getSelection();
                copyToClipboard(oldSelection);
            }

        } else if (isSelectAllGesture(evt)) {
            if ((source instanceof Selectable) && (mevt != null)) {
                selectionNode = source;
                Point2D p = new Point2D.Double(mevt.getX(), mevt.getY());
                AffineTransform t = source.getTransform();
                if (t == null) {
                    t = new AffineTransform();
                } else {
                    t = (AffineTransform) t.clone();
                }
                if (source instanceof TextNode) {
                    Point2D location = ((TextNode) source).getLocation();
                    t.translate(-location.getX(), -location.getY());
                }
                p = t.transform(p, null);
                //System.out.println("Select all "+p);

                // Object oldSelection = source.getSelection();
                // Shape oldShape = source.getHighlightShape();
                // Shape newShape =
                ((Selectable) source).selectAll(p.getX(), p.getY(), renderContext);
                /* TODO: fire a property change event on source!
                 * Perhaps selectTo should return the outline shape ?
                 * thus: source.firePropertyChangeEvent(
                 *         new PropertyChangeEvent(source,
                 *                                 Selectable.HIGHLIGHT_CHANGE,
                 *                                 oldshape, newShape));
                 *
                 * followed by:
                 *        source.firePropertyChangeEvent(
                 *          new PropertyChangeEvent(source,
                 *                                 Selectable.SELECTION_CHANGE,
                 *                                 oldSelection, newSelection));
                 *
                 */
                Object oldSelection = getSelection();
                copyToClipboard(oldSelection);
            }
        }
    }

    private boolean isSelectStartGesture(GraphicsNodeEvent evt) {
        return (evt.getID() == GraphicsNodeMouseEvent.MOUSE_PRESSED);
    }

    private boolean isSelectEndGesture(GraphicsNodeEvent evt) {
        return (evt.getID() == GraphicsNodeMouseEvent.MOUSE_RELEASED);
    }

    private boolean isSelectContinueGesture(GraphicsNodeEvent evt) {
        return (evt.getID() == GraphicsNodeMouseEvent.MOUSE_DRAGGED);
    }

    private boolean isSelectAllGesture(GraphicsNodeEvent evt) {
        return (evt.getID() == GraphicsNodeMouseEvent.MOUSE_CLICKED);
    }

    /*
     * Get the contents of the current selection.
     */
    public Object getSelection() {
        Object value = null;
        if (selectionNode instanceof Selectable) {
            value =  ((Selectable) selectionNode).getSelection(renderContext);
        }
        return value;
    }

    /**
     * Reports whether the current selection contains any objects.
     */
    public boolean isEmpty() {
        return (getSelection() == null);
    }

    private void squawkLikeAParrot(Object o) {
        System.out.println("Selection: "+o);
    }

    private void copyToClipboard(Object o) {
        String label="[unknown return type]";
        if (o instanceof CharacterIterator) {
            CharacterIterator iter = (CharacterIterator) o;
            char[] cbuff = new char[iter.getEndIndex()-iter.getBeginIndex()];
            if (cbuff.length > 0) {
                cbuff[0] = iter.first();
            }
            for (int i=1; i<cbuff.length;++i) {
                cbuff[i] = iter.next();
            }
            label = new String(cbuff);
            SecurityManager securityManager = System.getSecurityManager();
            boolean canAccessClipboard = true;
            if (securityManager != null) {
                try {
                    securityManager.checkSystemClipboardAccess();
                } catch (SecurityException e) {
                    canAccessClipboard = false;
                }
            }
            if (canAccessClipboard) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(label);
                clipboard.setContents(selection, selection);
            }
        }
        // remove this later
        squawkLikeAParrot(label);
    }

    private void report(GraphicsNodeEvent evt, String message) {
        GraphicsNode source = evt.getGraphicsNode();
        String label = "(non-text node)";
        if (source instanceof TextNode) {
            char[] cbuff;
            java.text.CharacterIterator iter =
                ((TextNode) source).getAttributedCharacterIterator();
            cbuff = new char[iter.getEndIndex()];
            if (cbuff.length > 0) cbuff[0] = iter.first();
            for (int i=1; i<cbuff.length;++i) {
                cbuff[i] = iter.next();
            }
            label = new String(cbuff);
        }
        System.out.println("Mouse "+message+" in "+label);
    }
}
