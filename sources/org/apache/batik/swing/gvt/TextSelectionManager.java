/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.gvt;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import java.awt.geom.AffineTransform;

import java.util.List;

import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.Selectable;

import org.apache.batik.gvt.event.AWTEventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.event.SelectionEvent;
import org.apache.batik.gvt.event.SelectionListener;

import org.apache.batik.gvt.text.ConcreteTextSelector;
import org.apache.batik.gvt.text.Mark;

/**
 * This class represents an object which manage GVT text nodes selection.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TextSelectionManager {

    /**
     * The cursor indicating that a text selection operation is under way.
     */
    public final static Cursor TEXT_CURSOR = new Cursor(Cursor.TEXT_CURSOR);

    /**
     * The text selector.
     */
    protected ConcreteTextSelector textSelector;

    /**
     * The associated JGVTComponent.
     */
    protected JGVTComponent component;

    /**
     * The selection overlay.
     */
    protected Overlay selectionOverlay = new SelectionOverlay();

    /**
     * The GVT root.
     */
    protected GraphicsNode gvtRoot;

    /**
     * The mouse listener.
     */
    protected MouseListener mouseListener;

    /**
     * To store the previous cursor.
     */
    protected Cursor previousCursor;

    /**
     * The selection highlight.
     */
    protected Shape selectionHighlight;

    /**
     * The text selection listener.
     */
    protected SelectionListener textSelectionListener;

    /**
     * Creates a new TextSelectionManager.
     */
    public TextSelectionManager(JGVTComponent comp,
                                AWTEventDispatcher ed) {
        textSelector = new ConcreteTextSelector();
        textSelectionListener = new TextSelectionListener();
        textSelector.addSelectionListener(textSelectionListener);
        mouseListener = new MouseListener();

        component = comp;
        component.getOverlays().add(selectionOverlay);

        ed.addGraphicsNodeMouseListener(mouseListener);
    }

    /**
     * Returns the selection overlay.
     */
    public Overlay getSelectionOverlay() {
        return selectionOverlay;
    }

    /**
     * Sets the selected text
     */
    public void setSelection(Mark start, Mark end) {
        textSelector.setSelection(start, end);
    }

    /**
     * To implement a GraphicsNodeMouseListener.
     */
    protected class MouseListener implements GraphicsNodeMouseListener {
        public void mouseClicked(GraphicsNodeMouseEvent evt) {
            if (evt.getSource() instanceof Selectable) {
                textSelector.mouseClicked(evt);
            }
        }

        public void mousePressed(GraphicsNodeMouseEvent evt) {
            if (evt.getSource() instanceof Selectable) {
                textSelector.mousePressed(evt);
            } else {
                if (selectionHighlight != null) {
                    Rectangle r = getHighlightBounds();
                    selectionHighlight = null;
                    component.paintImmediately(r);
                }
            }
        }

        public void mouseReleased(GraphicsNodeMouseEvent evt) {
            if (evt.getSource() instanceof Selectable) {
                textSelector.mouseReleased(evt);
            }
        }

        public void mouseEntered(GraphicsNodeMouseEvent evt) {
            if (evt.getSource() instanceof Selectable) {
                textSelector.mouseEntered(evt);
                previousCursor = component.getCursor();
                if (previousCursor.getType() == Cursor.DEFAULT_CURSOR) {
                    component.setCursor(TEXT_CURSOR);
                }
            }
        }

        public void mouseExited(GraphicsNodeMouseEvent evt) {
            if (evt.getSource() instanceof Selectable) {
                textSelector.mouseExited(evt);
                if (component.getCursor() == TEXT_CURSOR) {
                    component.setCursor(previousCursor);
                }
            }
        }

        public void mouseDragged(GraphicsNodeMouseEvent evt) {
            if (evt.getSource() instanceof Selectable) {
                textSelector.mouseDragged(evt);
            }
        }

        public void mouseMoved(GraphicsNodeMouseEvent evt) {
            if (evt.getSource() instanceof Selectable) {
                textSelector.mouseMoved(evt);
            }
        }
    }

    /**
     * To implements a selection listener.
     */
    protected class TextSelectionListener implements SelectionListener {
        public void selectionDone(SelectionEvent e) {
            selectionChanged(e);
        }
        public void selectionCleared(SelectionEvent e) {
            selectionStarted(e);
        }
        public void selectionStarted(SelectionEvent e) {
            if (selectionHighlight != null) {
                Rectangle r = getHighlightBounds();
                selectionHighlight = null;
                component.paintImmediately(r);
            }
        }
        public void selectionChanged(SelectionEvent e) {
            Rectangle r = null;
            AffineTransform at = component.getRenderingTransform();
            if (selectionHighlight != null) {
                r = at.createTransformedShape(selectionHighlight).getBounds();
                outset(r, 1);
            }

            selectionHighlight = e.getHighlightShape();
            if (selectionHighlight != null) {
                if (r != null) {
                    Rectangle r2 = getHighlightBounds();
                    component.paintImmediately(r.union(r2));
                } else {
                    component.paintImmediately(getHighlightBounds());
                }
            } else if (r != null) {
                component.paintImmediately(r);
            }
        }

    }

    protected Rectangle outset(Rectangle r, int amount) {
        r.x -= amount;
        r.y -= amount;
        r.width  += 2*amount;
        r.height += 2*amount;
        return r;
    }

    /**
     * The highlight bounds.
     */
    protected Rectangle getHighlightBounds() {
        AffineTransform at = component.getRenderingTransform();
        Shape s = at.createTransformedShape(selectionHighlight);
        return outset(s.getBounds(), 1);
    }

    static final Color fillColor   = new Color(200, 200, 255, 100);
    static final Color strokeColor = new Color(255, 255, 255, 255);

    /**
     * The selection overlay.
     */
    protected class SelectionOverlay implements Overlay {

        /**
         * Paints this overlay.
         */
        public void paint(Graphics g) {
            if (selectionHighlight != null) {
                AffineTransform at = component.getRenderingTransform();
                Shape s = at.createTransformedShape(selectionHighlight);

                Graphics2D g2d = (Graphics2D)g;

                // g2d.setXORMode(Color.white);
                // g2d.setColor(Color.black);
                g2d.setColor(fillColor);
                g2d.fill(s);

                g2d.setStroke(new java.awt.BasicStroke(1.0f));
                g2d.setColor(strokeColor);
                g2d.draw(s);
            }
        }
    }
}
